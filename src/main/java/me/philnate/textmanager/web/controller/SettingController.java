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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.cherimojava.data.mongo.entity.EntityFactory;

import me.philnate.textmanager.web.entities.Setting;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = "/entity/setting", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class SettingController {

	@Autowired
	protected EntityFactory factory;

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
}
