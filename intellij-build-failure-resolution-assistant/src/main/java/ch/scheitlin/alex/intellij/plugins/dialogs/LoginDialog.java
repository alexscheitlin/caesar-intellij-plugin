package ch.scheitlin.alex.intellij.plugins.dialogs;

import ch.scheitlin.alex.intellij.plugins.services.Controller;
import com.intellij.openapi.ui.Messages;

import javax.swing.*;
import java.awt.event.*;

public class LoginDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textFieldHost;
    private JTextField textFieldUsername;
    private JPasswordField passwordField;
    private JLabel labelHost;
    private JLabel labelUsername;
    private JLabel labelPassword;
    private JCheckBox checkBoxRememberMe;

    private boolean loginSuccessful;
    private boolean rememberMe;

    public LoginDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        checkBoxRememberMe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRememberMe();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public String[] showDialog(String host, String username, String password) {
        // initialize
        setLocationRelativeTo(null);    // center on screen
        setTitle("Log in to TeamCity");

        // show saved team city credentials
        if (host != null && username != null && password != null) {
            this.textFieldHost.setText(host);
            this.textFieldUsername.setText(username);
            this.passwordField.setText(password);
            this.checkBoxRememberMe.setSelected(true);
        }

        // show dialog
        this.pack();
        this.setVisible(true);

        // return after dispose()
        if (this.loginSuccessful) {
            return new String[]{
                    this.textFieldHost.getText(),
                    this.textFieldUsername.getText(),
                    String.valueOf(passwordField.getPassword())
            };
        } else {
            return null;
        }
    }

    private void onOK() {
        // get host, name, and password
        String host = textFieldHost.getText();
        String username = textFieldUsername.getText();
        String password = String.valueOf(passwordField.getPassword());

        // test team city connection
        if (!Controller.getInstance().testTeamCityConnection(host, username, password)) {
            // show error message
            Messages.showMessageDialog(
                    "Please make sure to enter your user name and password correctly.\n" +
                            "Please also check your connection to the TeamCity server!",
                    "Error", Messages.getInformationIcon());

            // clear password field
            passwordField.setText("");
            return;
        }

        this.loginSuccessful = true;

        // save or delete credentials
        if (this.rememberMe) {
            Controller.getInstance().saveTeamCityCredentials(
                    this.textFieldHost.getText(),
                    this.textFieldUsername.getText(),
                    String.valueOf(passwordField.getPassword())
            );
        } else {
            Controller.getInstance().deleteTeamCityCredentials();
        }

        // dispose login form
        dispose();
    }

    private void onCancel() {
        // dispose login form
        dispose();
    }

    private void onRememberMe() {
        this.rememberMe = this.checkBoxRememberMe.isSelected();
    }
}
