package ch.scheitlin.alex.intellij.plugins.services.helpers;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

import javax.swing.*;
import java.io.File;

public class IntelliJHelper {
    // returns whether a dark theme is used (currently only the intellij 'darcula' is checked)
    public static boolean hasDarkTheme() {
        return UIManager.getLookAndFeel().getName().contains("Darcula");
    }

    // gets a intellij project as a virtual file
    public static VirtualFile getProjectDirectoryFile(Project project) {
        if (project == null) {
            return null;
        }

        return project.getBaseDir();
    }

    // gets the path to a intellij project
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

    // reloads all files of a intellij project
    // (helpful if for example a file was changed outside the ide, e.g. with git)
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

    // shows a tool window (specified by its id) in a intellij project
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

    // hides a tool window (specified by its id) in a intellij project
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

    // opens a file within a intellij project
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

    // gets a tool window specified by its id
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

    // creates a new notification
    public static void pushNotification(String pluginId, String title, String content) {
        if (pluginId == null || title == null || content == null) {
            return;
        }

        // create notification
        Notification notification = new Notification(pluginId, title, content, NotificationType.INFORMATION);

        // show notification
        Notifications.Bus.notify(notification);
    }
}
