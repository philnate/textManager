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

import static me.philnate.textmanager.entities.EntityUtils.getCollectionName;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import me.philnate.textmanager.MongoBase;
import me.philnate.textmanager.entities.Entities.IndexOperationType;
import me.philnate.textmanager.entities.annotations.Collection;
import me.philnate.textmanager.entities.annotations.Index;
import me.philnate.textmanager.entities.annotations.IndexField;
import me.philnate.textmanager.entities.annotations.IndexField.Ordering;
import me.philnate.textmanager.entities.annotations.Named;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;

/**
 * tests around the Collection Annotation
 * 
 * @author philnate
 * 
 */
public class _Collection extends MongoBase {

    @Autowired
    DB db;

    @Autowired
    String dbName;

    @Test
    public void testNameResolution() {
	assertEquals("entity", getCollectionName(Entity.class));
	assertEquals("camelCase", getCollectionName(CamelCase.class));
    }

    @Test
    public void testNameResolutionAnnotation() {
	assertEquals("myCollection", getCollectionName(DifferentName.class));
    }

    @Test
    public void testIndexCreation() {
	entities.addIndex(Indexes.class, IndexOperationType.CREATE);
	DBCollection col = db.getCollection("system.indexes");
	assertEquals(
		1,
		col.find(
			BasicDBObjectBuilder.start("name", "_id_-1_type_1")
				.add("ns", dbName + ".indexes").get()).count());
	// should not fail if invoked twice
	entities.addIndex(Indexes.class, IndexOperationType.CREATE);
    }

    @Test
    public void testIndexCreationNotValidField() {
	try {
	    entities.addIndex(IndexNoField.class, IndexOperationType.CREATE);
	    fail("Should throw an exception");
	} catch (IllegalArgumentException e) {
	    assertThat(
		    e.getMessage(),
		    containsString("Indexes can only be created on existing fields. Got 'notHere' but isn't in known fields []"));
	}
    }

    @Test
    @Ignore
    public void testNoNameSetButCollectionAnnotationPresent() {
	// TODO should be possible to have Annotation just for declaring Indexes
    }

    @Test
    @Ignore
    public void testSparseIndex() {

    }

    @Test
    @Ignore
    public void testUniqueIndex() {

    }

    private static interface CamelCase extends Entity {
    }

    // possible white spaces should be trimmed
    @Collection(name = " myCollection ")
    private static interface DifferentName extends Entity {
    }

    @Collection(name = "indexes", indexes = @Index({
	    @IndexField(field = "_id", order = Ordering.DESC),
	    @IndexField(field = "type", order = Ordering.ASC) }))
    private static interface Indexes extends Entity {
	public Indexes setId(String id);

	@Named("type")
	public Indexes setMyType(String type);
    }

    @Collection(name = "indexNoField", indexes = @Index({ @IndexField(field = "notHere") }))
    private static interface IndexNoField extends Entity {
    }
}
