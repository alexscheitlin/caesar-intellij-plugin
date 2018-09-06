package ch.scheitlin.alex.intellij.plugins.services.helpers;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.RunManager;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointManager;
import com.intellij.xdebugger.breakpoints.XBreakpointProperties;
import com.intellij.xdebugger.breakpoints.XLineBreakpointType;

import java.io.File;
import java.util.List;

public class DebugHelper {
    // get a list of all run configurations of a intellij project
    public static List<RunConfiguration> getRunConfigurations(Project project) {
        if (project == null) {
            return null;
        }

        final RunManager runManager = RunManager.getInstance(project);
        return runManager.getAllConfigurationsList();
    }

    // add a new line break point to the specified line of the given file and activate it
    public static boolean addLineBreakpoint(final Project project, final String fileUrl, final int line) {
        if (project == null || fileUrl == null || fileUrl == "" || line < 0) {
            return false;
        }

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
                super("id", "title");
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

        final XDebuggerUtil debuggerUtil = XDebuggerUtil.getInstance();
        final XBreakpointManager breakpointManager = XDebuggerManager.getInstance(project).getBreakpointManager();
        final MyLineBreakpointType MY_LINE_BREAKPOINT_TYPE = new MyLineBreakpointType();
        final MyBreakpointProperties MY_LINE_BREAKPOINT_PROPERTIES = new MyBreakpointProperties();

        // disable all other breakpoints
        for (XBreakpoint breakpoint : breakpointManager.getAllBreakpoints()) {
            if (breakpoint.isEnabled()) {
                XSourcePosition position = breakpoint.getSourcePosition();
                if (position != null) {
                    debuggerUtil.toggleLineBreakpoint(project, position.getFile(), position.getLine());
                }
            }
        }

        // add new line break point
        Runnable runnable = () -> breakpointManager.addLineBreakpoint(
                MY_LINE_BREAKPOINT_TYPE,
                fileUrl,
                line,
                MY_LINE_BREAKPOINT_PROPERTIES
        );
        WriteCommandAction.runWriteCommandAction(project, runnable);

        // toggle breakpoint to activate
        final VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(new File(fileUrl));
        if (virtualFile == null) {
            return false;
        }
        debuggerUtil.toggleLineBreakpoint(project, virtualFile, line);

        return true;
    }

    // start a run configuration in debugging mode
    public static boolean startDebugger(Project project, RunConfiguration runConfiguration) {
        if (project == null || runConfiguration == null) {
            return false;
        }

        Executor executor = DefaultDebugExecutor.getDebugExecutorInstance();
        if (executor == null) {
            return false;
        }

        try {
            ExecutionEnvironmentBuilder
                    .create(project, executor, runConfiguration)
                    .buildAndExecute();
        } catch (ExecutionException ex) {
            return false;
        }

        return true;
    }
}
