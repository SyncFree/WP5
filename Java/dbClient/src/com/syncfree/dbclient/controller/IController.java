package com.syncfree.dbclient.controller;

import java.util.Iterator;

import com.syncfree.dbclient.controller.gui.IView;
import com.syncfree.dbclient.data.IBucket;
import com.syncfree.dbclient.data.IClient;
import com.syncfree.dbclient.data.ITuple;

/**
 * Simple contract to interact with the underlying database.
 * <p>
 * Implement this interface to define a controller. Note that it should have an
 * ID that should be part of its simple name where ID + "Controller" should be
 * the name of the controller name in a package
 * "com.syncfree.dbclient.controller." + ID, and any support for extra views and
 * actions that should be added in the package
 * "com.syncfree.dbclient.controller." + ID + ".gui" implementing
 * <code>IView</code> and maybe even <code>IExtrListener</code>.
 * <p>
 * Example:
 * <p>
 * ID: riak
 * <p>
 * Packages:
 * <p>
 * com.syncfree.dbclient.controller.riak
 * <p>
 * com.syncfree.dbclient.controller.riak.gui
 * <p>
 * Class: RiakController (RiakController.java)
 *
 * @author aas
 * @version 0.0
 */
public interface IController {
    /**
     * Allows controllers to provide their own views.
     * 
     * @param view
     *            the application GUI view.
     */
    public void init(IView view);

    /**
     * @return textual identification for the controller.
     */
    public String getID();

    /**
     * Establishes a connection between client and back-end database.
     * 
     * @param strClientName
     *            the client's name.
     * @throws DBException
     *             when failed to connect.
     */
    public void connect(final String strClientName) throws DBException;

    /**
     * Disconnects client from back-end database and remove any reference to
     * that client.
     * 
     * @param strClientName
     *            the client's name.
     * @throws DBException
     *             when failed to connect.
     */
    public void shutdown(final String strClientName) throws DBException;

    /**
     * Disconnects client from back-end database.
     * 
     * @param strClientName
     *            the client's name.
     * @throws DBException
     *             when failed to connect.
     */
    public void disconnect(final String strClientName) throws DBException;

    /**
     * Close any connection open.
     */
    public void close();

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
     *             from data when failed to connect.
     */
    public IClient createClient(final IClient client)
            throws ClientAlreadyExistsException, DBException,
            InvalidArgumentException;

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
     * @throws DBException
     *             from data when failed to connect.
     */
    public IBucket createBucket(final IBucket bucket)
            throws ClientDoesNotExistException, BucketAlreadyExistsException,
            DBException;

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
     *             when the passed value is invalid.
     * @throws DBException
     *             from data when failed to create for other reason.
     */
    public ITuple createTuple(final ITuple tuple)
            throws BucketDoesNotExistException, TupleAlreadyExistsException,
            InvalidArgumentException, ClientDoesNotExistException, DBException;

    /**
     * Provides the client with the specified name.
     * 
     * @param strClientName
     *            the name of the client.
     * @return the client with the specified name, if exists. Otherwise null.
     */
    public IClient getClient(final String strClientName);

    /**
     * Provides the bucket with the specified name for the specified client.
     * 
     * @param bucket
     *            the bucket basic data.
     * @return the bucket with the specified name, if exists. Otherwise null.
     */
    public IBucket getBucket(final IBucket bucket);

    /**
     * Provides the tuple with the specified name for the specified bucket and
     * client.
     * 
     * @param tuple
     *            the tuple basic data.
     * @return the tuple with the specified name, if exists. Otherwise null.
     */
    public ITuple getTuple(final ITuple tuple);

    /**
     * @return the list of clients available.
     */
    public Iterator<IClient> getClients();

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
    public Iterator<IBucket> refreshClient(final String strClientName)
            throws DBException;

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
    public Iterator<ITuple> refreshBucket(final IBucket bucket)
            throws DBException;

    /**
     * Removes the client with the specified name, if exists.
     * 
     * @param client
     *            the client identifier.
     * @return the removed client, if successful, or null otherwise.
     */
    public IClient removeClient(final IClient client);

    /**
     * Removes the client with the specified name, if exists.
     * 
     * @param bucket
     *            the bucket identifier.
     * @return the removed bucket, if successful, or null otherwise.
     */
    public IBucket removeBucket(final IBucket bucket);

    /**
     * Removes the client with the specified name, if exists.
     * 
     * @param tuple
     *            the tuple identifier.
     * @return the removed tuple, if successful, or null otherwise.
     * @throws DBException
     *             when fails to delete tuple in the database.
     */
    public ITuple removeTuple(final ITuple tuple) throws DBException;

    /**
     * @return true if it is allowed to delete a bucket, or false otherwise.
     */
    public boolean isAllowedToDeleteBuckets();
} // end interface IController
