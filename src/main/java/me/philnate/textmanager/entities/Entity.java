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

import me.philnate.textmanager.entities.annotations.Named;
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

    /**
     * allows to programatically read a given value from an entity. Will fail if
     * the given property isn't declared through a method name /{@link Named}.
     * 
     * @param property
     *            name to retrive
     * @return value of the property or null if the value isn't present.
     * @throws IllegalArgumentException
     *             if the property isn't declared for that entity
     */
    public Object get(String property);

    /**
     * allows to programmatically set a given property for an entity. Will fail
     * if the property isn't declared either through an explicit set method or
     * {@link Named} annotation on a set method
     * 
     * @param property
     *            name to write to
     * @param value
     *            to write
     * @throws IllegalArgumentException
     *             if the property isn't declared for that entity
     */
    public void set(String property, Object value);
}