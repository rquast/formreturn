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

@Entity @Table(name = "FORM")
@NamedQueries({@NamedQuery(name = "Form.count", query = "SELECT COUNT(frm.formId) FROM Form frm"),
    @NamedQuery(name = "Form.findByPublicationId", query = "SELECT frm FROM Form frm WHERE frm.publicationId = :publicationId ORDER BY frm.formId ASC"),
    @NamedQuery(name = "Form.findTemplateByPublicationId", query = "SELECT frm FROM Form frm WHERE frm.recordId IS NULL AND frm.publicationId = :publicationId"),
    @NamedQuery(name = "Form.findAll", query = "SELECT frm FROM Form frm")}) public class Form
    implements Serializable {
    @Id @Column(name = "FORM_ID") @GeneratedValue(strategy = IDENTITY) private long formId;

    @Version @Column(name = "VERSION") private int version;

    @Column(name = "FORM_PASSWORD") private String formPassword;

    @ManyToOne @JoinColumn(name = "PUBLICATION_ID") private Publication publicationId;

    @ManyToOne @JoinColumn(name = "RECORD_ID") private Record recordId;

    @OneToMany(mappedBy = "formId", cascade = REMOVE) private List<FormPage> formPageCollection;

    @Column(name = "AGGREGATE_MARK") private double aggregateMark;

    @Column(name = "ERROR_COUNT") private long errorCount;

    private static final long serialVersionUID = 1L;

    public Form() {
        super();
    }

    public long getFormId() {
        return this.formId;
    }

    public void setFormId(long formId) {
        this.formId = formId;
    }

    public String getFormPassword() {
        return this.formPassword;
    }

    public void setFormPassword(String formPassword) {
        this.formPassword = formPassword;
    }

    public Publication getPublicationId() {
        return this.publicationId;
    }

    public void setPublicationId(Publication publicationId) {
        this.publicationId = publicationId;
    }

    public Record getRecordId() {
        return this.recordId;
    }

    public void setRecordId(Record recordId) {
        this.recordId = recordId;
    }

    public List<FormPage> getFormPageCollection() {
        return this.formPageCollection;
    }

    public void setFormPageCollection(List<FormPage> formPageCollection) {
        this.formPageCollection = formPageCollection;
    }

    public double getAggregateMark() {
        return this.aggregateMark;
    }

    public void setAggregateMark(double aggregateMark) {
        this.aggregateMark = aggregateMark;
    }

    public long getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(long errorCount) {
        this.errorCount = errorCount;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

}
