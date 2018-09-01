package ch.scheitlin.alex.intellij.plugins.toolWindow;

import ch.scheitlin.alex.build.swing.ErrorPanel;
import ch.scheitlin.alex.intellij.plugins.services.Controller;
import ch.scheitlin.alex.build.model.Error;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class BuildSummaryPanel extends JPanel {
    private JPanel panelContent;
    private JPanel panelSummary;
    private JLabel labelBuildStatusKey;
    private JLabel labelBuildStatusValue;
    private JLabel labelBuildStatusTextKey;
    private JLabel labelBuildStatusTextValue;
    private JLabel labelFailureCategoryKey;
    private JLabel labelFailureCategoryValue;
    private JLabel labelFailedGoalKey;
    private JLabel labelFailedGoalValue;
    private JLabel labelFailedMessageKey;
    private JLabel labelFailedMessageValue;
    private JLabel labelProjectKey;
    private JLabel labelProjectValue;
    private JLabel labelBuildConfigurationKey;
    private JLabel labelBuildConfigurationValue;
    private JLabel labelBranchKey;
    private JLabel labelBranchValue;
    private JLabel labelErrorsKey;
    private JPanel panelErrorsValue;
    private JTextPane labelInformation;
    private JButton buttonBack;
    private JButton buttonContinue;
    private JScrollPane scrollPane;

    private final String BUILD_STATUS_TITLE = "Build Status:";
    private final String BUILD_STATUS_TEXT_TITLE = "Status Text:";
    private final String FAILURE_CATEGORY_TITLE = "Failure Category:";
    private final String FAILED_GOAL_TITLE = "Failed Goal:";
    private final String FAILED_MESSAGE_TITLE = "Error Message:";
    private final String PROJECT_TITLE = "Project:";
    private final String BUILD_CONFIGURATION_TITLE = "Build Configuration:";
    private final String BRANCH_NAME_TITLE = "Branch:";
    private final String ERRORS_TITLE = "Errors:";

    private final String BACK_BUTTON_OVERVIEW = "Back to Overview";
    private final String BACK_BUTTON_ABORT = "Abort";
    private final String CONTINUE_BUTTON_CHECKOUT = "Checkout";
    private final String CONTINUE_BUTTON_FINISH = "Finish";

    public BuildSummaryPanel(
            String buildStatus,
            String buildStatusText,
            String failureCategory,
            String failedGoal,
            String failedMessage,
            String projectName,
            String buildConfigurationName,
            String branchName,
            List<Error> errors,
            boolean isFixing,
            String newBranch,
            ActionListener fixAction,
            ActionListener finishAction,
            ActionListener abortAction
    ) {
        // set layout
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // configure and add content panel
        initAndAddContentPanel(20);

        this.panelSummary = initSummaryPanel(
                this.BUILD_STATUS_TITLE, buildStatus,
                this.BUILD_STATUS_TEXT_TITLE, buildStatusText,
                this.FAILURE_CATEGORY_TITLE, failureCategory,
                this.FAILED_GOAL_TITLE, failedGoal,
                this.FAILED_MESSAGE_TITLE, failedMessage,
                this.PROJECT_TITLE, projectName,
                this.BUILD_CONFIGURATION_TITLE, buildConfigurationName,
                this.BRANCH_NAME_TITLE, branchName
        );
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = JBUI.insets(0);
        c.weightx = 1.0;
        this.panelContent.add(this.panelSummary, c);

        if (errors != null) {
            // configure and add label with errors title
            this.labelErrorsKey = initLabel(this.ERRORS_TITLE);
            c.anchor = GridBagConstraints.LINE_START;
            c.gridx = 0;
            c.gridy = 1;
            c.gridwidth = 2;
            c.fill = GridBagConstraints.NONE;
            c.insets = JBUI.insets(20, 0, 0, 0);
            c.weightx = 0.0;
            this.panelContent.add(this.labelErrorsKey, c);

            // configure and add panel with errors
            initErrorsValuePanel(errors, failureCategory, isFixing);
            c.anchor = GridBagConstraints.LINE_START;
            c.gridx = 0;
            c.gridy = 2;
            c.gridwidth = 2;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = JBUI.insets(0);
            c.weightx = 1.0;
            this.panelContent.add(panelErrorsValue, c);
        }

        if (buildStatus.equals("FAILURE")) {
            // configure and add label with information message
            initInformationLabel(isFixing, this.CONTINUE_BUTTON_CHECKOUT, this.CONTINUE_BUTTON_FINISH, newBranch);
            c.anchor = GridBagConstraints.LINE_START;
            c.gridx = 0;
            c.gridy = 3;
            c.gridwidth = 2;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = JBUI.insets(20, 0, 0, 0);
            c.weightx = 1.0;
            this.panelContent.add(labelInformation, c);
        }

        // configure and add button to go back
        initBackButton(isFixing, this.BACK_BUTTON_OVERVIEW, this.BACK_BUTTON_ABORT, abortAction);
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.insets = JBUI.insets(20, 0, 0, 0);
        c.weightx = 0.0;
        this.panelContent.add(buttonBack, c);

        if (buildStatus.equals("FAILURE")) {
            // configure and add button to continue
            initContinueButton(isFixing, this.CONTINUE_BUTTON_CHECKOUT, this.CONTINUE_BUTTON_FINISH, fixAction, finishAction);
            c.anchor = GridBagConstraints.LINE_END;
            c.gridx = 1;
            c.gridy = 4;
            c.gridwidth = 1;
            c.fill = GridBagConstraints.NONE;
            c.insets = JBUI.insets(20, 0, 0, 0);
            c.weightx = 0.0;
            this.panelContent.add(buttonContinue, c);
        }

        // add panel to stick content to the top
        JPanel panel = new JPanel();
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        c.insets = JBUI.insets(0, 0, 0, 0);
        c.weightx = 1.0;
        c.weighty = 1.0;
        this.panelContent.add(panel, c);
    }

    private void initAndAddContentPanel(int padding) {
        this.panelContent = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // create panel to add padding
        JPanel paddingPanel = new JPanel();
        paddingPanel.setLayout(new GridBagLayout());
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = JBUI.insets(padding);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        paddingPanel.add(this.panelContent, c);

        // create panel with vertical scroll bar
        JPanel scrollBarPanel = new JPanel();
        scrollBarPanel.setLayout(new GridBagLayout());
        this.scrollPane = new JBScrollPane(paddingPanel);
        this.scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.scrollPane.setBorder(null);
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = JBUI.insets(0);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        scrollBarPanel.add(this.scrollPane, c);

        // add panel to summary panel
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = JBUI.insets(0);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        this.add(scrollBarPanel, c);
    }

    private JPanel initSummaryPanel(
            String buildStatusTitle, String buildStatusText,
            String buildStatusTextTitle, String buildStatusTextText,
            String failureCategoryTitle, String failureCategoryText,
            String failedGoalTitle, String failedGoalText,
            String failedMessageTitle, String failedMessageText,
            String projectTitle, String projectText,
            String buildConfigurationTitle, String buildConfigurationText,
            String branchNameTitle, String branchNameText
    ) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        int gap = 10;

        // configure and add label with build status title
        this.labelBuildStatusKey = initLabel(buildStatusTitle);
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.NONE;
        c.insets = JBUI.insets(0, 0, 0, 0);
        c.weightx = 0.0;
        panel.add(this.labelBuildStatusKey, c);

        // configure and add label with build status
        this.labelBuildStatusValue = initLabel(buildStatusText);
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 1;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = JBUI.insets(0, gap, 0, 0);
        c.weightx = 1.0;
        panel.add(this.labelBuildStatusValue, c);

        // configure and add label with build status text title
        this.labelBuildStatusTextKey = initLabel(buildStatusTextTitle);
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.NONE;
        c.insets = JBUI.insets(0, 0, 0, 0);
        c.weightx = 0.0;
        panel.add(this.labelBuildStatusTextKey, c);

        // configure and add label with build status text
        this.labelBuildStatusTextValue = initLabel(buildStatusTextText);
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 1;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = JBUI.insets(0, gap, 0, 0);
        c.weightx = 1.0;
        panel.add(this.labelBuildStatusTextValue, c);

        if (buildStatusText.equals("FAILURE")) {

            // configure and add label with failure category title
            this.labelFailureCategoryKey = initLabel(failureCategoryTitle);
            c.anchor = GridBagConstraints.LINE_START;
            c.gridx = 0;
            c.gridy = 2;
            c.fill = GridBagConstraints.NONE;
            c.insets = JBUI.insets(0, 0, 0, 0);
            c.weightx = 0.0;
            panel.add(this.labelFailureCategoryKey, c);

            // configure and add label with failure category
            this.labelFailureCategoryValue = initLabel(failureCategoryText);
            c.anchor = GridBagConstraints.LINE_START;
            c.gridx = 1;
            c.gridy = 2;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = JBUI.insets(0, gap, 0, 0);
            c.weightx = 1.0;
            panel.add(this.labelFailureCategoryValue, c);

            if (failedMessageText == null) {
                // configure and add label with failed goal title
                this.labelFailedGoalKey = initLabel(failedGoalTitle);
                c.anchor = GridBagConstraints.LINE_START;
                c.gridx = 0;
                c.gridy = 3;
                c.fill = GridBagConstraints.NONE;
                c.insets = JBUI.insets(0, 0, 0, 0);
                c.weightx = 0.0;
                panel.add(this.labelFailedGoalKey, c);

                // configure and add label with failed goal
                this.labelFailedGoalValue = initLabel(failedGoalText);
                c.anchor = GridBagConstraints.LINE_START;
                c.gridx = 1;
                c.gridy = 3;
                c.fill = GridBagConstraints.HORIZONTAL;
                c.insets = JBUI.insets(0, gap, 0, 0);
                c.weightx = 1.0;
                panel.add(this.labelFailedGoalValue, c);
            } else {
                // configure and add label with failed message title
                this.labelFailedMessageKey = initLabel(failedMessageTitle);
                c.anchor = GridBagConstraints.LINE_START;
                c.gridx = 0;
                c.gridy = 3;
                c.fill = GridBagConstraints.NONE;
                c.insets = JBUI.insets(0, 0, 0, 0);
                c.weightx = 0.0;
                panel.add(this.labelFailedMessageKey, c);

                // configure and add label with failed message
                this.labelFailedMessageValue = initLabel(failedMessageText);
                c.anchor = GridBagConstraints.LINE_START;
                c.gridx = 1;
                c.gridy = 3;
                c.fill = GridBagConstraints.HORIZONTAL;
                c.insets = JBUI.insets(0, gap, 0, 0);
                c.weightx = 1.0;
                panel.add(this.labelFailedMessageValue, c);
            }
        }

        // configure and add label with project title
        this.labelProjectKey = initLabel(projectTitle);
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 0;
        c.gridy = 4;
        c.fill = GridBagConstraints.NONE;
        c.insets = JBUI.insets(0, 0, 0, 0);
        c.weightx = 0.0;
        panel.add(this.labelProjectKey, c);

        // configure and add label with project name
        this.labelProjectValue = initLabel(projectText);
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 1;
        c.gridy = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = JBUI.insets(0, gap, 0, 0);
        c.weightx = 1.0;
        panel.add(this.labelProjectValue, c);

        // configure and add label with build configuration title
        this.labelBuildConfigurationKey = initLabel(buildConfigurationTitle);
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 0;
        c.gridy = 5;
        c.fill = GridBagConstraints.NONE;
        c.insets = JBUI.insets(0, 0, 0, 0);
        c.weightx = 0.0;
        panel.add(this.labelBuildConfigurationKey, c);

        // configure and add label with build configuration name
        this.labelBuildConfigurationValue = initLabel(buildConfigurationText);
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 1;
        c.gridy = 5;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = JBUI.insets(0, gap, 0, 0);
        c.weightx = 1.0;
        panel.add(this.labelBuildConfigurationValue, c);

        // configure and add label with branch name title
        this.labelBranchKey = initLabel(branchNameTitle);
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 0;
        c.gridy = 6;
        c.fill = GridBagConstraints.NONE;
        c.insets = JBUI.insets(0, 0, 0, 0);
        c.weightx = 0.0;
        panel.add(this.labelBranchKey, c);

        // configure and add label with branch name
        this.labelBranchValue = initLabel(branchNameText);
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 1;
        c.gridy = 6;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = JBUI.insets(0, gap, 0, 0);
        c.weightx = 1.0;
        panel.add(this.labelBranchValue, c);

        return panel;
    }

    private JLabel initLabel(String text) {
        JLabel label = new JLabel();
        label.setText(text);

        return label;
    }

    private void initErrorsValuePanel(List<Error> errors, String failureCategory, boolean isFixing) {
        if (errors == null) {
            return;
        }
        this.panelErrorsValue = new JPanel();
        this.panelErrorsValue.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        for (int i = 0; i < errors.size(); i++) {
            // create new error swing component
            String actionButton1Text = "Show";
            String actionButton2Text = null;
            if (failureCategory == "TESTING" && isFixing) {
                actionButton2Text = "Debug";
            }
            ErrorPanel errorComponent = new ErrorPanel(errors.get(i), actionButton1Text, actionButton2Text);

            // create actions for action buttons of error component
            final ErrorPanel that = errorComponent;

            // show file action
            ActionListener actionListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Controller.getInstance().openErrorInFile(that.getError());
                }
            };
            errorComponent.addButton1Action(actionListener);

            if (actionButton2Text != null) {
                // debug error action
                ActionListener actionListener2 = new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Controller.getInstance().debugError(that.getError());
                    }
                };
                errorComponent.addButton2Action(actionListener2);
            }

            // add bottom border if there is at least one more error panel
            if (i < errors.size() - 1) {
                errorComponent.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
            }

            // add top padding if there was at least one error panel before
            // add bottom padding if there is at least one more error panel
            int padding = 5;

            int paddingTop = 0;
            if (i != 0) {
                // not first panel
                paddingTop = padding;
            }

            int paddingBottom = 0;
            if (i < errors.size() - 1) {
                // not last panel
                paddingBottom = padding;
            }

            Border border = errorComponent.getBorder();
            Border margin = BorderFactory.createEmptyBorder(paddingTop, 0, paddingBottom, 0);
            errorComponent.setBorder(BorderFactory.createCompoundBorder(border, margin));

            c.anchor = GridBagConstraints.LINE_START;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = i;
            c.weightx = 1.0;
            this.panelErrorsValue.add(errorComponent, c);
        }
    }

    private void initInformationLabel(boolean isFixing, String checkoutText, String finishText, String newBranch) {
        this.labelInformation = new JTextPane();
        this.labelInformation.setEditable(false);
        this.labelInformation.setCursor(null);
        this.labelInformation.setOpaque(false);
        this.labelInformation.setFocusable(false);
        this.labelInformation.setContentType("text/html");

        String fontFamily = UIManager.getFont("Label.font").getFamily();
        int fontSize = UIManager.getFont("Label.font").getSize();
        String information = "";

        if (!isFixing) {
            information = "<p>Your local code may be different from the one that failed on the build server!" +
                    " Explore the errors in your local code using the <b>Show</b> buttons above.</p>" +
                    "<p>Use the <b>" + checkoutText + "</b> button below to change to the code that caused the build " +
                    "failure and explore the errors there. <i>A new branch will be created and uncommitted changes " +
                    "will be stashed automatically.<i></p>";
        } else {
            information = "<p>You are now on branch <b>" + newBranch + "</b> showing the code that caused the build failure.</p>" +
                    "<p>Once you are finished, click the <b>" + finishText + "</b> button.</p>";
        }

        this.labelInformation.setText(
                "<div style='font-family:" + fontFamily + ";font-size:" + fontSize + ";'>" + information + "</div>"
        );
    }

    private void initBackButton(boolean isFixing, String overviewText, String abortText, ActionListener abortAction) {
        this.buttonBack = new JButton();

        if (!isFixing) {
            this.buttonBack.setText(overviewText);
        } else {
            this.buttonBack.setText(abortText);
        }
        this.buttonBack.addActionListener(abortAction);
    }

    private void initContinueButton(
            boolean isFixing, String checkoutText, String finishText,
            ActionListener fixAction, ActionListener finishAction) {
        this.buttonContinue = new JButton();

        if (!isFixing) {
            this.buttonContinue.setText(checkoutText);
            this.buttonContinue.addActionListener(fixAction);

        } else {
            this.buttonContinue.setText(finishText);
            this.buttonContinue.addActionListener(finishAction);
        }
    }
}
