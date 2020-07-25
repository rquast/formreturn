package com.ebstrada.formreturn.manager.persistence.jpa;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.openjpa.persistence.jdbc.Index;

@Entity @Table(name = "SOURCE_TEXT") @NamedQueries({
    @NamedQuery(name = "SourceText.findBySourceTextId", query = "SELECT st FROM SourceText st WHERE st.sourceTextId = :sourceTextId"),
    @NamedQuery(name = "SourceText.findByRecordAndSourceField", query = "SELECT st FROM SourceText st WHERE st.recordId = :recordId AND st.sourceFieldId = :sourceFieldId"),
    @NamedQuery(name = "SourceText.findBySourceFieldIdAndSourceTextString", query = "SELECT st FROM SourceText st WHERE st.sourceFieldId = :sourceFieldId AND st.sourceTextString = :sourceTextString"),
    @NamedQuery(name = "SourceText.findAll", query = "SELECT st FROM SourceText st")})
public class SourceText implements Serializable {
    @Id @Column(name = "SOURCE_TEXT_ID") @GeneratedValue(strategy = IDENTITY) private long
        sourceTextId;

    @Version @Column(name = "VERSION") private int version;

    @Column(name = "SOURCE_TEXT_STRING") @Index(name = "IDX_SOURCE_TEXT_STRING") private String
        sourceTextString;

    @ManyToOne @JoinColumn(name = "RECORD_ID") private Record recordId;

    @ManyToOne @JoinColumn(name = "SOURCE_FIELD_ID") private SourceField sourceFieldId;

    private static final long serialVersionUID = 1L;

    public SourceText() {
        super();
    }

    public Record getRecordId() {
        return this.recordId;
    }

    public void setRecordId(Record recordId) {
        this.recordId = recordId;
    }

    public long getSourceTextId() {
        return this.sourceTextId;
    }

    public void setSourceTextId(long sourceTextId) {
        this.sourceTextId = sourceTextId;
    }

    public String getSourceTextString() {
        return this.sourceTextString;
    }

    public void setSourceTextString(String sourceTextString) {
        this.sourceTextString = sourceTextString;
    }

    public SourceField getSourceFieldId() {
        return this.sourceFieldId;
    }

    public void setSourceFieldId(SourceField sourceFieldId) {
        this.sourceFieldId = sourceFieldId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

}
