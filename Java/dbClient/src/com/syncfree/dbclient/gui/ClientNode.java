package com.syncfree.dbclient.gui;

import javax.swing.tree.DefaultMutableTreeNode;

import com.syncfree.dbclient.data.IClient;

/**
 * Client node in the GUI tree view.
 * 
 * @author aas
 */
public class ClientNode extends DefaultMutableTreeNode implements IClient, AllowEnable {
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

    /** The client's name. */
    private final String mstrClientName;
    private boolean mbEnabled;
    private boolean mbConnected;

    /**
     * Builds an instance of a client node with the specified details.
     * 
     * @param client
     *            the details of the client.
     */
    public ClientNode(final IClient client) {
        this.mstrClientName = client.getClientName();
        setEnabled(true);
        setConnected(false);
    } // Constructor ()

    @Override
    public boolean isEnabled() {
        return (this.mbEnabled && ((RootNode) getParent()).isEnabled());
    } // isEnabled()

    @Override
    public void setEnabled(final boolean bEnabled) {
        this.mbEnabled = bEnabled;
    } // setEnabled()

    @Override
    public String getClientName() {
        return this.mstrClientName;
    } // getClientName()

    @Override
    public TYPE getClientType() {
        return null;
    } // getClientType()

    @Override
    public String getURL() {
        return null;
    } // getURL()

    @Override
    public int getPort() {
        return -1;
    } // getPort()

    @Override
    public String getHost() {
        return null;
    } // getHost()

    @Override
    public String toString() {
        return getClientName();
    } // toString()

    @Override
    public boolean isConnected() {
        return this.mbConnected;
    } // isConnected()

    public void setConnected(final boolean bConnected) {
        this.mbConnected = bConnected;
    } // setConnected()
} // end class ClientNode
