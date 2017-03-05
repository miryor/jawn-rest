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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.miryor.jawn.rest.api.HourlyForecast;
import com.miryor.jawn.rest.api.TokenForecastRequest;
import com.miryor.jawn.rest.health.WundergroundAPIHealthCheck;
import com.miryor.jawn.rest.resources.HourlyForecastResource;
import com.mongodb.MongoClient;
import io.dropwizard.Application;
import io.dropwizard.client.HttpClientBuilder;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.http.impl.client.CloseableHttpClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

/**
 *
 * @author royrim
 */
public class JawnRestApplication extends Application<JawnRestConfiguration> {
    Cache<String, List<HourlyForecast>> hourlyForecastCache = CacheBuilder.newBuilder()
       .maximumSize(100)
       .expireAfterWrite(10, TimeUnit.MINUTES)
       .build();
    LinkedBlockingQueue<TokenForecastRequest> requestList = new LinkedBlockingQueue<TokenForecastRequest>();
    
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
        final Morphia morphia = new Morphia();
        morphia.mapPackage("com.miryor.jawn.rest.api");
        final Datastore datastore = morphia.createDatastore(new MongoClient(configuration.getMongoUrl()), "jawn");
        datastore.ensureIndexes();
        
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
                configuration.getWundergroundHourlyForecastResource(),
                hourlyForecastCache,
                datastore
            ) );
    }
    
}
