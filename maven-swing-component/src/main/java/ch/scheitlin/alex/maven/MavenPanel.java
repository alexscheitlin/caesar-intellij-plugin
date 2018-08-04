package ch.scheitlin.alex.maven;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class MavenPanel extends JPanel {
    private JTree treeMavenBuild;
    private JLabel labelSelectedGoal;

    private MavenBuild mavenBuild;

    private final Color MODULE_SUCCESS_COLOR = new Color(156, 0, 6);
    private final Color MODULE_FAILURE_COLOR = new Color(0, 97, 0);
    private final Color MODULE_SKIPPED_COLOR = new Color(156, 87, 0);

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
        this.treeMavenBuild.setFont(new Font("Courier", Font.BOLD, 20));

        // set goal icon
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        ImageIcon goalIcon = new ImageIcon(MavenPanel.class.getResource("/goal.png"));
        renderer.setLeafIcon(goalIcon);
        this.treeMavenBuild.setCellRenderer(renderer);

        //this.add(new JScrollPane(this.treeMavenBuild));
        this.treeMavenBuild.setShowsRootHandles(true);
        this.treeMavenBuild.setRootVisible(false);
        this.treeMavenBuild.setCellRenderer(new TreeCellRenderer());

        // show selected goal in label
        this.labelSelectedGoal = new JLabel();
        this.treeMavenBuild.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeMavenBuild.getLastSelectedPathComponent();

                if (selectedNode.isLeaf()) {
                    MavenGoal goal = getGoal(selectedNode.getUserObject().toString());
                    if (goal != null) {
                        labelSelectedGoal.setText(goal.getLines());
                    }
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

    class TreeCellRenderer extends DefaultTreeCellRenderer {

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);

            // get name of node (module or goal name)
            String name = (String) ((DefaultMutableTreeNode) value).getUserObject();

            // get module
            MavenModule module = getModule(name);
            if (module == null) {
                return this;
            }

            if (module.status == MavenModuleStatus.SUCCESS) {
                setForeground(Color.green);
            } else if (module.status == MavenModuleStatus.FAILURE) {
                setForeground(Color.red);
            } else if (module.status == MavenModuleStatus.SKIPPED) {
                setForeground(Color.yellow);
            }

            return this;
        }
    }

    private MavenModule getModule(String moduleName) {
        for (MavenModule module : this.mavenBuild.modules) {
            if (module.name.equals(moduleName)) {
                return module;
            }
        }

        return null;
    }

    private MavenGoal getGoal(String goalName) {
        for (MavenModule module : this.mavenBuild.modules) {
            for (MavenGoal goal : module.goals) {
                if (goal.name.equals(goalName)) {
                    return goal;
                }
            }
        }

        return null;
    }
}
