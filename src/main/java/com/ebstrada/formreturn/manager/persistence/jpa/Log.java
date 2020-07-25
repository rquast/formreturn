package com.ebstrada.formreturn.manager.persistence.jpa;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity @Table(name = "LOG") @NamedQueries({
    @NamedQuery(name = "Log.findByLogId", query = "SELECT lg FROM Log lg WHERE lg.logId = :logId"),
    @NamedQuery(name = "Log.findAll", query = "SELECT lg FROM Log lg"),
    @NamedQuery(name = "Log.count", query = "SELECT COUNT(lg.logId) FROM Log lg")}) public class Log
    implements Serializable {
    @Id @Column(name = "LOG_ID") @GeneratedValue(strategy = IDENTITY) private long logId;

    @Version @Column(name = "VERSION") private int version;

    @Column(name = "LOG_MESSAGE") private String logMessage;

    @Column(name = "LOG_TIME") private Timestamp logTime;

    @Column(name = "LOG_LEVEL") private short logLevel;

    private static final long serialVersionUID = 1L;

    public Log() {
        super();
    }

    public long getLogId() {
        return this.logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public String getLogMessage() {
        return this.logMessage;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }

    public Timestamp getLogTime() {
        return this.logTime;
    }

    public void setLogTime(Timestamp logTime) {
        this.logTime = logTime;
    }

    public short getLogLevel() {
        return this.logLevel;
    }

    public void setLogLevel(short logLevel) {
        this.logLevel = logLevel;
    }

    @Override public String toString() {
        return this.getLogMessage();
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

}

