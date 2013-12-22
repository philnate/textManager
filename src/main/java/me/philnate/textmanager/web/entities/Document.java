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
