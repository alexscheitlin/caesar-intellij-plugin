import ch.scheitlin.alex.maven.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        MavenBuild mavenBuild = getDummyData();

        // create new maven swing component
        MavenPanel mavenPanel = new MavenPanel(mavenBuild);
        mavenPanel.setFont(new Font("Courier", Font.BOLD, 20));

        // create frame and add error components
        JFrame frame = new JFrame();
        frame.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        frame.add(new JScrollPane(mavenPanel), c);
        frame.setSize(new Dimension(800, 800));

        // set look and feel of frame
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
        }

        // show frame
        frame.setVisible(true);
    }

    private static MavenBuild getDummyData() {
        MavenModuleStatus[] moduleStatus = {
                MavenModuleStatus.SUCCESS,
                MavenModuleStatus.SUCCESS,
                MavenModuleStatus.SUCCESS,
                MavenModuleStatus.SUCCESS,
                MavenModuleStatus.FAILURE,
                MavenModuleStatus.SKIPPED,
                MavenModuleStatus.SKIPPED,
        };

        int[] goalsPerModule = {
                6, 3, 3, 7, 12, 0, 0,
        };

        return getDummyMavenBuild(MavenBuildStatus.FAILURE, moduleStatus, goalsPerModule);
    }

    private static MavenBuild getDummyMavenBuild(MavenBuildStatus buildStatus, MavenModuleStatus[] moduleStatus, int[] goalsPerModule) {
        MavenBuild mavenBuild = new MavenBuild(buildStatus, null);

        List<MavenModule> mavenModules = new ArrayList<>();

        for (int i = 0; i < moduleStatus.length; i++) {
            mavenModules.add(getDummyMavenModule(i + 1, moduleStatus[i], goalsPerModule[i]));
        }

        mavenBuild.setModules(mavenModules);

        return mavenBuild;
    }

    private static MavenModule getDummyMavenModule(int moduleNumber, MavenModuleStatus moduleStatus, int numberOfGoals) {
        MavenModule mavenModule = new MavenModule("Module " + moduleNumber);
        mavenModule.setStatus(moduleStatus);

        for (int i = 0; i < numberOfGoals; i++) {
            mavenModule.addGoal(getDummyMavenGoal(i + 1, 5 + i * 2));
        }

        return mavenModule;
    }

    private static MavenGoal getDummyMavenGoal(int goalNumber, int numberOfLogLines) {
        MavenGoal mavenGoal = new MavenGoal();
        mavenGoal.setName("Goal " + goalNumber);
        for (int i = 0; i < numberOfLogLines; i++) {
            mavenGoal.addLine(i + 1 + ": Goal " + goalNumber);
        }

        return mavenGoal;
    }
}
