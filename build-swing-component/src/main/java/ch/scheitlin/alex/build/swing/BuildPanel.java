package ch.scheitlin.alex.build.swing;

import ch.scheitlin.alex.build.model.Build;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class BuildPanel extends JPanel {
    private JLabel labelBuildNumber;
    private JLabel labelBuildStatus;
    private JLabel labelBuildStatusText;
    private JButton buttonAction;

    public Build build;

    private String successfulSign = Character.toString((char) 10004);  // ✔
    private String failureSign = Character.toString((char) 10008);     // ✘

    private Color successfulColor = Color.GREEN;
    private Color failureColor = Color.RED;

    public BuildPanel(Build build, String actionButtonText) {
        // get build information
        this.build = build;

        // set layout for build panel
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // configure and add label with build number
        initBuildNumberLabel(this.build.getNumber());
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0.0;
        this.add(this.labelBuildNumber, c);

        // configure and add label with build status
        initBuildStatusLabel(this.build.getStatus());
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0.0;
        this.add(this.labelBuildStatus, c);

        // configure and add label with build status text
        initBuildStatusTextLabel(this.build.getStatusText());
        c.gridx = 2;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        this.add(this.labelBuildStatusText, c);

        // configure and add action button
        initActionButton(actionButtonText);
        c.gridx = 3;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_END;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0.0;
        this.add(this.buttonAction, c);
    }

    private void initBuildNumberLabel(String buildNumber) {
        this.labelBuildNumber = new JLabel();
        this.labelBuildNumber.setText("# " + buildNumber);
        this.labelBuildNumber.setPreferredSize(new Dimension(30, 20));
        this.labelBuildNumber.setHorizontalAlignment(SwingConstants.LEFT);
        //this.labelBuildNumber.setBorder(BorderFactory.createLineBorder(Color.black));
    }

    private void initBuildStatusLabel(boolean buildStatus) {
        this.labelBuildStatus = new JLabel();
        if (buildStatus) {
            this.labelBuildStatus.setForeground(this.successfulColor);
            this.labelBuildStatus.setText(this.successfulSign);
        } else {
            this.labelBuildStatus.setForeground(this.failureColor);
            this.labelBuildStatus.setText(this.failureSign);
        }
        this.labelBuildStatus.setPreferredSize(new Dimension(20, 20));
        this.labelBuildStatus.setHorizontalAlignment(SwingConstants.CENTER);
        //this.labelBuildStatus.setBorder(BorderFactory.createLineBorder(Color.black));
    }

    private void initBuildStatusTextLabel(String buildStatusText) {
        this.labelBuildStatusText = new JLabel();
        this.labelBuildStatusText.setText(buildStatusText);
        this.labelBuildStatusText.setPreferredSize(new Dimension(100, 20));
        this.labelBuildStatusText.setHorizontalAlignment(SwingConstants.LEFT);
        //this.labelBuildStatusText.setBorder(BorderFactory.createLineBorder(Color.black));
    }

    private void initActionButton(String text) {
        this.buttonAction = new JButton();
        this.buttonAction.setText(text);
    }

    public void addActionListener(ActionListener actionListener) {
        this.buttonAction.addActionListener(actionListener);
    }
}
