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

import java.beans.Introspector;
import java.lang.reflect.Proxy;

import me.philnate.textmanager.entities.annotations.Collection;
import me.philnate.textmanager.entities.annotations.Id;

import org.apache.commons.lang.StringUtils;

/**
 * Utility Class to instantiate Entity based Interfaces, which are wrapped into
 * proxies.
 * 
 * @author philnate
 * 
 */
public class Entities {

    // Don't instantiate this
    private Entities() {
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
    static <T extends Id> T instantiate(Class<T> clazz,
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
    public static <T extends Entity> T instantiate(Class<T> clazz) {
	return instantiate(clazz, new EntityInvocationHandler(clazz));
    }

    /**
     * Returns the collection name of a given Entity. If there's no
     * {@link Collection} annotation the Interface name is being used. If a
     * {@link Collection} annotation is present the supplied name will be
     * returned (trimmed to remove possible whitespaces around it)
     * 
     * @param clazz
     *            from which the collection name shall be retrieved
     * @return name of the entity to use for MongoDB
     */
    public static String getCollectionName(Class<? extends Entity> clazz) {
	Collection col = clazz.getAnnotation(Collection.class);
	if (col != null) {
	    // if we have a custom annotation we want to use this name instead
	    checkArgument(StringUtils.isNotBlank(col.name()),
		    "You must insert a collection name");
	    return col.name().trim();
	}
	return Introspector.decapitalize(clazz.getSimpleName());
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
    public static void addIndexes(Class<? extends Entity> clazz,
	    IndexOperationType type) {

    }

    public static enum IndexOperationType {
	/**
	 * create Indexes, fails if Index exist
	 */
	CREATE,
	/**
	 * create Indexes, doesn't fail if Index alredy exist
	 */
	ENSURE
    }
}
