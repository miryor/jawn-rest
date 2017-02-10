/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.miryor.jawn.rest.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.miryor.jawn.rest.api.HourlyForecast;
import com.miryor.jawn.rest.parser.WeatherJsonParser;
import com.miryor.jawn.rest.parser.WundergroundWeatherJsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
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
import java.security.GeneralSecurityException;
import java.util.Collections;

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
    private static final String WUNDERGROUND_HOURLY_URL = "http://api.wunderground.com/api/502f7c0bd4a4257d/hourly/q/";
    private static final String JSON_FORMAT = ".json";
    private CloseableHttpClient httpClient;
    
    private String googleClientId;
    
    public HourlyForecastResource(CloseableHttpClient httpClient, String googleClientId) {
        this.httpClient = httpClient;
        this.googleClientId = googleClientId;
    }
    
    @GET
    @Timed
    public List<HourlyForecast> hourlyForecast(
        @QueryParam("token") Optional<String> token,
        @QueryParam("location") Optional<String> location,
        @QueryParam("version") Optional<String> version
        ) {
        
        CloseableHttpResponse response = null;
        InputStream in = null;
        List<HourlyForecast> list = null;
        try {
            com.google.api.client.json.JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
                .setAudience(Collections.singletonList(googleClientId))
                .build();
            
            GoogleIdToken idToken = verifier.verify(token.get());
            if (idToken != null) {
              Payload payload = idToken.getPayload();

              // Print user identifier
              String userId = payload.getSubject();
              logger.debug("User ID: " + userId);

              // Get profile information from payload
              /*String email = payload.getEmail();
              boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
              String name = (String) payload.get("name");
              String pictureUrl = (String) payload.get("picture");
              String locale = (String) payload.get("locale");
              String familyName = (String) payload.get("family_name");
              String givenName = (String) payload.get("given_name");*/
              
            } else {
                throw new WebApplicationException( "Could not authenticate your token", 403 );
            } 
            
            HttpGet httpGet = new HttpGet(WUNDERGROUND_HOURLY_URL + location.get() + JSON_FORMAT);
            response = httpClient.execute(httpGet);
            if ( response.getStatusLine().getStatusCode() == HttpStatus.OK_200 ) {
                HttpEntity entity = response.getEntity();
                in = entity.getContent();
                WeatherJsonParser parser = new WundergroundWeatherJsonParser(in);
                list = parser.parseHourlyForecast();
                in.close();
                in = null;
             }
            else {
                if ( logger.isErrorEnabled() ) logger.error( "Error getting hourly forecast, got status " + response.getStatusLine().getStatusCode() );
                throw new WebApplicationException( "Error getting hourly forecast", response.getStatusLine().getStatusCode() );
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
