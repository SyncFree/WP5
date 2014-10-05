package com.syncfree.dbclient.out;

import com.syncfree.dbclient.data.IClient;

/**
 * Interface to save client details into a device or file.
 * 
 * @author aas
 * @version 0.0
 */
public interface IOutput {
    /**
     * Saves the specified client's details into the device/file.
     * 
     * @param client the client's details.
     * @throws Exception when failed to accomplish the task.
     */
    public void save(final IClient client) throws Exception;
} // end interface IOutput