package com.syncfree.dbclient.gui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.aasco.util.PropertyDef;
import com.syncfree.dbclient.data.IBucket;


/**
 * View of the data for a bucket.
 *
 * @author aas
 * @version 0.0
 */
public class BucketView extends JPanel {
    /**
     * A version number for this class so that serialisation can occur without
     * worrying about the underlying class changing between serialisation and
     * deserialisation.
     * <p>
     *
     * Not that we ever serialise this class of course, but JPanel implements
     * Serializable, so therefore by default we do as well.
     */
    private static final long serialVersionUID = -946162924094419365L;

    /** The bucket name label. */
    private static final String LABEL_NAME 
            = PropertyDef.getPropertyDef("com.syncfree.dbclient.gui.connection_"
                    + "bucket_name_label", "Name: ").getProperty();
    /** The number of columns for the bucket's name. */
    private static final int NAME_LABEL_NUM_COLS 
            = PropertyDef.getPropertyDef("com.syncfree.dbclient.gui.connection_"
                    + "bucket_name_num_cols", 12).getProperty();

//    private String mstrClientName; // the parent
    private JTextField mName;


    /**
     * Builds the empty bucket's view.
     */
    public BucketView() {
        super(new BorderLayout());

        // Bucket's name panel
        final JPanel panel = new JPanel(new BorderLayout());
        
        panel.add(new JLabel(LABEL_NAME), BorderLayout.WEST);
        this.mName = new JTextField(NAME_LABEL_NUM_COLS);
        panel.add(this.mName, BorderLayout.CENTER);
        add(panel, BorderLayout.NORTH);

        add(new JPanel(), BorderLayout.CENTER);
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
    } // setEditable()

    /**
     * Updates the bucket's view with the details of the specified bucket.
     * 
     * @param bucket the details of the bucket.
     */
    public void update(final IBucket bucket) {
        this.mName.setText(bucket.getBucketName());
//      this.mstrClientName = bucket.getBucketParentName();
    } // update()
} // end class BucketView
