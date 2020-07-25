package com.ebstrada.formreturn.manager.persistence.jpa;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.openjpa.persistence.jdbc.Index;

@Entity @Table(name = "FRAGMENT_BARCODE") public class FragmentBarcode implements Serializable {
    @Id @Column(name = "FRAGMENT_BARCODE_ID") @GeneratedValue(strategy = IDENTITY) private long
        fragmentOmrId;

    @Version @Column(name = "VERSION") private int version;

    @Column(name = "X2_PERCENT") private double x2Percent;

    @Column(name = "CAPTURED_DATA_FIELD_NAME") private String capturedDataFieldName;

    @Column(name = "X1_PERCENT") private double x1Percent;

    @Column(name = "Y1_PERCENT") private double y1Percent;

    @Column(name = "Y2_PERCENT") private double y2Percent;

    @Index(name = "IDX_FRAGMENT_BARCODE_ORDER_INDEX") @Column(name = "ORDER_INDEX") private long
        orderIndex;

    @Column(name = "BARCODE_VALUE") private String barcodeValue;

    @Column(name = "RECONCILIATION_KEY") private short reconciliationKey;

    @Column(name = "BARCODE_TYPE") private short barcodeType;

    @ManyToOne @JoinColumn(name = "SEGMENT_ID") private Segment segmentId;

    private static final long serialVersionUID = 1L;

    public FragmentBarcode() {
        super();
    }

    public long getFragmentOmrId() {
        return this.fragmentOmrId;
    }

    public void setFragmentOmrId(long fragmentOmrId) {
        this.fragmentOmrId = fragmentOmrId;
    }

    public double getX2Percent() {
        return this.x2Percent;
    }

    public void setX2Percent(double x2Percent) {
        this.x2Percent = x2Percent;
    }

    public String getCapturedDataFieldName() {
        return this.capturedDataFieldName;
    }

    public void setCapturedDataFieldName(String capturedDataFieldName) {
        this.capturedDataFieldName = capturedDataFieldName;
    }

    public double getX1Percent() {
        return this.x1Percent;
    }

    public void setX1Percent(double x1Percent) {
        this.x1Percent = x1Percent;
    }

    public double getY1Percent() {
        return this.y1Percent;
    }

    public void setY1Percent(double y1Percent) {
        this.y1Percent = y1Percent;
    }

    public double getY2Percent() {
        return this.y2Percent;
    }

    public void setY2Percent(double y2Percent) {
        this.y2Percent = y2Percent;
    }

    public Segment getSegmentId() {
        return this.segmentId;
    }

    public void setSegmentId(Segment segmentId) {
        this.segmentId = segmentId;
    }

    public void setReconciliationKey(short reconciliationKey) {
        this.reconciliationKey = reconciliationKey;
    }

    public short getReconciliationKey() {
        return reconciliationKey;
    }

    public long getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(long orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getBarcodeValue() {
        return barcodeValue;
    }

    public void setBarcodeValue(String barcodeValue) {
        this.barcodeValue = barcodeValue;
    }

    public short getBarcodeType() {
        return barcodeType;
    }

    public void setBarcodeType(short barcodeType) {
        this.barcodeType = barcodeType;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

}
