package com.ebstrada.formreturn.manager.ui.editor.dialog;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.ebstrada.formreturn.manager.gef.presentation.FigText;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;

public class TextPropertyDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private FigText _figText;

    public TextPropertyDialog(FigText figText) {
        super(Main.getInstance(), true);

        initComponents();

        if (Main.MAC_OS_X) {
            int MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
            editorTextArea.getInputMap()
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_A, MASK), "select-all");
            editorTextArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_C, MASK), "copy");
            editorTextArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_X, MASK), "cut");
            editorTextArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_V, MASK), "paste");
        }

        this.setTitle(Localizer.localize("UI", "TextPropertyDialogTitle"));
        _figText = figText;

        editorTextArea.setText(_figText.getText());
        editorTextArea.updateUI();
        getRootPane().setDefaultButton(okButton);
    }

    private void okButtonActionPerformed(ActionEvent e) {
        _figText.setText(editorTextArea.getText());
        _figText.damage();
        dispose();
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        dispose();
    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                editorTextArea.requestFocusInWindow();
            }
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY
        // //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();
        panel1 = new JPanel();
        scrollPane1 = new JScrollPane();
        editorTextArea = new JTextPane();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        addWindowFocusListener(new WindowAdapter() {
            @Override public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(10, 0, 5, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[] {0, 75, 6, 75, 0};
                ((GridBagLayout) buttonBar.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout) buttonBar.getLayout()).columnWeights =
                    new double[] {1.0, 0.0, 0.0, 0.0, 1.0E-4};
                ((GridBagLayout) buttonBar.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                //---- okButton ----
                okButton.setFont(UIManager.getFont("Button.font"));
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        okButtonActionPerformed(e);
                    }
                });
                okButton.setText(Localizer.localize("UI", "OKButtonText"));
                buttonBar.add(okButton,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

                //---- cancelButton ----
                cancelButton.setFont(UIManager.getFont("Button.font"));
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed(e);
                    }
                });
                cancelButton.setText(Localizer.localize("UI", "CancelButtonText"));
                buttonBar.add(cancelButton,
                    new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);

            //======== panel1 ========
            {
                panel1.setLayout(new BorderLayout());

                //======== scrollPane1 ========
                {

                    //---- editorTextArea ----
                    editorTextArea.setFont(UIManager.getFont("TextArea.font"));
                    scrollPane1.setViewportView(editorTextArea);
                }
                panel1.add(scrollPane1, BorderLayout.CENTER);
            }
            dialogPane.add(panel1, BorderLayout.CENTER);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(440, 335);
        setLocationRelativeTo(null);
        // //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    private JPanel panel1;
    private JScrollPane scrollPane1;
    private JTextPane editorTextArea;
    // JFormDesigner - End of variables declaration //GEN-END:variables

    public void setAlignment(int _justification) {
        StyledDocument doc = editorTextArea.getStyledDocument();
        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        switch (_justification) {
            case FigText.JUSTIFY_RIGHT:
                StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_RIGHT);
                break;
            case FigText.JUSTIFY_LEFT:
                StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_LEFT);
                break;
            case FigText.JUSTIFY_CENTER:
                StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_CENTER);
                break;
            case FigText.JUSTIFY_JUSTIFIED:
                StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_JUSTIFIED);
                break;
        }
        doc.setParagraphAttributes(0, doc.getLength(), attributeSet, false);
    }
}
