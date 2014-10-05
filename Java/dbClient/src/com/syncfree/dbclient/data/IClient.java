package com.syncfree.dbclient.data;


/**
 * API to access to the elements of a DB client.
 * 
 * @author aas
 * @version 0.0
 */
public interface IClient {
    /**
     * The types of connections.
     * 
     * @author aas
     */
    public enum TYPE {
        HTTP,
        PBC;
    } // end TYPE


    /**
     * @return the name of the client.
     */
    public String getClientName();

    public IClient.TYPE getClientType();

    public String getURL();

    public int getPort();

    public String getHost();

    /**
     * @return true if it was attempted to connect to the database successfully or false otherwise.
     */
    public boolean isConnected();
} // end class IClient
