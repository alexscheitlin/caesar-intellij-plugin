package ch.scheitlin.alex.build.swing;

import ch.scheitlin.alex.build.model.Branch;
import ch.scheitlin.alex.build.model.Build;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BranchPanel extends JPanel {
    // data
    private Branch branch;
    private boolean showAllBuilds;

    // components
    private JLabel labelBranchName;
    public BuildPanel[] buildPanels;

    // appearance settings
    private Color branchFontColor = Color.BLUE;

    // appearance constants
    private final String DEFAULT_BRANCH_NAME = "default";
    private final int BRANCH_NAME_LABEL_WIDTH = 50;
    private final int BRANCH_NAME_FONT_STYLE = Font.BOLD + Font.ITALIC;

    public BranchPanel(Branch branch, boolean showAllBuilds, String buildPanelActionButtonText) {
        // set data variables
        this.branch = branch;
        this.showAllBuilds = showAllBuilds;

        // get branch information
        String branchName = this.branch.getName();
        List<Build> builds = this.branch.getBuilds();

        // set layout for the BranchPanel
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // initialize label with branch name
        this.labelBranchName = initBranchNameLabel(
                branchName,
                this.branchFontColor,
                this.DEFAULT_BRANCH_NAME,
                this.BRANCH_NAME_LABEL_WIDTH,
                this.BRANCH_NAME_FONT_STYLE
        );
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.0;
        c.insets = new Insets(0, 0, 0, 10);
        this.add(this.labelBranchName, c);

        // initialize build panels
        this.buildPanels = initBuildPanels(builds, buildPanelActionButtonText);
        for (int i = 0; i <  this.buildPanels.length; i++) {
            c.anchor = GridBagConstraints.LINE_START;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 1;
            c.gridy = i;
            c.weightx = 1.0;
            c.insets = new Insets(0, 0, 0, 0);
            this.add(this.buildPanels[i], c);
        }

        // show the specified amount of builds
        showBuilds(this.showAllBuilds);
    }

    private JLabel initBranchNameLabel(
            String branchName,
            Color fontColor,
            String defaultBranchName,
            int labelWidth,
            int fontStyle) {
        JLabel label = new JLabel();
        label.setText(branchName == null ? defaultBranchName : branchName);
        label.setForeground(fontColor);

        // set font
        String fontName = label.getFont().getFontName();
        int fontSize = label.getFont().getSize();
        Font newFont = new Font(fontName, fontStyle, fontSize);
        label.setFont(newFont);

        // set size
        Dimension dimension = new Dimension(labelWidth, 20); // 20 is default label height
        label.setMinimumSize(dimension);
        label.setPreferredSize(dimension);
        label.setMaximumSize(dimension);

        return label;
    }

    private BuildPanel[] initBuildPanels(List<Build> builds, String buildPanelActionButtonText) {
        BuildPanel[] panels = new BuildPanel[builds.size()];

        for (int i = 0; i < panels.length; i++) {
            panels[i] = new BuildPanel(builds.get(i), buildPanelActionButtonText);
        }

        return panels;
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
