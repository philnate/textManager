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
