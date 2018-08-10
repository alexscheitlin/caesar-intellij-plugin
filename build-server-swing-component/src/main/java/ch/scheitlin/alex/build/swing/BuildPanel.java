package ch.scheitlin.alex.build.swing;

import ch.scheitlin.alex.build.model.Build;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class BuildPanel extends JPanel {
    // data
    public Build build;

    // components
    private JLabel labelBuildNumber;
    private JLabel labelBuildStatus;
    private JLabel labelBuildStatusText;
    private JButton buttonAction;

    // appearance constants
    private final int BUILD_NUMBER_LABEL_WIDTH = 30;
    private final int BUILD_STATUS_LABEL_WIDTH = 20;
    private final int BUILD_STATUS_TEXT_LABEL_WIDTH = 100;
    private final String BUILD_STATUS_SIGN_SUCCESSFUL = Character.toString((char) 10004);  // ✔
    private final String BUILD_STATUS_SIGN_FAILURE = Character.toString((char) 10008);     // ✘
    private final Color BUILD_STATUS_COLOR_SUCCESSFUL = Color.GREEN;
    private final Color BUILD_STATUS_COLOR_FAILURE = Color.RED;

    public BuildPanel(Build build, String actionButtonText) {
        // set data variables
        this.build = build;

        // get build information
        String buildNumber = this.build.getNumber();
        boolean buildStatus = this.build.getStatus();
        String buildStatusText = this.build.getStatusText();

        // set layout for the BuildPanel
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // initialize label with build number
        this.labelBuildNumber = initBuildNumberLabel(buildNumber, this.BUILD_NUMBER_LABEL_WIDTH);
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.0;
        this.add(this.labelBuildNumber, c);

        // initialize label with build status (cross or tick)
        this.labelBuildStatus = initBuildStatusLabel(
                buildStatus,
                this.BUILD_STATUS_SIGN_SUCCESSFUL, this.BUILD_STATUS_SIGN_FAILURE,
                this.BUILD_STATUS_COLOR_SUCCESSFUL, this.BUILD_STATUS_COLOR_FAILURE,
                this.BUILD_STATUS_LABEL_WIDTH
        );
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.0;
        this.add(this.labelBuildStatus, c);

        // initialize label with build status text
        this.labelBuildStatusText = initBuildStatusTextLabel(
                buildStatusText,
                this.BUILD_STATUS_TEXT_LABEL_WIDTH);
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 1.0;
        this.add(this.labelBuildStatusText, c);

        // initialize button for action
        this.buttonAction = initActionButton(actionButtonText);
        c.anchor = GridBagConstraints.LINE_END;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 3;
        c.gridy = 0;
        c.weightx = 0.0;
        this.add(this.buttonAction, c);
    }

    private JLabel initBuildNumberLabel(String buildNumber, int labelWidth) {
        JLabel label = new JLabel();
        label.setText("# " + buildNumber);

        // set size
        Dimension newSize = new Dimension(labelWidth, 20); // 20 is default label height
        label.setPreferredSize(newSize);

        return label;
    }

    private JLabel initBuildStatusLabel(
            boolean buildStatus,
            String successfulSign, String failureSign,
            Color successfulColor, Color failureColor,
            int labelWidth) {
        JLabel label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER);

        // set size
        Dimension newSize = new Dimension(labelWidth, 20); // 20 is default label height
        label.setPreferredSize(newSize);

        // set color and sign
        if (buildStatus) {
            label.setForeground(successfulColor);
            label.setText(successfulSign);
        } else {
            label.setForeground(failureColor);
            label.setText(failureSign);
        }

        return label;
    }

    private JLabel initBuildStatusTextLabel(String buildStatusText, int labelWidth) {
        JLabel label = new JLabel();
        label.setText(buildStatusText);

        // set size
        Dimension newSize = new Dimension(labelWidth, 20); // 20 is default label height
        label.setPreferredSize(newSize);

        return label;
    }

    private JButton initActionButton(String text) {
        JButton button = new JButton();
        button.setText(text);

        return button;
    }

    public void addActionListener(ActionListener actionListener) {
        this.buttonAction.addActionListener(actionListener);
    }
}
