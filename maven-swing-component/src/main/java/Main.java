import ch.scheitlin.alex.maven.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    static boolean darkTheme = true;

    public static void main(String[] args) {
        MavenBuild mavenBuild = getDummyData();

        // create new maven swing component
        MavenPanel mavenPanel = new MavenPanel(mavenBuild, darkTheme);
        mavenPanel.setFont(new Font("Courier", Font.BOLD, 20));
        mavenPanel.setBackground(Color.darkGray);

        // create frame and add error components
        JFrame frame = new JFrame();
        frame.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        frame.add(mavenPanel, c);
        frame.setSize(new Dimension(800, 800));
        
        if (darkTheme) {
            Color dark = Color.darkGray;
            frame.setBackground(dark);
        }

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
        MavenModuleBuildStatus[] moduleStatus = {
                MavenModuleBuildStatus.SUCCESS,
                MavenModuleBuildStatus.SUCCESS,
                MavenModuleBuildStatus.SUCCESS,
                MavenModuleBuildStatus.SUCCESS,
                MavenModuleBuildStatus.FAILURE,
                MavenModuleBuildStatus.SKIPPED,
                MavenModuleBuildStatus.SKIPPED,
        };

        int[] goalsPerModule = {
                6, 3, 3, 7, 12, 0, 0,
        };

        return getDummyMavenBuild(MavenBuildStatus.FAILURE, moduleStatus, goalsPerModule);
    }

    private static MavenBuild getDummyMavenBuild(MavenBuildStatus buildStatus, MavenModuleBuildStatus[] moduleStatus, int[] goalsPerModule) {
        MavenBuild mavenBuild = new MavenBuild(buildStatus, null, null, null);

        List<MavenModule> mavenModules = new ArrayList<>();

        for (int i = 0; i < moduleStatus.length; i++) {
            mavenModules.add(getDummyMavenModule(i + 1, moduleStatus[i], goalsPerModule[i]));
        }

        mavenBuild.setModules(mavenModules);

        return mavenBuild;
    }

    private static MavenModule getDummyMavenModule(int moduleNumber, MavenModuleBuildStatus moduleStatus, int numberOfGoals) {
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
