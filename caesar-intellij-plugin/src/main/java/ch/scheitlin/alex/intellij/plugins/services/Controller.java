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
import ch.scheitlin.alex.intellij.plugins.toolWindow.CaesarToolWindow;
import ch.scheitlin.alex.maven.MavenBuild;
import ch.scheitlin.alex.maven.MavenGoal;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.ide.ui.EditorOptionsTopHitProvider;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import javafx.util.Pair;

import java.util.List;

public class Controller {
    private Storage storage;
    private Caesar caesar;

    private Project project;
    private CaesarToolWindow caesarToolWindow;

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

    // store project name of selected project on overview to go back to same project after coming back to overview
    private String selectedBuildServerProjectName;

    // store selected build and build configuration name on overview to show it on summary panel
    private String selectedBuildServerConfigurationName;
    private BuildServerBuild selectedBuild;

    // try to login without asking for credentials
    public boolean tryAutoConnect() {
        try {
            connect(false);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    // login with asking for credentials
    public void connect() {
        try {
            connect(true);
        } catch (Exception ex) {
            String title = "Connection Error";
            String content = ex.getMessage();
            pushNotification(title, content);
        }
    }

    // login with saved or provided credentials
    private void connect(boolean openDialog) throws Exception {
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
                throw new Exception("Credentials not found!");
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
        this.caesar.connect(host, username, password);

        updateCaesarToolWindow();
        showCaesarToolWindow();
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

    public String getSelectedBuildServerProjectName() {
        return this.selectedBuildServerProjectName;
    }

    public void setSelectedBuildServerProjectName(String projectName) {
        this.selectedBuildServerProjectName = projectName;
    }

    public String getSelectedBuildServerBuildConfigurationName() {
        return this.selectedBuildServerConfigurationName;
    }

    public BuildServerBuild getSelectedBuild() {
        return this.selectedBuild;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // CAESAR: download & process
    // -----------------------------------------------------------------------------------------------------------------

    public void downloadAndProcess(BuildServerBuild build, String buildConfigurationName) {
        this.selectedBuildServerConfigurationName = buildConfigurationName;
        this.selectedBuild = build;

        try {
            this.caesar.download(build);
        } catch (Exception ex) {
            String title = "Download Error";
            String content = ex.getMessage();
            pushNotification(title, content);

            return;
        }

        try {
            this.caesar.process();
        } catch (Exception ex) {
            String title = "Process Error";
            String content = ex.getMessage();
            pushNotification(title, content);

            try {
                this.caesar.abort();
            } catch (Exception e) {
                title = "Abort Error";
                content = e.getMessage();
                pushNotification(title, content);
            }
            return;
        }

        updateCaesarToolWindow();
    }

    public String getBuildServerBuildLog() {
        return this.caesar.getBuildServerBuildLog();
    }

    public String getMavenBuildLog() {
        return this.caesar.getMavenBuildLog();
    }

    public MavenBuild getMavenBuild() {
        return this.caesar.getMavenBuild();
    }

    public String getFailedGoalString() {
        MavenGoal failedGoal = this.caesar.getMavenBuild().getFailedGoal();
        if (failedGoal != null) {
            return failedGoal.getPlugin().getName() + ":" + failedGoal.getPlugin().getVersion() + ":" + failedGoal.getName();
        } else {
            return "No failed goal detected!";
        }
    }

    public String getFailureCategory() {
        String failureCategory = this.caesar.getFailureCategory();
        if (failureCategory != null) {
            return failureCategory;
        } else {
            return "No category found!";
        }
    }

    public List<Error> getErrors() {
        return this.caesar.getErrors();
    }

    // -----------------------------------------------------------------------------------------------------------------
    // CAESAR: fix
    // -----------------------------------------------------------------------------------------------------------------

    public void fix() {
        try {
            this.caesar.fix(IntelliJHelper.getProjectPath(this.project));
        } catch (Exception ex) {
            String title = "Fix Error";
            String content = ex.getMessage();
            pushNotification(title, content);

            return;
        }

        IntelliJHelper.reloadProjectFiles(this.project);
        updateCaesarToolWindow();
    }

    public void debugError(Error error) {
        String filePath = IntelliJHelper.getProjectPath(this.project) + "/" + error.getFullPath();

        int lineNumber = error.getLine() - 1;

        // create and toggle a line break point
        if (!DebugHelper.addLineBreakpoint(this.project, filePath, lineNumber)) {
            String title = "Debug Error";
            String content = "Could not add breakpoint!";
            pushNotification(title, content);
        }

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

        if (runConfiguration == null || !DebugHelper.startDebugger(this.project, runConfiguration)) {
            String title = "Debug Error";
            String content = "Could not start debugger!";
            pushNotification(title, content);
        }
    }

    public void openErrorInFile(Error error) {
        String filePath = IntelliJHelper.getProjectPath(this.project) + "/" + error.getFullPath();

        int lineNumber = error.getLine() - 1;
        int columnNumber = error.getColumn() - 1;

        // open file
        if (!IntelliJHelper.openFile(this.project, filePath, lineNumber, columnNumber)) {
            String title = "Editor Error";
            String content = "Could not open file!";
            pushNotification(title, content);
        }
    }

    public String getGitRepositoryUrl() {
        return this.caesar.getGitRepositoryUrl();
    }

    public String getStashedChanges() {
        return this.caesar.getStashedChanges();
    }

    public String getNewBranch() {
        return this.caesar.getNewBranch();
    }

    // -----------------------------------------------------------------------------------------------------------------
    // CAESAR: finish
    // -----------------------------------------------------------------------------------------------------------------

    public void finish() {
        try {
            this.caesar.finish();
        } catch (Exception ex) {
            String title = "Finish Error";
            String content = ex.getMessage();
            pushNotification(title, content);

            return;
        }

        IntelliJHelper.reloadProjectFiles(this.project);
        updateCaesarToolWindow();
    }

    // -----------------------------------------------------------------------------------------------------------------
    // CAESAR: disconnect
    // -----------------------------------------------------------------------------------------------------------------

    public void disconnect() {
        try {
            this.caesar.disconnect();
        } catch (Exception ex) {
            String title = "Connection Error";
            String content = ex.getMessage();
            pushNotification(title, content);

            return;
        }

        updateCaesarToolWindow();
        hideCaesarToolWindow();
    }

    // -----------------------------------------------------------------------------------------------------------------
    // CAESAR: abort
    // -----------------------------------------------------------------------------------------------------------------

    public void abort() {
        try {
            this.caesar.abort();
        } catch (Exception ex) {
            String title = "Abort Error";
            String content = ex.getMessage();
            pushNotification(title, content);

            return;
        }

        updateCaesarToolWindow();
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

    public boolean isLoggedIn() {
        return !this.caesar.isInNoStage();
    }

    // -----------------------------------------------------------------------------------------------------------------

    public void setProject(Project project) {
        this.project = project;
    }

    public CaesarToolWindow getCaesarToolWindow() {
        return this.caesarToolWindow;
    }

    public void setCaesarToolWindow(CaesarToolWindow caesarToolWindow) {
        this.caesarToolWindow = caesarToolWindow;
    }

    private void updateCaesarToolWindow() {
        if (this.getCaesarToolWindow() != null) {
            this.caesarToolWindow.update();
        }
    }

    public boolean showCaesarToolWindow() {
        return IntelliJHelper.showToolWindow(this.project, this.TOOL_WINDOW_ID);
    }

    public boolean hideCaesarToolWindow() {
        return IntelliJHelper.hideToolWindow(this.project, this.TOOL_WINDOW_ID);
    }

    public boolean hasDarkTheme() {
        return IntelliJHelper.hasDarkTheme();
    }

    public void pushNotification(String title, String content) {
        IntelliJHelper.pushNotification(this.TOOL_WINDOW_ID, title, content);
    }
}
