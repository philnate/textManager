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
package me.philnate.textmanager.entities;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;
import static me.philnate.textmanager.entities.EntityUtils.getCollectionName;
import static me.philnate.textmanager.entities.EntityUtils.getFields;

import java.lang.reflect.Proxy;
import java.util.Set;

import me.philnate.textmanager.entities.annotations.Collection;
import me.philnate.textmanager.entities.annotations.Index;
import me.philnate.textmanager.entities.annotations.IndexField;
import me.philnate.textmanager.entities.annotations.IndexField.Ordering;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * Utility Class to instantiate Entity based Interfaces, which are wrapped into
 * proxies.
 * 
 * @author philnate
 * 
 */
public class Entities {

    private final DB db;

    /**
     * @param db
     *            reference to db which is used for storage
     */
    public Entities(DB db) {
	this.db = db;
    }

    /**
     * Creates a new instance of the supplied Class with the given
     * InvocationHandler. Supposed to be only invoked through UnitTests.
     * 
     * @param clazz
     *            Class to instantiate a new Instance of, must have
     *            {@link Entity} as supertype
     * @param handler
     *            which does the functional work
     * @return new Instance of the given class
     */
    @SuppressWarnings("unchecked")
    <T extends Entity> T instantiate(Class<T> clazz,
	    EntityInvocationHandler handler) {
	return (T) Proxy.newProxyInstance(
		EntityInvocationHandler.class.getClassLoader(),
		new Class<?>[] { clazz }, handler);
    }

    /**
     * Create a new instance of the supplied Class
     * 
     * @param clazz
     *            Class to instantiate a new Instance of, must have
     *            {@link Entity} as supertype
     * @return new Instance of the given class
     */
    public <T extends Entity> T instantiate(Class<T> clazz) {
	return instantiate(clazz, new EntityInvocationHandler(clazz, db));
    }

    /**
     * Searches classpath for all Occurences of a {@link Entity}(or only classes
     * extending a given class) extending interfaces and creates indexes for
     * these classes. This method is normally called on System startup to ensure
     * that all indexes are in place
     * 
     * @param clazz
     * @param type
     */
    public static void addIndexes(Class<? extends Entity> clazz) {
	throw new UnsupportedOperationException();
    }

    /**
     * Executes the specified index operation for the given Class(creating all
     * Indexes)
     * 
     * @param clazz
     *            for which the indexes shall be created
     * @param type
     *            type of the index creation on CREATE it will fail if the index
     *            already exists
     */
    // TODO make this package private once addIndexes is filled with life
    public void addIndex(Class<? extends Entity> clazz) {

	Collection col = clazz.getAnnotation(Collection.class);
	Set<String> fields = getFields(clazz);
	// iterate through all Indexes and create index
	if (col != null && col.indexes() != null) {
	    for (Index idx : col.indexes()) {
		DBObject index = new BasicDBObject();
		for (IndexField field : idx.value()) {
		    checkArgument(
			    fields.contains(field.field()),
			    format("Indexes can only be created on existing fields. Got '%s' but isn't in known fields %s",
				    field.field(), fields));
		    index.put(field.field(), field.order() == Ordering.ASC ? 1
			    : -1);
		}
		// create index
		db.getCollection(getCollectionName(clazz)).ensureIndex(index,
			DBCollection.genIndexName(index), idx.unqiue());
	    }
	}
    }
}
