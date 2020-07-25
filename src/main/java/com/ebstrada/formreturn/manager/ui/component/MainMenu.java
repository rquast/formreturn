package com.ebstrada.formreturn.manager.ui.component;

import java.awt.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.*;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.MenuBarUI;

import com.ebstrada.formreturn.manager.gef.base.AdjustGridAction;
import com.ebstrada.formreturn.manager.gef.base.AlignAction;
import com.ebstrada.formreturn.manager.gef.base.CopyAction;
import com.ebstrada.formreturn.manager.gef.base.CutAction;
import com.ebstrada.formreturn.manager.gef.base.DeleteFromModelAction;
import com.ebstrada.formreturn.manager.gef.base.DistributeAction;
import com.ebstrada.formreturn.manager.gef.base.DuplicateAction;
import com.ebstrada.formreturn.manager.gef.base.PasteAction;
import com.ebstrada.formreturn.manager.gef.base.ReorderAction;
import com.ebstrada.formreturn.manager.gef.base.SelectAllAction;
import com.ebstrada.formreturn.manager.gef.base.SelectInvertAction;
import com.ebstrada.formreturn.manager.gef.base.SelectNextAction;
import com.ebstrada.formreturn.manager.gef.base.ShowGridAction;
import com.ebstrada.formreturn.manager.gef.base.SnapToGridAction;
import com.ebstrada.formreturn.manager.gef.undo.RedoAction;
import com.ebstrada.formreturn.manager.gef.undo.UndoAction;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;
import com.ebstrada.formreturn.manager.util.preferences.persistence.ApplicationStatePreferences;

public class MainMenu {

    public MainMenu() {
        initComponents();
        disableGridMenuItems();
    }

    public JMenu mainRecentFileMenu = new JMenu();

    public void removeOSXMenuItems() {
        fileMenu.remove(preferencesItem);
        fileMenu.remove(quitItem);
        helpMenu.remove(aboutItem);
        int fileMenuItemCount = fileMenu.getItemCount();
        fileMenu.remove(fileMenuItemCount - 1);
    }

    private void createNewForm(ActionEvent e) {
        Main.getInstance().createNewForm(e);
    }

    private void createNewSegment(ActionEvent e) {
        Main.getInstance().createNewSegment(e);
    }

    private void sourceDataManagerActionPerformed(ActionEvent e) {
        Main.getInstance().sourceDataManagerActionPerformed(e);
    }

    private void openItemActionPerformed(ActionEvent e) {
        Main.getInstance().openItemActionPerformed(e);
    }

    private void saveItemActionPerformed(ActionEvent e) {
        Main.getInstance().saveItemActionPerformed(e);
    }

    private void saveAsItemActionPerformed(ActionEvent e) {
        Main.getInstance().saveAsItemActionPerformed(e);
    }

    private void preferencesItemActionPerformed(ActionEvent e) {
        Main.getInstance().preferencesItemActionPerformed(e);
    }

    private void quitItemActionPerformed(ActionEvent e) {
        Main.getInstance().quitItemActionPerformed(e);
    }

    private void aboutItemActionPerformed(ActionEvent e) {
        Main.getInstance().aboutItemActionPerformed(e);
    }

    public JMenuBar getMenuBar() {
        return menuBar;
    }

    public JMenu getOpenRecentSubMenu() {
        return openRecentSubMenu;
    }

    public JCheckBoxMenuItem getStatusBarCheckBoxMenuItem() {
        return statusBarCheckBoxMenuItem;
    }

    public JCheckBoxMenuItem getQuickLauncherCheckBoxMenuItem() {
        return quickLauncherCheckBoxMenuItem;
    }

    private void capturedDataManagerItemActionPerformed(ActionEvent e) {
        Main.getInstance().capturedDataManagerItemActionPerformed(e);
    }

