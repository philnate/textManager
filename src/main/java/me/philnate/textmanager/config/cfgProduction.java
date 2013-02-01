/**
 *   textManager, a GUI for managing bills for texter jobs
 *
 *   Copyright (C) 2012- philnate
 *
 *   This file is part of textManager.
 *
 *   textManager is free software: you can redistribute it and/or modify it under the terms of the
 *   GNU General Public License as published by the Free Software Foundation, either version 3 of the
 *   License, or (at your option) any later version.
 *
 *   textManager is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 *   See the GNU General Public License for more details. You should have received a copy of the GNU
 *   General Public License along with textManager. If not, see <http://www.gnu.org/licenses/>.
 */
package me.philnate.textmanager.config;

import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

@Configurable
@Profile("production")
public class cfgProduction {

    @Bean
    public Mongo mongo() throws UnknownHostException, MongoException {
	return new Mongo("localhost", 27017);
    }

    @Bean
    public DB db() throws UnknownHostException, MongoException {
	return mongo().getDB("textManager");
    }
}
