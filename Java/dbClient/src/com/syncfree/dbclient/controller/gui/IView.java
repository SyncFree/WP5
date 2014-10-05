package com.syncfree.dbclient.controller.gui;

import javax.swing.JFrame;
import javax.swing.tree.TreePath;

import com.syncfree.dbclient.data.IBucket;
import com.syncfree.dbclient.data.IClient;
import com.syncfree.dbclient.data.ITuple;

/**
 * Access to the main view to allow custom views.
 * <p>
 * Remember to use <code>Verbose</code> class to log any message to the log
 * view.
 * 
 * @author aas
 * @version 0.0
 */
public interface IView {
    /**
     * @return the path for the selected node.
     */
    public TreePath getSelectionPath();

    /**
     * @return the GUI frame.
     */
    public JFrame getFrame();

    /**
     * Allows to set an extra listener.
     * 
     * @param listener
     *            the extra action listener.
     */
    public void setExtrListener(IExtrListener listener);

    /**
     * Enables or disables this component, depending on the value of the
     * parameter <code>bEnabled</code>.
     * <p>
     * Disabling the main view will make it unaccessible by mouse action so make
     * sure to re-enable once completed the action that required to disable the
     * main view.
     * 
     * @param bEnabled
     *            true this component is enabled; otherwise this component is
     *            disabled.
     */
    public void setEnabled(boolean bEnabled);

    /**
     * Enables or disables the popup menu for the specified client.
     * 
     * @param client
     *            the client's details.
     * @param bEnable
     *            true if the view for the specified client is enabled or false
     *            otherwise.
     */
    public void setEnable(IClient client, boolean bEnable);

    /**
     * Enables or disables the popup menu for the specified bucket.
     * 
     * @param bucket
     *            the bucket's details.
     * @param bEnable
     *            true if the view for the specified bucket is enabled or false
     *            otherwise.
     */
    public void setEnable(IBucket bucket, boolean bEnable);

    /**
     * Enables or disables the popup menu for the specified tuple.
     * 
     * @param tuple
     *            the tuple's details.
     * @param bEnable
     *            true if the view for the specified tuple is enabled or false
     *            otherwise.
     */
    public void setEnable(ITuple tuple, boolean bEnable);
} // end class IView()
