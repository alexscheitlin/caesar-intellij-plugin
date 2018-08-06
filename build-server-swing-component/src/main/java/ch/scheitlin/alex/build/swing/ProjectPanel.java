package ch.scheitlin.alex.build.swing;

import ch.scheitlin.alex.build.model.BuildConfiguration;
import ch.scheitlin.alex.build.model.Project;

import javax.swing.JPanel;
import java.awt.*;
import java.util.List;

public class ProjectPanel extends JPanel {
    public BuildConfigurationPanel[] buildConfigurationPanels;

    private final int BUILD_CONFIGURATION_PANEL_SPACE = 20;
    private Color branchPanel_branchFontColor = Color.lightGray;

    public ProjectPanel(Project project, String buildPanelActionButtonText) {
        // get project information
        List<BuildConfiguration> buildConfigurations = project.getBuildConfigurations();

        // set layout for build configuration panel
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // configure and add build configuration panels
        initBuildConfigurationPanels(buildConfigurations, buildPanelActionButtonText);
        for (int i = 0; i < buildConfigurations.size(); i++) {
            c.gridx = 0;
            c.gridy = i;
            c.anchor = GridBagConstraints.NORTH;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
            if (i == buildConfigurations.size() -1) {
                c.insets = new Insets(0,0,0,0);
            } else {
                c.insets = new Insets(0,0,BUILD_CONFIGURATION_PANEL_SPACE,0);
            }
            this.add(this.buildConfigurationPanels[i], c);
        }
    }

    public void setBranchPanelBranchFontColor(Color color) {
        this.branchPanel_branchFontColor = color;
        for (int i = 0; i <this.buildConfigurationPanels.length; i++) {
            this.buildConfigurationPanels[i].setBranchFontColor(branchPanel_branchFontColor);
        }
    }

    private void initBuildConfigurationPanels(List<BuildConfiguration> buildConfigurations, String buildPanelActionButtonText) {
        this.buildConfigurationPanels = new BuildConfigurationPanel[buildConfigurations.size()];

        for (int i = 0; i <this.buildConfigurationPanels.length; i++) {
            this.buildConfigurationPanels[i] = new BuildConfigurationPanel(buildConfigurations.get(i), buildPanelActionButtonText);
            this.buildConfigurationPanels[i].setBranchFontColor(branchPanel_branchFontColor);
        }
    }
}
