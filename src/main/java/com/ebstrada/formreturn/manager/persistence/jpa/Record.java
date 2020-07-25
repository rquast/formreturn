package com.ebstrada.formreturn.manager.persistence.jpa;

import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity @Table(name = "RECORD") @NamedQueries({
    @NamedQuery(name = "Record.findByRecordIds", query = "SELECT r FROM Record r WHERE r.recordId IN (:recordIds)"),
    @NamedQuery(name = "Record.findByRecordId", query = "SELECT r FROM Record r WHERE r.recordId = :recordId"),
    @NamedQuery(name = "Record.findByDataSetId", query = "SELECT r FROM Record r WHERE r.dataSetId = :dataSetId"),
    @NamedQuery(name = "Record.findAll", query = "SELECT r FROM Record r"),
    @NamedQuery(name = "Record.count", query = "SELECT COUNT(r.recordId) FROM Record r")})
public class Record implements Serializable {
    private static final CascadeType[] DETACH = null;

    @Id @Column(name = "RECORD_ID") @GeneratedValue(strategy = IDENTITY) private long recordId;

    @Version @Column(name = "VERSION") private int version;

    @Column(name = "RECORD_CREATED") private Timestamp recordCreated;

    @Column(name = "RECORD_MODIFIED") private Timestamp recordModified;

    @ManyToOne @JoinColumn(name = "DATA_SET_ID") private DataSet dataSetId;

    @OneToMany(mappedBy = "recordId", cascade = REMOVE) private List<SourceText>
        sourceTextCollection;

    @OneToMany(mappedBy = "recordId", cascade = REMOVE) private List<SourceImage>
        sourceImageCollection;

    @OneToMany(mappedBy = "recordId", fetch = FetchType.EAGER) private List<Form> formCollection;

    private static final long serialVersionUID = 1L;

    public Record() {
        super();
    }

    public long getRecordId() {
        return this.recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    public Timestamp getRecordCreated() {
        return this.recordCreated;
    }

    public void setRecordCreated(Timestamp recordCreated) {
        this.recordCreated = recordCreated;
    }

    public Timestamp getRecordModified() {
        return this.recordModified;
    }

    public void setRecordModified(Timestamp recordModified) {
        this.recordModified = recordModified;
    }

    public DataSet getDataSetId() {
        return this.dataSetId;
    }

    public void setDataSetId(DataSet dataSetId) {
        this.dataSetId = dataSetId;
    }

    public List<SourceText> getSourceTextCollection() {
        return this.sourceTextCollection;
    }

    public void setSourceTextCollection(List<SourceText> sourceTextCollection) {
        this.sourceTextCollection = sourceTextCollection;
    }

    public List<SourceImage> getSourceImageCollection() {
        return this.sourceImageCollection;
    }

    public void setSourceImageCollection(List<SourceImage> sourceImageCollection) {
        this.sourceImageCollection = sourceImageCollection;
    }

    public List<Form> getFormCollection() {
        return this.formCollection;
    }

    public void setFormCollection(List<Form> formCollection) {
        this.formCollection = formCollection;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

}
