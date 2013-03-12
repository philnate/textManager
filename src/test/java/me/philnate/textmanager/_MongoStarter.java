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
package me.philnate.textmanager;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.junit.Test;

public class _MongoStarter {

    @Test
    public void testMongoStartStop() throws IOException {
	try (Socket sa = new Socket()) {
	    MongoStarter.start();
	    InetSocketAddress addr = new InetSocketAddress("localhost",
		    MongoStarter.getPort());
	    sa.connect(addr, 1000);
	    sa.close();
	    MongoStarter.stop();
	    try {
		sa.connect(
			new InetSocketAddress("localhost", MongoStarter
				.getPort()), 1000);
		fail("Should not be possible to connect to mongod any more");
	    } catch (Exception e) {
		assertThat(e.getMessage(), containsString("Socket is closed"));
	    }
	}
    }
}
