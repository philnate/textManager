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
package me.philnate.textmanager.updates;

import static org.junit.Assert.assertEquals;

import me.philnate.textmanager.TestBase;

import org.junit.Test;

/**
 * Requires a mongod instance to run in order to successfully test this code
 * 
 * @author user
 * 
 */
public class _Updater extends TestBase {

    @Test
    public void testUpdateFind() {
	Version oldV = null;
	for (Version vers : Updater.createUpdateList(
		"me.philnate.textmanager.updates.testUpdates").keySet()) {
	    if (oldV == null) {
		oldV = vers;
		continue;
	    }
	    assertEquals(Version.BEFORE, oldV.compareTo(vers));
	    oldV = vers;
	}
    }
}
