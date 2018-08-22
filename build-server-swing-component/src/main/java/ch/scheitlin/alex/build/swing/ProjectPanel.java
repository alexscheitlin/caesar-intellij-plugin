package ch.scheitlin.alex.build.swing;

import ch.scheitlin.alex.build.model.BuildServerBuildConfiguration;
import ch.scheitlin.alex.build.model.BuildServerProject;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JLabel;

public class ProjectPanel extends JPanel {
    // data
    private BuildServerProject project;

    // components
    public BuildConfigurationPanel[] buildConfigurationPanels;

    // appearance settings
    private Color branchPanel_branchFontColor = Color.lightGray;

    // appearance constants
    private final String NO_BUILD_CONFIGURATIONS_AVAILABLE_MESSAGE = "No builds found!";
    private final int BUILD_CONFIGURATION_PANEL_SPACE = 20;

    public ProjectPanel(BuildServerProject project, String buildPanelActionButtonText) {
        // set data variables
        this.project = project;

        // get project information
        List<BuildServerBuildConfiguration> buildConfigurations = this.project.getBuildConfigurations();

        // set layout for the ProjectPanel
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // show message if no build configurations available
        // most likely no build configuration is configured on the build server for this project
        if (buildConfigurations.size() == 0) {
            c.insets = new Insets(20, 20, 20, 20);
            this.add(new JLabel(this.NO_BUILD_CONFIGURATIONS_AVAILABLE_MESSAGE), c);
            return;
        }

        // initialize build configuration panels
        this.buildConfigurationPanels = initBuildConfigurationPanels(
                buildConfigurations,
                this.branchPanel_branchFontColor,
                buildPanelActionButtonText
        );
        for (int i = 0; i < buildConfigurations.size(); i++) {
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = i;
            c.weightx = 1.0;

            if (i == buildConfigurations.size() - 1) {
                c.insets = new Insets(0, 0, 0, 0);
            } else {
                c.insets = new Insets(0, 0, BUILD_CONFIGURATION_PANEL_SPACE, 0);
            }
            this.add(this.buildConfigurationPanels[i], c);
        }

        // add empty panel to move all build configurations to the top when resizing
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = buildConfigurations.size();
        c.weightx = 1.0;
        c.weighty = 1.0;
        this.add(new JPanel(), c);
    }

    private BuildConfigurationPanel[] initBuildConfigurationPanels(
            List<BuildServerBuildConfiguration> buildConfigurations,
            Color branchPanelBranchFontColor,
            String buildPanelActionButtonText)
    {
        BuildConfigurationPanel[] panels = new BuildConfigurationPanel[buildConfigurations.size()];

        for (int i = 0; i < panels.length; i++) {
            panels[i] = new BuildConfigurationPanel(buildConfigurations.get(i), buildPanelActionButtonText);
            panels[i].setBranchFontColor(branchPanelBranchFontColor);
        }

        return panels;
    }

    public void setBranchPanelBranchFontColor(Color color) {
        this.branchPanel_branchFontColor = color;

        for (int i = 0; i < this.buildConfigurationPanels.length; i++) {
            this.buildConfigurationPanels[i].setBranchFontColor(branchPanel_branchFontColor);
        }
    }
}
