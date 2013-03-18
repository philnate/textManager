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
import me.philnate.textmanager.TestBase;
import me.philnate.textmanager.entities.annotations.Named;

import org.junit.Ignore;
import org.junit.Test;

/**
 * tests about {@link Named} resolution in Entities
 * 
 * @author philnate
 * 
 */
public class _Named extends TestBase {
    private EntityInvocationHandler handler;

    @Test
    public void testNamedReplacesMethodname() {
	handler = new EntityInvocationHandler(NamedClass.class);
	Entities.instantiate(NamedClass.class, handler).setType("id");
	assertEquals("id", handler.container.get("_id"));
	assertNull(handler.container.get("type"));
    }

    @Test
    @Ignore
    public void testIdFieldAndNamedIdSet() {
	// should give an exception
    }

    @Test
    public void testNamedValueSet() {
	try {
	    new EntityInvocationHandler(EmptyNamed.class);
	    fail("should throw an IAE");
	} catch (IllegalArgumentException e) {
	    assertThat(
		    e.getMessage(),
		    containsString("Name of field must be not empty or null. On method 'setNamed'"));
	}
    }

    @Test
    public void testIgnoreNamedOnGet() {
	handler = new EntityInvocationHandler(NamedClass.class);
	Entities.instantiate(NamedClass.class, handler).setValue("val");
	assertEquals("val", handler.container.get("value"));
	assertNull(handler.container.get("val"));
    }

    private static interface EmptyNamed extends Entity {
	@Named("")
	public EmptyNamed setNamed(String named);
    }

    private static interface NamedClass extends Entity {
	@Named("_id")
	public NamedClass setType(String type);

	public NamedClass setValue(String value);

	@Named("val")
	public String getValue();
    }
}