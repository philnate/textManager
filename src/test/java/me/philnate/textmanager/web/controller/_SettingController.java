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

import org.junit.Before;
import org.junit.Test;
import org.mongodb.MongoDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.github.cherimojava.data.mongo.entity.EntityFactory;
import com.github.cherimojava.data.spring.EntityConverter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

public class _SettingController extends ControllerBaseTest {

	private MockMvc mvc;

	@Autowired
	private EntityFactory factory;

	@Autowired
	private MongoDatabase db;

	@Before
	public void setup() {
		SettingController sc = new SettingController();
		sc.factory = factory;
		sc.db = db;
		mvc = MockMvcBuilders.standaloneSetup(sc).setMessageConverters(new EntityConverter(factory)).build();
	}

	@Test
	public void getPost() throws Exception {
		String entity = "{ \"_id\" : \"unique\", \"value\" : \"1\" }";
		mvc.perform(get("/entity/setting/unique").contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isNotFound());
		mvc.perform(
				post("/entity/setting").contentType(MediaType.APPLICATION_JSON).content(entity).accept(
						MediaType.APPLICATION_JSON)).andExpect(status().isOk());
		mvc.perform(get("/entity/setting/unique").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(
				content().string(entity));
	}

	@Test
	public void queryAll() throws Exception {
		String some = "{ \"_id\" : \"some\", \"value\" : \"1\" }";
		String entity = "{ \"_id\" : \"entity\", \"value\" : \"1\" }";
		mvc.perform(
				post("/entity/setting").contentType(MediaType.APPLICATION_JSON).content(some).accept(
						MediaType.APPLICATION_JSON)).andExpect(status().isOk());
		mvc.perform(
				post("/entity/setting").contentType(MediaType.APPLICATION_JSON).content(entity).accept(
						MediaType.APPLICATION_JSON)).andExpect(status().isOk());
		assertJson(
				sameJSONAs("{ \"list\" : [{ \"_id\" : \"some\", \"value\" : \"1\" }, { \"_id\" : \"entity\", \"value\" : \"1\" }] }"),
				mvc.perform(
						post("/entity/setting/query").contentType(MediaType.APPLICATION_JSON).content("{}").accept(
								MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
	}
}
