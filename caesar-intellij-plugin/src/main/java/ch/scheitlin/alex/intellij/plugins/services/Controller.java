package ch.scheitlin.alex.intellij.plugins.services;

import ch.scheitlin.alex.build.Caesar;
import ch.scheitlin.alex.build.model.BuildServer;
import ch.scheitlin.alex.build.model.BuildServerBuild;
import ch.scheitlin.alex.build.model.BuildServerType;
import ch.scheitlin.alex.build.model.Error;
import ch.scheitlin.alex.intellij.plugins.dialogs.LoginDialog;
import ch.scheitlin.alex.intellij.plugins.toolWindow.ToolWindow;
import ch.scheitlin.alex.maven.MavenBuild;
import ch.scheitlin.alex.maven.MavenGoal;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.RunManager;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.breakpoints.XBreakpointManager;
import com.intellij.xdebugger.breakpoints.XBreakpointProperties;
import com.intellij.xdebugger.breakpoints.XLineBreakpointType;
import javafx.util.Pair;

import java.io.File;
import java.util.List;

public class Controller {
    private Storage storage;
    private Caesar caesar;

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

    public BuildServer fetchBuildServerInformation() {
        return this.caesar.fetchBuildServerInformation();
    }

    public List<String> getTeamCityProjectNames() {
        return this.caesar.getBuildServerInformation().getProjectNames();
    }

    public ch.scheitlin.alex.build.model.BuildServerProject getTeamCityProject(String projectName) {
        return this.caesar.getBuildServerInformation().getProject(projectName);
    }

    public String getGitRepositoryOriginUrl() {
        return this.storage.project.getBaseDir().getPath();
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

    public void saveTeamCityCredentials(String host, String username, String password) {
        this.storage.saveTeamCityCredentials(host, username, password);
    }

    public void deleteTeamCityCredentials() {
        this.storage.deleteTeamCityCredentials();
    }

    public boolean testTeamCityConnection(String host, String username, String password) {
        return this.caesar.testBuildServerConnection(this.BUILD_SERVER_TYPE, host, username, password);
    }

    public void getBuildInformation(String buildConfigurationName, BuildServerBuild build) {
        this.storage.teamCityBuildConfigurationName = buildConfigurationName;

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
        return this.storage.project;
    }

    private void updateToolWindow() {
        this.storage.toolWindow.update();
    }

    private void reloadProjectFiles() {
        this.storage.project.getBaseDir().refresh(true, true);
    }

    public MavenBuild getMavenBuild() {
        return this.caesar.getMavenBuild();
    }

    public String getRawTeamCityBuildLog() {
        return this.caesar.getBuildServerBuildLog();
    }

    public void openErrorInFile(Error error) {
        String filePath = this.storage.project.getBasePath() + "/" + error.getFullPath();

        int lineNumber = error.getLine() - 1;
        int columnNumber = error.getColumn() - 1;

        // open file
        Controller.getInstance().openFile(filePath, lineNumber, columnNumber);
    }

    public void debugError(Error error) {
        String filePath = this.storage.project.getBasePath() + "/" + error.getFullPath();

        int lineNumber = error.getLine() - 1;

        // create and toggle a line break point
        addLineBreakpoint(this.storage.project, filePath, lineNumber);

        // select run configuration
        // the run configuration for this file needs to exist
        String configurationName = error.getFile().split("\\.")[0];     // remove file ending
        RunConfiguration runConfiguration = null;
        for (RunConfiguration config : getRunConfigurations(this.storage.project)) {
            if (config.getName().equals(configurationName)) {
                runConfiguration = config;
                break;
            }
        }

        try {
            startDebugger(this.storage.project, runConfiguration);
        } catch (ExecutionException ex) {
            System.out.print(ex.getMessage());
        }
    }

    private void openFile(String filePath, int lineNumber, int columnNumber) {
        File file = new File(filePath);
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file);
        OpenFileDescriptor openFileDescriptor = new OpenFileDescriptor(this.storage.project, virtualFile, lineNumber, columnNumber);
        openFileDescriptor.navigate(true);

        System.out.println("Opening " + this.storage.project.getBasePath() + "/" + filePath);
    }

    // add a new line break point to the specified line of the given file and activate it
    private void addLineBreakpoint(final Project project, final String fileUrl, final int line) {
        class MyBreakpointProperties extends XBreakpointProperties<MyBreakpointProperties> {
            public String myOption;

            public MyBreakpointProperties() {
            }

            @Override
            public MyBreakpointProperties getState() {
                return this;
            }

            @Override
            public void loadState(final MyBreakpointProperties state) {
                myOption = state.myOption;
            }
        }

        class MyLineBreakpointType extends XLineBreakpointType<MyBreakpointProperties> {
            public MyLineBreakpointType() {
                super("it", "title");
            }

            @Override
            public MyBreakpointProperties createBreakpointProperties(VirtualFile file, final int line) {
                return null;
            }

            @Override
            public MyBreakpointProperties createProperties() {
                return new MyBreakpointProperties();
            }
        }

        final XBreakpointManager breakpointManager = XDebuggerManager.getInstance(project).getBreakpointManager();
        final MyLineBreakpointType MY_LINE_BREAKPOINT_TYPE = new MyLineBreakpointType();
        final MyBreakpointProperties MY_LINE_BREAKPOINT_PROPERTIES = new MyBreakpointProperties();

        // add new line break point
        Runnable runnable = () -> breakpointManager.addLineBreakpoint(
                MY_LINE_BREAKPOINT_TYPE,
                fileUrl,
                line,
                MY_LINE_BREAKPOINT_PROPERTIES
        );
        WriteCommandAction.runWriteCommandAction(project, runnable);

        // toggle breakpoint to activate
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(new File(fileUrl));
        XDebuggerUtil.getInstance().toggleLineBreakpoint(project, virtualFile, line);
    }


    // start the run configuration in debugging mode
    private void startDebugger(Project project, RunConfiguration runConfiguration) throws ExecutionException {
        Executor executor = DefaultDebugExecutor.getDebugExecutorInstance();

        ExecutionEnvironmentBuilder
                .create(project, executor, runConfiguration)
                .buildAndExecute();
    }

    private List<RunConfiguration> getRunConfigurations(Project project) {
        final RunManager runManager = RunManager.getInstance(project);
        return runManager.getAllConfigurationsList();
    }
}
