package ch.scheitlin.alex.build.swing;

import ch.scheitlin.alex.build.model.Branch;
import ch.scheitlin.alex.build.model.BuildConfiguration;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BuildConfigurationPanel extends JPanel {
    // data
    private BuildConfiguration buildConfiguration;

    // components
    private JPanel panelTitleRow;
    private JPanel panelContentRow;
    private JLabel labelConfigurationName;
    private JLabel labelShowAllBuilds;
    public BranchPanel[] branchPanels;

    // appearance settings
    private boolean showAllBuilds = false;

    // appearance constants
    private final int BUILD_CONFIGURATION_NAME_LABEL_FONT_STYLE = Font.BOLD;
    private final int BUILD_CONFIGURATION_NAME_LABEL_FONT_SIZE = 15; // label default is 12
    private final String SHOW_ONE_BUILD_TEXT = "show one";
    private final String SHOW_ALL_BUILDS_TEXT = "show all";
    private final String SHOW_ALL_BUILDS_ENABLED_COLOR = "1E90FF";
    private final String SHOW_ALL_BUILDS_DISABLED_COLOR = "C0C0C0";
    private final String NO_BUILD_CONFIGURATIONS_AVAILABLE_MESSAGE = "No builds found!";

    public BuildConfigurationPanel(BuildConfiguration buildConfiguration, String buildPanelActionButtonText) {
        // set data variables
        this.buildConfiguration = buildConfiguration;

        // get configuration information
        String buildConfigurationName = this.buildConfiguration.getName();
        List<Branch> buildConfigurationBranches = this.buildConfiguration.getBranches();

        // set layout for the BuildConfigurationPanel
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // initialize title panel
        // with build configuration name and label to show just one or all builds per branch
        this.panelTitleRow = initTitleRowPanel(
                buildConfigurationName,
                this.BUILD_CONFIGURATION_NAME_LABEL_FONT_STYLE, this.BUILD_CONFIGURATION_NAME_LABEL_FONT_SIZE,
                buildConfigurationBranches,
                this.SHOW_ONE_BUILD_TEXT, this.SHOW_ALL_BUILDS_TEXT,
                this.SHOW_ALL_BUILDS_ENABLED_COLOR, this.SHOW_ALL_BUILDS_DISABLED_COLOR
        );
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        this.add(this.panelTitleRow, c);

        // initialize content panel with branch panels
        this.panelContentRow = initContentRowPanel(
                buildConfigurationBranches,
                this.NO_BUILD_CONFIGURATIONS_AVAILABLE_MESSAGE,
                this.showAllBuilds,
                buildPanelActionButtonText
        );
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0;
        this.add(this.panelContentRow, c);

        // show the specified amount of builds
        showBuilds();
    }

    private JPanel initTitleRowPanel(
            String buildConfigurationName,
            int buildConfigurationNameLabelFontStyle, int buildConfigurationNameLabelFontSize,
            List<Branch> buildConfigurationBranches,
            String showOneBuildText, String showAllBuildsText,
            String showAllBuildsEnabledColor, String showAllBuildsDisabledColor) {
        JPanel panel = new JPanel();

        // set layout for panel
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // initialize label with the name of the build configuration
        this.labelConfigurationName = initConfigurationNameLabel(
                buildConfigurationName,
                buildConfigurationNameLabelFontStyle,
                buildConfigurationNameLabelFontSize
        );
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        panel.add(this.labelConfigurationName, c);

        // initialize label to show just one or all builds per branch
        this.labelShowAllBuilds = initShowAllBuildsLabel(
                buildConfigurationBranches,
                showAllBuilds,
                showOneBuildText, showAllBuildsText,
                showAllBuildsEnabledColor, showAllBuildsDisabledColor
        );
        c.anchor = GridBagConstraints.LINE_END;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.0;
        panel.add(this.labelShowAllBuilds, c);

        return panel;
    }

    private JLabel initConfigurationNameLabel(String name, int fontStyle, int fontSize) {
        JLabel label = new JLabel();
        label.setText(name);

        // set font style and font size
        String fontName = label.getFont().getFontName();
        Font newFont = new Font(fontName, fontStyle, fontSize);
        label.setFont(newFont);

        return label;
    }

    private JLabel initShowAllBuildsLabel(
            List<Branch> branches,
            boolean allBuilds,
            String showOneBuildText, String showAllBuildsText,
            String showAllBuildsEnabled, String showAllBuildsDisabled) {
        JLabel label = new JLabel();

        // get the max amount of builds per branch
        int maxBuildsPerBranch = 0;
        for (Branch branch : branches) {
            if (branch.getBuilds().size() == 1 && maxBuildsPerBranch == 0) {
                maxBuildsPerBranch = 1;
            } else if (branch.getBuilds().size() > 1 && maxBuildsPerBranch < branch.getBuilds().size()) {
                maxBuildsPerBranch = branch.getBuilds().size();
            }
        }

        String labelText = allBuilds ? showOneBuildText : showAllBuildsText;
        String labelColor = maxBuildsPerBranch > 1 ? showAllBuildsEnabled : showAllBuildsDisabled;

        String labelContent = "<html><u style='color: " + labelColor + ";'>" + labelText + "</u></html>";
        label.setText(labelContent);

        // only enable "show all" label if there are branches with more than just one build
        if (maxBuildsPerBranch > 1) {
            // add click handler to show more builds
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);

                    String labelText = showAllBuilds ? showAllBuildsText : showOneBuildText;

                    // toggle boolean
                    showAllBuilds = !showAllBuilds;

                    // update ui
                    showBuilds();
                    labelShowAllBuilds.setText(
                            "<html><u style='color: " + showAllBuildsEnabled + ";'>" + labelText + "</u></html>"
                    );
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    super.mouseEntered(e);
                    labelShowAllBuilds.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    super.mouseExited(e);
                    labelShowAllBuilds.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            });
        }

        return label;
    }

    private JPanel initContentRowPanel(
            List<Branch> buildConfigurationBranches,
            String noBuildConfigurationsAvailableMessage,
            boolean showAllBuilds,
            String buildPanelActionButtonText) {
        JPanel panel = new JPanel();

        // set layout for panel
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // show message if no branches (= build configurations) available
        // most likely no build was run for this build configuration
        if (buildConfigurationBranches.size() == 0) {
            c.insets = new Insets(20, 20, 20, 20);
            panel.add(new JLabel(noBuildConfigurationsAvailableMessage), c);
            return panel;
        }

        // initialize branch panels
        this.branchPanels = initBranchPanels(buildConfigurationBranches, showAllBuilds, buildPanelActionButtonText);
        for (int i = 0; i < buildConfigurationBranches.size(); i++) {
            c.anchor = GridBagConstraints.LINE_START;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = i;
            c.weightx = 1.0;
            panel.add(this.branchPanels[i], c);
        }

        return panel;
    }

    private BranchPanel[] initBranchPanels(
            List<Branch> branches,
            boolean showAllBuilds,
            String buildPanelActionButtonText)
    {
        BranchPanel[] panels = new BranchPanel[branches.size()];

        for (int i = 0; i < panels.length; i++) {
            panels[i] = new BranchPanel(branches.get(i), showAllBuilds, buildPanelActionButtonText);
        }

        return panels;
    }

    private void showBuilds() {
        // return if no build was run for this build configuration
        if (this.branchPanels == null) {
            return;
        }

        // update branch panels (either display just the last build or all)
        for (BranchPanel branchPanel : this.branchPanels) {
            branchPanel.showBuilds(this.showAllBuilds);
        }
    }

    public void setBranchFontColor(Color color) {
        // return if no build was run for this build configuration
        if (this.branchPanels == null) {
            return;
        }

        for (BranchPanel branchPanel : this.branchPanels) {
            branchPanel.setBranchFontColor(color);
        }
    }

    public String getBuildConfigurationName() {
        return this.buildConfiguration.getName();
    }
}
