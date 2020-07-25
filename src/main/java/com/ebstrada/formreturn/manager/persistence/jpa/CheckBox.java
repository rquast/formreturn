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

@Entity @Table(name = "CHECK_BOX") public class CheckBox implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id @Column(name = "CHECK_BOX_ID") @GeneratedValue(strategy = IDENTITY) private long checkBoxId;

    @Version @Column(name = "VERSION") private int version;

    @ManyToOne @JoinColumn(name = "FRAGMENT_OMR_ID") private FragmentOmr fragmentOmrId;

    @Column(name = "FRAGMENT_X_RATIO") private double fragmentXRatio;

    @Column(name = "FRAGMENT_Y_RATIO") private double fragmentYRatio;

    @Column(name = "CHECK_BOX_VALUE") private String checkBoxValue;

    @Column(name = "CHECK_BOX_MARKED") private short checkBoxMarked;

    @Column(name = "ROW_NUMBER") private short rowNumber;

    @Column(name = "COLUMN_NUMBER") private short columnNumber;

    public CheckBox() {
        super();
    }

    public long getCheckBoxId() {
        return checkBoxId;
    }

    public void setCheckBoxId(long checkBoxId) {
        this.checkBoxId = checkBoxId;
    }

    public String getCheckBoxValue() {
        return checkBoxValue;
    }

    public void setCheckBoxValue(String checkBoxValue) {
        this.checkBoxValue = checkBoxValue;
    }

    public short getCheckBoxMarked() {
        return checkBoxMarked;
    }

    public void setCheckBoxMarked(short checkBoxMarked) {
        this.checkBoxMarked = checkBoxMarked;
    }

    public FragmentOmr getFragmentOmrId() {
        return fragmentOmrId;
    }

    public double getFragmentXRatio() {
        return fragmentXRatio;
    }

    public void setFragmentXRatio(double fragmentXRatio) {
        this.fragmentXRatio = fragmentXRatio;
    }

    public double getFragmentYRatio() {
        return fragmentYRatio;
    }

    public void setFragmentYRatio(double fragmentYRatio) {
        this.fragmentYRatio = fragmentYRatio;
    }

    public void setFragmentOmrId(FragmentOmr fragmentOmrId) {
        this.fragmentOmrId = fragmentOmrId;
    }

    public short getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(short rowNumber) {
        this.rowNumber = rowNumber;
    }

    public short getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(short columnNumber) {
        this.columnNumber = columnNumber;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

}
