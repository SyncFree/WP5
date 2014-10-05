package com.syncfree.dbclient.controller.riak;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.aasco.util.ReadOnlyList;
import com.aasco.util.Verbose;
import com.basho.riak.client.IRiakObject;
import com.basho.riak.client.RiakException;
import com.basho.riak.client.RiakRetryFailedException;
import com.basho.riak.client.bucket.Bucket;
import com.basho.riak.client.cap.UnresolvedConflictException;
import com.basho.riak.client.convert.ConversionException;
import com.syncfree.dbclient.controller.DBException;
import com.syncfree.dbclient.controller.TupleAlreadyExistsException;
import com.syncfree.dbclient.data.IBucket;
import com.syncfree.dbclient.data.IClient;
import com.syncfree.dbclient.data.IList;
import com.syncfree.dbclient.data.ITuple;

/**
 * Representation of a Riak bucket.
 * 
 * @author aas
 * @version 0.0
 */
public class RiakBucket implements IBucket, IList<RiakTuple> {
    /** The list of tuples in the bucket. */
    private final List<RiakTuple> mTuples;
    /** The client this bucket belongs to. */
    private final RiakClient mParent;

    private Bucket mRealBucket;

    /**
     * Builds a bucket with the given details, i,e, name.
     * 
     * @param client
     *            the client that obtained the buckets.
     * @param bucket
     *            the details of the buckets.
     */
    public RiakBucket(final RiakClient client, final Bucket bucket) {
        this.mRealBucket = bucket;
        this.mTuples = new ArrayList<>();
        this.mParent = client;
    } // Constructor ()

    //
    /**
     * @return this bucket's n_val.
     */
    public String getNVal() {
        return String.valueOf(this.mRealBucket.getNVal());
    } // getNVal()
      //

    // Actions ==============================
    /**
     * Creates a tuple with the specified data.
     *
     * @param tuple
     *            the tuple's details.
     * @return the specified tuple if it does not already exists.
     * @throws TupleAlreadyExistsException
     *             when the tuple already exist.
     * @throws ConversionException
     *             when .
     * @throws UnresolvedConflictException
     *             when .
     * @throws RiakRetryFailedException
     *             when .
     */
    public RiakTuple createTuple(final ITuple tuple)
            throws TupleAlreadyExistsException, RiakRetryFailedException,
            UnresolvedConflictException, ConversionException {
        RiakTuple t = getTuple(tuple.getKeyName());

        if (t != null) {
            throw new TupleAlreadyExistsException(tuple.getKeyName());
        }

        Object value;

        try {
            value = tuple.getValue();
        } catch (DBException e) {
            value = null;
        } // end try
        this.mRealBucket.store(tuple.getKeyName(), value).execute();
        t = new RiakTuple(this, tuple.getKeyName());
        this.mTuples.add(t);

        return t;
    } // createTuple()

    /**
     * Deletes the specified tuple.
     * 
     * @param strKeyName
     *            the key's name.
     * @return the deleted tuple.
     * @throws DBException
     *             when fails to delete the tuple identified by the specified
     *             key.
     */
    public RiakTuple delete(final String strKeyName) throws DBException {
        try {
            if (this.mRealBucket == null) {
                this.mRealBucket = this.mParent.retrieveBucket(this.mRealBucket
                        .getName());
            }
            if (this.mRealBucket != null) {
                this.mRealBucket.delete(strKeyName).execute();
            } else {
                Verbose.debug(
                        "Note that tuple \"{0}\" is NOT connected to database",
                        strKeyName);
            }
        } catch (RiakException re) {
            throw new DBException(re);
        } // end try
        for (int i = 0; i < this.mTuples.size(); ++i) {
            final RiakTuple tuple = this.mTuples.get(i);

            if (tuple.getKeyName().equals(strKeyName)) {
                this.mTuples.remove(i);

                return tuple;
            }
        }

        return null;
    } // delete()

    /**
     * Acquires the tuples' key that already exist.
     * 
     * @return the tuples' key that already exist.
     * @throws DBException
     *             when fails to provide the tuples for this bucket.
     */
    public Iterator<RiakTuple> refresh() throws DBException {
        // TODO: must be done in a thread
        final ReadOnlyList<RiakTuple> list;

        try {
            if (this.mRealBucket == null) {
                this.mRealBucket = this.mParent.retrieveBucket(getBucketName());
            }
            this.mTuples.clear();
            for (String key : this.mRealBucket.keys()) {
                this.mTuples.add(new RiakTuple(this, key));
            }
        } catch (RiakException re) {
            throw new DBException(re);
        }
        list = new ReadOnlyList<>(this.mTuples);

        return list.iterator();
    } // refresh()
      // End actions ==========================

    /**
     * Provides the tuple with the specified name.
     * 
     * @param strKeyName
     *            the name of the tuple's key.
     * @return the tuple with the specified key's name, if exists. Otherwise
     *         null.
     */
    public RiakTuple getTuple(final String strKeyName) {
        for (RiakTuple tuple : this.mTuples) {
            if (tuple.getKeyName().equals(strKeyName)) {
                return tuple;
            }
        }

        return null;
    } // getTuple()

    /**
     * Retrieves the tuple from Riak.
     * 
     * @param strKey
     *            the tuple key.
     * @return the real Riak tuple.
     * @throws TupleAlreadyExistsException
     */
    protected RiakTuple fetchBucket(final String strKey)
            throws TupleAlreadyExistsException {
        try {
            final RiakTuple tuple;

            this.mRealBucket.fetch(strKey).execute(); // check that it exists
            tuple = new RiakTuple(this, strKey);
            this.mTuples.add(tuple);

            return tuple;
        } catch (final RiakRetryFailedException rrfe) {
            Verbose.log(
                    rrfe,
                    "Failed to retrive tuple \"{0}\" in bucket \"{1}\" using client \"{2}\"; {3}",
                    strKey, getBucketName(), this.getBucketParentName(),
                    rrfe.getMessage());
        } // end try

        return null;
    } // fetchBucket()

    /**
     * @return an iterator over the Riak keys in this list in proper sequence.
     */
    public Iterator<RiakTuple> getKeys() {
        return this.mTuples.iterator();
    } // getKeys()

    /**
     * @return the name of the key.
     */
    public String getName() {
        return this.mRealBucket.getName();
    } // getName()

    /**
     * @return the client that hold this bucket.
     */
    public RiakClient getClient() {
        return this.mParent;
    } // getClient()

    @Override
    public String getBucketName() {
        return this.mRealBucket.getName();
    } // getBucketName()

    @Override
    public IClient getBucketParent() {
        return this.mParent;
    } // getBucketParent()

    @Override
    public String getBucketParentName() {
        return this.mParent.getName();
    } // getBucketParentName()

    @Override
    public boolean add(final RiakTuple tuple) {
        if (tuple == null) {
            Verbose.warning("No tuple specified to be added");

            return false;
        }
        if (!this.mTuples.contains(tuple)) {
            return this.mTuples.add(tuple);
        }

        Verbose.warning(
                "Tuple with key \"{0}\" already exist for bucket \"{1}\" and client \"{2}\"",
                tuple.getKeyName(), tuple.getTupleParentName(), tuple
                        .getTupleParent().getBucketParentName());

        return false;
    } // add()

    @Override
    public String toString() {
        return getBucketName();
    } // toString()

    IRiakObject fetch(final String strKey) throws UnresolvedConflictException,
            RiakRetryFailedException, ConversionException {
        return this.mRealBucket.fetch(strKey).execute();
    } // fetch()
} // end class RiackBucket
