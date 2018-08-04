package ch.scheitlin.alex.intellij.plugins.toolWindow;

import ch.scheitlin.alex.maven.MavenGoal;
import ch.scheitlin.alex.maven.MavenModule;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.List;

public class TreeViewPanel extends JPanel {
    private JTree treeMavenBuild;
    private JLabel labelSelectedGoal;

    private List<MavenModule> mavenModules;

    public TreeViewPanel (List<MavenModule> mavenModules) {
        this.mavenModules = mavenModules;

        // create tree
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Maven Build");
        for (MavenModule module : this.mavenModules) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(module.name);
            root.add(node);
            for (MavenGoal goal : module.goals) {
                node.add(new DefaultMutableTreeNode(goal.name));
            }
        }

        this.treeMavenBuild = new JTree(root);
        //this.treeMavenBuild = new com.intellij.ui.treeStructure.Tree(root);

        // set goal icon
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        ImageIcon goalIcon = new ImageIcon(TreeViewPanel.class.getResource("/goal.png"));
        renderer.setLeafIcon(goalIcon);
        this.treeMavenBuild.setCellRenderer(renderer);

        //this.add(new JScrollPane(this.treeMavenBuild));
        this.treeMavenBuild.setShowsRootHandles(true);
        //this.treeMavenBuild.setRootVisible(false);

        // show selected goal in label
        this.labelSelectedGoal = new JLabel();
        this.treeMavenBuild.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeMavenBuild.getLastSelectedPathComponent();

                if (selectedNode.isLeaf()) {
                    labelSelectedGoal.setText(selectedNode.getUserObject().toString());
                } else {
                    labelSelectedGoal.setText("");
                }
            }
        });

        // set layout
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // add tree
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        this.add(this.treeMavenBuild, c);

        // add label
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0;
        c.weighty = 0.0;
        this.add(this.labelSelectedGoal, c);
    }
}
