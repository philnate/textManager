/**
 *   textManager, a GUI for managing bills for texter jobs
 *
 *   Copyright (C) 2013 philnate
 *
 *   This file is part of textManager.
 *
 *   textManager is free software: you can redistribute it and/or modify it under the terms of the
 *   GNU General  License as published by the Free Software Foundation, either version 3 of the
 *   License, or (at your option) any later version.
 *
 *   textManager is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 *   See the GNU General  License for more details. You should have received a copy of the GNU
 *   General  License along with textManager. If not, see <http://www.gnu.org/licenses/>.
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
    String branch;

    @Value("${git.commit.id}")
    String commitId;

    @Value("${git.commit.id.describe}")
    String commitIdDescribe;

    @Value("${git.commit.id.abbrev}")
    String commitIdAbbrev;

    @Value("${git.build.user.name}")
    String buildUserName;

    @Value("${git.build.user.email}")
    String buildUserEmail;

    @Value("${git.build.time}")
    String buildTime;

    @Value("${git.commit.user.name}")
    String commitUserName;

    @Value("${git.commit.user.email}")
    String commitUserEmail;

    @Value("${git.commit.message.full}")
    String commitMessageFull;

    @Value("${git.commit.message.short}")
    String commitMessageShort;

    @Value("${git.commit.time}")
    String commitTime;

    // TODO doesn't work in the moment
    // @Value("${maven.project.version}")
    // String mavenProjectVersion;

    public String getBranch() {
	return branch;
    }

    public String getCommitId() {
	return commitId;
    }

    public String getCommitIdAbbrev() {
	return commitIdAbbrev;
    }

    public String getBuildUserName() {
	return buildUserName;
    }

    public String getBuildUserEmail() {
	return buildUserEmail;
    }

    public String getBuildTime() {
	return buildTime;
    }

    public String getCommitUserName() {
	return commitUserName;
    }

    public String getCommitUserEmail() {
	return commitUserEmail;
    }

    public String getCommitMessageFull() {
	return commitMessageFull;
    }

    public String getCommitMessageShort() {
	return commitMessageShort;
    }

    public String getCommitTime() {
	return commitTime;
    }

    public String getCommitIdDescribe() {
	return commitIdDescribe;
    }
}
