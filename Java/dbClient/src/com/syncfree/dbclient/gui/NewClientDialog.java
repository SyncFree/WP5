package com.syncfree.dbclient.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.aasco.util.PropertyDef;
import com.syncfree.dbclient.data.IClient;

/**
 * GUI dialog window to input the required details to create a DB client.
 *
 * @author aas
 * @version 0.0
 */
public class NewClientDialog extends JDialog implements ActionListener,
        ItemListener {
    /**
     * A version number for this class so that serialisation can occur without
     * worrying about the underlying class changing between serialisation and
     * deserialisation.
     * <p>
     *
     * Not that we ever serialise this class of course, but JDialog implements
     * Serializable, so therefore by default we do as well.
     */
    private static final long serialVersionUID = -8404503117882461208L;

    /**
     * The default name for the DB New Client control.
     */
    public static final String DEFAULT_NAME = "DB New Client";

    public static final String ACTION_CREATE_CLIENT = "Create";
    public static final String ACTION_CANCEL_CREATE_CLIENT = "Cancel Create Client";

    /** The dialog title. */
    private static final String TITLE = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.solution_nre_client_title", DEFAULT_NAME)
            .getProperty();
    /** The name for the create client button. */
    private static final String CREATE_CLIENT = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.solution_nre_create_client_btn",
            ACTION_CREATE_CLIENT).getProperty();
    /** The name for the cancel client button. */
    private static final String CANCEL = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.solution_cancel_btn", "Cancel")
            .getProperty();
    // Types of connections
    /** The HTTP connection type. */
    private static final String CONN_HTTP = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.connection_http", "HTTP").getProperty();
    /** The DB connection type. */
    private static final String CONN_PCB = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.connection_pcb", "PCB").getProperty();
    /** The client name label. */
    private static final String LABEL_NAME = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.connection_client_name_label", "Name: ")
            .getProperty();
    /** The DB connection type label. */
    private static final String LABEL_TYPE = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.connection_conn_type_label", "Type: ")
            .getProperty();
    /** The URL for a HTTP connection. */
    private static final String LABEL_URL = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.connection_conn_url_label", "URL: ")
            .getProperty();
    /** The host for a PDB connection. */
    private static final String LABEL_HOST = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.connection_conn_host_label", "Host: ")
            .getProperty();
    /** The host for a PDB connection. */
    private static final String LABEL_PORT = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.connection_conn_port_label", "Port: ")
            .getProperty();
    /** The number of columns for the client's name. */
    private static final int NAME_LABEL_NUM_COLS = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.connection_client_name_num_cols", 12)
            .getProperty();
    /** The number of columns for the client's connection url. */
    private static final int URL_LABEL_NUM_COLS = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.connection_client_url_num_cols", 30)
            .getProperty();
    /** The number of columns for the client's connection url. */
    private static final int HOST_LABEL_NUM_COLS = PropertyDef
            .getPropertyDef(
                    "com.syncfree.dbclient.gui.connection_"
                            + "client_host_num_cols", 25).getProperty();
    /** The number of columns for the client's connection url. */
    private static final int PORT_LABEL_NUM_COLS = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.connection_client_port_num_cols", 6)
            .getProperty();

    private static final int CREATE_CLIENT_DIALOG_WIDTH = PropertyDef
            .getPropertyDef(
                    "com.syncfree.dbclient.gui.connection_create_client_dialog_width",
                    500).getProperty();
    private static final int CREATE_CLIENT_DIALOG_HEIGHT = PropertyDef
            .getPropertyDef(
                    "com.syncfree.dbclient.gui.connection_create_client_dialog_height",
                    140).getProperty();

    /** The Client's name view. */
    private JTextField mName;
    /** Holds the connection views. */
    private JPanel mCards;
    /** The HTTP connection view. */
    private HttpView mHttp;
    /** The PCB connection view. */
    private PbcView mPbc;
    /** Holds the correct connection view. */
    private IClient mData;

    /**
     * Builds the client's dialog input GUI.
     * 
     * @param oFrame
     *            the application frame.
     * @param listener
     *            the action listener.
     */
    public NewClientDialog(final JFrame oFrame, final ActionListener listener) {
        super(oFrame, TITLE, true);

        final JPanel pane = new JPanel(new BorderLayout());
        final JPanel top = buildTopPane();
        final JPanel bottom = buildBottomPane(listener);

        pane.add(top, BorderLayout.CENTER);
        pane.add(bottom, BorderLayout.SOUTH);
        pane.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
        add(pane);

        getAccessibleContext().setAccessibleName(DEFAULT_NAME);
        // getAccessibleContext().setAccessibleDescription(
        // ResourceBundleHelper.getAlwaysString(
        // ResourceBundleHelper.BASE_RESOURCE_BUNDLE,
        // "com.syncfree.dbclient.gui.create_client_accessibility_desc"));

        setName(getTitle());
        setPreferredSize(new Dimension(CREATE_CLIENT_DIALOG_WIDTH,
                CREATE_CLIENT_DIALOG_HEIGHT));

        pack();

        final int x = oFrame.getX() + ((oFrame.getWidth() - getWidth()) / 2);
        final int y = oFrame.getY() + ((oFrame.getHeight() - getHeight()) / 2);

        setLocation(x, y);
    } // Constructor ()

    public IClient getData() {
        return new Client(this);
    } // getData()

    @Override
    public void actionPerformed(final ActionEvent ae) {
        setVisible(false);
    } // actionPerformed()

    public String getClientName() {
        return this.mName.getText();
    } // getClientName()

    public IClient.TYPE getClientType() {
        return this.mData.getClientType();
    } // getClientType()

    public String getURL() {
        return this.mData.getURL();
    } // getURL()

    public int getPort() {
        return this.mData.getPort();
    } // getPort()

    public String getHost() {
        return this.mData.getHost();
    } // getHost()

    public boolean isConnected() {
        return false;
    } // isConnected()

    @Override
    public void itemStateChanged(final ItemEvent ie) {
        final CardLayout cl = (CardLayout) (this.mCards.getLayout());
        final String itemID = (String) ie.getItem();

        if (itemID.equals(CONN_HTTP)) {
            this.mData = (IClient) this.mHttp;
        } else {
            this.mData = (IClient) this.mPbc;
        }
        cl.show(this.mCards, itemID);
    } // itemStateChanged()

    /**
     * Clears the dialog-box controls.
     */
    public void reset() {
        this.mName.setText("");
        this.mHttp.reset();
        this.mPbc.reset();
    } // reset()

    /**
     * Builds the top part of the dialog window where the client data is
     * selected/introduced.
     * 
     * @return the top pane.
     */
    private JPanel buildTopPane() {
        final JPanel top = new JPanel(new BorderLayout());
        JPanel p;

        // Client's name panel
        final JLabel lName = new JLabel(LABEL_NAME);
        final JPanel pTop = new JPanel();

        p = new JPanel(new BorderLayout());
        pTop.add(lName);
        this.mName = new JTextField(NAME_LABEL_NUM_COLS);
        pTop.add(this.mName);
        p.add(pTop, BorderLayout.CENTER);

        // Create connection type panel
        final JLabel lType = new JLabel(LABEL_TYPE);
        final JComboBox<String> cbTypes = new JComboBox<>(new String[] {
                CONN_HTTP, CONN_PCB });
        final JPanel pMiddle = new JPanel();

        cbTypes.setEditable(false);
        cbTypes.addItemListener(this);
        pMiddle.add(lType);
        pMiddle.add(cbTypes);
        p.add(pMiddle, BorderLayout.EAST);

        p.setAlignmentX(LEFT_ALIGNMENT);
        top.add(p, BorderLayout.NORTH);

        // Create the panel that contains the "cards"
        this.mHttp = new HttpView();
        this.mData = this.mHttp;
        this.mPbc = new PbcView();

        this.mCards = new JPanel(new CardLayout());
        this.mCards.add(this.mHttp, CONN_HTTP);
        this.mCards.add(this.mPbc, CONN_PCB);
        top.add(this.mCards, BorderLayout.CENTER);
        top.setAlignmentX(LEFT_ALIGNMENT);

        return top;
    } // buildTopPane()

    /**
     * Builds the bottom part of the dialog window.
     * 
     * @param listener
     *            the listener associated the actions for this dialog window.
     * @return the bottom pane.
     */
    private JPanel buildBottomPane(final ActionListener listener) {
        final JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        final JButton create = new JButton(CREATE_CLIENT);
        final JButton cancel = new JButton(CANCEL);

        create.setActionCommand(ACTION_CREATE_CLIENT);
        bottom.add(create);
        cancel.setActionCommand(ACTION_CANCEL_CREATE_CLIENT);
        bottom.add(cancel);
        if (listener != null) {
            create.addActionListener(listener);
            create.addActionListener(this);
            cancel.addActionListener(this);
        }

        return bottom;
    } // buildBottomPane()

    /**
     * GUI of the client creation input view for HTTP.
     * 
     * @author aas
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

        @Override
        public String getClientName() {
            return NewClientDialog.this.mName.getText();
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

        @Override
        public boolean isConnected() {
            return false;
        } // isConnected()
    } // end class HttpView

    /**
     * GUI for the client creation input view for PCD.
     * 
     * @author aas
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

        @Override
        public String getClientName() {
            return NewClientDialog.this.mName.getText();
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

        @Override
        public boolean isConnected() {
            return false;
        } // isConnected()
    } // end class PbcView
} // end class NewClientDialog

/**
 * Copy of the data.
 * 
 * @author aas
 * @Version 0.0
 */
class Client implements IClient {
    private String mstrName;
    private TYPE meType;
    private String mstrConnection;
    private int miPort;

    Client(final NewClientDialog dialog) {
        this.mstrName = dialog.getClientName();
        this.meType = dialog.getClientType();
        if (this.meType == TYPE.HTTP) {
            this.mstrConnection = dialog.getURL();
        } else {
            this.miPort = dialog.getPort();
            this.mstrConnection = dialog.getHost();
        }
    } // Constructor ()

    @Override
    public String getClientName() {
        return this.mstrName;
    } // getClientName()

    @Override
    public TYPE getClientType() {
        return this.meType;
    } // getClientType()

    @Override
    public String getURL() {
        return this.mstrConnection;
    } // getURL()

    @Override
    public int getPort() {
        return this.miPort;
    } // getPort()

    @Override
    public String getHost() {
        return this.mstrConnection;
    } // getHost()

    @Override
    public boolean isConnected() {
        return false;
    } // isConnected()
} // end class Client
