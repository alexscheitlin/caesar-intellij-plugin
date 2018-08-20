import ch.scheitlin.alex.build.model.BuildServerBranch;
import ch.scheitlin.alex.build.model.BuildServerBuild;
import ch.scheitlin.alex.build.model.BuildServerBuildConfiguration;
import ch.scheitlin.alex.build.model.BuildServerProject;
import ch.scheitlin.alex.build.swing.BranchPanel;
import ch.scheitlin.alex.build.swing.BuildConfigurationPanel;
import ch.scheitlin.alex.build.swing.BuildPanel;
import ch.scheitlin.alex.build.swing.ProjectPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        BuildServerProject project = getDummyData();
        List<BuildServerBuildConfiguration> configs = project.getBuildConfigurations();
        ProjectPanel projectPanel = new ProjectPanel(project, "Action");

        // add actions listeners to build panels
        for (BuildConfigurationPanel buildConfigurationPanel : projectPanel.buildConfigurationPanels) {
            for (BranchPanel branchPanel : buildConfigurationPanel.branchPanels) {
                for (BuildPanel buildPanel : branchPanel.buildPanels) {
                    final BuildPanel that = buildPanel;

                    ActionListener actionListener = new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            System.out.println(that.build.getRepository() + " - " + that.build.getBranch() + " - " + that.build.getCommit());
                        }
                    };

                    that.addActionListener(actionListener);
                }
            }
        }

        // create content panel
        JPanel content = new JPanel();
        content.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 0, 0);
        c.weightx = 1.0;
        c.weighty = 1.0;
        content.add(projectPanel, c);

        // create and show frame
        JFrame frame = new JFrame();
        frame.setLayout(new GridBagLayout());
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(20, 20, 20, 20);
        c.weightx = 1.0;
        c.weighty = 1.0;
        frame.add(content, c);
        frame.setSize(new Dimension(800, 800));

        // set look and feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
        }

        frame.setVisible(true);
    }


    private static BuildServerProject getDummyData() {
        // build configuration 1
        List<BuildServerBuild> builds1 = new ArrayList<BuildServerBuild>();
        builds1.add(new BuildServerBuild("5", false, "Status"));
        builds1.add(new BuildServerBuild("4", true, "Status"));
        builds1.add(new BuildServerBuild("3", true, "Status"));
        builds1.add(new BuildServerBuild("2", false, "Status"));
        builds1.add(new BuildServerBuild("1", true, "Status"));
        BuildServerBranch branch1 = new BuildServerBranch("master", builds1);

        List<BuildServerBuild> builds2 = new ArrayList<BuildServerBuild>();
        builds2.add(new BuildServerBuild("5", false, "Status"));
        builds2.add(new BuildServerBuild("4", false, "Status"));
        builds2.add(new BuildServerBuild("3", false, "Status"));
        builds2.add(new BuildServerBuild("2", false, "Status"));
        builds2.add(new BuildServerBuild("1", false, "Status"));
        BuildServerBranch branch2 = new BuildServerBranch("test", builds2);

        List<BuildServerBranch> branches1 = new ArrayList<BuildServerBranch>();
        branches1.add(branch1);
        branches1.add(branch2);

        BuildServerBuildConfiguration config1 = new BuildServerBuildConfiguration("Config1", branches1);
        // build configuration 1

        // build configuration 2
        List<BuildServerBuild> builds3 = new ArrayList<BuildServerBuild>();
        builds3.add(new BuildServerBuild("3", true, "Status"));
        builds3.add(new BuildServerBuild("2", false, "Status"));
        builds3.add(new BuildServerBuild("1", false, "Status"));
        BuildServerBranch branch3 = new BuildServerBranch("master", builds3);

        List<BuildServerBuild> builds4 = new ArrayList<BuildServerBuild>();
        builds4.add(new BuildServerBuild("3", false, "Status"));
        builds4.add(new BuildServerBuild("2", false, "Status"));
        builds4.add(new BuildServerBuild("1", false, "Status"));
        BuildServerBranch branch4 = new BuildServerBranch("test", builds4);

        List<BuildServerBuild> builds5 = new ArrayList<BuildServerBuild>();
        builds5.add(new BuildServerBuild("3", true, "Status"));
        builds5.add(new BuildServerBuild("2", true, "Status"));
        builds5.add(new BuildServerBuild("1", true, "Status"));
        BuildServerBranch branch5 = new BuildServerBranch("TRUE", builds5);

        List<BuildServerBranch> branches2 = new ArrayList<BuildServerBranch>();
        branches2.add(branch3);
        branches2.add(branch4);
        branches2.add(branch5);

        BuildServerBuildConfiguration config2 = new BuildServerBuildConfiguration("Config2", branches2);
        // build configuration 2

        List<BuildServerBuildConfiguration> configs = new ArrayList<BuildServerBuildConfiguration>();
        configs.add(config1);
        configs.add(config2);

        BuildServerProject project1 = new BuildServerProject("Project 1", configs);


        return project1;
    }
}
