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

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import me.philnate.textmanager.web.config.datastore.cfgMongo;
import me.philnate.textmanager.web.config.profiles.cfgProduction;
import me.philnate.textmanager.web.config.profiles.cfgTesting;

@Configuration
@Import({ cfgProduction.class, cfgTesting.class, cfgMongo.class, cfgController.class })
public class RootConfig {

	// config needed for production
	public static final String PROFILE_PRODUCTION = "production";
	// config needed for testing
	public static final String PROFILE_TESTING = "testing";
	// config needed for unit test
	public static final String PROFILE_UNITTEST = "unittest";

	@Bean
	public static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
		PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
		return ppc;
	}
}
