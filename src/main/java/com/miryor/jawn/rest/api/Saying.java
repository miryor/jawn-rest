/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.miryor.jawn.rest.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;

/**
 *
 * @author royrim
 */
public class Saying {
    
    private long id;
    
    @Length(max = 3)
    private String content;
    
    public Saying() {
        
    }
    
    public Saying(long id, String content) {
        this.id = id;
        this.content = content;
    }
    
    @JsonProperty
    public long getId() {
        return id;
    }
    
    @JsonProperty
    public String getContent() {
        return content;
    }
    
}
