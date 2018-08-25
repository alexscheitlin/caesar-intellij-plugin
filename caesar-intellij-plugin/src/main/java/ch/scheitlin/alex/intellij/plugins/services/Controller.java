package ch.scheitlin.alex.intellij.plugins.services;

import ch.scheitlin.alex.build.Caesar;
import ch.scheitlin.alex.build.model.BuildServer;
import ch.scheitlin.alex.build.model.BuildServerBuild;
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
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import javafx.util.Pair;

import java.io.File;
import java.util.List;

public class Controller {
    private Storage storage;
    private Caesar caesar;
    private Project project;
    private ToolWindow toolWindow;
    private String buildServerProjectName;
    private String buildServerConfigurationName;

    private final BuildServerType BUILD_SERVER_TYPE = BuildServerType.TEAM_CITY;

    public Controller() {
        this.storage = ServiceManager.getService(Storage.class);
        this.caesar = new Caesar(this.BUILD_SERVER_TYPE);
    }

    public static Controller getInstance() {
        return ServiceManager.getService(Controller.class);
    }

    public boolean tryAutoLogin(Project project) {
        return login(project, false);
    }

    public boolean login(Project project) {
        return login(project, true);
    }

    private boolean login(Project project, boolean openDialog) {
        this.project = project;

        List<Pair<String, String>> properties = this.storage.getBuildServerCredentials();
        String host = properties.get(0).getValue();
        String username = properties.get(1).getValue();
        String password = properties.get(2).getValue();

        if (openDialog) {
            // show login dialog to get build server host, username, and password
            LoginDialog dialog = new LoginDialog();

            // set build server host, username, and password if data was entered in login dialog
            String[] result = dialog.showDialog(host, username, password);

            // if result is null the cancel button was clicked
            if (result == null) {
                return false;
            }

            // use the just entered credentials
            host = result[0];
            username = result[1];
            password = result[2];
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

    public void logout() {
        this.caesar.disconnect();

        // update tool window
        this.getToolWindow().update();
    }

    public void setToolWindow(ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
    }

    public ToolWindow getToolWindow() {
        return this.toolWindow;
    }

    public void showToolWindow() {
        IntelliJHelper.showToolWindow(this.project, "BFR Assistant");
    }

    public BuildServer fetchBuildServerInformation() {
        return this.caesar.fetchBuildServerInformation();
    }

    public List<String> getBuildServerProjectNames() {
        return this.caesar.getBuildServerInformation().getProjectNames();
    }

    public ch.scheitlin.alex.build.model.BuildServerProject getBuildServerProject(String projectName) {
        return this.caesar.getBuildServerInformation().getProject(projectName);
    }

    public String getGitRepositoryOriginUrl() {
        return IntelliJHelper.getProjectPath(this.project);
    }

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

    public void saveBuildServerCredentials(String host, String username, String password) {
        this.storage.saveBuildServerCredentials(host, username, password);
    }

    public void deleteBuildServerCredentials() {
        this.storage.deleteBuildServerCredentials();
    }

    public boolean testBuildServerConnection(String host, String username, String password) {
        return this.caesar.testBuildServerConnection(this.BUILD_SERVER_TYPE, host, username, password);
    }

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

    public void startFixingBrokenBuild() {
        this.caesar.fix(this.getGitRepositoryOriginUrl());
        reloadProjectFiles();
        updateToolWindow();
    }

    public void stopFixingBrokenBuild() {
        this.caesar.finish();
        reloadProjectFiles();
        updateToolWindow();
    }

    public void abortBuildFix() {
        this.caesar.abort();

        updateToolWindow();
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

    public Project getIntelliJProject() {
        return this.project;
    }

    private void updateToolWindow() {
        this.toolWindow.update();
    }

    private void reloadProjectFiles() {
        IntelliJHelper.getProjectDirectoryFile(this.project).refresh(true, true);
    }

    public MavenBuild getMavenBuild() {
        return this.caesar.getMavenBuild();
    }

    public String getBuildServerBuildLog() {
        return this.caesar.getBuildServerBuildLog();
    }

    public void openErrorInFile(Error error) {
        String filePath = IntelliJHelper.getProjectPath(this.project) + "/" + error.getFullPath();

        int lineNumber = error.getLine() - 1;
        int columnNumber = error.getColumn() - 1;

        // open file
        openFile(filePath, lineNumber, columnNumber);
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

    private void openFile(String filePath, int lineNumber, int columnNumber) {
        File file = new File(filePath);
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file);
        OpenFileDescriptor openFileDescriptor = new OpenFileDescriptor(this.project, virtualFile, lineNumber, columnNumber);
        openFileDescriptor.navigate(true);

        System.out.println("Opening " + IntelliJHelper.getProjectPath(this.project) + "/" + filePath);
    }
}
