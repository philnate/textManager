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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import me.philnate.textmanager.MongoBase;
import me.philnate.textmanager.entities.annotations.Id;
import me.philnate.textmanager.entities.annotations.Named;

import org.junit.Ignore;
import org.junit.Test;

/**
 * tests about {@link Id} resolution in Entities
 * 
 * @author philnate
 * 
 */
public class _Id extends MongoBase {
    private EntityInvocationHandler handler;

    @Test
    public void testIdResolution() {
	handler = newHandler(Ided.class);
	entities.instantiate(Ided.class, handler).setId("1234");
	assertEquals("1234", handler.container.get("_id"));
    }

    @Test
    public void testIdAnnotationResolution() {
	handler = newHandler(CustomId.class);
	entities.instantiate(CustomId.class, handler).setMyId("test");
	assertEquals("test", handler.container.get("_id"));
    }

    @Test
    public void testNotAllowedMultipleIdAnnotations() {
	try {
	    newHandler(MultiIdInValid.class);
	    fail("should throw an IAE exception");
	} catch (IllegalArgumentException e) {
	    assertEquals(
		    "You cannot have multiple properties named '_id'. Please check your set method names and @Named annotations.",
		    e.getMessage());
	}
    }

    @Test
    public void testIgnoreIdOnGet() {
	handler = newHandler(IdOnGet.class);
	entities.instantiate(IdOnGet.class, handler).setMyId("id");
	assertEquals("id", handler.container.get("myId"));
	assertNull(handler.container.get("_id"));
    }

    @Test
    @Ignore
    public void testIdFieldAndNamedIdSet() {
	try {
	    entities.instantiate(ConflictingNamed.class);
	    fail("should throw an NPE");
	} catch (IllegalArgumentException e) {
	    assertThat(
		    e.getMessage(),
		    containsString("You can not have a method annotated with Named(id) and a setId method"));
	}
    }

    private static interface ConflictingNamed extends Entity {
	public ConflictingNamed setId(String type);

	@Named("_id")
	public ConflictingNamed setType(String id);
    }

    private static interface IdOnGet extends Entity {
	public IdOnGet setMyId(String id);

	@Id
	public String getMyId();
    }

    private static interface MultiIdInValid extends Entity {
	@Id
	public MultiIdInValid setMyId(String myId);

	@Id
	public MultiIdInValid setIdMy(String idMy);
    }

    private static interface Ided extends Entity {
	public Ided setId(String id);
    }

    private static interface CustomId extends Entity {
	@Id
	public CustomId setMyId(String myid);
    }
}
