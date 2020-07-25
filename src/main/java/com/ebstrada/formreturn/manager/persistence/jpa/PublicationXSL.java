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

import org.apache.openjpa.persistence.jdbc.Index;

@Entity @Table(name = "PUBLICATION_XSL") public class PublicationXSL implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id @Column(name = "PUBLICATION_XSL_ID") @GeneratedValue(strategy = IDENTITY) private long
        publicationXSLId;

    @Version @Column(name = "VERSION") private int version;

    @Column(name = "FILE_NAME") private String fileName;

    @Column(name = "GUID") private String guid;

    @Column(name = "DESCRIPTION") private String description;

    @Index(name = "IDX_PUBLICATION_XSL_ORDER_INDEX") @Column(name = "ORDER_INDEX") private long
        orderIndex;

    @Lob @Basic(fetch = FetchType.LAZY) @Column(name = "XSL_DATA") private byte[] xslData;

    @ManyToOne @JoinColumn(name = "PUBLICATION_ID") private Publication publicationId;

    public PublicationXSL() {
        super();
    }

    public long getPublicationXSLId() {
        return publicationXSLId;
    }

    public void setPublicationXSLId(long publicationXSLId) {
        this.publicationXSLId = publicationXSLId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Publication getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(Publication publicationId) {
        this.publicationId = publicationId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(long orderIndex) {
        this.orderIndex = orderIndex;
    }

    public byte[] getXslData() {
        return xslData;
    }

    public void setXslData(byte[] xslData) {
        this.xslData = xslData;
    }

}
