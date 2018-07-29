package ch.scheitlin.alex.intellij.plugins.services;

import ch.scheitlin.alex.build.Helper;
import ch.scheitlin.alex.build.model.Build;
import ch.scheitlin.alex.build.model.Error;
import ch.scheitlin.alex.build.model.BuildConfiguration;
import ch.scheitlin.alex.intellij.plugins.dialogs.LoginDialog;
import ch.scheitlin.alex.intellij.plugins.toolWindow.ToolWindow;
import ch.scheitlin.alex.maven.model.MavenBuild;
import ch.scheitlin.alex.maven.model.MavenGoal;
import ch.scheitlin.alex.maven.model.MavenModule;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import javafx.util.Pair;

import java.util.List;

public class Controller {
    private Storage storage;
    private Helper helper;

    public Controller() {
        this.storage = ServiceManager.getService(Storage.class);
        this.helper = new Helper();
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
            String[] result = dialog.showDialog(host, username, password);
            host = result[0];
            username = result[1];
            password = result[2];
        }

        // check whether the login was successful or not
        if (this.helper.connect(host, username, password)) {
            // update tool window
            if (this.getToolWindow() != null) {
                this.getToolWindow().update();
            }

            return true;
        }

        return false;
    }

    public void logout() {
        this.helper.disconnect();

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
        ToolWindowManager.getInstance(this.storage.project).getToolWindow("Build Fixer").show(null);
    }

    public List<String> getTeamCityProjectNames() {
        return this.helper.getTeamCityProjectNames();
    }

    public List<BuildConfiguration> getBuildConfigurationsToShow(String projectName) {
        return this.helper.getBuildConfigurationsToShow(projectName);
    }

    public String getGitRepositoryOriginUrl() {
        return this.storage.project.getBaseDir().getPath();
    }

    public boolean isInNoStage() {
        return this.helper.isInNoStage();
    }

    public boolean isConnected() {
        return this.helper.isConnected();
    }

    public boolean hasDownloaded() {
        return this.helper.hasDownloaded();
    }

    public boolean hasProcessed() {
        return this.helper.hasProcessed();
    }

    public boolean isFixing() {
        return this.helper.isFixing();
    }

    public void saveTeamCityCredentials(String host, String username, String password) {
        this.storage.saveTeamCityCredentials(host, username, password);
    }

    public void deleteTeamCityCredentials() {
        this.storage.deleteTeamCityCredentials();
}

    public boolean testTeamCityConnection(String host, String username, String password) {
        return this.helper.testTeamCityConnection(host, username, password);
    }

    public void getBuildInformation(String buildConfigurationName, Build build) {
        this.storage.teamCityBuildConfigurationName = buildConfigurationName;

        if (!this.helper.download(build)) {
            System.out.println("Could not download build log from build server!");
        }

        if (!this.helper.process()) {
            System.out.println("Could not process build log!");
        }

        updateToolWindow();
    }

    public void startFixingBrokenBuild() {
        this.helper.fix(this.getGitRepositoryOriginUrl());
        reloadProjectFiles();
        updateToolWindow();
    }

    public void stopFixingBrokenBuild() {
        this.helper.finish();
        reloadProjectFiles();
        updateToolWindow();
    }

    public void abortBuildFix() {
        this.helper.abort();

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
        return this.helper.mavenBuild.status.toString();
    }

    public String getFailedGoal() {
        if (this.helper.mavenBuild.failedGoal != null) {
            MavenGoal failedGoal = this.helper.mavenBuild.failedGoal;
            return failedGoal.plugin + ":" + failedGoal.version + ":" + failedGoal.name;
        } else {
            return "No failed goal detected!";
        }
    }

    public String getFailureCategory() {
        if (this.helper.failureCategory != null) {
            return this.helper.failureCategory;
        } else {
            return "No category found!";
        }
    }

    public List<Error> getErrors() {
        return this.helper.errors;
    }

    public Project getIntelliJProject() {
        return this.storage.project;
    }

    private void updateToolWindow() {
        this.storage.toolWindow.update();
    }

    private void reloadProjectFiles() {
        this.storage.project.getBaseDir().refresh(true,true);
    }

    public void setSelectedTeamCityProject(Object project) {
        this.storage.selectedTeamCityProject = project;
    }

    public Object getSelectedTeamCityProject() {
        return this.storage.selectedTeamCityProject;
    }

    public List<MavenModule> getMavenModules() {
        return this.helper.mavenBuild.modules;
    }
}
