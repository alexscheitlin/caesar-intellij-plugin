package ch.scheitlin.alex.maven;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.List;
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

public class MavenPanel extends JPanel {
    // data
    private MavenBuild mavenBuild;

    // components
    private JLabel labelSuccessModuleLegend;
    private JLabel labelFailureModuleLegend;
    private JLabel labelSkippedModuleLegend;
    private JLabel labelGoalLegend;
    private JTree treeMavenBuild;
    private JScrollPane treeScrollPane;
    private JTextPane labelSelectedGoal;
    private JScrollPane linesScrollPane;

    // appearance constants
    private final String MODULE_SUCCESS_LEGEND_TEXT = "Successful Module";
    private final String MODULE_FAILURE_LEGEND_TEXT = "Failed Module";
    private final String MODULE_SKIPPED_LEGEND_TEXT = "Skipped Module";
    private final String GOAL_LEGEND_TEXT = "Executed Goal";
    private final Color MODULE_SUCCESS_COLOR = new Color(0, 97, 0);
    private final Color MODULE_FAILURE_COLOR = new Color(156, 0, 6);
    private final Color MODULE_SKIPPED_COLOR = new Color(156, 87, 0);
    private final Color GOAL_COLOR = Color.BLACK;
    private final String MODULE_SUCCESS_ICON_RESOURCE_PATH = "/module_success.png";
    private final String MODULE_FAILURE_ICON_RESOURCE_PATH = "/module_failure.png";
    private final String MODULE_SKIPPED_ICON_RESOURCE_PATH = "/module_skipped.png";
    private final String GOAL_ICON_RESOURCE_PATH = "/goal.png";
    private final int SELECTED_GOAL_LABEL_HEIGHT = 200;

    public MavenPanel(MavenBuild mavenBuild) {
        // set data variables
        this.mavenBuild = mavenBuild;

        // get build information
        List<MavenModule> modules = this.mavenBuild.getModules();

        // set layout
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // initialize label with success module legend
        this.labelSuccessModuleLegend = initLegendLabel(
                this.MODULE_SUCCESS_LEGEND_TEXT,
                this.MODULE_SUCCESS_COLOR,
                this.MODULE_SUCCESS_ICON_RESOURCE_PATH
        );
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 20, 10);
        c.weightx = 0.0;
        c.weighty = 0.0;
        this.add(this.labelSuccessModuleLegend, c);

