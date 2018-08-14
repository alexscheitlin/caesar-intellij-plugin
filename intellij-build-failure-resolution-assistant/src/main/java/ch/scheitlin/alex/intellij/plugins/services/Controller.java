package ch.scheitlin.alex.intellij.plugins.services;

import ch.scheitlin.alex.build.Assistant;
import ch.scheitlin.alex.build.model.Build;
import ch.scheitlin.alex.build.model.Error;
import ch.scheitlin.alex.build.model.BuildConfiguration;
import ch.scheitlin.alex.intellij.plugins.dialogs.LoginDialog;
import ch.scheitlin.alex.intellij.plugins.toolWindow.ToolWindow;
import ch.scheitlin.alex.maven.MavenBuild;
import ch.scheitlin.alex.maven.MavenGoal;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import javafx.util.Pair;

import java.util.List;

public class Controller {
    private Storage storage;
    private Assistant assistant;

    public Controller() {
        this.storage = ServiceManager.getService(Storage.class);
        this.assistant = new Assistant();
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
        this.storage.project = project;

        List<Pair<String, String>> properties = this.storage.getTeamCityCredentials();
        String host = properties.get(0).getValue();
        String username = properties.get(1).getValue();
        String password = properties.get(2).getValue();

        if (openDialog) {
            // show login dialog to get TeamCity host, username, and password
            LoginDialog dialog = new LoginDialog();

            // set TeamCity host, username, and password if data was entered in login dialog
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
        if (this.assistant.connect(host, username, password)) {
            // update tool window
            if (this.getToolWindow() != null) {
                this.getToolWindow().update();
            }

            return true;
        }

        return false;
    }

    public void logout() {
        this.assistant.disconnect();

        // update tool window
        this.getToolWindow().update();
    }

    public void setToolWindow(ToolWindow toolWindow) {
        this.storage.toolWindow = toolWindow;
    }

    public ToolWindow getToolWindow() {
        return this.storage.toolWindow;
    }

    public void showToolWindow() {
        ToolWindowManager manager = ToolWindowManager.getInstance(this.storage.project);
        com.intellij.openapi.wm.ToolWindow toolWindow = manager.getToolWindow("BFR Assistant");
        toolWindow.show(null);
    }

    public List<String> getTeamCityProjectNames() {
        return this.assistant.getBuildServerInformation().getProjectNames();
    }

    public ch.scheitlin.alex.build.model.Project getTeamCityProject(String projectName) {
        return this.assistant.getBuildServerInformation().getProject(projectName);
    }

    public String getGitRepositoryOriginUrl() {
        return this.storage.project.getBaseDir().getPath();
    }

    public boolean isInNoStage() {
        return this.assistant.isInNoStage();
    }

    public boolean isConnected() {
        return this.assistant.isConnected();
    }

    public boolean hasDownloaded() {
        return this.assistant.hasDownloaded();
    }

    public boolean hasProcessed() {
        return this.assistant.hasProcessed();
    }

    public boolean isFixing() {
        return this.assistant.isFixing();
    }

    public void saveTeamCityCredentials(String host, String username, String password) {
        this.storage.saveTeamCityCredentials(host, username, password);
    }

    public void deleteTeamCityCredentials() {
        this.storage.deleteTeamCityCredentials();
    }

    public boolean testTeamCityConnection(String host, String username, String password) {
        return this.assistant.testTeamCityConnection(host, username, password);
    }

    public void getBuildInformation(String buildConfigurationName, Build build) {
        this.storage.teamCityBuildConfigurationName = buildConfigurationName;

        if (!this.assistant.download(build)) {
            System.out.println("Could not download build log from build server!");
        }

        if (!this.assistant.process()) {
            System.out.println("Could not process build log!");
        }

        updateToolWindow();
    }

    public void startFixingBrokenBuild() {
        this.assistant.fix(this.getGitRepositoryOriginUrl());
        reloadProjectFiles();
        updateToolWindow();
    }

    public void stopFixingBrokenBuild() {
        this.assistant.finish();
        reloadProjectFiles();
        updateToolWindow();
    }

    public void abortBuildFix() {
        this.assistant.abort();

        updateToolWindow();
    }

    public String getTeamCityProjectName() {
        return this.storage.teamCityProjectName;
    }

    public void setTeamCityProjectName(String projectName) {
        this.storage.teamCityProjectName = projectName;
    }

    public String getTeamCityBuildConfigurationName() {
        return this.storage.teamCityBuildConfigurationName;
    }

    public String getBuildStatus() {
        return this.assistant.mavenBuild.getStatus().toString();
    }

    public String getFailedGoal() {
        if (this.assistant.mavenBuild.getFailedGoal() != null) {
            MavenGoal failedGoal = this.assistant.mavenBuild.getFailedGoal();
            return failedGoal.getPlugin() + ":" + failedGoal.getVersion() + ":" + failedGoal.getName();
        } else {
            return "No failed goal detected!";
        }
    }

    public String getFailureCategory() {
        if (this.assistant.failureCategory != null) {
            return this.assistant.failureCategory;
        } else {
            return "No category found!";
        }
    }

    public List<Error> getErrors() {
        return this.assistant.errors;
    }

    public Project getIntelliJProject() {
        return this.storage.project;
    }

    private void updateToolWindow() {
        this.storage.toolWindow.update();
    }

    private void reloadProjectFiles() {
        this.storage.project.getBaseDir().refresh(true, true);
    }

    public MavenBuild getMavenBuild() {
        return this.assistant.mavenBuild;
    }

    public String getRawTeamCityBuildLog() {
        return this.assistant.getRawTeamCityBuildLog();
    }
}
