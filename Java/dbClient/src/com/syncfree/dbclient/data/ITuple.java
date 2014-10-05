package com.syncfree.dbclient.data;

import com.syncfree.dbclient.controller.DBException;

/**
 * API to access to the elements of a DB key.
 * 
 * @author aas
 * @version 0.0
 */
public interface ITuple {
    /**
     * @return the name of the key.
     */
    public String getKeyName();

    /**
     * @return the bucket the tuple belongs to.
     */
    public IBucket getTupleParent();

    /**
     * @return the typle's parent name.
     */
    public String getTupleParentName();

    /**
     * @return the value from the tuple.
     * @throws DBException when failed to get data for tuple.
     */
    public Object getValue() throws DBException;
} // end interface IKey
