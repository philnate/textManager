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

import java.beans.Introspector;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

import me.philnate.textmanager.entities.annotations.Id;
import me.philnate.textmanager.entities.annotations.Named;
import me.philnate.textmanager.entities.annotations.Versioned;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
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

    /**
     * shared instance to the DB where data is being stored
     */
    @Autowired
    static DB db;

    public EntityInvocationHandler(Class<? extends Entity> clazz) {
	collection = db.getCollection(Entities.getCollectionName(clazz));
	if (clazz.getAnnotation(Versioned.class) != null) {
	    isVersioned = true;
	}
	// detect if we have some explicitly named Id field (different from Id)
	Optional<String> idFieldName = Optional.absent();
	for (Method m : Lists.newArrayList(clazz.getMethods())) {
	    // only check for annotations on set (eases life)
	    if (!m.getName().startsWith("set")) {
		continue;
	    }
	    String methodName = getPropertyNameFromMethod(m);
	    if (m.isAnnotationPresent(Named.class)) {
		// check if given properties are mapped differently
		Named named = m.getAnnotation(Named.class);
		checkArgument(
			StringUtils.isNotEmpty(named.value()),
			format("Name of field must be not empty or null. On method '%s'",
				m.getName()));
		mappings.put(methodName, named.value());
	    }
	    // check for Id field
	    if (m.isAnnotationPresent(Id.class)) {
		if (idFieldName.isPresent()
			&& !idFieldName.get().equals(methodName)) {
		    throw new IllegalArgumentException(
			    format("You can only specify one @Id annotation per Document type, but found for '%s' [%s,%s]",
				    clazz.getName(), idFieldName.get(),
				    methodName));
		}
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
	    checkNotNull(args, "Set method without any argument isn't valid");
	    checkArgument(args.length == 1,
		    "Set method is expected to only hold one argument");
	    // only make any changes if the new value is different from the old
	    // one
	    if (!args[0].equals(container.get(nameNoPrefix))) {
		// copy the old version of document only if it's not new (empty)
		if (isVersioned && !container.isEmpty() && !hasChanged) {
		    oldVersions.add(container.copy());
		}
		hasChanged = true;
		container.put(nameNoPrefix, args[0]);
	    }
	    // if method has Class as return type return the proxy
	    if (method.getReturnType().isInstance(proxy)) {
		return proxy;
	    }
	} else if (name.startsWith("get")) {
	    checkArgument(args == null || args.length == 0,
		    "Get method is expected to have no arguments");
	    return container.get(nameNoPrefix);
	} else if (name.equals("save")) {
	    collection.save(container);
	} else if (name.equals("toString")) {
	    return container.toString();
	}
	return null;
    }

    private static String getPropertyNameFromMethod(Method m) {
	return Introspector.decapitalize(m.getName().substring(3));
    }
}
