package com.syncfree.dbclient.controller.riak.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.aasco.util.PropertyDef;

/**
 * Response GUI view.
 * 
 * @author aas
 * @version 0.0
 */
public class ResponseView extends JDialog implements ActionListener {
    /**
     * A version number for this class so that serialisation can occur without
     * worrying about the underlying class changing between serialisation and
     * deserialisation.
     * <p>
     *
     * Not that we ever serialise this class of course, but JDialog implements
     * Serializable, so therefore by default we do as well.
     */
    private static final long serialVersionUID = -2611091533441981027L;

    /** The text for this view label. */
    private static final String LABEL = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.controller.riak.gui.response", "Response:")
            .getProperty();

    /** The text for this view button. */
    private static final String ACT_OK = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.controller.riak.gui.bottom_ok", "OK")
            .getProperty();

    /** The width of the results dialog window. */
    private static final int RESULT_DIALOG_WIDTH = PropertyDef
            .getPropertyDef(
                    "com.syncfree.dbclient.controller.riak.gui.result_dialog_width",
                    400).getProperty();
    /** The height of the results dialog window. */
    private static final int RESULT_DIALOG_HEIGHT = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.controller.riak.gui.result_dialog_height",
            240).getProperty();

    /** The holder of the result. */
    private final JTextArea mResponse;

    /**
     * Builds the view.
     * 
     * @param frame
     *            the GUI frame.
     */
    public ResponseView(final JFrame oFrame) {
        super(oFrame, true);

        final JPanel pane = new JPanel(new BorderLayout());
        final JPanel bottomPane;
        final JScrollPane scroll;
        final JPanel topPane;
        final JPanel p;
        final JButton button;

        // Top
        topPane = new JPanel(new BorderLayout());

        p = new JPanel(new BorderLayout());
        p.add(new JLabel(LABEL), BorderLayout.WEST);
        topPane.add(p, BorderLayout.NORTH);

        this.mResponse = new JTextArea();
        this.mResponse.setEditable(false);
        scroll = new JScrollPane(this.mResponse);
        topPane.add(scroll, BorderLayout.CENTER);

        pane.add(topPane, BorderLayout.CENTER);

        // Bottom
        bottomPane = new JPanel(new BorderLayout());
        button = new JButton(ACT_OK);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent ae) {
                ResponseView.this.setVisible(false);
                ResponseView.this.mResponse.setText("");
            } // actionPerformed()
        });
        bottomPane.add(button, BorderLayout.EAST);

        pane.add(bottomPane, BorderLayout.SOUTH);
        pane.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
        add(pane);

        setPreferredSize(new Dimension(RESULT_DIALOG_WIDTH,
                RESULT_DIALOG_HEIGHT));

        pack();

        final int x = oFrame.getX() + ((oFrame.getWidth() - getWidth()) / 2) + 220;
        final int y = oFrame.getY() + ((oFrame.getHeight() - getHeight()) / 2) + 150;

        setLocation(x, y);
    } // Constructor ()

    /**
     * Sets the title and shows the response.
     * 
     * @param strTitle
     *            the title of the view.
     * @param strResponse
     *            the response to show.
     */
    public void set(final String strTitle, final String strResponse) {
        setTitle(strTitle);
        this.mResponse.setText(strResponse);
        this.mResponse.setSelectionStart(0);
        this.mResponse.setSelectionEnd(0);
        setVisible(true);
    } // set()

    @Override
    public void actionPerformed(final ActionEvent ae) {
        setVisible(false);
    } // actionPerformed()
} // end class ResponseView
