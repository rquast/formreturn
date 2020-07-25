package com.ebstrada.formreturn.manager.persistence.jpa;

import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity @Table(name = "DATA_SET") @NamedQueries({
    @NamedQuery(name = "DataSet.findByDataSetId", query = "SELECT ds FROM DataSet ds WHERE ds.dataSetId = :dataSetId"),
    @NamedQuery(name = "DataSet.findAll", query = "SELECT ds FROM DataSet ds"),
    @NamedQuery(name = "DataSet.count", query = "SELECT COUNT(ds.dataSetId) FROM DataSet ds")})
public class DataSet implements Serializable {
    @Id @Column(name = "DATA_SET_ID") @GeneratedValue(strategy = IDENTITY) private long dataSetId;

    @Version @Column(name = "VERSION") private int version;

    @Column(name = "DATA_SET_NAME") private String dataSetName;

    @OneToMany(mappedBy = "dataSetId", cascade = REMOVE) private List<QueryProfile>
        queryProfileCollection;

    @OneToMany(mappedBy = "dataSetId", cascade = REMOVE) private List<Publication>
        publicationCollection;

    @OneToMany(mappedBy = "dataSetId", cascade = REMOVE) private List<Record> recordCollection;

    @OneToMany(mappedBy = "dataSetId", cascade = REMOVE) @OrderBy("orderIndex ASC")
    private List<SourceField> sourceFieldCollection;

    private static final long serialVersionUID = 1L;

    public DataSet() {
        super();
    }

    public long getDataSetId() {
        return this.dataSetId;
    }

    public void setDataSetId(long dataSetId) {
        this.dataSetId = dataSetId;
    }

    public String getDataSetName() {
        return this.dataSetName;
    }

    public void setDataSetName(String dataSetName) {
        this.dataSetName = dataSetName;
    }

    public List<QueryProfile> getQueryProfileCollection() {
        return this.queryProfileCollection;
    }

    public void setQueryProfileCollection(List<QueryProfile> queryProfileCollection) {
        this.queryProfileCollection = queryProfileCollection;
    }

    public List<Publication> getPublicationCollection() {
        return this.publicationCollection;
    }

    public void setPublicationCollection(List<Publication> publicationCollection) {
        this.publicationCollection = publicationCollection;
    }

    public List<Record> getRecordCollection() {
        return this.recordCollection;
    }

    public void setRecordCollection(List<Record> recordCollection) {
        this.recordCollection = recordCollection;
    }

    public List<SourceField> getSourceFieldCollection() {
        return this.sourceFieldCollection;
    }

    public void setSourceFieldCollection(List<SourceField> sourceFieldCollection) {
        this.sourceFieldCollection = sourceFieldCollection;
    }

    @Override public String toString() {
        return this.getDataSetName();
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

}
