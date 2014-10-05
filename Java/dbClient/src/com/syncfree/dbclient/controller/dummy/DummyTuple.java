package com.syncfree.dbclient.controller.dummy;

import com.aasco.util.Verbose;
import com.syncfree.dbclient.controller.DBException;
import com.syncfree.dbclient.data.IBucket;
import com.syncfree.dbclient.data.ITuple;


/**
 * Representation of a tuple in the Dummy DB.
 * 
 * @author aas
 * @version 0.0
 */
public class DummyTuple implements ITuple {
    /** The name of the tuple key. */
    private final String mstrKeyName;
    /** The value in the tuple. */
    private String mstrValue;
    /** The bucket this tuple belongs to. */
    private final DummyBucket mParent;


    /**
     * Builds a tuple from the provided details.
     * 
     * @param bucket the bucket the new tuple will belong to.
     * @param tuple the details of the tuple.
     */
    public DummyTuple(final DummyBucket bucket, final ITuple tuple) {
        this(bucket, tuple.getKeyName(), get(tuple).toString());
    } // Constructor ()

    /**
     * Builds a tuple from the provided details.
     * 
     * @param bucket the bucket the new tuple will belong to.
     * @param strKey the tuple's key.
     * @param strValue the tuple's value.
     */
    public DummyTuple(final DummyBucket bucket, final String strKey, final String strValue) {
        this.mstrKeyName = strKey;
        this.mParent = bucket;
        this.mstrValue = strValue;
    } // Constructor ()
 
    public String getValue() {
        return this.mstrValue;
    } // getValue()
 
    public void setValue(final String strValue) {
        this.mstrValue = strValue;
    } // setValue()

    /**
     * @return the name of the key.
     */
    public String getKeyName() {
        return this.mstrKeyName;
    } // getName()

    /**
     * @return the bucket this tuple belong to.
     */
    public DummyBucket getBucket() {
        return this.mParent;
    } // getBucket()

    @Override
    public IBucket getTupleParent() {
        return this.mParent;
    } // getTupleParent()

    @Override
    public String getTupleParentName() {
        return this.mParent.getName();
    } // getTupleParentName()

    @Override
    public String toString() {
        return this.mstrKeyName;
    } // toString()

    protected static Object get(final ITuple tuple) {
        try {
            return tuple.getValue();
        } catch (DBException dbe) {
            Verbose.log(dbe,
                    "Failed to retrive value for tuple {0}.{1}.{2}; {3}",
                    tuple.getTupleParent().getBucketParentName(), 
                    tuple.getTupleParentName(), tuple.getKeyName(), 
                    dbe.getMessage());
        } // end try

        return null;
    } // get()
} // end class DummyTuple
