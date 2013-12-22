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
package me.philnate.textmanager.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.cherimojava.data.mongo.entity.EntityFactory;

import me.philnate.textmanager.web.controller.HomeController;
import me.philnate.textmanager.web.entities.Setting;
import me.philnate.textmanager.web.util.EntityConverter;

import static me.philnate.textmanager.web.config.RootConfig.PROFILE_PRODUCTION;

@Configuration(PROFILE_PRODUCTION)
public class cfgController {

	@Bean
	public HomeController homeController() {
		return new HomeController();
	}

	@Bean
	@Autowired
	public EntityConverter<Setting> settingConverter(EntityFactory factory) {
		return new EntityConverter<Setting>(factory.create(Setting.class), factory);
	}
}
