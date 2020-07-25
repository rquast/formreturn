package com.ebstrada.formreturn.manager.persistence.jpa;

import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.sql.Timestamp;
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

@Entity @Table(name = "FORM_PAGE") @NamedQueries({
    @NamedQuery(name = "FormPage.findByFormPageNumber", query = "SELECT fp FROM FormPage fp WHERE fp.formId = :formId AND fp.formPageNumber = :formPageNumber"),
    @NamedQuery(name = "FormPage.findByFormId", query = "SELECT fp FROM FormPage fp WHERE fp.formId = :formId ORDER BY fp.formPageNumber ASC"),
    @NamedQuery(name = "FormPage.count", query = "SELECT COUNT(fp.formPageId) FROM FormPage fp"),
    @NamedQuery(name = "FormPage.findAll", query = "SELECT fp FROM FormPage fp")})
public class FormPage implements Serializable {
    @Id @Column(name = "FORM_PAGE_ID") @GeneratedValue(strategy = IDENTITY) private long formPageId;

    @Version @Column(name = "VERSION") private int version;

    @Column(name = "FORM_PAGE_NUMBER") private long formPageNumber;

    @Column(name = "CAPTURE_TIME") private Timestamp captureTime;

    @Column(name = "PROCESSED_TIME") private Timestamp processedTime;

    @OneToMany(mappedBy = "formPageId", cascade = REMOVE) private List<Segment> segmentCollection;

    @OneToMany(mappedBy = "formPageId", cascade = REMOVE) private List<ProcessedImage>
        processedImageCollection;

    @ManyToOne @JoinColumn(name = "FORM_ID") private Form formId;

    @Column(name = "AGGREGATE_MARK") private double aggregateMark;

    @Column(name = "ERROR_COUNT") private long errorCount;

    @Column(name = "SCANNED_PAGE_NUMBER") private int scannedPageNumber;

    private static final long serialVersionUID = 1L;

    public FormPage() {
        super();
    }

    public long getFormPageId() {
        return this.formPageId;
    }

    public void setFormPageId(long formPageId) {
        this.formPageId = formPageId;
    }

    public List<ProcessedImage> getProcessedImageCollection() {
        return this.processedImageCollection;
    }

    public void setProcessedImageCollection(List<ProcessedImage> processedImageCollection) {
        this.processedImageCollection = processedImageCollection;
    }

    public long getFormPageNumber() {
        return this.formPageNumber;
    }

    public void setFormPageNumber(long formPageNumber) {
        this.formPageNumber = formPageNumber;
    }

    public Form getFormId() {
        return this.formId;
    }

    public void setFormId(Form formId) {
        this.formId = formId;
    }

    public List<Segment> getSegmentCollection() {
        return this.segmentCollection;
    }

    public void setSegmentCollection(List<Segment> segmentCollection) {
        this.segmentCollection = segmentCollection;
    }

    public Timestamp getCaptureTime() {
        return captureTime;
    }

    public void setCaptureTime(Timestamp captureTime) {
        this.captureTime = captureTime;
    }

    public Timestamp getProcessedTime() {
        return this.processedTime;
    }

    public void setProcessedTime(Timestamp processedTime) {
        this.processedTime = processedTime;
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

    public void setScannedPageNumber(int scannedPageNumber) {
        this.scannedPageNumber = scannedPageNumber;
    }

    public int getScannedPageNumber() {
        return scannedPageNumber;
    }

}
