/**
 * Copyright (C) 2012 philnate (http://github.com/philnate/textmanager)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.philnate.textmanager.web.controller;

import java.util.List;

import org.mongodb.Document;
import org.mongodb.MongoCursor;
import org.mongodb.MongoDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.cherimojava.data.mongo.entity.EntityFactory;
import com.github.cherimojava.data.mongo.entity.EntityUtils;
import com.github.cherimojava.data.mongo.io.EntityCodec;
import com.google.common.collect.Lists;

import com.github.cherimojava.data.spring.Listed;
import me.philnate.textmanager.web.entities.Setting;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Controller managing Settings of textManager
 */
@RestController
@RequestMapping(value = "/entity/setting", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class SettingController {

	@Autowired
	protected EntityFactory factory;

	@Autowired
	protected MongoDatabase db;

	/**
	 * returns OK if the document was successfully saved
	 *
	 * @param setting
	 * @return
	 */
	@RequestMapping(method = POST)
	public ResponseEntity post(@RequestBody Setting setting) {
		setting.save();
		return new ResponseEntity(HttpStatus.OK);
	}

	@RequestMapping(value = "/{key}", method = GET)
	public Object get(@PathVariable("key") String key) {
		Setting s = factory.load(Setting.class, key);
		if (s != null) {
			return s;
		} else {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(value = "/query", method = POST)
	public Listed<Setting> query() {
		MongoCursor<Setting> cursor = db.getCollection(EntityUtils.getCollectionName(Setting.class),
				new EntityCodec<Setting>(db, EntityFactory.getProperties(Setting.class))).find(new Document()).iterator();
		List<Setting> settings = Lists.newArrayList(cursor);
		cursor.close();
		return EntityFactory.instantiate(Listed.class).setList(settings);
	}
}
