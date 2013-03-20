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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;
import me.philnate.textmanager.MongoBase;
import me.philnate.textmanager.entities.annotations.Versioned;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

public class _EntityProxying extends MongoBase {

    private EntityInvocationHandler handler;

    @Before
    public void setup() {
    }

    @Test
    public void testInstantiationValidChain() {
	entities.instantiate(Entity.class);
	entities.instantiate(Valid.class);
    }

    @Test
    public void testInstatiateNonValid() {
	try {
	    entities.instantiate(InValid.class);
	    fail("Proxying only allowed for interfaces");
	} catch (Exception e) {
	    assertThat(e.getMessage(), containsString("is not an interface"));
	}
    }

    @Test
    public void testProxySet() {
	BasicDBObject spy = Mockito.spy(new BasicDBObject());
	handler = newHandler(Valid.class);
	handler.container = spy;

	Valid obj = entities.instantiate(Valid.class, handler).setType("me");
	assertNotNull(obj);
	verify(spy, times(1)).put("type", "me");
	assertEquals(1, handler.container.size());
    }

    @Test
    public void testProxyGet() {
	BasicDBObject spy = Mockito.spy(new BasicDBObject());
	handler = newHandler(Valid.class);
	handler.container = spy;

	when(spy.get("type")).thenReturn("me");
	Valid obj = entities.instantiate(Valid.class, handler).setType("me");
	assertEquals("me", obj.getType());
    }

    @Test
    public void testIsModifiedFlagVersioned() {
	handler = newHandler(VersionedValid.class);
	assertFalse(handler.hasChanged);

	VersionedValid obj = entities
		.instantiate(VersionedValid.class, handler).setType("me");
	assertTrue(handler.hasChanged);
	assertEquals(0, handler.oldVersions.size());
	handler.hasChanged = false;
	obj.setType("me");
	assertFalse(handler.hasChanged);
	assertEquals(0, handler.oldVersions.size());
	obj.setType("em");
	assertTrue(handler.hasChanged);
	assertEquals(1, handler.oldVersions.size());
    }

    @Test
    public void testModifiedFlagEntity() {
	handler = newHandler(Valid.class);
	assertFalse(handler.hasChanged);

	Valid obj = entities.instantiate(Valid.class, handler).setType("me");
	assertTrue(handler.hasChanged);
	assertEquals(0, handler.oldVersions.size());
	obj.setType("me");
	assertTrue(handler.hasChanged);
	assertEquals(0, handler.oldVersions.size());
	obj.setType("em");
	assertTrue(handler.hasChanged);
	assertEquals(0, handler.oldVersions.size());
    }

    @Test
    public void testToString() {
	handler = newHandler(Valid.class);
	Valid obj = entities.instantiate(Valid.class, handler).setType("me");
	assertThat(obj.toString(), is("{ \"type\" : \"me\"}"));
    }

    @Test
    public void testSetIsMissingParam() {
	try {
	    entities.instantiate(Defect.class).setNoParam();
	    fail("Should throw an excpetion as set without param makes no sense");
	} catch (NullPointerException e) {
	    assertThat(
		    e.getMessage(),
		    containsString("Set method without any argument isn't valid"));
	}
    }

    @Test
    public void testSetHasTooManyParams() {
	try {
	    entities.instantiate(Defect.class).setMultiParam("one", "two");
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
	    entities.instantiate(Defect.class).getParam("one");
	    fail("should throw an exception as gets don't take arguments");
	} catch (IllegalArgumentException e) {
	    assertThat(
		    e.getMessage(),
		    containsString("Get method is expected to have no arguments"));
	}
    }

    // TODO move those tests into custom Class for testing read/write (keeping
    // this free from MongoBase
    @Test
    public void testSaveEntity() {
	DBCollection col = getCollection(Valid.class);
	assertEquals(0, col.count());
	entities.instantiate(Valid.class).setType("me").save();
	assertEquals(1, col.count());
	assertJson(col.findOne(), sameJSONAs("{type:\"me\"}")
		.allowingExtraUnexpectedFields());
	// TODO extend to read saved entry back
    }

    // TODO add Tests/functionality to set/get Value arbitrary

    @Test
    public void testSaveVersionedEntity() {
	DBCollection col = getCollection(Valid.class);
	assertEquals(0, col.count());
	entities.instantiate(Valid.class).setType("me").save();
	assertEquals(1, col.count());
	assertJson(col.findOne(), sameJSONAs("{type:\"me\"}")
		.allowingExtraUnexpectedFields());
	// TODO extend to save versioned information
	// TODO extend to read saved entry back
    }

    private static abstract class InValid implements Entity {
    }

    private static interface Defect extends Entity {
	public Defect setNoParam();

	public Defect setMultiParam(String arg1, String arg2);

	public String getParam(String arg);
    }

    @Versioned
    private static interface VersionedValid extends Entity {
	public VersionedValid setType(String param);
    }

    private static interface Valid extends Entity {
	public Valid setType(String type);

	public String getType();
    }
}
