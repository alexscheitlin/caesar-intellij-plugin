package ch.scheitlin.alex.intellij.plugins.toolWindow;

import ch.scheitlin.alex.maven.MavenBuild;
import ch.scheitlin.alex.maven.MavenPanel;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;

public class DataPanel extends JPanel {
    private JButton buttonBuildServerBuildLog;
    private JButton buttonMavenBuildLog;
    private MavenPanel mavenPanel;

    private final String BUTTON_BUILD_SERVER_BUILD_LOG_TEXT = "Build Server Build Log";
    private final String BUTTON_MAVEN_BUILD_LOG_TEXT = "Maven Build Log";

    public DataPanel(
            MavenBuild mavenBuild,
            ActionListener buildServerBuildLogAction,
            ActionListener mavenBuildLogAction) {
        // set layout
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // add button to display build server build log
        this.buttonBuildServerBuildLog = new JButton();
        this.buttonBuildServerBuildLog.setText(this.BUTTON_BUILD_SERVER_BUILD_LOG_TEXT);
        this.buttonBuildServerBuildLog.addActionListener(buildServerBuildLogAction);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.insets = new Insets(0, 0, 10, 0);
        c.weightx = 0.5;
        c.weighty = 0.0;
        this.add(this.buttonBuildServerBuildLog, c);

        // add button to display build server build log
        this.buttonMavenBuildLog = new JButton();
        this.buttonMavenBuildLog.setText(this.BUTTON_MAVEN_BUILD_LOG_TEXT);
        this.buttonMavenBuildLog.addActionListener(mavenBuildLogAction);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        c.insets = new Insets(0, 0, 10, 0);
        c.weightx = 0.5;
        c.weighty = 0.0;
        this.add(this.buttonMavenBuildLog, c);

        // add maven panel
        this.mavenPanel = new MavenPanel(mavenBuild);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.insets = new Insets(0, 0, 0, 0);
        c.weightx = 1.0;
        c.weighty = 1.0;
        this.add(this.mavenPanel, c);
    }
}
