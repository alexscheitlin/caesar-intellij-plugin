package ch.scheitlin.alex.intellij.plugins.actions;

import ch.scheitlin.alex.intellij.plugins.services.Controller;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;

public class LoginAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // log in via the Controller application service
        if (!Controller.getInstance().connect()) {
            String title = "Connection Error";
            String content = "Could not connect to build server!";
            Controller.getInstance().pushNotification(title, content);
        }
    }

    // only show menu item if user is not logged in
    public void update(AnActionEvent event) {
        Presentation presentation = event.getPresentation();

        // disable and don't show item on the menu if user is logged in
        Controller controller = Controller.getInstance();
        presentation.setEnabled(!controller.isLoggedIn());
        presentation.setVisible(!controller.isLoggedIn());
    }
}
