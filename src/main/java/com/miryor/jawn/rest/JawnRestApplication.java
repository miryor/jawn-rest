package com.miryor.jawn.rest;

import com.miryor.jawn.rest.resources.HourlyForecastResource;
import io.dropwizard.Application;
import io.dropwizard.client.HttpClientBuilder;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.http.client.HttpClient;
import static org.eclipse.jetty.util.log.JettyLogHandler.config;

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

        final HttpClient httpClient = new HttpClientBuilder(environment)
            .using(configuration.getHttpClientConfiguration())
            .build(getName());
        environment.jersey().register( new HourlyForecastResource( httpClient ) );
    }
    
}
