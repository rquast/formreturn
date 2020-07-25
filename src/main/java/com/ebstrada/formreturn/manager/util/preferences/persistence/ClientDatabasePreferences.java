package com.ebstrada.formreturn.manager.util.preferences.persistence;

import com.ebstrada.formreturn.manager.util.NoObfuscation;

public class ClientDatabasePreferences implements NoObfuscation {

    private String serverIPAddress = "127.0.0.1";

    private int portNumber = 1527;

    private String databaseName = "FRDB";

    private String username = "formreturn";

    private String password = "";

    public String getServerIPAddress() {
        return serverIPAddress;
    }

    public void setServerIPAddress(String serverIPAddress) {
        this.serverIPAddress = serverIPAddress;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
