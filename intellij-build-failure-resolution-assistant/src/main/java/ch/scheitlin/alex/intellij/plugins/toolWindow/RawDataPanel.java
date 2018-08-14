package ch.scheitlin.alex.intellij.plugins.toolWindow;

import ch.scheitlin.alex.maven.MavenBuild;
import ch.scheitlin.alex.maven.MavenPanel;

import javax.swing.*;
import java.awt.*;

public class RawDataPanel extends JPanel {
    private MavenPanel mavenPanel;

    public RawDataPanel(MavenBuild mavenBuild) {
        this.mavenPanel = new MavenPanel(mavenBuild);

        // set layout
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // add maven panel
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        this.add(this.mavenPanel, c);
    }
}
