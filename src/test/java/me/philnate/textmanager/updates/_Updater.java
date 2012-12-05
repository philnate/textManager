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
