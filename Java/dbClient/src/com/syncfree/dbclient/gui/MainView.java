package com.syncfree.dbclient.gui;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.aasco.gui.AboutDlg;
import com.aasco.gui.HelpDlg;
import com.aasco.gui.LogGUIView;
import com.aasco.gui.LogView;
import com.aasco.util.ArgDef;
import com.aasco.util.ImageManager;
import com.aasco.util.ParamDef;
import com.aasco.util.PropertyDef;
import com.aasco.util.Verbose;
import com.syncfree.dbclient.DBGuiTool;
import com.syncfree.dbclient.controller.DBException;
import com.syncfree.dbclient.controller.IController;
import com.syncfree.dbclient.controller.gui.IExtrListener;
import com.syncfree.dbclient.controller.gui.IView;
import com.syncfree.dbclient.data.IBucket;
import com.syncfree.dbclient.data.IClient;
import com.syncfree.dbclient.data.ITuple;
import com.syncfree.dbclient.in.IInput;

/**
 * The main application window of the DB client application.
 * <p>
 * Uses the View, Model and Controller pattern.
 * 
 * @author aas
 * @version 0.0
 */
public class MainView extends JFrame implements WindowListener, IView {
    /**
     * A version number for this class so that serialisation can occur without
     * worrying about the underlying class changing between serialisation and
     * deserialisation.
     * <p>
     *
     * Not that we ever serialise this class of course, but JFrame implements
     * Serializable, so therefore by default we do as well.
     */
    private static final long serialVersionUID = 5174698845470370048L;

    // Command line keys
    /**
     * Definition to the command line arguments with key 'db'. Format:
     * <p>
     * -db db_controller
     * <p>
     * Input the property database type used by this application.
     */
    public static final ArgDef KEY_DB = new ArgDef("db", false, false,
            "Set the DB controller to be used", ParamDef.stringInstance(
                    "db_controller", true,
                    "The name of the DB controller, default riak"));
    /**
     * Definition of the command line arguments for the key 'm'. Format:
     * <p>
     * -m properties_filename
     * <p>
     * Input the property file used by this application.
     */
    public static final ArgDef KEY_PROPERTIES_FILENAME = new ArgDef("m", false,
            false, "Set the property file to be used", ParamDef.stringInstance(
                    "properties_filename", true,
                    "The name of the property file"));

    /**
     * Definition of the command line argument for the key 'in'. Format:
     * <p>
     * -in <filename>
     * <p>
     * Input the property input file used by this application. The file is in a
     * CSV format with character '#' at the start to identify a comment row.
     */
    public static final ArgDef INPUT_FILE = new ArgDef("in", false, false,
            "Sets the input client's details file", ParamDef.stringInstance(
                    "filename", true, "The input client's details filename"));

    // Definitions
    /** The width of the dialog. */
    private static final PropertyDef<String> TITLE = PropertyDef
            .getPropertyDef("com.syncfree.dbclient.gui.solution_title",
                    "DB Client ({0})");
    /** The top node in the tree. */
    private static final PropertyDef<String> TOP = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.solution_top_node", "DB");

    // Application dimensions
    /** The width of the dialog. */
    private static final PropertyDef<Integer> WIDTH = PropertyDef
            .getPropertyDef("com.syncfree.dbclient.gui.solution_dialog_width",
                    856);
    /** The height of the window. */
    private static final PropertyDef<Integer> HEIGHT = PropertyDef
            .getPropertyDef("com.syncfree.dbclient.gui.solution_"
                    + "dialog_height", 400);
    /** The minimum width of the left pane. */
    private static final PropertyDef<Integer> MIN_WIDTH = PropertyDef
            .getPropertyDef("com.syncfree.dbclient.gui.solution_min_width", 100);
    /** The minimum width of the left pane. */
    private static final PropertyDef<Integer> HORIZONTAL_DIVIDER_LOCATION = PropertyDef
            .getPropertyDef(
                    "com.syncfree.dbclient.gui.solution_horizontal_devider_loc",
                    200);
    /** The minimum width of the left pane. */
    private static final PropertyDef<Integer> VERTICAL_DIVIDER_LOCATION = PropertyDef
            .getPropertyDef(
                    "com.syncfree.dbclient.gui.solution_vertical_devicer_loc",
                    200);

