package com.syncfree.dbclient.controller;


/**
 * Exception thrown when a passed arguments is invalid.
 * 
 * @author aas
 * @version 0.0
 */
public class InvalidArgumentException extends Exception {
    /**
     * A version number for this class so that serialisation can occur
     * without worrying about the underlying class changing between
     * serialisation and deserialisation.<p>
     *
     * Not that we ever serialise this class of course, but Exception implements
     * Serializable, so therefore by default we do as well.
     */
    private static final long serialVersionUID = -1885256255161365472L;


    /**
     * Builds a new exception with the specified detail message. The cause is not initialised, and 
     * may subsequently be initialised by a call to initCause.
     * 
     * @param strMsg the message.
     */
    public InvalidArgumentException(final String strMsg) {
        super(strMsg);
    } // Constructor ()
} // end class InvalidArgumentException
