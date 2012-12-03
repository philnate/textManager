package me.philnate.textmanager.updates;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class _Updater {

    @Test
    public void testUpdateFind() {
	Version oldV = null;
	for (Version vers : Updater.createUpdateList(
		_Updater.class.getCanonicalName()).keySet()) {
	    if (oldV == null) {
		oldV = vers;
		continue;
	    }
	    assertEquals(Version.BEFORE, oldV.compareTo(vers));
	    oldV = vers;
	}
    }

    @UpdateScript(UpdatesVersion = "1")
    private class Update1 implements Update {

	@Override
	public void preCheck() {
	}

	@Override
	public void upgrade() {
	}

	@Override
	public void postCheck() {
	}

    }

    @UpdateScript(UpdatesVersion = "2")
    private class Update2 implements Update {

	@Override
	public void preCheck() {
	}

	@Override
	public void upgrade() {
	}

	@Override
	public void postCheck() {
	}

    }
}
