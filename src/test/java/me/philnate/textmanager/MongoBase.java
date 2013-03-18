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
package me.philnate.textmanager;

import static org.junit.Assert.assertThat;

import java.util.List;

import me.philnate.textmanager.entities.Entities;
import me.philnate.textmanager.entities.Entity;
import me.philnate.textmanager.entities.EntityInvocationHandler;
import me.philnate.textmanager.web.config.cfgMongoDB;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Lists;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

/**
 * Base Class for Tests requiring MongoDB
 * 
 * @author philnate
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(cfgMongoDB.PROFILE_TESTING)
@ContextConfiguration(classes = cfgMongoDB.class)
public abstract class MongoBase {

    @Autowired
    private DB db;

    @Autowired
    protected Entities entities;

    List<DBCollection> collectionPurge = Lists.newArrayList();

    /**
     * CleanUp all temporary collection data
     */
    @After
    public final void collectionCleanup() {
	for (DBCollection col : collectionPurge) {
	    col.drop();
	}
    }

    /**
     * retrieves DBCollection for given Entity based class and remembers it, so
     * that on shutdown of the TC a cleanUp can be done
     * 
     * @param clazz
     * @return
     */
    public DBCollection getCollection(Class<? extends Entity> clazz) {
	// TODO lets make this unique per TestCase, so we can run tests in
	// parallel
	DBCollection col = db.getCollection(Entities.getCollectionName(clazz));
	collectionPurge.add(col);
	return col;
    }

    public EntityInvocationHandler newHandler(Class<? extends Entity> clazz) {
	return new EntityInvocationHandler(clazz, db);
    }

    /**
     * just some little tool method to avoid that JSONAssert requires the Test
     * method to throw an exception
     * 
     * @param expected
     * @param actual
     * @param exception
     * @return
     */
    public void assertJson(DBObject actual, Matcher<? super String> expected) {
	assertThat(JSON.serialize(actual), expected);
    }
}