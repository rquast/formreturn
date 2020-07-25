package com.ebstrada.formreturn.manager.persistence.jpa;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity @Table(name = "FRAGMENT_OCR") public class FragmentOcr implements Serializable {
    @Id @Column(name = "FRAGMENT_OCR_ID") @GeneratedValue(strategy = IDENTITY) private long
        fragmentOcrId;

    @Version @Column(name = "VERSION") private int version;

    @Column(name = "X2_PERCENT") private short x2Percent;

    @Column(name = "CAPTURED_DATA_FIELD_NAME") private String capturedDataFieldName;

    @Column(name = "X1_PERCENT") private short x1Percent;

    @Column(name = "Y1_PERCENT") private short y1Percent;

    @Column(name = "Y2_PERCENT") private short y2Percent;

    @Column(name = "CAPTURED_STRING") private String capturedString;

    @Column(name = "ORDER_INDEX") private long orderIndex;

    @ManyToOne @JoinColumn(name = "SEGMENT_ID") private Segment segmentId;

    private static final long serialVersionUID = 1L;

    public FragmentOcr() {
        super();
    }

    public long getFragmentOcrId() {
        return this.fragmentOcrId;
    }

    public void setFragmentOcrId(long fragmentOcrId) {
        this.fragmentOcrId = fragmentOcrId;
    }

    public short getX2Percent() {
        return this.x2Percent;
    }

    public void setX2Percent(short x2Percent) {
        this.x2Percent = x2Percent;
    }

    public String getCapturedDataFieldName() {
        return this.capturedDataFieldName;
    }

    public void setCapturedDataFieldName(String capturedDataFieldName) {
        this.capturedDataFieldName = capturedDataFieldName;
    }

    public short getX1Percent() {
        return this.x1Percent;
    }

    public void setX1Percent(short x1Percent) {
        this.x1Percent = x1Percent;
    }

    public short getY1Percent() {
        return this.y1Percent;
    }

    public void setY1Percent(short y1Percent) {
        this.y1Percent = y1Percent;
    }

    public short getY2Percent() {
        return this.y2Percent;
    }

    public void setY2Percent(short y2Percent) {
        this.y2Percent = y2Percent;
    }

    public Segment getSegmentId() {
        return this.segmentId;
    }

    public void setSegmentId(Segment segmentId) {
        this.segmentId = segmentId;
    }

    public long getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(long orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getCapturedString() {
        return capturedString;
    }

    public void setCapturedString(String capturedString) {
        this.capturedString = capturedString;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

}
