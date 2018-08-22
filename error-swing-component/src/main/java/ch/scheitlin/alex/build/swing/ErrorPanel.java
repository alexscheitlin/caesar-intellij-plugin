package ch.scheitlin.alex.build.swing;

import ch.scheitlin.alex.build.model.Error;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ErrorPanel extends JPanel {
    // data
    private Error error;
    private String actionButtonText;

    // components
    private JLabel labelPath;
    private JLabel labelMessage;
    private JButton buttonAction;

    public ErrorPanel(Error error, String actionButtonText) {
        // set data variables
        this.error = error;
        this.actionButtonText = actionButtonText;

        // get error information
        String errorPath = this.error.getPath();
        String errorFile = this.error.getFile();
        int errorLine = this.error.getLine();
        int errorColumn = this.error.getColumn();
        String errorMessage = this.error.getMessage();

        // set layout for the ErrorPanel
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // initialize label showing the path and file name where the error occurred
        this.labelPath = initPathLabel(errorPath, errorFile, errorLine, errorColumn);
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0, 0, 0, 0);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        this.add(this.labelPath, c);

        // initialize label showing the error message
        this.labelMessage = initMessageLabel(errorMessage);
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0, 0, 0, 0);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0;
        this.add(this.labelMessage, c);

        // initialize button to start action
        this.buttonAction = initActionButton(this.actionButtonText);
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0, 20, 0, 0);
        c.fill = GridBagConstraints.NONE;
        c.gridheight = 2;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.0;
        this.add(this.buttonAction, c);
    }

    public Error getError() {
        return this.error;
    }

    private JLabel initPathLabel(String path, String file, int line, int column) {
        JLabel label = new JLabel();

        StringBuilder textBuilder = new StringBuilder();

        // ignore path if it is null
        if (path != null) {
            textBuilder.append(path + "/");
        }
        textBuilder.append(file);
        textBuilder.append(" - [" + line + ":" + column + "]");

        label.setText(textBuilder.toString());

        return label;
    }

    private JLabel initMessageLabel(String message) {
        JLabel label = new JLabel();
        label.setText(message);

        return label;
    }

    private JButton initActionButton(String text) {
        JButton button = new JButton();
        button.setText(text);

        return button;
    }

    public void addButtonAction(ActionListener actionListener) {
        this.buttonAction.addActionListener(actionListener);
    }
}
