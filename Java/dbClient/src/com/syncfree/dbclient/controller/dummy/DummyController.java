package com.syncfree.dbclient.controller.dummy;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.tree.TreePath;

import com.aasco.util.PropertyDef;
import com.aasco.util.Verbose;
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
import com.syncfree.dbclient.data.IBucket;
import com.syncfree.dbclient.data.IClient;
import com.syncfree.dbclient.data.ITuple;

/**
 * Handles all interactions between the GUI layer and the data layer.
 * <p>
 * Support to access allow to create clients, buckets and tuples without a real
 * database, i.e. in memory.
 * 
 * @author aas
 * @version 0.0
 */
public class DummyController implements IController, IExtrListener {
    // Actions
    private static final String ACTION_CLIENT_SHUTDOWN = "Shutdown";

    // Actions for a client
    public static final String[] ACTIONS_CLIENT = { ACTION_CLIENT_SHUTDOWN };
    public static final String[] ACTIONS_CLIENT_TXT = {
        PropertyDef.getPropertyDef(
                "com.syncfree.dbclient.controller.dummy.action_shutdown",
                ACTION_CLIENT_SHUTDOWN).getProperty()
                };

    /** List of all clients. */
    private final List<DummyClient> mClients;

    private IView mView;

    /**
     * Builds the controller to access Dummy.
     * 
     * @param astrArgs
     *            the command line arguments.
     */
    public DummyController(final String[] astrArgs) {
        this.mClients = new ArrayList<>();
    } // Constructor ()

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
        final DummyClient client = getClient_(strClientName);
        Iterator<IBucket> iter = null;

        if (client != null) {
            iter = new IIterator<>(client.refresh());
        }

