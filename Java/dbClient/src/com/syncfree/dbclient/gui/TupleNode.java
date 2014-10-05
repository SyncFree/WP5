package com.syncfree.dbclient.gui;

import javax.swing.tree.DefaultMutableTreeNode;

import com.syncfree.dbclient.data.IBucket;
import com.syncfree.dbclient.data.ITuple;

/**
 * Bucket node in the GUI tree view.
 * 
 * @author aas
 */
class TupleNode extends DefaultMutableTreeNode implements ITuple, AllowEnable {
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

    /** The tuple's key. */
    private final String mstrKey;
    private boolean mbEnabled;

    /**
     * Builds an instance of a tuple node with the specified details.
     * 
     * @param tuple
     *            the details of the tuple.
     */
    public TupleNode(final ITuple tuple) {
        this.mstrKey = tuple.getKeyName();
        setEnabled(true);
    } // Constructor ()

    @Override
    public boolean isEnabled() {
        return (this.mbEnabled && ((BucketNode) getParent()).isEnabled());
    } // isEnabled()

    public boolean isConnected() {
        return ((BucketNode) getParent()).isConnected();
    } // isConnected()

    @Override
    public void setEnabled(final boolean bEnabled) {
        this.mbEnabled = bEnabled;
    } // setEnabled()

    @Override
    public String getValue() {
        return null; // not to be used
    } // getValue()

    @Override
    public String getKeyName() {
        return this.mstrKey;
    } // getKeyName()

    @Override
    public IBucket getTupleParent() {
        return (BucketNode) getParent();
    } // getTupleParent()

    @Override
    public String getTupleParentName() {
        return this.mstrKey;
    } // getTupleParentName()

    @Override
    public String toString() {
        return getKeyName();
    } // toString()
} // end class TupleNode
