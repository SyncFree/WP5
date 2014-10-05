package com.syncfree.dbclient.controller;

import com.syncfree.dbclient.data.ITuple;


/**
 * Exception thrown when trying to build a tuple with the same key which already exists.
 * 
 * @author aas
 * @version 0.0
 */
public class TupleAlreadyExistsException extends Exception {
    /**
     * A version number for this class so that serialisation can occur
     * without worrying about the underlying class changing between
     * serialisation and deserialisation.<p>
     *
     * Not that we ever serialise this class of course, but Exception implements
     * Serializable, so therefore by default we do as well.
     */
    private static final long serialVersionUID = -7156581293052289302L;


    /**
     * Builds the exception for the specified key.
     * 
     * @param strKey the tuple's key.
     */
    public TupleAlreadyExistsException(final String strKey) {
        super("The tuple with key '" + strKey + "' already exists.");
    } // Construct ()

    /**
     * Builds the exception for the specified tuple.
     * 
     * @param tuple the tuple.
     */
    public TupleAlreadyExistsException(final ITuple tuple) {
        this(tuple.getKeyName());
    } // Construct ()
} // end class TupleAlreadyExistsException
