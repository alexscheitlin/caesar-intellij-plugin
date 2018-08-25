package ch.scheitlin.alex.intellij.plugins.services.helpers;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindowManager;

public class IntelliJHelper {
    public static VirtualFile getProjectDirectoryFile(Project project) {
        return project.getBaseDir();
    }
    public static String getProjectPath(Project project) {
        return getProjectDirectoryFile(project).getPath();
    }

    public static void showToolWindow(Project project, String toolWindowId) {
        ToolWindowManager manager = ToolWindowManager.getInstance(project);
        com.intellij.openapi.wm.ToolWindow toolWindow = manager.getToolWindow(toolWindowId);
        toolWindow.show(null);
    }
}
