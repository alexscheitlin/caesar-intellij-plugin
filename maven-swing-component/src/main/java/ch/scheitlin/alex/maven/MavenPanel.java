package ch.scheitlin.alex.maven;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class MavenPanel extends JPanel {
    private JLabel labelSuccessModule;
    private JLabel labelFailureModule;
    private JLabel labelSkippedModule;
    private JLabel labelGoal;
    private JTree treeMavenBuild;
    private JTextPane labelSelectedGoal;
    private JScrollPane treeScrollPane;
    private JScrollPane linesScrollPane;

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

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                linesScrollPane.getVerticalScrollBar().setValue(0);
                            }
                        });
                    }
                } else {
                    labelSelectedGoal.setText("");
                }
            }
        });

        // load legend icons
        ImageIcon moduleSuccessIcon = new ImageIcon(MavenPanel.class.getResource("/module_success.png"));
        ImageIcon moduleFailureIcon = new ImageIcon(MavenPanel.class.getResource("/module_failure.png"));
        ImageIcon moduleSkippedIcon = new ImageIcon(MavenPanel.class.getResource("/module_skipped.png"));
        ImageIcon goalIcon = new ImageIcon(MavenPanel.class.getResource("/goal.png"));

        // create legend labels
        this.labelSuccessModule = new JLabel("Successful Module");
        this.labelSuccessModule.setForeground(MODULE_SUCCESS_COLOR);
        this.labelSuccessModule.setIcon(moduleSuccessIcon);

        this.labelFailureModule = new JLabel("Failed Module");
        this.labelFailureModule.setForeground(MODULE_FAILURE_COLOR);
        this.labelFailureModule.setIcon(moduleFailureIcon);

        this.labelSkippedModule = new JLabel("Skipped Module");
        this.labelSkippedModule.setForeground(MODULE_SKIPPED_COLOR);
        this.labelSkippedModule.setIcon(moduleSkippedIcon);

        this.labelGoal = new JLabel("Executed Goal");
        this.labelGoal.setIcon(goalIcon);

        // set layout
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // add legend labels
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 1;
        c.insets = new Insets(0, 0, 20, 10);
        c.weightx = 0.0;
        c.weighty = 0.0;

        c.gridx = 0;
        c.gridy = 0;
        this.add(this.labelSuccessModule, c);
        c.gridx = 1;
        c.gridy = 0;
        this.add(this.labelFailureModule, c);
        c.gridx = 2;
        c.gridy = 0;
        this.add(this.labelSkippedModule, c);
        c.gridx = 3;
        c.gridy = 0;
        this.add(this.labelGoal, c);

        // add tree
        this.treeScrollPane = new JScrollPane(this.treeMavenBuild);
        this.treeScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = 5;
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(0, 0, 0, 0);
        c.weightx = 1.0;
        c.weighty = 1.0;
        this.add(this.treeScrollPane, c);

        // add label
        this.linesScrollPane = new JScrollPane(this.labelSelectedGoal);
        this.linesScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        Dimension linesDimension = new Dimension(new Dimension(100, 200));
        this.linesScrollPane.setMaximumSize(linesDimension);
        this.linesScrollPane.setPreferredSize(linesDimension);
        this.linesScrollPane.setMinimumSize(linesDimension);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 5;
        c.gridx = 0;
        c.gridy = 2;
        c.insets = new Insets(0, 0, 0, 0);
        c.weightx = 1.0;
        c.weighty = 0.0;
        this.add(this.linesScrollPane, c);

        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                double linesSize = 0.25;
                Dimension treeDimension = new Dimension(new Dimension(100, (int) (getHeight() * (1.0 - linesSize))));
                Dimension linesDimension = new Dimension(new Dimension(100, (int) (getHeight() * linesSize)));

                treeScrollPane.setMaximumSize(treeDimension);
                treeScrollPane.setPreferredSize(treeDimension);
                treeScrollPane.setMinimumSize(treeDimension);

                linesScrollPane.setMaximumSize(linesDimension);
                linesScrollPane.setPreferredSize(linesDimension);
                linesScrollPane.setMinimumSize(linesDimension);
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

            // set module icon
            ImageIcon moduleIcon = new ImageIcon(MavenPanel.class.getResource("/module.png"));
            ImageIcon moduleSuccessIcon = new ImageIcon(MavenPanel.class.getResource("/module_success.png"));
            ImageIcon moduleFailureIcon = new ImageIcon(MavenPanel.class.getResource("/module_failure.png"));
            ImageIcon moduleSkippedIcon = new ImageIcon(MavenPanel.class.getResource("/module_skipped.png"));
            setIcon(moduleIcon);

            // set module colors depending on module status
            MavenModuleStatus status = module.getStatus();
            if (status == MavenModuleStatus.SUCCESS) {
                setForeground(MODULE_SUCCESS_COLOR);
                setIcon(moduleSuccessIcon);
            } else if (status == MavenModuleStatus.FAILURE) {
                setForeground(MODULE_FAILURE_COLOR);
                setIcon(moduleFailureIcon);
            } else if (status == MavenModuleStatus.SKIPPED) {
                setForeground(MODULE_SKIPPED_COLOR);
                setIcon(moduleSkippedIcon);
            }

            // set font bold
            setFont(new Font(getFont().getName(), Font.BOLD, getFont().getSize()));

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

    private void expandAllNodes(JTree tree, int startingIndex, int rowCount) {
        for (int i = startingIndex; i < rowCount; ++i) {
            tree.expandRow(i);
        }

        if (tree.getRowCount() != rowCount) {
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }
}
