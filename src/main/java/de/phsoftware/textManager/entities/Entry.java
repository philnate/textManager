/**
 *   textManager, a GUI for managing bills for texter jobs
 *
 *   Copyright (C) 2012- philnate
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
package de.phsoftware.textManager.entities;

import static de.phsoftware.textManager.utils.DB.ds;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Per convention classes implementing Entry should implement a
 * <code>public static Query&lt;T&gt; find()</code> method as well as a find
 * method with parameters of the primary doc key returning at most one document
 * ( <code>public static
 * T find(Object... docKeyPart)</code>)
 * 
 * @author philnate
 * 
 */
public abstract class Entry {

    public void save() {
	ds.save(this);
    }

    @Override
    public String toString() {
	return ToStringBuilder.reflectionToString(this);
    }

    /**
     * deletes the current entry and all linked documents if existent
     */
    public abstract void delete();
}
