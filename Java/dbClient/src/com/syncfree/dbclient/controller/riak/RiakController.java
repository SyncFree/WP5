package com.syncfree.dbclient.controller.riak;

import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.tree.TreePath;

import com.aasco.util.PropertyDef;
import com.aasco.util.Verbose;
import com.basho.riak.client.RiakException;
import com.basho.riak.client.RiakRetryFailedException;
import com.syncfree.dbclient.controller.BucketAlreadyExistsException;
import com.syncfree.dbclient.controller.BucketDoesNotExistException;
import com.syncfree.dbclient.controller.ClientAlreadyExistsException;
import com.syncfree.dbclient.controller.ClientDoesNotExistException;
import com.syncfree.dbclient.controller.DBException;
import com.syncfree.dbclient.controller.IController;
import com.syncfree.dbclient.controller.IIterator;
import com.syncfree.dbclient.controller.InvalidArgumentException;
import com.syncfree.dbclient.controller.TupleAlreadyExistsException;
import com.syncfree.dbclient.controller.gui.IExtrListener;
import com.syncfree.dbclient.controller.gui.IView;
import com.syncfree.dbclient.controller.riak.gui.ResponseView;
import com.syncfree.dbclient.data.IBucket;
import com.syncfree.dbclient.data.IClient;
import com.syncfree.dbclient.data.ITuple;
import com.syncfree.dbclient.data.IClient.TYPE;

/**
 * Handles all interactions between the GUI layer and the data layer.
 * <p>
 * Support to access the buckets on Riak.
 * 
 * @author aas
 * @version 0.0
 */
public class RiakController implements IController, IExtrListener {
    // Actions
    private static final String ACTION_CLIENT_PING = "Ping"; // ping()
    private static final String ACTION_CLIENT_STATS = "Statistics"; // stats()
    private static final String ACTION_CLIENT_CONFIG = "Configuration"; // getConfig()

    private static final String ACTION_BUCKET_NVAL = "NVal"; // getNVal()

    // Actions for a client
    public static final String[] ACTIONS_CLIENT = { ACTION_CLIENT_PING,
            ACTION_CLIENT_STATS
    // , ACTION_CLIENT_CONFIG
    };
    public static final String[] ACTIONS_CLIENT_TXT = {
            PropertyDef.getPropertyDef(
                    "com.syncfree.dbclient.controller.riak.action_ping",
                    ACTION_CLIENT_PING).getProperty(),
            PropertyDef.getPropertyDef(
                    "com.syncfree.dbclient.controller.riak.action_statistics",
                    ACTION_CLIENT_STATS).getProperty()
    // , PropertyDef.getPropertyDef(
    // "com.syncfree.dbclient.controller.riak.action_config",
    // ACTION_CLIENT_CONFIG).getProperty()
    };
    // Actions for a bucket
    public static final String[] ACTIONS_BUCKET = { ACTION_BUCKET_NVAL };
    public static final String[] ACTIONS_BUCKET_TXT = { PropertyDef
            .getPropertyDef(
                    "com.syncfree.dbclient.controller.riak.action_bucket_nval",
                    ACTION_BUCKET_NVAL).getProperty() };

