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
package me.philnate.textmanager.utils;

import me.philnate.textmanager.config.cfgMongo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.common.base.Throwables;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.gridfs.GridFS;

/**
 * 
 * @author philnate
 * 
 */
public class DB {

    public static final Datastore ds;
    public static final GridFS docs;
    public static final GridFS pdf;
    public static final GridFS tex;
    public static final GitRepositoryState state;

    private static Logger LOG = LoggerFactory.getLogger(DB.class);
    static {
	Morphia store = new Morphia();
	Mongo mg;
	try {
	    // set a default Profile which is production, so testcases have the
	    // possibility to set a activeprofile to override production profile
	    System.setProperty("spring.profiles.default", "production");
	    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(
		    cfgMongo.class);
	    mg = ctx.getAutowireCapableBeanFactory().getBean(Mongo.class);
	    state = ctx.getAutowireCapableBeanFactory().getBean(
		    GitRepositoryState.class);

	    store.mapPackage("me.philnate.textmanager.entities");
	    ds = store.createDatastore(mg, (String) ctx.getBean("dbName"));

	} catch (MongoException e) {
	    LOG.error("Error while loading mongodb configuration", e);
	    throw Throwables.propagate(e);
	}
	ds.ensureIndexes();
	docs = new GridFS(ds.getDB(), "doc");
	pdf = new GridFS(ds.getDB(), "pdf");
	tex = new GridFS(ds.getDB(), "tex");
    }
}
