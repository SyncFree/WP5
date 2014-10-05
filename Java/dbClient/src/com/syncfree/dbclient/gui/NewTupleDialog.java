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
import com.syncfree.dbclient.data.ITuple;

/**
 * GUI dialog window to input the required details to create a DB tuple.
 *
 * @author aas
 * @version 0.0
 */
public class NewTupleDialog extends JDialog implements ActionListener, ITuple {
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
     * The default name for the DB New Tuple control.
     */
    public static final String DEFAULT_TITLE = "DB New Tuple";

    public static final String ACTION_CREATE_TUPLE = "create_tuple";
    public static final String ACTION_CANCEL_CREATE_TUPLE = "cancel_create_tuple";

    /** The dialog title. */
    private static final String TITLE = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.solution_" + "nre_tuple_title",
            DEFAULT_TITLE).getProperty();
    /** The name for the create tuple button. */
    private static final String CREATE_TUPLE = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.solution_" + "nre_create_tuple_btn",
            ACTION_CREATE_TUPLE).getProperty();
    /** The name for the cancel tuple button. */
    private static final String CANCEL = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.solution_" + "cancel_btn", "Cancel")
            .getProperty();
    /** The tuple key label. */
    private static final String LABEL_KEY = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.connection_" + "tuple_key_label",
            "Key:   ").getProperty();
    /** The tuple value label. */
    private static final String LABEL_VALUE = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.connection_tuple_value_label", "Value: ")
            .getProperty();
    /** The number of columns for the tuple key. */
    private static final int KEY_LABEL_NUM_COLS = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.connection_tuple_key_num_cols", 12)
            .getProperty();
    /** The number of columns for the tuple value. */
    private static final int VALUE_LABEL_NUM_COLS = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.connection_tuple_value_num_cols", 12)
            .getProperty();

    private static final int CREATE_TUPLE_DIALOG_WIDTH = PropertyDef
            .getPropertyDef(
                    "com.syncfree.dbclient.gui.connection_"
                            + "create_tuple_dialog_width", 500).getProperty();
    private static final int CREATE_TUPLE_DIALOG_HEIGHT = PropertyDef
            .getPropertyDef(
                    "com.syncfree.dbclient.gui.connection_"
                            + "create_tuple_dialog_height", 140).getProperty();

    /** The details of the tuple parent. */
    private IBucket mNodeBucket;
    private JTextField mKey;
    private JTextField mValue;

    /**
     * Builds the tuple's dialog input GUI.
     * 
     * @param oFrame
     *            the application frame.
     * @param listener
     *            the action listener.
     * @param nodeBucket
     *            the details for the parent bucket.
     */
    public NewTupleDialog(final JFrame oFrame, final ActionListener listener,
            final IBucket nodeBucket) {
        super(oFrame, TITLE, true);
        this.mNodeBucket = nodeBucket;

        final JPanel pane = new JPanel(new BorderLayout());
        final JPanel top = buildTopPane();
        final JPanel bottom = buildBottomPane(listener);

        pane.add(top, BorderLayout.CENTER);
        pane.add(bottom, BorderLayout.SOUTH);
        pane.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
        add(pane);

        getAccessibleContext().setAccessibleName(DEFAULT_TITLE);

        setName(getTitle());
        setPreferredSize(new Dimension(CREATE_TUPLE_DIALOG_WIDTH,
                CREATE_TUPLE_DIALOG_HEIGHT));

        pack();

        final int x = oFrame.getX() + ((oFrame.getWidth() - getWidth()) / 2);
        final int y = oFrame.getY() + ((oFrame.getHeight() - getHeight()) / 2);

        setLocation(x, y);
    } // Constructor ()

    public ITuple getData() {
        return new Tuple(this);
    } // getData()

    @Override
    public void actionPerformed(final ActionEvent ae) {
        setVisible(false);
    } // actionPerformed()

    @Override
    public Object getValue() {
        return this.mValue.getText();
    } // getValue()

    @Override
    public String getKeyName() {
        return this.mKey.getText();
    } // getKeyName()

    @Override
    public IBucket getTupleParent() {
        return this.mNodeBucket;
    } // getTupleParent()

    @Override
    public String getTupleParentName() {
        return this.mNodeBucket.getBucketName();
    } // getTupleParentName()

    /**
     * Clears the dialog-box controls.
     * 
     * @param nodeBucket
     *            the details for the parent bucket.
     */
    public void reset(final IBucket nodeBucket) {
        this.mNodeBucket = nodeBucket;
        this.mKey.setText("");
        this.mValue.setText("");
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
        JPanel panel;

        panel = new JPanel();
        panel.add(new JLabel(LABEL_KEY));
        this.mKey = new JTextField(KEY_LABEL_NUM_COLS);
        panel.add(this.mKey);
        top.add(panel, BorderLayout.NORTH);

        panel = new JPanel();
        panel.add(new JLabel(LABEL_VALUE));
        this.mValue = new JTextField(VALUE_LABEL_NUM_COLS);
        panel.add(this.mValue, BorderLayout.CENTER);
        top.add(panel);

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
        final JButton create = new JButton(CREATE_TUPLE);
        final JButton cancel = new JButton(CANCEL);

        create.setActionCommand(ACTION_CREATE_TUPLE);
        bottom.add(create);
        cancel.setActionCommand(ACTION_CANCEL_CREATE_TUPLE);
        bottom.add(cancel);
        if (listener != null) {
            create.addActionListener(listener);
            create.addActionListener(this);
            cancel.addActionListener(this);
        }

        return bottom;
    } // buildBottomPane()
} // end class NewTupleDialog

/**
 * Copy of the data.
 * 
 * @author aas
 * @version 0.0
 */
class Tuple implements ITuple {
    private IBucket mBucket;
    private String mstrKeyName;
    private Object mValue;

    Tuple(final NewTupleDialog tuple) {
        this.mBucket = tuple.getTupleParent();
        this.mstrKeyName = tuple.getKeyName();
        this.mValue = tuple.getValue();
    } // Constructor ()

    @Override
    public String getKeyName() {
        return this.mstrKeyName;
    } // getKeyName()

    @Override
    public IBucket getTupleParent() {
        return this.mBucket;
    } // getTupleParent()

    @Override
    public String getTupleParentName() {
        return this.mBucket.getBucketName();
    } // getTupleParentName()

    @Override
    public Object getValue() {
        return this.mValue;
    } // getValue()
} // end class Tuple
