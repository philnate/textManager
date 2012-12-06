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
package me.philnate.textmanager.utils;

import java.io.IOException;
import java.io.InputStream;
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

    /**
     * takes a Process, starts it and waits for its completion. While it's
     * running all output will be printed to commandline
     * 
     * @param builder
     * @return
     * @throws IOException
     */
    protected int printOutputStream(ProcessBuilder builder) throws IOException {
	Process proc = builder.start();
	InputStream is = proc.getInputStream();
	try {
	    Thread writer = new WriteStream(is);
	    writer.start();
	    int rc = proc.waitFor();
	    if (writer.isAlive()) {
		Thread.sleep(1000);
		writer.interrupt();
	    }
	    System.out.println("Return code:" + rc);
	    return rc;
	} catch (InterruptedException e) {
	    return -1;
	} finally {
	    proc.destroy();
	    is.close();
	}
    }

    class WriteStream extends Thread {
	private final InputStream is;

	public WriteStream(InputStream is) {
	    this.is = is;
	}

	@Override
	public void run() {
	    while (true) {
		try {
		    for (int i = 0; i < is.available(); i++) {
			int c = is.read();
			if (c == -1) {
			    return;
			}
			System.out.print((char) c);
		    }
		    Thread.sleep(10);
		    if (isInterrupted()) {
			return;
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		    return;
		} catch (InterruptedException e) {
		    e.printStackTrace();
		    return;
		}
	    }
	}
    }
}
