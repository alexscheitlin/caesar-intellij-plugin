package ch.scheitlin.alex.build.swing;

import ch.scheitlin.alex.build.model.Branch;
import ch.scheitlin.alex.build.model.Build;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BranchPanel extends JPanel {

    private JLabel labelBranchName;
    public BuildPanel[] buildPanels;

    private boolean showAllBuilds;

    private Color branchFontColor = Color.BLUE;

    public BranchPanel(Branch branch, boolean showAllBuilds, String buildPanelActionButtonText) {
        this.showAllBuilds = showAllBuilds;

        // get branch information
        String branchName = branch.getName();
        List<Build> builds = branch.getBuilds();

        // set layout for branch panel
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // configure and add label with branch name
        initBranchNameLabel(branchName);
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0.0;
        c.insets = new Insets(0, 0, 0, 10);
        this.add(this.labelBranchName, c);

        // configure and add panels with builds
        initBuildPanels(builds, buildPanelActionButtonText);
        for (int i = 0; i < builds.size(); i++) {
            c.gridx = 1;
            c.gridy = i;
            c.anchor = GridBagConstraints.LINE_START;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
            c.insets = new Insets(0, 0, 0, 0);
            this.add(this.buildPanels[i], c);
        }

        // show the specified amount of builds
        showBuilds(this.showAllBuilds);
    }

    private void initBranchNameLabel(String branchName) {
        Dimension dimension = new Dimension(50, 20);

        this.labelBranchName = new JLabel();
        this.labelBranchName.setText(branchName == null ? "default" : branchName);
        this.labelBranchName.setMinimumSize(dimension);
        this.labelBranchName.setPreferredSize(dimension);
        this.labelBranchName.setMaximumSize(dimension);
        this.labelBranchName.setOpaque(true);
        //this.labelBranchName.setBackground(JBColor.blue);
        //this.labelBranchName.setForeground(JBColor.white);
        //this.labelBranchName.setBorder(new LineBorder(JBColor.BLUE, 3, true));
        this.labelBranchName.setForeground(branchFontColor);
        this.labelBranchName.setFont(new Font("Verdana", Font.BOLD + Font.ITALIC, 12));
    }

    private void initBuildPanels(List<Build> builds, String buildPanelActionButtonText) {
        this.buildPanels = new BuildPanel[builds.size()];

        for (int i = 0; i < this.buildPanels.length; i++) {
            this.buildPanels[i] = new BuildPanel(builds.get(i), buildPanelActionButtonText);
        }
    }

    public void showBuilds(boolean showAllBuilds) {
        this.showAllBuilds = showAllBuilds;

        // show the specified amount of builds (either just the last one or all)
        for (int i = 1; i < this.buildPanels.length; i++) {
            this.buildPanels[i].setVisible(this.showAllBuilds);
        }
    }

    public void setBranchFontColor(Color color) {
        this.branchFontColor = color;
        this.labelBranchName.setForeground(color);
    }
}
