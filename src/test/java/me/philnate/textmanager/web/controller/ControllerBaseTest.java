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

import org.hamcrest.Matcher;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import me.philnate.textmanager.web.config.RootConfig;

import static me.philnate.textmanager.web.config.RootConfig.PROFILE_TESTING;
import static me.philnate.textmanager.web.config.RootConfig.PROFILE_UNITTEST;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { RootConfig.class })
@WebAppConfiguration
@ActiveProfiles({ PROFILE_UNITTEST, PROFILE_TESTING })
public class ControllerBaseTest {

	public void assertJson(Matcher<? super String> expected, String actual) {
		assertThat(actual, expected);
	}
}
