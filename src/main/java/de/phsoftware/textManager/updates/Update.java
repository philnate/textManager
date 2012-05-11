package de.phsoftware.textManager.updates;


/**
 * Base class from which concrete schema updates inherit. Performs basic
 * validations and starts update process
 * 
 * @author philnate
 * 
 */
public abstract class Update {
    protected static int versionToUpdate;

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
