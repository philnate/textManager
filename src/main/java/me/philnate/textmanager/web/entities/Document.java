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

import org.bson.types.ObjectId;

import com.github.cherimojava.data.mongo.entity.Entity;

/**
 * Holds the information about stored documents like title and wordcount.
 *
 * @author philnate
 */
public interface Document extends Entity<Document> {

	public Document setId(ObjectId id);

	public ObjectId getId();

	public Document setTitle(String title);

	public String getTitle();

	public Document setWordCount(Integer wc);

	public Integer getWordCount();
}
