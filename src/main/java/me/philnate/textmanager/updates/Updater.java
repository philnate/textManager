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

import static me.philnate.textmanager.utils.DB.ds;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.net.URLDecoder;
import java.util.Map;

import me.philnate.textmanager.entities.Setting;

import com.google.common.collect.Maps;

import eu.infomas.annotation.AnnotationDetector;
import eu.infomas.annotation.AnnotationDetector.TypeReporter;

public class Updater {
    private static final File installPath = getInstallPath();
    private static final int version = 1;
    private static Map<Version, Class<? extends Update>> updates = Maps
	    .newTreeMap();

    /**
     * checks what the actual db version is, if an old version is encountered
     * appropriate updates are performed to get the db to the latest version
     */
    public static void checkUpdateNeeded() {
	createUpdateList();
	Setting v = Setting.find("version");
	if (null == v) {
	    v = new Setting("version", "1");
	    ds.save(v);
	}
	backUp();
	rollback();
    }

    private static void createUpdateList() {
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
		    System.out
			    .println("Found annotated class, but could not load it "
				    + className);
		}
	    }

	};
	final AnnotationDetector cf = new AnnotationDetector(reporter);
	try {
	    // load updates
	    cf.detect("me.philnate.textmanager");
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * backups database in order some unexpected error occurs
     */
    public static void backUp() {
	ProcessBuilder dump = new ProcessBuilder(new File(installPath,
		"mongodump").toString(), "-h localhost", "--port 27017",
		"-o db.backUp");
	dump.directory(installPath);
	try {
	    dump.start();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    /**
     * rolls back any made backups as something in the upgrade went wrong
     */
    private static void rollback() {
	// ProcessBuilder restore = new ProcessBuilder(new File(installPath,
	// "mongorestore").toString(), "--drop", "-h localhost",
	// "--port 27017", "-o db.backUp");
	// restore.directory(installPath);
	// try {
	// if (0==restore.start().exitValue()) {
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
    }

    private static File getInstallPath() {
	String path = Update.class.getProtectionDomain().getCodeSource()
		.getLocation().getPath();
	try {
	    String decodedPath = URLDecoder.decode(path, "UTF-8");
	    if (decodedPath.endsWith(".jar")) {
		return new File(new File(decodedPath).getParent(), "mongo");
	    } else {
		return new File("/usr/bin/");
	    }
	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	    return null;
	}
    }
}
