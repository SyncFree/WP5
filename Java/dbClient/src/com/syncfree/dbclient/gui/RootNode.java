package com.syncfree.dbclient.gui;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Client node in the GUI tree view.
 * 
 * @author aas
 */
class RootNode extends DefaultMutableTreeNode implements AllowEnable {
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
    private static final long serialVersionUID = -5077271582280682736L;

    private boolean mbEnabled;

    RootNode(final String strName) {
        super(strName);
        this.mbEnabled = true;
    } // Constructor ()

    @Override
    public boolean isEnabled() {
        return this.mbEnabled;
    } // isEnabled()

    @Override
    public void setEnabled(final boolean bEnabled) {
        this.mbEnabled = bEnabled;
    } // setEnabled()
} // end class RootNode
