package com.syncfree.dbclient.in;

import java.io.IOException;

import com.syncfree.dbclient.data.IClient;

/**
 * Interface to read client details from a device or file.
 * 
 * @author aas
 * @version 0.0
 */
public interface IInput {
    /**
     * @return true if there is more client details, or false otherwise.
     * 
     * @throws IOException when failed to access the data in the device/file.
     */
    public boolean hasNext() throws IOException;

    /**
     * @return the next client details.
     * 
     * @throws IOException when failed to access the data in the device/file.
     */
    public IClient next() throws IOException;
} // end interface IInput
