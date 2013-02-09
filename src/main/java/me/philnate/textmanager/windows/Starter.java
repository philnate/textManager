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
package me.philnate.textmanager.windows;

import java.awt.EventQueue;
import java.io.IOException;

import me.philnate.textmanager.updates.Updater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * Class used to start and stop application, only used in production
 * 
 * @author philnate
 * 
 */
public class Starter {

    public static int port = 27017;

    private static MongodExecutable mongodExecutable;

    private static final String packageName = "me.philnate.textmanager.updates";

    private static Logger LOG = LoggerFactory.getLogger(Starter.class);

    public static void main(String[] args) throws IOException {
	start();
    }

    public static void start() {
	try {
	    port = Network.getFreeServerPort();
	    MongodConfig mongodConfig = new MongodConfig(Version.V2_0_7,
		    new Net(port, Network.localhostIsIPv6()), new Storage(
			    "./db", null, 0), new Timeout());

	    RuntimeConfig runtimeConfig = new RuntimeConfig();
	    runtimeConfig.setTempDirFactory(new FixedPath("./bin/"));
	    runtimeConfig.getDownloadConfig().setArtifactStorePathNaming(
		    new FixedPath("./bin/"));
	    MongodStarter runtime = MongodStarter.getInstance(runtimeConfig);

	    mongodExecutable = runtime.prepare(mongodConfig);
	    mongodExecutable.start();

	    // check if updates are needed
	    Updater.checkUpdateNeeded(packageName);

	    // start MainWindow
	    EventQueue.invokeLater(new Runnable() {
		@Override
		public void run() {
		    try {
			new MainWindow().show();
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		}
	    });
	} catch (Exception e) {
	    LOG.error("got exception from mongodb", e);
	    shutdown();
	}
    }

    public static void shutdown() {
	if (mongodExecutable != null) {
	    mongodExecutable.stop();
	}
    }
}
