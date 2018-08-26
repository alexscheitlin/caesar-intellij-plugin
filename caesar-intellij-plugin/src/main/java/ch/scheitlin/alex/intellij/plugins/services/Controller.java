package ch.scheitlin.alex.intellij.plugins.services;

import ch.scheitlin.alex.build.Caesar;
import ch.scheitlin.alex.build.model.BuildServer;
import ch.scheitlin.alex.build.model.BuildServerBuild;
import ch.scheitlin.alex.build.model.BuildServerProject;
import ch.scheitlin.alex.build.model.BuildServerType;
import ch.scheitlin.alex.build.model.Error;
import ch.scheitlin.alex.intellij.plugins.dialogs.LoginDialog;
import ch.scheitlin.alex.intellij.plugins.services.helpers.DebugHelper;
import ch.scheitlin.alex.intellij.plugins.services.helpers.IntelliJHelper;
import ch.scheitlin.alex.intellij.plugins.toolWindow.ToolWindow;
import ch.scheitlin.alex.maven.MavenBuild;
import ch.scheitlin.alex.maven.MavenGoal;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import javafx.util.Pair;

import java.util.List;

public class Controller {
    private Storage storage;
    private Caesar caesar;

    private Project project;
    private ToolWindow toolWindow;
    private String buildServerProjectName;
    private String buildServerConfigurationName;

    private final BuildServerType BUILD_SERVER_TYPE = BuildServerType.TEAM_CITY;
    private final String TOOL_WINDOW_ID = "CAESAR";

    public Controller() {
        this.storage = Storage.getInstance();
        this.caesar = new Caesar(this.BUILD_SERVER_TYPE);
    }

    public static Controller getInstance() {
        return ServiceManager.getService(Controller.class);
    }

