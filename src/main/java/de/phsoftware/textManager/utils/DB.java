package de.phsoftware.textManager.utils;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.gridfs.GridFS;

/**
 * 
 * @author philnate
 * 
 */
public class DB {

    public static final Datastore ds = create();
    public static final GridFS fs = new GridFS(ds.getDB(), "docs");

    @SuppressWarnings("deprecation")
    private static Datastore create() {
	Morphia store = new Morphia();
	store.mapPackage("de.phsoftware.textManager.entities");
	return store.createDatastore("textManager");
    }
}
