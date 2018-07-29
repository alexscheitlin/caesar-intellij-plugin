package ch.scheitlin.alex.intellij.plugins.toolWindow;

import ch.scheitlin.alex.build.swing.ErrorPanel;
import ch.scheitlin.alex.intellij.plugins.services.Controller;
import ch.scheitlin.alex.build.model.Error;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

public class InformationPanel extends JPanel {
    private JPanel panelContent;
    private JLabel labelProjectKey;
    private JLabel labelProjectValue;
    private JLabel labelBuildConfigurationKey;
    private JLabel labelBuildConfigurationValue;
    private JLabel labelBuildStatusKey;
    private JLabel labelBuildStatusValue;
    private JLabel labelFailedGoalKey;
    private JLabel labelFailedGoalValue;
    private JLabel labelFailureCategoryKey;
    private JLabel labelFailureCategoryValue;
    private JLabel labelErrorsKey;
    private JPanel panelErrorsValue;
    private JTextPane labelInformation;
    private JButton buttonBack;
    private JButton buttonContinue;

    public InformationPanel(String projectName,
                            String buildConfigurationName,
                            String buildStatus,
                            String failedGoal,
                            String failureCategory,
                            List<Error> errors,
                            Project project
    ) {
        // set layout
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // configure and add content panel
        initAndAddContentPanel(20);

        // configure and add label with project title
        initProjectKeyLabel();
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = JBUI.insets(0);
        c.weightx = 0.5;
        this.panelContent.add(labelProjectKey, c);

        // configure and add label with project name
        initProjectValueLabel(projectName);
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = JBUI.insets(0);
        c.weightx = 0.5;
        this.panelContent.add(labelProjectValue, c);

        // configure and add label with build configuration title
        initBuildConfigurationKeyLabel();
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = JBUI.insets(0);
        c.weightx = 0.5;
        this.panelContent.add(labelBuildConfigurationKey, c);

        // configure and add label with build configuration name
        initBuildConfigurationValueLabel(buildConfigurationName);
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = JBUI.insets(0);
        c.weightx = 0.5;
        this.panelContent.add(labelBuildConfigurationValue, c);

        // configure and add label with build status title
        initBuildStatusKeyLabel();
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = JBUI.insets(0);
        c.weightx = 0.5;
        this.panelContent.add(labelBuildStatusKey, c);

        // configure and add label with build status
        initBuildStatusValueLabel(buildStatus);
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = JBUI.insets(0);
        c.weightx = 0.5;
        this.panelContent.add(labelBuildStatusValue, c);

        // configure and add label with failed goal title
        initFailedGoalKeyLabel();
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = JBUI.insets(0);
        c.weightx = 0.5;
        this.panelContent.add(labelFailedGoalKey, c);

        // configure and add label with failed goal
        initFailedGoalValueLabel(failedGoal);
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 1;
        c.gridy = 3;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = JBUI.insets(0);
        c.weightx = 0.5;
        this.panelContent.add(labelFailedGoalValue, c);

        // configure and add label with failure category title
        initFailureCategoryKeyLabel();
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = JBUI.insets(0);
        c.weightx = 0.5;
        this.panelContent.add(labelFailureCategoryKey, c);

        // configure and add label with failure category
        initFailureCategoryValueLabel(failureCategory);
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 1;
        c.gridy = 4;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = JBUI.insets(0);
        c.weightx = 0.5;
        this.panelContent.add(labelFailureCategoryValue, c);

        // configure and add label with errors title
        initErrorsKeyLabel();
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = JBUI.insets(20, 0, 0, 0);
        c.weightx = 0.5;
        this.panelContent.add(labelErrorsKey, c);

        // configure and add panel with errors
        initErrorsValuePanel(errors, project);
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.NONE;
        c.insets = JBUI.insets(0);
        c.weightx = 0.0;
        this.panelContent.add(panelErrorsValue, c);

        // configure and add label with information message
        initInformationLabel();
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 0;
        c.gridy = 7;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = JBUI.insets(20, 0, 0, 0);
        c.weightx = 1.0;
        this.panelContent.add(labelInformation, c);

        // configure and add button to go back
        initBackButton();
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 0;
        c.gridy = 8;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.insets = JBUI.insets(20, 0, 0, 0);
        c.weightx = 0.0;
        this.panelContent.add(buttonBack, c);

        // configure and add button to continue
        initContinueButton();
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 1;
        c.gridy = 8;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.insets = JBUI.insets(20, 0, 0, 0);
        c.weightx = 0.0;
        this.panelContent.add(buttonContinue, c);

        // add panel to move content to the top
        // (at least one component needs to have weighty greater than 0.0)
        c.gridx = 0;
        c.gridy = 9;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        c.insets = JBUI.insets(0);
        c.weightx = 1.0;
        c.weighty = 1.0;
        this.panelContent.add(new JPanel(), c);
    }