        // initialize label with failed module legend
        this.labelFailureModuleLegend = initLegendLabel(
                this.MODULE_FAILURE_LEGEND_TEXT,
                this.MODULE_FAILURE_COLOR,
                this.MODULE_FAILURE_ICON_RESOURCE_PATH
        );
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 1;
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 20, 10);
        c.weightx = 0.0;
        c.weighty = 0.0;
        this.add(this.labelFailureModuleLegend, c);

        // initialize label with skipped module legend
        this.labelSkippedModuleLegend = initLegendLabel(
                this.MODULE_SKIPPED_LEGEND_TEXT,
                this.MODULE_SKIPPED_COLOR,
                this.MODULE_SKIPPED_ICON_RESOURCE_PATH
        );
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 1;
        c.gridx = 2;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 20, 10);
        c.weightx = 0.0;
        c.weighty = 0.0;
        this.add(this.labelSkippedModuleLegend, c);

        // initialize label with goal legend
        this.labelGoalLegend = initLegendLabel(
                this.GOAL_LEGEND_TEXT,
                this.GOAL_COLOR,
                this.GOAL_ICON_RESOURCE_PATH
        );
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 1;
        c.gridx = 3;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 20, 10);
        c.weightx = 0.0;
        c.weighty = 0.0;
        this.add(this.labelGoalLegend, c);

        // initialize tree with maven modules and goals
        this.treeMavenBuild = initMavenBuildTree(modules);
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

        // initialize label with goal lines
        this.labelSelectedGoal = initSelectedGoalLabel();
        this.linesScrollPane = new JScrollPane(this.labelSelectedGoal);
        this.linesScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        Dimension linesDimension = new Dimension(new Dimension(0, this.SELECTED_GOAL_LABEL_HEIGHT));
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

        // resize scroll panels for tree and selected goal when MavenPanel is resized
        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                double linesSize = 0.25;
                Dimension treeDimension = new Dimension(new Dimension(0, (int) (getHeight() * (1.0 - linesSize))));
                Dimension linesDimension = new Dimension(new Dimension(0, (int) (getHeight() * linesSize)));

                // resize tree
                treeScrollPane.setMaximumSize(treeDimension);
                treeScrollPane.setPreferredSize(treeDimension);
                treeScrollPane.setMinimumSize(treeDimension);

                // resize selected goal
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

    private JLabel initLegendLabel(String text, Color color, String iconResourcePath) {
        JLabel label = new JLabel();
        label.setText(text);
        label.setForeground(color);
        label.setIcon(loadResourceIcon(iconResourcePath));

        return label;
    }

    private JTree initMavenBuildTree(List<MavenModule> modules) {
        // create tree root
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(null);

        // add modules to root
        for (MavenModule module : modules) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(module.getName());
            root.add(node);

            // add goals to module
            for (MavenGoal goal : module.getGoals()) {
                node.add(new DefaultMutableTreeNode(goal.getName()));
            }
        }

        // create tree
        JTree tree = new JTree(root);
        tree.setShowsRootHandles(false); // hide handles before modules
        tree.setRootVisible(false);      // hide root (maven modules are the highest level to be shown)
        tree.setCellRenderer(new TreeCellRenderer());  // add icons and colors to tree nodes

        // expand all modules to make goals visible
        expandAllNodes(tree, 0, tree.getRowCount());

        // update label with goal lines if a new goal is selected in the tree
        tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeMavenBuild.getLastSelectedPathComponent();

                // show goal lines of selected goal
                if (selectedNode.isLeaf()) {
                    MavenGoal goal = getGoal(mavenBuild, selectedNode.getUserObject().toString());
                    if (goal != null) {
                        labelSelectedGoal.setText(goal.getLinesAsMultiLineString());

                        // scroll to top of goal lines
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                linesScrollPane.getVerticalScrollBar().setValue(0);
                            }
                        });
                    }
                } else {
                    // nothing to display for modules
                    labelSelectedGoal.setText("");
                }
            }
        });

        return tree;
    }

    private JTextPane initSelectedGoalLabel() {
        JTextPane label = new JTextPane();
        label.setEditable(false);
        label.setCursor(null);
        label.setOpaque(false);
        label.setFocusable(false);

        return label;
    }

    private ImageIcon loadResourceIcon(String path) {
        return new ImageIcon(MavenPanel.class.getResource(path));
    }

    private MavenModule getModule(MavenBuild build, String moduleName) {
        for (MavenModule module : build.getModules()) {
            if (module.getName().equals(moduleName)) {
                return module;
            }
        }

        return null;
    }

    private MavenGoal getGoal(MavenBuild build, String goalName) {
        for (MavenModule module : build.getModules()) {
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

    // tree cell renderer to add custom icons and colors to the tree
    class TreeCellRenderer extends DefaultTreeCellRenderer {

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);

            // set goal icon for all tree leaves
            ImageIcon goalIcon = loadResourceIcon(GOAL_ICON_RESOURCE_PATH);
            this.setLeafIcon(goalIcon);

            // get the tree node to treat (every node will be treated with the following code)
            DefaultMutableTreeNode treeNode = ((DefaultMutableTreeNode) value);

            // get name of node (module or goal name)
            String name = (String) treeNode.getUserObject();

            // get module name
            MavenModule module = getModule(mavenBuild, name);
            if (module == null) {

                // do not proceed if the nod is not a module
                return this;
            }

            // load module icons
            ImageIcon moduleSuccessIcon = loadResourceIcon(MODULE_SUCCESS_ICON_RESOURCE_PATH);
            ImageIcon moduleFailureIcon = loadResourceIcon(MODULE_FAILURE_ICON_RESOURCE_PATH);
            ImageIcon moduleSkippedIcon = loadResourceIcon(MODULE_SKIPPED_ICON_RESOURCE_PATH);

            // set module icons and colors depending on module status
            MavenModuleBuildStatus status = module.getStatus();
            if (status == MavenModuleBuildStatus.SUCCESS) {
                setForeground(MODULE_SUCCESS_COLOR);
                setIcon(moduleSuccessIcon);
            } else if (status == MavenModuleBuildStatus.FAILURE) {
                setForeground(MODULE_FAILURE_COLOR);
                setIcon(moduleFailureIcon);
            } else if (status == MavenModuleBuildStatus.SKIPPED) {
                setForeground(MODULE_SKIPPED_COLOR);
                setIcon(moduleSkippedIcon);
            }

            // set font bold
            setFont(new Font(getFont().getName(), Font.BOLD, getFont().getSize()));

            return this;
        }
    }
}
