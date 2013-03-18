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

import static org.junit.Assert.assertEquals;
import me.philnate.textmanager.MongoBase;
import me.philnate.textmanager.entities.annotations.Collection;

import org.junit.Test;

/**
 * tests around the Collection Annotation
 * 
 * @author philnate
 * 
 */
public class _Collection extends MongoBase {

    @Test
    public void testNameResolution() {
	assertEquals("entity", Entities.getCollectionName(Entity.class));
	assertEquals("camelCase", Entities.getCollectionName(CamelCase.class));
    }

    @Test
    public void testNameResolutionAnnotation() {
	assertEquals("myCollection",
		Entities.getCollectionName(DifferentName.class));
    }

    private static interface CamelCase extends Entity {
    }

    // eventual white spaces should be trimmed
    @Collection(name = " myCollection ")
    private static interface DifferentName extends Entity {
    }

}
