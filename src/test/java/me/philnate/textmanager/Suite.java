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
package me.philnate.textmanager;

import me.philnate.textmanager.windows.Starter;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.extensions.dynamicsuite.ClassPath;
import org.junit.extensions.dynamicsuite.Filter;
import org.junit.extensions.dynamicsuite.TestClassFilter;
import org.junit.extensions.dynamicsuite.suite.DynamicSuite;
import org.junit.runner.RunWith;

@RunWith(DynamicSuite.class)
@ClassPath
@Filter(Suite.class)
public class Suite implements TestClassFilter {

    @Override
    public boolean include(String className) {
	String[] parts = className.split("\\.");
	if (parts.length == 0) {
	    return false;
	}
	return parts[parts.length - 1].startsWith("_");
    }

    @Override
    public boolean include(@SuppressWarnings("rawtypes") Class clazz) {
	return (TestBase.class.isAssignableFrom(clazz));
    }

    @BeforeClass
    public static final void start() {
	// System.setProperty("spring.profiles.active", "test");
	Starter.start();
    }

    @AfterClass
    public static final void stop() {
	Starter.shutdown();
    }
}
