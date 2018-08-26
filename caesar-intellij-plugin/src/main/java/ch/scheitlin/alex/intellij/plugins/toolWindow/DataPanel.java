package ch.scheitlin.alex.intellij.plugins.toolWindow;

import ch.scheitlin.alex.intellij.plugins.dialogs.MultiLineStringDialog;
import ch.scheitlin.alex.maven.MavenBuild;
import ch.scheitlin.alex.maven.MavenPanel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;

public class DataPanel extends JPanel {
    private JButton buttonRawBuildServerBuildLog;
    private MavenPanel mavenPanel;

    public DataPanel(String rawBuildServerBuildLog, MavenBuild mavenBuild) {
        // set layout
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // add button to display raw build server build log
        this.buttonRawBuildServerBuildLog = new JButton();
        this.buttonRawBuildServerBuildLog.setText("Show Build Server Build Log");
        this.buttonRawBuildServerBuildLog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MultiLineStringDialog dialog = new MultiLineStringDialog();
                dialog.showDialog(rawBuildServerBuildLog);
            }
        });
        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 10, 0);
        c.weightx = 0.0;
        c.weighty = 0.0;
        this.add(this.buttonRawBuildServerBuildLog, c);

        // add maven panel
        this.mavenPanel = new MavenPanel(mavenBuild);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(0, 0, 0, 0);
        c.weightx = 1.0;
        c.weighty = 1.0;
        this.add(this.mavenPanel, c);
    }
}
