package com.syncfree.dbclient.controller;

import com.syncfree.dbclient.data.ITuple;


/**
 * Exception thrown when trying to build a tuple without a valid bucket.
 * 
 * @author aas
 * @version 0.0
 */
public class BucketDoesNotExistException extends Exception {
    /**
     * A version number for this class so that serialisation can occur
     * without worrying about the underlying class changing between
     * serialisation and deserialisation.<p>
     *
     * Not that we ever serialise this class of course, but Exception implements
     * Serializable, so therefore by default we do as well.
     */
    private static final long serialVersionUID = -7156581293052289302L;


    public BucketDoesNotExistException(final String strName) {
        super("The bucket with name '" + strName + "' does not exists.");
    } // Construct ()

    public BucketDoesNotExistException(final ITuple tuple) {
        this(tuple.getTupleParent().getBucketName());
    } // Construct ()
} // end class BucketDoesNotExistException