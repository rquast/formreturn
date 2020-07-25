package com.ebstrada.formreturn.manager.logic.recognition.structure;

import com.ebstrada.formreturn.manager.logic.recognition.reader.BarcodeReaderTypes;
import com.ebstrada.formreturn.manager.persistence.jpa.FragmentBarcode;

public class BarcodeRecognitionStructure extends FragmentRecognitionStructure {

    private int barcodeType = BarcodeReaderTypes.AUTO_DETECT;

    private boolean reconciliationKey = false;

    private String barcodeValue;

    private boolean invalidated = false;

    public BarcodeRecognitionStructure() {
    }

    public BarcodeRecognitionStructure(FragmentBarcode fragmentBarcode) {
        setReconciliationKey(fragmentBarcode.getReconciliationKey() > 0 ? true : false);
        setBarcodeType(fragmentBarcode.getBarcodeType());
        setBarcodeValue(fragmentBarcode.getBarcodeValue());
        setPercentX1(fragmentBarcode.getX1Percent());
        setPercentX2(fragmentBarcode.getX2Percent());
        setPercentY1(fragmentBarcode.getY1Percent());
        setPercentY2(fragmentBarcode.getY2Percent());
        setReconciliationKey((fragmentBarcode.getReconciliationKey() == 1) ? true : false);
        setFieldName(fragmentBarcode.getCapturedDataFieldName());
        setOrderIndex((int) fragmentBarcode.getOrderIndex());
    }

    public boolean isReconciliationKey() {
        return reconciliationKey;
    }

    public void setReconciliationKey(boolean reconciliationKey) {
        this.reconciliationKey = reconciliationKey;
    }

    public int getBarcodeType() {
        return barcodeType;
    }

    public void setBarcodeType(int barcodeType) {
        this.barcodeType = barcodeType;
    }

    public void setBarcodeValue(String barcodeValue) {
        this.barcodeValue = barcodeValue;
    }

    public String getBarcodeValue() {
        return barcodeValue;
    }

    public String toString() {
        return barcodeValue;
    }

    public boolean isInvalidated() {
        return invalidated;
    }

    public void setInvalidated(boolean invalidated) {
        this.invalidated = invalidated;
    }

}
