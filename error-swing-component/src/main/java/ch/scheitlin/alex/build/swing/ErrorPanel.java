package ch.scheitlin.alex.build.swing;

import ch.scheitlin.alex.build.model.Error;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import javax.swing.*;

public class ErrorPanel extends JPanel {
    // data
    private Error error;
    private String actionButton1Text;
    private String actionButton2Text;

    // components
    private JLabel labelPath;
    private JLabel labelShowMore;
    private JTextPane labelMessage;
    private JButton buttonAction1;
    private JButton buttonAction2;

    // appearance settings
    private boolean showMore = false;

    // appearance constants
    private final String SHOW_MORE_TEXT = "&#x25BC"; // ▼
    private final String SHOW_LESS_TEXT = "&#x25B2";  // ▲
    private final String SHOW_MORE_TEXT_ENABLED_COLOR = "1E90FF";
    private final String SHOW_MORE_DISABLED_COLOR = "C0C0C0";

    public ErrorPanel(Error error, String actionButton1Text, String actionButton2Text) {
        // set data variables
        this.error = error;
        this.actionButton1Text = actionButton1Text;
        this.actionButton2Text = actionButton2Text;

        // get error information
        String errorPath = this.error.getPath();
        String errorFile = this.error.getFile();
        int errorLine = this.error.getLine();
        int errorColumn = this.error.getColumn();
        String errorMessage = this.error.getMessage();

        // set layout for the ErrorPanel
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // initialize label showing the path and file name where the error occurred
        this.labelPath = initPathLabel(errorPath, errorFile, errorLine, errorColumn);
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.insets = new Insets(0, 0, 0, 0);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        this.add(this.labelPath, c);

        // initialize label to show more or less of the error message
        this.labelShowMore = initShowMoreLabel();
        c.anchor = GridBagConstraints.FIRST_LINE_END;
        c.insets = new Insets(0, 5, 0, 0);
        c.fill = GridBagConstraints.NONE;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.0;
        this.add(this.labelShowMore, c);

        // initialize label showing the error message
        this.labelMessage = initMessageLabel(errorMessage);
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.insets = new Insets(0, 0, 0, 0);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0;
        this.add(this.labelMessage, c);

        // initialize button 1 to start action 1
        this.buttonAction1 = initActionButton(this.actionButton1Text);
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.insets = new Insets(0, 5, 0, 0);
        c.fill = GridBagConstraints.NONE;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 0.0;
        this.add(this.buttonAction1, c);

        if (actionButton2Text != null) {
            // initialize button 2 to start action 2
            this.buttonAction2 = initActionButton(this.actionButton2Text);
            c.anchor = GridBagConstraints.FIRST_LINE_START;
            c.insets = new Insets(0, 5, 0, 0);
            c.fill = GridBagConstraints.NONE;
            c.gridheight = 1;
            c.gridwidth = 1;
            c.gridx = 2;
            c.gridy = 1;
            c.weightx = 0.0;
            this.add(this.buttonAction2, c);
        }

        // resize behaviour
        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (showMore) {
                    resizeMessageLabel();
                } else {
                    setMessageLabelText();
                }
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                String labelText = showMore ? SHOW_LESS_TEXT : SHOW_MORE_TEXT;
                String labelColor = SHOW_MORE_TEXT_ENABLED_COLOR;//isMessageComplete() ? SHOW_MORE_TEXT_ENABLED_COLOR : SHOW_MORE_DISABLED_COLOR;
                String labelContent = "<html><span style='color: " + labelColor + ";'>" + labelText + "</span></html>";
                labelShowMore.setText(labelContent);
            }
        });
    }

    public Error getError() {
        return this.error;
    }

    private JLabel initPathLabel(String path, String file, int line, int column) {
        JLabel label = new JLabel();

        StringBuilder textBuilder = new StringBuilder();

        // ignore path if it is null
        if (path != null) {
            textBuilder.append(path + "/");
        }
        textBuilder.append(file);
        textBuilder.append(" - [" + line + ":" + column + "]");

        label.setText(textBuilder.toString());

        return label;
    }

    private JLabel initShowMoreLabel() {
        JLabel label = new JLabel();

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                toggleShowMore();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                labelShowMore.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                labelShowMore.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        return label;
    }

    private JTextPane initMessageLabel(String message) {
        JTextPane label = new JTextPane();
        label.setEditable(false);
        label.setCursor(null);
        label.setOpaque(false);

        label.setText(message);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                resizeMessageLabel();
            }
        });

        return label;
    }

    private JButton initActionButton(String text) {
        JButton button = new JButton();
        button.setText(text);

        return button;
    }

    private void toggleShowMore() {
        // label show more settings
        showMore = !showMore;

        String labelText = showMore ? this.SHOW_LESS_TEXT : this.SHOW_MORE_TEXT;
        //String labelColor = isMessageComplete() ? this.SHOW_MORE_TEXT_ENABLED_COLOR : this.SHOW_MORE_DISABLED_COLOR;
        String labelContent = "<html><span style='color: " + this.SHOW_MORE_TEXT_ENABLED_COLOR + ";'>" + labelText + "</span></html>";

        labelShowMore.setText(labelContent);

        // label error message settings
        resizeMessageLabel();
    }

    private void resizeMessageLabel() {
        // set size
        int width;
        int height;

        if (!this.showMore) {
            width = this.labelMessage.getWidth();
            height = 22;
        } else {
            this.labelMessage.setText(this.error.getMessage());
            width = this.labelMessage.getWidth();
            height = this.labelMessage.getPreferredSize().height;
        }

        Dimension newSize = new Dimension(width, height);
        this.labelMessage.setMinimumSize(newSize);
        this.labelMessage.setMaximumSize(newSize);
    }

    private void setMessageLabelText() {
        // set three dots at the end if label to small
        String labelText = error.getMessage();
        Font labelFont = labelMessage.getFont();
        int labelWidth = labelMessage.getWidth();

        if (isMessageComplete()) {
            int start = 8;
            for (int i = start; i < labelText.toCharArray().length; i++) {
                String currentText = labelText.substring(0, i);
                int currentTextWidth = getTextWidth(currentText, labelFont);
                if (currentTextWidth > labelWidth) {
                    String text = labelText.substring(0, i - start) + " ...";
                    this.labelMessage.setText(text);
                    break;
                }
            }
        } else {
            this.labelMessage.setText(labelText);
        }
    }

    private boolean isMessageComplete() {
        String text = error.getMessage();
        Font font = labelMessage.getFont();
        int textWidth = getTextWidth(text, font);
        int labelWidth = labelMessage.getWidth();

        return textWidth > labelWidth;
    }

    private int getTextWidth(String text, Font font) {
        AffineTransform affinetransform = new AffineTransform();
        FontRenderContext frc = new FontRenderContext(affinetransform, true, true);
        return (int) (font.getStringBounds(text, frc).getWidth());
    }

    public void addButton1Action(ActionListener actionListener) {
        this.buttonAction1.addActionListener(actionListener);
    }

    public void addButton2Action(ActionListener actionListener) {
        this.buttonAction2.addActionListener(actionListener);
    }
}
