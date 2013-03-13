package me.philnate.textmanager.web.config;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.annotation.PreDestroy;

import me.philnate.textmanager.web.config.cfgMongoDB.cfgProduction;
import me.philnate.textmanager.web.config.cfgMongoDB.cfgTesting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.Mongo;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.AbstractMongoConfig.Net;
import de.flapdoodle.embed.mongo.config.AbstractMongoConfig.Storage;
import de.flapdoodle.embed.mongo.config.AbstractMongoConfig.Timeout;
import de.flapdoodle.embed.mongo.config.DownloadConfig;
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

    public static final String PROFILE_PRODUCTION = "production";
    public static final String PROFILE_TESTING = "testing";

    @Configuration
    @Profile(PROFILE_PRODUCTION)
    public static class cfgProduction {

	@Bean
	public String dbName() {
	    return "textManager";
	}
    }

    @Configuration
    @Profile(PROFILE_TESTING)
    public static class cfgTesting {
	@Bean
	public String dbName() {
	    return "testManager";
	}
    }

    @Autowired
    private String dbName;

    @Bean
    public Integer mongoPort() throws IOException {
	return Network.getFreeServerPort();
    }

    @Bean
    public MongodConfig mongodConfig() throws UnknownHostException, IOException {
	return new MongodConfig(Version.V2_0_7, new Net(mongoPort(),
		Network.localhostIsIPv6()), new Storage("./db", null, 0),
		new Timeout());
    }

    @Bean
    public RuntimeConfig runtimeConfig() {
	RuntimeConfig runtimeConfig = new RuntimeConfig();
	runtimeConfig.setTempDirFactory(new FixedPath("./bin/"));
	return runtimeConfig;
    }

    @Bean
    public DownloadConfig downloadConfig() {
	DownloadConfig dconfig = runtimeConfig().getDownloadConfig();
	dconfig.setArtifactStorePathNaming(new FixedPath("./bin/"));
	return dconfig;
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
    public Mongo mongo() throws UnknownHostException, IOException {
	return new Mongo("localhost", mongoPort());
    }

    @Bean
    public MongoTemplate mongoTemplate() throws UnknownHostException,
	    IOException {
	return new MongoTemplate(mongo(), dbName);
    }

    /**
     * Close MongoDB once the context is closed, which happens when app is going
     * to be closed
     * 
     * @throws UnknownHostException
     * @throws IOException
     */
    @PreDestroy
    public void destroy() throws UnknownHostException, IOException {
	mongodExecutable().stop();
    }
}
