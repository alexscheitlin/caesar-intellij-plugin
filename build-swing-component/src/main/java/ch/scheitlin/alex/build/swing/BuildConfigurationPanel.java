package ch.scheitlin.alex.build.swing;

// TODO: import com.intellij.util.ui.JBUI;

import ch.scheitlin.alex.build.model.Branch;
import ch.scheitlin.alex.build.model.BuildConfiguration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class BuildConfigurationPanel extends JPanel {

    private JPanel row1;
    private JPanel row2;

    private JLabel labelConfigurationName;
    private JLabel labelShowMoreBuilds;
    public BranchPanel[] branchPanels;

    private boolean showAllBuilds;
    private List<Branch> branches;

    private String showMoreFontColor = "#1E90FF";
    private String showMoreFontColorDisabled = "#C0C0C0";

    public BuildConfigurationPanel(BuildConfiguration buildConfiguration, String buildPanelActionButtonText) {
        this.showAllBuilds = false;

        // get configuration information
        String buildConfigurationName = buildConfiguration.getName();
        this.branches = buildConfiguration.getBranches();

        // set layout for build configuration panel
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // set first row with build configuration name and label to show more builds
        this.row1 = new JPanel();
        this.row1.setLayout(new GridBagLayout());

        // configure and add label with configuration name to first row
        initConfigurationNameLabel(buildConfigurationName);
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        this.row1.add(this.labelConfigurationName, c);

        // configure and add label to show more builds to first row
        initShowMoreBuildsLabel();
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_END;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0.0;
        this.row1.add(this.labelShowMoreBuilds, c);

        // add first row to panel
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        this.add(row1, c);

        // set second row with all branch panels
        this.row2 = new JPanel();
        this.row2.setLayout(new GridBagLayout());

        // configure and add branch panels to second row
        initBranchPanels(this.branches, buildPanelActionButtonText);
        for (int i = 0; i < this.branches.size(); i++) {
            c.gridx = 0;
            c.gridy = i;
            c.anchor = GridBagConstraints.LINE_START;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
            this.row2.add(this.branchPanels[i], c);
        }

        // show message if no branches (= build configurations) available for this project
        if (this.branches.size() == 0) {
            // TODO: c.insets = JBUI.insets(20);
            c.insets = new Insets(20, 20, 20, 20);
            this.row2.add(new JLabel("No builds found!"), c);
        }

        // add second row to panel
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        this.add(row2, c);

        // show the specified amount of builds
        showBuilds();
    }

    private void initConfigurationNameLabel(String configurationName) {
        this.labelConfigurationName = new JLabel();
        this.labelConfigurationName.setText(configurationName);

        // set font: bold and larger
        // standard label font size: 12
        Font font = this.labelConfigurationName.getFont();
        Font boldFont = new Font(font.getFontName(), Font.BOLD, 15);
        this.labelConfigurationName.setFont(boldFont);
    }

    private void initShowMoreBuildsLabel() {
        this.labelShowMoreBuilds = new JLabel();

        // get the maximal amount of builds per branch
        int counter = 0;
        for (Branch branch : this.branches) {
            if (branch.getBuilds().size() == 1 && counter == 0) {
                counter = 1;
            } else if (branch.getBuilds().size() > 1 && counter < branch.getBuilds().size()) {
                counter = branch.getBuilds().size();
            }
        }

        // only enable "show more" label if there are branches with more than just one build
        if (counter > 1) {
            this.labelShowMoreBuilds.setText("<html><u style='color: " + showMoreFontColor + ";'>show more</u></html>");

            // add click handler to show more builds
            this.labelShowMoreBuilds.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);

                    String labelText = showAllBuilds ? "show more"  : "show less";

                    // toggle boolean
                    showAllBuilds = !showAllBuilds;

                    // update ui
                    showBuilds();
                    labelShowMoreBuilds.setText(
                            "<html><u style='color: " + showMoreFontColor + ";'>" + labelText + "</u></html>"
                    );
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    super.mouseEntered(e);
                    labelShowMoreBuilds.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    super.mouseExited(e);
                    labelShowMoreBuilds.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            });
        } else {
            this.labelShowMoreBuilds.setText("<html><u style='color: " + showMoreFontColorDisabled + ";'>show more</u></html>");
        }
    }

    private void initBranchPanels(List<Branch> branches, String buildPanelActionButtonText) {
        this.branchPanels = new BranchPanel[branches.size()];

        for (int i = 0; i < this.branchPanels.length; i++) {
            this.branchPanels[i] = new BranchPanel(branches.get(i), this.showAllBuilds, buildPanelActionButtonText);
        }
    }

    private void showBuilds() {
        // update branch panels (either display just the last build or all)
        for (BranchPanel branchPanel : this.branchPanels) {
            branchPanel.showBuilds(this.showAllBuilds);
        }
    }

    public void setBranchFontColor(Color color) {
        for (BranchPanel branchPanel : this.branchPanels) {
            branchPanel.setBranchFontColor(color);
        }
    }
}
