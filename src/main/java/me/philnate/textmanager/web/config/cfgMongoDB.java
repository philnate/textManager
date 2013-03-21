/**
 *   textManager, a GUI for managing bills for texter jobs
 *
 *   Copyright (C) 2013 philnate
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
package me.philnate.textmanager.web.config;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.UUID;

import me.philnate.cherimongo.db.entities.Entities;
import me.philnate.textmanager.web.config.cfgMongoDB.cfgProduction;
import me.philnate.textmanager.web.config.cfgMongoDB.cfgTesting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import com.mongodb.DB;
import com.mongodb.MongoClient;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.AbstractMongoConfig.Net;
import de.flapdoodle.embed.mongo.config.AbstractMongoConfig.Storage;
import de.flapdoodle.embed.mongo.config.AbstractMongoConfig.Timeout;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.RuntimeConfig;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.io.directories.FixedPath;
import de.flapdoodle.embed.process.runtime.Network;

/**
 * MongoDB spring configuration
 * 
 * @author philnate
 * 
 */
@Configuration
@Import({ cfgProduction.class, cfgTesting.class })
public class cfgMongoDB {

    // TODO instantiate a Entities class to avoid to have this awkward DB wiring
    public static final String PROFILE_PRODUCTION = "production";
    public static final String PROFILE_TESTING = "testing";

    private static interface MongoDBProfile {
	public String dbName();

	public String storagePath();
    }

    @Configuration
    @Profile(PROFILE_PRODUCTION)
    public static class cfgProduction implements MongoDBProfile {

	@Override
	@Bean
	public String dbName() {
	    return "textManager";
	}

	@Override
	@Bean
	public String storagePath() {
	    return "./db";
	}
    }

    @Configuration
    @Profile(PROFILE_TESTING)
    public static class cfgTesting implements MongoDBProfile {
	@Override
	@Bean
	public String dbName() {
	    return "testManager";
	}

	@Override
	@Bean
	public String storagePath() {
	    return "./target/" + UUID.randomUUID();
	}
    }

    @Autowired
    private String dbName;

    @Autowired
    private String storagePath;

    @Bean
    public Integer mongoPort() throws IOException {
	return Network.getFreeServerPort();
    }

    @Bean
    public Version version() {
	return Version.V2_2_1;
    }

    @Bean
    public MongodConfig mongodConfig() throws UnknownHostException, IOException {
	return new MongodConfig(version(), new Net(mongoPort(),
		Network.localhostIsIPv6()), new Storage(storagePath, null, 0),
		new Timeout());
    }

    @Bean
    public RuntimeConfig runtimeConfig() {
	FixedPath store = new FixedPath("./bin/");
	RuntimeConfig runtimeConfig = new RuntimeConfig();
	runtimeConfig.setTempDirFactory(store);
	runtimeConfig.getDownloadConfig().setArtifactStorePathNaming(store);
	return runtimeConfig;
    }

    @Bean
    public MongodExecutable mongodExecutable() throws UnknownHostException,
	    IOException {
	MongodStarter runtime = MongodStarter.getInstance(runtimeConfig());
	MongodExecutable exe = runtime.prepare(mongodConfig());
	exe.start();
	return exe;
    }

    @Bean
    public MongoClient mongo() throws UnknownHostException, IOException {
	return new MongoClient("localhost", mongoPort());
    }

    @Bean
    public DB db() throws UnknownHostException, IOException {
	return mongo().getDB(dbName);
    }

    @Bean
    public Entities entities() throws UnknownHostException, IOException {
	return new Entities(db());
    }
}
