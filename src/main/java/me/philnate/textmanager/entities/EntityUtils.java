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

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.util.Set;

import me.philnate.textmanager.entities.annotations.Collection;
import me.philnate.textmanager.entities.annotations.Named;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Sets;

/**
 * Internal Utility methods for working with Entities
 * 
 * @author philnate
 * 
 */
public class EntityUtils {

    /* no need for an Instance */
    private EntityUtils() {
    }

    /**
     * retrieves from a Method the name which is used within MongoDB (First
     * letter lower case, if it's a CamelCase name or all Upper letter If it's
     * URL)
     * 
     * @param m
     *            method to retrieve Storage name from
     * @return
     */
    public static String getPropertyNameFromMethod(Method m) {
	return getPropertyNameFromMethod(m, false);
    }

    /**
     * Retrieves from a Method the name which is used within MongoDB. Presence
     * of {@link Named} can be ignored or not.
     * 
     * @param m
     *            method to retrieve Storage name from
     * @param ignoreNamed
     *            tells if declared {@link Named} should be used to retrieve
     *            property name or not
     * @return property name matching this method
     */
    public static String getPropertyNameFromMethod(Method m, boolean ignoreNamed) {
	// if only the method Name shall be used for lookup ignore @Named
	Named named = ignoreNamed ? null : m.getAnnotation(Named.class);
	if (named != null) {
	    // check if given properties are mapped differently
	    checkArgument(
		    StringUtils.isNotEmpty(named.value()),
		    format("Name of field must be not empty or null. On method '%s'",
			    m.getName()));
	}
	String field = Introspector.decapitalize(named != null ? named.value()
		: m.getName().substring(3));
	return "id".equals(field) ? "_id" : field;
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
	// if we have a custom annotation we want to use this name instead
	return (col != null && StringUtils.isNotBlank(col.name()) ? col.name()
		.trim() : Introspector.decapitalize(clazz.getSimpleName()));
    }

    public static Set<String> getFields(Class<? extends Entity> clazz) {
	Set<String> fields = Sets.newHashSet();
	for (Method m : clazz.getMethods()) {
	    if (!m.getName().startsWith("set")) {
		continue;
	    }
	    fields.add(getPropertyNameFromMethod(m));
	}
	return fields;
    }
}
