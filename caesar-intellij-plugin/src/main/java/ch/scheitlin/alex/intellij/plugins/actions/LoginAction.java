package ch.scheitlin.alex.intellij.plugins.actions;

import ch.scheitlin.alex.intellij.plugins.services.Controller;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.components.ServiceManager;

public class LoginAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // log in via the Controller application service
        Controller controller = ServiceManager.getService(Controller.class);
        if (controller.login(e.getProject())) {
            Controller.getInstance().showToolWindow();
        }
    }

    // only show menu item if user is not logged in
    public void update(AnActionEvent event) {
        Presentation presentation = event.getPresentation();

        // disable and don't show item on the menu if user is logged in
        Controller controller = ServiceManager.getService(Controller.class);
        presentation.setEnabled(!controller.isConnected());
        presentation.setVisible(!controller.isConnected());
    }
}
