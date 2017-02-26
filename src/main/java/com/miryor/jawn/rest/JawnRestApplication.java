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
package com.miryor.jawn.rest;

import com.miryor.jawn.rest.health.WundergroundAPIHealthCheck;
import com.miryor.jawn.rest.resources.HourlyForecastResource;
import io.dropwizard.Application;
import io.dropwizard.client.HttpClientBuilder;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.http.impl.client.CloseableHttpClient;

/**
 *
 * @author royrim
 */
public class JawnRestApplication extends Application<JawnRestConfiguration> {
    public static void main(String[] args) throws Exception {
        new JawnRestApplication().run(args);
    }
    
    @Override
    public String getName() {
        return "jawn-rest";
    }
    
    @Override
    public void initialize(Bootstrap<JawnRestConfiguration> bootstrap) {
        
    }
    
    @Override
    public void run(JawnRestConfiguration configuration, Environment environment) {

        final CloseableHttpClient httpClient = new HttpClientBuilder(environment)
            .using(configuration.getHttpClientConfiguration())
            .build(getName());
        
        environment.healthChecks().register("wunderground", 
            new WundergroundAPIHealthCheck(
                httpClient, 
                configuration.getWundergroundApiKey(),
                configuration.getWundergroundHourlyForecastResource()
            ));
        
        
        environment.jersey().register( 
            new HourlyForecastResource( 
                httpClient, 
                configuration.getGoogleClientId(),
                configuration.getWundergroundApiKey(),
                configuration.getWundergroundHourlyForecastResource()
            ) );
    }
    
}
