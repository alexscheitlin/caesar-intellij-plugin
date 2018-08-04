import ch.scheitlin.alex.maven.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        MavenBuild mavenBuild = getDummyData();

        // create new maven swing component
        MavenPanel errorComponent = new MavenPanel(mavenBuild);

        // create frame and add error components
        JFrame frame = new JFrame();
        frame.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        frame.add(errorComponent, c);
        frame.setSize(new Dimension(800,800));

        // set look and feel of frame
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {}

        // show frame
        frame.setVisible(true);
    }

    private static MavenBuild getDummyData() {
        MavenBuild mavenBuild = new MavenBuild();
        mavenBuild.status = MavenBuildStatus.FAILURE;
        mavenBuild.modules = new ArrayList<MavenModule>();

        MavenModule mavenModule1 = new MavenModule();
        mavenModule1.status = MavenModuleStatus.SUCCESS;
        mavenModule1.name = "Module 1";
        mavenModule1.version = "v1";
        mavenModule1.goals = new ArrayList<MavenGoal>();
        mavenBuild.modules.add(mavenModule1);

        MavenGoal mavenGoal1 = new MavenGoal();
        mavenGoal1.name = "Goal 1";
        mavenModule1.goals.add(mavenGoal1);

        MavenGoal mavenGoal2 = new MavenGoal();
        mavenGoal2.name = "Goal 2";
        mavenModule1.goals.add(mavenGoal2);

        MavenModule mavenModule2 = new MavenModule();
        mavenModule2.status = MavenModuleStatus.FAILURE;
        mavenModule2.name = "Module 2";
        mavenModule2.version = "v1";
        mavenModule2.goals = new ArrayList<MavenGoal>();
        mavenBuild.modules.add(mavenModule2);

        MavenGoal mavenGoal3 = new MavenGoal();
        mavenGoal3.name = "Goal 3";
        mavenModule2.goals.add(mavenGoal3);

        return mavenBuild;
    }
}
