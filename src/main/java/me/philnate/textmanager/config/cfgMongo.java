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

import me.philnate.textmanager.config.cfgMongo.cfgProduction;
import me.philnate.textmanager.config.cfgMongo.cfgTest;
import me.philnate.textmanager.windows.Starter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

@Configuration
@Import({ cfgProduction.class, cfgTest.class })
public class cfgMongo {

    @Configuration
    public static class cfgTest {
	@Bean
	public String dbName() {
	    return "testManager";
	}
    }

    @Configuration
    public static class cfgProduction {
	@Bean
	public String dbName() {
	    return "textManager";
	}
    }

    @Bean
    public Mongo mongo() throws UnknownHostException, MongoException {
	return new Mongo("localhost", Starter.port);
    }

    @Bean
    public DB db() throws UnknownHostException, MongoException {
	return mongo().getDB("textManager");
    }
}
