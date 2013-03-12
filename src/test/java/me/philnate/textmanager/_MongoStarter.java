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
