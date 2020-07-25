package com.ebstrada.formreturn.manager.ui.panel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.ui.tab.*;

import org.jdesktop.swingx.JXTaskPaneContainer;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.component.JStatusBar;
import com.ebstrada.formreturn.manager.ui.editor.RecognitionPreviewPanel;
import com.ebstrada.formreturn.manager.ui.editor.panel.RecognitionPanelController;

public class DesktopDockingPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private JStatusBar statusBar;

    public static final int PANEL_MINIMIZED = 0;

    public static final int PANEL_RESTORED = 1;

    public static final int PANEL_MAXIMIZED = 2;

    private int navigatorPanelState;
    private int systemConsolePanelState;

    private PropertiesPanelController propertiesPanelController;

    public static final int defaultDividerSize = 3;
    public static final int defaultSystemConsoleDividerLocation = 150;

    private int systemConsoleLastDividerLocation = 0;

    private RecognitionPanelController recognitionPanelController;

    private StandardDesktopToolbarPanel dtp;

    private DesktopTabbedPane desktopTabbedPane;

    private JScrollPane systemConsoleScrollPane;

    public DesktopDockingPanel(DesktopTabbedPane desktopTabbedPane,
        JScrollPane systemConsoleScrollPane) {

        this.desktopTabbedPane = desktopTabbedPane;
        this.systemConsoleScrollPane = systemConsoleScrollPane;

        initComponents();

        statusBar = JStatusBar.getInstance();
        add(statusBar, BorderLayout.SOUTH);

        navigatorTaskPaneContainer.setBackground(new Color(239, 241, 248));

        propertiesPanelController = new PropertiesPanelController(navigatorTaskPaneContainer);

        desktopPane.add((Component) desktopTabbedPane, BorderLayout.CENTER);
        systemConsolePane.add(systemConsoleScrollPane, BorderLayout.CENTER);

    }

    public DesktopTabbedPane getDesktopTabbedPane() {
        return desktopTabbedPane;
    }

    public JScrollPane getSystemConsoleScrollPane() {
        return systemConsoleScrollPane;
    }

    public void setSystemConsoleScrollPane(JScrollPane systemConsoleScrollPane) {
        this.systemConsoleScrollPane = systemConsoleScrollPane;
    }

    public void showDesktopToolbar() {
        if (dtp == null) {
            dtp = new StandardDesktopToolbarPanel();
        }
        add(dtp, BorderLayout.NORTH);
        revalidate();
    }

    public void hideDesktopToolbar() {
        if (dtp != null) {
            remove(dtp);
        }
        revalidate();
    }

    public void hideStatusBar() {
        if (statusBar != null) {
            remove(statusBar);
        }
        revalidate();
    }

    public void showStatusBar() {
        if (statusBar == null) {
            statusBar = JStatusBar.getInstance();
        }
        add(statusBar, BorderLayout.SOUTH);
        revalidate();
    }

    public JStatusBar getStatusBar() {
        return statusBar;
    }

    private void minimizeNavigatorButtonActionPerformed(ActionEvent e) {
        setNavigatorPanelState(PANEL_MINIMIZED);
    }

    private void minimizeSystemConsoleButtonActionPerformed(ActionEvent e) {
        setSystemConsolePanelState(PANEL_MINIMIZED);
    }

    private void thisComponentResized(ComponentEvent e) {
        resetNavigationDividerLocation();
    }

    private void resetNavigationDividerLocation() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                navigatorSplitPane.setDividerLocation(180);
            }
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY
        // //GEN-BEGIN:initComponents
        navigatorSplitPane = new JSplitPane();
        navigatorPane = new JPanel();
        navigatorScrollPane = new JScrollPane();
        navigatorTaskPaneContainer = new JXTaskPaneContainer();
        navigatorToolBar = new JPanel();
        controlsLabel = new JLabel();
        minimizeNavigatorButton = new JButton();
        systemConsoleSplitPane = new JSplitPane();
        desktopPane = new JPanel();
        systemConsolePane = new JPanel();
        systemConsoleToolBar = new JPanel();
        systemConsoleLabel = new JLabel();
        minimizeSystemConsoleButton = new JButton();

        //======== this ========
        setBorder(null);
        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                thisComponentResized(e);
            }
        });
        setLayout(new BorderLayout());

        //======== navigatorSplitPane ========
        {
            navigatorSplitPane.setBorder(null);
            navigatorSplitPane.setDividerSize(3);
            navigatorSplitPane.setDividerLocation(180);

            //======== navigatorPane ========
            {
                navigatorPane.setBorder(new MatteBorder(0, 0, 1, 1, Color.gray));
                navigatorPane.setOpaque(false);
                navigatorPane.setBackground(null);
                navigatorPane.setLayout(new BorderLayout());

                //======== navigatorScrollPane ========
                {
                    navigatorScrollPane.setViewportBorder(null);
                    navigatorScrollPane.setBorder(null);
                    navigatorScrollPane.setViewportView(navigatorTaskPaneContainer);
                }
                navigatorPane.add(navigatorScrollPane, BorderLayout.CENTER);

                //======== navigatorToolBar ========
                {
                    navigatorToolBar.setBorder(
                        new CompoundBorder(new MatteBorder(0, 0, 1, 0, Color.gray),
                            new EmptyBorder(4, 4, 4, 4)));
                    navigatorToolBar.setForeground(null);
                    navigatorToolBar.setLayout(new GridBagLayout());
                    ((GridBagLayout) navigatorToolBar.getLayout()).columnWidths =
                        new int[] {0, 0, 0};
                    ((GridBagLayout) navigatorToolBar.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout) navigatorToolBar.getLayout()).columnWeights =
                        new double[] {1.0, 0.0, 1.0E-4};
                    ((GridBagLayout) navigatorToolBar.getLayout()).rowWeights =
                        new double[] {0.0, 1.0E-4};

                    //---- controlsLabel ----
                    controlsLabel.setIcon(new ImageIcon(getClass()
                        .getResource("/com/ebstrada/formreturn/manager/ui/icons/wrench.png")));
                    controlsLabel.setFont(UIManager.getFont("Label.font"));
                    controlsLabel.setText(Localizer.localize("UI", "DesktopDockingControlsTitle"));
                    navigatorToolBar.add(controlsLabel,
                        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 0, 4), 0, 0));

                    //---- minimizeNavigatorButton ----
                    minimizeNavigatorButton.setBorder(null);
                    minimizeNavigatorButton.setBorderPainted(false);
                    minimizeNavigatorButton.setIcon(new ImageIcon(getClass().getResource(
                        "/com/ebstrada/formreturn/manager/ui/icons/minimize_black.png")));
                    minimizeNavigatorButton.setIconTextGap(2);
                    minimizeNavigatorButton.setMargin(new Insets(8, 8, 8, 4));
                    minimizeNavigatorButton.setContentAreaFilled(false);
                    minimizeNavigatorButton.setBackground(null);
                    minimizeNavigatorButton.setMaximumSize(new Dimension(16, 16));
                    minimizeNavigatorButton.setMinimumSize(new Dimension(16, 16));
                    minimizeNavigatorButton.setPreferredSize(new Dimension(16, 16));
                    minimizeNavigatorButton.setFocusPainted(false);
                    minimizeNavigatorButton.addActionListener(new ActionListener() {
                        @Override public void actionPerformed(ActionEvent e) {
                            minimizeNavigatorButtonActionPerformed(e);
                        }
                    });
                    navigatorToolBar.add(minimizeNavigatorButton,
                        new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                }
                navigatorPane.add(navigatorToolBar, BorderLayout.NORTH);
            }
            navigatorSplitPane.setLeftComponent(navigatorPane);

            //======== systemConsoleSplitPane ========
            {
                systemConsoleSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
                systemConsoleSplitPane.setBorder(null);
                systemConsoleSplitPane.setDividerSize(3);
                systemConsoleSplitPane.setResizeWeight(0.6);

                //======== desktopPane ========
                {
                    desktopPane.setBorder(null);
                    desktopPane.setOpaque(false);
                    desktopPane.setLayout(new BorderLayout());
                }
                systemConsoleSplitPane.setTopComponent(desktopPane);

                //======== systemConsolePane ========
                {
                    systemConsolePane.setBorder(new MatteBorder(1, 0, 1, 1, Color.gray));
                    systemConsolePane.setOpaque(false);
                    systemConsolePane.setLayout(new BorderLayout());

                    //======== systemConsoleToolBar ========
                    {
                        systemConsoleToolBar.setBorder(
                            new CompoundBorder(new MatteBorder(0, 0, 1, 0, Color.gray),
                                new EmptyBorder(4, 4, 4, 4)));
                        systemConsoleToolBar.setForeground(null);
                        systemConsoleToolBar.setLayout(new GridBagLayout());
                        ((GridBagLayout) systemConsoleToolBar.getLayout()).columnWidths =
                            new int[] {0, 0, 0};
                        ((GridBagLayout) systemConsoleToolBar.getLayout()).rowHeights =
                            new int[] {0, 0};
                        ((GridBagLayout) systemConsoleToolBar.getLayout()).columnWeights =
                            new double[] {1.0, 0.0, 1.0E-4};
                        ((GridBagLayout) systemConsoleToolBar.getLayout()).rowWeights =
                            new double[] {0.0, 1.0E-4};

                        //---- systemConsoleLabel ----
                        systemConsoleLabel.setFont(UIManager.getFont("Label.font"));
                        systemConsoleLabel.setIcon(new ImageIcon(getClass()
                            .getResource("/com/ebstrada/formreturn/manager/ui/icons/monitor.png")));
                        systemConsoleLabel
                            .setText(Localizer.localize("UI", "DesktopDockingSystemConsoleTitle"));
                        systemConsoleToolBar.add(systemConsoleLabel,
                            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 4), 0, 0));

                        //---- minimizeSystemConsoleButton ----
                        minimizeSystemConsoleButton.setBorder(null);
                        minimizeSystemConsoleButton.setBorderPainted(false);
                        minimizeSystemConsoleButton.setBackground(null);
                        minimizeSystemConsoleButton.setIcon(new ImageIcon(getClass().getResource(
                            "/com/ebstrada/formreturn/manager/ui/icons/minimize_black.png")));
                        minimizeSystemConsoleButton.setIconTextGap(2);
                        minimizeSystemConsoleButton.setMargin(new Insets(8, 8, 8, 4));
                        minimizeSystemConsoleButton.setContentAreaFilled(false);
                        minimizeSystemConsoleButton.setMaximumSize(new Dimension(16, 16));
                        minimizeSystemConsoleButton.setMinimumSize(new Dimension(16, 16));
                        minimizeSystemConsoleButton.setPreferredSize(new Dimension(16, 16));
                        minimizeSystemConsoleButton.setFocusPainted(false);
                        minimizeSystemConsoleButton.addActionListener(new ActionListener() {
                            @Override public void actionPerformed(ActionEvent e) {
                                minimizeSystemConsoleButtonActionPerformed(e);
                            }
                        });
                        systemConsoleToolBar.add(minimizeSystemConsoleButton,
                            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                    }
                    systemConsolePane.add(systemConsoleToolBar, BorderLayout.NORTH);
                }
                systemConsoleSplitPane.setBottomComponent(systemConsolePane);
            }
            navigatorSplitPane.setRightComponent(systemConsoleSplitPane);
        }
        add(navigatorSplitPane, BorderLayout.CENTER);
        // //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    private JSplitPane navigatorSplitPane;
    private JPanel navigatorPane;
    private JScrollPane navigatorScrollPane;
    private JXTaskPaneContainer navigatorTaskPaneContainer;
    private JPanel navigatorToolBar;
    private JLabel controlsLabel;
    private JButton minimizeNavigatorButton;
    private JSplitPane systemConsoleSplitPane;
    private JPanel desktopPane;
    private JPanel systemConsolePane;
    private JPanel systemConsoleToolBar;
    private JLabel systemConsoleLabel;
    private JButton minimizeSystemConsoleButton;
    // JFormDesigner - End of variables declaration //GEN-END:variables

    public int getNavigatorPanelState() {
        return navigatorPanelState;
    }

    public void setNavigatorPanelState(final int _navigatorPanelState) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                if (_navigatorPanelState == PANEL_MINIMIZED) {

                    desktopTabbedPane.setBorder(new MatteBorder(0, 0, 1, 0, Color.gray));

                    navigatorPane.setEnabled(false);
                    navigatorPane.setVisible(false);
                    navigatorSplitPane.setDividerSize(0);
                    navigatorSplitPane.setDividerLocation(0);
                    JStatusBar statusBar = Main.getInstance().getStatusBar();
                    statusBar.showControlsButton();

                } else if (_navigatorPanelState == PANEL_MAXIMIZED) {

                    desktopTabbedPane.setBorder(new MatteBorder(0, 0, 1, 0, Color.gray));

                    if (navigatorPanelState == PANEL_MAXIMIZED) {
                        setNavigatorPanelState(PANEL_RESTORED);
                        return;
                    } else {
                        setSystemConsolePanelState(PANEL_MINIMIZED);
                        navigatorPane.setEnabled(true);
                        navigatorPane.setVisible(true);
                        navigatorSplitPane.setDividerSize(0);
                        navigatorSplitPane.setDividerLocation(navigatorSplitPane.getWidth());
                    }
                } else if (_navigatorPanelState == PANEL_RESTORED) {

                    if (Main.WINDOWS) {
                        desktopTabbedPane.setBorder(new MatteBorder(0, 0, 1, 0, Color.gray));
                    } else {
                        desktopTabbedPane.setBorder(new MatteBorder(0, 1, 1, 0, Color.gray));
                    }

                    navigatorPane.setEnabled(true);
                    navigatorPane.setVisible(true);
                    navigatorSplitPane.setDividerSize(defaultDividerSize);
                    resetNavigationDividerLocation();

                    statusBar.hideControlsButton();

                }

                navigatorPanelState = _navigatorPanelState;
            }
        });

    }

    public int getSystemConsolePanelState() {
        return systemConsolePanelState;
    }

    public void setSystemConsolePanelState(final int _systemConsolePanelState) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                if (_systemConsolePanelState == PANEL_MINIMIZED) {
                    systemConsolePane.setEnabled(false);
                    systemConsolePane.setVisible(false);
                    systemConsoleLastDividerLocation = systemConsoleSplitPane.getDividerLocation();
                    systemConsoleSplitPane.setDividerSize(0);
                    systemConsoleSplitPane.setDividerLocation(systemConsoleSplitPane.getHeight());
                    JStatusBar statusBar = Main.getInstance().getStatusBar();
                    statusBar.showSystemConsoleButton();
                } else if (_systemConsolePanelState == PANEL_MAXIMIZED) {

                    if (systemConsolePanelState == PANEL_MAXIMIZED) {
                        setSystemConsolePanelState(PANEL_RESTORED);
                        return;
                    } else {
                        systemConsolePane.setEnabled(true);
                        systemConsolePane.setVisible(true);
                        systemConsoleLastDividerLocation =
                            systemConsoleSplitPane.getDividerLocation();
                        systemConsoleSplitPane.setDividerSize(0);
                        systemConsoleSplitPane.setDividerLocation(0);
                    }
                } else if (_systemConsolePanelState == PANEL_RESTORED) {

                    systemConsolePane.setEnabled(true);
                    systemConsolePane.setVisible(true);
                    systemConsoleSplitPane.setDividerSize(defaultDividerSize);
                    if (systemConsoleLastDividerLocation <= 0) {
                        systemConsoleLastDividerLocation = (systemConsoleSplitPane.getHeight()
                            - defaultSystemConsoleDividerLocation);
                    }
                    systemConsoleSplitPane.setDividerLocation(systemConsoleLastDividerLocation);
                }

                systemConsolePanelState = _systemConsolePanelState;


            }
        });

    }

    public PropertiesPanelController getPropertiesPanelController() {
        return propertiesPanelController;
    }

    public void setPropertiesPanelController(PropertiesPanelController propertiesPanelController) {
        this.propertiesPanelController = propertiesPanelController;
    }

    public RecognitionPanelController getRecognitionPanelController(
        RecognitionPreviewPanel recognitionPreviewPanel) {
        recognitionPanelController =
            new RecognitionPanelController(navigatorTaskPaneContainer, recognitionPreviewPanel);
        return recognitionPanelController;
    }

    public void setRecognitionPanelController(
        RecognitionPanelController recognitionPanelController) {
        this.recognitionPanelController = recognitionPanelController;
    }


}
