package ch.scheitlin.alex.intellij.plugins.actions;

import ch.scheitlin.alex.intellij.plugins.services.Controller;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.components.ServiceManager;

public class LogoutAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // log out via the Controller application service
        Controller controller = ServiceManager.getService(Controller.class);
        controller.logout();
    }

    // only show menu item if user is logged in
    public void update(AnActionEvent event) {
        Presentation presentation = event.getPresentation();

        // disable and don't show item on the menu if user is not logged in
        Controller controller = ServiceManager.getService(Controller.class);
        presentation.setEnabled(controller.isConnected());
        presentation.setVisible(controller.isConnected());
    }
}
