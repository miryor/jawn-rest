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
package com.miryor.jawn.rest.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

/**
 *
 * @author royrim
 */
public class HourlyForecastRequest {
    
    String token;
    String location;
    String ip;
    Date requestDate;

    public HourlyForecastRequest(String token, String location, String ip, Date requestDate) {
        this.token = token;
        this.location = location;
        this.ip = ip;
        this.requestDate = requestDate;
    }

    @JsonProperty
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @JsonProperty
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @JsonProperty
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @JsonProperty
    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }
    
    
    
}
