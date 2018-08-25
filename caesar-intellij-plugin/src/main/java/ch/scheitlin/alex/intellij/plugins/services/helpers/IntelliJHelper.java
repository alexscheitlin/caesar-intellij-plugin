package ch.scheitlin.alex.intellij.plugins.services.helpers;

import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindowManager;

import java.io.File;

public class IntelliJHelper {
    public static VirtualFile getProjectDirectoryFile(Project project) {
        return project.getBaseDir();
    }

    public static String getProjectPath(Project project) {
        return getProjectDirectoryFile(project).getPath();
    }

    public static void reloadProjectFiles(Project project) {
        getProjectDirectoryFile(project).refresh(true, true);
    }

    public static void showToolWindow(Project project, String toolWindowId) {
        ToolWindowManager manager = ToolWindowManager.getInstance(project);
        com.intellij.openapi.wm.ToolWindow toolWindow = manager.getToolWindow(toolWindowId);
        toolWindow.show(null);
    }

    public static void openFile(Project project, String filePath, int lineNumber, int columnNumber) {
        File file = new File(filePath);
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file);
        OpenFileDescriptor openFileDescriptor = new OpenFileDescriptor(project, virtualFile, lineNumber, columnNumber);
        openFileDescriptor.navigate(true);
    }
}
