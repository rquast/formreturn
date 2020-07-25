package com.ebstrada.formreturn.manager.persistence.jpa;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.openjpa.persistence.jdbc.Index;

@Entity @Table(name = "INCOMING_IMAGE") @NamedQueries({
    @NamedQuery(name = "IncomingImage.findByIncomingImageId", query = "SELECT ii FROM IncomingImage ii WHERE ii.incomingImageId = :incomingImageId"),
    @NamedQuery(name = "IncomingImage.findAll", query = "SELECT ii FROM IncomingImage ii"),
    @NamedQuery(name = "IncomingImage.findByMatchStatus", query = "SELECT ii FROM IncomingImage ii WHERE ii.incomingImageId = (SELECT MIN(ii.incomingImageId) FROM IncomingImage ii WHERE ii.matchStatus = :matchStatus)"),
    @NamedQuery(name = "IncomingImage.count", query = "SELECT COUNT(ii.incomingImageId) FROM IncomingImage ii")})
public class IncomingImage implements Serializable {
    @Id @Column(name = "INCOMING_IMAGE_ID") @GeneratedValue(strategy = IDENTITY) private long
        incomingImageId;

    @Version @Column(name = "VERSION") private int version;

    @Column(name = "INCOMING_IMAGE_NAME") private String incomingImageName;

    @Lob @Basic(fetch = FetchType.LAZY) @Column(name = "INCOMING_IMAGE_DATA") private byte[]
        incomingImageData;

    @Column(name = "CAPTURE_TIME") private Timestamp captureTime;

    @Column(name = "MATCH_STATUS") @Index(name = "IDX_MATCH_STATUS") private short matchStatus;

    @Column(name = "MATCH_ERROR_TYPE") private short matchErrorType;

    @Column(name = "MATCH_ERROR_DATA") private String matchErrorData;

    @Column(name = "NUMBER_OF_PAGES") private long numberOfPages;

    @Column(name = "ASSIGN_TO_FORM_PAGE_ID") private long assignToFormPageId;

    @Column(name = "MATCH_ERROR_SCANNED_PAGE_NUMBER") private int matchErrorScannedPageNumber;

    private static final long serialVersionUID = 1L;

    public IncomingImage() {
        super();
    }

    public long getIncomingImageId() {
        return this.incomingImageId;
    }

    public void setIncomingImageId(long incomingImageId) {
        this.incomingImageId = incomingImageId;
    }

    public String getIncomingImageName() {
        return this.incomingImageName;
    }

    public void setIncomingImageName(String incomingImageName) {
        this.incomingImageName = incomingImageName;
    }

    public byte[] getIncomingImageData() {
        return this.incomingImageData;
    }

    public void setIncomingImageData(byte[] incomingImageData) {
        this.incomingImageData = incomingImageData;
    }

    public short getMatchStatus() {
        return this.matchStatus;
    }

    public void setMatchStatus(short matchStatus) {
        this.matchStatus = matchStatus;
    }

    public Timestamp getCaptureTime() {
        return this.captureTime;
    }

    public void setCaptureTime(Timestamp captureTime) {
        this.captureTime = captureTime;
    }

    @Override public String toString() {
        return this.getIncomingImageName();
    }

    public void setMatchErrorType(int error) {
        matchErrorType = (short) error;
    }

    public short getMatchErrorType() {
        return matchErrorType;
    }

    public String getMatchErrorData() {
        return matchErrorData;
    }

    public void setMatchErrorData(String matchErrorData) {
        this.matchErrorData = matchErrorData;
    }

    public void setNumberOfPages(long numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public long getNumberOfPages() {
        return numberOfPages;
    }

    public long getAssignToFormPageId() {
        return assignToFormPageId;
    }

    public void setAssignToFormPageId(long assignToFormPageId) {
        this.assignToFormPageId = assignToFormPageId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setMatchErrorScannedPageNumber(int pageNumber) {
        this.matchErrorScannedPageNumber = pageNumber;
    }

    public int getMatchErrorScannedPageNumber() {
        return matchErrorScannedPageNumber;
    }

}
