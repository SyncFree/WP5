package com.syncfree.dbclient.controller.riak;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import com.aasco.util.ReadOnlyList;
import com.aasco.util.Verbose;
import com.basho.riak.client.IRiakClient;
import com.basho.riak.client.RiakException;
import com.basho.riak.client.RiakFactory;
//import com.basho.riak.client.RiakFactory;
import com.basho.riak.client.RiakRetryFailedException;
import com.basho.riak.client.bucket.Bucket;
import com.basho.riak.client.cap.UnresolvedConflictException;
import com.basho.riak.client.convert.ConversionException;
import com.basho.riak.client.query.NodeStats;
import com.basho.riak.client.query.StreamingOperation;
import com.syncfree.dbclient.controller.BucketDoesNotExistException;
import com.syncfree.dbclient.controller.DBException;
import com.syncfree.dbclient.controller.InvalidArgumentException;
import com.syncfree.dbclient.controller.TupleAlreadyExistsException;
import com.syncfree.dbclient.data.IBucket;
import com.syncfree.dbclient.data.IClient;
import com.syncfree.dbclient.data.IList;
import com.syncfree.dbclient.data.ITuple;

/**
 * Representation of a Riak client.
 *
 * @author aas
 * @version 0.0
 */
public class RiakClient implements IClient, IList<RiakBucket> {
    /** The name of the client. */
    private final String mstrName;
    /** The list of buckets. */
    private final List<RiakBucket> mBuckets;
    /** The controller. */
    private RiakController mParent;
    // Connection
    private final TYPE mType;
    private final String mstrHost;
    private final String mstrURL;
    private final int miPort;

    /** The client connection to Riak. */
    private IRiakClient mRealClient;

    /**
     * Builds a client with the provided details.
     * 
     * @param client
     *            the details of the client, i.e. name and connection details.
     * @throws RiakException
     *             from Riak.
     * @throws InvalidArgumentException
     *             when the connection type is invalid.
     */
    public RiakClient(final IClient client) throws RiakException,
            InvalidArgumentException {
        this.mstrName = client.getClientName();
        this.mType = client.getClientType();
        this.mstrHost = client.getHost();
        this.mstrURL = client.getURL();
        this.miPort = client.getPort();
        this.mBuckets = new ArrayList<>();

        this.mRealClient = null;
        // if (this.mType == TYPE.HTTP) {
        // this.mRealClient = RiakFactory.httpClient(this.mstrURL);
        // } else if (this.mType == TYPE.PBC) {
        // this.mRealClient = RiakFactory
        // .pbcClient(this.mstrHost, this.miPort);
        // } else {
        // throw new InvalidArgumentException("Invalid connection type '"
        // + this.mType.name() + "'; unsupported");
        // }
    } // Constructor ()

    //
    public String ping() {
        String strResponse;

        try {
            this.mRealClient.ping();
            strResponse = "Pong";
        } catch (final RiakException re) {
            Verbose.log(Level.SEVERE, re,
                    "Failed to complete the \"PING\" request; {0}",
                    re.getMessage());
            strResponse = null;
        } // end try

        return strResponse;
    } // ping()

    private boolean isValidDataType(final Class<?> clazz) {
        final Class<?>[] aClasses = { BigInteger.class, String.class,
                Boolean.class, String[].class };

        for (final Class<?> c : aClasses) {
            if (c == clazz) {
                return true;
            }
        } // end for

        return false;
    } // isValidDataType()

    /**
     * Performs the Riak statistics operation on the node(s) this client is
     * connected to.
     * 
     * @return the statistics per node, or null if an error.
     */
    public String stats() {
        final StringBuilder buf = new StringBuilder();

        try {
            for (final NodeStats stats : this.mRealClient.stats()) {
                if (buf.length() > 0) {
                    buf.append('\n');
                }

                final Method[] aMethods = NodeStats.class.getMethods();
                String[] astrParts;

                // Order the array of methods
                Arrays.sort(aMethods, new Comparator<Method>() {
                    @Override
                    public int compare(final Method m1, final Method m2) {
                        return m1.getName().compareTo(m2.getName());
                    } // compare()
                });

                // Add return value from appropriate methods to the buffer
                for (final Method method : aMethods) {
                    if (method.getParameterTypes().length > 0
                            || !isValidDataType(method.getReturnType())) {
                        continue;
                    }

                    // Break method name into its words
                    astrParts = method.getName().split("_");
                    if (astrParts.length <= 1) {
                        astrParts = method.getName().split("(?=\\p{Lu})"); // Split
                                                                           // by
                                                                           // upper
                                                                           // case
                    }

                    // Save the name of the field into the buffer
                    for (final String strValue : astrParts) {
                        buf.append(strValue.toLowerCase());
                        buf.append(' ');
                    } // end for

                    // Save the value of the field into the buffer
                    buf.append("= ");
                    try {
                        final Object v = method.invoke(stats);

                        if (v == null) {
                            buf.append("<null>\n");
                        } else if (v.getClass() == String[].class) {
                            final String[] astrV = (String[]) v;
                            final int len = buf.length();

                            for (final String s : astrV) {
                                if (len > buf.length()) {
                                    buf.append('\t');
                                }
                                buf.append(s);
                                buf.append('\n');
                            } // end for
                        } else {
                            buf.append(v);
                            buf.append('\n');
                        }
                    } catch (final Exception e) {
                        Verbose.warning(
                                "Failed when calling method \"{0}\"; {1}",
                                method.getName(), e.getMessage());

                        buf.append('\n');
                    } // end try
                } // end for
            } // end for

            return buf.toString();
        } catch (final RiakException re) {
            Verbose.log(Level.SEVERE,
                    "Failed to get the full stats using client \"{0}\"; {1}",
                    getClientName(), re);
        } // end try

        return null;
    } // stats()

