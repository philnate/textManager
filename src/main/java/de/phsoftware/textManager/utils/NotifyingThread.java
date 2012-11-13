/**
 *   textManager, a GUI for managing bills for texter jobs
 *
 *   Copyright (C) ${year} philnate
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
package de.phsoftware.textManager.utils;

import java.util.Set;

import com.google.common.collect.Sets;

public abstract class NotifyingThread extends Thread {

    private final Set<ThreadCompleteListener> listeners = Sets.newHashSet();

    public final void addListener(final ThreadCompleteListener listener) {
	listeners.add(listener);
    }

    public final void removeListener(final ThreadCompleteListener listener) {
	listeners.remove(listener);
    }

    private final void notifyListeners() {
	for (ThreadCompleteListener listener : listeners) {
	    listener.threadCompleted(this);
	}
    }

    public NotifyingThread(ThreadCompleteListener completeListener) {
	addListener(completeListener);
    }

    public NotifyingThread() {
    };

    @Override
    public final void run() {
	try {
	    doRun();
	} finally {
	    notifyListeners();
	}
    }

    protected abstract void doRun();

}
