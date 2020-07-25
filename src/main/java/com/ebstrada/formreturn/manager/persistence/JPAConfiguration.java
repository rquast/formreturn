package com.ebstrada.formreturn.manager.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.derby.jdbc.ClientDataSource;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;
import com.ebstrada.formreturn.manager.util.preferences.persistence.ClientDatabasePreferences;
import com.ebstrada.formreturn.server.preferences.ServerPreferencesManager;

public class JPAConfiguration {

    private Map<String, String> connectionMap = new HashMap<String, String>();

    private String connectionURL;
    private String connectionDriverName;
    private String connectionUserName;
    private String connectionPassword;
    private String synchronizeMappings;

    private EntityManagerFactory entityManagerFactory;

    private boolean syncSchema = false;

    private boolean isServerConfiguration = false;

    public JPAConfiguration(boolean isServerConfiguration) {

        this.isServerConfiguration = isServerConfiguration;

        ClientDatabasePreferences cdp = null;
        if (isServerConfiguration) {
            cdp = ServerPreferencesManager.getClientDatabase();
        } else {
            cdp = PreferencesManager.getClientDatabase();
        }

        String serverIP = cdp.getServerIPAddress();
        if (serverIP.trim().equals("0.0.0.0")) {
            serverIP = "127.0.0.1";
        }

        // initialize with default settings
        setConnectionURL(
            "jdbc:derby://" + serverIP + ":" + cdp.getPortNumber() + "/" + cdp.getDatabaseName());
        // setConnectionDriverName("org.apache.derby.jdbc.ClientDriver");
        setConnectionDriverName("org.apache.commons.dbcp.BasicDataSource");
        setConnectionUserName(cdp.getUsername());
        if (cdp.getPassword() == null || cdp.getPassword().trim().length() <= 0) {
            setConnectionPassword(PreferencesManager.getSystemPassword());
        } else {
            setConnectionPassword(cdp.getPassword());
        }

        setSynchronizeMappings("buildSchema");

        if (isServerConfiguration) {
            setSyncSchema(true);
        } else {
            setSyncSchema(false);
        }

        buildConnectionMap();

    }

    private Connection getConnection()
        throws InstantiationException, IllegalAccessException, ClassNotFoundException,
        SQLException {
        Class.forName(getConnectionDriverName()).newInstance();
        Properties props = new Properties();
        props.put("user", "formreturn");
        props.put("password", getConnectionPassword());
        props.put("securityMechanism", ClientDataSource.STRONG_PASSWORD_SUBSTITUTE_SECURITY + "");
        Connection conn = DriverManager.getConnection(getConnectionURL(), props);
        conn.setAutoCommit(false);
        return conn;
    }

    public void buildConnectionMap() {

        connectionMap = new HashMap<String, String>();
        // connectionMap.put("openjpa.ConnectionURL", getConnectionURL());
        connectionMap.put("openjpa.ConnectionDriverName", getConnectionDriverName());
        // connectionMap.put("openjpa.ConnectionRetainMode", "always");
	/*
	connectionMap
		.put("openjpa.ConnectionUserName", getConnectionUserName());
	connectionMap
		.put("openjpa.ConnectionPassword", getConnectionPassword());
		*/
        connectionMap.put("openjpa.jdbc.Schema", "FORMRETURN");

        connectionMap.put("openjpa.ConnectionProperties",
            "DriverClassName=org.apache.derby.jdbc.ClientDriver,Url=" + getConnectionURL()
                + ",Username=" + getConnectionUserName() + ",Password=" + getConnectionPassword()
                + ",securityMechanism=" + ClientDataSource.STRONG_PASSWORD_SUBSTITUTE_SECURITY);
        connectionMap.put("derby.drda.securityMechanism", "STRONG_PASSWORD_SUBSTITUTE_SECURITY");

        if (isSyncSchema()) {
            connectionMap.put("openjpa.jdbc.SynchronizeMappings", getSynchronizeMappings());
        }

    }

    public boolean isSyncSchema() {
        return syncSchema;
    }

    public void setSyncSchema(boolean syncSchema) {
        this.syncSchema = syncSchema;
    }

    public Map<String, String> getConnectionMap() {
        return connectionMap;
    }

    public void setConnectionMap(Map<String, String> connectionMap) {
        this.connectionMap = connectionMap;
    }

    public String getConnectionURL() {
        return connectionURL;
    }

    public void setConnectionURL(String connectionURL) {
        this.connectionURL = connectionURL;
    }

    public String getConnectionDriverName() {
        return connectionDriverName;
    }

    public void setConnectionDriverName(String connectionDriverName) {
        this.connectionDriverName = connectionDriverName;
    }

    public String getConnectionUserName() {
        return connectionUserName;
    }

    public void setConnectionUserName(String connectionUserName) {
        this.connectionUserName = connectionUserName;
    }

    public String getConnectionPassword() {
        return connectionPassword;
    }

    public void setConnectionPassword(String connectionPassword) {
        this.connectionPassword = connectionPassword;
    }

    public String getSynchronizeMappings() {
        return synchronizeMappings;
    }

    public void setSynchronizeMappings(String synchronizeMappings) {
        this.synchronizeMappings = synchronizeMappings;
    }

    public EntityManager getEntityManager() {

        EntityManager entityManager = null;

        try {
            entityManager = getEntityManagerFactory().createEntityManager();
            if (!isServerConfiguration) {
                Main.getInstance().setDatabaseStatusConnected();
            }
        } catch (org.apache.openjpa.persistence.PersistenceException pe) {
            throw pe;
        } catch (Exception ex) {
            if (!isServerConfiguration) {
                Main.getInstance().setDatabaseStatusDisconnected(ex.getMessage() + "\n" + Localizer
                    .localize("UI", "JPAConfigurationShutdownSuggestionMessage"));
            }
        }

        return entityManager;

    }

    public EntityManagerFactory getEntityManagerFactory() throws Exception {
        if (this.entityManagerFactory == null) {

            boolean versionsDiffer = true;
            if (!isServerConfiguration) {

                // get connection
                Connection conn = null;
                Statement s = null;
                ResultSet rs = null;
                try {
                    conn = getConnection();

                    s = conn.createStatement();
                    s.executeUpdate(
                        "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.authentication.builtin.algorithm', NULL)");
                    s.close();

                    s = conn.createStatement();

                    rs = s.executeQuery(
                        "SELECT PROPERTY_VALUE FROM SYSTEM_PROPERTIES WHERE PROPERTY_NAME = 'database.version'");

                    while (rs.next()) {
                        String databaseVersion = rs.getString(1);
                        if (databaseVersion.equals(Main.VERSION + "")) {
                            versionsDiffer = false;
                        }
                    }
                    rs.close();
                } catch (Exception ex) {
                    throw ex;
                } finally {
                    if (s != null) {
                        try {
                            s.close();
                        } catch (SQLException e) {
                        }
                    }
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException e) {
                        }
                    }
                    if (conn != null) {
                        try {
                            conn.close();
                        } catch (SQLException e) {
                        }
                    }
                }

                if (versionsDiffer) {
                    throw new Exception(
                        Localizer.localize("UI", "JPAConfigurationDatabaseVersionsDifferMessage"));
                }

            }

            entityManagerFactory =
                Persistence.createEntityManagerFactory("frm-persistence", getConnectionMap());

        }
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

}
