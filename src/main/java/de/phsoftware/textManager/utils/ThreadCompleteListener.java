package de.phsoftware.textManager.utils;

/**
 * Implementing classes may be able to be notified when a Thread finishes
 * 
 * @author philnate
 * 
 */
public interface ThreadCompleteListener {

    public void threadCompleted(NotifyingThread notifyingThread);
}
