package com.ebstrada.formreturn.server.derby;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.derby.jdbc.ClientDataSource;
import org.apache.log4j.Logger;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.server.Main;
import com.ebstrada.formreturn.server.ServerGUI;
import com.ebstrada.formreturn.server.preferences.ServerPreferencesManager;

public class DatabaseInstance {

    private String embeddedDriver = "org.apache.derby.jdbc.EmbeddedDriver";

    private String databaseName = "";

    private String systemPassword = ServerPreferencesManager.getSystemPassword();

    private static final Logger logger = Logger.getLogger(DatabaseInstance.class);

    public DatabaseInstance(String databaseName) {
        setDatabaseName(databaseName);
        Connection conn = null;
        try {
            conn = getConnection();
            setDatabaseVersion(conn, Main.VERSION);
            setDatabaseTriggers(conn);
            setIncomingImageProcedure(conn);
        } catch (SQLException e) {
            logger.warn(e.getLocalizedMessage(), e);
        } catch (InstantiationException e) {
            logger.warn(e.getLocalizedMessage(), e);
        } catch (IllegalAccessException e) {
            logger.warn(e.getLocalizedMessage(), e);
        } catch (ClassNotFoundException e) {
            logger.warn(e.getLocalizedMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    if (!(conn.isClosed())) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    logger.warn(e.getLocalizedMessage(), e);
                }
            }
        }
    }

    private void setDatabaseTriggers(Connection conn) throws SQLException {

        // Set FORM table delete triggers

        Statement s = null;
        ResultSet res = null;

        try {

            s = conn.createStatement();

            String sql = "SELECT TRIGGERNAME FROM SYS.SYSTRIGGERS";

            Vector<String> triggerNames = new Vector<String>();
            triggerNames.add("FORM_PAGE_DELETE");
            triggerNames.add("PROCESSED_IMAGE_DELETE");
            triggerNames.add("SEGMENT_DELETE");
            triggerNames.add("FRAGMENT_OMR_DELETE");
            triggerNames.add("FRAGMENT_IMAGE_ZONE_DELETE");
            triggerNames.add("FRAGMENT_BARCODE_DELETE");
            triggerNames.add("FRAGMENT_OCR_DELETE");
            triggerNames.add("CHECK_BOX_DELETE");

            res = s.executeQuery(sql);

            int foundNames = 0;
            while (res.next()) {
                String triggerName = res.getString("TRIGGERNAME");

                if (triggerNames.contains(triggerName)) {
                    foundNames++;
                }

            }

            res.close();
            s.close();

            if (foundNames != triggerNames.size()) {

                // Remove any existing triggers

                for (String triggerName : triggerNames) {
                    s = conn.createStatement();
                    sql = "DROP TRIGGER " + triggerName;
                    try {
                        s.executeUpdate(sql);
                    } catch (Exception ex) {
                    }
                    s.close();
                }

                // Recreate all the triggers

                s = conn.createStatement();
                sql =
                    "CREATE TRIGGER FORM_PAGE_DELETE AFTER DELETE ON FORM REFERENCING OLD AS DELETED_FORM FOR EACH ROW MODE DB2SQL DELETE FROM FORM_PAGE WHERE FORM_ID = DELETED_FORM.FORM_ID";
                s.executeUpdate(sql);
                s.close();

                s = conn.createStatement();
                sql =
                    "CREATE TRIGGER PROCESSED_IMAGE_DELETE AFTER DELETE ON FORM_PAGE REFERENCING OLD AS DELETED_FORM_PAGE FOR EACH ROW MODE DB2SQL DELETE FROM PROCESSED_IMAGE WHERE FORM_PAGE_ID = DELETED_FORM_PAGE.FORM_PAGE_ID";
                s.executeUpdate(sql);
                s.close();

                s = conn.createStatement();
                sql =
                    "CREATE TRIGGER SEGMENT_DELETE AFTER DELETE ON FORM_PAGE REFERENCING OLD AS DELETED_FORM_PAGE FOR EACH ROW MODE DB2SQL DELETE FROM SEGMENT WHERE FORM_PAGE_ID = DELETED_FORM_PAGE.FORM_PAGE_ID";
                s.executeUpdate(sql);
                s.close();

                s = conn.createStatement();
                sql =
                    "CREATE TRIGGER FRAGMENT_OMR_DELETE AFTER DELETE ON SEGMENT REFERENCING OLD AS DELETED_SEGMENT FOR EACH ROW MODE DB2SQL DELETE FROM FRAGMENT_OMR WHERE SEGMENT_ID = DELETED_SEGMENT.SEGMENT_ID";
                s.executeUpdate(sql);
                s.close();

                s = conn.createStatement();
                sql =
                    "CREATE TRIGGER FRAGMENT_IMAGE_ZONE_DELETE AFTER DELETE ON SEGMENT REFERENCING OLD AS DELETED_SEGMENT FOR EACH ROW MODE DB2SQL DELETE FROM FRAGMENT_IMAGE_ZONE WHERE SEGMENT_ID = DELETED_SEGMENT.SEGMENT_ID";
                s.executeUpdate(sql);
                s.close();

                s = conn.createStatement();
                sql =
                    "CREATE TRIGGER FRAGMENT_BARCODE_DELETE AFTER DELETE ON SEGMENT REFERENCING OLD AS DELETED_SEGMENT FOR EACH ROW MODE DB2SQL DELETE FROM FRAGMENT_BARCODE WHERE SEGMENT_ID = DELETED_SEGMENT.SEGMENT_ID";
                s.executeUpdate(sql);
                s.close();

                s = conn.createStatement();
                sql =
                    "CREATE TRIGGER FRAGMENT_OCR_DELETE AFTER DELETE ON SEGMENT REFERENCING OLD AS DELETED_SEGMENT FOR EACH ROW MODE DB2SQL DELETE FROM FRAGMENT_OCR WHERE SEGMENT_ID = DELETED_SEGMENT.SEGMENT_ID";
                s.executeUpdate(sql);
                s.close();

                s = conn.createStatement();
                sql =
                    "CREATE TRIGGER CHECK_BOX_DELETE AFTER DELETE ON FRAGMENT_OMR REFERENCING OLD AS DELETED_FRAGMENT_OMR FOR EACH ROW MODE DB2SQL DELETE FROM CHECK_BOX WHERE FRAGMENT_OMR_ID = DELETED_FRAGMENT_OMR.FRAGMENT_OMR_ID";
                s.executeUpdate(sql);
                s.close();

            }

        } finally {
            if (res != null && !(res.isClosed())) {
                res.close();
            }
            if (s != null && !(s.isClosed())) {
                s.close();
            }
        }



        // TODO: set RECORD table delete triggers


    }

    private void setDatabaseVersion(Connection connection, String version) {

        String createTableSQL = "CREATE TABLE SYSTEM_PROPERTIES ";
        createTableSQL +=
            "(PROPERTY_NAME VARCHAR(255) NOT NULL, PROPERTY_VALUE VARCHAR(255) NOT NULL, PRIMARY KEY(PROPERTY_NAME))";

        Statement s = null;
        try {
            s = connection.createStatement();
            s.executeUpdate(createTableSQL);
        } catch (SQLException e) {
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (SQLException e) {
                }
            }
        }
        s = null;

        String deleteDatabaseVersionSQL =
            "DELETE FROM SYSTEM_PROPERTIES WHERE PROPERTY_NAME = 'database.version'";
        try {
            s = connection.createStatement();
            s.executeUpdate(deleteDatabaseVersionSQL);
        } catch (SQLException e) {
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (SQLException e) {
                }
            }
        }
        s = null;

        String insertDatabaseVersionSQL =
            "INSERT INTO SYSTEM_PROPERTIES VALUES ('database.version', '" + Main.VERSION + "')";
        try {
            s = connection.createStatement();
            s.executeUpdate(insertDatabaseVersionSQL);
        } catch (SQLException e) {
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (SQLException e) {
                }
            }
        }
        s = null;
    }

    private Connection getConnection()
        throws InstantiationException, IllegalAccessException, ClassNotFoundException,
        SQLException {
        Class.forName(embeddedDriver).newInstance();
        Properties props = new Properties();
        props.put("user", "formreturn");
        props.put("password", systemPassword);
        props.put("securityMechanism", ClientDataSource.STRONG_PASSWORD_SUBSTITUTE_SECURITY + "");
        Connection conn =
            DriverManager.getConnection("jdbc:derby:" + getDatabaseName() + ";create=true", props);
        return conn;
    }

    public void backupDatabase(File backupDirectory) throws DatabaseInstanceException {

        boolean isSuccess = false;
        Connection conn = null;

        try {
            conn = getConnection();
            String sqlstmt = "CALL SYSCS_UTIL.SYSCS_BACKUP_DATABASE(?)";
            CallableStatement cs = conn.prepareCall(sqlstmt);
            cs.setString(1, backupDirectory.getAbsolutePath());
            cs.execute();
            cs.close();
            isSuccess = true;
        } catch (InstantiationException e) {
            logger.warn(e.getLocalizedMessage(), e);
        } catch (IllegalAccessException e) {
            logger.warn(e.getLocalizedMessage(), e);
        } catch (ClassNotFoundException e) {
            logger.warn(e.getLocalizedMessage(), e);
        } catch (SQLException e) {
            logger.warn(e.getLocalizedMessage(), e);
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.warn(e.getLocalizedMessage(), e);
            }
        }

        if (!isSuccess) {
            throw new DatabaseInstanceException(
                Localizer.localize("Server", "DatabaseBackupFailureMessage"));
        }

    }

    public void initDB() throws DatabaseInstanceException {

        boolean isSuccess = false;
        Connection conn = null;

        try {
            conn = getConnection();
            checkUserPrivs(conn);
            isSuccess = true;
        } catch (InstantiationException e) {
            logger.warn(e.getLocalizedMessage(), e);
        } catch (IllegalAccessException e) {
            logger.warn(e.getLocalizedMessage(), e);
        } catch (ClassNotFoundException e) {
            logger.warn(e.getLocalizedMessage(), e);
        } catch (SQLException e) {
            logger.warn(e.getLocalizedMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.warn(e.getLocalizedMessage(), e);
                }
            }
        }

        if (!isSuccess) {
            throw new DatabaseInstanceException(
                Localizer.localize("Server", "DatabaseInitializationFailureMessage"));
        }

    }

    public void changeDatabaseUserPassword(String username, String password)
        throws DatabaseInstanceException {
        boolean isSuccess = false;
        Connection conn = null;

        if (username.trim().equalsIgnoreCase("formreturn")) {
            throw new DatabaseInstanceException(
                Localizer.localize("Server", "CannotChangeSystemPasswordMessage"));
        }

        try {

            conn = getConnection();
            Statement s = conn.createStatement();

            s.executeUpdate(
                "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.user." + username + "', '"
                    + password.trim() + "')");

            isSuccess = true;

        } catch (InstantiationException e1) {
            logger.warn(e1.getLocalizedMessage(), e1);
        } catch (IllegalAccessException e1) {
            logger.warn(e1.getLocalizedMessage(), e1);
        } catch (ClassNotFoundException e1) {
            logger.warn(e1.getLocalizedMessage(), e1);
        } catch (SQLException e1) {
            logger.warn(e1.getLocalizedMessage(), e1);
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.warn(e.getLocalizedMessage(), e);
            }
        }

        if (!isSuccess) {
            throw new DatabaseInstanceException(String
                .format(Localizer.localize("Server", "CannotChangeUserPasswordMessage"), username));
        }

    }

    public void removeDatabaseUser(String username) throws DatabaseInstanceException {

        boolean isSuccess = false;
        Connection conn = null;

        if (username.trim().equalsIgnoreCase("formreturn")) {
            throw new DatabaseInstanceException(
                Localizer.localize("Server", "CannotRemoveSystemUserMessage"));
        }

        try {

            conn = getConnection();
            Statement s = conn.createStatement();

            ArrayList<String> databaseUsers = getDatabaseUsers();
            databaseUsers.remove(username);

            String databaseUsersString = Misc.implode(databaseUsers.toArray(), ",");

            s.executeUpdate(
                "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.user." + username + "', '')");
            s.executeUpdate(
                "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.database.fullAccessUsers', '"
                    + databaseUsersString + "')");

            // revoke user privs from formreturn schema
            Vector<String> schemaNames = new Vector<String>();

            ResultSet rs = s.executeQuery("SELECT s.SCHEMANAME || '.' || t.TABLENAME "
                + "FROM SYS.SYSTABLES t, SYS.SYSSCHEMAS s " + "WHERE t.SCHEMAID = s.SCHEMAID "
                + "AND t.TABLETYPE = 'T' " + "AND s.SCHEMANAME = 'FORMRETURN'");

            while (rs.next()) {
                schemaNames.add(rs.getString(1));
            }
            rs.close();

            for (String schemaName : schemaNames) {
                s.executeUpdate(
                    "REVOKE ALL PRIVILEGES ON TABLE " + schemaName + " FROM " + username);
            }

            isSuccess = true;

        } catch (InstantiationException e1) {
            logger.warn(e1.getLocalizedMessage(), e1);
        } catch (IllegalAccessException e1) {
            logger.warn(e1.getLocalizedMessage(), e1);
        } catch (ClassNotFoundException e1) {
            logger.warn(e1.getLocalizedMessage(), e1);
        } catch (SQLException e1) {
            logger.warn(e1.getLocalizedMessage(), e1);
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.warn(e.getLocalizedMessage(), e);
            }
        }

        if (!isSuccess) {
            throw new DatabaseInstanceException(
                String.format(Localizer.localize("Server", "CannotRemoveUserMessage"), username));
        }

    }

    public void addDatabaseUser(String username, String password) throws DatabaseInstanceException {

        boolean isSuccess = false;
        Connection conn = null;

        // TODO: first check for existing users.. throw error if already exists

        try {

            conn = getConnection();
            Statement s = conn.createStatement();

            ArrayList<String> databaseUsers = getDatabaseUsers();
            databaseUsers.add(username);

            String databaseUsersString = Misc.implode(databaseUsers.toArray(), ",");

            s.executeUpdate(
                "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.user." + username + "', '"
                    + password + "')");
            s.executeUpdate(
                "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.database.fullAccessUsers', '"
                    + databaseUsersString + "')");

            // grant user privs to formreturn schema
            Vector<String> schemaNames = new Vector<String>();

            ResultSet rs = s.executeQuery("SELECT s.SCHEMANAME || '.' || t.TABLENAME "
                + "FROM SYS.SYSTABLES t, SYS.SYSSCHEMAS s " + "WHERE t.SCHEMAID = s.SCHEMAID "
                + "AND t.TABLETYPE = 'T' " + "AND s.SCHEMANAME = 'FORMRETURN'");

            while (rs.next()) {
                schemaNames.add(rs.getString(1));
            }
            rs.close();

            for (String schemaName : schemaNames) {
                s.executeUpdate("GRANT ALL PRIVILEGES ON TABLE " + schemaName + " TO " + username);
            }

            isSuccess = true;

        } catch (InstantiationException e1) {
            logger.error(e1.getLocalizedMessage(), e1);
        } catch (IllegalAccessException e1) {
            logger.error(e1.getLocalizedMessage(), e1);
        } catch (ClassNotFoundException e1) {
            logger.error(e1.getLocalizedMessage(), e1);
        } catch (SQLException e1) {
            logger.error(e1.getLocalizedMessage(), e1);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.warn(e.getLocalizedMessage(), e);
                }
            }
        }

        if (!isSuccess) {
            throw new DatabaseInstanceException(
                String.format(Localizer.localize("Server", "CannotAddNewUserMessage"), username));
        }

    }

    public ArrayList<String> getDatabaseUsers() {
        ArrayList<String> databaseUsers = new ArrayList<String>();

        Connection conn = null;
        try {
            conn = getConnection();
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(
                "VALUES SYSCS_UTIL.SYSCS_GET_DATABASE_PROPERTY('derby.database.fullAccessUsers')");
            if (rs.next()) {
                if (rs.getString(1) != null) {
                    String fullAccessUsers = rs.getString(1);
                    StringTokenizer token = new StringTokenizer(fullAccessUsers, ",");
                    while (token.hasMoreTokens()) {
                        databaseUsers.add(token.nextToken().trim());
                    }
                }
            }

            rs.close();

            s.close();
        } catch (InstantiationException e) {
            logger.error(e.getLocalizedMessage(), e);
        } catch (IllegalAccessException e) {
            logger.error(e.getLocalizedMessage(), e);
        } catch (ClassNotFoundException e) {
            logger.error(e.getLocalizedMessage(), e);
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage(), e);
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.warn(e.getLocalizedMessage(), e);
            }
        }

        return databaseUsers;

    }

    private void setIncomingImageProcedure(Connection conn) throws SQLException {

        Statement s = null;
        ResultSet res = null;

        boolean foundProcedure = false;

        try {

            DatabaseMetaData md = conn.getMetaData();
            res = md.getProcedures(null, "FORMRETURN", "CHECK_INCOMING_IMAGES");
            foundProcedure = res.next();

            s = conn.createStatement();

            if (foundProcedure) {
                s.executeUpdate("DROP PROCEDURE CHECK_INCOMING_IMAGES");
            }

        } catch (Exception ex) {
            Misc.printStackTrace(ex);
            if (ServerGUI.getInstance() != null) {
                logger.error(ex.getLocalizedMessage());
            }
        } finally {

            try {

                s.executeUpdate(
                    "CREATE PROCEDURE CHECK_INCOMING_IMAGES() " + "PARAMETER STYLE JAVA "
                        + "LANGUAGE JAVA " + "NO SQL "
                        + "EXTERNAL NAME 'com.ebstrada.formreturn.server.Main.processIncomingImages'");

            } catch (Exception ex) {
                Misc.printStackTrace(ex);
                if (ServerGUI.getInstance() != null) {
                    logger.error(ex.getLocalizedMessage());
                }
            } finally {
                if (res != null && !(res.isClosed())) {
                    res.close();
                }
                if (s != null && !(s.isClosed())) {
                    s.close();
                }
            }

        }



    }

    private void checkUserPrivs(Connection conn) throws SQLException {
        Statement s = conn.createStatement();
        Statement s2 = conn.createStatement();

        ResultSet rs = s.executeQuery(
            "VALUES SYSCS_UTIL.SYSCS_GET_DATABASE_PROPERTY('derby.connection.requireAuthentication')");
        if (rs.next()) {
            if (rs.getString(1) == null || !(rs.getString(1).trim().equalsIgnoreCase("true"))) {
                s2.executeUpdate(
                    "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.connection.requireAuthentication', 'true')");
            }
        }
        rs.close();

        rs = s.executeQuery(
            "VALUES SYSCS_UTIL.SYSCS_GET_DATABASE_PROPERTY('derby.authentication.provider')");
        if (rs.next()) {
            if (rs.getString(1) == null || !(rs.getString(1).trim().equalsIgnoreCase("BUILTIN"))) {
                s2.executeUpdate(
                    "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.authentication.provider', 'BUILTIN')");
            }
        }
        rs.close();
        s2.executeUpdate(
            "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.authentication.builtin.algorithm', NULL)");

        rs = s.executeQuery(
            "VALUES SYSCS_UTIL.SYSCS_GET_DATABASE_PROPERTY('derby.database.sqlAuthorization')");
        if (rs.next()) {
            if (rs.getString(1) == null || !(rs.getString(1).trim().equalsIgnoreCase("TRUE"))) {
                s2.executeUpdate(
                    "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.database.sqlAuthorization', 'TRUE')");
            }
        }
        rs.close();

        rs = s.executeQuery(
            "VALUES SYSCS_UTIL.SYSCS_GET_DATABASE_PROPERTY('derby.database.defaultConnectionMode')");
        if (rs.next()) {
            if (rs.getString(1) == null || !(rs.getString(1).trim().equalsIgnoreCase("noAccess"))) {
                s2.executeUpdate(
                    "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.database.defaultConnectionMode', 'noAccess')");
            }
        }
        rs.close();

        rs = s.executeQuery(
            "VALUES SYSCS_UTIL.SYSCS_GET_DATABASE_PROPERTY('derby.database.propertiesOnly')");
        if (rs.next()) {
            if (rs.getString(1) == null || !(rs.getString(1).trim().equalsIgnoreCase("true"))) {
                s2.executeUpdate(
                    "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.database.propertiesOnly', 'true')");
            }
        }
        rs.close();

        s.executeUpdate("CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.user.formreturn', '"
            + systemPassword + "')");
        s.executeUpdate("CALL SYSCS_UTIL.SYSCS_SET_USER_ACCESS('formreturn','FULLACCESS')");
        s.executeUpdate(
            "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.database.fullAccessUsers', 'formreturn')");

        s.close();
        s2.close();

    }

    public void shutdown() throws DatabaseInstanceException {

        boolean gotSQLExc = false;

        try {
            Class.forName(embeddedDriver).newInstance();
            Properties props = new Properties();
            props.put("user", "formreturn");
            props.put("password", systemPassword);
            props.put("securityMechanism",
                ClientDataSource.STRONG_PASSWORD_SUBSTITUTE_SECURITY + "");
            DriverManager.getConnection("jdbc:derby:" + databaseName + ";shutdown=true", props);
        } catch (SQLException se) {
            gotSQLExc = true;
        } catch (InstantiationException e) {
            logger.warn(e.getLocalizedMessage(), e);
        } catch (IllegalAccessException e) {
            logger.warn(e.getLocalizedMessage(), e);
        } catch (ClassNotFoundException e) {
            logger.warn(e.getLocalizedMessage(), e);
        } catch (Exception e) {
            logger.warn(e.getLocalizedMessage(), e);
        }

        if (!gotSQLExc) {
            throw new DatabaseInstanceException(
                Localizer.localize("Server", "DatabaseShutdownFailureMessage"));
        }

    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }



}
