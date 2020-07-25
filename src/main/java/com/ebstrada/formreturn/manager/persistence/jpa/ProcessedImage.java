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

@Entity @Table(name = "PROCESSED_IMAGE") @NamedQueries({
    @NamedQuery(name = "ProcessedImage.findByProcessedImageId", query = "SELECT pi FROM ProcessedImage pi WHERE pi.processedImageId = :processedImageId"),
    @NamedQuery(name = "ProcessedImage.findAll", query = "SELECT pi FROM ProcessedImage pi"),
    @NamedQuery(name = "ProcessedImage.count", query = "SELECT COUNT(pi.processedImageId) FROM ProcessedImage pi")})
public class ProcessedImage implements Serializable {
    @Id @Column(name = "PROCESSED_IMAGE_ID") @GeneratedValue(strategy = IDENTITY) private long
        processedImageId;

    @Version @Column(name = "VERSION") private int version;

    @ManyToOne @JoinColumn(name = "FORM_PAGE_ID") private FormPage formPageId;

    @Column(name = "PROCESSED_IMAGE_NAME") private String processedImageName;

    @Lob @Basic(fetch = FetchType.LAZY) @Column(name = "PROCESSED_IMAGE_DATA") private byte[]
        processedImageData;

    private static final long serialVersionUID = 1L;

    public ProcessedImage() {
        super();
    }

    public long getProcessedImageId() {
        return this.processedImageId;
    }

    public void setProcessedImageId(long processedImageId) {
        this.processedImageId = processedImageId;
    }

    public String getProcessedImageName() {
        return this.processedImageName;
    }

    public void setProcessedImageName(String processedImageName) {
        this.processedImageName = processedImageName;
    }

    public byte[] getProcessedImageData() {
        return this.processedImageData;
    }

    public void setProcessedImageData(byte[] processedImageData) {
        this.processedImageData = processedImageData;
    }

    public FormPage getFormPageId() {
        return this.formPageId;
    }

    public void setFormPageId(FormPage formPageId) {
        this.formPageId = formPageId;
    }

    @Override public String toString() {
        return this.getProcessedImageName();
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

}

