package com.syncfree.dbclient.gui;

import javax.swing.tree.DefaultMutableTreeNode;

import com.syncfree.dbclient.data.IBucket;
import com.syncfree.dbclient.data.IClient;

/**
 * Bucket node in the GUI tree view.
 * 
 * @author aas
 */
public class BucketNode extends DefaultMutableTreeNode implements IBucket, AllowEnable {
    /**
     * A version number for this class so that serialisation can occur without
     * worrying about the underlying class changing between serialisation and
     * deserialisation.
     * <p>
     *
     * Not that we ever serialise this class of course, but
     * DefaultMutableTreeNode implements Serializable, so therefore by default
     * we do as well.
     */
    private static final long serialVersionUID = 7768491444319787704L;

    /** The bucket's name. */
    private final String mstrBucketName;
    private boolean mbEnabled;

    /**
     * Builds an instance of a bucket node with the specified details.
     * 
     * @param bucket
     *            the details of the bucket.
     */
    public BucketNode(final IBucket bucket) {
        this.mstrBucketName = bucket.getBucketName();
        setEnabled(true);
    } // Constructor ()

    @Override
    public boolean isEnabled() {
        return (this.mbEnabled && ((ClientNode) getParent()).isEnabled());
    } // isEnabled()

    public boolean isConnected() {
        return ((ClientNode) getParent()).isConnected();
    } // isConnected()

    @Override
    public void setEnabled(final boolean bEnabled) {
        this.mbEnabled = bEnabled;
    } // setEnabled()

    @Override
    public String getBucketName() {
        return this.mstrBucketName;
    } // getBucketName()

    @Override
    public String toString() {
        return getBucketName();
    } // toString()

    @Override
    public IClient getBucketParent() {
        return (ClientNode) getParent();
    } // getBucketParent()

    @Override
    public String getBucketParentName() {
        return getBucketParent().getClientName();
    } // getBucketParentName()
} // end class BucketNode
