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

import static me.philnate.textmanager.updates.Version.AFTER;
import static me.philnate.textmanager.updates.Version.BEFORE;
import static me.philnate.textmanager.updates.Version.EQUAL;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class _Version {

    @Test
    public void testValidVersionParsing() {
	Version v = new Version();
	assertEquals(0, v.getMajor());
	assertEquals(0, v.getMinor());
	assertEquals(0, v.getPatch());

	v = new Version("12");
	assertEquals(12, v.getMajor());
	assertEquals(0, v.getMinor());
	assertEquals(0, v.getPatch());

	v = new Version("12.12");
	assertEquals(12, v.getMajor());
	assertEquals(12, v.getMinor());
	assertEquals(0, v.getPatch());

	v = new Version("12.12.1");
	assertEquals(12, v.getMajor());
	assertEquals(12, v.getMinor());
	assertEquals(1, v.getPatch());
    }

    @Test
    public void testInvalidVersions() {
	try {
	    new Version("12.");
	} catch (IllegalArgumentException e) {
	    // expected exception
	}
	try {
	    new Version("12.12.");
	} catch (IllegalArgumentException e) {
	    // expected exception
	}
	try {
	    new Version(null);
	} catch (NullPointerException e) {
	    // expected exception
	}
	try {
	    new Version("noVersion");
	} catch (IllegalArgumentException e) {
	    // expected exception
	}
    }

    @Test
    public void testComparation() {
	assertEquals(EQUAL,
		new Version("1.1.1").compareTo(new Version("1.1.1")));
	assertEquals(EQUAL, new Version("1.1").compareTo(new Version("1.1")));
	assertEquals(EQUAL, new Version("1").compareTo(new Version("1")));

	assertEquals(AFTER, new Version("2").compareTo(new Version("1")));
	assertEquals(AFTER, new Version("2.2").compareTo(new Version("2.1")));
	assertEquals(AFTER,
		new Version("2.2.2").compareTo(new Version("2.2.1")));

	assertEquals(BEFORE, new Version("1").compareTo(new Version("2")));
	assertEquals(BEFORE, new Version("2.1").compareTo(new Version("2.2")));
	assertEquals(BEFORE,
		new Version("2.2.1").compareTo(new Version("2.2.2")));

    }
}
