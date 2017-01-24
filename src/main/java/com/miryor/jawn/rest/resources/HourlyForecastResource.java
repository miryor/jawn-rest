/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.miryor.jawn.rest.resources;

import com.codahale.metrics.annotation.Timed;
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
    
    public HourlyForecastResource(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }
    
    @GET
    @Timed
    public List<HourlyForecast> hourlyForecast(
        @QueryParam("location") Optional<String> location,
        @QueryParam("version") Optional<String> version
        ) {
        
        HttpGet httpGet = new HttpGet(WUNDERGROUND_HOURLY_URL + location.get() + JSON_FORMAT);
        CloseableHttpResponse response = null;
        InputStream in = null;
        List<HourlyForecast> list = null;
        try {
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
