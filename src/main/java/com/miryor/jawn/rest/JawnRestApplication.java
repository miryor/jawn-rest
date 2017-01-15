package com.miryor.jawn.rest;

import com.miryor.jawn.rest.health.TemplateHealthCheck;
import com.miryor.jawn.rest.resources.HelloWorldResource;
import com.miryor.jawn.rest.resources.HourlyForecastResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

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
        final HelloWorldResource resource = new HelloWorldResource(
            configuration.getTemplate(), 
            configuration.getDefaultName()
        );
        environment.jersey().register(resource);
        environment.jersey().register( new HourlyForecastResource() );
        final TemplateHealthCheck healthCheck = 
            new TemplateHealthCheck(configuration.getTemplate());
        environment.healthChecks().register("template", healthCheck);
    }
    
}
