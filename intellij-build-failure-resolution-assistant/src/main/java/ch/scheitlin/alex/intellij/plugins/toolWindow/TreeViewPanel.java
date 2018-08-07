package ch.scheitlin.alex.intellij.plugins.toolWindow;

import ch.scheitlin.alex.maven.MavenBuild;
import ch.scheitlin.alex.maven.MavenPanel;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import java.awt.*;

public class TreeViewPanel extends JPanel {
    private JBScrollPane scrollPane;
    private MavenPanel mavenPanel;

    public TreeViewPanel (MavenBuild mavenBuild) {
        this.mavenPanel = new MavenPanel(mavenBuild);

        // create scroll pane with tree
        this.scrollPane = new JBScrollPane(this.mavenPanel);
        this.scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // set layout
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // add scroll pane
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        this.add(this.scrollPane, c);
    }
}
