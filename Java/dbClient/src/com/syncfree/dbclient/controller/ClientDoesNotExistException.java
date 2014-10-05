package com.syncfree.dbclient.controller;

import com.syncfree.dbclient.data.IClient;


/**
 * Exception thrown when a required client does not exist.
 * 
 * @author aas
 * @version 0.0
 */
public class ClientDoesNotExistException extends Exception {
    /**
     * A version number for this class so that serialisation can occur
     * without worrying about the underlying class changing between
     * serialisation and deserialisation.<p>
     *
     * Not that we ever serialise this class of course, but Exception implements
     * Serializable, so therefore by default we do as well.
     */
    private static final long serialVersionUID = -7156581293052289302L;


    public ClientDoesNotExistException(final String strName) {
        super("The client with name '" + strName + "' already exists.");
    } // Construct ()

    public ClientDoesNotExistException(final IClient client) {
        this(client.getClientName());
    } // Construct ()
} // end class ClientDoesNotExistException
