package com.syncfree.dbclient.data;


/**
 * API to access to the elements of a DB bucket.
 *
 * @author aas
 * @version 0.0
 */
public interface IBucket {
    /**
     * @return the name of the bucket.
     */
    public String getBucketName();

    /**
     * @return the client that obtained the bucket.
     */
    public IClient getBucketParent();

    /**
     * @return the client's name.
     */
    public String getBucketParentName();
} // end interface IBucket
