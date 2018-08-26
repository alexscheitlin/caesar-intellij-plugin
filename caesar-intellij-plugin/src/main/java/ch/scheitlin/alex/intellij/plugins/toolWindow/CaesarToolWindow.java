package ch.scheitlin.alex.intellij.plugins.toolWindow;

import ch.scheitlin.alex.build.model.Error;
import ch.scheitlin.alex.intellij.plugins.services.Controller;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import javax.swing.*;
import java.util.List;

public class CaesarToolWindow implements ToolWindowFactory {

    private ToolWindow overview;
    private LoginPanel panelLogin;
    private OverviewPanel panelOverview;
    private BuildSummaryPanel panelBuildSummary;
    private RawDataPanel panelRawData;
    private FixPanel panelFix;

    private Project project;
    private boolean startUp = true;

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        // save provided project and tool window
        this.project = project;
        this.overview = toolWindow;

        // save reference to storage
        Controller.getInstance().setCaesarToolWindow(this);

        update();
    }

    // display one of the panels in this package depending on the build fix helper stage
    public void update() {
        Controller controller = Controller.getInstance();

        if (controller.isInNoStage()) {
            // try to auto log in if the tool is at startup
            if (this.startUp && controller.tryAutoLogin(this.project)) {
                // only auto login at start up
                // later on one must be able to logout without automatically login again
                this.startUp = false;

                this.update();
                return;
            }

            // show login panel if user is not logged in
            this.panelLogin = new LoginPanel(this.project);

            setToolWindowContent(this.panelLogin);

        } else if (controller.isConnected()) {
            // show overview panel if user is connected with the build server
            this.panelOverview = new OverviewPanel();

            setToolWindowContent(this.panelOverview);

        } else if (controller.hasDownloaded() || controller.hasProcessed()) {
            String projectName = controller.getSelectedBuildServerProjectName();
            String buildConfigurationName = controller.getSelectedBuildServerBuildConfigurationName();
            String buildStatus = controller.getMavenBuild().getStatus().toString();
            String failedGoal = controller.getFailedGoal();
            String failureCategory = controller.getFailureCategory();
            List<Error> errors = controller.getErrors();
            Project project = controller.getIntelliJProject();

            // show build information if build log is downloaded (and processed)
            this.panelBuildSummary = new BuildSummaryPanel(
                    projectName,
                    buildConfigurationName,
                    buildStatus,
                    failedGoal,
                    failureCategory,
                    errors,
                    project
            );

            this.panelRawData = new RawDataPanel(controller.getBuildServerBuildLog(), controller.getMavenBuild());

            setToolWindowContent(this.panelBuildSummary, "Summary", this.panelRawData, "Raw Data");

        } else if (controller.isFixing()) {
            // show build fix panel if user is fixing a broken build
            this.panelFix = new FixPanel();

            setToolWindowContent(this.panelFix);
        }
    }

    // set one component as tool window content
    private void setToolWindowContent(JComponent component) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(component, "", false);
        this.overview.getContentManager().removeAllContents(true);
        this.overview.getContentManager().addContent(content);
    }

    // set two components as tool window content
    private void setToolWindowContent(JComponent component1, String title1, JComponent component2, String title2) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content1 = contentFactory.createContent(component1, title1, false);
        Content content2 = contentFactory.createContent(component2, title2, false);
        this.overview.getContentManager().removeAllContents(true);
        this.overview.getContentManager().addContent(content1);
        this.overview.getContentManager().addContent(content2);
    }
}
