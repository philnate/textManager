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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import me.philnate.textmanager.MongoBase;

import org.junit.Test;

public class _Entities extends MongoBase {

    private EntityInvocationHandler handler;

    @Test
    public void testSaveMethod() {
	handler = newHandler(TestProps.class);
	entities.instantiate(TestProps.class, handler).set("type", "me");
	assertEquals(1, handler.container.size());
	assertEquals("me", handler.container.get("type"));
    }

    @Test
    public void testSaveUnknownProperty() {
	handler = newHandler(TestProps.class);
	try {
	    entities.instantiate(TestProps.class, handler).set("typ", "me");
	    fail("should throw an NPE");
	} catch (IllegalArgumentException e) {
	    assertThat(
		    e.getMessage(),
		    containsString("You can only access properties which have been declared through a setter methods. Given Property 'typ', has no matching 'setTyp' method."));
	}
	assertEquals(0, handler.container.size());
    }

    @Test
    public void testReadUnknownProperty() {
	try {
	    entities.instantiate(TestProps.class).get("typ");
	    fail("should throw an NPE");
	} catch (IllegalArgumentException e) {
	    assertThat(
		    e.getMessage(),
		    containsString("You can only access properties which have been declared through a setter methods. Given Property 'typ', has no matching 'setTyp' method."));
	}
    }

    @Test
    public void testReadSaveProperty() {
	// test various combinations of get/set methods userdefined/entity
	// defined
	TestProps entity = entities.instantiate(TestProps.class);
	entity.set("type", "me");
	assertEquals("me", entity.get("type"));
	assertEquals("em", entity.setType("em").get("type"));
	entity.set("type", "me");
	assertEquals("me", entity.getType());
    }

    private static interface TestProps extends Entity {
	public TestProps setType(String type);

	public String getType();
    }
}
