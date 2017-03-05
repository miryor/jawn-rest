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
            WeatherJsonParser parser = new WundergroundWeatherJsonParser("11233", in);
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
