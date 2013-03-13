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