    private void initAndAddContentPanel(int padding) {
        this.panelContent = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // request max size to better place the components and add padding to the content
        this.panelContent = new JPanel(new GridBagLayout());
        c.gridx = 0;
        c.gridy = 0;
        c.insets = JBUI.insets(padding);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        this.add(this.panelContent, c);
    }

    private void initProjectKeyLabel() {
        this.labelProjectKey = new JLabel();
        this.labelProjectKey.setText("Project:");
    }

    private void initProjectValueLabel(String projectName) {
        this.labelProjectValue = new JLabel();
        this.labelProjectValue.setText(projectName);
    }

    private void initBuildConfigurationKeyLabel() {
        this.labelBuildConfigurationKey = new JLabel();
        this.labelBuildConfigurationKey.setText("Build Configuration:");
    }

    private void initBuildConfigurationValueLabel(String buildConfigurationName) {
        this.labelBuildConfigurationValue = new JLabel();
        this.labelBuildConfigurationValue.setText(buildConfigurationName);
    }

    private void initBuildStatusKeyLabel() {
        this.labelBuildStatusKey = new JLabel();
        this.labelBuildStatusKey.setText("Build Status:");
    }

    private void initBuildStatusValueLabel(String buildStatus) {
        this.labelBuildStatusValue = new JLabel();
        this.labelBuildStatusValue.setText(buildStatus);
    }

    private void initFailedGoalKeyLabel() {
        this.labelFailedGoalKey = new JLabel();
        this.labelFailedGoalKey.setText("Failed Goal:");
    }

    private void initFailedGoalValueLabel(String failedGoal) {
        this.labelFailedGoalValue = new JLabel();
        this.labelFailedGoalValue.setText(failedGoal);
    }

    private void initFailureCategoryKeyLabel() {
        this.labelFailureCategoryKey = new JLabel();
        this.labelFailureCategoryKey.setText("Failure Category:");
    }

    private void initFailureCategoryValueLabel(String failureCategory) {
        this.labelFailureCategoryValue = new JLabel();
        this.labelFailureCategoryValue.setText(failureCategory);
    }

    private void initErrorsKeyLabel() {
        this.labelErrorsKey = new JLabel();
        this.labelErrorsKey.setText("Errors:");
    }

    private void initErrorsValuePanel(List<Error> errors, Project project) {
        if (errors == null) {
            return;
        }
        this.panelErrorsValue = new JPanel();
        this.panelErrorsValue.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        for (int i = 0; i < errors.size(); i++) {
            // create new error swing component
            ErrorPanel errorComponent = new ErrorPanel(errors.get(i), "Show");

            // create action for action button of error component
            final ErrorPanel that = errorComponent;
            ActionListener actionListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.out.println(project.getBasePath().toString());
                    System.out.println("Opening " + that.getError().getPath() + "/" + that.getError().getFile() + "...");

                    String filePath = that.getError().getPath() + "/" + that.getError().getFile(); // path to file within project (no leading '/')
                    int lineNumber = that.getError().getLine() - 1;
                    int columnNumber = that.getError().getColumn() - 1;
                    VirtualFile file = LocalFileSystem.getInstance().findFileByIoFile(new File(project.getBasePath().toString() + "/" + filePath));
                    new OpenFileDescriptor(project, file, lineNumber, columnNumber).navigate(true);
                }
            };
            errorComponent.addButtonAction(actionListener);

            c.gridx = 0;
            c.gridy = i;
            this.panelErrorsValue.add(errorComponent, c);
        }
    }

    private void initInformationLabel() {
        this.labelInformation = new JTextPane();
        this.labelInformation.setEditable(false);
        this.labelInformation.setCursor(null);
        this.labelInformation.setOpaque(false);
        this.labelInformation.setFocusable(false);
        this.labelInformation.setContentType("text/html");

        String fontFamily = UIManager.getFont("Label.font").getFamily();
        int fontSize = UIManager.getFont("Label.font").getSize();
        String information = "Your local code may be different from the version that failed on the build server!" +
                " Explore the errors in your local code using the <b><i>View</i></b> buttons above." +
                " Use the <b><i>Checkout</i></b> button to change to the code that failed on the build server and explore the errors there." +
                " Uncommitted changes will be stashed automatically.";

        this.labelInformation.setText(
                "<div style='font-family:" + fontFamily + ";font-size:" + fontSize + ";'>" + information + "</div>"
        );
    }

    private void initBackButton() {
        this.buttonBack = new JButton();
        this.buttonBack.setText("Back to Overview");
        this.buttonBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Controller controller = ServiceManager.getService(Controller.class);
                controller.abortBuildFix();
            }
        });
    }

    private void initContinueButton() {
        this.buttonContinue = new JButton();
        this.buttonContinue.setText("Checkout");
        this.buttonContinue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Controller controller = ServiceManager.getService(Controller.class);
                controller.startFixingBrokenBuild();
            }
        });
    }
}
