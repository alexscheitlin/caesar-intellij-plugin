package ch.scheitlin.alex.intellij.plugins.dialogs;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public class MultiLineStringDialog extends JDialog {
    // data
    private String multiLineString;

    // components
    private JTextPane labelMultiLineString;
    private JScrollPane scrollPane;
    private JButton buttonClose;
    private JPanel panelContent;

    // appearance constants
    private final int LABEL_MULTI_LINE_STRING_WIDTH = 1000;
    private final int LABEL_MULTI_LINE_STRING_HEIGHT = 750;
    private final String BUTTON_CLOSE_TEXT = "Close";
    private final int BUTTON_CLOSE_WIDTH = 75;

    public MultiLineStringDialog() {
        // set layout for content panel
        this.panelContent = new JPanel();
        this.panelContent.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // initialize label with multiline string to display
        this.labelMultiLineString = initMultiLineStringLabel(
                this.LABEL_MULTI_LINE_STRING_WIDTH,
                this.LABEL_MULTI_LINE_STRING_HEIGHT
        );
        this.scrollPane = new JScrollPane(this.labelMultiLineString);
        this.scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 0, 0);
        c.weightx = 1.0;
        c.weighty = 1.0;
        this.panelContent.add(this.scrollPane, c);

        // initialize button to close the dialog
        this.buttonClose = initCloseButton(this.BUTTON_CLOSE_TEXT, this.BUTTON_CLOSE_WIDTH);
        c.anchor = GridBagConstraints.SOUTHEAST;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(10, 10, 10, 10);
        c.weightx = 0.0;
        c.weighty = 0.0;
        this.panelContent.add(this.buttonClose, c);

        // set content panel as content pane of dialog
        this.setContentPane(this.panelContent);

        // call onClose() when cross is clicked
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onClose();
            }
        });

        // call onClose() on ESCAPE
        this.panelContent.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onClose();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        this.setTitle("Raw Build Server Build Log");
        this.setModal(true);
        this.getRootPane().setDefaultButton(this.buttonClose);
    }

    private JTextPane initMultiLineStringLabel(int width, int height) {
        JTextPane label = new JTextPane();
        label.setEditable(false);
        label.setOpaque(false);

        // set size
        Dimension newSize = new Dimension(width, height);
        label.setMinimumSize(newSize);
        label.setPreferredSize(newSize);
        label.setMaximumSize(newSize);

        return label;
    }

    private JButton initCloseButton(String text, int width) {
        JButton button = new JButton();
        button.setText(text);

        // set size
        Dimension newSize = new Dimension(width, 24);
        button.setMinimumSize(newSize);
        button.setPreferredSize(newSize);
        button.setMaximumSize(newSize);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onClose();
            }
        });

        return button;
    }

    public void showDialog(String multiLineString) {
        this.multiLineString = multiLineString;
        this.labelMultiLineString.setText(this.multiLineString);

        this.pack();

        // scroll to top of goal lines
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                scrollPane.getVerticalScrollBar().setValue(0);
            }
        });

        // show dialog
        this.setVisible(true);
    }

    private void onClose() {
        dispose();
    }
}
