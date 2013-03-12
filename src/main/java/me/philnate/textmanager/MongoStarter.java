package me.philnate.textmanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class MongoStarter {
    private static int port = 27017;

    private static MongodExecutable mongodExecutable;

    private static Logger LOG = LoggerFactory.getLogger(MongoStarter.class);

    public static void start() {
	try {
	    port = Network.getFreeServerPort();
	    MongodConfig mongodConfig = new MongodConfig(Version.V2_0_7,
		    new Net(port, Network.localhostIsIPv6()), new Storage(
			    "./db", null, 0), new Timeout());

	    RuntimeConfig runtimeConfig = new RuntimeConfig();
	    runtimeConfig.setTempDirFactory(new FixedPath("./bin/"));
	    DownloadConfig dconfig = runtimeConfig.getDownloadConfig();
	    dconfig.setArtifactStorePathNaming(new FixedPath("./bin/"));
	    MongodStarter runtime = MongodStarter.getInstance(runtimeConfig);

	    mongodExecutable = runtime.prepare(mongodConfig);
	    mongodExecutable.start();
	} catch (Exception e) {
	    LOG.error("got exception from mongodb", e);
	    stop();
	    System.exit(-1);
	}
    }

    public static void stop() {
	if (mongodExecutable != null) {
	    mongodExecutable.stop();
	}
    }

    public static int getPort() {
	return port;
    }

}
