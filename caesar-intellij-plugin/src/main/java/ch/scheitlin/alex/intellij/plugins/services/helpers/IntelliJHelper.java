package ch.scheitlin.alex.intellij.plugins.services.helpers;

import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

import javax.swing.*;
import java.io.File;

public class IntelliJHelper {
    public static boolean hasDarkTheme() {
        return UIManager.getLookAndFeel().getName().contains("Darcula");
    }

    public static VirtualFile getProjectDirectoryFile(Project project) {
        if (project == null) {
            return null;
        }

        return project.getBaseDir();
    }

    public static String getProjectPath(Project project) {
        if (project == null) {
            return null;
        }

        VirtualFile projectDirectory = getProjectDirectoryFile(project);
        if (projectDirectory == null) {
            return null;
        }

        return projectDirectory.getPath();
    }

    public static boolean reloadProjectFiles(Project project) {
        if (project == null) {
            return false;
        }

        VirtualFile projectDirectory = getProjectDirectoryFile(project);
        if (projectDirectory == null) {
            return false;
        }

        projectDirectory.refresh(true, true);

        return true;
    }

    public static boolean showToolWindow(Project project, String toolWindowId) {
        if (project == null || toolWindowId == null || toolWindowId == "") {
            return false;
        }

        ToolWindow toolWindow = getToolWindowById(project, toolWindowId);
        if (toolWindow == null) {
            return false;
        }

        toolWindow.show(null);

        return true;
    }

    public static boolean hideToolWindow(Project project, String toolWindowId) {
        if (project == null || toolWindowId == null || toolWindowId == "") {
            return false;
        }

        ToolWindow toolWindow = getToolWindowById(project, toolWindowId);
        if (toolWindow == null) {
            return false;
        }

        toolWindow.hide(null);

        return true;
    }

    public static boolean openFile(Project project, String filePath, int lineNumber, int columnNumber) {
        if (project == null || filePath == null || filePath == "") {
            return false;
        }

        File file = new File(filePath);
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file);
        if (virtualFile == null) {
            return false;
        }

        OpenFileDescriptor openFileDescriptor = new OpenFileDescriptor(project, virtualFile, lineNumber, columnNumber);

        try {
            openFileDescriptor.navigate(true);
        } catch (IllegalStateException ex) {
            return false;
        }

        return true;
    }

    private static ToolWindow getToolWindowById(Project project, String id) {
        if (project == null || id == null || id == "") {
            return null;
        }

        ToolWindowManager manager = ToolWindowManager.getInstance(project);
        if (manager == null) {
            return null;
        }

        return manager.getToolWindow(id);
    }
}
