package com.syncfree.dbclient.controller;


/**
 * Wrapper for specified exception from the underlying database.
 *
 * @author aas
 * @version 0.0
 */
public class DBException extends Exception {
    /**
     * A version number for this class so that serialisation can occur
     * without worrying about the underlying class changing between
     * serialisation and deserialisation.<p>
     *
     * Not that we ever serialise this class of course, but Exception implements
     * Serializable, so therefore by default we do as well.
     */
    private static final long serialVersionUID = 8869102699100129983L;


    public DBException(final String strMessage, final Throwable excp) {
        super(strMessage, excp);
    } // Constructor ()

    public DBException(final Throwable excp) {
        this("Exception in the underlying database", excp);
    } // Constructor ()
} // end class DBException
