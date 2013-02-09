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
package me.philnate.textmanager.updates;

/**
 * Base class from which concrete schema updates inherit. Performs basic
 * validations and starts update process
 * 
 * @author philnate
 * 
 */
public interface Update {

    /**
     * Performs preChecks verifying that an update is needed
     * 
     * @throws Exception
     *             when preCheck fails, telling that something isn't as expected
     *             for this db version
     */
    public abstract void preCheck();

    /**
     * performs actual upgrade
     * 
     * @throws Exception
     *             any exception which may be raised during updating
     */
    public abstract void upgrade();

    /**
     * performs postChecks verifying that everything is correct after upgrade,
     * calling rollback if something went wrong to set db in initial state.
     * 
     * @throws Exception
     *             when postCheck fails, telling what went wrong
     */
    public abstract void postCheck();

}
