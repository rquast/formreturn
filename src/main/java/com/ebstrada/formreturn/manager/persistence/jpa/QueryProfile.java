package com.ebstrada.formreturn.manager.persistence.jpa;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity @Table(name = "QUERY_PROFILE") public class QueryProfile implements Serializable {
    @Id @Column(name = "QUERY_PROFILE_ID") @GeneratedValue(strategy = IDENTITY) private long
        queryProfileId;

    @Version @Column(name = "VERSION") private int version;

    @Column(name = "QUERY_PROFILE_NAME") private String queryProfileName;

    @Lob @Basic(fetch = FetchType.LAZY) @Column(name = "QUERY_PROFILE_XML") private byte[]
        queryProfileXml;

    @ManyToOne @JoinColumn(name = "DATA_SET_ID") private DataSet dataSetId;

    private static final long serialVersionUID = 1L;

    public QueryProfile() {
        super();
    }

    public long getQueryProfileId() {
        return this.queryProfileId;
    }

    public void setQueryProfileId(long queryProfileId) {
        this.queryProfileId = queryProfileId;
    }

    public String getQueryProfileName() {
        return this.queryProfileName;
    }

    public void setQueryProfileName(String queryProfileName) {
        this.queryProfileName = queryProfileName;
    }

    public byte[] getQueryProfileXml() {
        return this.queryProfileXml;
    }

    public void setQueryProfileXml(byte[] queryProfileXml) {
        this.queryProfileXml = queryProfileXml;
    }

    public DataSet getDataSetId() {
        return this.dataSetId;
    }

    public void setDataSetId(DataSet dataSetId) {
        this.dataSetId = dataSetId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

}
