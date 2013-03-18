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
package me.philnate.textmanager.entities.annotations;

/**
 * Holds information about one single Field of an Index
 * 
 * @author philnate
 * 
 */
public @interface IndexField {
    /**
     * name of the field for this Index, must match the actual name used for the
     * field in mongodb (not in java)
     */
    public String field();

    /**
     * Ordering of the Field (ascending=true, descending=false)
     */
    public boolean order() default true;
}