    // Titles
    // Titles for a client
    public static final String TITLE_PING = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.controller.riak.gui.title_client_ping",
            ACTION_CLIENT_PING).getProperty();
    public static final String TITLE_CLIENT_STATS = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.controller.riak.gui.title_client_stats",
            ACTION_CLIENT_STATS).getProperty();
    public static final String TITLE_CLIENT_CONFIG = PropertyDef
            .getPropertyDef(
                    "com.syncfree.dbclient.controller.riak.gui.title_client_config",
                    ACTION_CLIENT_CONFIG).getProperty();
    // Title for a bucket
    public static final String TITLE_BUCKET_NVAL = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.controller.riak.gui.title_bucket_nval",
            ACTION_BUCKET_NVAL).getProperty();

    /** The main view access. */
    private IView mView;

    /** List of all clients. */
    private final List<RiakClient> mClients;
    /** The response view. */
    private ResponseView mResponseView;

    /**
     * Builds the controller to access Riak.
     * 
     * @param astrArgs
     *            the command line arguments.
     */
    public RiakController(final String[] astrArgs) {
        this.mClients = new ArrayList<>();
    } // Constructor ()

    @Override
    public String getID() {
        return "Riak";
    } // getID()

    /**
     * Reads from underlying database the buckets and return them.
     * 
     * @param strClientName
     *            the client's name.
     * @return the buckets from the specified client or null if the client does
     *         not exist.
     * @throws DBException
     *             when failed to access the buckets from the database.
     */
    @Override
    public Iterator<IBucket> refreshClient(final String strClientName)
            throws DBException {
        final RiakClient client = getClient_(strClientName);
        Iterator<IBucket> iter = null;

        if (client != null) {
            iter = new IIterator<>(client.refresh());
        }

        return iter;
    } // refreshClient()

    /**
     * Reads from underlying database the tuples in the specified bucket and
     * return them.
     * 
     * @param bucket
     *            the bucket's details.
     * @return the tuples from the specified bucket or null if the bucket or
     *         client do not exist.
     * @throws DBException
     *             when failed to access the buckets from the database.
     */
    @Override
    public Iterator<ITuple> refreshBucket(final IBucket bucket)
            throws DBException {
        final RiakClient client = getClient_(bucket.getBucketParentName());
        Iterator<ITuple> iter = null;

        if (client != null) {
            final RiakBucket b = client.getBucket(bucket.getBucketName());

            if (b != null) {
                iter = new IIterator<>(b.refresh());
            }
        }

        return iter;
    } // refreshClient()

    /**
     * Provides the client with the specified name.
     * 
     * @param strClientName
     *            the name of the client.
     * @return the client with the specified name, if exists. Otherwise null.
     */
    public final IClient getClient(final String strClientName) {
        return getClient_(strClientName);
    } // getClient()

    /**
     * Provides the bucket with the specified name for the specified client.
     * 
     * @param bucket
     *            the bucket basic data.
     * @return the bucket with the specified name, if exists. Otherwise null.
     */
    public final IBucket getBucket(final IBucket bucket) {
        return getBucket_(bucket);
    } // getBucket()

    /**
     * Provides the tuple with the specified name for the specified bucket and
     * client.
     * 
     * @param tuple
     *            the tuple basic data.
     * @return the tuple with the specified name, if exists. Otherwise null.
     */
    @Override
    public final ITuple getTuple(final ITuple tuple) {
        return getTuple_(tuple);
    } // getTuple()

    /**
     * @return the list of clients available.
     */
    @Override
    public Iterator<IClient> getClients() {
        return new IIterator<>(this.mClients.iterator());
    } // getClients()

    /**
     * Creates a client with the specified name.
     *
     * @param client
     *            the client's details.
     * @return the specified client if it does not already exists.
     * @throws ClientAlreadyExistsException
     *             when the client already exists.
     * @throws InvalidArgumentException
     *             when provided an unsupported connection type.
     * @throws DBException
     *             from Riak when failed to connect.
     */
    @Override
    public IClient createClient(final IClient client)
            throws ClientAlreadyExistsException, DBException,
            InvalidArgumentException {
        RiakClient c = getClient_(client.getClientName());

        if (c != null) {
            throw new ClientAlreadyExistsException(client.getClientName());
        }

        try {
            c = new RiakClient(client);
        } catch (RiakException re) {
            throw new DBException(re);
        }
        this.mClients.add(c);

        return c;
    } // createClient()

    /**
     * Builds a bucket with the specified details.
     * 
     * @param bucket
     *            the details of the bucket to build.
     * @return the bucket that was created.
     * @throws ClientDoesNotExistException
     *             when the required client does not exist.
     * @throws BucketAlreadyExistsException
     *             when the bucket already exists.
     */
    @Override
    public IBucket createBucket(final IBucket bucket)
            throws ClientDoesNotExistException, BucketAlreadyExistsException,
            DBException {
        final String strClientName = bucket.getBucketParentName();
        final RiakClient c = getClient_(strClientName);

        if (c == null) {
            throw new ClientDoesNotExistException(strClientName);
        }

        try {
            return c.createBucket(bucket.getBucketName());
        } catch (RiakRetryFailedException rrfe) {
            throw new DBException(rrfe);
        } // end try
    } // createBucket()

    /**
     * Builds a tuple with the specified details.
     * 
     * @param tuple
     *            the details of the tuple to build.
     * @return the tuple that was created.
     * @throws BucketDoesNotExistException
     *             when when the bucket does not already exist.
     * @throws TupleAlreadyExistsException
     *             when the tuple already exists.
     * @throws InvalidArgumentException
     *             when failed to create the specified tuple.
     * @throws ClientDoesNotExistException
     *             when when the client does not already exist.
     * @throws DBException when .
     */
    @Override
    public ITuple createTuple(final ITuple tuple)
            throws BucketDoesNotExistException, TupleAlreadyExistsException,
            InvalidArgumentException, ClientDoesNotExistException, DBException {
        final IBucket bucket = tuple.getTupleParent();
        final IClient client = bucket.getBucketParent();
        final RiakClient c = getClient_(client.getClientName());

        if (c == null) {
            throw new ClientDoesNotExistException(client.getClientName());
        }

        try {
            return c.createTuple(tuple);
        } catch (final Exception e) {
            throw new DBException(e);
        } // end try
    } // createTuple()

    @Override
    public void connect(final String strClientName) throws DBException {
        final RiakClient client = getClient_(strClientName);

        try {
            client.connect();
        } catch (Throwable e) {
            throw new DBException(e);
        } // end try
    } // connect()

    @Override
    public void shutdown(final String strClientName) throws DBException {
        final RiakClient client = getClient_(strClientName);

        try {
            client.shutdown();
            this.mClients.remove(client);
        } catch (Throwable e) {
            throw new DBException(e);
        } // end try
    } // shutdown()

    @Override
    public void disconnect(final String strClientName) throws DBException {
        final RiakClient client = getClient_(strClientName);

        try {
            client.shutdown();
        } catch (Throwable e) {
            throw new DBException(e);
        } // end try
    } // disconnect()

    /**
     * Closes any connection open without removing any of the conection details.
     */
    @Override
    public void close() {
        for (final RiakClient client : this.mClients) {
            client.close();
        }
    } // close()

    /**
     * Removes the client with the specified name, if exists.
     * 
     * @param strClientName
     *            the name of the client.
     * @return the client with the specified name.
     */
    public RiakClient removeClient(final String strClientName) {
        final RiakClient client = getClient_(strClientName);

        client.close();
        this.mClients.remove(client);

        return client;
    } // removeClient()

    @Override
    public RiakClient removeClient(final IClient client) {
        return removeClient(client.getClientName());
    } // removeClient()

    RiakBucket removeBucket(final String strClientName,
            final String strBucketName) {
        final RiakClient client = getClient_(strClientName);

        return client.removeBucket(strBucketName);
    } // removeClient()

    @Override
    public RiakBucket removeBucket(final IBucket bucket) {
        return removeBucket(bucket.getBucketParent().getClientName(),
                bucket.getBucketName());
    } // removeBucket()

    @Override
    public ITuple removeTuple(final ITuple tuple) throws DBException {
        final RiakBucket bucket = getBucket_(tuple.getTupleParent());

        return bucket.delete(tuple.getKeyName());
    } // removeTuple()

    @Override
    public void init(final IView view) {
        this.mView = view;
        this.mResponseView = new ResponseView(view.getFrame());
        view.setExtrListener(this);
    } // init()

    @Override
    public boolean isAllowedToDeleteBuckets() {
        return false;
    } // isAllowedToDeleteBuckets()

    // IExtrListener ==============================
    @Override
    public void actionPerformed(final ActionEvent ae) {
        final String strAction = ae.getActionCommand();
        String strTitle = null;
        String strResponse = null;

        if (ACTION_CLIENT_PING.equals(strAction)) {
            final TreePath path = this.mView.getSelectionPath();
            final IClient clientNode = (IClient) path.getLastPathComponent();
            final RiakClient client = getClient_(clientNode.getClientName());

            strResponse = client.ping();
            if (strResponse != null) {
                strTitle = MessageFormat.format(TITLE_PING,
                        clientNode.getClientName());
            }
        } else if (ACTION_CLIENT_STATS.equals(strAction)) {
            final TreePath path = this.mView.getSelectionPath();
            final IClient clientNode = (IClient) path.getLastPathComponent();
            final RiakClient client = getClient_(clientNode.getClientName());

            strResponse = client.stats();
            if (strResponse != null) {
                strTitle = MessageFormat.format(TITLE_CLIENT_STATS,
                        clientNode.getClientName());
            }
            // } else if (ACTION_CLIENT_CONFIG.equals(strAction)) {
            // final TreePath path = this.mView.getSelectionPath();
            // final IClient clientNode = (IClient) path.getLastPathComponent();
            // final RiakClient client = getClient_(clientNode.getClientName());
            //
            // strResponse = client.config();
            // strTitle = MessageFormat.format(TITLE_CLIENT_CONFIG,
            // clientNode.getClientName());
            // if (strResponse != null) {
            // strTitle = MessageFormat.format(TITLE_CLIENT_CONFIG,
            // clientNode.getClientName());
            // }
        } else if (ACTION_BUCKET_NVAL.equals(strAction)) {
            final TreePath path = this.mView.getSelectionPath();
            final IBucket bucketNode = (IBucket) path.getLastPathComponent();
            final RiakBucket bucket = getBucket_(bucketNode);

            strResponse = bucket.getNVal();
            if (strResponse != null) {
                strTitle = MessageFormat.format(TITLE_BUCKET_NVAL,
                        bucketNode.getBucketName());
            }
        } else {
            Verbose.warning("Unsupported operation \"{0}\"", strAction);
        }

        if (strTitle != null) {
            this.mResponseView.set(strTitle, strResponse);
        }
    } // actionPerformed()

    @Override
    public JMenuItem[] getMenuItems(final IClient client,
            final boolean isConnected) {
        final JMenuItem[] aMenuItems = new JMenuItem[ACTIONS_CLIENT.length];
        int iValid = 0;

        for (int i = 0; i < ACTIONS_CLIENT.length; ++i) {
            final String strAction = ACTIONS_CLIENT[i];
            final String strActionTxt = ACTIONS_CLIENT_TXT[i];
            final JMenuItem item;

            if (strAction == ACTION_CLIENT_STATS) {
                final RiakClient c = getClient_(client.getClientName());

                if (c.getClientType() == TYPE.PBC) {
                    continue;
                }
            }
            ++iValid;

            item = new JMenuItem(strAction);
            item.setActionCommand(strActionTxt);
            item.addActionListener(this);
            item.setEnabled(isConnected);
            aMenuItems[i] = item;
        } // end for

        if (iValid < aMenuItems.length) {
            // Less items than the default ones so remove empty entries
            final JMenuItem[] aMenuItems1 = new JMenuItem[iValid];

            for (int i = 0, j = -1; i < aMenuItems.length; ++i) {
                if (aMenuItems[i] != null) {
                    aMenuItems1[++j] = aMenuItems[i];
                }
            } // end for

            return aMenuItems1;
        }

        return aMenuItems;
    } // getMenuItems()

    @Override
    public JMenuItem[] getMenuItems(final IBucket bucket,
            final boolean isConnected) {
        return null;
    } // getMenuItems()

    @Override
    public JMenuItem[] getMenuItems(final ITuple tuple,
            final boolean isConnected) {
        return null;
    } // getMenuItems()
    // End IExtrListener ==========================

    /**
     * Provides the client with the specified name.
     * 
     * @param strName
     *            the name of the client.
     * @return the client with the specified name, if exists. Otherwise null.
     */
    protected RiakClient getClient_(final String strName) {
        for (final RiakClient client : this.mClients) {
            if (client.getName().equals(strName)) {
                return client;
            }
        }

        return null;
    } // getClient_()

    /**
     * Provides the bucket with the specified name for the specified client.
     * 
     * @param bucket
     *            the bucket basic data.
     * @return the bucket with the specified name, if exists. Otherwise null.
     */
    protected RiakBucket getBucket_(final IBucket bucket) {
        final RiakClient client = getClient_(bucket.getBucketParentName());

        if (client != null) {
            return client.getBucket(bucket.getBucketName());
        }

        return null;
    } // getBucket_()

    /**
     * Provides the tuple with the specified name for the specified bucket and
     * client.
     * 
     * @param tuple
     *            the tuple basic data.
     * @return the tuple with the specified name, if exists. Otherwise null.
     */
    protected RiakTuple getTuple_(final ITuple tuple) {
        final RiakBucket bucket = getBucket_(tuple.getTupleParent());

        if (bucket != null) {
            return bucket.getTuple(tuple.getKeyName());
        }

        return null;
    } // getTuple_()
} // end class DataController
