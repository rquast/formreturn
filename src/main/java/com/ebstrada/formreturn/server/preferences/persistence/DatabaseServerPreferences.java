package com.ebstrada.formreturn.server.preferences.persistence;

import com.ebstrada.formreturn.manager.util.NoObfuscation;

public class DatabaseServerPreferences implements NoObfuscation {

    private boolean allowExternalConnections = false;

    private String listeningAddresses = "127.0.0.1";

    private int portNumber = 1527;

    public boolean isAllowExternalConnections() {
        return allowExternalConnections;
    }

    public void setAllowExternalConnections(boolean allowExternalConnections) {
        this.allowExternalConnections = allowExternalConnections;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public String getListeningAddresses() {
        if (listeningAddresses == null) {
            listeningAddresses = "127.0.0.1";
        }
        return listeningAddresses;
    }

    public void setListeningAddresses(String listeningAddresses) {
        this.listeningAddresses = listeningAddresses;
    }

}