    public boolean testBuildServerConnection(String host, String username, String password) {
        return this.caesar.testBuildServerConnection(this.BUILD_SERVER_TYPE, host, username, password);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // CAESAR: connect
    // -----------------------------------------------------------------------------------------------------------------

    // try to login without asking for credentials
    public boolean tryAutoLogin(Project project) {
        return login(project, false);
    }

    // login with asking for credentials
    public boolean login(Project project) {
        return login(project, true);
    }

    // login with saved or provided credentials
    private boolean login(Project project, boolean openDialog) {
        this.project = project;

        // get credentials from storage (if the are available)
        List<Pair<String, String>> properties = this.storage.getBuildServerCredentials();
        String host = properties.get(0).getValue();
        String username = properties.get(1).getValue();
        String password = properties.get(2).getValue();

        // ask for credentials
        if (openDialog) {
            // show login dialog to get build server host, username, and password
            LoginDialog dialog = new LoginDialog();

            // set build server host, username, and password if data was entered in login dialog
            Object[] result = dialog.showDialog(host, username, password);

            // if result is null the cancel button was clicked
            if (result == null) {
                return false;
            }

            // use the just entered credentials
            host = (String) result[0];
            username = (String) result[1];
            password = (String) result[2];

            // save credentials if remember me button was checked and delete them if not
            if ((boolean) result[3]) {
                this.storage.saveBuildServerCredentials(host, username, password);
            } else {
                this.storage.deleteBuildServerCredentials();
            }

        }

        // check whether the login was successful or not
        if (this.caesar.connect(host, username, password)) {
            // update tool window
            if (this.getToolWindow() != null) {
                this.getToolWindow().update();
            }

            return true;
        }

        return false;
    }

    public BuildServer fetchBuildServerInformation() {
        return this.caesar.fetchBuildServerInformation();
    }

    public List<String> getBuildServerProjectNames() {
        return this.caesar.getBuildServerInformation().getProjectNames();
    }

    public BuildServerProject getBuildServerProject(String projectName) {
        return this.caesar.getBuildServerInformation().getProject(projectName);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // CAESAR: download & process
    // -----------------------------------------------------------------------------------------------------------------

    public void getBuildInformation(String buildConfigurationName, BuildServerBuild build) {
        this.buildServerConfigurationName = buildConfigurationName;

        if (!this.caesar.download(build)) {
            System.out.println("Could not download build log from build server!");
        }

        if (!this.caesar.process()) {
            System.out.println("Could not process build log!");
        }

        updateToolWindow();
    }

    // -----------------------------------------------------------------------------------------------------------------
    // CAESAR: fix
    // -----------------------------------------------------------------------------------------------------------------

    public void startFixingBrokenBuild() {
        this.caesar.fix(IntelliJHelper.getProjectPath(this.project));
        IntelliJHelper.reloadProjectFiles(this.project);
        updateToolWindow();
    }

    public void debugError(Error error) {
        String filePath = IntelliJHelper.getProjectPath(this.project) + "/" + error.getFullPath();

        int lineNumber = error.getLine() - 1;

        // create and toggle a line break point
        DebugHelper.addLineBreakpoint(this.project, filePath, lineNumber);

        // select run configuration
        // the run configuration for this file needs to exist
        String configurationName = error.getFile().split("\\.")[0];     // remove file ending
        RunConfiguration runConfiguration = null;
        for (RunConfiguration config : DebugHelper.getRunConfigurations(this.project)) {
            if (config.getName().equals(configurationName)) {
                runConfiguration = config;
                break;
            }
        }

        try {
            DebugHelper.startDebugger(this.project, runConfiguration);
        } catch (ExecutionException ex) {
            System.out.print(ex.getMessage());
        }
    }

    public void openErrorInFile(Error error) {
        String filePath = IntelliJHelper.getProjectPath(this.project) + "/" + error.getFullPath();

        int lineNumber = error.getLine() - 1;
        int columnNumber = error.getColumn() - 1;

        // open file
        IntelliJHelper.openFile(this.project, filePath, lineNumber, columnNumber);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // CAESAR: finish
    // -----------------------------------------------------------------------------------------------------------------

    public void stopFixingBrokenBuild() {
        this.caesar.finish();
        IntelliJHelper.reloadProjectFiles(this.project);
        updateToolWindow();
    }

    // -----------------------------------------------------------------------------------------------------------------
    // CAESAR: disconnect
    // -----------------------------------------------------------------------------------------------------------------

    public boolean logout() {
        if (!this.caesar.disconnect()) {
            return false;
        }

        // update tool window
        this.getToolWindow().update();

        return true;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // CAESAR: abort
    // -----------------------------------------------------------------------------------------------------------------

    public void abortBuildFix() {
        this.caesar.abort();

        updateToolWindow();
    }

    // -----------------------------------------------------------------------------------------------------------------
    // CAESAR: stages
    // -----------------------------------------------------------------------------------------------------------------

    public boolean isInNoStage() {
        return this.caesar.isInNoStage();
    }

    public boolean isConnected() {
        return this.caesar.isConnected();
    }

    public boolean hasDownloaded() {
        return this.caesar.hasDownloaded();
    }

    public boolean hasProcessed() {
        return this.caesar.hasProcessed();
    }

    public boolean isFixing() {
        return this.caesar.isFixing();
    }

    // -----------------------------------------------------------------------------------------------------------------

    public Project getIntelliJProject() {
        return this.project;
    }

    public ToolWindow getToolWindow() {
        return this.toolWindow;
    }

    public void setToolWindow(ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
    }

    private void updateToolWindow() {
        this.toolWindow.update();
    }

    public void showToolWindow() {
        IntelliJHelper.showToolWindow(this.project, this.TOOL_WINDOW_ID);
    }

    public void hideToolWindow() {
        IntelliJHelper.hideToolWindow(this.project, this.TOOL_WINDOW_ID);
    }

    public String getBuildServerProjectName() {
        return this.buildServerProjectName;
    }

    public void setBuildServerProjectName(String projectName) {
        this.buildServerProjectName = projectName;
    }

    public String getBuildServerBuildConfigurationName() {
        return this.buildServerConfigurationName;
    }

    public String getBuildServerBuildLog() {
        return this.caesar.getBuildServerBuildLog();
    }

    public MavenBuild getMavenBuild() {
        return this.caesar.getMavenBuild();
    }

    public String getBuildStatus() {
        return this.caesar.getMavenBuild().getStatus().toString();
    }

    public String getFailedGoal() {
        if (this.caesar.getMavenBuild().getFailedGoal() != null) {
            MavenGoal failedGoal = this.caesar.getMavenBuild().getFailedGoal();
            return failedGoal.getPlugin().getName() + ":" + failedGoal.getPlugin().getVersion() + ":" + failedGoal.getName();
        } else {
            return "No failed goal detected!";
        }
    }

    public String getFailureCategory() {
        if (this.caesar.getFailureCategory() != null) {
            return this.caesar.getFailureCategory();
        } else {
            return "No category found!";
        }
    }

    public List<Error> getErrors() {
        return this.caesar.getErrors();
    }
}
