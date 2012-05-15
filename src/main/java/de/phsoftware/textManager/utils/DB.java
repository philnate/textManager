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
    public static final GridFS docs = new GridFS(ds.getDB(), "doc");
    public static final GridFS pdf = new GridFS(ds.getDB(), "pdf");
    public static final GridFS tex = new GridFS(ds.getDB(), "tex");

    @SuppressWarnings("deprecation")
    private static Datastore create() {
	Morphia store = new Morphia();
	store.mapPackage("de.phsoftware.textManager.entities");
	return store.createDatastore("textManager");
    }
}
