package com.ebstrada.formreturn.manager.persistence.jpa;

import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.openjpa.persistence.jdbc.Index;

@Entity @Table(name = "SOURCE_FIELD") @NamedQueries({
    @NamedQuery(name = "SourceField.findBySourceFieldId", query = "SELECT sf FROM SourceField sf WHERE sf.sourceFieldId = :sourceFieldId"),
    @NamedQuery(name = "SourceField.findByDataSetId", query = "SELECT sf FROM SourceField sf WHERE sf.dataSetId = :dataSetId"),
    @NamedQuery(name = "SourceField.findBySourceFieldName", query = "SELECT sf FROM SourceField sf WHERE sf.sourceFieldName = :sourceFieldName AND sf.dataSetId = :dataSetId"),
    @NamedQuery(name = "SourceField.findAll", query = "SELECT sf FROM SourceField sf")})
public class SourceField implements Serializable {
    @Id @Column(name = "SOURCE_FIELD_ID") @GeneratedValue(strategy = IDENTITY) private long
        sourceFieldId;

    @Version @Column(name = "VERSION") private int version;

    @Column(name = "SOURCE_FIELD_NAME") @Index(name = "IDX_SOURCE_FIELD_NAME") private String
        sourceFieldName;

    @Column(name = "SOURCE_FIELD_TYPE") private String sourceFieldType;

    @Index(name = "IDX_SOURCE_FIELD_ORDER_INDEX") @Column(name = "ORDER_INDEX") private long
        orderIndex;

    @ManyToOne @JoinColumn(name = "DATA_SET_ID") private DataSet dataSetId;

    @OneToMany(mappedBy = "sourceFieldId", cascade = REMOVE) private List<SourceText>
        sourceTextCollection;

    @OneToMany(mappedBy = "sourceFieldId", cascade = REMOVE) private List<SourceImage>
        sourceImageCollection;

    private static final long serialVersionUID = 1L;

    public SourceField() {
        super();
    }

    public long getSourceFieldId() {
        return this.sourceFieldId;
    }

    public void setSourceFieldId(long sourceFieldId) {
        this.sourceFieldId = sourceFieldId;
    }

    public String getSourceFieldName() {
        return this.sourceFieldName;
    }

    public void setSourceFieldName(String sourceFieldName) {
        this.sourceFieldName = sourceFieldName;
    }

    public String getSourceFieldType() {
        return this.sourceFieldType;
    }

    public void setSourceFieldType(String sourceFieldType) {
        this.sourceFieldType = sourceFieldType;
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

    public long getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(long orderIndex) {
        this.orderIndex = orderIndex;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

}
