package com.ebstrada.formreturn.manager.persistence.jpa;

import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity @Table(name = "PUBLICATION") @NamedQueries({
    @NamedQuery(name = "Publication.findByPublicationId", query = "SELECT pub FROM Publication pub WHERE pub.publicationId = :publicationId"),
    @NamedQuery(name = "Publication.findByDataSetId", query = "SELECT pub FROM Publication pub WHERE pub.dataSetId = :dataSetId"),
    @NamedQuery(name = "Publication.findByPublicationName", query = "SELECT pub FROM Publication pub WHERE pub.publicationName = :publicationName AND pub.dataSetId = :dataSetId"),
    @NamedQuery(name = "Publication.count", query = "SELECT COUNT(pub.publicationId) FROM Publication pub"),
    @NamedQuery(name = "Publication.findAll", query = "SELECT pub FROM Publication pub")})
public class Publication implements Serializable {
    @Id @Column(name = "PUBLICATION_ID") @GeneratedValue(strategy = IDENTITY) private long
        publicationId;

    @Version @Column(name = "VERSION") private int version;

    @Column(name = "PUBLICATION_NAME") private String publicationName;

    @Lob @Basic(fetch = FetchType.LAZY) @Column(name = "TEMPLATE_FILE") private byte[] templateFile;

    @Column(name = "LUMINANCE_THRESHOLD") private short luminanceThreshold;

    @Column(name = "PUBLICATION_CREATED") private Timestamp publicationCreated;

    @Column(name = "DESKEW_THRESHOLD") private double deskewThreshold;

    @Column(name = "FRAGMENT_PADDING") private short fragmentPadding;

    @Column(name = "MARK_THRESHOLD") private short markThreshold;

    @Column(name = "PERFORM_DESKEW") private short performDeskew;

    @Column(name = "PUBLICATION_TYPE") private short publicationType;

    @Column(name = "SCANNED_IN_ORDER") private short scannedInOrder;

    @ManyToOne @JoinColumn(name = "DATA_SET_ID") private DataSet dataSetId;

    @OneToMany(mappedBy = "publicationId", cascade = REMOVE) private List<Form> formCollection;

    @OneToMany(mappedBy = "publicationId", cascade = REMOVE) private List<Grading>
        gradingCollection;

    @OneToMany(mappedBy = "publicationId", cascade = REMOVE) private List<PublicationXSL>
        publicationXSLCollection;

    @OneToMany(mappedBy = "publicationId", cascade = REMOVE) private List<PublicationJAR>
        publicationJARCollection;

    private static final long serialVersionUID = 1L;

    public Publication() {
        super();
    }

    public long getPublicationId() {
        return this.publicationId;
    }

    public void setPublicationId(long publicationId) {
        this.publicationId = publicationId;
    }

    public String getPublicationName() {
        return this.publicationName;
    }

    public void setPublicationName(String publicationName) {
        this.publicationName = publicationName;
    }

    public byte[] getTemplateFile() {
        return this.templateFile;
    }

    public void setTemplateFile(byte[] templateFile) {
        this.templateFile = templateFile;
    }

    public short getLuminanceThreshold() {
        return this.luminanceThreshold;
    }

    public void setLuminanceThreshold(short luminanceThreshold) {
        this.luminanceThreshold = luminanceThreshold;
    }

    public Timestamp getPublicationCreated() {
        return this.publicationCreated;
    }

    public void setPublicationCreated(Timestamp publicationCreated) {
        this.publicationCreated = publicationCreated;
    }

    public double getDeskewThreshold() {
        return this.deskewThreshold;
    }

    public void setDeskewThreshold(double deskewThreshold) {
        this.deskewThreshold = deskewThreshold;
    }

    public short getFragmentPadding() {
        return this.fragmentPadding;
    }

    public void setFragmentPadding(short fragmentPadding) {
        this.fragmentPadding = fragmentPadding;
    }

    public short getMarkThreshold() {
        return this.markThreshold;
    }

    public void setMarkThreshold(short markThreshold) {
        this.markThreshold = markThreshold;
    }

    public short getPerformDeskew() {
        return this.performDeskew;
    }

    public void setPerformDeskew(short performDeskew) {
        this.performDeskew = performDeskew;
    }

    public DataSet getDataSetId() {
        return this.dataSetId;
    }

    public void setDataSetId(DataSet dataSetId) {
        this.dataSetId = dataSetId;
    }

    public List<Form> getFormCollection() {
        return this.formCollection;
    }

    public void setFormCollection(List<Form> formCollection) {
        this.formCollection = formCollection;
    }

    public List<Grading> getGradingCollection() {
        return gradingCollection;
    }

    public void setGradingCollection(List<Grading> gradingCollection) {
        this.gradingCollection = gradingCollection;
    }

    public List<PublicationXSL> getPublicationXSLCollection() {
        return publicationXSLCollection;
    }

    public void setPublicationXSLCollection(List<PublicationXSL> publicationXSLCollection) {
        this.publicationXSLCollection = publicationXSLCollection;
    }

    public List<PublicationJAR> getPublicationJARCollection() {
        return publicationJARCollection;
    }

    public void setPublicationJARCollection(List<PublicationJAR> publicationJARCollection) {
        this.publicationJARCollection = publicationJARCollection;
    }

    public short getPublicationType() {
        return publicationType;
    }

    public void setPublicationType(short publicationType) {
        this.publicationType = publicationType;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setScannedInOrder(short scannedInOrder) {
        this.scannedInOrder = scannedInOrder;
    }

    public short getScannedInOrder() {
        return scannedInOrder;
    }

}
