package com.syncfree.dbclient.controller.dummy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.aasco.util.ReadOnlyList;
import com.aasco.util.Verbose;
import com.syncfree.dbclient.controller.DBException;
import com.syncfree.dbclient.controller.TupleAlreadyExistsException;
import com.syncfree.dbclient.data.IBucket;
import com.syncfree.dbclient.data.IClient;
import com.syncfree.dbclient.data.IList;
import com.syncfree.dbclient.data.ITuple;


/**
 * Representation of a bucket in the Dummy.
 * 
 * @author aas
 * @version 0.0
 */
public class DummyBucket implements IBucket, IList<DummyTuple> {
    /** The list of tuples in the bucket. */
    private final List<DummyTuple> mTuples;
    /** The client this bucket belongs to. */
    private final DummyClient mParent;

    private final String mstrBucketName;

    /**
     * Builds a bucket with the given details, i,e, name.
     * 
     * @param client
     *            the client that obtained the bucket.
     * @param strBucketName
     *            the name of the bucket.
     */
    public DummyBucket(final DummyClient client, final String strBucketName) {
        this.mstrBucketName = strBucketName;
        this.mTuples = new ArrayList<>();
        this.mParent = client;
    } // Constructor ()

    // Actions ==============================
    /**
     * Deletes the specified tuple.
     * 
     * @return the tuple's details deleted.
     * @param strKeyName
     *            the key's name.
     * @throws DBException
     *             when fails to delete the specified tuple.
     */
    public DummyTuple delete(final String strKeyName) throws DBException {
        for (int i = 0; i < this.mTuples.size(); ++i) {
            final DummyTuple tuple = this.mTuples.get(i);

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
     *             when fails to obtain all the tuple's details.
     */
    public Iterator<DummyTuple> refresh() throws DBException {
        final ReadOnlyList<DummyTuple> list = new ReadOnlyList<>(this.mTuples);

        return list.iterator();
    } // refresh()
        // End actions ==========================

    /**
     * Creates a tuple with the specified data.
     *
     * @param tuple
     *            the tuple's details.
     * @return the specified tuple if it does not already exists.
     * @throws TupleAlreadyExistsException
     *             when the tuple already exist.
     */
    public DummyTuple buildTuple(final ITuple tuple)
            throws TupleAlreadyExistsException {
        DummyTuple t = getTuple(tuple.getKeyName());

        if (t != null) {
            throw new TupleAlreadyExistsException(tuple.getKeyName());
        }

        t = new DummyTuple(this, tuple);
        this.mTuples.add(t);

        return t;
    } // buildBucket()

    /**
     * Provides the tuple with the specified name.
     * 
     * @param strKeyName
     *            the name of the tuple's key.
     * @return the tuple with the specified key's name, if exists. Otherwise
     *         null.
     */
    public DummyTuple getTuple(final String strKeyName) {
        for (DummyTuple tuple : this.mTuples) {
            if (tuple.getKeyName().equals(strKeyName)) {
                return tuple;
            }
        }

        return null;
    } // getTuple()

    /**
     * @return an iterator over the Riak keys in this list in proper sequence.
     */
    public Iterator<DummyTuple> getKeys() {
        return this.mTuples.iterator();
    } // getKeys()

    /**
     * @return the name of the key.
     */
    public String getName() {
        return this.mstrBucketName;
    } // getName()

    /**
     * @return the client that hold this bucket.
     */
    public DummyClient getClient() {
        return this.mParent;
    } // getClient()

    @Override
    public String getBucketName() {
        return this.mstrBucketName;
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
    public boolean add(final DummyTuple tuple) {
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
        return this.mstrBucketName;
    } // toString()
} // end class DummyBucket
