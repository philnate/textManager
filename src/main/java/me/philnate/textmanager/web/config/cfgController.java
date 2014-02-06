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
package me.philnate.textmanager.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.github.cherimojava.data.mongo.entity.EntityFactory;
import com.github.cherimojava.data.spring.EntityConverter;

import me.philnate.textmanager.web.controller.HomeController;
import me.philnate.textmanager.web.controller.LayoutController;
import me.philnate.textmanager.web.controller.SettingController;

import static me.philnate.textmanager.web.config.RootConfig.PROFILE_UNITTEST;

@Configuration
@Profile("!" + PROFILE_UNITTEST)
public class cfgController {

	@Bean
	public HomeController homeController() {
		return new HomeController();
	}

	@Bean
	public SettingController settingController() {
		return new SettingController();
	}

	@Bean
	public LayoutController layoutController() {
		return new LayoutController();
	}

	@Bean
	@Autowired
	public EntityConverter entityConverter(EntityFactory factory) {
		return new EntityConverter(factory);
	}
}
