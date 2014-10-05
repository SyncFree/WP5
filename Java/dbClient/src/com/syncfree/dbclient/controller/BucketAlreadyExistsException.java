package com.syncfree.dbclient.controller;

import com.syncfree.dbclient.data.IBucket;


/**
 * Exception thrown when trying to build a bucket with the same name which already exists.
 * 
 * @author aas
 * @version 0.0
 */
public class BucketAlreadyExistsException extends Exception {
    /**
     * A version number for this class so that serialisation can occur
     * without worrying about the underlying class changing between
     * serialisation and deserialisation.<p>
     *
     * Not that we ever serialise this class of course, but Exception implements
     * Serializable, so therefore by default we do as well.
     */
    private static final long serialVersionUID = -7156581293052289302L;


    public BucketAlreadyExistsException(final String strName) {
        super("The bucket with name '" + strName + "' already exists.");
    } // Construct ()

    public BucketAlreadyExistsException(final IBucket bucket) {
        this(bucket.getBucketName());
    } // Construct ()
} // end class BucketAlreadyExists
