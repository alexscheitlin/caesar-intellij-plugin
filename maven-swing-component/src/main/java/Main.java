import ch.scheitlin.alex.maven.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        MavenBuild mavenBuild = getDummyData();

        // create new maven swing component
        MavenPanel errorComponent = new MavenPanel(mavenBuild);
        errorComponent.setFont(new Font("Courier", Font.BOLD,20));

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
        MavenModule mavenModule1 = new MavenModule("Module 1");
        mavenModule1.setStatus(MavenModuleStatus.SUCCESS);
        mavenModule1.setVersion("v1");

        MavenGoal mavenGoal1 = new MavenGoal();
        mavenGoal1.setName("Goal 1");
        mavenGoal1.addLine("1: Goal 1");
        mavenGoal1.addLine("2: Goal 1");
        mavenModule1.addGoal(mavenGoal1);

        MavenGoal mavenGoal2 = new MavenGoal();
        mavenGoal2.setName("Goal 2");
        mavenGoal2.addLine("1: Goal 2");
        mavenGoal2.addLine("2: Goal 2");
        mavenModule1.addGoal(mavenGoal2);

        MavenModule mavenModule2 = new MavenModule("Module 2");
        mavenModule2.setStatus(MavenModuleStatus.FAILURE);
        mavenModule2.setVersion("v1");

        MavenGoal mavenGoal3 = new MavenGoal();
        mavenGoal3.setName("Goal 3");
        mavenGoal3.addLine("1: Goal 3");
        mavenGoal3.addLine("2: Goal 3");
        mavenGoal3.addLine("3: Goal 3");
        mavenModule2.addGoal(mavenGoal3);

        MavenModule mavenModule3 = new MavenModule("Module 3");
        mavenModule3.setStatus(MavenModuleStatus.SKIPPED);
        mavenModule3.setVersion("v1");

        List<MavenModule> modules = new ArrayList<MavenModule>();
        modules.add(mavenModule1);
        modules.add(mavenModule2);
        modules.add(mavenModule3);

        MavenBuild mavenBuild = new MavenBuild(MavenBuildStatus.FAILURE, null);
        mavenBuild.setModules(modules);

        return mavenBuild;
    }
}