        return iter;
    } // refreshClient()

    @Override
    public String getID() {
        return "Dummy";
    } // getID()

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
        final DummyClient client = getClient_(bucket.getBucketParentName());
        Iterator<ITuple> iter = null;

        if (client != null) {
            final DummyBucket b = client.getBucket(bucket.getBucketName());

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
     */
    @Override
    public IClient createClient(final IClient client)
            throws ClientAlreadyExistsException {
        DummyClient c = getClient_(client.getClientName());

        if (c != null) {
            throw new ClientAlreadyExistsException(client.getClientName());
        }

        c = new DummyClient(client);
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
            throws ClientDoesNotExistException, BucketAlreadyExistsException {
        final String strClientName = bucket.getBucketParentName();
        final DummyClient c = getClient_(strClientName);

        if (c == null) {
            throw new ClientDoesNotExistException(strClientName);
        }

        return c.buildBucket(bucket);
    } // createBucket()

    /**
     * Builds a tuple with the specified details.
     * 
     * @param tuple
     *            the details of the tuple to build.
     * @return the tuple that was created.
     * @throws TupleAlreadyExistsException
     *             when the tuple already exists.
     * @throws BucketDoesNotExistException
     *             when when the bucket does not already exist.
     * @throws ClientDoesNotExistException
     *             when when the client does not already exist.
     * @throws InvalidArgumentException
     *             when failed to create the specified tuple.
     */
    @Override
    public ITuple createTuple(final ITuple tuple)
            throws BucketDoesNotExistException, TupleAlreadyExistsException,
            InvalidArgumentException, ClientDoesNotExistException {
        final IBucket bucket = tuple.getTupleParent();
        final IClient client = bucket.getBucketParent();
        final DummyClient c = getClient_(client.getClientName());

        if (c == null) {
            throw new ClientDoesNotExistException(client.getClientName());
        }

        return c.buildTuple(tuple);
    } // createTuple()

    @Override
    public void connect(final String strClientName) throws DBException {
        final DummyClient client = getClient_(strClientName);

        client.connect();
    } // connect()

    @Override
    public void shutdown(final String strClientName) throws DBException {
        final DummyClient client = getClient_(strClientName);

        client.shutdown();
        this.mClients.remove(client);
    } // shutdown()

    @Override
    public void disconnect(final String strClientName) throws DBException {
        final DummyClient client = getClient_(strClientName);

        client.shutdown();
    } // disconnect()

    /**
     * Close any connection open.
     */
    public void close() {
        for (final DummyClient client : this.mClients) {
            client.close();
        } // end for
    } // close()

    /**
     * Removes the client with the specified name, if exists.
     * 
     * @param strClientName
     *            the name of the client.
     * @return the client with the specified name.
     */
    public DummyClient removeClient(final String strClientName) {
        final DummyClient client = getClient_(strClientName);

        client.close();
        this.mClients.remove(client);

        return client;
    } // removeClient()

    @Override
    public DummyClient removeClient(final IClient client) {
        return removeClient(client.getClientName());
    } // removeClient()

    public DummyBucket removeBucket(final String strClientName,
            final String strBucketName) {
        final DummyClient client = getClient_(strClientName);

        return client.removeBucket(strBucketName);
    } // removeClient()

    @Override
    public DummyBucket removeBucket(final IBucket bucket) {
        return removeBucket(bucket.getBucketParent().getClientName(),
                bucket.getBucketName());
    } // removeBucket()

    @Override
    public ITuple removeTuple(final ITuple tuple) throws DBException {
        final DummyBucket bucket = getBucket_(tuple.getTupleParent());

        return bucket.delete(tuple.getKeyName());
    } // removeTuple()

    @Override
    public void init(final IView view) {
        this.mView = view;
        view.setExtrListener(this);
    } // init()

    @Override
    public void actionPerformed(final ActionEvent ae) {
        final String strAction = ae.getActionCommand();

        if (ACTION_CLIENT_SHUTDOWN.equals(strAction)) {
            final TreePath path = this.mView.getSelectionPath();
            final IClient clientNode = (IClient) path.getLastPathComponent();
            final DummyClient c = getClient_(clientNode.getClientName());

            c.setConnected(false);
            Verbose.info("Completed shutdown");
        }
    } // actionPerformed()

    @Override
    public JMenuItem[] getMenuItems(final IClient client, final boolean isConnected) {
        final JMenuItem[] aMenuItems = new JMenuItem[ACTIONS_CLIENT.length];

        for (int i = 0; i < ACTIONS_CLIENT.length; ++i) {
            final String strAction = ACTIONS_CLIENT[i];
            final String strActionTxt = ACTIONS_CLIENT_TXT[i];
            final JMenuItem item;

            item = new JMenuItem(strAction);
            item.setActionCommand(strActionTxt);
            item.addActionListener(this);
            item.setEnabled(isConnected);
            aMenuItems[i] = item;
        } // end for

        return aMenuItems;
    } // getMenuItems()

    @Override
    public JMenuItem[] getMenuItems(final IBucket bucket, boolean isConnected) {
        return null;
    } // getMenuItems()

    @Override
    public JMenuItem[] getMenuItems(final ITuple tuple, boolean isConnected) {
        return null;
    } // getMenuItems()

    @Override
    public boolean isAllowedToDeleteBuckets() {
        return true;
    } // isAllowedToDeleteBuckets()

    /**
     * Provides the client with the specified name.
     * 
     * @param strName
     *            the name of the client.
     * @return the client with the specified name, if exists. Otherwise null.
     */
    protected DummyClient getClient_(final String strName) {
        for (final DummyClient client : this.mClients) {
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
    protected DummyBucket getBucket_(final IBucket bucket) {
        final DummyClient client = getClient_(bucket.getBucketParentName());

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
    protected DummyTuple getTuple_(final ITuple tuple) {
        final DummyBucket bucket = getBucket_(tuple.getTupleParent());

        if (bucket != null) {
            return bucket.getTuple(tuple.getKeyName());
        }

        return null;
    } // getTuple_()
} // end class DummyController
