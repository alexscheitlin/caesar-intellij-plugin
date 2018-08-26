package ch.scheitlin.alex.intellij.plugins.toolWindow;

import ch.scheitlin.alex.intellij.plugins.services.Controller;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FixPanel extends JPanel {
    private JPanel panelContent;
    private JTextArea labelInformation;
    private JButton buttonContinue;

    public FixPanel() {
        // set layout
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // configure and add content panel
        initAndAddContentPanel(20);

        // configure and add label with information message
        initInformationLabel();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = JBUI.insets(0);
        c.weightx = 1.0;
        this.panelContent.add(labelInformation, c);

        // configure and add button to continue
        initContinueButton();
        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.NONE;
        c.insets = JBUI.insets(20, 0, 0, 0);
        c.weightx = 0.0;
        this.panelContent.add(buttonContinue, c);

        // add panel to move content to the top
        // (at least one component needs to have weighty greater than 0.0)
        c.gridx = 0;
        c.gridy = 2;
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

    private void initInformationLabel() {
        this.labelInformation = new JTextArea();
        this.labelInformation.setText("The version of the broken build was loaded successfully to a new branch called '...'.\n" +
                "Please fix the build, commit your changes, and merge the branch into the original branch that triggered the broken build.\n" +
                "Once you are finished click the following button to go back to your work (load stashed changes to the branch you previously worked on."
        );

        this.labelInformation.setEditable(false);
        this.labelInformation.setCursor(null);
        this.labelInformation.setOpaque(false);
        this.labelInformation.setFocusable(false);
        this.labelInformation.setFont(UIManager.getFont("Label.font"));
        this.labelInformation.setWrapStyleWord(true);
        this.labelInformation.setLineWrap(true);
    }

    private void initContinueButton() {
        this.buttonContinue = new JButton();
        this.buttonContinue.setText("Continue");
        this.buttonContinue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!Controller.getInstance().stopFixingBrokenBuild()) {
                    System.out.println("Could not finish build fixing!");
                }
            }
        });
    }
}
