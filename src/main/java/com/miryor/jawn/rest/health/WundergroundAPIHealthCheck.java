/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.miryor.jawn.rest.health;

import com.codahale.metrics.health.HealthCheck;
import com.miryor.jawn.rest.api.HourlyForecast;
import com.miryor.jawn.rest.parser.WeatherJsonParser;
import com.miryor.jawn.rest.parser.WundergroundWeatherJsonParser;
import java.io.InputStream;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.eclipse.jetty.http.HttpStatus;


/**
 *
 * @author royrim
 */
public class WundergroundAPIHealthCheck extends HealthCheck {
    private CloseableHttpClient httpClient;    
    private String wundergroundHourlyForecastResource;
    private String wundergroundApiKey;
    
    public WundergroundAPIHealthCheck(
        CloseableHttpClient httpClient, 
        String wundergroundApiKey,
        String wundergroundHourlyForecastResource
    ) {
        this.httpClient = httpClient;
        this.wundergroundApiKey = wundergroundApiKey;
        this.wundergroundHourlyForecastResource = wundergroundHourlyForecastResource;
    }

    @Override
    protected Result check() throws Exception {
        CloseableHttpResponse response = null;
        InputStream in = null;
        List<HourlyForecast> list = null;
        HttpGet httpGet = new HttpGet( String.format(wundergroundHourlyForecastResource, wundergroundApiKey, "11233") );
        response = httpClient.execute(httpGet);
        if ( response.getStatusLine().getStatusCode() == HttpStatus.OK_200 ) {
            HttpEntity entity = response.getEntity();
            in = entity.getContent();
            WeatherJsonParser parser = new WundergroundWeatherJsonParser(in);
            list = parser.parseHourlyForecast();
            in.close();
            in = null;
            if ( list.size() > 0 ) {
                return Result.healthy();
            }
            else {
                return Result.unhealthy("Something wrong with wunderground hourly forecast content");
            }
        }
        else {
            return Result.unhealthy("Something wrong with wunderground api, status: " + response.getStatusLine() );
        }
    }
    
}
