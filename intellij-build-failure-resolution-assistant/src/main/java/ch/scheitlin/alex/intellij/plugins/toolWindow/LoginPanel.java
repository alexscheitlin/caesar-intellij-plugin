package ch.scheitlin.alex.intellij.plugins.toolWindow;

import ch.scheitlin.alex.intellij.plugins.services.Controller;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPanel extends JPanel {
    private JPanel panelContent;
    private JLabel labelTitle;
    private JLabel labelInformation;
    private JButton buttonLogin;
    private JLabel labelIcon;

    Project project;

    public LoginPanel(Project project) {
        this.project = project;

        // set layout
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // configure and add content panel
        initAndAddContentPanel(20);

        // configure and add label with title
        initTitleLabel();
        c.gridx = 0;
        c.gridy = 0;
        this.panelContent.add(labelTitle, c);

        // configure and add label with information message
        initInformationLabel();
        c.gridx = 0;
        c.gridy = 1;
        c.insets = JBUI.insets(20, 0, 0, 0);
        this.panelContent.add(labelInformation, c);

        // configure and add button to login
        initLoginButton();
        c.gridx = 0;
        c.gridy = 2;
        c.insets = JBUI.insets(20, 0, 0, 0);
        this.panelContent.add(buttonLogin, c);

        // configure and add label with icon
        initIconLabel();
        c.gridx = 0;
        c.gridy = 3;
        c.insets = JBUI.insets(50, 0, 0, 0);
        this.panelContent.add(this.labelIcon, c);

        // add panel to move content to the top
        // (at least one component needs to have weighty greater than 0.0)
        c.gridx = 0;
        c.gridy = 4;
        c.fill = GridBagConstraints.BOTH;
        c.insets = JBUI.insets(0);
        c.weightx = 1.0;
        c.weighty = 1.0;
        this.panelContent.add(new JPanel(), c);
    }

    private void initAndAddContentPanel(int padding) {
        this.panelContent = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // request max size to better place the components and add padding to the content
        this.panelContent = new JPanel(new GridBagLayout());
        c.gridx = 0;
        c.gridy = 0;
        c.insets = JBUI.insets(padding);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        this.add(this.panelContent, c);
    }

    private void initTitleLabel() {
        this.labelTitle = new JLabel();
        this.labelTitle.setText("Build Failure Resolution Assistant");

        Font labelFont = this.labelTitle.getFont();
        Font newFont = new Font(labelFont.getName(), Font.BOLD, 15);
        this.labelTitle.setFont(newFont);
    }

    private void initInformationLabel() {
        this.labelInformation = new JLabel();
        this.labelInformation.setText("Please login first!");
    }

    private void initLoginButton() {
        this.buttonLogin = new JButton();
        this.buttonLogin.setText("Login");
        this.buttonLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Controller controller = ServiceManager.getService(Controller.class);
                controller.login(LoginPanel.this.project);
            }
        });
    }

    private void initIconLabel() {
        this.labelIcon = new JLabel();
        ImageIcon icon = new ImageIcon(LoginPanel.class.getResource("/icons/icon_large.png"));
        this.labelIcon.setIcon(icon);
    }
}
