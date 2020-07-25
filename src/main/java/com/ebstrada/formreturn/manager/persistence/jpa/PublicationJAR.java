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

@Entity @Table(name = "PUBLICATION_JAR") public class PublicationJAR implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id @Column(name = "PUBLICATION_JAR_ID") @GeneratedValue(strategy = IDENTITY) private long
        publicationJARId;

    @Version @Column(name = "VERSION") private int version;

    @Column(name = "FILE_NAME") private String fileName;

    @Column(name = "GUID") private String guid;

    @Column(name = "DESCRIPTION") private String description;

    @Index(name = "IDX_PUBLICATION_JAR_ORDER_INDEX") @Column(name = "ORDER_INDEX") private long
        orderIndex;

    @Lob @Basic(fetch = FetchType.LAZY) @Column(name = "JAR_DATA") private byte[] jarData;

    @ManyToOne @JoinColumn(name = "PUBLICATION_ID") private Publication publicationId;

    public PublicationJAR() {
        super();
    }

    public long getPublicationJARId() {
        return publicationJARId;
    }

    public void setPublicationJARId(long publicationJARId) {
        this.publicationJARId = publicationJARId;
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

    public byte[] getJarData() {
        return jarData;
    }

    public void setJarData(byte[] jarData) {
        this.jarData = jarData;
    }

}
