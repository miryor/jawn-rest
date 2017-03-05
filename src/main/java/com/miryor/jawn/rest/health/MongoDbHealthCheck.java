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
import com.miryor.jawn.rest.api.UserForecastRequest;
import org.mongodb.morphia.Datastore;

/**
 *
 * @author royrim
 */
public class MongoDbHealthCheck extends HealthCheck {
    
    Datastore datastore;
    public MongoDbHealthCheck(Datastore datastore) {
        this.datastore = datastore;
    }
    
    @Override
    protected Result check() throws Exception {
        datastore.createQuery(UserForecastRequest.class).count();
        return Result.healthy();
    }
}
