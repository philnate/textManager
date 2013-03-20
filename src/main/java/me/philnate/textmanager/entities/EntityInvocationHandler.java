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
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static me.philnate.textmanager.entities.EntityUtils.getCollectionName;
import static me.philnate.textmanager.entities.EntityUtils.getPropertyNameFromMethod;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import me.philnate.textmanager.entities.annotations.Id;
import me.philnate.textmanager.entities.annotations.Named;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;

/**
 * Handler which maps Interfaces to the underlying DBObject which is then stored
 * in MongoDB
 * 
 * @author philnate
 * 
 */
public class EntityInvocationHandler implements InvocationHandler {

    /**
     * holds the actual data
     */
    BasicDBObject container = new BasicDBObject();

    /**
     * stores if the Object has changed or not, so that only changed objects
     * need to be changed
     */
    boolean hasChanged = false;

    /**
     * contains older Versions of the document, each version is distinguished
     * through a version number(position in array)
     */
    BasicDBList oldVersions = new BasicDBList();

    private boolean isVersioned = false;

    /**
     * Stores all fields this entity contains
     */
    private final Set<String> fields;
    /**
     * stores the name of the field which is used as Id
     */
    private final String idFieldName;

    /**
     * holds translations of Method names to properties in MongoDB
     */
    private final Map<String, String> mappings = Maps.newHashMap();

    /**
     * collection to which this class maps
     */
    private final DBCollection collection;

    public EntityInvocationHandler(Class<? extends Entity> clazz, DB db) {
	collection = db.getCollection(getCollectionName(clazz));
	fields = EntityUtils.getFields(clazz);

	if (VersionedEntity.class.isAssignableFrom(clazz)) {
	    isVersioned = true;
	}
	// detect if we have some explicitly named Id field (different from Id)
	Optional<String> idFieldName = Optional.absent();
	for (Method m : clazz.getMethods()) {
	    // only check for annotations on set (eases life)
	    if (!m.getName().startsWith("set")) {
		continue;
	    }
	    String methodName = getPropertyNameFromMethod(m);
	    mappings.put(getPropertyNameFromMethod(m, true), methodName);
	    // check for Id field
	    if (m.isAnnotationPresent(Id.class)) {
		idFieldName = Optional.of(methodName);
	    }
	}
	// set Id field per default to Id (thus annotation on setId/getId not
	// needed
	this.idFieldName = idFieldName.isPresent() ? idFieldName.get() : "id";
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
	    throws Throwable {
	String name = method.getName();
	// freed method name from prefix (set/get)
	String nameNoPrefix = getPropertyNameFromMethod(method);
	// check if there's a different naming for this method
	nameNoPrefix = Objects.firstNonNull(mappings.get(nameNoPrefix),
		nameNoPrefix);
	if (idFieldName.equals(nameNoPrefix)) {
	    // check if the property is Id then we need to slightly rename it to
	    // match mongodbs expectations
	    nameNoPrefix = "_id";
	}
	if (name.startsWith("set")) {
	    if (name.equals("set") && args.length == 2) {
		// entity defined set
		set((String) args[0], args[1]);
	    } else {
		// custom setter methods
		checkNotNull(args,
			"Set method without any argument isn't valid");
		checkArgument(args.length == 1,
			"Set method is expected to only hold one argument");
		set(nameNoPrefix, args[0]);
	    }
	    // if method has Class as return type return the proxy
	    if (method.getReturnType().isInstance(proxy)) {
		return proxy;
	    }
	} else if (name.startsWith("get")) {
	    if (name.equals("get") && args.length == 1) {
		// Entity defined get
		return get((String) args[0]);
	    } else {
		// custom get Method
		checkArgument(args == null || args.length == 0,
			"Get method is expected to have no arguments");
		return get(nameNoPrefix);
	    }
	} else if (name.equals("save")) {
	    collection.save(container);
	} else if (name.equals("toString")) {
	    return container.toString();
	}
	return null;
    }

    /**
     * sets the value for the given property, will fail with
     * IllegalArgumentException if the property isn't declared either through
     * set method or {@link Named} annotation on set method. If this entity is
     * versioned and it's the first change to it after write, a copy for history
     * will be created
     * 
     * @param property
     *            name of the property to set
     * @param value
     *            of the property to set
     * @throws IllegalArgumentException
     *             if the property tried to be set isn't declared for that
     *             entity
     */
    private void set(String property, Object value) {
	checkPropertyDeclared(property);
	// only make any changes if the new value is different from the old
	// one
	if (!value.equals(container.get(property))) {
	    // copy the old version of document only if it's not new (empty)
	    if (isVersioned && !container.isEmpty() && !hasChanged) {
		oldVersions.add(container.copy());
	    }
	    hasChanged = true;
	    container.put(property, value);
	}
    }

    private Object get(String property) {
	checkPropertyDeclared(property);
	return container.get(property);
    }

    private void checkPropertyDeclared(String property) {
	checkArgument(
		fields.contains(property),
		format("You can only access properties which have been declared through a setter methods. Given Property '%s', has no matching 'set%s' method.",
			property, EntityUtils.capitalize(property)));
    }
}
