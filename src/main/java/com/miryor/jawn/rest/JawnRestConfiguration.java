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

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.client.HttpClientConfiguration;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 *
 * @author royrim
 */
public class JawnRestConfiguration extends Configuration {
    
    @JsonProperty("mongoUrl")
    private String mongoUrl;
    public String getMongoUrl() {
        return mongoUrl;
    }
    
    @JsonProperty("googleClientId")
    private String googleClientId;    
    public String getGoogleClientId() {
        return googleClientId;
    }

    @JsonProperty("wundergroundHourlyForecastResource")
    private String wundergroundHourlyForecastResource;    
    public String getWundergroundHourlyForecastResource() {
        return wundergroundHourlyForecastResource;
    }

    @JsonProperty("wundergroundApiKey")
    private String wundergroundApiKey;    
    public String getWundergroundApiKey() {
        return wundergroundApiKey;
    }
    
    @Valid
    @NotNull
    private HttpClientConfiguration httpClient = new HttpClientConfiguration();
    
    @JsonProperty("httpClient")
    public HttpClientConfiguration getHttpClientConfiguration() {
        return httpClient;
    }

    @JsonProperty("httpClient")
    public void setHttpClientConfiguration(HttpClientConfiguration httpClient) {
        this.httpClient = httpClient;
    }
}
