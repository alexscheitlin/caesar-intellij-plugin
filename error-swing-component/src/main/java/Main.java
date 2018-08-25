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
        // create frame
        JFrame frame = new JFrame();
        frame.setLayout(new GridBagLayout());
        frame.setSize(new Dimension(200, 200));

        // create new error swing components
        List<Error> errors = getDummyErrors();
        ErrorPanel[] errorComponents = new ErrorPanel[getDummyErrors().size()];
        for (int i = 0; i < errors.size(); i++) {
            errorComponents[i] = new ErrorPanel(errors.get(i), "Show", "Debug");

            // create action for action button of error component
            final ErrorPanel that = errorComponents[i];
            ActionListener actionListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Opening " + that.getError().getPath() + "/" + that.getError().getFile() + "...");
                }
            };
            errorComponents[i].addButton1Action(actionListener);

            // create frame and add error components
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = i;
            c.insets = new Insets(10, 0, 10, 0);
            c.weightx = 1.0;
            frame.add(errorComponents[i], c);
        }

        // set look and feel of frame
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
        }

        // show frame
        frame.setVisible(true);
    }

    public static List<Error> getDummyErrors() {
        // normal error
        String path1 = "C:/users/alex/desktop";
        String file1 = "Test.java";
        int line1 = 1;
        int column1 = 2;
        String message1 = "Error message";
        Error error1 = new Error(path1, file1, line1, column1, message1);

        // error with no path
        String path2 = null;
        String file2 = "Test.java";
        int line2 = 1;
        int column2 = 2;
        String message2 = "Error message";
        Error error2 = new Error(path2, file2, line2, column2, message2);

        // error with very long message and multiple lines
        String path3 = "C:/users/alex/desktop";
        String file3 = "Test.java";
        int line3 = 1;
        int column3 = 2;
        String message3 = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum.\nStet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";
        Error error3 = new Error(path3, file3, line3, column3, message3);

        ArrayList errors = new ArrayList<Error>();
        errors.add(error1);
        errors.add(error2);
        errors.add(error3);

        return errors;
    }
}
