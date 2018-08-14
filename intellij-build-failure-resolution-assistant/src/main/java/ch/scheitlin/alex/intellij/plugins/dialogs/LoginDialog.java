package ch.scheitlin.alex.intellij.plugins.dialogs;

import ch.scheitlin.alex.intellij.plugins.services.Controller;
import com.intellij.openapi.ui.Messages;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

public class LoginDialog extends JDialog {
    // data
    private boolean loginSuccessful;
    private boolean rememberMe;

    // components
    private JLabel labelHost;
    private JTextField textFieldHost;
    private JLabel labelUsername;
    private JTextField textFieldUsername;
    private JLabel labelPassword;
    private JPasswordField passwordField;
    private JCheckBox checkBoxRememberMe;
    private JPanel panelInput;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel panelControls;
    private JPanel panelContent;

    // appearance constants
    private final int BUTTON_WIDTH = 75;
    private final int TEXT_FIELD_WIDTH = 175;

    public LoginDialog() {
        // set layout for content panel
        this.panelContent = new JPanel();
        this.panelContent.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // initialize panel with input fields
        this.panelInput = initInputPanel("Host:", "Username:", "Password:", "Remember Me", this.TEXT_FIELD_WIDTH);
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(10, 10, 10, 10);
        c.weightx = 1.0;
        this.panelContent.add(this.panelInput, c);

        // initialize panel with control buttons
        this.panelControls = initControlPanel("OK", "Cancel", this.BUTTON_WIDTH);
        c.anchor = GridBagConstraints.SOUTHEAST;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(10, 10, 10, 10);
        c.weightx = 0.0;
        c.weighty = 1.0;
        this.panelContent.add(this.panelControls, c);

        // set content panel as content pane of LoginDialog
        this.setContentPane(this.panelContent);

        // call onCancel() when cross is clicked
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        this.panelContent.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        // set icon
        Image icon = new ImageIcon(LoginDialog.class.getResource("/icons/icon_16x16.png")).getImage();
        setIconImage(icon);

        this.setTitle("Log in to Build Server");
        this.setModal(true);
        this.getRootPane().setDefaultButton(this.buttonOK);
        this.setLocationRelativeTo(null);    // center on screen
    }

    private JPanel initInputPanel(
            String hostTitle,
            String usernameTitle,
            String passwordTitle,
            String rememberMeTitle,
            int textFieldWidth) {
        JPanel panel = new JPanel();

        // set layout for panel
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // initialize label with host Title
        this.labelHost = initLabel(hostTitle);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 0, 10);
        c.weightx = 0.0;
        panel.add(this.labelHost, c);

        // initialize text field for host input
        this.textFieldHost = initTextField(textFieldWidth);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 1;
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 0, 0);
        c.weightx = 1.0;
        panel.add(this.textFieldHost, c);

        // initialize label with username title
        this.labelUsername = initLabel(usernameTitle);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(0, 0, 0, 10);
        c.weightx = 0.0;
        panel.add(this.labelUsername, c);

        // initialize text field for username input
        this.textFieldUsername = initTextField(textFieldWidth);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 1;
        c.gridx = 1;
        c.gridy = 1;
        c.insets = new Insets(0, 0, 0, 0);
        c.weightx = 1.0;
        panel.add(this.textFieldUsername, c);

        // initialize label with password title
        this.labelPassword = initLabel(passwordTitle);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 2;
        c.insets = new Insets(0, 0, 0, 10);
        c.weightx = 0.0;
        panel.add(this.labelPassword, c);

        // initialize password field for password input
        this.passwordField = initPasswordField(textFieldWidth);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 1;
        c.gridx = 1;
        c.gridy = 2;
        c.insets = new Insets(0, 0, 0, 0);
        c.weightx = 1.0;
        panel.add(this.passwordField, c);

        // initialize check box for remember me input
        this.checkBoxRememberMe = initRememberMeCheckBox(rememberMeTitle);
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 3;
        c.insets = new Insets(20, 0, 0, 0);
        c.weightx = 0.0;
        panel.add(this.checkBoxRememberMe, c);

        return panel;
    }

    private JLabel initLabel(String text) {
        JLabel label = new JLabel();
        label.setText(text);

        return label;
    }

    private JTextField initTextField(int width) {
        JTextField textField = new JTextField();

        Dimension newSize = new Dimension(width, 24);
        textField.setMinimumSize(newSize);
        textField.setPreferredSize(newSize);
        textField.setMaximumSize(newSize);

        return textField;
    }

    private JPasswordField initPasswordField(int width) {
        JPasswordField passwordField = new JPasswordField();

        Dimension newSize = new Dimension(width, 24);
        passwordField.setMinimumSize(newSize);
        passwordField.setPreferredSize(newSize);
        passwordField.setMaximumSize(newSize);

        return passwordField;
    }

    private JCheckBox initRememberMeCheckBox(String text) {
        JCheckBox checkBox = new JCheckBox();
        checkBox.setText(text);

        checkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRememberMe();
            }
        });

        return checkBox;
    }

    private JPanel initControlPanel(String okTitle, String cancelTitle, int buttonWidth) {
        JPanel panel = new JPanel();

        // set layout for panel
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // initialize button for ok action
        this.buttonOK = initOkButton(okTitle, buttonWidth);
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 0, 0);
        panel.add(this.buttonOK, c);

        // initialize button for cancel action
        this.buttonCancel = initCancelButton(cancelTitle, buttonWidth);
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(0, 10, 0, 0);
        panel.add(this.buttonCancel, c);

        return panel;
    }

    private JButton initOkButton(String text, int width) {
        JButton button = new JButton();
        button.setText(text);

        // set size
        Dimension newSize = new Dimension(width, 24);
        button.setMinimumSize(newSize);
        button.setPreferredSize(newSize);
        button.setMaximumSize(newSize);

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        return button;
    }

    private JButton initCancelButton(String text, int width) {
        JButton button = new JButton();
        button.setText(text);

        // set size
        Dimension newSize = new Dimension(width, 24);
        button.setMinimumSize(newSize);
        button.setPreferredSize(newSize);
        button.setMaximumSize(newSize);

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        return button;
    }

    public String[] showDialog(String host, String username, String password) {
        // show saved team city credentials
        if (host != null && username != null && password != null) {
            this.textFieldHost.setText(host);
            this.textFieldUsername.setText(username);
            this.passwordField.setText(password);
            this.checkBoxRememberMe.setSelected(true);
            onRememberMe();
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

    private void onRememberMe() {
        this.rememberMe = this.checkBoxRememberMe.isSelected();
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
}
