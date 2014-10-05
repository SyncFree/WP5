package com.syncfree.dbclient.controller.riak;

import com.aasco.util.Verbose;
import com.basho.riak.client.DefaultRiakObject;
import com.basho.riak.client.RiakRetryFailedException;
import com.basho.riak.client.cap.UnresolvedConflictException;
import com.basho.riak.client.convert.ConversionException;
import com.syncfree.dbclient.controller.DBException;
import com.syncfree.dbclient.data.IBucket;
import com.syncfree.dbclient.data.ITuple;

/**
 * Representation of a tuple.
 * 
 * @author aas
 * @version 0.0
 */
public class RiakTuple implements ITuple {
    /** The name of the tuple key. */
    private final String mstrKeyName;
    /** The bucket this tuple belongs to. */
    private final RiakBucket mParent;

    /**
     * Builds a tuple from the provided details.
     * 
     * @param bucket
     *            the bucket the new tuple will belong to.
     * @param strKey
     *            the tuple's key.
     * @param value
     *            the tuple's value.
     */
    public RiakTuple(final RiakBucket bucket, final String strKey) {
        this.mstrKeyName = strKey;
        this.mParent = bucket;
    } // Constructor ()

    @Override
    public Object getValue() throws DBException {
        try {
            final DefaultRiakObject value = (DefaultRiakObject) this.mParent.fetch(this.mstrKeyName);

            return value.getValueAsString();
        } catch (UnresolvedConflictException | RiakRetryFailedException
                | ConversionException e) {
            throw new DBException(e);
        } // end try
    } // getValue()

    @Override
    public String getKeyName() {
        return this.mstrKeyName;
    } // getName()

    /**
     * @return the bucket this tuple belong to.
     */
    public RiakBucket getBucket() {
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
        } catch (final DBException dbe) {
            Verbose.log(dbe,
                    "Failed to retrive value for tuple {0}.{1}.{2}; {3}", tuple
                            .getTupleParent().getBucketParentName(), tuple
                            .getTupleParentName(), tuple.getKeyName(), dbe
                            .getMessage());
        } // end try

        return null;
    } // get()
} // end class RiakTuple
