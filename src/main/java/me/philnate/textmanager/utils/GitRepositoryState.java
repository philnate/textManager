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
package me.philnate.textmanager.utils;

import org.springframework.beans.factory.annotation.Value;

/**
 * Simple container for information about git repository and build commit, like
 * from https://github.com/ktoso/maven-git-commit-id-plugin/ suggested
 * 
 * @author philnate
 * 
 */
public class GitRepositoryState {

    @Value("${git.branch}")
    public String branch;

    @Value("${git.commit.id}")
    public String commitId;

    @Value("${git.commit.id.abbrev}")
    public String commitIdAbbrev;

    @Value("${git.build.user.name}")
    public String buildUserName;

    @Value("${git.build.user.email}")
    public String buildUserEmail;

    @Value("${git.build.time}")
    public String buildTime;

    @Value("${git.commit.user.name}")
    public String commitUserName;

    @Value("${git.commit.user.email}")
    public String commitUserEmail;

    @Value("${git.commit.message.full}")
    public String commitMessageFull;

    @Value("${git.commit.message.short}")
    public String commitMessageShort;

    @Value("${git.commit.time}")
    public String commitTime;
    // TODO doesn't work in the moment
    // @Value("${maven.project.version}")
    // public String mavenProjectVersion;
}
