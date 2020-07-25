package com.ebstrada.formreturn.manager.ui.frame;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.wizard.IWizardController;

public class WizardDialog extends JDialog {

    public static final int CANCEL = 0;

    public static final int NEXT = 1;

    public static final int BACK = 2;

    public static final int FINISH = 3;

    private IWizardController controller;

    private static final long serialVersionUID = 1L;

    public WizardDialog() {
        initComponents();
        setIconImage(new ImageIcon(getClass()
            .getResource("/com/ebstrada/formreturn/manager/ui/icons/frmanager_256x256.png"))
            .getImage());
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        try {
            controller.cancel();
            this.dispose();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    private void setActiveButtons(ArrayList<Integer> activeButtons) {

        hideCancelButton();
        hideNextButton();
        hideBackButton();
        hideFinishButton();

        for (Integer activeButton : activeButtons) {
            switch (activeButton) {
                case CANCEL:
                    showCancelButton();
                    this.cancelButton.setEnabled(true);
                    break;
                case NEXT:
                    showNextButton();
                    this.nextButton.setEnabled(true);
                    break;
                case BACK:
                    showBackButton();
                    this.backButton.setEnabled(true);
                    break;
                case FINISH:
                    showFinishButton();
                    this.finishButton.setEnabled(true);
                    break;
            }
        }

        if (this.finishButton.isVisible()) {
            this.getRootPane().setDefaultButton(this.finishButton);
        } else if (this.nextButton.isVisible()) {
            this.getRootPane().setDefaultButton(this.nextButton);
        } else if (this.cancelButton.isVisible()) {
            this.getRootPane().setDefaultButton(this.cancelButton);
        }

    }

    private void backButtonActionPerformed(ActionEvent e) {
        try {
            controller.back();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        updateWizard();
    }

    private void nextButtonActionPerformed(ActionEvent e) {
        try {
            controller.next();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        updateWizard();
    }

    private void updateWizard() {
        JPanel currentPanel = controller.getActivePanel();
        if (currentPanel != null) {
            contentPanel.removeAll();
            contentPanel.add(currentPanel);
            validate();
        }

        ArrayList<Integer> activeButtons = controller.getActiveButtons();

        if (activeButtons != null) {
            setActiveButtons(activeButtons);
        }
    }

    private void thisWindowClosing(WindowEvent e) {
        if (this.cancelButton.isVisible()) {
            try {
                controller.cancel();
                dispose();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (finishButton.isVisible()) {
                    finishButton.requestFocusInWindow();
                } else if (nextButton.isVisible()) {
                    nextButton.requestFocusInWindow();
                } else if (cancelButton.isVisible()) {
                    cancelButton.requestFocusInWindow();
                }
            }
        });
    }

    public IWizardController getController() {
        return controller;
    }

    public void setController(IWizardController controller) {
        this.controller = controller;
        setTitle(controller.getWizardTitle());
        updateWizard();
    }

    private void finishButtonActionPerformed(ActionEvent e) {
        try {
            controller.finish();
            dispose();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    public void hideCancelButton() {
        this.cancelButton.setEnabled(false);
        this.cancelButton.setVisible(false);
    }

    public void showCancelButton() {
        this.cancelButton.setVisible(true);
    }

    public void hideBackButton() {
        this.backButton.setEnabled(false);
        this.backButton.setVisible(false);
    }

    public void showBackButton() {
        this.backButton.setVisible(true);
    }

    public void hideNextButton() {
        this.nextButton.setEnabled(false);
        this.nextButton.setVisible(false);
    }

    public void showNextButton() {
        this.nextButton.setVisible(true);
    }

    public void hideFinishButton() {
        this.finishButton.setEnabled(false);
        this.finishButton.setVisible(false);
    }

    public void showFinishButton() {
        this.finishButton.setVisible(true);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        buttonBar = new JPanel();
        backButton = new JButton();
        nextButton = new JButton();
        cancelButton = new JButton();
        finishButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                thisWindowClosing(e);
            }
        });
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

            //======== contentPanel ========
            {
                contentPanel.setLayout(new BorderLayout());
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 0));

                //---- backButton ----
                backButton.setFont(UIManager.getFont("Button.font"));
                backButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        backButtonActionPerformed(e);
                    }
                });
                backButton.setText(Localizer.localize("UI", "BackButtonText"));
                buttonBar.add(backButton);

                //---- nextButton ----
                nextButton.setFont(UIManager.getFont("Button.font"));
                nextButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        nextButtonActionPerformed(e);
                    }
                });
                nextButton.setText(Localizer.localize("UI", "NextButtonText"));
                buttonBar.add(nextButton);

                //---- cancelButton ----
                cancelButton.setFont(UIManager.getFont("Button.font"));
                cancelButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed(e);
                    }
                });
                cancelButton.setText(Localizer.localize("UI", "CancelButtonText"));
                buttonBar.add(cancelButton);

                //---- finishButton ----
                finishButton.setFont(UIManager.getFont("Button.font"));
                finishButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        finishButtonActionPerformed(e);
                    }
                });
                finishButton.setText(Localizer.localize("UI", "FinishButtonText"));
                buttonBar.add(finishButton);
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(500, 360);
        setLocationRelativeTo(null);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel buttonBar;
    private JButton backButton;
    private JButton nextButton;
    private JButton cancelButton;
    private JButton finishButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}
