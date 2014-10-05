package com.syncfree.dbclient.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.aasco.util.PropertyDef;
import com.aasco.util.Verbose;
import com.syncfree.dbclient.data.IClient;
import com.syncfree.dbclient.data.IClient.TYPE;

/**
 * View of the data for a client.
 *
 * @author aas
 * @version 0.0
 */
public class ClientView extends JPanel {
    /**
     * A version number for this class so that serialisation can occur without
     * worrying about the underlying class changing between serialisation and
     * deserialisation.
     * <p>
     *
     * Not that we ever serialise this class of course, but JPanel implements
     * Serializable, so therefore by default we do as well.
     */
    private static final long serialVersionUID = 5691867644857485706L;

    // Types of connections
    /** The HTTP connection type. */
    private static final String CONN_HTTP = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.connection_http", "HTTP")
            .getProperty();
    /** The DB connection type. */
    private static final String CONN_PBC = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.connection_pbc", "PBC")
            .getProperty();
    /** The client name label. */
    private static final String LABEL_NAME = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.connection_client_name_label",
            "Name: ").getProperty();
    /** The DB connection type label. */
    private static final String LABEL_TYPE = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.connection_conn_type_label",
            "Type: ").getProperty();
    /** The URL for a HTTP connection. */
    private static final String LABEL_URL = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.connection_conn_url_label",
            "URL: ").getProperty();
    /** The host for a PDB connection. */
    private static final String LABEL_HOST = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.connection_conn_host_label",
            "Host: ").getProperty();
    /** The host for a PDB connection. */
    private static final String LABEL_PORT = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.connection_conn_port_label",
            "Port: ").getProperty();
    /** The number of columns for the client's name. */
    private static final int NAME_LABEL_NUM_COLS = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.connection_" + "client_name_num_cols",
            12).getProperty();
    /** The number of columns for the client's connection url. */
    private static final int URL_LABEL_NUM_COLS = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.connection_" + "client_url_num_cols",
            30).getProperty();
    /** The number of columns for the client's connection url. */
    private static final int HOST_LABEL_NUM_COLS = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.connection_client_host_num_cols",
            25).getProperty();
    /** The number of columns for the client's connection url. */
    private static final int PORT_LABEL_NUM_COLS = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.connection_client_port_num_cols",
            6).getProperty();

    /** The GUI for the client's view. */
    private JTextField mName;
    /** The GUI for the type of connection. */
    private JTextField mType;
    /** It holds the views for the different connection types. */
    private JPanel mCards;
    /** The GUI for the HTTP connection type. */
    private HttpView mHttp;
    /** The GUI for the PCB connection type. */
    private PbcView mPbc;

    /**
     * Builds the empty client's view. Call #update(IClient) to fill the view
     * with the relevant values.
     */
    public ClientView() {
        // Top
        final JPanel pTop = new JPanel();

        pTop.add(new JLabel(LABEL_NAME));
        this.mName = new JTextField(NAME_LABEL_NUM_COLS);
        pTop.add(this.mName);
        pTop.add(new JLabel(LABEL_TYPE));
        this.mType = new JTextField(NAME_LABEL_NUM_COLS);
        pTop.add(this.mType);
        add(pTop, BorderLayout.NORTH);

        // Create the panel that contains the "cards"
        this.mHttp = new HttpView();
        this.mPbc = new PbcView();
        this.mCards = new JPanel(new CardLayout());
        this.mCards.add(this.mHttp, CONN_HTTP);
        this.mCards.add(this.mPbc, CONN_PBC);
        add(this.mCards, BorderLayout.CENTER);

        setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
        setEditable(false);
    } // Constructor ()

    /**
     * Sets the editable elements of the view to allow inputs or not.
     * 
     * @param bEditable true to allow inputs or false otherwise.
     */
    public void setEditable(final boolean bEditable) {
        this.mName.setEditable(bEditable);
        this.mType.setEditable(bEditable);
        this.mHttp.setEditable(bEditable);
        this.mPbc.setEditable(bEditable);
    } // setEditable()

    /**
     * Updates the view.
     * 
     * @param client
     *            the client's details.
     */
    public void update(final IClient client) {
        final String viewID;

        this.mName.setText(client.getClientName());
        if (client.getClientType() == TYPE.HTTP) {
            this.mType.setText(CONN_HTTP);
            this.mHttp.update(client);
            this.mPbc.reset();
            viewID = CONN_HTTP;
        } else if (client.getClientType() == TYPE.PBC) {
            this.mType.setText(CONN_PBC);
            this.mPbc.update(client);
            this.mHttp.reset();
            viewID = CONN_PBC;
        } else {
            Verbose.warning("Invalid type '" + client.getClientType().name()
                    + "'");

            return;
        }
        showView(viewID);
    } // update()

    /**
     * Shows the specified transmission type view.
     * 
     * @param viewID
     *            the transmission view ID.
     */
    protected void showView(final String viewID) {
        final CardLayout cl = (CardLayout) (this.mCards.getLayout());

        cl.show(this.mCards, viewID);
    } // showView()

    /**
     * GUI of the client creation input view for HTTP.
     * 
     * @author aas
     * @version 0.0
     */
    class HttpView extends JPanel implements IClient {
        /**
         * A version number for this class so that serialisation can occur
         * without worrying about the underlying class changing between
         * serialisation and deserialisation.
         * <p>
         *
         * Not that we ever serialise this class of course, but JPanel
         * implements Serializable, so therefore by default we do as well.
         */
        private static final long serialVersionUID = 7663652464650218654L;

        /** The URL for the connection. */
        private final JTextField tfUrl;

        /**
         * Builds the view to acquire the HTTP connection details.
         */
        public HttpView() {
            final JPanel p = new JPanel();

            p.add(new JLabel(LABEL_URL));
            this.tfUrl = new JTextField(URL_LABEL_NUM_COLS);
            p.add(this.tfUrl);
            add(p);
        } // Constructor ()

        /**
         * Sets the editable elements of the view to allow inputs or not.
         * 
         * @param bEditable true to allow inputs or false otherwise.
         */
        public void setEditable(final boolean bEditable) {
            this.tfUrl.setEditable(bEditable);
        } // setEditable()

        @Override
        public String getClientName() {
            return ClientView.this.mName.getText();
        } // getClientName()

        @Override
        public TYPE getClientType() {
            return TYPE.HTTP;
        } // getClientType()

        @Override
        public String getURL() {
            return this.tfUrl.getText();
        } // getURL()

        @Override
        public String getHost() {
            return null;
        } // getHost()

        @Override
        public int getPort() {
            return -1;
        } // getPort()

        /**
         * Clears the dialog-box controls.
         */
        public void reset() {
            this.tfUrl.setText("");
        } // reset()

        /**
         * Updates the view.
         * 
         * @param client
         *            the client's details.
         */
        public void update(final IClient client) {
            this.tfUrl.setText(client.getURL());
        } // update()

        @Override
        public boolean isConnected() {
            return false;
        } // isConnected()
    } // end class HttpView


    /**
     * GUI for the client creation input view for PCD.
     * 
     * @author aas
     * @version 0.0
     */
    class PbcView extends JPanel implements IClient {
        /**
         * A version number for this class so that serialisation can occur
         * without worrying about the underlying class changing between
         * serialisation and deserialisation.
         * <p>
         *
         * Not that we ever serialise this class of course, but JPanel
         * implements Serializable, so therefore by default we do as well.
         */
        private static final long serialVersionUID = -6783633480344062581L;

        /** The host to connect to. */
        private final JTextField tfHost;

        /** The port used to connect to the host. */
        private final JTextField tfPort;

        /**
         * Builds the view to acquire the PCB connection details.
         */
        public PbcView() {
            super(new BorderLayout());

            JPanel p;

            p = new JPanel();
            p.add(new JLabel(LABEL_HOST));
            this.tfHost = new JTextField(HOST_LABEL_NUM_COLS);
            p.add(this.tfHost);
            add(p, BorderLayout.CENTER);

            p = new JPanel();
            p.add(new JLabel(LABEL_PORT));
            this.tfPort = new JTextField(PORT_LABEL_NUM_COLS);
            p.add(this.tfPort);
            add(p, BorderLayout.EAST);
        } // Constructor ()

        /**
         * Sets the editable elements of the view to allow inputs or not.
         * 
         * @param bEditable true to allow inputs or false otherwise.
         */
        public void setEditable(final boolean bEditable) {
            this.tfHost.setEditable(bEditable);
            this.tfPort.setEditable(bEditable);
        } // setEditable()

        @Override
        public String getClientName() {
            return ClientView.this.mName.getText();
        } // getClientName()

        @Override
        public TYPE getClientType() {
            return TYPE.PBC;
        } // getClientType()

        @Override
        public String getURL() {
            return null;
        } // getURL()

        @Override
        public int getPort() {
            return Integer.parseInt(this.tfPort.getText());
        } // getPort()

        @Override
        public String getHost() {
            return this.tfHost.getText();
        } // getHost()

        /**
         * Clears the dialog-box controls.
         */
        public void reset() {
            this.tfHost.setText("");
            this.tfPort.setText("");
        } // reset()

        /**
         * Updates the view.
         * 
         * @param client
         *            the client's details.
         */
        public void update(final IClient client) {
            this.tfHost.setText(client.getHost());
            this.tfPort.setText(String.valueOf(client.getPort()));
        } // update()

        @Override
        public boolean isConnected() {
            return false;
        } // isConnected()
    } // end class PbcView
} // end class ClientView
