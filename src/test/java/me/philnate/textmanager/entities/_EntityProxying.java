package me.philnate.textmanager.entities;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class _EntityProxying {

    private EntityInvocationHandler handler;

    @Before
    public void setup() {
	handler = new EntityInvocationHandler();
    }

    @Test
    public void testInstantiationValidChain() {
	Entities.instantiate(Entity.class);
	Entities.instantiate(Valid.class);
    }

    @Test
    public void testInstatiateNonValid() {
	try {
	    Entities.instantiate(InValid.class);
	    fail("Proxying only allowed for interfaces");
	} catch (Exception e) {
	    assertThat(e.getMessage(), containsString("is not an interface"));
	}
    }

    @Test
    public void testProxySet() {
	BasicDBObject spy = Mockito.spy(new BasicDBObject());
	handler.container = spy;

	Valid obj = Entities.instantiate(Valid.class, handler).setType("me");
	assertNotNull(obj);
	verify(spy, times(1)).put("type", "me");
    }

    @Test
    public void testProxyGet() {
	BasicDBObject spy = Mockito.spy(new BasicDBObject());
	handler.container = spy;

	when(spy.get("type")).thenReturn("me");
	Valid obj = Entities.instantiate(Valid.class, handler).setType("me");
	assertEquals("me", obj.getType());
    }

    @Test
    public void testIsModifiedFlag() {
	assertFalse(handler.hasChanged);

	Valid obj = Entities.instantiate(Valid.class, handler).setType("me");
	assertTrue(handler.hasChanged);

	handler.hasChanged = false;
	obj.setType("me");
	assertFalse(handler.hasChanged);
	obj.setType("em");
	assertTrue(handler.hasChanged);
    }

    @Test
    public void testToString() {
	Valid obj = Entities.instantiate(Valid.class, handler).setType("me");
	assertThat(obj.toString(), is("{ \"type\" : \"me\"}"));
    }

    @Test
    public void testSetIsMissingParam() {
	try {
	    Entities.instantiate(Defect.class).setNoParam();
	    fail("Should throw an excpetion as set without param makes no sense");
	} catch (NullPointerException e) {
	    assertThat(
		    e.getMessage(),
		    containsString("Set method without any argument isn't valid"));
	}
    }

    @Test
    public void testCopyOnChange() {
	Valid obj = Entities.instantiate(Valid.class, handler);
	assertNotNull(handler.oldVersions);
	assertEquals(0, handler.oldVersions.size());
	assertNull(handler.container.get("type"));

	// for new objects we don't need to copy data over
	obj.setType("me");
	assertEquals(0, handler.oldVersions.size());
	assertEquals("me", handler.container.get("type"));

	obj.setType("em");
	assertEquals(1, handler.oldVersions.size());
	assertEquals("em", handler.container.get("type"));
	assertEquals("me", ((DBObject) handler.oldVersions.get(0)).get("type"));
    }

    @Test
    public void testSetHasTooManyParams() {
	try {
	    Entities.instantiate(Defect.class).setMultiParam("one", "two");
	    fail("Should fail as multiple params don't make any sense today");
	} catch (IllegalArgumentException e) {
	    assertThat(
		    e.getMessage(),
		    containsString("Set method is expected to only hold one argument"));
	}
    }

    @Test
    public void testGetNoParams() {
	try {
	    Entities.instantiate(Defect.class).getParam("one");
	    fail("should throw an exception as gets don't take arguments");
	} catch (IllegalArgumentException e) {
	    assertThat(
		    e.getMessage(),
		    containsString("Get method is expected to have no arguments"));
	}
    }

    private static abstract class InValid implements Entity {
    }

    private static interface Defect extends Entity {
	public Defect setNoParam();

	public Defect setMultiParam(String arg1, String arg2);

	public String getParam(String arg);
    }

    private static interface Valid extends Entity {
	public Valid setType(String type);

	public String getType();
    }
}
