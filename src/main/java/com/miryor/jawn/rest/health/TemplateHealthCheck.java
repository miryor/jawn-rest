/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.miryor.jawn.rest.health;

import com.codahale.metrics.health.HealthCheck;

/**
 *
 * @author royrim
 */
public class TemplateHealthCheck extends HealthCheck {
    
    private final String template;
    
    public TemplateHealthCheck(String template) {
        this.template = template;
    }
    
    @Override
    protected Result check() throws Exception {
        final String saying = String.format(template, "TEST");
        if (!saying.contains("TEST")) {
            return Result.unhealthy("template doesn't include a name");
        }
        return Result.healthy();
    }
    
}