    private void quickLauncherCheckBoxMenuItemActionPerformed(ActionEvent e) {

        ApplicationStatePreferences applicationState = PreferencesManager.getApplicationState();

        if (quickLauncherCheckBoxMenuItem.isSelected()) {
            applicationState.setQuickLauncherEnabled(true);
            Main.getInstance().showQuickLauncher();
        } else {
            applicationState.setQuickLauncherEnabled(false);
            Main.getInstance().removeQuickLauncher();
        }
        try {
            PreferencesManager.savePreferences(Main.getXstream());
        } catch (IOException e1) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
        }
    }

    private void statusBarCheckBoxMenuItemActionPerformed(ActionEvent e) {

        ApplicationStatePreferences applicationState = PreferencesManager.getApplicationState();

        if (statusBarCheckBoxMenuItem.isSelected()) {
            applicationState.setStatusBarEnabled(true);
            Main.getInstance().showStatusBar();
        } else {
            applicationState.setStatusBarEnabled(false);
            Main.getInstance().removeStatusBar();
        }
        try {
            PreferencesManager.savePreferences(Main.getXstream());
        } catch (IOException e1) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
        }
    }

    private void processingQueueMenuItemActionPerformed(ActionEvent e) {
        Main.getInstance().processingQueueManagerActionPerformed(e);
    }

    private void snapToGridCheckBoxMenuItemActionPerformed(ActionEvent e) {
        SnapToGridAction stga = new SnapToGridAction();
        stga.actionPerformed(e);
    }

    public void setSnapToGridCheckBoxMenuItemSelected(boolean b) {
        snapToGridCheckBoxMenuItem.setSelected(b);
    }

    private void showGridCheckBoxMenuItemActionPerformed(ActionEvent e) {
        ShowGridAction sga = new ShowGridAction();
        sga.actionPerformed(e);
    }

    public void setShowGridCheckBoxMenuItemSelected(boolean b) {
        showGridCheckBoxMenuItem.setSelected(b);
    }

    private void adjustGridSizeMenuItemActionPerformed(ActionEvent e) {
        AdjustGridAction aga = new AdjustGridAction();
        aga.actionPerformed(e);
    }

    private void closeItemActionPerformed(ActionEvent e) {
        Main.getInstance().closeSelectedWindow();
    }

    private void closeAllItemActionPerformed(ActionEvent e) {
        Main.getInstance().closeAllWindows();
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

        if (Main.MAC_OS_X) {
            Class<?> aquaClazz;
            try {
                aquaClazz = Class.forName("com.apple.laf.AquaMenuBarUI");
                MenuBarUI aquaMenuBarUI = (MenuBarUI) aquaClazz.newInstance();
                menuBar = new JMenuBar();
                menuBar.setUI(aquaMenuBarUI);
            } catch (ClassNotFoundException e) {
                Misc.printStackTrace(e);
                menuBar = new JMenuBar();
            } catch (InstantiationException e) {
                Misc.printStackTrace(e);
                menuBar = new JMenuBar();
            } catch (IllegalAccessException e) {
                Misc.printStackTrace(e);
                menuBar = new JMenuBar();
            }
        } else {
            menuBar = new JMenuBar();
        }

        fileMenu = new JMenu();
        newFormItem = new JMenuItem();
        newSegmentItem = new JMenuItem();
        sourceDataManagerItem = new JMenuItem();
        processingQueueMenuItem = new JMenuItem();
        capturedDataManagerItem = new JMenuItem();
        openItem = new JMenuItem();
        openRecentSubMenu = new JMenu();
        uploadImageItem = new JMenuItem();
        uploadImageFolderItem = new JMenuItem();
        closeItem = new JMenuItem();
        closeAllItem = new JMenuItem();
        saveItem = new JMenuItem();
        saveAsItem = new JMenuItem();
        preferencesItem = new JMenuItem();
        quitItem = new JMenuItem();
        editMenu = new JMenu();
        undoItem = new JMenuItem();
        redoItem = new JMenuItem();
        selectAllItem = new JMenuItem();
        selectInvertItem = new JMenuItem();
        selectNextItem = new JMenuItem();
        cutItem = new JMenuItem();
        copyItem = new JMenuItem();
        pasteItem = new JMenuItem();
        snapToGridCheckBoxMenuItem = new JCheckBoxMenuItem();
        showGridCheckBoxMenuItem = new JCheckBoxMenuItem();
        adjustGridSizeMenuItem = new JMenuItem();
        duplicateItem = new JMenuItem();
        deleteItem = new JMenuItem();
        objectMenu = new JMenu();
        forwardItem = new JMenuItem();
        backwardItem = new JMenuItem();
        toFrontItem = new JMenuItem();
        toBackItem = new JMenuItem();
        alignTopsItem = new JMenuItem();
        alignBottomsItem = new JMenuItem();
        alignLeftItem = new JMenuItem();
        alignRightItem = new JMenuItem();
        alignHorizontalCentersItem = new JMenuItem();
        alignVerticalCentersItem = new JMenuItem();
        distributeHorizontalSpacingItem = new JMenuItem();
        distributeVerticalSpacingItem = new JMenuItem();
        viewMenu = new JMenu();
        toolbarsMenu = new JMenu();
        quickLauncherCheckBoxMenuItem = new JCheckBoxMenuItem();
        statusBarCheckBoxMenuItem = new JCheckBoxMenuItem();
        helpMenu = new JMenu();
        aboutItem = new JMenuItem();

        //======== menuBar ========
        {
            menuBar.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, Color.gray),
                new EmptyBorder(0, 0, 1, 0)));
            menuBar.setFont(UIManager.getFont("MenuBar.font"));

            //======== fileMenu ========
            {
                fileMenu.setIcon(null);
                fileMenu.setSelectedIcon(null);
                fileMenu.setFont(UIManager.getFont("Menu.font"));
                fileMenu.setText(Localizer.localize("UI", "FileMenu"));

                //---- newFormItem ----
                newFormItem.setIcon(new ImageIcon(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/ui/icons/page_white_add.png")));
                newFormItem.setFont(UIManager.getFont("MenuItem.font"));
                newFormItem.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        createNewForm(e);
                    }
                });
                newFormItem.setText(Localizer.localize("UI", "NewFormMenuItem"));
                newFormItem.setAccelerator(Localizer.getShortcut("UI", "NewFormShortcut"));
                fileMenu.add(newFormItem);

                //---- newSegmentItem ----
                newSegmentItem.setIcon(new ImageIcon(getClass().getResource(
                    "/com/ebstrada/formreturn/manager/ui/icons/page_white_width.png")));
                newSegmentItem.setFont(UIManager.getFont("MenuItem.font"));
                newSegmentItem.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        createNewSegment(e);
                    }
                });
                newSegmentItem.setText(Localizer.localize("UI", "NewSegmentMenuItem"));
                newSegmentItem.setAccelerator(Localizer.getShortcut("UI", "NewSegmentShortcut"));
                fileMenu.add(newSegmentItem);
                fileMenu.addSeparator();

                //---- sourceDataManagerItem ----
                sourceDataManagerItem.setIcon(new ImageIcon(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/ui/icons/database.png")));
                sourceDataManagerItem.setFont(UIManager.getFont("MenuItem.font"));
                sourceDataManagerItem.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        sourceDataManagerActionPerformed(e);
                    }
                });
                sourceDataManagerItem.setText(Localizer.localize("UI", "SourceDataMenuItem"));
                sourceDataManagerItem
                    .setAccelerator(Localizer.getShortcut("UI", "SourceDataShortcut"));
                fileMenu.add(sourceDataManagerItem);
                fileMenu.addSeparator();

                //---- processingQueueMenuItem ----
                processingQueueMenuItem.setIcon(new ImageIcon(
                    getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/clock.png")));
                processingQueueMenuItem.setFont(UIManager.getFont("MenuItem.font"));
                processingQueueMenuItem.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        processingQueueMenuItemActionPerformed(e);
                    }
                });
                processingQueueMenuItem
                    .setText(Localizer.localize("UI", "ProcessingQueueMenuItem"));
                processingQueueMenuItem
                    .setAccelerator(Localizer.getShortcut("UI", "ProcessingQueueShortcut"));
                fileMenu.add(processingQueueMenuItem);
                fileMenu.addSeparator();

                //---- capturedDataManagerItem ----
                capturedDataManagerItem.setIcon(new ImageIcon(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/ui/icons/chart_bar.png")));
                capturedDataManagerItem.setFont(UIManager.getFont("MenuItem.font"));
                capturedDataManagerItem.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        capturedDataManagerItemActionPerformed(e);
                    }
                });
                capturedDataManagerItem
                    .setText(Localizer.localize("UI", "CapturedDataManagerMenuItem"));
                capturedDataManagerItem
                    .setAccelerator(Localizer.getShortcut("UI", "CapturedDataManagerShortcut"));
                fileMenu.add(capturedDataManagerItem);
                fileMenu.addSeparator();

                //---- openItem ----
                openItem.setIcon(new ImageIcon(getClass().getResource(
                    "/com/ebstrada/formreturn/manager/ui/icons/folder_page_white.png")));
                openItem.setFont(UIManager.getFont("MenuItem.font"));
                openItem.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        openItemActionPerformed(e);
                    }
                });
                openItem.setText(Localizer.localize("UI", "OpenMenuItem"));
                openItem.setAccelerator(Localizer.getShortcut("UI", "OpenShortcut"));
                fileMenu.add(openItem);

                //======== openRecentSubMenu ========
                {
                    openRecentSubMenu.setIcon(new ImageIcon(getClass()
                        .getResource("/com/ebstrada/formreturn/manager/ui/icons/folder_go.png")));
                    openRecentSubMenu.setFont(UIManager.getFont("Menu.font"));
                    openRecentSubMenu.setText(Localizer.localize("UI", "OpenRecentSubMenu"));
                }
                fileMenu.add(openRecentSubMenu);
                fileMenu.addSeparator();

                //---- uploadImageItem ----
                uploadImageItem.setIcon(new ImageIcon(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/ui/icons/images.png")));
                uploadImageItem.setFont(UIManager.getFont("MenuItem.font"));
                uploadImageItem.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        uploadImageItemActionPerformed(e);
                    }
                });
                uploadImageItem.setText(Localizer.localize("UI", "UploadImageMenuItem"));
                uploadImageItem.setAccelerator(Localizer.getShortcut("UI", "UploadImageShortcut"));
                fileMenu.add(uploadImageItem);

                //---- uploadImageFolderItem ----
                uploadImageFolderItem.setIcon(new ImageIcon(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/ui/icons/folder_image.png")));
                uploadImageFolderItem.setFont(UIManager.getFont("MenuItem.font"));
                uploadImageFolderItem.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        uploadImageFolderItemActionPerformed(e);
                    }
                });
                uploadImageFolderItem
                    .setText(Localizer.localize("UI", "UploadImageFolderMenuItem"));
                uploadImageFolderItem
                    .setAccelerator(Localizer.getShortcut("UI", "UploadImageFolderShortcut"));
                fileMenu.add(uploadImageFolderItem);
                fileMenu.addSeparator();

                //---- closeItem ----
                closeItem.setIcon(new ImageIcon(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/ui/icons/eclipse/close.gif")));
                closeItem.setFont(UIManager.getFont("MenuItem.font"));
                closeItem.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        closeItemActionPerformed(e);
                    }
                });
                closeItem.setText(Localizer.localize("UI", "CloseMenuItem"));
                closeItem.setAccelerator(Localizer.getShortcut("UI", "CloseShortcut"));
                fileMenu.add(closeItem);

                //---- closeAllItem ----
                closeAllItem.setIcon(new ImageIcon(getClass().getResource(
                    "/com/ebstrada/formreturn/manager/ui/icons/eclipse/closeall.gif")));
                closeAllItem.setFont(UIManager.getFont("MenuItem.font"));
                closeAllItem.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        closeAllItemActionPerformed(e);
                    }
                });
                closeAllItem.setText(Localizer.localize("UI", "CloseAllMenuItem"));
                closeAllItem.setAccelerator(Localizer.getShortcut("UI", "CloseAllShortcut"));
                fileMenu.add(closeAllItem);
                fileMenu.addSeparator();

                //---- saveItem ----
                saveItem.setIcon(new ImageIcon(
                    getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/disk.png")));
                saveItem.setFont(UIManager.getFont("MenuItem.font"));
                saveItem.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        saveItemActionPerformed(e);
                    }
                });
                saveItem.setText(Localizer.localize("UI", "SaveMenuItem"));
                saveItem.setAccelerator(Localizer.getShortcut("UI", "SaveShortcut"));
                fileMenu.add(saveItem);

                //---- saveAsItem ----
                saveAsItem.setIcon(new ImageIcon(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/ui/icons/disk_multiple.png")));
                saveAsItem.setFont(UIManager.getFont("MenuItem.font"));
                saveAsItem.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        saveAsItemActionPerformed(e);
                    }
                });
                saveAsItem.setText(Localizer.localize("UI", "SaveAsMenuItem"));
                saveAsItem.setAccelerator(Localizer.getShortcut("UI", "SaveAsShortcut"));
                fileMenu.add(saveAsItem);
                fileMenu.addSeparator();

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
                preferencesItem.setAccelerator(Localizer.getShortcut("UI", "PreferencesShortcut"));
                fileMenu.add(preferencesItem);

                //---- quitItem ----
                quitItem.setIcon(new ImageIcon(getClass().getResource(
                    "/com/ebstrada/formreturn/manager/ui/icons/mainmenu/door_in.png")));
                quitItem.setFont(UIManager.getFont("MenuItem.font"));
                quitItem.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        quitItemActionPerformed(e);
                    }
                });
                quitItem.setText(Localizer.localize("UI", "QuitMenuItem"));
                quitItem.setAccelerator(Localizer.getShortcut("UI", "QuitShortcut"));
                fileMenu.add(quitItem);
            }
            menuBar.add(fileMenu);

            //======== editMenu ========
            {
                editMenu.setIcon(null);
                editMenu.setSelectedIcon(null);
                editMenu.setFont(UIManager.getFont("Menu.font"));
                editMenu.setText(Localizer.localize("UI", "EditMenu"));

                //---- undoItem ----
                undoItem = editMenu.add(new UndoAction("Undo"));
                undoItem.setIcon(new ImageIcon(getClass().getResource(
                    "/com/ebstrada/formreturn/manager/ui/icons/mainmenu/arrow_undo.png")));
                undoItem.setFont(UIManager.getFont("MenuItem.font"));
                undoItem.setText(Localizer.localize("UI", "UndoMenuItem"));
                undoItem.setAccelerator(Localizer.getShortcut("UI", "UndoShortcut"));
                editMenu.add(undoItem);

                //---- redoItem ----
                redoItem = editMenu.add(new RedoAction("Redo"));
                redoItem.setIcon(new ImageIcon(getClass().getResource(
                    "/com/ebstrada/formreturn/manager/ui/icons/mainmenu/arrow_redo.png")));
                redoItem.setFont(UIManager.getFont("MenuItem.font"));
                redoItem.setText(Localizer.localize("UI", "RedoMenuItem"));
                redoItem.setAccelerator(Localizer.getShortcut("UI", "RedoShortcut"));
                editMenu.add(redoItem);
                editMenu.addSeparator();

                //---- selectAllItem ----
                selectAllItem = editMenu.add(new SelectAllAction());
                selectAllItem.setFont(UIManager.getFont("MenuItem.font"));
                selectAllItem.setText(Localizer.localize("UI", "SelectAllMenuItem"));
                selectAllItem.setAccelerator(Localizer.getShortcut("UI", "SelectAllShortcut"));
                editMenu.add(selectAllItem);

                //---- selectInvertItem ----
                selectInvertItem = editMenu.add(new SelectInvertAction());
                selectInvertItem.setFont(UIManager.getFont("MenuItem.font"));
                selectInvertItem.setText(Localizer.localize("UI", "SelectInvertMenuItem"));
                selectInvertItem
                    .setAccelerator(Localizer.getShortcut("UI", "SelectInvertShortcut"));
                editMenu.add(selectInvertItem);

                //---- selectNextItem ----
                selectNextItem = editMenu.add(new SelectNextAction());
                selectNextItem.setFont(UIManager.getFont("MenuItem.font"));
                selectNextItem.setText(Localizer.localize("UI", "SelectNextMenuItem"));
                selectNextItem.setAccelerator(Localizer.getShortcut("UI", "SelectNextShortcut"));
                editMenu.add(selectNextItem);
                editMenu.addSeparator();

                //---- cutItem ----
                cutItem = editMenu.add(new CutAction("Cut"));
                cutItem.setIcon(new ImageIcon(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/ui/icons/mainmenu/cut.png")));
                cutItem.setFont(UIManager.getFont("MenuItem.font"));
                cutItem.setText(Localizer.localize("UI", "CutMenuItem"));
                cutItem.setAccelerator(Localizer.getShortcut("UI", "CutShortcut"));
                editMenu.add(cutItem);

                //---- copyItem ----
                copyItem = editMenu.add(new CopyAction());
                copyItem.setIcon(new ImageIcon(getClass().getResource(
                    "/com/ebstrada/formreturn/manager/ui/icons/mainmenu/page_white_copy.png")));
                copyItem.setFont(UIManager.getFont("MenuItem.font"));
                copyItem.setText(Localizer.localize("UI", "CopyMenuItem"));
                copyItem.setAccelerator(Localizer.getShortcut("UI", "CopyShortcut"));
                editMenu.add(copyItem);

                //---- pasteItem ----
                pasteItem = editMenu.add(new PasteAction("Paste"));
                pasteItem.setIcon(new ImageIcon(getClass().getResource(
                    "/com/ebstrada/formreturn/manager/ui/icons/mainmenu/paste_plain.png")));
                pasteItem.setFont(UIManager.getFont("MenuItem.font"));
                pasteItem.setText(Localizer.localize("UI", "PasteMenuItem"));
                pasteItem.setAccelerator(Localizer.getShortcut("UI", "PasteShortcut"));
                editMenu.add(pasteItem);
                editMenu.addSeparator();

                //---- snapToGridCheckBoxMenuItem ----
                snapToGridCheckBoxMenuItem.setFont(UIManager.getFont("MenuItem.font"));
                snapToGridCheckBoxMenuItem.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        snapToGridCheckBoxMenuItemActionPerformed(e);
                    }
                });
                snapToGridCheckBoxMenuItem
                    .setText(Localizer.localize("UI", "SnapToGridCheckBoxMenuItem"));
                snapToGridCheckBoxMenuItem
                    .setAccelerator(Localizer.getShortcut("UI", "SnapToGridShortcut"));
                editMenu.add(snapToGridCheckBoxMenuItem);

                //---- showGridCheckBoxMenuItem ----
                showGridCheckBoxMenuItem.setText("Show Grid");
                showGridCheckBoxMenuItem.setFont(UIManager.getFont("MenuItem.font"));
                showGridCheckBoxMenuItem.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        showGridCheckBoxMenuItemActionPerformed(e);
                    }
                });
                showGridCheckBoxMenuItem
                    .setText(Localizer.localize("UI", "ShowGridCheckBoxMenuItem"));
                showGridCheckBoxMenuItem
                    .setAccelerator(Localizer.getShortcut("UI", "ShowGridShortcut"));
                editMenu.add(showGridCheckBoxMenuItem);

                //---- adjustGridSizeMenuItem ----
                adjustGridSizeMenuItem.setFont(UIManager.getFont("MenuItem.font"));
                adjustGridSizeMenuItem.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        adjustGridSizeMenuItemActionPerformed(e);
                    }
                });
                adjustGridSizeMenuItem.setText(Localizer.localize("UI", "AdjustGridSizeMenuItem"));
                adjustGridSizeMenuItem
                    .setAccelerator(Localizer.getShortcut("UI", "AdjustGridSizeShortcut"));
                editMenu.add(adjustGridSizeMenuItem);
                editMenu.addSeparator();

                //---- duplicateItem ----
                duplicateItem = editMenu.add(new DuplicateAction("Duplicate"));
                duplicateItem.setIcon(new ImageIcon(getClass().getResource(
                    "/com/ebstrada/formreturn/manager/ui/icons/mainmenu/duplicate.png")));
                duplicateItem.setFont(UIManager.getFont("MenuItem.font"));
                duplicateItem.setText(Localizer.localize("UI", "DuplicateMenuItem"));
                duplicateItem.setAccelerator(Localizer.getShortcut("UI", "DuplicateShortcut"));
                editMenu.add(duplicateItem);
                editMenu.addSeparator();

                //---- deleteItem ----
                deleteItem = editMenu.add(new DeleteFromModelAction("Delete"));
                deleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0));
                deleteItem.setIcon(new ImageIcon(
                    getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/cross.png")));
                deleteItem.setFont(UIManager.getFont("MenuItem.font"));
                deleteItem.setText(Localizer.localize("UI", "DeleteMenuItem"));
                editMenu.add(deleteItem);
            }
            menuBar.add(editMenu);

            //======== objectMenu ========
            {
                objectMenu.setFont(UIManager.getFont("Menu.font"));
                objectMenu.setText(Localizer.localize("UI", "ObjectMenu"));

                //---- forwardItem ----
                forwardItem =
                    objectMenu.add(new ReorderAction("Forward", ReorderAction.BRING_FORWARD));
                forwardItem.setIcon(new ImageIcon(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/gef/Images/Forward.png")));
                forwardItem.setFont(UIManager.getFont("MenuItem.font"));
                forwardItem.setText(Localizer.localize("UI", "ForwardMenuItem"));
                objectMenu.add(forwardItem);

                //---- backwardItem ----
                backwardItem =
                    objectMenu.add(new ReorderAction("Backward", ReorderAction.SEND_BACKWARD));
                backwardItem.setIcon(new ImageIcon(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/gef/Images/Backward.png")));
                backwardItem.setFont(UIManager.getFont("MenuItem.font"));
                backwardItem.setText(Localizer.localize("UI", "BackwardMenuItem"));
                objectMenu.add(backwardItem);

                //---- toFrontItem ----
                toFrontItem =
                    objectMenu.add(new ReorderAction("To Front", ReorderAction.BRING_TO_FRONT));
                toFrontItem.setIcon(new ImageIcon(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/gef/Images/ToFront.png")));
                toFrontItem.setFont(UIManager.getFont("MenuItem.font"));
                toFrontItem.setText(Localizer.localize("UI", "ToFrontMenuItem"));
                objectMenu.add(toFrontItem);

                //---- toBackItem ----
                toBackItem =
                    objectMenu.add(new ReorderAction("To Back", ReorderAction.SEND_TO_BACK));
                toBackItem.setIcon(new ImageIcon(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/gef/Images/ToBack.png")));
                toBackItem.setFont(UIManager.getFont("MenuItem.font"));
                toBackItem.setText(Localizer.localize("UI", "ToBackMenuItem"));
                objectMenu.add(toBackItem);
                objectMenu.addSeparator();

                //---- alignTopsItem ----
                alignTopsItem = objectMenu.add(new AlignAction(AlignAction.ALIGN_TOPS));
                alignTopsItem.setIcon(new ImageIcon(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/gef/Images/AlignTops.png")));
                alignTopsItem.setFont(UIManager.getFont("MenuItem.font"));
                alignTopsItem.setText(Localizer.localize("UI", "AlignTopsMenuItem"));
                objectMenu.add(alignTopsItem);

                //---- alignBottomsItem ----
                alignBottomsItem = objectMenu.add(new AlignAction(AlignAction.ALIGN_BOTTOMS));
                alignBottomsItem.setIcon(new ImageIcon(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/gef/Images/AlignBottoms.png")));
                alignBottomsItem.setFont(UIManager.getFont("MenuItem.font"));
                alignBottomsItem.setText(Localizer.localize("UI", "AlignBottomsMenuItem"));
                objectMenu.add(alignBottomsItem);

                //---- alignLeftItem ----
                alignLeftItem = objectMenu.add(new AlignAction(AlignAction.ALIGN_LEFTS));
                alignLeftItem.setIcon(new ImageIcon(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/gef/Images/AlignLefts.png")));
                alignLeftItem.setFont(UIManager.getFont("MenuItem.font"));
                alignLeftItem.setText(Localizer.localize("UI", "AlignLeftMenuItem"));
                objectMenu.add(alignLeftItem);

                //---- alignRightItem ----
                alignRightItem = objectMenu.add(new AlignAction(AlignAction.ALIGN_RIGHTS));
                alignRightItem.setIcon(new ImageIcon(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/gef/Images/AlignRights.png")));
                alignRightItem.setFont(UIManager.getFont("MenuItem.font"));
                alignRightItem.setText(Localizer.localize("UI", "AlignRightMenuItem"));
                objectMenu.add(alignRightItem);
                objectMenu.addSeparator();

                //---- alignHorizontalCentersItem ----
                alignHorizontalCentersItem =
                    objectMenu.add(new AlignAction(AlignAction.ALIGN_H_CENTERS));
                alignHorizontalCentersItem.setIcon(new ImageIcon(getClass().getResource(
                    "/com/ebstrada/formreturn/manager/gef/Images/AlignHorizontalCenters.png")));
                alignHorizontalCentersItem.setFont(UIManager.getFont("MenuItem.font"));
                alignHorizontalCentersItem
                    .setText(Localizer.localize("UI", "AlignHorizontalCentersMenuItem"));
                alignHorizontalCentersItem
                    .setAccelerator(Localizer.getShortcut("UI", "AlignHorizontalCentersShortcut"));
                objectMenu.add(alignHorizontalCentersItem);

                //---- alignVerticalCentersItem ----
                alignVerticalCentersItem =
                    objectMenu.add(new AlignAction(AlignAction.ALIGN_V_CENTERS));
                alignVerticalCentersItem.setIcon(new ImageIcon(getClass().getResource(
                    "/com/ebstrada/formreturn/manager/gef/Images/AlignVerticalCenters.png")));
                alignVerticalCentersItem.setFont(UIManager.getFont("MenuItem.font"));
                alignVerticalCentersItem
                    .setText(Localizer.localize("UI", "AlignVerticalCentersMenuItem"));
                alignVerticalCentersItem
                    .setAccelerator(Localizer.getShortcut("UI", "AlignVerticalCentersShortcut"));
                objectMenu.add(alignVerticalCentersItem);
                objectMenu.addSeparator();

                //---- distributeHorizontalSpacingItem ----
                distributeHorizontalSpacingItem =
                    objectMenu.add(new DistributeAction(DistributeAction.H_SPACING));
                distributeHorizontalSpacingItem.setIcon(new ImageIcon(getClass().getResource(
                    "/com/ebstrada/formreturn/manager/gef/Images/DistributeHorizontalSpacing.png")));
                distributeHorizontalSpacingItem.setFont(UIManager.getFont("MenuItem.font"));
                distributeHorizontalSpacingItem
                    .setText(Localizer.localize("UI", "DistributeHorizontalSpacingMenuItem"));
                distributeHorizontalSpacingItem.setAccelerator(
                    Localizer.getShortcut("UI", "DistributeHorizontalSpacingShortcut"));
                objectMenu.add(distributeHorizontalSpacingItem);

                //---- distributeVerticalSpacingItem ----
                distributeVerticalSpacingItem =
                    objectMenu.add(new DistributeAction(DistributeAction.V_SPACING));
                distributeVerticalSpacingItem.setIcon(new ImageIcon(getClass().getResource(
                    "/com/ebstrada/formreturn/manager/gef/Images/DistributeVerticalSpacing.png")));
                distributeVerticalSpacingItem.setFont(UIManager.getFont("MenuItem.font"));
                distributeVerticalSpacingItem
                    .setText(Localizer.localize("UI", "DistributeVerticalSpacingItemMenuItem"));
                distributeVerticalSpacingItem.setAccelerator(
                    Localizer.getShortcut("UI", "DistributeVerticalSpacingItemShortcut"));
                objectMenu.add(distributeVerticalSpacingItem);
            }
            menuBar.add(objectMenu);

            //======== viewMenu ========
            {
                viewMenu.setIcon(null);
                viewMenu.setSelectedIcon(null);
                viewMenu.setFont(UIManager.getFont("Menu.font"));
                viewMenu.setText(Localizer.localize("UI", "ViewMenu"));

                //======== toolbarsMenu ========
                {
                    toolbarsMenu.setFont(UIManager.getFont("Menu.font"));
                    toolbarsMenu.setText(Localizer.localize("UI", "ToolbarsSubMenu"));

                    //---- quickLauncherCheckBoxMenuItem ----
                    quickLauncherCheckBoxMenuItem.setFont(UIManager.getFont("MenuItem.font"));
                    quickLauncherCheckBoxMenuItem.addActionListener(new ActionListener() {
                        @Override public void actionPerformed(ActionEvent e) {
                            quickLauncherCheckBoxMenuItemActionPerformed(e);
                        }
                    });
                    quickLauncherCheckBoxMenuItem
                        .setText(Localizer.localize("UI", "QuickLauncherCheckBoxMenuItem"));
                    toolbarsMenu.add(quickLauncherCheckBoxMenuItem);
                }
                viewMenu.add(toolbarsMenu);

                //---- statusBarCheckBoxMenuItem ----
                statusBarCheckBoxMenuItem.setFont(UIManager.getFont("MenuItem.font"));
                statusBarCheckBoxMenuItem.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        statusBarCheckBoxMenuItemActionPerformed(e);
                    }
                });
                statusBarCheckBoxMenuItem
                    .setText(Localizer.localize("UI", "StatusBarCheckBoxMenuItem"));
                viewMenu.add(statusBarCheckBoxMenuItem);
            }
            menuBar.add(viewMenu);

            //======== helpMenu ========
            {
                helpMenu.setIcon(null);
                helpMenu.setSelectedIcon(null);
                helpMenu.setFont(UIManager.getFont("Menu.font"));
                helpMenu.setText(Localizer.localize("UI", "HelpMenu"));

                //---- aboutItem ----
                aboutItem.setIcon(new ImageIcon(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/ui/icons/frmanager_16x16.png")));
                aboutItem.setFont(UIManager.getFont("MenuItem.font"));
                aboutItem.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        aboutItemActionPerformed(e);
                    }
                });
                aboutItem.setText(Localizer.localize("UI", "AboutMenuItem"));
                helpMenu.add(aboutItem);
            }
            menuBar.add(helpMenu);
        }
        // //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem newFormItem;
    private JMenuItem newSegmentItem;
    private JMenuItem sourceDataManagerItem;
    private JMenuItem processingQueueMenuItem;
    private JMenuItem capturedDataManagerItem;
    private JMenuItem openItem;
    private JMenu openRecentSubMenu;
    private JMenuItem uploadImageItem;
    private JMenuItem uploadImageFolderItem;
    private JMenuItem closeItem;
    private JMenuItem closeAllItem;
    private JMenuItem saveItem;
    private JMenuItem saveAsItem;
    private JMenuItem preferencesItem;
    private JMenuItem quitItem;
    private JMenu editMenu;
    private JMenuItem undoItem;
    private JMenuItem redoItem;
    private JMenuItem selectAllItem;
    private JMenuItem selectInvertItem;
    private JMenuItem selectNextItem;
    private JMenuItem cutItem;
    private JMenuItem copyItem;
    private JMenuItem pasteItem;
    private JCheckBoxMenuItem snapToGridCheckBoxMenuItem;
    private JCheckBoxMenuItem showGridCheckBoxMenuItem;
    private JMenuItem adjustGridSizeMenuItem;
    private JMenuItem duplicateItem;
    private JMenuItem deleteItem;
    private JMenu objectMenu;
    private JMenuItem forwardItem;
    private JMenuItem backwardItem;
    private JMenuItem toFrontItem;
    private JMenuItem toBackItem;
    private JMenuItem alignTopsItem;
    private JMenuItem alignBottomsItem;
    private JMenuItem alignLeftItem;
    private JMenuItem alignRightItem;
    private JMenuItem alignHorizontalCentersItem;
    private JMenuItem alignVerticalCentersItem;
    private JMenuItem distributeHorizontalSpacingItem;
    private JMenuItem distributeVerticalSpacingItem;
    private JMenu viewMenu;
    private JMenu toolbarsMenu;
    private JCheckBoxMenuItem quickLauncherCheckBoxMenuItem;
    private JCheckBoxMenuItem statusBarCheckBoxMenuItem;
    private JMenu helpMenu;
    private JMenuItem aboutItem;
    // JFormDesigner - End of variables declaration //GEN-END:variables

    public void enableGridMenuItems() {
        this.adjustGridSizeMenuItem.setEnabled(true);
        this.showGridCheckBoxMenuItem.setEnabled(true);
        this.snapToGridCheckBoxMenuItem.setEnabled(true);
    }

    public void disableGridMenuItems() {
        this.adjustGridSizeMenuItem.setEnabled(false);
        this.showGridCheckBoxMenuItem.setEnabled(false);
        this.showGridCheckBoxMenuItem.setSelected(false);
        this.snapToGridCheckBoxMenuItem.setEnabled(false);
        this.snapToGridCheckBoxMenuItem.setSelected(false);
    }

}
