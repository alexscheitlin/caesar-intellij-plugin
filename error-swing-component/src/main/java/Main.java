import ch.scheitlin.alex.build.model.Error;
import ch.scheitlin.alex.build.swing.ErrorPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Error> errors = getDummyErrors();

        // create new error swing component
        ErrorPanel errorComponent = new ErrorPanel(errors.get(0), "Show");

        // create action for action button of error component
        final ErrorPanel that = errorComponent;
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Opening " + that.getError().getPath() + "/" + that.getError().getFile() + "...");
            }
        };
        errorComponent.addButtonAction(actionListener);

        // create frame and add error components
        JFrame frame = new JFrame();
        frame.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        frame.add(errorComponent, c);
        frame.setSize(new Dimension(800,800));

        // set look and feel of frame
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {}

        // show frame
        frame.setVisible(true);
    }

    public static List<Error> getDummyErrors() {
        String path1 = "C:/users/alex/desktop";
        String file1 = "Test.java";
        int line1 = 1;
        int column1 = 2;
        String message1 = "Error message";
        Error error1 = new Error(path1, file1, line1, column1, message1);

        ArrayList errors = new ArrayList<Error>();
        errors.add(error1);

        return errors;
    }
}
