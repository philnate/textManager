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
