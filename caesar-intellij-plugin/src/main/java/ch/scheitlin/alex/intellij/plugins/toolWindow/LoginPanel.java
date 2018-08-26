package ch.scheitlin.alex.intellij.plugins.toolWindow;

import ch.scheitlin.alex.intellij.plugins.services.Controller;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.net.URL;

public class LoginPanel extends JPanel {
    private JPanel panelContent;
    private JLabel labelTitle;
    private JLabel labelSubtitle;
    private JLabel labelInformation;
    private JButton buttonLogin;
    private JLabel labelIcon;

    Project project;

    private final String TITLE = "CAESAR";
    private final String SUBTITLE = "Ci Assistant for Efficient (Build Failure)<br>Summarization And Resolution";
    private final String ICON = "/icons/icon_610x908.png";

    public LoginPanel(Project project) {
        this.project = project;

        // set layout
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // configure and add content panel
        initAndAddContentPanel(20);

        // configure and add label with title
        initTitleLabel(this.TITLE);
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 0;
        this.panelContent.add(labelTitle, c);

        // configure and add label with subtitle
        initSubtitleLabel(this.SUBTITLE);
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 1;
        c.insets = JBUI.insets(20, 0, 0, 0);
        this.panelContent.add(labelSubtitle, c);

        // configure and add label with information message
        initInformationLabel();
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 2;
        c.insets = JBUI.insets(40, 0, 0, 0);
        this.panelContent.add(labelInformation, c);

        // configure and add button to login
        initLoginButton();
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 3;
        c.insets = JBUI.insets(20, 0, 0, 0);
        this.panelContent.add(buttonLogin, c);

        // configure and add label with icon
        initIconLabel(this.ICON);
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 4;
        c.fill = GridBagConstraints.BOTH;
        c.insets = JBUI.insets(50, 0, 0, 0);
        c.weightx = 1.0;
        c.weighty = 1.0;
        this.panelContent.add(this.labelIcon, c);

        // add panel to move content to the top
        // (at least one component needs to have weighty greater than 0.0)
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 5;
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

    private void initTitleLabel(String text) {
        this.labelTitle = new JLabel();
        this.labelTitle.setText(text);

        Font labelFont = this.labelTitle.getFont();
        Font newFont = new Font(labelFont.getName(), Font.BOLD, 20);
        this.labelTitle.setFont(newFont);
    }

    private void initSubtitleLabel(String text) {
        this.labelSubtitle = new JLabel();
        this.labelSubtitle.setText("<html><div style='text-align:center;'>" + text + "</div></html>");
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
                if (!Controller.getInstance().login(LoginPanel.this.project)) {
                    System.out.println("Login failed!");
                }
            }
        });
    }

    private void initIconLabel(String icon) {
        this.labelIcon = new JLabel();
        this.labelIcon.setHorizontalAlignment(JLabel.CENTER);
        this.labelIcon.setVerticalAlignment(JLabel.TOP);

        URL iconURL = LoginPanel.class.getResource(icon);
        ImageIcon iconImage = new ImageIcon(iconURL);

        Dimension maxSize = new Dimension(200, 200);

        this.labelIcon.setIcon(iconImage);

        this.labelIcon.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                // set boundary size (image may not be larger than this)
                int boundaryWidth = labelIcon.getWidth();
                int boundaryHeight = labelIcon.getHeight();
                Dimension boundarySize = new Dimension(boundaryWidth, boundaryHeight);

                // lower boundary size if max size is exceeded
                if (boundarySize.width > maxSize.width) {
                    boundarySize.width = maxSize.width;
                }
                if (boundarySize.height > maxSize.height) {
                    boundarySize.height = maxSize.height;
                }

                // resize image
                ImageIcon scaledIconImage = new ImageIcon(scaleImage(iconImage.getImage(), boundarySize));
                labelIcon.setIcon(scaledIconImage);
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });
    }

    private Image scaleImage(Image image, Dimension boundarySize) {
        int currentWidth = image.getWidth(null);
        int currentHeight = image.getHeight(null);
        int boundWidth = boundarySize.width;
        int boundHeight = boundarySize.height;
        int newWidth = currentWidth;
        int newHeight = currentHeight;

        // scale to bound width
        if (currentWidth > boundWidth) {
            newWidth = boundWidth;
            newHeight = (newWidth * currentHeight) / currentWidth;
        }

        // scale new height
        if (newHeight >boundHeight) {
            newHeight = boundHeight;
            newWidth = (newHeight * currentWidth) / currentHeight;
        }

        // scale image
        return image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
    }
}
