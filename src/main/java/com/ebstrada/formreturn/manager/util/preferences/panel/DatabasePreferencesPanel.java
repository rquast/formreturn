package com.ebstrada.formreturn.manager.util.preferences.panel;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.*;
import javax.swing.border.*;

import org.apache.derby.jdbc.ClientDataSource;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;
import com.ebstrada.formreturn.manager.util.preferences.persistence.ApplicationStatePreferences;
import com.ebstrada.formreturn.manager.util.preferences.persistence.ClientDatabasePreferences;

public class DatabasePreferencesPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public DatabasePreferencesPanel() {
        initComponents();
        restoreSettings();
    }

    private void restoreSettings() {

        ApplicationStatePreferences applicationState = PreferencesManager.getApplicationState();

        launchLocalDBonStartupCheckBox.setSelected(applicationState.isLaunchServerOnStartup());
        connectPreferredDBonStartupCheckBox.setSelected(applicationState.isConnectToDBOnStartup());

        ClientDatabasePreferences cdp = PreferencesManager.getClientDatabase();

        if (cdp.getDatabaseName() != null) {
            databaseNameTextField.setText(cdp.getDatabaseName());
        }
        if (cdp.getServerIPAddress() != null) {
            remoteAddressTextField.setText(cdp.getServerIPAddress());
        }
        if (cdp.getUsername() != null) {
            usernameTextField.setText(cdp.getUsername());
        }
        if (cdp.getPassword() != null) {
            passwordField.setText(cdp.getPassword());
        }
        if (cdp.getPortNumber() > 0) {
            portNumberTextField.setText(cdp.getPortNumber() + "");
        }

        localDatabaseList.setListData(PreferencesManager.getDatabaseList());

    }

    private void launchLocalDatabaseNowButtonActionPerformed(ActionEvent e) {
        com.ebstrada.formreturn.server.ServerGUI.startServer();
    }

    private void launchLocalDBonStartupCheckBoxActionPerformed(ActionEvent e) {

        ApplicationStatePreferences applicationState = PreferencesManager.getApplicationState();
        applicationState.setLaunchServerOnStartup(launchLocalDBonStartupCheckBox.isSelected());

        try {
            PreferencesManager.savePreferences(Main.getXstream());
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
        }
    }

    private void connectionActionButtonActionPerformed(ActionEvent e) {

        Connection conn = null;

        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
            Properties props = new Properties();
            props.put("user", usernameTextField.getText());
            props.put("password", new String(passwordField.getPassword()));
            props.put("securityMechanism",
                ClientDataSource.STRONG_PASSWORD_SUBSTITUTE_SECURITY + "");

            int portNumber = Misc.parseIntegerString(portNumberTextField.getText());
            if (portNumber < 1 || portNumber >= 65535) {
                portNumber = 1527;
                portNumberTextField.setText(portNumber + "");
            }

            String jdbcURL =
                "jdbc:derby://" + remoteAddressTextField.getText().trim() + ":" + portNumber + "/"
                    + databaseNameTextField.getText().trim();

            conn = DriverManager.getConnection(jdbcURL, props);

            String message =
                Localizer.localize("Util", "DatabasePreferencesConnectionSuccessMessage");
            String caption =
                Localizer.localize("Util", "DatabasePreferencesConnectionSuccessTitle");
            javax.swing.JOptionPane
                .showConfirmDialog(this, message, caption, javax.swing.JOptionPane.DEFAULT_OPTION,
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
        } catch (InstantiationException ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
        } catch (IllegalAccessException ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
        } catch (ClassNotFoundException ex) {
            String message = Localizer.localize("Util",
                "DatabasePreferencesConnectionErrorInvalidDriverClassNameMessage");
            String caption = Localizer.localize("Util", "DatabasePreferencesConnectionErrorTitle");
            javax.swing.JOptionPane
                .showConfirmDialog(this, message, caption, javax.swing.JOptionPane.DEFAULT_OPTION,
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            String message =
                Localizer.localize("Util", "DatabasePreferencesConnectionErrorInvalidDatabaseName");
            message += ex.getMessage();
            String caption = Localizer.localize("Util", "DatabasePreferencesConnectionErrorTitle");
            javax.swing.JOptionPane
                .showConfirmDialog(this, message, caption, javax.swing.JOptionPane.DEFAULT_OPTION,
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception cse) {

                }
            }
        }
    }

    private void saveConnectionDetailsButtonActionPerformed(ActionEvent e) {

        ClientDatabasePreferences cdp = PreferencesManager.getClientDatabase();

        cdp.setDatabaseName(databaseNameTextField.getText());
        cdp.setServerIPAddress(remoteAddressTextField.getText());
        cdp.setUsername(usernameTextField.getText());
        cdp.setPassword(new String(passwordField.getPassword()));

        ApplicationStatePreferences applicationState = PreferencesManager.getApplicationState();
        applicationState.setConnectToDBOnStartup(connectPreferredDBonStartupCheckBox.isSelected());

        int portNumber = Misc.parseIntegerString(portNumberTextField.getText());
        if (portNumber < 1 || portNumber >= 65535) {
            portNumber = 1527;
            portNumberTextField.setText(portNumber + "");
        }
        cdp.setPortNumber(portNumber);

        try {
            PreferencesManager.savePreferences(Main.getXstream());
            String message =
                Localizer.localize("Util", "DatabasePreferencesConnectionDetailsSavedMessage");
            String caption =
                Localizer.localize("Util", "DatabasePreferencesConnectionDetailsSavedTitle");
            javax.swing.JOptionPane
                .showConfirmDialog(this, message, caption, javax.swing.JOptionPane.DEFAULT_OPTION,
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
        }
    }

    private void setAsActiveDatabaseButtonActionPerformed(ActionEvent e) {
        String databaseName = (String) localDatabaseList.getSelectedValue();
        databaseNameTextField.setText(databaseName);
        remoteAddressTextField.setText("127.0.0.1");
        usernameTextField.setText("formreturn");
        passwordField.setText(PreferencesManager.getSystemPassword());
        portNumberTextField.setText("1527");
    }

    private void setDefaultButtonActionPerformed(ActionEvent e) {
        databaseNameTextField.setText("FRDB");
        remoteAddressTextField.setText("127.0.0.1");
        usernameTextField.setText("formreturn");
        passwordField.setText(PreferencesManager.getSystemPassword());
        portNumberTextField.setText("1527");
    }

    private void refreshLocalDatabaseListButtonActionPerformed(ActionEvent e) {
        localDatabaseList.setListData(PreferencesManager.getDatabaseList());
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel5 = new JPanel();
        serverHeadingLabel = new JLabel();
        panel9 = new JPanel();
        panel10 = new JPanel();
        launchLocalDBonStartupCheckBox = new JCheckBox();
        launchLocalDatabaseNowButton = new JButton();
        panel3 = new JPanel();
        databaseConnectionDetailsHeadingLabel = new JLabel();
        panel7 = new JPanel();
        panel11 = new JPanel();
        databaseNameLabel = new JLabel();
        databaseNameTextField = new JTextField();
        connectPreferredDBonStartupCheckBox = new JCheckBox();
        panel2 = new JPanel();
        usernameLabel = new JLabel();
        usernameTextField = new JTextField();
        passwordLabel = new JLabel();
        passwordField = new JPasswordField();
        addressLabel = new JLabel();
        remoteAddressTextField = new JTextField();
        portNumberLabel = new JLabel();
        portNumberTextField = new JTextField();
        panel6 = new JPanel();
        setDefaultButton = new JButton();
        testConnectionButton = new JButton();
        saveConnectionDetailsButton = new JButton();
        panel4 = new JPanel();
        localDatabasesHeadingLabel = new JLabel();
        panel8 = new JPanel();
        scrollPane1 = new JScrollPane();
        localDatabaseList = new JList();
        panel1 = new JPanel();
        refreshLocalDatabaseListButton = new JButton();
        setAsActiveDatabaseButton = new JButton();

        //======== this ========
        setBorder(new EmptyBorder(10, 15, 10, 15));
        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWidths = new int[] {0, 0};
        ((GridBagLayout) getLayout()).rowHeights = new int[] {35, 35, 35, 35, 0, 35, 35, 0, 30, 0};
        ((GridBagLayout) getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout) getLayout()).rowWeights =
            new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0E-4};

        //======== panel5 ========
        {
            panel5.setOpaque(false);
            panel5.setLayout(new GridBagLayout());
            ((GridBagLayout) panel5.getLayout()).columnWidths = new int[] {0, 0, 0};
            ((GridBagLayout) panel5.getLayout()).rowHeights = new int[] {30, 0};
            ((GridBagLayout) panel5.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
            ((GridBagLayout) panel5.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- serverHeadingLabel ----
            serverHeadingLabel.setFont(UIManager.getFont("Label.font"));
            serverHeadingLabel
                .setText(Localizer.localize("Util", "DatabasePreferencesServerHeadingLabel"));
            panel5.add(serverHeadingLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //======== panel9 ========
            {
                panel9.setOpaque(false);
                panel9.setBorder(new MatteBorder(0, 0, 1, 0, Color.lightGray));
                panel9.setLayout(new BorderLayout());
            }
            panel5.add(panel9,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel5, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

        //======== panel10 ========
        {
            panel10.setOpaque(false);
            panel10.setLayout(new GridBagLayout());
            ((GridBagLayout) panel10.getLayout()).columnWidths = new int[] {0, 0, 0};
            ((GridBagLayout) panel10.getLayout()).rowHeights = new int[] {30, 0};
            ((GridBagLayout) panel10.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0E-4};
            ((GridBagLayout) panel10.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- launchLocalDBonStartupCheckBox ----
            launchLocalDBonStartupCheckBox.setFont(UIManager.getFont("CheckBox.font"));
            launchLocalDBonStartupCheckBox.setOpaque(false);
            launchLocalDBonStartupCheckBox.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    launchLocalDBonStartupCheckBoxActionPerformed(e);
                }
            });
            launchLocalDBonStartupCheckBox
                .setText(Localizer.localize("Util", "DatabasePreferencesLaunchServerCheckBox"));
            panel10.add(launchLocalDBonStartupCheckBox,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- launchLocalDatabaseNowButton ----
            launchLocalDatabaseNowButton.setFont(UIManager.getFont("Button.font"));
            launchLocalDatabaseNowButton.setFocusPainted(false);
            launchLocalDatabaseNowButton.setIcon(new ImageIcon(getClass().getResource(
                "/com/ebstrada/formreturn/manager/ui/icons/formreturn_server_16x16.png")));
            launchLocalDatabaseNowButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    launchLocalDatabaseNowButtonActionPerformed(e);
                }
            });
            launchLocalDatabaseNowButton
                .setText(Localizer.localize("Util", "DatabasePreferencesLaunchServerButtonText"));
            panel10.add(launchLocalDatabaseNowButton,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel10, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

        //======== panel3 ========
        {
            panel3.setOpaque(false);
            panel3.setLayout(new GridBagLayout());
            ((GridBagLayout) panel3.getLayout()).columnWidths = new int[] {0, 0, 0};
            ((GridBagLayout) panel3.getLayout()).rowHeights = new int[] {30, 0};
            ((GridBagLayout) panel3.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
            ((GridBagLayout) panel3.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- databaseConnectionDetailsHeadingLabel ----
            databaseConnectionDetailsHeadingLabel.setFont(UIManager.getFont("Label.font"));
            databaseConnectionDetailsHeadingLabel.setText(Localizer
                .localize("Util", "DatabasePreferencesDatabaseConnectionDetailsHeadingLabel"));
            panel3.add(databaseConnectionDetailsHeadingLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //======== panel7 ========
            {
                panel7.setOpaque(false);
                panel7.setBorder(new MatteBorder(0, 0, 1, 0, Color.lightGray));
                panel7.setLayout(new BorderLayout());
            }
            panel3.add(panel7,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel3, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

        //======== panel11 ========
        {
            panel11.setOpaque(false);
            panel11.setLayout(new GridBagLayout());
            ((GridBagLayout) panel11.getLayout()).columnWidths = new int[] {0, 0, 15, 0, 0};
            ((GridBagLayout) panel11.getLayout()).rowHeights = new int[] {30, 0};
            ((GridBagLayout) panel11.getLayout()).columnWeights =
                new double[] {0.0, 1.0, 0.0, 0.0, 1.0E-4};
            ((GridBagLayout) panel11.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- databaseNameLabel ----
            databaseNameLabel.setFont(UIManager.getFont("Label.font"));
            databaseNameLabel
                .setText(Localizer.localize("Util", "DatabasePreferencesDatabaseNameLabel"));
            panel11.add(databaseNameLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- databaseNameTextField ----
            databaseNameTextField.setFont(UIManager.getFont("TextField.font"));
            panel11.add(databaseNameTextField,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- connectPreferredDBonStartupCheckBox ----
            connectPreferredDBonStartupCheckBox.setFont(UIManager.getFont("CheckBox.font"));
            connectPreferredDBonStartupCheckBox.setOpaque(false);
            connectPreferredDBonStartupCheckBox.setText(
                Localizer.localize("Util", "DatabasePreferencesConnectToDBOnStartCheckBox"));
            panel11.add(connectPreferredDBonStartupCheckBox,
                new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel11, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

        //======== panel2 ========
        {
            panel2.setOpaque(false);
            panel2.setLayout(new GridBagLayout());
            ((GridBagLayout) panel2.getLayout()).columnWidths = new int[] {0, 0, 15, 0, 0, 0};
            ((GridBagLayout) panel2.getLayout()).rowHeights = new int[] {35, 30, 0};
            ((GridBagLayout) panel2.getLayout()).columnWeights =
                new double[] {0.0, 1.0, 0.0, 0.0, 1.0, 1.0E-4};
            ((GridBagLayout) panel2.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

            //---- usernameLabel ----
            usernameLabel.setFont(UIManager.getFont("Label.font"));
            usernameLabel.setText(Localizer.localize("Util", "DatabasePreferencesUsernameLabel"));
            panel2.add(usernameLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

            //---- usernameTextField ----
            usernameTextField.setFont(UIManager.getFont("TextField.font"));
            panel2.add(usernameTextField,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));

            //---- passwordLabel ----
            passwordLabel.setFont(UIManager.getFont("Label.font"));
            passwordLabel.setText(Localizer.localize("Util", "DatabasePreferencesPasswordLabel"));
            panel2.add(passwordLabel,
                new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

            //---- passwordField ----
            passwordField.setFont(UIManager.getFont("TextField.font"));
            panel2.add(passwordField,
                new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- addressLabel ----
            addressLabel.setFont(UIManager.getFont("Label.font"));
            addressLabel.setText(Localizer.localize("Util", "DatabasePreferencesAddressLabel"));
            panel2.add(addressLabel,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));

            //---- remoteAddressTextField ----
            remoteAddressTextField.setFont(UIManager.getFont("TextField.font"));
            panel2.add(remoteAddressTextField,
                new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- portNumberLabel ----
            portNumberLabel.setFont(UIManager.getFont("Label.font"));
            portNumberLabel
                .setText(Localizer.localize("Util", "DatabasePreferencesPortNumberLabel"));
            panel2.add(portNumberLabel,
                new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));

            //---- portNumberTextField ----
            portNumberTextField.setFont(UIManager.getFont("TextField.font"));
            panel2.add(portNumberTextField,
                new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel2, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

        //======== panel6 ========
        {
            panel6.setOpaque(false);
            panel6.setBorder(new CompoundBorder(new MatteBorder(1, 0, 0, 0, Color.lightGray),
                new EmptyBorder(5, 0, 0, 0)));
            panel6.setLayout(new GridBagLayout());
            ((GridBagLayout) panel6.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
            ((GridBagLayout) panel6.getLayout()).rowHeights = new int[] {30, 0};
            ((GridBagLayout) panel6.getLayout()).columnWeights =
                new double[] {0.0, 1.0, 0.0, 0.0, 1.0E-4};
            ((GridBagLayout) panel6.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- setDefaultButton ----
            setDefaultButton.setFont(UIManager.getFont("Button.font"));
            setDefaultButton.setFocusPainted(false);
            setDefaultButton.setIcon(new ImageIcon(getClass().getResource(
                "/com/ebstrada/formreturn/manager/ui/icons/arrow_rotate_anticlockwise.png")));
            setDefaultButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    setDefaultButtonActionPerformed(e);
                }
            });
            setDefaultButton.setText(
                Localizer.localize("Util", "DatabasePreferencesRestoreDefaultsButtonText"));
            panel6.add(setDefaultButton,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- testConnectionButton ----
            testConnectionButton.setFont(UIManager.getFont("Button.font"));
            testConnectionButton.setFocusPainted(false);
            testConnectionButton.setIcon(new ImageIcon(getClass().getResource(
                "/com/ebstrada/formreturn/manager/ui/icons/util/database_connect.png")));
            testConnectionButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    connectionActionButtonActionPerformed(e);
                }
            });
            testConnectionButton
                .setText(Localizer.localize("Util", "DatabasePreferencesTestConnectionButtonText"));
            panel6.add(testConnectionButton,
                new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- saveConnectionDetailsButton ----
            saveConnectionDetailsButton.setFont(UIManager.getFont("Button.font"));
            saveConnectionDetailsButton.setFocusPainted(false);
            saveConnectionDetailsButton.setIcon(new ImageIcon(
                getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/disk.png")));
            saveConnectionDetailsButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    saveConnectionDetailsButtonActionPerformed(e);
                }
            });
            saveConnectionDetailsButton.setText(
                Localizer.localize("Util", "DatabasePreferencesSaveConnectionDetailsButtonText"));
            panel6.add(saveConnectionDetailsButton,
                new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel6, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

        //======== panel4 ========
        {
            panel4.setOpaque(false);
            panel4.setLayout(new GridBagLayout());
            ((GridBagLayout) panel4.getLayout()).columnWidths = new int[] {0, 0, 0};
            ((GridBagLayout) panel4.getLayout()).rowHeights = new int[] {30, 0};
            ((GridBagLayout) panel4.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
            ((GridBagLayout) panel4.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- localDatabasesHeadingLabel ----
            localDatabasesHeadingLabel.setFont(UIManager.getFont("Label.font"));
            localDatabasesHeadingLabel.setText(
                Localizer.localize("Util", "DatabasePreferencesLocalDatabasesHeadingLabel"));
            panel4.add(localDatabasesHeadingLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //======== panel8 ========
            {
                panel8.setOpaque(false);
                panel8.setBorder(new MatteBorder(0, 0, 1, 0, Color.lightGray));
                panel8.setLayout(new BorderLayout());
            }
            panel4.add(panel8,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel4, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

        //======== scrollPane1 ========
        {

            //---- localDatabaseList ----
            localDatabaseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            localDatabaseList.setFont(UIManager.getFont("List.font"));
            scrollPane1.setViewportView(localDatabaseList);
        }
        add(scrollPane1, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

        //======== panel1 ========
        {
            panel1.setOpaque(false);
            panel1.setBorder(null);
            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
            ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {30, 0};
            ((GridBagLayout) panel1.getLayout()).columnWeights =
                new double[] {0.0, 1.0, 0.0, 1.0E-4};
            ((GridBagLayout) panel1.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- refreshLocalDatabaseListButton ----
            refreshLocalDatabaseListButton.setFont(UIManager.getFont("Button.font"));
            refreshLocalDatabaseListButton.setFocusPainted(false);
            refreshLocalDatabaseListButton.setIcon(new ImageIcon(getClass()
                .getResource("/com/ebstrada/formreturn/manager/ui/icons/arrow_refresh.png")));
            refreshLocalDatabaseListButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    refreshLocalDatabaseListButtonActionPerformed(e);
                }
            });
            refreshLocalDatabaseListButton.setText(
                Localizer.localize("Util", "DatabasePreferencesRefreshDatabaseListButtonText"));
            panel1.add(refreshLocalDatabaseListButton,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- setAsActiveDatabaseButton ----
            setAsActiveDatabaseButton.setFont(UIManager.getFont("Button.font"));
            setAsActiveDatabaseButton.setFocusPainted(false);
            setAsActiveDatabaseButton.setIcon(new ImageIcon(
                getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/disk.png")));
            setAsActiveDatabaseButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    setAsActiveDatabaseButtonActionPerformed(e);
                }
            });
            setAsActiveDatabaseButton.setText(
                Localizer.localize("Util", "DatabasePreferencesSetAsActiveDatabaseButtonText"));
            panel1.add(setAsActiveDatabaseButton,
                new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel1, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel5;
    private JLabel serverHeadingLabel;
    private JPanel panel9;
    private JPanel panel10;
    private JCheckBox launchLocalDBonStartupCheckBox;
    private JButton launchLocalDatabaseNowButton;
    private JPanel panel3;
    private JLabel databaseConnectionDetailsHeadingLabel;
    private JPanel panel7;
    private JPanel panel11;
    private JLabel databaseNameLabel;
    private JTextField databaseNameTextField;
    private JCheckBox connectPreferredDBonStartupCheckBox;
    private JPanel panel2;
    private JLabel usernameLabel;
    private JTextField usernameTextField;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private JLabel addressLabel;
    private JTextField remoteAddressTextField;
    private JLabel portNumberLabel;
    private JTextField portNumberTextField;
    private JPanel panel6;
    private JButton setDefaultButton;
    private JButton testConnectionButton;
    private JButton saveConnectionDetailsButton;
    private JPanel panel4;
    private JLabel localDatabasesHeadingLabel;
    private JPanel panel8;
    private JScrollPane scrollPane1;
    private JList localDatabaseList;
    private JPanel panel1;
    private JButton refreshLocalDatabaseListButton;
    private JButton setAsActiveDatabaseButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
