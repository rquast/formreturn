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
import javax.persistence.Table;
import javax.persistence.Version;

@Entity @Table(name = "SOURCE_IMAGE") public class SourceImage implements Serializable {
    @Id @Column(name = "SOURCE_IMAGE_ID") @GeneratedValue(strategy = IDENTITY) private long
        sourceImageId;

    @Version @Column(name = "VERSION") private int version;

    @Lob @Basic(fetch = FetchType.LAZY) @Column(name = "SOURCE_IMAGE_DATA") private byte[]
        sourceImageData;

    @ManyToOne @JoinColumn(name = "RECORD_ID") private Record recordId;

    @ManyToOne @JoinColumn(name = "SOURCE_FIELD_ID") private SourceField sourceFieldId;

    private static final long serialVersionUID = 1L;

    public SourceImage() {
        super();
    }

    public Record getRecordId() {
        return this.recordId;
    }

    public void setRecordId(Record recordId) {
        this.recordId = recordId;
    }

    public long getSourceImageId() {
        return this.sourceImageId;
    }

    public void setSourceImageId(long sourceImageId) {
        this.sourceImageId = sourceImageId;
    }

    public byte[] getSourceImageData() {
        return this.sourceImageData;
    }

    public void setSourceImageData(byte[] sourceImageData) {
        this.sourceImageData = sourceImageData;
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
