package com.ebstrada.formreturn.manager.ui.popup;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;

@SuppressWarnings("serial") public class DesktopMenu extends JPopupMenu {

    public DesktopMenu(Main frame) {
        initComponents();
    }

    private void newFileItemActionPerformed(ActionEvent e) {
        Main.getInstance().createNewForm(e);
    }

    private void quitItemActionPerformed(ActionEvent e) {
        Main.getInstance().closeApplication();
    }

    private void openItemActionPerformed(ActionEvent e) {
        Main.getInstance().open();
    }

    private void preferencesItemActionPerformed(ActionEvent e) {
        Main.getInstance().preferences();
    }

    private void newSegmentItemActionPerformed(ActionEvent e) {
        Main.getInstance().createNewSegment(e);
    }

    private void sourceDataManagerActionPerformed(ActionEvent e) {
        Main.getInstance().sourceDataManagerActionPerformed(e);
    }

    private void capturedDataManagerItemActionPerformed(ActionEvent e) {
        Main.getInstance().capturedDataManagerItemActionPerformed(e);
    }

    private void processingQueueMenuItemActionPerformed(ActionEvent e) {
        Main.getInstance().processingQueueManagerActionPerformed(e);
    }

    private void uploadImageItemActionPerformed(ActionEvent e) {
        Main.getInstance().uploadImageItemActionPerformed(e);
    }

    private void uploadImageFolderItemActionPerformed(ActionEvent e) {
        Main.getInstance().uploadImageFolderItemActionPerformed(e);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY
        // //GEN-BEGIN:initComponents
        newFormItem = new JMenuItem();
        newSegmentItem = new JMenuItem();
        sourceDataManagerItem = new JMenuItem();
        processingQueueMenuItem = new JMenuItem();
        capturedDataManagerItem = new JMenuItem();
        openItem = new JMenuItem();
        openRecentMenu = Main.getInstance().popupRecentFileMenu;
        uploadImageItem = new JMenuItem();
        uploadImageFolderItem = new JMenuItem();
        preferencesItem = new JMenuItem();
        quitItem = new JMenuItem();

        //======== this ========
        setFont(UIManager.getFont("PopupMenu.font"));

        //---- newFormItem ----
        newFormItem.setIcon(new ImageIcon(getClass()
            .getResource("/com/ebstrada/formreturn/manager/ui/icons/page_white_add.png")));
        newFormItem.setFont(UIManager.getFont("MenuItem.font"));
        newFormItem.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                newFileItemActionPerformed(e);
            }
        });
        newFormItem.setText(Localizer.localize("UI", "NewFormMenuItem"));
        add(newFormItem);

        //---- newSegmentItem ----
        newSegmentItem.setIcon(new ImageIcon(getClass()
            .getResource("/com/ebstrada/formreturn/manager/ui/icons/page_white_width.png")));
        newSegmentItem.setFont(UIManager.getFont("MenuItem.font"));
        newSegmentItem.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                newSegmentItemActionPerformed(e);
            }
        });
        newSegmentItem.setText(Localizer.localize("UI", "NewSegmentMenuItem"));
        add(newSegmentItem);
        addSeparator();

        //---- sourceDataManagerItem ----
        sourceDataManagerItem.setIcon(new ImageIcon(
            getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/database.png")));
        sourceDataManagerItem.setFont(UIManager.getFont("MenuItem.font"));
        sourceDataManagerItem.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                sourceDataManagerActionPerformed(e);
            }
        });
        sourceDataManagerItem.setText(Localizer.localize("UI", "SourceDataMenuItem"));
        add(sourceDataManagerItem);
        addSeparator();

        //---- processingQueueMenuItem ----
        processingQueueMenuItem.setIcon(new ImageIcon(
            getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/clock.png")));
        processingQueueMenuItem.setFont(UIManager.getFont("MenuItem.font"));
        processingQueueMenuItem.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                processingQueueMenuItemActionPerformed(e);
            }
        });
        processingQueueMenuItem.setText(Localizer.localize("UI", "ProcessingQueueMenuItem"));
        add(processingQueueMenuItem);
        addSeparator();

        //---- capturedDataManagerItem ----
        capturedDataManagerItem.setIcon(new ImageIcon(
            getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/chart_bar.png")));
        capturedDataManagerItem.setFont(UIManager.getFont("MenuItem.font"));
        capturedDataManagerItem.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                capturedDataManagerItemActionPerformed(e);
            }
        });
        capturedDataManagerItem.setText(Localizer.localize("UI", "CapturedDataManagerMenuItem"));
        add(capturedDataManagerItem);
        addSeparator();

        //---- openItem ----
        openItem.setIcon(new ImageIcon(getClass()
            .getResource("/com/ebstrada/formreturn/manager/ui/icons/folder_page_white.png")));
        openItem.setFont(UIManager.getFont("MenuItem.font"));
        openItem.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                openItemActionPerformed(e);
            }
        });
        openItem.setText(Localizer.localize("UI", "OpenMenuItem"));
        add(openItem);

        //======== openRecentMenu ========
        {
            openRecentMenu.setIcon(new ImageIcon(
                getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/folder_go.png")));
            openRecentMenu.setFont(UIManager.getFont("Menu.font"));
            openRecentMenu.setText(Localizer.localize("UI", "OpenRecentSubMenu"));
        }
        add(openRecentMenu);
        addSeparator();

        //---- uploadImageItem ----
        uploadImageItem.setIcon(new ImageIcon(
            getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/images.png")));
        uploadImageItem.setFont(UIManager.getFont("MenuItem.font"));
        uploadImageItem.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                uploadImageItemActionPerformed(e);
            }
        });
        uploadImageItem.setText(Localizer.localize("UI", "UploadImageMenuItem"));
        add(uploadImageItem);

        //---- uploadImageFolderItem ----
        uploadImageFolderItem.setIcon(new ImageIcon(
            getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/folder_image.png")));
        uploadImageFolderItem.setFont(UIManager.getFont("MenuItem.font"));
        uploadImageFolderItem.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                uploadImageFolderItemActionPerformed(e);
            }
        });
        uploadImageFolderItem.setText(Localizer.localize("UI", "UploadImageFolderMenuItem"));
        add(uploadImageFolderItem);
        addSeparator();

        //---- preferencesItem ----
        preferencesItem.setIcon(new ImageIcon(
            getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/cog.png")));
        preferencesItem.setFont(UIManager.getFont("MenuItem.font"));
        preferencesItem.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                preferencesItemActionPerformed(e);
            }
        });
        preferencesItem.setText(Localizer.localize("UI", "PreferencesMenuItem"));
        add(preferencesItem);
        addSeparator();

        //---- quitItem ----
        quitItem.setIcon(new ImageIcon(getClass()
            .getResource("/com/ebstrada/formreturn/manager/ui/icons/mainmenu/door_in.png")));
        quitItem.setFont(UIManager.getFont("MenuItem.font"));
        quitItem.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                quitItemActionPerformed(e);
            }
        });
        quitItem.setText(Localizer.localize("UI", "QuitMenuItem"));
        add(quitItem);
        // //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    private JMenuItem newFormItem;
    private JMenuItem newSegmentItem;
    private JMenuItem sourceDataManagerItem;
    private JMenuItem processingQueueMenuItem;
    private JMenuItem capturedDataManagerItem;
    private JMenuItem openItem;
    private JMenu openRecentMenu;
    private JMenuItem uploadImageItem;
    private JMenuItem uploadImageFolderItem;
    private JMenuItem preferencesItem;
    private JMenuItem quitItem;
    // JFormDesigner - End of variables declaration //GEN-END:variables
}
