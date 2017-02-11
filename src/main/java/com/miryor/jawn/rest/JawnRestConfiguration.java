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
