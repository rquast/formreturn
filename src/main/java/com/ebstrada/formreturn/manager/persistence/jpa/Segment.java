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
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Version;

@Entity public class Segment implements Serializable {
    @Id @Column(name = "SEGMENT_ID") @GeneratedValue(strategy = IDENTITY) private long segmentId;

    @Version @Column(name = "VERSION") private int version;

    @Column(name = "BARCODE_ONE") private String barcodeOne;

    @Column(name = "BARCODE_TWO") private String barcodeTwo;

    @Column(name = "NAME") private String name;

    @Column(name = "AGGREGATE_MARK") private double aggregateMark;

    @ManyToOne @JoinColumn(name = "FORM_PAGE_ID") private FormPage formPageId;

    @OneToMany(mappedBy = "segmentId", cascade = REMOVE) private List<FragmentOcr>
        fragmentOcrCollection;

    @OneToMany(mappedBy = "segmentId", cascade = REMOVE) @OrderBy("orderIndex ASC")
    private List<FragmentOmr> fragmentOmrCollection;

    @OneToMany(mappedBy = "segmentId", cascade = REMOVE) private List<FragmentImageZone>
        fragmentImageZoneCollection;

    @OneToMany(mappedBy = "segmentId", cascade = REMOVE) @OrderBy("orderIndex ASC")
    private List<FragmentBarcode> fragmentBarcodeCollection;

    private static final long serialVersionUID = 1L;

    public Segment() {
        super();
    }

    public long getSegmentId() {
        return this.segmentId;
    }

    public void setSegmentId(long segmentId) {
        this.segmentId = segmentId;
    }

    public String getBarcodeOne() {
        return this.barcodeOne;
    }

    public void setBarcodeOne(String barcodeOne) {
        this.barcodeOne = barcodeOne;
    }

    public String getBarcodeTwo() {
        return this.barcodeTwo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBarcodeTwo(String barcodeTwo) {
        this.barcodeTwo = barcodeTwo;
    }

    public double getAggregateMark() {
        return aggregateMark;
    }

    public void setAggregateMark(double aggregateMark) {
        this.aggregateMark = aggregateMark;
    }

    public FormPage getFormPageId() {
        return this.formPageId;
    }

    public void setFormPageId(FormPage formPageId) {
        this.formPageId = formPageId;
    }

    public List<FragmentOcr> getFragmentOcrCollection() {
        return this.fragmentOcrCollection;
    }

    public void setFragmentOcrCollection(List<FragmentOcr> fragmentOcrCollection) {
        this.fragmentOcrCollection = fragmentOcrCollection;
    }

    public List<FragmentOmr> getFragmentOmrCollection() {
        return this.fragmentOmrCollection;
    }

    public void setFragmentOmrCollection(List<FragmentOmr> fragmentOmrCollection) {
        this.fragmentOmrCollection = fragmentOmrCollection;
    }

    public List<FragmentImageZone> getFragmentImageZoneCollection() {
        return this.fragmentImageZoneCollection;
    }

    public void setFragmentImageZoneCollection(
        List<FragmentImageZone> fragmentImageZoneCollection) {
        this.fragmentImageZoneCollection = fragmentImageZoneCollection;
    }

    public List<FragmentBarcode> getFragmentBarcodeCollection() {
        return fragmentBarcodeCollection;
    }

    public void setFragmentBarcodeCollection(List<FragmentBarcode> fragmentBarcodeCollection) {
        this.fragmentBarcodeCollection = fragmentBarcodeCollection;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

}
