package de.phsoftware.textManager.utils;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.gridfs.GridFS;

/**
 * 
 * @author philnate
 * 
 */
@SuppressWarnings("deprecation")
public class DB {

    public static final Datastore ds;
    public static final GridFS docs;
    public static final GridFS pdf;
    public static final GridFS tex;

    static {
	Morphia store = new Morphia();
	store.mapPackage("de.phsoftware.textManager.entities");
	ds = store.createDatastore("textManager");
	ds.ensureIndexes();

	docs = new GridFS(ds.getDB(), "doc");
	pdf = new GridFS(ds.getDB(), "pdf");
	tex = new GridFS(ds.getDB(), "tex");
    }
}
