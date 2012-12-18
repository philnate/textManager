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

import static java.lang.String.format;
import static me.philnate.textmanager.utils.DB.ds;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.TreeMap;

import me.philnate.textmanager.entities.Setting;
import me.philnate.textmanager.utils.NotifyingThread;

import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;

import eu.infomas.annotation.AnnotationDetector;
import eu.infomas.annotation.AnnotationDetector.TypeReporter;

public class Updater {
    private static final File installPath = SystemUtils.getUserDir();
    private static final File backUpPath = new File(installPath, "db.backUp");
    private static final Version startVersion = new Version("1");
    private static final String packageName = "me.philnate.textmanager.updates";

    private static Logger LOG = LoggerFactory.getLogger(Updater.class);

    /**
     * checks what the actual db version is, if an old version is encountered
     * appropriate updates are performed to get the db to the latest version
     */
    public static void checkUpdateNeeded() {
	TreeMap<Version, Class<? extends Update>> updates = createUpdateList(packageName);
	Setting v = Setting.find("version");
	// check that an version is set, if none was found set it to 1
	LOG.info(format("Database version is %s", v.getValue()));
	if (StringUtils.isBlank(v.getValue())) {
	    Version db = (Version) ComparatorUtils.min(startVersion,
		    updates.lastKey(), null);
	    LOG.debug(String.format("No Version set, assuming []",
		    db.toString()));
	    v = new Setting("version", db);
	    ds.save(v);
	}
	LOG.info(format("Found these Database upgrades: '%s'", updates.keySet()));
	for (Version vers : updates.keySet()) {
	    if (vers.compareTo(new Version(v.getValue())) < vers.AFTER) {
		// if version is smaller than actual db version we have nothing
		// todo here
		LOG.debug(format("Database is already newer than '%s'", vers));
		continue;
	    }
	    try {
		LOG.info(format("Going to update Database to version '%s'",
			vers));
		backUp();
		// create new Instance
		Update up = updates.get(vers).newInstance();
		// verify that everything is met for this update
		up.preCheck();
		// do the actual update
		up.upgrade();
		// verify that everything is as expected
		up.postCheck();
		// update the version
		v.setValue(vers.toString()).save();
	    } catch (Exception e) {
		// in case of an exception stop further rollback and stop
		// further updates
		LOG.error(
			"Update process caused an exception going to rollback",
			e);
		rollback();
		return;
	    } finally {
		// finally drop backup directory to avoid to get conflicting
		// data versions
		try {
		    FileUtils.deleteDirectory(backUpPath);
		} catch (IOException e) {
		    LOG.error("Could not remove file", e);
		}
	    }
	}
    }

    static TreeMap<Version, Class<? extends Update>> createUpdateList(
	    String packageName) {
	final TreeMap<Version, Class<? extends Update>> updates = Maps
		.newTreeMap();

	final TypeReporter reporter = new TypeReporter() {

	    @SuppressWarnings("unchecked")
	    @Override
	    public Class<? extends Annotation>[] annotations() {
		return new Class[] { UpdateScript.class };
	    }

	    @Override
	    public void reportTypeAnnotation(
		    Class<? extends Annotation> annotation, String className) {
		Class<? extends Update> clazz;
		try {
		    clazz = (Class<? extends Update>) Updater.class
			    .getClassLoader().loadClass(className);
		    updates.put(
			    new Version(clazz.getAnnotation(UpdateScript.class)
				    .UpdatesVersion()), clazz);
		} catch (ClassNotFoundException e) {
		    LOG.error("Found annotated class, but could not load it "
			    + className, e);
		}
	    }

	};
	final AnnotationDetector cf = new AnnotationDetector(reporter);
	try {
	    // load updates
	    cf.detect(packageName);
	} catch (IOException e) {
	    LOG.error("An error occured while collecting Updates", e);
	}
	return updates;
    }

    /**
     * backups database in order some unexpected error occurs while update
     * happens
     */
    public static void backUp() {
	final ProcessBuilder dump = new ProcessBuilder(new File(installPath,
		getProgram("win/mongodump")).toString(), "-h",
		"localhost:27017", "-o", backUpPath.getAbsolutePath(), "--db",
		"textManager");
	dump.directory(installPath);
	// try {
	new NotifyingThread() {

	    @Override
	    protected void doRun() {
		try {
		    printOutputStream(dump);
		} catch (IOException e) {
		    LOG.error("Error while doing backup of db files", e);
		    Throwables.propagate(e);
		}
	    }
	}.run();
    }

    /**
     * rolls back any made backups as something in the upgrade went wrong
     */
    private static void rollback() {
	final ProcessBuilder restore = new ProcessBuilder(new File(installPath,
		getProgram("win/mongorestore")).toString(), "--drop", "-h",
		"localhost:27017", "--db", "textManager",
		backUpPath.getAbsolutePath());
	restore.directory(installPath);
	new NotifyingThread() {

	    @Override
	    protected void doRun() {
		try {
		    printOutputStream(restore);
		} catch (IOException e) {
		    LOG.error("Error while doing rollback of db files", e);
		    Throwables.propagate(e);
		}
	    }
	};
    }

    /**
     * checks which operation system is in use and based on that the appropriate
     * application name is returned. So for windows it will add the .exe suffix
     * 
     * @param appName
     * @return
     */
    private static String getProgram(String appName) {
	if (SystemUtils.IS_OS_WINDOWS) {
	    return appName + ".exe";
	}
	return appName;
    }
}
