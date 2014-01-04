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
