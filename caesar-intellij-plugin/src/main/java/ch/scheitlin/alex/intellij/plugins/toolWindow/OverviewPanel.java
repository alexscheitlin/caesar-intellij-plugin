package ch.scheitlin.alex.intellij.plugins.toolWindow;

import ch.scheitlin.alex.build.model.BuildServerProject;
import ch.scheitlin.alex.build.swing.ProjectPanel;
import ch.scheitlin.alex.intellij.plugins.services.Controller;
import ch.scheitlin.alex.build.swing.BranchPanel;
import ch.scheitlin.alex.build.swing.BuildConfigurationPanel;
import ch.scheitlin.alex.build.swing.BuildPanel;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class OverviewPanel extends JPanel {
    private JPanel panelContent;
    private JButton buttonRefresh;
    private ComboBox<String> comboBoxProject;
    private JBScrollPane scrollPane;
    private JPanel panelWrapper;
    private JPanel panelConfigurations;
    private ProjectPanel projectPanel;

    private List<String> projectNames;

    public OverviewPanel() {
        // set layout
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // configure panel to add padding to the content
        initAndAddContentPanel(20);

        // configure and add button to refresh the team city information with projects and builds
        initRefreshButton();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.gridwidth = 1;
        c.insets = JBUI.insets(0, 0, 10, 0);
        this.panelContent.add(this.buttonRefresh, c);

        // configure and add combo box with teamcity projects
        initProjectComboBox();
        c.gridx = 1;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.gridwidth = 1;
        c.insets = JBUI.insets(0, 0, 10, 0);
        this.panelContent.add(this.comboBoxProject, c);

        // configure and add panel with build configuration panels
        initPanelWithBuildConfigurationPanels(null);
        //initPanelWithBuildConfigurationPanels(getDummyConfigs());
        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridwidth = 2;
        c.insets = JBUI.insets(0, 0, 0, 0);
        this.panelContent.add(this.scrollPane, c);

        onRefresh();
    }

    private void initAndAddContentPanel(int padding) {
        this.panelContent = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.insets = JBUI.insets(padding);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        this.add(this.panelContent, c);
    }

    private void initRefreshButton() {
        this.buttonRefresh = new JButton();
        this.buttonRefresh.setText("Refresh");

        this.buttonRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRefresh();
            }
        });
    }

    private void initProjectComboBox() {
        this.comboBoxProject = new ComboBox<>();

        this.comboBoxProject.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onProjectChange();
            }
        });
    }

    private void initPanelWithBuildConfigurationPanels(BuildServerProject project) {
        try {
            // remove second row if it already exists
            this.panelContent.remove(this.scrollPane);
        } catch (Exception e) {

        }

        // create panel holding all build configuration panels
        this.panelConfigurations = new JPanel();
        this.panelConfigurations.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // create and place build configurations panel
        // but only if there is some data available, if not, show error message
        if (project != null) {

            // check whether there are build configurations configured in team city or not
            if (project.getBuildConfigurations().size() > 0) {
                this.projectPanel = new ProjectPanel(project, "View");
                this.projectPanel.setBranchPanelBranchFontColor(JBColor.blue);

                // add actions listeners to build panels
                for (BuildConfigurationPanel buildConfigurationPanel : projectPanel.buildConfigurationPanels) {
                    // only proceed if there is at least one build run with this build configuration
                    if (buildConfigurationPanel.branchPanels != null) {
                        for (BranchPanel branchPanel : buildConfigurationPanel.branchPanels) {
                            for (BuildPanel buildPanel : branchPanel.buildPanels) {
                                BuildPanel that = buildPanel;
                                String buildConfigurationName = buildConfigurationPanel.getBuildConfigurationName();

                                ActionListener actionListener = new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        if (!Controller.getInstance().getBuildInformation(buildConfigurationName, that.build)) {
                                            System.out.println("Could not get build server information!");
                                        }
                                    }
                                };

                                that.addActionListener(actionListener);

                                System.out.println("\t" + that.build.getId() + ": " + that.build.getRepository() + " | " + that.build.getBranch());
                            }
                        }
                    }
                }

                c.anchor = GridBagConstraints.NORTH;
                c.fill = GridBagConstraints.HORIZONTAL;
                c.weightx = 1.0;
                this.panelConfigurations.add(this.projectPanel, c);
            } else {
                c.insets = JBUI.insets(20);

                this.panelConfigurations.add(new JLabel("No build configurations found!"), c);
            }
        } else {
            this.panelConfigurations.add(new JLabel("No data found!"), c);
        }

        // add panel with build configuration panels to a wrapper panel to stick it to the top
        this.panelWrapper = new JPanel(new BorderLayout());
        this.panelWrapper.add(this.panelConfigurations, BorderLayout.PAGE_START);

        // add wrapper panel to scroll pane
        this.scrollPane = new JBScrollPane(this.panelWrapper);
        this.scrollPane.setBorder(null);
    }

    private void onRefresh() {
        // fetch data
        Controller.getInstance().fetchBuildServerInformation();

        // get all projects in TeamCity
        this.projectNames = Controller.getInstance().getBuildServerProjectNames();

        // set default message
        String defaultMessage = "-- Please select a project --";

        // get current selected item
        ComboBoxModel<String> currentModel = this.comboBoxProject.getModel();
        Object selectedItem = currentModel.getSelectedItem();
        String selectedProject = null;
        if (selectedItem != null) {
            selectedProject = selectedItem.toString();
        } else if (Controller.getInstance().getSelectedBuildServerProjectName() != null) {
            selectedProject = Controller.getInstance().getSelectedBuildServerProjectName();
        }

        int selectedIndex = -1;

        // get project names
        String[] projectNames;
        if (this.projectNames == null) {
            projectNames = new String[1];
            projectNames[0] = defaultMessage;
        } else {
            projectNames = new String[this.projectNames.size() + 1];
            projectNames[0] = defaultMessage;
            for (int i = 1; i < this.projectNames.size() + 1; i++) {
                projectNames[i] = this.projectNames.get(i - 1);

                if (projectNames[i].equals(selectedProject)) {
                    selectedIndex = i;
                }
            }
        }

        // show project names
        this.comboBoxProject.setModel(new DefaultComboBoxModel<>(projectNames));

        if (selectedIndex != -1) {
            this.comboBoxProject.setSelectedIndex(selectedIndex);
        }

        onProjectChange();

        // enable load button and project combo box
        // if the user is logged in
        Controller controller = ServiceManager.getService(Controller.class);
        this.buttonRefresh.setEnabled(controller.isConnected());
        this.comboBoxProject.setEnabled(controller.isConnected());
    }

    private void onProjectChange() {
        // get index of selected project
        int index = comboBoxProject.getSelectedIndex();

        // get build configurations of a project if a one is selected
        // (at index 0 there is no project but the message to select one)
        BuildServerProject project = null;
        if (index > 0) {
            // get name of selected project
            String projectName = this.projectNames.get(index - 1);
            Controller.getInstance().setSelectedBuildServerProjectName(projectName);

            // get the project
            project = Controller.getInstance().getBuildServerProject(projectName);
        }

        // show the builds
        initPanelWithBuildConfigurationPanels(project);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 3;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridwidth = 2;
        this.panelContent.add(this.scrollPane, c);

        // refresh overview panel
        this.panelContent.revalidate();
        this.panelContent.repaint();
    }
}
