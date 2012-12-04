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
package me.philnate.textmanager.updates;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.primitives.Ints;

public class Version implements Comparable<Version> {
    private int major;
    private int minor;
    private int patch;

    protected static final int BEFORE = -1;
    protected static final int EQUAL = 0;
    protected static final int AFTER = 1;

    /**
     * creates a new Version without any Version Information set, thus it will
     * default to 0.0.0
     */
    public Version() {
	setVersion(0, 0, 0);
    }

    /**
     * creates a new Version based upon this String, may contain only a major,
     * major.minor or major.minor.patch information
     * 
     * @param version
     */
    public Version(String version) {
	checkNotNull(version, "Version may not be null");
	checkArgument(version.matches("[0-9]+(.[0-9]+(.[0-9]+)?)?"));
	String[] parts = version.split("\\.");
	switch (parts.length) {
	case 3:/* fallthrough */
	    patch = defaultInt(parts[2]);
	case 2:/* fallthrough */
	    minor = defaultInt(parts[1]);
	case 1:
	    major = defaultInt(parts[0]);
	    break;
	default:
	    throw new IllegalArgumentException(
		    "Size of array is zero, should not happen");
	}
    }

    /**
     * reads the given Value or 0 if it couldn't be parsed
     * 
     * @param value
     * @return
     */
    private int defaultInt(String value) {
	return Objects.firstNonNull(Ints.tryParse(value), 0);
    }

    private void setVersion(int major, int minor, int patch) {
	this.major = major;
	this.minor = minor;
	this.patch = patch;
    }

    public int getMajor() {
	return major;
    }

    public int getMinor() {
	return minor;
    }

    public int getPatch() {
	return patch;
    }

    /**
     * returns the version information in major.minor.patch format
     */
    @Override
    public String toString() {
	return String.format("%s.%s.%s", major, minor, patch);
    }

    @Override
    public int compareTo(Version o) {
	if (o == this) {
	    return EQUAL;
	}
	return ComparisonChain.start().compare(this.major, o.major)
		.compare(this.minor, o.minor).compare(this.patch, o.patch)
		.result();
    }
}
