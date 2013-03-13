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

import java.lang.reflect.Proxy;

/**
 * Utility Class to instantiate Entity based Interfaces, which are wrapped into
 * proxies.
 * 
 * @author philnate
 * 
 */
public class Entities {

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
    static <T extends Entity> T instantiate(Class<T> clazz,
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
	return instantiate(clazz, new EntityInvocationHandler());
    }
}
