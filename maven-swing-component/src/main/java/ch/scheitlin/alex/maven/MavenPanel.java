package ch.scheitlin.alex.maven;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class MavenPanel extends JPanel {
    private JTree treeMavenBuild;
    private JLabel labelSelectedGoal;

    private MavenBuild mavenBuild;

    public MavenPanel(MavenBuild mavenBuild) {
        this.mavenBuild = mavenBuild;

        // create tree
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Maven Build");

        // add modules to tree
        for (MavenModule module : this.mavenBuild.modules) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(module.name);
            root.add(node);

            // add goals to module
            for (MavenGoal goal : module.goals) {
                node.add(new DefaultMutableTreeNode(goal.name));
            }
        }
        this.treeMavenBuild = new JTree(root);

        // set goal icon
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        ImageIcon goalIcon = new ImageIcon(MavenPanel.class.getResource("/goal.png"));
        renderer.setLeafIcon(goalIcon);
        this.treeMavenBuild.setCellRenderer(renderer);

        //this.add(new JScrollPane(this.treeMavenBuild));
        this.treeMavenBuild.setShowsRootHandles(true);
        this.treeMavenBuild.setRootVisible(false);

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
