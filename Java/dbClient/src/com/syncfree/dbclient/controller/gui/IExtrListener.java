package com.syncfree.dbclient.controller.gui;

import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import com.syncfree.dbclient.data.IBucket;
import com.syncfree.dbclient.data.IClient;
import com.syncfree.dbclient.data.ITuple;

/**
 * Extra operations to extend the already basic ones provided by the <code>MainView</code>.
 * 
 * @author aas
 * @version 0.0
 */
public interface IExtrListener extends ActionListener {
    /**
     * Provides the list of extra menu items to add to the popup menu that will
     * show on top of a client icon on the main view.
     * 
     * @param client
     *            the client's details.
     * @param isConnected
     *            true if the client is connected or false otherwise.
     * @return the list of extra menu items. It may be null when no extra menu
     *         items to be added.
     */
    public JMenuItem[] getMenuItems(IClient client, boolean isConnected);

    /**
     * Provides the list of extra menu items to add to the popup menu that will
     * show on top of a bucket icon on the main view.
     * 
     * @param bucket
     *            the bucket's details.
     * @param isConnected
     *            true if the bucket is connected or false otherwise.
     * @return the list of extra menu items. It may be null when no extra menu
     *         items to be added.
     */
    public JMenuItem[] getMenuItems(IBucket bucket, boolean isConnected);

    /**
     * Provides the list of extra menu items to add to the popup menu that will
     * show on top of a tuple icon on the main view.
     * 
     * @param tuple
     *            the tuple's details.
     * @param isConnected
     *            true if the tuple is connected or false otherwise.
     * @return the list of extra menu items. It may be null when no extra menu
     *         items to be added.
     */
    public JMenuItem[] getMenuItems(ITuple tuple, boolean isConnected);
} // end interface IExtrListener
