/*
 * Copyright (C) 2017 royrim.
 *
 * JAWN-REST is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.miryor.jawn.rest.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.miryor.jawn.rest.api.HourlyForecast;
import com.miryor.jawn.rest.parser.WeatherJsonParser;
import com.miryor.jawn.rest.parser.WundergroundWeatherJsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.eclipse.jetty.http.HttpStatus;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.common.cache.Cache;
import com.miryor.jawn.rest.api.TokenForecastRequest;
import com.miryor.jawn.rest.api.UserForecastRequest;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import org.hibernate.validator.constraints.NotEmpty;
import org.mongodb.morphia.Datastore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 


/**
 *
 * @author royrim
 */
@Path("/hourlyForecast")
@Produces(MediaType.APPLICATION_JSON)
public class HourlyForecastResource {
    private final Logger logger = LoggerFactory.getLogger(HourlyForecastResource.class);

    private CloseableHttpClient httpClient;    
    private String googleClientId;
    private String wundergroundHourlyForecastResource;
    private String wundergroundApiKey;
    
    private Cache<String, List<HourlyForecast>> hourlyForecastCache;
    private Datastore datastore;
    
    public HourlyForecastResource(
        CloseableHttpClient httpClient, 
        String googleClientId, 
        String wundergroundApiKey,
        String wundergroundHourlyForecastResource,
        Cache<String, List<HourlyForecast>> hourlyForecastCache,
        Datastore datastore
    ) {
        this.httpClient = httpClient;
        this.googleClientId = googleClientId;
        this.wundergroundApiKey = wundergroundApiKey;
        this.wundergroundHourlyForecastResource = wundergroundHourlyForecastResource;
        this.hourlyForecastCache = hourlyForecastCache;
        this.datastore = datastore;
    }
    
    @GET
    @Timed(name="showAll-timed")
    @Metered(name="showAll-metered")
    @ExceptionMetered(name="showAll-exceptionmetered")
    public List<HourlyForecast> hourlyForecast(
        @QueryParam("token") @NotEmpty String token,
        @QueryParam("location") @NotEmpty String location,
        @QueryParam("version") @NotEmpty String version,
        @Context HttpServletRequest request
        ) {
        
        datastore.save(new TokenForecastRequest(token, location, request.getRemoteAddr(), new Date()) );
        
        CloseableHttpResponse response = null;
        InputStream in = null;
        List<HourlyForecast> list = null;
        try {
            com.google.api.client.json.JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
                .setAudience(Collections.singletonList(googleClientId))
                .build();
            
            GoogleIdToken idToken = verifier.verify(token);
            if (idToken != null) {
              Payload payload = idToken.getPayload();

              // Print user identifier
              String userId = payload.getSubject();
              String email = payload.getEmail();
              logger.debug("User ID: " + userId);

              // Get profile information from payload
              /*
              boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
              String name = (String) payload.get("name");
              String pictureUrl = (String) payload.get("picture");
              String locale = (String) payload.get("locale");
              String familyName = (String) payload.get("family_name");
              String givenName = (String) payload.get("given_name");*/
              
              datastore.save(new UserForecastRequest(email, location, request.getRemoteAddr(), new Date()) );
              
              
            } else {
                throw new WebApplicationException( "Could not authenticate your token", 403 );
            } 
            
            list = hourlyForecastCache.getIfPresent(location);
            
            if ( list != null ) logger.debug( "Forecast for " + location + " found in cache" );

            if ( list == null ) {
                HttpGet httpGet = new HttpGet( String.format(wundergroundHourlyForecastResource, wundergroundApiKey, location) );
                response = httpClient.execute(httpGet);
                if ( response.getStatusLine().getStatusCode() == HttpStatus.OK_200 ) {
                    HttpEntity entity = response.getEntity();
                    in = entity.getContent();
                    WeatherJsonParser parser = new WundergroundWeatherJsonParser(location, in);
                    list = parser.parseHourlyForecast();
                    in.close();
                    in = null;
                    hourlyForecastCache.put(location,list);
                 }
                else {
                    if ( logger.isErrorEnabled() ) logger.error( "Error getting hourly forecast, got status " + response.getStatusLine().getStatusCode() );
                    throw new WebApplicationException( "Error getting hourly forecast", response.getStatusLine().getStatusCode() );
                }
            }
        }
        catch ( GeneralSecurityException e ) {
            if ( logger.isErrorEnabled() ) logger.error( "Error getting hourly forecast", e );
            throw new WebApplicationException( "Error getting hourly forecast", 500 );
        }
        catch ( IOException e ) {
            if ( logger.isErrorEnabled() ) logger.error( "Error getting hourly forecast", e );
            throw new WebApplicationException( "Error getting hourly forecast", 500 );
        }
        finally { 
            if ( in != null ) try { in.close(); } catch ( Exception e ) {}
            if ( response != null ) try { response.close(); } catch ( Exception e ) {}
        }
        return list;
    }
    
}