    public void shutdown() {
        this.mRealClient.shutdown();
        this.mRealClient = null;
    } // shutdown()
      //

    @Override
    public boolean isConnected() {
        return (this.mRealClient != null);
    } // isConnected()

    /**
     * Establishes the connection with the underlying database.
     * 
     * @throws RiakException
     *             when failed to connect.
     */
    public void connect() throws RiakException {
        if (this.mType == TYPE.HTTP) {
            this.mRealClient = RiakFactory.httpClient(this.mstrURL);
        } else {
            this.mRealClient = RiakFactory
                    .pbcClient(this.mstrHost, this.miPort);
        }
    } // connect()

    /**
     * Closes the the connection used by this client.
     */
    public void close() {
        if (isConnected()) {
            this.mRealClient.shutdown();
        } else {
            Verbose.debug(
                    "Note that client \"{0}\" is NOT connected to a database",
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
    public RiakBucket getBucket(final String strName) {
        for (RiakBucket bucket : this.mBuckets) {
            if (bucket.getName().equals(strName)) {
                return bucket;
            }
        } // end for

        return fetchBucket(strName);
    } // getBucket()

    /**
     * Provides the buckets already loaded, or if none was loaded then calls
     * <code>refresh</code>.
     * 
     * @return an iterator over the Riak bucket in this list in proper sequence.
     */
    public Iterator<RiakBucket> getBuckets() {
        return this.mBuckets.iterator();
    } // getBuckets()

    // Actions ===============================================
    /**
     * Creates a bucket with the specified data.
     *
     * @param strBucketName
     *            the name of the bucket.
     * @return the specified bucket if it does not already exists.
     * @throws RiakRetryFailedException
     *             when failed to create the specified bucket.
     */
    public RiakBucket createBucket(final String strBucketName)
            throws RiakRetryFailedException {
        final RiakBucket bucket;
        final Bucket b;

        b = this.mRealClient.createBucket(strBucketName).execute();
        bucket = new RiakBucket(this, b);
        this.mBuckets.add(bucket);

        return bucket;
    } // createBucket()

    /**
     * Acquires the buckets that already exist.
     * 
     * @return the buckets that already exist.
     * @throws DBException
     *             when fails to provide all the buckets.
     */
    public Iterator<RiakBucket> refresh() throws DBException {
        final ReadOnlyList<RiakBucket> list;

        this.mBuckets.clear();
        try {
            final StreamingOperation<String> iter = this.mRealClient
                    .listBucketsStreaming();

            for (final String strBucketName : iter) {
                fetchBucket(strBucketName);
            } // end for
        } catch (final Exception re) {
            throw new DBException(re);
        } // end try
        list = new ReadOnlyList<>(this.mBuckets);

        return list.iterator();
    } // refresh()
      // End actions ===========================================

    Bucket retrieveBucket(final String strBucketName)
            throws RiakRetryFailedException {
        if (isConnected()) {
            return this.mRealClient.fetchBucket(strBucketName).execute();
        }

        return null;
    } // retrieveBucket()

    /**
     * Creates a tuple from the provided details.
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
     * @throws ConversionException
     *             when .
     * @throws UnresolvedConflictException
     *             when .
     * @throws RiakRetryFailedException
     *             when .
     */
    public RiakTuple createTuple(final ITuple tuple)
            throws BucketDoesNotExistException, TupleAlreadyExistsException,
            InvalidArgumentException, RiakRetryFailedException,
            UnresolvedConflictException, ConversionException {
        final IBucket bucket = tuple.getTupleParent();
        final RiakBucket b = getBucket(bucket.getBucketName());

        if (b == null) {
            throw new BucketDoesNotExistException(tuple);
        }

        if (!bucket.getBucketParent().getClientName()
                .equals(b.getClient().getName())) {
            throw new InvalidArgumentException("Different buckets");
        }

        return b.createTuple(tuple);
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
    public RiakController getController() {
        return this.mParent;
    } // getController()

    /**
     * Removes the specified bucket.
     *
     * @param strBucketName
     *            the name of the bucket.
     * @return the removed bucket.
     */
    public RiakBucket removeBucket(final String strBucketName) {
        for (int i = 0; i < this.mBuckets.size(); ++i) {
            final RiakBucket bucket = this.mBuckets.get(i);

            if (bucket.getName().equals(strBucketName)) {
                return this.mBuckets.remove(i);
            }
        } // end for

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
    public boolean add(final RiakBucket bucket) {
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

    /**
     * Retrieves the bucket from Riak.
     * 
     * @param strName
     *            the name of the bucket.
     * @return the real Riak bucket.
     */
    protected RiakBucket fetchBucket(final String strName) {
        try {
            final Bucket bucket = this.mRealClient.fetchBucket(strName)
                    .execute();
            final RiakBucket b = new RiakBucket(this, bucket);

            this.mBuckets.add(b);

            return b;
        } catch (final RiakRetryFailedException rrfe) {
            Verbose.log(
                    rrfe,
                    "Failed to retrive bucket \"{0}\" using client \"{1}\"; {2}",
                    strName, getClientName(), rrfe.getMessage());
        } // end try

        return null;
    } // fetchBucket()
} // end class RiakClient
