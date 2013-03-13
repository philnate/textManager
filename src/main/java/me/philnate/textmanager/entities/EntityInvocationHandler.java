package me.philnate.textmanager.entities;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.Introspector;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

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
     * stores if the Object has changed or not
     */
    boolean hasChanged = false;

    /**
     * contains older Versions of the document, each version is distinguished
     * through a version number(position in array)
     */
    BasicDBList oldVersions = new BasicDBList();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
	    throws Throwable {
	String name = method.getName();
	String nameNoPrefix = Introspector.decapitalize(name.substring(3));
	if (name.startsWith("set")) {
	    checkNotNull(args, "Set method without any argument isn't valid");
	    checkArgument(args.length == 1,
		    "Set method is expected to only hold one argument");
	    if (!args[0].equals(container.get(nameNoPrefix))) {
		if (!container.isEmpty()) {
		    oldVersions.add(container.copy());
		}
		hasChanged = true;
		container.put(nameNoPrefix, args[0]);
	    }
	    if (method.getReturnType().isInstance(proxy)) {
		return proxy;
	    }
	} else if (name.startsWith("get")) {
	    checkArgument(args == null || args.length == 0,
		    "Get method is expected to have no arguments");
	    return container.get(nameNoPrefix);
	} else if (name.equals("toString")) {
	    return container.toString();
	}
	return null;
    }

}
