package com.syncfree.dbclient.gui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.aasco.util.PropertyDef;
import com.syncfree.dbclient.controller.DBException;
import com.syncfree.dbclient.data.ITuple;


/**
 * View of the data for a tuple.
 *
 * @author aas
 * @version 0.0
 */
public class TupleView extends JPanel {
    /**
     * A version number for this class so that serialisation can occur without
     * worrying about the underlying class changing between serialisation and
     * deserialisation.
     * <p>
     *
     * Not that we ever serialise this class of course, but JPanel implements
     * Serializable, so therefore by default we do as well.
     */
    private static final long serialVersionUID = 1031783210281076077L;

    /** The tuple key label. */
    private static final String LABEL_KEY = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.connection_tuple_key_label",
            "Key:   ").getProperty();
    /** The tuple value label. */
    private static final String LABEL_VALUE = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.connection_tuple_value_label",
            "Value: ").getProperty();
    /** The number of columns for the tuple key. */
    private static final int KEY_NUM_COLS = PropertyDef
            .getPropertyDef(
                    "com.syncfree.dbclient.gui.connection_"
                            + "tuple_key_num_cols", 12).getProperty();
    /** The number of rows for the tuple value. */
    private static final int VALUE_NUM_ROWS = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.connectiontuple_value_num_rows",
            30).getProperty();
    /** The number of columns for the tuple value. */
    private static final int VALUE_NUM_COLS = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.gui.connection_tuple_value_num_cols", 12)
            .getProperty();

    /** The GUI control that holds the tuple's key. */
    private JTextField mKey;
    /** The GUI control that holds the tuple's value. */
    private JTextArea mValue;

    /**
     * Builds an empty view of a tuple. Use #update(ITuple) to fill the view
     * with a tuple details.
     */
    public TupleView() {
        super(new BorderLayout());

        JPanel panel;
        JScrollPane scroll;

        panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(LABEL_KEY), BorderLayout.WEST);
        this.mKey = new JTextField(KEY_NUM_COLS);
        this.mKey.setEditable(false);
        panel.add(this.mKey, BorderLayout.CENTER);
        add(panel, BorderLayout.NORTH);

        panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(LABEL_VALUE), BorderLayout.NORTH);
        this.mValue = new JTextArea(VALUE_NUM_ROWS, VALUE_NUM_COLS);
        this.mValue.setEditable(false);
        scroll = new JScrollPane(this.mValue);
        panel.add(scroll, BorderLayout.CENTER);
        add(panel, BorderLayout.CENTER);

        setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
        setEditable(false);
    } // Constructor ()

    /**
     * Sets the editable elements of the view to allow inputs or not.
     * 
     * @param bEditable true to allow inputs or false otherwise.
     */
    public void setEditable(final boolean bEditable) {
        this.mKey.setEditable(bEditable);
        this.mValue.setEditable(bEditable);
    } // setEditable()

    /**
     * Updates the view with the tuple's details.
     * 
     * @param tuple
     *            the tuple's details.
     * @throws DBException when trying to load the value in the tuple.
     */
    public void update(final ITuple tuple) throws DBException {
        this.mKey.setText(tuple.getKeyName());
        this.mValue.setText(tuple.getValue().toString());
    } // update()
} // end class TupleView