    // Actions
    /** The popupmenu text for the creation of a new client. */
    private static final String HELP = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.help", "Help").getProperty();
    /** The popupmenu text for the creation of a new client. */
    private static final String CREATE_CLIENT = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.solution_create_client", "Create Client")
            .getProperty();
    /** The popupmenu text for the creation of a new bucket. */
    private static final String CREATE_BUCKET = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.solution_create_bucket", "Create Bucket")
            .getProperty();
    /** The popupmenu text for the creation of a new tuple. */
    private static final String CREATE_TUPLE = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.solution_create_tuple", "Create Tuple")
            .getProperty();
    /** The popupmenu text for the creation of a new bucket. */
    private static final String DELETE_BUCKET = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.solution_delete_bucket", "Delete Bucket")
            .getProperty();
    /** The popupmenu text for the creation of a new tuple. */
    private static final String DELETE_TUPLE = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.solution_delete_tuple", "Delete Tuple")
            .getProperty();
    /** The popupmenu text for the creation of a new client. */
    private static final String REFRESH_CLIENT = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.refresh_client", "Refresh").getProperty();
    /** The popupmenu text for the creation of a new bucket. */
    private static final String REFRESH_BUCKET = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.refresh_bucket", "Refresh").getProperty();
    /** The popupmenu text for the connecting. */
    private static final String CONNECT_CLIENT = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.connect_client", "Connect").getProperty();
    public static final String SHUTDOWN_CLIENT = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.controller.riak.gui.title_client_shutdown",
            "Shutdown").getProperty();
    public static final String DISCONNECT_CLIENT = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.controller.riak.gui.title_client_disconnect",
            "Disconnect").getProperty();

    // Images
    /** The image in the tree for the root when open. */
    private static final String IMG_ROOT_OPEN = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.img_root_open", "images/rootOpen.gif")
            .getProperty();
    /** The image in the tree for the root when open. */
    private static final String IMG_ROOT_CLOSE = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.img_root_close", "images/rootClose.gif")
            .getProperty();
    /** The image in the tree for the client when open. */
    private static final String IMG_CLIENT_OPEN = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.img_client_open",
            "images/databaseOpen.gif").getProperty();
    /** The image in the tree for the client when open. */
    private static final String IMG_CLIENT_CLOSE = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.img_client_close",
            "images/databaseClose.gif").getProperty();
    /** The image in the tree for the bucket when open. */
    private static final String IMG_BUCKET_OPEN = PropertyDef
            .getPropertyDef("com.syncfree.dbclient.gui.img_bucket_open",
                    "images/bucketOpen.jpg").getProperty();
    /** The image in the tree for the bucket when open. */
    private static final String IMG_BUCKET_CLOSE = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.img_bucket_close",
            "images/bucketClose.jpg").getProperty();
    /** The image in the tree for the tuple when open. */
    private static final String IMG_TUPLE = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.img_tuple", "images/tuple.gif")
            .getProperty();

    /** The fix number of threads in the pool. */
    private static final int NUM_THREADS_POOL = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.number_threads_pool", 10).getProperty();

    /** The cursor used when enabled. */
    private static final Cursor CURSOR_ENABLED = Cursor
            .getPredefinedCursor(PropertyDef.getPropertyDef(
                    "com.syncfree.dbclient.gui.cursor_enabled",
                    Cursor.DEFAULT_CURSOR).getProperty());

    /** The cursor used when enabled. */
    private static final Cursor CURSOR_DISENABLED = Cursor
            .getPredefinedCursor(PropertyDef.getPropertyDef(
                    "com.syncfree.dbclient.gui.cursor_disenabled",
                    Cursor.WAIT_CURSOR).getProperty());

    // Menu
    private static final String MENU_FILE = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.menu_file", "File").getProperty();
    private static final String MENU_VIEW = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.menu_view", "View").getProperty();
    private static final String MENU_ACTIONS = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.menu_action", "Actions").getProperty();
    private static final String MENU_HELP = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.menu_help", "Help").getProperty();
    private static final String MENU_QUIT = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.menu_quit", "Quit").getProperty();
    private static final String MENU_LOG_VIEW = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.menu_log_view", "Log View").getProperty();

    private static final String MENU_HELP_ABOUT = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.menu_help_about", "About").getProperty();
    private static final String MENU_HELP_CONTENT = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.menu_help_content", "Content")
            .getProperty();

    // About
    private static final String ABOUT_NAME = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.about_name", "SyncFree").getProperty();
    private static final String ABOUT_IMG = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.about_img", (String) null).getProperty();
    private static final String ABOUT_TOOLTIP = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.about_tooltip", "About").getProperty();
    private static final String ABOUT_COPYRIGHT = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.about_copyright",
            "Copyright SyncFree 2014").getProperty();
    private static final String ABOUT_INFO = PropertyDef
            .getPropertyDef("com.syncfree.dbclient.gui.about_info",
                    "SyncFree project it is an European project supported by the EU...\n")
            .getProperty();

    // Help
    private static final String[] CONTENT_NAMES = { "DB Client Tool",
            "Introduction", "GUI Overview" };
    private static final String[] CONTENT_FILENAMES = {
            "data/help/dbClientTool.html", "data/help/introduction.html",
            "data/help/guiOverview.html" };
    private static final String CONTENT_TITLE = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.content_title", "SyncFree").getProperty();
    private static final String CONTENT_IMG = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.content_img", (String) null)
            .getProperty();

    // ID
    private static final String VIEW_ROOT = "view_root";
    private static final String VIEW_CLIENT = "view_client";
    private static final String VIEW_BUCKET = "view_bucket";
    private static final String VIEW_TUPLE = "view_tuple";

    private static final String IMG_ID_ROOT_OPEN = "img_root_open";
    private static final String IMG_ID_ROOT_CLOSE = "img_root_close";
    private static final String IMG_ID_CLIENT_OPEN = "img_client_open";
    private static final String IMG_ID_CLIENT_CLOSE = "img_client_close";
    private static final String IMG_ID_BUCKET_OPEN = "img_bucket_open";
    private static final String IMG_ID_BUCKET_CLOSE = "img_bucket_close";
    private static final String IMG_ID_TUPLE_OPEN = "img_tuple_open";
    private static final String IMG_ID_TUPLE_CLOSE = "img_tuple_close";

    // Actions
    private static final String ACTION_CREATE_CLIENT_DLG = "act_create_client_dlg";
    private static final String ACTION_CREATE_BUCKET_DLG = "act_create_bucket_dlg";
    private static final String ACTION_CREATE_TUPLE_DLG = "act_create_tuple_dlg";
    private static final String ACTION_DELETE_BUCKET = "act_delete_bucket";
    private static final String ACTION_DELETE_TUPLE = "act_delete_tuple";
    private static final String ACTION_REFRESH_CLIENT = "act_refresh_client";
    private static final String ACTION_REFRESH_BUCKET = "act_refresh_bucket";
    private static final String ACTION_CONNECT_CLIENT = "act_connect_client";
    private static final String ACTION_SHUTDOWN_CLIENT = "act_shutdown_client";
    private static final String ACTION_DISCONNECT_CLIENT = "act_disconnect_client";

    private static final String ACTION_TREE_HELP = "act_tree_help";
    private static final String ACTION_MENU_HELP_ABOUT = "act_menu_help_about";
    private static final String ACTION_MENU_HELP_CONTENT = "act_menu_help_content";

    /** The internal reference to the GUI controller. */
    private IController mController;
    private final boolean mShouldBeVisible = true;
    /** The processor of the actions. */
    private final Processor mProcessor;

    // GUI controls
    private final JPanel mCards;
    private final ClientView mClientView;
    private final BucketView mBucketView;
    private final TupleView mTupleView;
    private final JTree mTree;
    private final RootNode mTopTreeNode;
    private final JSplitPane mRightPane;
    private final LogGUIView mLogView;

    // The split pane location for the rightPane
    private int mDividerLocation;
    private JCheckBoxMenuItem mLogViewMenuItem;

    // The list of extra listeners
    private List<IExtrListener> mListExtrListeners;

    /**
     * The main application window.
     * 
     * @param controller
     *            the DB controller.
     * @param astrArgs
     *            the command line arguments.
     * @param logView
     *            the GUI of the log view.
     * @param input
     *            the input device to read clients from.
     * @throws IOException
     *             when failed to read client's from device.
     */
    public MainView(final IController controller, final String[] astrArgs,
            final LogView logView, final IInput input) throws IOException {
        super(MessageFormat.format(TITLE.getProperty(), controller.getID()));

        Verbose.debug("Entering MainView(String[])");

        this.mListExtrListeners = new ArrayList<>();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final MyTreeSelectionListener listener = new MyTreeSelectionListener();
        final JScrollPane treeView;

        buildMenu(astrArgs, listener);

        // Left side
        this.mTopTreeNode = new RootNode(TOP.getProperty());
        createNodes(this.mTopTreeNode);
        this.mTree = new JTree(this.mTopTreeNode);
        this.mTree.addMouseMotionListener(listener);
        this.mTree.setCellRenderer(new IconRenderer());
        this.mTree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
        this.mTree.addTreeSelectionListener(listener); // listen for when the
                                                       // selection changes
        this.mTree.setShowsRootHandles(true);
        this.mTree.addMouseListener(listener);

        treeView = new JScrollPane(this.mTree);

        // Right side
        final JPanel rootView;

        this.mCards = new JPanel(new CardLayout());
        rootView = new JPanel();
        this.mCards.add(rootView, VIEW_ROOT);
        this.mClientView = new ClientView();
        this.mCards.add(this.mClientView, VIEW_CLIENT);
        this.mBucketView = new BucketView();
        this.mCards.add(this.mBucketView, VIEW_BUCKET);
        this.mTupleView = new TupleView();
        this.mCards.add(this.mTupleView, VIEW_TUPLE);
        this.mLogView = buildLogView(astrArgs, listener, logView);
        this.mRightPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                this.mCards, this.mLogView);
        this.mRightPane.setOneTouchExpandable(true);
        this.mRightPane.setDividerLocation(HORIZONTAL_DIVIDER_LOCATION
                .getProperty());

        // Create a split pane with the two scroll panes in it
        final JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, treeView, this.mRightPane);

        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(VERTICAL_DIVIDER_LOCATION.getProperty());

        // Provide minimum sizes for the two components in the split pane
        final Dimension minimumSize = new Dimension(MIN_WIDTH.getProperty(),
                HEIGHT.getProperty());

        treeView.setMinimumSize(minimumSize);
        this.mRightPane.setMinimumSize(minimumSize);
        add(splitPane);
        addWindowListener(this);

        pack();
        setSize(WIDTH.getProperty(), HEIGHT.getProperty());
        this.mLogView.setAutoScrollBottom();

        this.mController = controller;
        this.mController.init(this);
        this.mProcessor = new Processor();
        if (input != null) {
            while (input.hasNext()) {
                final IClient client = input.next();

                this.mProcessor.createClient_(client);
            } // end while
        }

        // Centre on screen
        final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        final int x = (int) ((d.getWidth() - this.getWidth()) / 2);
        final int y = (int) ((d.getHeight() - this.getHeight()) / 2);

        setLocation(x, y);
        setEnabled(true);
        setVisible(true);

        Verbose.debug("Leaving MainView(Sring[])");
    } // Constructor ()

    @Override
    public void setEnabled(final boolean bEnabled) {
        super.setEnabled(bEnabled);

        final Cursor cursor = bEnabled ? CURSOR_ENABLED : CURSOR_DISENABLED;

        setCursor(cursor);
    } // setEnabled()

    // WindowListener implementation
    @Override
    public void windowOpened(final WindowEvent e) {
    } // windowOpened()

    @Override
    public void windowClosing(final WindowEvent e) {
        closeWindow();
    } // windowClosing()

    @Override
    public void windowClosed(final WindowEvent e) {
    } // windowClosed()

    @Override
    public void windowIconified(final WindowEvent e) {
    } // windowIconified()

    @Override
    public void windowDeiconified(final WindowEvent e) {
    } // windowDeiconified()

    @Override
    public void windowActivated(final WindowEvent e) {
    } // windowActivated()

    @Override
    public void windowDeactivated(final WindowEvent e) {
    } // windowDeactivated()
      // end WindowListener implementation

    /**
     * Called when exiting the application. This will request the connections to
     * database to stop and all the established connections to be closed.
     */
    public void closeWindow() {
        this.mProcessor.close();
    } // closeWindow()

    /**
     * Builds the static nodes.
     *
     * @param topTreeNode
     *            the top node in the tree.
     */
    private void createNodes(final DefaultMutableTreeNode topTreeNode) {
        // None for now
    } // createNodes()

    /**
     * Builds the full GUI log view.
     * 
     * @param astrArgs
     *            the command line arguments.
     * @param listener
     *            the action listener.
     * @param logView
     *            the GUI log view.
     * @return the built log view.
     */
    private LogGUIView buildLogView(final String[] astrArgs,
            final ActionListener listener, LogView logView) {
        final LogGUIView logGuiView = new LogGUIView(logView);

        logGuiView.setActionListener(listener);

        return logGuiView;
    } // buildLogView()

    /**
     * Builds and adds the menu to the GUI.
     * 
     * @param astrArgs
     *            the command line arguments.
     */
    private void buildMenu(final String[] astrArgs,
            final ActionListener listener) {
        Verbose.debug("Entering buildMenu(String[])");

        final JMenuBar menuBar = new JMenuBar();
        final JMenu fileMenu = new JMenu(MENU_FILE);
        final JMenu viewMenu = new JMenu(MENU_VIEW);
        final JMenu actionMenu = new JMenu(MENU_ACTIONS);
        final JMenu helpMenu = new JMenu(MENU_HELP);
        final JMenuItem quitMenuItem = new JMenuItem(MENU_QUIT);
        JMenuItem item;

        this.mLogViewMenuItem = new JCheckBoxMenuItem(MENU_LOG_VIEW);
        quitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent ae) {
                Verbose.info("Exited!");

                // Quits the application when invoked.
                System.exit(0);
            } // actionPerformed()
        });
        quitMenuItem.setMnemonic(KeyEvent.VK_Q);
        fileMenu.add(quitMenuItem);
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);

        this.mLogViewMenuItem.setSelected(true);
        this.mLogViewMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent ae) {
                final JCheckBoxMenuItem logViewMenuItem = (JCheckBoxMenuItem) ae
                        .getSource();

                if (logViewMenuItem.isSelected()) {
                    showLogView();
                } else {
                    hideLogView();
                }
            } // actionPerformed()
        });
        viewMenu.add(this.mLogViewMenuItem);
        menuBar.add(viewMenu);

        actionMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(final MenuEvent me) {
                Verbose.info("Selected \"{0}\" menu", MENU_ACTIONS);
                actions(actionMenu, listener);
            } // menuSelected()

            @Override
            public void menuDeselected(final MenuEvent me) {
                actionMenu.removeAll();
            } // menuDeselected()

            @Override
            public void menuCanceled(final MenuEvent me) {
            } // menuCanceled()
        });
        menuBar.add(actionMenu);

        item = new JMenuItem(MENU_HELP_ABOUT);
        item.setActionCommand(ACTION_MENU_HELP_ABOUT);
        item.addActionListener(listener);
        helpMenu.add(item);
        item = new JMenuItem(MENU_HELP_CONTENT);
        item.setActionCommand(ACTION_MENU_HELP_CONTENT);
        item.addActionListener(listener);
        helpMenu.add(item);
        menuBar.add(helpMenu);

        this.setJMenuBar(menuBar);

        Verbose.debug("Exiting buildMenu(String[])");
    } // buildMenu()

    /**
     * Hides the Log View.
     */
    private void hideLogView() {
        Verbose.debug("Hide Log View");
        this.mDividerLocation = MainView.this.mRightPane.getDividerLocation();
        this.mRightPane.remove(MainView.this.mLogView);
    } // hideLogView()

    /**
     * Shows the Log View.
     */
    private void showLogView() {
        Verbose.debug("Show Log View");
        this.mRightPane.add(MainView.this.mLogView);
        this.mRightPane.setDividerLocation(this.mDividerLocation);
    } // showLogView()

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
     * Builds and writes the help into the log devices.
     */
    public static void help() {
        Verbose.log(Level.OFF, "-----------------------------------");
        Verbose.log(Level.OFF, "Help");
        Verbose.log(Level.OFF, "====");

        helpHeader();
        Verbose.log(Level.OFF, "\n");

        helpBody();
        Verbose.log(Level.OFF, "-----------------------------------");
    } // help()

    // IView ======
    @Override
    public TreePath getSelectionPath() {
        return MainView.this.mTree.getSelectionPath();
    } // getSelectionPath()

    @Override
    public JFrame getFrame() {
        return this;
    } // getFrame()

    @Override
    public void setExtrListener(final IExtrListener listener) {
        if (!this.mListExtrListeners.contains(listener)) {
            this.mListExtrListeners.add(listener);
        }
    } // setExtrListener()

    @Override
    public void setEnable(final IClient client, boolean bEnable) {
        final ClientNode clientNode = getNode(client);

        clientNode.setEnabled(bEnable);
    } // setEnable)

    @Override
    public void setEnable(final IBucket bucket, final boolean bEnable) {
        final BucketNode bucketNode = getNode(bucket);

        bucketNode.setEnabled(bEnable);
    } // setEnable()

    @Override
    public void setEnable(final ITuple tuple, boolean bEnable) {
        final TupleNode tupleNode = getNode(tuple);

        tupleNode.setEnabled(bEnable);
    } // setEnable()
      // End IView ==

    /**
     * Finds the node with the specified name within the passed parent node.
     * 
     * @param parent
     *            the parent node in the tree.
     * @param strName
     *            the name of the node in the specified parent node.
     * @return the node with the specified name within the specified parent
     *         node, ir any, or null otherwise.
     */
    protected DefaultMutableTreeNode getNode(
            final DefaultMutableTreeNode parent, final String strName) {
        for (int i = 0; i < parent.getChildCount(); ++i) {
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent
                    .getChildAt(i);

            if (node.toString().equals(strName)) {
                return node;
            }
        } // end while

        return null;
    } // getNode()

    /**
     * Searches for the tree view client node for the specified client.
     * 
     * @param client
     *            the client's details.
     * @return the tree view client node for the specified client.
     */
    protected ClientNode getNode(final IClient client) {
        return (ClientNode) getNode(this.mTopTreeNode, client.getClientName());
    } // getNode()

    /**
     * Searches for the tree view bucket node for the specified bucket.
     * 
     * @param bucket
     *            the details of the bucket.
     * @return the tree view bucket node for the specified bucket.
     */
    protected BucketNode getNode(final IBucket bucket) {
        final DefaultMutableTreeNode clientNode = getNode(this.mTopTreeNode,
                bucket.getBucketParentName());

        return (BucketNode) getNode(clientNode, bucket.getBucketName());
    } // getNode()

    /**
     * Searches for the tree view tuple node for the specified tuple.
     * 
     * @param tuple
     *            the tuple.
     * @return the tree view tuple node for the specified tuple.
     */
    protected TupleNode getNode(final ITuple tuple) {
        final DefaultMutableTreeNode bucketNode = getNode(tuple
                .getTupleParent());

        return (TupleNode) getNode(bucketNode, tuple.getKeyName());
    } // getNode()

    /**
     * Builds the header for the help.
     */
    private static void helpHeader() {
        final StringBuilder buf = new StringBuilder();

        buf.append(DBGuiTool.class.getSimpleName());
        buf.append(" ");
        ArgDef.ARGDEF_LOGGING_LEVEL.helpHeader(buf);
        buf.append(" [");
        KEY_PROPERTIES_FILENAME.helpHeader(buf);
        buf.append("] [");
        KEY_DB.helpHeader(buf);
        buf.append("] [");
        INPUT_FILE.helpHeader(buf);
        buf.append("]");

        Verbose.log(Level.OFF, buf.toString());
    } // helpHeader()

    /**
     * Builds the body of the help.
     */
    private static void helpBody() {
        final StringBuilder buf = new StringBuilder();

        ArgDef.ARGDEF_LOGGING_LEVEL.helpBody(buf);
        KEY_PROPERTIES_FILENAME.helpBody(buf);
        KEY_DB.helpBody(buf);
        INPUT_FILE.helpBody(buf);

        Verbose.log(Level.OFF, buf.toString());
    } // helpBody()

    /**
     * Builds the popup menu used when on top of the tree or when selecting
     * "Actions" on the main menu.
     * 
     * @param popupMenu
     *            the popup menu component.
     * @param listener
     *            the action listener for the popup menu items.
     */
    private void actions(final JComponent popupMenu,
            final ActionListener listener) {
        final TreePath path = MainView.this.mTree.getSelectionPath();

        if (path == null) {
            return;
        }

        final Object o = path.getLastPathComponent();
        DefaultMutableTreeNode node = null;
        boolean isEnabled = false;
        boolean isConnected = false;
        JMenuItem item;

        if (o instanceof RootNode) {
            // Add client
            final RootNode rootNode = (RootNode) o;

            isEnabled = rootNode.isEnabled();

            item = new JMenuItem(HELP);
            item.setActionCommand(ACTION_TREE_HELP);
            item.addActionListener(listener);
            popupMenu.add(item);

            popupMenu.add(new JPopupMenu.Separator());

            item = new JMenuItem(CREATE_CLIENT);
            item.setActionCommand(ACTION_CREATE_CLIENT_DLG);
            item.addActionListener(listener);
            item.setEnabled(isEnabled);
            popupMenu.add(item);
        } else if (o instanceof ClientNode) {
            // Add bucket
            final ClientNode clientNode = (ClientNode) o;
            final IClient c = MainView.this.mController.getClient(clientNode
                    .getClientName());

            node = clientNode;
            isEnabled = clientNode.isEnabled();
            isConnected = (c == null ? false : (isEnabled && c.isConnected()));

            item = new JMenuItem(HELP);
            item.setActionCommand(ACTION_TREE_HELP);
            item.addActionListener(listener);
            popupMenu.add(item);

            popupMenu.add(new JPopupMenu.Separator());

            item = new JMenuItem(CONNECT_CLIENT);
            item.setActionCommand(ACTION_CONNECT_CLIENT);
            item.addActionListener(listener);
            item.setEnabled(!isConnected);
            popupMenu.add(item);

            item = new JMenuItem(DISCONNECT_CLIENT);
            item.setActionCommand(ACTION_DISCONNECT_CLIENT);
            item.addActionListener(listener);
            item.setEnabled(isConnected);
            popupMenu.add(item);

            item = new JMenuItem(SHUTDOWN_CLIENT);
            item.setActionCommand(ACTION_SHUTDOWN_CLIENT);
            item.addActionListener(listener);
            item.setEnabled(isConnected);
            popupMenu.add(item);

            popupMenu.add(new JPopupMenu.Separator());

            item = new JMenuItem(CREATE_BUCKET);
            item.setActionCommand(ACTION_CREATE_BUCKET_DLG);
            item.addActionListener(listener);
            item.setEnabled(isConnected);
            popupMenu.add(item);

            item = new JMenuItem(REFRESH_CLIENT);
            item.setActionCommand(ACTION_REFRESH_CLIENT);
            item.addActionListener(listener);
            item.setEnabled(isConnected);
            popupMenu.add(item);
        } else if (o instanceof BucketNode) {
            // Bucket popupmenu
            final BucketNode bucketNode = (BucketNode) o;

            node = bucketNode;
            isEnabled = bucketNode.isEnabled();
            isConnected = (isEnabled && bucketNode.isConnected());

            item = new JMenuItem(HELP);
            item.setActionCommand(ACTION_TREE_HELP);
            item.addActionListener(listener);
            popupMenu.add(item);

            popupMenu.add(new JPopupMenu.Separator());

            item = new JMenuItem(CREATE_TUPLE);
            item.setActionCommand(ACTION_CREATE_TUPLE_DLG);
            item.addActionListener(listener);
            item.setEnabled(isConnected);
            popupMenu.add(item);

            if (MainView.this.mController.isAllowedToDeleteBuckets()) {
                item = new JMenuItem(DELETE_BUCKET);
                item.setActionCommand(ACTION_DELETE_BUCKET);
                item.addActionListener(listener);
                item.setEnabled(isConnected);
                popupMenu.add(item);
            }

            popupMenu.add(new JPopupMenu.Separator());

            item = new JMenuItem(REFRESH_BUCKET);
            item.setActionCommand(ACTION_REFRESH_BUCKET);
            item.addActionListener(listener);
            item.setEnabled(isConnected);
            popupMenu.add(item);
        } else if (o instanceof TupleNode) {
            // Tuple popupmenu
            final TupleNode tupleNode = (TupleNode) o;

            node = tupleNode;
            isEnabled = tupleNode.isEnabled();
            isConnected = (isEnabled && tupleNode.isConnected());

            item = new JMenuItem(HELP);
            item.setActionCommand(ACTION_TREE_HELP);
            item.addActionListener(listener);
            popupMenu.add(item);

            popupMenu.add(new JPopupMenu.Separator());

            item = new JMenuItem(DELETE_TUPLE);
            item.setActionCommand(ACTION_DELETE_TUPLE);
            item.addActionListener(listener);
            item.setEnabled(isConnected);
            popupMenu.add(item);
        }

        if (node != null) {
            // Extra listener
            for (final IExtrListener l : this.mListExtrListeners) {
                final JMenuItem[] aMenuItems;

                if (node instanceof IClient) {
                    aMenuItems = l.getMenuItems((IClient) node, isConnected);
                } else if (node instanceof IBucket) {
                    aMenuItems = l.getMenuItems((IBucket) node, isConnected);
                } else {
                    aMenuItems = l.getMenuItems((ITuple) node, isConnected);
                }

                if (aMenuItems != null && aMenuItems.length > 0) {
                    popupMenu.add(new JPopupMenu.Separator());
                    for (JMenuItem it : aMenuItems) {
                        // it.setEnabled(isEnabled);
                        popupMenu.add(it);
                    }
                }
            } // end for
        }
    } // actions()

    /**
     * The control for some of the GUI controls operation.
     *
     * @author aas
     */
    class MyTreeSelectionListener implements TreeSelectionListener,
            MouseListener, ActionListener, MouseMotionListener {
        /** Auxiliary dialog window to input the appropriate values. */
        private JDialog dialog;

        @Override
        public void actionPerformed(final ActionEvent ae) {
            final String strAction = ae.getActionCommand();

            if (strAction.equals(LogGUIView.ACTION_CLEAR)) {
                return;
            }

            Verbose.debug("Called action \"{0}\"", strAction);
            if (strAction == ACTION_CREATE_CLIENT_DLG) {
                if (!(this.dialog instanceof NewClientDialog)) {
                    this.dialog = new NewClientDialog(MainView.this, this);
                } else {
                    ((NewClientDialog) this.dialog).reset();
                }
                this.dialog.setVisible(true);
            } else if (strAction == ACTION_CREATE_BUCKET_DLG) {
                final TreePath path = MainView.this.mTree.getSelectionPath();
                final ClientNode node = (ClientNode) path
                        .getLastPathComponent();

                if (!(this.dialog instanceof NewBucketDialog)) {
                    this.dialog = new NewBucketDialog(MainView.this, this,
                            node.getClientName());
                } else {
                    ((NewBucketDialog) this.dialog).reset();
                }
                this.dialog.setVisible(true);
            } else if (strAction == ACTION_CREATE_TUPLE_DLG) {
                final TreePath path = MainView.this.mTree.getSelectionPath();
                final BucketNode node = (BucketNode) path
                        .getLastPathComponent();

                if (!(this.dialog instanceof NewTupleDialog)) {
                    this.dialog = new NewTupleDialog(MainView.this, this, node);
                } else {
                    ((NewTupleDialog) this.dialog).reset(node);
                }
                this.dialog.setVisible(true);
            } else if (strAction.equals(LogGUIView.ACTION_CLOSE)) {
                hideLogView();
                MainView.this.mLogViewMenuItem.setSelected(false);
            } else if (strAction == NewClientDialog.ACTION_CREATE_CLIENT) {
                createClient();
            } else if (strAction == ACTION_REFRESH_CLIENT) {
                final TreePath path = MainView.this.mTree.getSelectionPath();
                final ClientNode node = (ClientNode) path
                        .getLastPathComponent();

                refreshClient(node);
            } else if (strAction == NewBucketDialog.ACTION_CREATE_BUCKET) {
                createBucket();
            } else if (strAction == ACTION_DELETE_BUCKET) {
                deleteBucket();
            } else if (strAction == ACTION_REFRESH_BUCKET) {
                final TreePath path = MainView.this.mTree.getSelectionPath();
                final BucketNode node = (BucketNode) path
                        .getLastPathComponent();

                refreshBucket(node);
            } else if (strAction == NewTupleDialog.ACTION_CREATE_TUPLE) {
                createTuple();
            } else if (strAction == ACTION_DELETE_TUPLE) {
                deleteTuple();
            } else if (strAction == ACTION_CONNECT_CLIENT) {
                final TreePath path = MainView.this.mTree.getSelectionPath();
                final ClientNode clientNode = (ClientNode) path
                        .getLastPathComponent();

                MainView.this.mProcessor.connect(clientNode);
                Verbose.info("Completed connection of \"{0}\"",
                        clientNode.getClientName());
            } else if (ACTION_SHUTDOWN_CLIENT.equals(strAction)) {
                final TreePath path = MainView.this.mTree.getSelectionPath();
                final ClientNode clientNode = (ClientNode) path
                        .getLastPathComponent();

                MainView.this.mProcessor.shutdown(clientNode);
                Verbose.info("Completed shutdown of \"{0}\"",
                        clientNode.getClientName());
            } else if (ACTION_DISCONNECT_CLIENT.equals(strAction)) {
                final TreePath path = MainView.this.mTree.getSelectionPath();
                final ClientNode clientNode = (ClientNode) path
                        .getLastPathComponent();

                MainView.this.mProcessor.disconnect(clientNode);
                Verbose.info("Completed disconnection of \"{0}\"",
                        clientNode.getClientName());
            } else if (strAction.equals(LogGUIView.ACTION_HELP)) {
                help();
            } else if (strAction.equals(ACTION_MENU_HELP_ABOUT)) {
                final AboutDlg about = new AboutDlg(MainView.this, ABOUT_NAME,
                        ABOUT_IMG, ABOUT_TOOLTIP, ABOUT_COPYRIGHT, ABOUT_INFO);

                about.setVisible(true);
            } else if (strAction.equals(ACTION_MENU_HELP_CONTENT)) {
                // Title, Frame, Contents Names, Contents Filenames
                final HelpDlg help = new HelpDlg(CONTENT_TITLE, MainView.this,
                        CONTENT_NAMES, CONTENT_FILENAMES, CONTENT_IMG);

                help.setVisible(true);
            } else if (strAction.equals(ACTION_TREE_HELP)) {
                final TreePath path = MainView.this.mTree.getSelectionPath();
                final Object node = path.getLastPathComponent();
                final String strFilename;

                if (node instanceof RootNode) {
                    strFilename = CONTENT_FILENAMES[2] + "?rootNode";
                } else if (node instanceof ClientNode) {
                    strFilename = CONTENT_FILENAMES[2] + "?clientNode";
                } else if (node instanceof BucketNode) {
                    strFilename = CONTENT_FILENAMES[2] + "?bucketNode";
                } else {
                    strFilename = CONTENT_FILENAMES[2] + "?tupleNode";
                }

                // Title, Frame, Contents Names, Contents Filenames
                final HelpDlg help = new HelpDlg(CONTENT_TITLE, MainView.this,
                        CONTENT_NAMES, CONTENT_FILENAMES, CONTENT_IMG);

                help.setPage(strFilename);
            } else {
                MainView.this.mProcessor.actionPerformed(ae);
            }
            Verbose.debug("Back from action \"{0}\"", strAction);
        } // actionPerformed()

        @Override
        public void valueChanged(final TreeSelectionEvent tse) {
            // Returns the last path element of the selection.
            // This method is useful only when the selection model allows a
            // single selection.
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) MainView.this.mTree
                    .getLastSelectedPathComponent();

            if (node == null) {
                // Nothing is selected.
                return;
            }

            if (node == MainView.this.mTopTreeNode) {
                showView(VIEW_ROOT);
            } else if (node instanceof ClientNode) {
                final IClient client = MainView.this.mController
                        .getClient(((ClientNode) node).getClientName());

                MainView.this.mClientView.update(client);
                showView(VIEW_CLIENT);
            } else if (node instanceof BucketNode) {
                final IBucket bucket = MainView.this.mController
                        .getBucket((BucketNode) node);

                MainView.this.mBucketView.update(bucket);
                showView(VIEW_BUCKET);
            } else if (node instanceof TupleNode) {
                final ITuple tuple = MainView.this.mController
                        .getTuple((TupleNode) node);

                try {
                    MainView.this.mTupleView.update(tuple);
                    showView(VIEW_TUPLE);
                } catch (final DBException dbe) {
                    Verbose.log(
                            dbe,
                            "Failed to retrieve the value for the key \"{0}\" in buckte \"{1}\" using client \"{2}\"; {3}",
                            tuple.getKeyName(), tuple.getTupleParentName(),
                            tuple.getTupleParent().getBucketParentName(), dbe);
                } // end try
            }
        } // valueChanged()

        @Override
        public void mouseClicked(final MouseEvent me) {
            if (SwingUtilities.isRightMouseButton(me)) {
                final int row = MainView.this.mTree.getClosestRowForLocation(
                        me.getX(), me.getY());
                final JPopupMenu popupMenu = new JPopupMenu();

                MainView.this.mTree.setSelectionRow(row);
                MainView.this.actions(popupMenu, this);
                popupMenu.show(me.getComponent(), me.getX(), me.getY());
            }
        } // mouseClicked()

        @Override
        public void mousePressed(final MouseEvent me) {
        } // mousePressed()

        @Override
        public void mouseReleased(final MouseEvent me) {
        } // mouseReleased()

        @Override
        public void mouseEntered(final MouseEvent me) {
        } // mouseEntered()

        @Override
        public void mouseExited(final MouseEvent me) {
            MainView.this.mTree.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } // mouseExited()

        // MouseMotionListener
        @Override
        public void mouseDragged(final MouseEvent me) {
        } // mouseDragged()

        @Override
        public void mouseMoved(final MouseEvent me) {
            // Set correct mouse icon
            final TreePath path = MainView.this.mTree.getPathForLocation(
                    me.getX(), me.getY());

            if (path != null) {
                final AllowEnable node = (AllowEnable) path
                        .getLastPathComponent();

                if (node.isEnabled()) {
                    MainView.this.mTree.setCursor(CURSOR_ENABLED);
                } else {
                    MainView.this.mTree.setCursor(CURSOR_DISENABLED);
                }
            } else {
                MainView.this.mTree.setCursor(CURSOR_ENABLED);
            }
        } // mouseMoved()
          // end MouseMotionListener

        /**
         * Obtains all the buckets for the client.
         * 
         * @param clientNode
         *            the bucket node in the GUI tree view.
         */
        protected void refreshClient(final ClientNode clientNode) {
            MainView.this.mProcessor.refreshClient(clientNode);
        } // refreshClient()

        /**
         * Obtains all the buckets for the client.
         * 
         * @param bucketNode
         *            the bucket node in the GUI tree view.
         */
        protected void refreshBucket(final BucketNode bucketNode) {
            MainView.this.mProcessor.refreshBucket(bucketNode);
        } // refreshBucket()

        /**
         * Creates a tuple from values in the client dialog.
         */
        protected void createClient() {
            if (this.dialog instanceof NewClientDialog) {
                Verbose.info("Create DB client '"
                        + ((NewClientDialog) this.dialog).getClientName() + "'");
                MainView.this.mProcessor
                        .createClient(((NewClientDialog) this.dialog).getData());
            } else {
                Verbose.warning("Invalid action '"
                        + NewClientDialog.ACTION_CREATE_CLIENT + "'");
            }
        } // createClient()

        /**
         * Creates a tuple from values in the bucket dialog.
         */
        protected void createBucket() {
            if (this.dialog instanceof NewBucketDialog) {
                Verbose.info("Create DB bucket");
                MainView.this.mProcessor
                        .createBucket(((NewBucketDialog) this.dialog).getData());
            } else {
                Verbose.warning("Invalid action '"
                        + NewBucketDialog.ACTION_CREATE_BUCKET + "'");
            }
        } // createBucket()

        /**
         * Creates a tuple from values in the tuple dialog.
         */
        protected void createTuple() {
            if (this.dialog instanceof NewTupleDialog) {
                Verbose.info("Create DB tuple");
                MainView.this.mProcessor
                        .createTuple(((NewTupleDialog) this.dialog).getData());
            } else {
                Verbose.warning("Invalid action '"
                        + NewBucketDialog.ACTION_CREATE_BUCKET + "'");
            }
        } // createTuple()

        /**
         * Deletes the current selected bucket.
         */
        protected void deleteBucket() {
            final TreePath path = MainView.this.mTree.getSelectionPath();
            final BucketNode bucketNode = (BucketNode) path
                    .getLastPathComponent();

            MainView.this.mProcessor.removeBucket(bucketNode);
        } // deleteBucket()

        /**
         * Deletes the current selected tuple.
         */
        protected void deleteTuple() {
            final TreePath path = MainView.this.mTree.getSelectionPath();
            final TupleNode tupleNode = (TupleNode) path.getLastPathComponent();

            MainView.this.mProcessor.removeTuple(tupleNode);
        } // deleteTuple()
    } // end class MyTreeSelectionListener

    /**
     * Controls how the node are represented on the tree.
     * 
     * @author aas
     */
    class IconRenderer extends DefaultTreeCellRenderer {
        /**
         * A version number for this class so that serialisation can occur
         * without worrying about the underlying class changing between
         * serialisation and deserialisation.
         * <p>
         *
         * Not that we ever serialise this class of course, but
         * DefaultTreeCellRenderer implements Serializable, so therefore by
         * default we do as well.
         */
        private static final long serialVersionUID = 450859556901465887L;

        @Override
        public Component getTreeCellRendererComponent(final JTree tree,
                final Object value, final boolean sel, final boolean expanded,
                final boolean leaf, final int row, final boolean hasFocus) {
            final DefaultMutableTreeNode node;
            final ImageIcon icon;

            // Start with default behaviour
            super.getTreeCellRendererComponent(tree, value, sel, expanded,
                    hasFocus, row, hasFocus);

            // Customise based on local conditions/state
            node = (DefaultMutableTreeNode) value;
            if (node == MainView.this.mTopTreeNode) {
                // Root
                if (expanded) {
                    // Open
                    icon = ImageManager.getImage(IMG_ID_ROOT_OPEN,
                            IMG_ROOT_OPEN);
                } else {
                    // Close
                    icon = ImageManager.getImage(IMG_ID_ROOT_CLOSE,
                            IMG_ROOT_CLOSE);
                }
            } else if (node instanceof ClientNode) {
                // Client
                final ClientNode clientNode = (ClientNode) node;

                if (expanded) {
                    // Open
                    icon = ImageManager.getImage(IMG_ID_CLIENT_OPEN,
                            IMG_CLIENT_OPEN);
                } else {
                    // Close
                    icon = ImageManager.getImage(
                            IMG_ID_CLIENT_CLOSE
                                    + (clientNode.isConnected() ? "_connected"
                                            : ""),
                            buidIconName(IMG_CLIENT_CLOSE,
                                    clientNode.isConnected()));
                }
            } else if (node instanceof BucketNode) {
                // Bucket
                if (expanded) {
                    // Open
                    icon = ImageManager.getImage(IMG_ID_BUCKET_OPEN,
                            IMG_BUCKET_OPEN);
                } else {
                    // Close
                    icon = ImageManager.getImage(IMG_ID_BUCKET_CLOSE,
                            IMG_BUCKET_CLOSE);
                }
            } else if (node instanceof TupleNode) {
                // Tuple
                if (expanded) {
                    // Open
                    icon = ImageManager.getImage(IMG_ID_TUPLE_OPEN, IMG_TUPLE);
                } else {
                    // Close
                    icon = ImageManager.getImage(IMG_ID_TUPLE_CLOSE, IMG_TUPLE);
                }
            } else {
                Verbose.warning("Invalid node; unrecognised node in tree");

                return this;
            }
            setIcon(icon);

            return this;
        } // getTreeCellRendererComponent()

        private String buidIconName(String strImgName, boolean bConnected) {
            final int iIndex = strImgName.lastIndexOf('.');

            if (iIndex != -1) {
                if (bConnected) {
                    strImgName = strImgName.substring(0, iIndex) + "Connected"
                            + strImgName.substring(iIndex);
                }
            }

            return strImgName;
        } // buidIconName()

        /**
         * Add this override to recalculate the width of this JLabel. The super
         * class default behaviour miscalculates the width, and so the '...' can
         * appear. Instead, we 'simulate' the FontMetrics' stringWidth() method,
         * by using charWidth(), plus some initialisation and padding.
         * <p>
         * Solution from https://community.oracle.com/thread/1362316?tstart=0
         */
        @Override
        public Dimension getPreferredSize() {
            final Dimension dim = super.getPreferredSize();
            final FontMetrics fm = getFontMetrics(getFont());
            final char[] chars = getText().toCharArray();
            /*
             * Initialise the width value to take into account any icons and
             * gaps. Don't try to get the Icon's width programmatically from
             * within this method, as an infinite loop will result. I've just
             * assumed a width of 16 here, but you could (should?) save the
             * actual value in getTreeCellRendererComponent() and retrieve it
             * here, if you wanted.
             */
            int w = getIconTextGap() + 16;

            for (final char ch : chars) { // change to old style for pre-JDK 5
                w += fm.charWidth(ch);
            } // end for
            w += getText().length();
            dim.width = w;

            return dim;
        } // getPreferredSize()
    } // end class IconRenderer

    /**
     * Support for the standard actions and delegate the special ones, both of
     * which are executed in their own threads.
     * 
     * @author aas
     */
    class Processor {
        private final ExecutorService mPoolThreads;

        Processor() {
            this.mPoolThreads = Executors.newFixedThreadPool(NUM_THREADS_POOL);
        } // Constructor ()

        /**
         * Executes the action for each listeners in their particular thread.
         * 
         * @param ae
         *            the action.
         */
        public void actionPerformed(final ActionEvent ae) {
            MainView.this.setEnabled(false);

            // Extra listener
            for (final ActionListener action : MainView.this.mListExtrListeners) {
                this.mPoolThreads.execute(new Runnable() {
                    @Override
                    public void run() {
                        action.actionPerformed(ae);

                        // TODO: maybe only the last one running of the list
                        // should be enable the main view
                        MainView.this.setEnabled(true);
                    } // run()
                });
            } // end for
        } // actionPerformed()

        /**
         * Connects to the DB with the specified client's details.
         * 
         * @param clientNode
         *            the node that represents the client.
         */
        public void connect(final ClientNode clientNode) {
            if (clientNode == null) {
                Verbose.warning("Failed to connect the client;no client specified");

                return;
            }
            clientNode.setEnabled(false);

            this.mPoolThreads.execute(new Runnable() {
                @Override
                public void run() {
                    connect_(clientNode);
                } // run()
            });
        } // connect()

        /**
         * .
         * 
         * @param clientNode
         *            .
         */
        public void shutdown(final ClientNode clientNode) {
            if (clientNode == null) {
                Verbose.warning("Failed to shutdown the client; no client specified");

                return;
            }
            clientNode.setEnabled(false);

            this.mPoolThreads.execute(new Runnable() {
                @Override
                public void run() {
                    shutdown_(clientNode);
                } // run()
            });
        } // shutdown()

        /**
         * .
         * 
         * @param clientNode
         *            .
         */
        public void disconnect(final ClientNode clientNode) {
            if (clientNode == null) {
                Verbose.warning("Failed to disconnect the client; no client specified");

                return;
            }
            clientNode.setEnabled(false);

            this.mPoolThreads.execute(new Runnable() {
                @Override
                public void run() {
                    disconnect_(clientNode);
                } // run()
            });
        } // disconnect()

        /**
         * Connects to the DB with the specified client's details.
         * 
         * @param strClientName
         *            the client's name.
         */
        public void connect(final String strClientName) {
            final ClientNode clientNode;

            clientNode = (ClientNode) getNode(MainView.this.mTopTreeNode,
                    strClientName);
            if (clientNode == null) {
                Verbose.warning(
                        "Failed to retrieve the node for client \"{0}\"; unable to connect using client \"{0}\"",
                        strClientName);

                return;
            }
            connect(clientNode);
        } // connect()

        /**
         * Closes the connections and exists.
         */
        public void close() {
            if (MainView.this.mController != null) {
                MainView.this.mController.close();
            }
            System.exit(0);
        } // close()

        /**
         * Creates a client with the specified tuple. The execution is run in a
         * different thread.
         * 
         * @param inputClient
         *            the client's details.
         */
        public void createClient(final IClient inputClient) {
            this.mPoolThreads.execute(new Runnable() {
                @Override
                public void run() {
                    createClient_(inputClient);
                } // run()
            });
        } // createClient()

        /**
         * Creates a bucket with the specified tuple. The execution is run in a
         * different thread.
         * 
         * @param inputBucket
         *            the bucket's details.
         */
        public void createBucket(final IBucket inputBucket) {
            this.mPoolThreads.execute(new Runnable() {
                @Override
                public void run() {
                    createBucket_(inputBucket);
                } // run()
            });
        } // createBucket()

        /**
         * Creates a tuple with the specified tuple. The execution is run in a
         * different thread.
         * 
         * @param inputTuple
         *            the tuple's details.
         */
        public void createTuple(final ITuple inputTuple) {
            this.mPoolThreads.execute(new Runnable() {
                @Override
                public void run() {
                    createTuple_(inputTuple);
                } // run()
            });
        } // createTuple()

        /**
         * Retrieves the current buckets from the DB for the specified client
         * and updates view.
         * 
         * @param client
         *            the client.
         */
        public void refreshClient(final IClient client) {
            // Disable node until completed operation
            final ClientNode clientNade = getNode(client);

            if (clientNade == null) {
                Verbose.warning(
                        "Failed to retrieve the node for client \"{0}\"; unable to get all the buckets using client \"{0}\"",
                        client.getClientName());

                return;
            }
            clientNade.setEnabled(false);

            this.mPoolThreads.execute(new Runnable() {
                @Override
                public void run() {
                    refreshClient_(clientNade);
                } // run()
            });
        } // refreshClient()

        /**
         * Retrieves the current tuples from the DB for the specified bucket and
         * updates view.
         * 
         * @param bucket
         *            the bucket.
         */
        public void refreshBucket(final IBucket bucket) {
            // Disable node until completed operation
            // This may need to be moved into refreshBucket_(BucketNode) if the
            // number of tuples can be big. Maybe disable the bucket and then in
            // the new execution thread disable already existing tuples
            final BucketNode bucketNade = getNode(bucket);

            if (bucketNade == null) {
                return;
            }
            bucketNade.setEnabled(false);

            this.mPoolThreads.execute(new Runnable() {
                @Override
                public void run() {
                    refreshBucket_(bucketNade);
                } // run()
            });
        } // refreshBucket()

        /**
         * Removes the specified client from the DB and updates the view.
         * 
         * @param clientNode
         *            the client.
         */
        public void removeClient(final ClientNode clientNode) {
            if (clientNode == null) {
                // Should not happen
                Verbose.error("No DB to remove");

                return;
            }

            // Disable node until completed operation
            clientNode.setEnabled(false);

            this.mPoolThreads.execute(new Runnable() {
                @Override
                public void run() {
                    removeClient_(clientNode);
                } // run()
            });
        } // removeClient()

        /**
         * Removes the specified bucket from the DB and updates the view.
         * 
         * @param bucketNode
         *            the bucket.
         */
        public void removeBucket(final BucketNode bucketNode) {
            if (bucketNode == null) {
                // Should not happen
                Verbose.error("No bucket to remove");

                return;
            }

            // Disable node until completed operation
            bucketNode.setEnabled(false);

            this.mPoolThreads.execute(new Runnable() {
                @Override
                public void run() {
                    removeBucket_(bucketNode);
                } // run()
            });
        } // removeBucket()

        /**
         * Removes the specified tuple from the DB and updates the view.
         * 
         * @param tupleNode
         *            the tuple.
         */
        public void removeTuple(final TupleNode tupleNode) {
            if (tupleNode == null) {
                // Should not happen
                Verbose.error("No typle to remove");

                return;
            }

            // Disable node until completed operation
            tupleNode.setEnabled(false);

            this.mPoolThreads.execute(new Runnable() {
                @Override
                public void run() {
                    removeTuple_(tupleNode);
                } // run()
            });
        } // removeTuple()

        /**
         * Deletes the specified node from the tree.
         * 
         * @param node
         *            the node from the right hand side tree.
         */
        void deleteNode(final DefaultMutableTreeNode node) {
            if (node == null) {
                return;
            }

            final DefaultTreeModel model = (DefaultTreeModel) (MainView.this.mTree
                    .getModel());

            model.removeNodeFromParent(node);
        } // deleteNode()

        /**
         * Add the bucket to the view.
         * 
         * @param bucket
         *            the tuple.
         * @param scrollBottom
         *            true if the view should scroll to the point the new node
         *            will appear or false otherwise.
         */
        private void update(final IBucket bucket, final boolean scrollBottom) {
            final ClientNode clientNode = MainView.this.getNode(bucket
                    .getBucketParent());

            if (clientNode != null) {
                final BucketNode bucketNode = new BucketNode(bucket);
                final DefaultTreeModel model = (DefaultTreeModel) MainView.this.mTree
                        .getModel();

                model.insertNodeInto(bucketNode, clientNode,
                        clientNode.getChildCount());
                if (scrollBottom && MainView.this.mShouldBeVisible) {
                    MainView.this.mTree.scrollPathToVisible(new TreePath(
                            bucketNode.getPath()));
                }
                Verbose.debug("Updated bucket \"{0}\" using client \"{1}\"",
                        bucket.getBucketName(), bucket.getBucketParentName());
            } else {
                Verbose.warning(
                        "Missing client \"{0}\"; unable to create bucket \"{1}\"",
                        bucket.getBucketParentName(), bucket.getBucketName());
            }
        } // update()

        /**
         * Add the tuple to the view.
         * 
         * @param inputTuple
         *            the tuple.
         * @param scrollBottom
         *            true if the view should scroll to the point the new node
         *            will appear or false otherwise.
         */
        private void update(final ITuple inputTuple, final boolean scrollBottom) {
            final BucketNode bucketNode = getNode(inputTuple.getTupleParent());

            update(bucketNode, inputTuple, scrollBottom);
        } // update()

        private void update(final BucketNode bucketNode,
                final ITuple inputTuple, final boolean scrollBottom) {
            if (bucketNode != null) {
                if (inputTuple.getTupleParent() != null
                        && !inputTuple.getTupleParentName().equals(
                                bucketNode.getBucketName())) {
                    Verbose.warning(
                            "Tuple \"{0}\" should be in bucket \"{2}\" but it is in bucket \"{1}\"",
                            inputTuple.getKeyName(),
                            inputTuple.getTupleParentName(),
                            bucketNode.getBucketName());

                    return;
                }

                final TupleNode tupleNode = new TupleNode(inputTuple);
                final DefaultTreeModel model = (DefaultTreeModel) MainView.this.mTree
                        .getModel();

                model.insertNodeInto(tupleNode, bucketNode,
                        bucketNode.getChildCount());
                if (scrollBottom && MainView.this.mShouldBeVisible) {
                    MainView.this.mTree.scrollPathToVisible(new TreePath(
                            tupleNode.getPath()));
                }
                Verbose.debug(
                        "Updated tuple \"{0}\" in bucket \"{1}\" using client \"{2}\"",
                        inputTuple.getKeyName(),
                        inputTuple.getTupleParentName(),
                        inputTuple.getTupleParentName());
            }
        } // update()

        /**
         * Creates a client with the specified tuple.
         * 
         * @param inputClient
         *            the client's details.
         */
        protected void createClient_(final IClient inputClient) {
            try {
                final IClient client = MainView.this.mController
                        .createClient(inputClient);
                final ClientNode clientNode = new ClientNode(client);
                final DefaultTreeModel model = (DefaultTreeModel) MainView.this.mTree
                        .getModel();
                final DefaultMutableTreeNode root = (DefaultMutableTreeNode) model
                        .getRoot();

                model.insertNodeInto(clientNode, root, root.getChildCount());
                if (MainView.this.mShouldBeVisible) {
                    MainView.this.mTree.scrollPathToVisible(new TreePath(
                            clientNode.getPath()));
                }
                Verbose.debug("Updated client \"{0}\"",
                        clientNode.getClientName());
            } catch (Exception caee) {
                Verbose.log(caee, "Failed to create client \"{0}\": {1}",
                        inputClient.getClientName(), caee.getMessage());
            } // end try
        } // createClient_()

        /**
         * Creates a bucket with the specified tuple.
         * 
         * @param inputBucket
         *            the bucket's details.
         */
        public void createBucket_(final IBucket inputBucket) {
            try {
                final IBucket bucket = MainView.this.mController
                        .createBucket(inputBucket);

                update(bucket, true);
            } catch (Exception excp) {
                Verbose.log(
                        excp,
                        "Fail to create bucket \"{0}\" using client \"{1}\": {2}",
                        inputBucket.getBucketName(),
                        inputBucket.getBucketParentName(), excp.getMessage());
            } // end try
        } // createBucket_()

        /**
         * Creates a tuple with the specified tuple.
         * 
         * @param inputTuple
         *            the tuple's details.
         */
        public void createTuple_(final ITuple inputTuple) {
            try {
                final ITuple tuple = MainView.this.mController
                        .createTuple(inputTuple);

                update(tuple, true);
            } catch (Exception excp) {
                Verbose.log(
                        excp,
                        "Faile to create tuple with key \"{0}\" in bucket \"{1}\" using client \"{2}\": {3}",
                        inputTuple.getKeyName(), inputTuple
                                .getTupleParentName(), inputTuple
                                .getTupleParent().getBucketParentName(), excp
                                .getMessage());
            } // end try
        } // createTuple_()

        /**
         * Retrieves the current buckets from the DB for the specified client
         * and updates view.
         * 
         * @param clientNode
         *            the client.
         */
        public void refreshClient_(final ClientNode clientNode) {
            if (clientNode == null) {
                // Should not happen
                Verbose.error("Null client node; unable to adquiring the buckets");

                return;
            }

            try {
                final Iterator<IBucket> iter = MainView.this.mController
                        .refreshClient(clientNode.getClientName());
                BucketNode bucketNode = null;

                // Remove current buckets from the view
                if (clientNode != null) {
                    final DefaultTreeModel model = (DefaultTreeModel) (MainView.this.mTree
                            .getModel());

                    while (clientNode.getChildCount() > 0) {
                        model.removeNodeFromParent((MutableTreeNode) clientNode
                                .getChildAt(0));
                    }
                }

                // Updates the view with the buckets from the DB
                while (iter.hasNext()) {
                    final IBucket bucket = iter.next();

                    bucketNode = new BucketNode(bucket);
                    update(bucket, false);
                } // end while

                if (bucketNode != null && MainView.this.mShouldBeVisible) {
                    MainView.this.mTree.scrollPathToVisible(new TreePath(
                            bucketNode.getPath()));
                }
            } catch (final DBException dbe) {
                Verbose.log(Level.WARNING, dbe,
                        "Failed to get all buckets using client \"{0}\"; {1}",
                        clientNode.getClientName(), dbe.getMessage());
            } finally {
                // Re-enable the node
                clientNode.setEnabled(true);
            } // end try
        } // refreshClient_()

        /**
         * Retrieves the current tuples from the DB for the specified bucket and
         * updates view.
         * 
         * @param bucket
         *            the bucket.
         */
        public void refreshBucket_(final BucketNode bucketNade) {
            if (bucketNade == null) {
                // Should not happen
                Verbose.error("Null bucket node; unable to adquiring the tuples within that bucket");

                return;
            }

            try {
                final Iterator<ITuple> iter = MainView.this.mController
                        .refreshBucket(bucketNade);
                TupleNode tupleNode = null;
                DefaultMutableTreeNode node;

                // Remove current tuple from the view
                node = getNode(bucketNade);
                if (node != null) {
                    final DefaultTreeModel model = (DefaultTreeModel) (MainView.this.mTree
                            .getModel());

                    while (node.getChildCount() > 0) {
                        model.removeNodeFromParent((MutableTreeNode) node
                                .getChildAt(0));
                    }
                }

                // Updates the view with the tuples from the DB
                while (iter.hasNext()) {
                    final ITuple tuple = iter.next();

                    tupleNode = new TupleNode(tuple);
                    update(bucketNade, tupleNode, false);
                } // end while

                if (tupleNode != null && MainView.this.mShouldBeVisible) {
                    MainView.this.mTree.scrollPathToVisible(new TreePath(
                            tupleNode.getPath()));
                }
            } catch (final DBException dbe) {
                Verbose.log(
                        dbe,
                        "Failed to get all tuples' key for bucket \"{0}\" using client \"{1}\"; {2}",
                        bucketNade.getBucketName(),
                        bucketNade.getBucketParentName(), dbe.getMessage());
            } finally {
                // Re-enable the node
                bucketNade.setEnabled(true);
            } // end try
        } // refreshBucket_()

        /**
         * Connects to the DB with the specified client's details.
         * 
         * @param clientNode
         *            the node in the view representing a client.
         */
        public void connect_(final ClientNode clientNode) {
            if (clientNode == null) {
                // Should not happen
                Verbose.error("Null client node; unable to connect using that node");

                return;
            }

            try {
                MainView.this.mController.connect(clientNode.getClientName());
                Verbose.info("\"{0}\" is connected", clientNode.getClientName());
            } catch (final DBException dbe) {
                Verbose.log(
                        dbe,
                        "Failed to connect to the DB using the client \"{0}\" connection details; {1}",
                        clientNode.getClientName(), dbe.getMessage());
            } finally {
                final IClient client = MainView.this.mController
                        .getClient(clientNode.getClientName());

                if (client != null) {
                    clientNode.setConnected(client.isConnected());
                    clientNode.setEnabled(true);
                }

                // Update the node in the tree
                ((DefaultTreeModel) MainView.this.mTree.getModel())
                        .nodeChanged(clientNode);
            } // end try
        } // connect()

        /**
         * Disconnects from the DB the specified client and removes it from the
         * tree view.
         * 
         * @param clientNode
         *            the node in the view representing a client.
         */
        public void shutdown_(final ClientNode clientNode) {
            if (clientNode == null) {
                // Should not happen
                Verbose.error("Null client node; unable to connect using that node");

                return;
            }

            try {
                MainView.this.mController.shutdown(clientNode.getClientName());
                Verbose.info("\"{0}\" has been disconnected",
                        clientNode.getClientName());
            } catch (final DBException dbe) {
                Verbose.log(
                        dbe,
                        "Failed to connect to the DB using the client \"{0}\" connection details; {1}",
                        clientNode.getClientName(), dbe.getMessage());
            } finally {
                final IClient client = MainView.this.mController
                        .getClient(clientNode.getClientName());

                if (client != null) {
                    clientNode.setConnected(client.isConnected());
                    clientNode.setEnabled(true);

                    // Update the node in the tree
                    ((DefaultTreeModel) MainView.this.mTree.getModel())
                            .nodeChanged(clientNode);
                } else {
                    ((DefaultTreeModel) MainView.this.mTree.getModel())
                            .removeNodeFromParent(clientNode);
                    // ((DefaultMutableTreeNode)
                    // clientNode.getParent()).remove(clientNode);
                }
            } // end try
        } // shutdown_()

        /**
         * Disconnects from the DB the specified client.
         * 
         * @param clientNode
         *            the node in the view representing a client.
         */
        public void disconnect_(final ClientNode clientNode) {
            if (clientNode == null) {
                // Should not happen
                Verbose.error("Null client node; unable to connect using that node");

                return;
            }

            try {
                MainView.this.mController
                        .disconnect(clientNode.getClientName());
                Verbose.info("\"{0}\" has been disconnected",
                        clientNode.getClientName());
            } catch (final DBException dbe) {
                Verbose.log(
                        dbe,
                        "Failed to connect to the DB using the client \"{0}\" connection details; {1}",
                        clientNode.getClientName(), dbe.getMessage());
            } finally {
                final IClient client = MainView.this.mController
                        .getClient(clientNode.getClientName());

                if (client != null) {
                    clientNode.setConnected(client.isConnected());
                    clientNode.setEnabled(true);
                }

                // Update the node in the tree
                ((DefaultTreeModel) MainView.this.mTree.getModel())
                        .nodeChanged(clientNode);
            } // end try
        } // disconnect_()

        /**
         * Removes the specified client from the DB and updates the view.
         * 
         * @param clientNode
         *            the client.
         */
        public void removeClient_(final ClientNode clientNode) {
            MainView.this.mController.removeClient(clientNode);
            deleteNode(clientNode);
        } // removeClient_()

        /**
         * Removes the specified bucket from the DB and updates the view.
         * 
         * @param bucketNode
         *            the bucket. Must not be null.
         */
        private void removeBucket_(final BucketNode bucketNode) {
            bucketNode.setEnabled(false);
            MainView.this.mController.removeBucket(bucketNode);
            deleteNode(bucketNode);
        } // removeBucket_()

        /**
         * Removes the specified tuple from the DB and updates the view.
         * 
         * @param tupleNode
         *            the tuple.
         */
        public void removeTuple_(final TupleNode tupleNode) {
            try {
                MainView.this.mController.removeTuple(tupleNode);
                deleteNode(tupleNode);
            } catch (DBException dbe) {
                Verbose.log(
                        dbe,
                        "Error processing the delition of typle with key \"{0}\" from bucket \"{1}\" using client \"{2}\"; {3}",
                        tupleNode.getKeyName(), tupleNode.getTupleParentName(),
                        tupleNode.getTupleParent().getBucketParentName(),
                        dbe.getMessage());
            } // end try
        } // removeTuple_()
    } // end class Processor
} // end class MainView

/**
 * .
 *
 * @author aas
 * @version 0.0
 */
interface AllowEnable {
    /**
     * @return true if control is enabled or false otherwise.
     */
    public boolean isEnabled();

    /**
     * Sets if the control is enabled or disabled.
     * 
     * @param bEnabled
     *            true if control is enabled or false otherwise.
     */
    public void setEnabled(final boolean bEnabled);
} // end interface AllowEnable
