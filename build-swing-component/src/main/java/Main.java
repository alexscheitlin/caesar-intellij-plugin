import ch.scheitlin.alex.build.model.Branch;
import ch.scheitlin.alex.build.model.Build;
import ch.scheitlin.alex.build.model.BuildConfiguration;
import ch.scheitlin.alex.build.swing.BranchPanel;
import ch.scheitlin.alex.build.swing.BuildConfigurationPanel;
import ch.scheitlin.alex.build.swing.BuildPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<BuildConfiguration> configs = getDummyConfigs();

        // create configuration panels
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        for (int i = 0; i < configs.size(); i++) {
            c.gridx = 0;
            c.gridy = i;
            c.anchor = GridBagConstraints.NORTH;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
            c.insets = new Insets(0, 0, 40, 0);
            BuildConfigurationPanel configPanel = new BuildConfigurationPanel(configs.get(i), "Action");
            configPanel.setBranchFontColor(Color.lightGray);
            panel.add(configPanel, c);

            // add actions listeners to build panels
            for (BranchPanel branchPanel : configPanel.branchPanels) {
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
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.insets = new Insets(0,0,0,0);
        content.add(panel,c);

        // create and show frame
        JFrame frame = new JFrame();
        frame.setLayout(new GridBagLayout());
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.insets = new Insets(20,20,20,20);
        frame.add(content, c);
        frame.setSize(new Dimension(800,800));

        // set look and feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {}

        frame.setVisible(true);
    }


    private static List<BuildConfiguration> getDummyConfigs() {
        /////////////////////////////////////////////////////////////////////////////////////////
        List<Build> builds1 = new ArrayList<Build>();
        builds1.add(new Build("5", false, "Status"));
        builds1.add(new Build("4", true, "Status"));
        builds1.add(new Build("3", true, "Status"));
        builds1.add(new Build("2", false, "Status"));
        builds1.add(new Build("1", true, "Status"));
        Branch branch1 = new Branch("master", builds1);

        ArrayList<Build> builds2 = new ArrayList<Build>();
        builds2.add(new Build("5", false, "Status"));
        builds2.add(new Build("4", false, "Status"));
        builds2.add(new Build("3", false, "Status"));
        builds2.add(new Build("2", false, "Status"));
        builds2.add(new Build("1", false, "Status"));
        Branch branch2 = new Branch("test", builds2);

        ArrayList<Branch> branches1 = new ArrayList<Branch>();
        branches1.add(branch1);
        branches1.add(branch2);

        BuildConfiguration config1 = new BuildConfiguration("Config1", branches1);
        /////////////////////////////////////////////////////////////////////////////////////////
        ArrayList<Build> builds3 = new ArrayList<Build>();
        builds3.add(new Build("3", true, "Status"));
        builds3.add(new Build("2", false, "Status"));
        builds3.add(new Build("1", false, "Status"));
        Branch branch3 = new Branch("master", builds3);

        ArrayList<Build> builds4 = new ArrayList<Build>();
        builds4.add(new Build("3", false, "Status"));
        builds4.add(new Build("2", false, "Status"));
        builds4.add(new Build("1", false, "Status"));
        Branch branch4 = new Branch("test", builds4);

        ArrayList<Build> builds5 = new ArrayList<Build>();
        builds5.add(new Build("3", true, "Status"));
        builds5.add(new Build("2", true, "Status"));
        builds5.add(new Build("1", true, "Status"));
        Branch branch5 = new Branch("TRUE", builds5);

        ArrayList<Branch> branches2 = new ArrayList<Branch>();
        branches2.add(branch3);
        branches2.add(branch4);
        branches2.add(branch5);

        BuildConfiguration config2 = new BuildConfiguration("Config2", branches2);
        /////////////////////////////////////////////////////////////////////////////////////////

        ArrayList<BuildConfiguration> configs = new ArrayList<BuildConfiguration>();
        configs.add(config1);
        configs.add(config2);

        return configs;
    }
}
