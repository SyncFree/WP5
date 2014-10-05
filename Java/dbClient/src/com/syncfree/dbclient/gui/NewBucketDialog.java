package com.syncfree.dbclient.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.aasco.util.PropertyDef;
import com.syncfree.dbclient.data.IBucket;
import com.syncfree.dbclient.data.IClient;

/**
 * GUI dialog window to input the required details to create a DB bucket.
 *
 * @author aas
 * @version 0.0
 */
public class NewBucketDialog extends JDialog implements ActionListener, IBucket {
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
     * The default name for the DB New Bucket control.
     */
    public static final String DEFAULT_NAME = "DB New Bucket";

    public static final String ACTION_CREATE_BUCKET = "create_bucket";
    public static final String ACTION_CANCEL_CREATE_BUCKET = "cancel_create_bucket";

    /** The dialog title. */
    private static final String TITLE = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.solution_" + "nre_bucket_title",
            DEFAULT_NAME).getProperty();
    /** The name for the create bucket button. */
    private static final String CREATE_BUCKET = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.solution_" + "nre_create_bucket_btn",
            ACTION_CREATE_BUCKET).getProperty();
    /** The name for the cancel bucket button. */
    private static final String CANCEL = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.solution_" + "cancl_btn", "Cancel")
            .getProperty();
    /** The bucket name label. */
    private static final String LABEL_NAME = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.connection_" + "bucket_name_label",
            "Name: ").getProperty();
    /** The number of columns for the bucket's name. */
    private static final int NAME_LABEL_NUM_COLS = PropertyDef
            .getPropertyDef(
                    "com.syncfree.dbclient.gui.connection_"
                            + "bucket_name_num_cols", 12).getProperty();

    private static final int CREATE_BUCKET_DIALOG_WIDTH = PropertyDef
            .getPropertyDef(
                    "com.syncfree.dbclient.gui.connection_"
                            + "create_bucket_dialog_width", 500).getProperty();
    private static final int CREATE_BUCKET_DIALOG_HEIGHT = PropertyDef
            .getPropertyDef(
                    "com.syncfree.dbclient.gui.connection_"
                            + "create_bucket_dialog_height", 90).getProperty();

    private final String mstrClientName; // the parent
    private JTextField mName;

    /**
     * Builds the new bucket dialog input GUI.
     * 
     * @param oFrame
     *            the application frame.
     * @param listener
     *            the action listener.
     * @param strClientName
     *            the client's name.
     */
    public NewBucketDialog(final JFrame oFrame, final ActionListener listener,
            final String strClientName) {
        super(oFrame, TITLE, true);
        this.mstrClientName = strClientName;

        final JPanel pane = new JPanel(new BorderLayout());
        final JPanel top = buildTopPane();
        final JPanel bottom = buildBottomPane(listener);

        pane.add(top, BorderLayout.CENTER);
        pane.add(bottom, BorderLayout.SOUTH);
        pane.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
        add(pane);

        getAccessibleContext().setAccessibleName(DEFAULT_NAME);

        setName(getTitle());
        setPreferredSize(new Dimension(CREATE_BUCKET_DIALOG_WIDTH,
                CREATE_BUCKET_DIALOG_HEIGHT));

        pack();

        final int x = oFrame.getX() + ((oFrame.getWidth() - getWidth()) / 2);
        final int y = oFrame.getY() + ((oFrame.getHeight() - getHeight()) / 2);

        setLocation(x, y);
    } // Constructor ()

    public IBucket getData() {
        return new Bucket(this);
    } // getData()

    @Override
    public void actionPerformed(final ActionEvent ae) {
        setVisible(false);
    } // actionPerformed()

    @Override
    public String getBucketName() {
        return this.mName.getText();
    } // getBucketName()

    @Override
    public IClient getBucketParent() {
        return null;
    } // getBucketParent()

    @Override
    public String getBucketParentName() {
        return this.mstrClientName;
    } // getBucketParentName()

    /**
     * Clears the dialog-box controls.
     */
    public void reset() {
        this.mName.setText("");
    } // reset()

    /**
     * Builds the top part of the dialog window where the bucket data is
     * selected/introduced.
     * 
     * @return the top pane.
     */
    private JPanel buildTopPane() {
        // Bucket's name panel
        final JPanel top = new JPanel(new BorderLayout());
        final JLabel lName = new JLabel(LABEL_NAME);

        top.add(lName, BorderLayout.WEST);
        this.mName = new JTextField(NAME_LABEL_NUM_COLS);
        top.add(this.mName, BorderLayout.CENTER);

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
        final JButton create = new JButton(CREATE_BUCKET);
        final JButton cancel = new JButton(CANCEL);

        create.setActionCommand(ACTION_CREATE_BUCKET);
        bottom.add(create);
        cancel.setActionCommand(ACTION_CANCEL_CREATE_BUCKET);
        bottom.add(cancel);
        if (listener != null) {
            create.addActionListener(listener);
            create.addActionListener(this);
            cancel.addActionListener(this);
        }

        return bottom;
    } // buildBottomPane()
} // end class NewBucketDialog

/**
 * Copy of the data.
 * 
 * @author aas
 * @version 0.0
 */
class Bucket implements IBucket {
    private final String mstrClientName;
    private final String mstrName;

    Bucket(final NewBucketDialog data) {
        this.mstrClientName = data.getBucketParentName();
        this.mstrName = data.getBucketName();
    } // Constructor ()

    @Override
    public String getBucketName() {
        return this.mstrName;
    } // getBucketName()

    @Override
    public IClient getBucketParent() {
        return null;
    } // getBucketParent()

    @Override
    public String getBucketParentName() {
        return this.mstrClientName;
    } // getBucketParentName()
} // end class Bucket
