package ch.scheitlin.alex.maven;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class MavenPanel extends JPanel {
    private JTree treeMavenBuild;
    private JTextPane labelSelectedGoal;

    private MavenBuild mavenBuild;

    private final Color MODULE_SUCCESS_COLOR = new Color(0, 97, 0);
    private final Color MODULE_FAILURE_COLOR = new Color(156, 0, 6);
    private final Color MODULE_SKIPPED_COLOR = new Color(156, 87, 0);

    public MavenPanel(MavenBuild mavenBuild) {
        this.mavenBuild = mavenBuild;

        // create tree
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Maven Build");

        // add modules to tree
        for (MavenModule module : this.mavenBuild.getModules()) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(module.getName());
            root.add(node);

            // add goals to module
            for (MavenGoal goal : module.getGoals()) {
                node.add(new DefaultMutableTreeNode(goal.getName()));
            }
        }
        this.treeMavenBuild = new JTree(root);
        //this.treeMavenBuild.setFont(new Font("Courier", Font.BOLD, 20));

        //this.add(new JScrollPane(this.treeMavenBuild));
        this.treeMavenBuild.setShowsRootHandles(false);     // hide handles before modules
        this.treeMavenBuild.setRootVisible(false);          // hide root (maven modules are the highest level to be shown)
        this.treeMavenBuild.setCellRenderer(new TreeCellRenderer());
        expandAllNodes(this.treeMavenBuild, 0, this.treeMavenBuild.getRowCount());

        // show selected goal in label
        this.labelSelectedGoal = new JTextPane();
        this.labelSelectedGoal.setEditable(false);
        this.labelSelectedGoal.setCursor(null);
        this.labelSelectedGoal.setOpaque(false);
        this.labelSelectedGoal.setFocusable(false);
        this.treeMavenBuild.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeMavenBuild.getLastSelectedPathComponent();

                if (selectedNode.isLeaf()) {
                    MavenGoal goal = getGoal(selectedNode.getUserObject().toString());
                    if (goal != null) {
                        labelSelectedGoal.setText(goal.getLinesAsMultiLineString());
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

            // set goal icon
            ImageIcon goalIcon = new ImageIcon(MavenPanel.class.getResource("/goal.png"));
            this.setLeafIcon(goalIcon);

            // tree node
            DefaultMutableTreeNode treeNode = ((DefaultMutableTreeNode) value);

            // get name of node (module or goal name)
            String name = (String) treeNode.getUserObject();

            // get module
            MavenModule module = getModule(name);
            if (module == null) {
                return this;
            }

            // set module colors depending on module status
            MavenModuleStatus status = module.getStatus();
            if (status == MavenModuleStatus.SUCCESS) {
                setForeground(MODULE_SUCCESS_COLOR);
            } else if (status == MavenModuleStatus.FAILURE) {
                setForeground(MODULE_FAILURE_COLOR);
            } else if (status == MavenModuleStatus.SKIPPED) {
                setForeground(MODULE_SKIPPED_COLOR);
            }

            // set module icon
            ImageIcon moduleIcon = new ImageIcon(MavenPanel.class.getResource("/module.png"));
            setIcon(moduleIcon);

            return this;
        }
    }

    private MavenModule getModule(String moduleName) {
        for (MavenModule module : this.mavenBuild.getModules()) {
            if (module.getName().equals(moduleName)) {
                return module;
            }
        }

        return null;
    }

    private MavenGoal getGoal(String goalName) {
        for (MavenModule module : this.mavenBuild.getModules()) {
            for (MavenGoal goal : module.getGoals()) {
                if (goal.getName().equals(goalName)) {
                    return goal;
                }
            }
        }

        return null;
    }

    private void expandAllNodes(JTree tree, int startingIndex, int rowCount){
        for(int i=startingIndex;i<rowCount;++i){
            tree.expandRow(i);
        }

        if(tree.getRowCount()!=rowCount){
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }
}
