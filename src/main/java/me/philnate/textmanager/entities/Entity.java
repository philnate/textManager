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
package me.philnate.textmanager.entities;

import me.philnate.textmanager.entities.annotations.Versioned;

/**
 * Basic Interface for documents containing common functionality for all
 * records. If the Document needs to be versioned one must add the Annotation
 * {@link Versioned}. If there's a a setter/Getter method called setId/getId
 * will be used as Id for this document.
 * 
 * @author philnate
 * 
 */
public interface Entity {
    /**
     * Saves the entity to MongoDB
     */
    public void save();

    public Object get(String name);

    public void set(String name, Object value);
}