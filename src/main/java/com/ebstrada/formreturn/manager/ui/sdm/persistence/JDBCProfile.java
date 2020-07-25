package com.ebstrada.formreturn.manager.ui.sdm.persistence;

import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("JDBCProfile") public class JDBCProfile implements NoObfuscation {

    String profileName;
    private String serverIPAddress;
    private String serverPortNumber;
    private String jdbcProtocol;
    private String username;
    private String password;
    private String jdbcDriverFilePath;
    private String jdbcURL;
    private String sqlQuery;
    private String databaseName;

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public void setServerIPAddress(String serverIPAddress) {
        this.serverIPAddress = serverIPAddress;
    }

    public void setServerPortNumber(String serverPortNumber) {
        this.serverPortNumber = serverPortNumber;
    }

    public void setJDBCProtocol(String jdbcProtocol) {
        this.jdbcProtocol = jdbcProtocol;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setJDBCDriverFilePath(String jdbcDriverFilePath) {
        this.jdbcDriverFilePath = jdbcDriverFilePath;
    }

    public void setJDBCURL(String jdbcURL) {
        this.jdbcURL = jdbcURL;
    }

    public void setSQLQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    public String getJdbcProtocol() {
        return jdbcProtocol;
    }

    public void setJdbcProtocol(String jdbcProtocol) {
        this.jdbcProtocol = jdbcProtocol;
    }

    public String getJdbcDriverFilePath() {
        return jdbcDriverFilePath;
    }

    public void setJdbcDriverFilePath(String jdbcDriverFilePath) {
        this.jdbcDriverFilePath = jdbcDriverFilePath;
    }

    public String getJdbcURL() {
        return jdbcURL;
    }

    public void setJdbcURL(String jdbcURL) {
        this.jdbcURL = jdbcURL;
    }

    public String getSqlQuery() {
        return sqlQuery;
    }

    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    public String getServerIPAddress() {
        return serverIPAddress;
    }

    public String getServerPortNumber() {
        return serverPortNumber;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

}
