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
package me.philnate.textmanager.web.entities;

import com.github.cherimojava.data.mongo.entity.Entity;
import com.github.cherimojava.data.mongo.entity.annotation.Id;

/**
 * holds information about the application configuration
 *
 * @author philnate
 */
public interface Setting extends Entity<Setting> {
	/**
	 * Name of setting
	 */
	public Setting setName(String key);

	@Id
	public String getName();

	/**
	 * Value of setting
	 */
	public Setting setValue(String value);

	public String getValue();
}
