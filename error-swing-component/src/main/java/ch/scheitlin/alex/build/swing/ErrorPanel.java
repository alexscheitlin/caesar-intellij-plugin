package ch.scheitlin.alex.build.swing;

import ch.scheitlin.alex.build.model.Error;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ErrorPanel extends JPanel {
    private Error error;

    private JLabel labelPath;
    private JLabel labelMessage;
    private JButton buttonAction;

    public ErrorPanel(Error error, String actionButtonText) {
        this.error = error;

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // initialize label showing the path and file name
        this.labelPath = initPathLabel(this.error.getPath(), this.error.getFile(), this.error.getLine(), this.error.getColumn());
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0,0,0,0);
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 0;
        this.add(this.labelPath, c);

        // initialize label showing the message
        this.labelMessage = initMessageLabel(this.error.getMessage());
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0,0,0,0);
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 1;
        this.add(this.labelMessage, c);

        // initialize button to start action
        this.buttonAction = initActionButton(actionButtonText);
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0,20,0,0);
        c.gridheight = 2;
        c.gridx = 1;
        c.gridy = 0;
        this.add(this.buttonAction, c);
    }

    public Error getError() {
        return this.error;
    }

    private JLabel initPathLabel(String path, String file, int line, int column) {
        JLabel label = new JLabel();
        label.setText(path + "/" + file + " - [" + line + ":" + column + "]");

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
