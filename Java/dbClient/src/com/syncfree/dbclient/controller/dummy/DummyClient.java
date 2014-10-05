package com.syncfree.dbclient.controller.dummy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.aasco.util.Verbose;
import com.syncfree.dbclient.controller.BucketAlreadyExistsException;
import com.syncfree.dbclient.controller.BucketDoesNotExistException;
import com.syncfree.dbclient.controller.DBException;
import com.syncfree.dbclient.controller.InvalidArgumentException;
import com.syncfree.dbclient.controller.TupleAlreadyExistsException;
import com.syncfree.dbclient.data.IBucket;
import com.syncfree.dbclient.data.IClient;
import com.syncfree.dbclient.data.IList;
import com.syncfree.dbclient.data.ITuple;

/**
 * .
 * 
 * @author aas
 * @version 0.0
 */
public class DummyClient implements IClient, IList<DummyBucket> {
    /** The name of the client. */
    private final String mstrName;
    /** The list of buckets. */
    private final List<DummyBucket> mBuckets;
    /** The controller. */
    private DummyController mParent;
    // Connection
    private final TYPE mType;
    private final String mstrHost;
    private final String mstrURL;
    private final int miPort;

    private boolean mbConnected;

    /**
     * Builds a client with the provided details.
     * 
     * @param client
     *            the details of the client, i.e. name and connection details.
     */
    public DummyClient(final IClient client) {
        this.mstrName = client.getClientName();
        this.mType = client.getClientType();
        this.mstrHost = client.getHost();
        this.mstrURL = client.getURL();
        this.miPort = client.getPort();
        this.mBuckets = new ArrayList<>();
        this.mbConnected = false;
    } // Constructor ()

    @Override
    public boolean isConnected() {
        return this.mbConnected;
    } // isConnected()

    /**
     * Establishes the connection with the underlying database.
     */
    public void connect() {
        this.mbConnected = true;
    } // connect()

    /**
     * Disconnect client from underlying database.
     */
    public void shutdown() {
        this.mbConnected = false;
    } // shutdown()

    /**
     * Closes the the connection used by this client.
     */
    public void close() {
        if (!isConnected()) {
            Verbose.info(
                    "Unable to disconnect \"{0}\" from database because \"{0}\" is NOT connected to the database",
                    getClientName());
        }
    } // close()

    /**
     * Provides the bucket with the specified name.
     * 
     * @param strName
     *            the name of the bucket.
     * @return the bucket with the specified name, if exists. Otherwise null.
     */
    public DummyBucket getBucket(final String strName) {
        for (DummyBucket bucket : this.mBuckets) {
            if (bucket.getName().equals(strName)) {
                return bucket;
            }
        } // end for

        return null;
    } // getBucket()

    /**
     * Provides the buckets already loaded, or if none was loaded then calls
     * <code>refresh</code>.
     * 
     * @return an iterator over the Riak bucket in this list in proper sequence.
     */
    public Iterator<DummyBucket> getBuckets() {
        return this.mBuckets.iterator();
    } // getBuckets()

    // Actions ===============================================
    public DummyBucket createBucket(final String strBucketName) {
        final DummyBucket bucket = new DummyBucket(this, strBucketName);

        this.mBuckets.add(bucket);

        return bucket;
    } // createBucket()

    /**
     * Acquires the buckets that already exist.
     * 
     * @return the buckets that already exist.
     * @throws DBException
     *             when fails to obtain all the bucket's details.
     */
    public Iterator<DummyBucket> refresh() throws DBException {
        return this.mBuckets.iterator();
    } // refresh()
      // End actions ===========================================

    void setConnected(final boolean bConnected) {
        this.mbConnected = bConnected;
    } // setConnected()

    DummyBucket retrieveBucket(final String strBucketName) {
        if (isConnected()) {
            return getBucket(strBucketName);
        }

        return null;
    } // retrieveBucket()

    /**
     * Creates a bucket with the specified data.
     *
     * @param bucket
     *            the bucket details.
     * @return the specified bucket if it does not already exists.
     * @throws BucketAlreadyExistsException
     *             when the bucket already exist.
     */
    public DummyBucket buildBucket(final IBucket bucket)
            throws BucketAlreadyExistsException {
        DummyBucket b = getBucket(bucket.getBucketName());

        if (b != null) {
            throw new BucketAlreadyExistsException(bucket.getBucketName());
        }

        b = new DummyBucket(this, bucket.getBucketName());
        this.mBuckets.add(b);

        return b;
    } // buildBucket()

    /**
     * Builds a tuple from the provided details.
     * 
     * @param tuple
     *            the details for the tuple to build.
     * @return the tuple that was created.
     * @throws BucketDoesNotExistException
     *             when the required bucket does not exist.
     * @throws TupleAlreadyExistsException
     *             when the tuple already exists.
     * @throws InvalidArgumentException
     *             when any of the details is invalid.
     */
    public DummyTuple buildTuple(final ITuple tuple)
            throws BucketDoesNotExistException, TupleAlreadyExistsException,
            InvalidArgumentException {
        final IBucket bucket = tuple.getTupleParent();
        final DummyBucket b = getBucket(bucket.getBucketName());

        if (b == null) {
            throw new BucketDoesNotExistException(tuple);
        }

        if (!bucket.getBucketParent().getClientName()
                .equals(b.getClient().getName())) {
            throw new InvalidArgumentException("Different buckets");
        }

        return b.buildTuple(tuple);
    } // buildBucket()

    /**
     * @return the name of the client.
     */
    public String getName() {
        return this.mstrName;
    } // getName()

    /**
     * @return the data controller.
     */
    public DummyController getController() {
        return this.mParent;
    } // getController()

    /**
     * Removes the specified bucket.
     *
     * @param strBucketName
     *            the name of the bucket.
     * @return the removed bucket.
     */
    public DummyBucket removeBucket(final String strBucketName) {
        for (int i = 0; i < this.mBuckets.size(); ++i) {
            final DummyBucket bucket = this.mBuckets.get(i);

            if (bucket.getName().equals(strBucketName)) {
                return this.mBuckets.remove(i);
            }
        }

        return null;
    } // removeBucket()

    @Override
    public String getClientName() {
        return this.mstrName;
    } // getClientName()

    @Override
    public TYPE getClientType() {
        return this.mType;
    } // getClientType()

    @Override
    public String getURL() {
        return this.mstrURL;
    } // getURL()

    @Override
    public int getPort() {
        return this.miPort;
    } // getPort()

    @Override
    public String getHost() {
        return this.mstrHost;
    } // getHost()

    @Override
    public boolean add(final DummyBucket bucket) {
        if (bucket == null) {
            Verbose.warning("No bucket specified to be added");

            return false;
        }
        if (!this.mBuckets.contains(bucket)) {
            return this.mBuckets.add(bucket);
        }

        Verbose.warning(
                "Bucket \"{0}\" already exists for client \"{1}\"; not allowed to add duplicate",
                bucket.getBucketName(), bucket.getBucketParentName());

        return false;
    } // add()

    @Override
    public String toString() {
        return this.mstrName;
    } // toString()
} // end class DummyClient
