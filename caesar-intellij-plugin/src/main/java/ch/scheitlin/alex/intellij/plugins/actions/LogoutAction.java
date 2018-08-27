package ch.scheitlin.alex.intellij.plugins.actions;

import ch.scheitlin.alex.intellij.plugins.services.Controller;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;

public class LogoutAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // log out via the Controller application service
        if (!Controller.getInstance().disconnect()) {
            System.out.println("Could not disconnect from build server!");
        }
    }

    // only show menu item if user is logged in
    public void update(AnActionEvent event) {
        Presentation presentation = event.getPresentation();

        // disable and don't show item on the menu if user is not logged in
        Controller controller = Controller.getInstance();
        presentation.setEnabled(controller.isLoggedIn());
        presentation.setVisible(controller.isLoggedIn());
    }
}
