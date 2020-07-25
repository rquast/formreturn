package com.ebstrada.formreturn.manager.gef.undo.memento;

import com.ebstrada.formreturn.manager.gef.presentation.FigBarcodeReader;
import com.ebstrada.formreturn.manager.gef.undo.Memento;

public class BarcodeReaderMemento extends Memento {

    public FigBarcodeReader figBarcodeReader;

    private String oldFieldname;

    private boolean oldShowText;

    private boolean oldReconciliationKey;

    private int oldFieldnameOrderIndex;

    private int oldCornerDivisor;

    private String oldBarcodeAreaText;

    private boolean oldShowCorners;

    private int oldBarcodeType;

    private String newFieldname;

    private boolean newShowText;

    private boolean newReconciliationKey;

    private int newFieldnameOrderIndex;

    private int newCornerDivisor;

    private String newBarcodeAreaText;

    private boolean newShowCorners;

    private int newBarcodeType;

    public BarcodeReaderMemento(FigBarcodeReader figBarcodeReader) {
        this.figBarcodeReader = figBarcodeReader;
    }

    public void undo() {
        figBarcodeReader.setFieldname(oldFieldname);
        figBarcodeReader.setShowText(oldShowText);
        figBarcodeReader.setReconciliationKey(oldReconciliationKey);
        figBarcodeReader.setFieldnameOrderIndex(oldFieldnameOrderIndex);
        figBarcodeReader.setCornerDivisor(oldCornerDivisor);
        figBarcodeReader.setBarcodeAreaText(oldBarcodeAreaText);
        figBarcodeReader.setShowCorners(oldShowCorners);
        figBarcodeReader.setBarcodeType(oldBarcodeType);
        figBarcodeReader.damage();
        figBarcodeReader.firePropChange("undo", null, null);
    }

    public void redo() {
        figBarcodeReader.setFieldname(newFieldname);
        figBarcodeReader.setShowText(newShowText);
        figBarcodeReader.setReconciliationKey(oldReconciliationKey);
        figBarcodeReader.setFieldnameOrderIndex(newFieldnameOrderIndex);
        figBarcodeReader.setCornerDivisor(newCornerDivisor);
        figBarcodeReader.setBarcodeAreaText(newBarcodeAreaText);
        figBarcodeReader.setShowCorners(newShowCorners);
        figBarcodeReader.setBarcodeType(newBarcodeType);
        figBarcodeReader.damage();
        figBarcodeReader.firePropChange("redo", null, null);
    }

    public void dispose() {
    }

    public String toString() {
        return (isStartChain() ? "*" : " ") + "BarcodeMemento";
    }

    public void setNewFieldname(String newFieldname) {
        this.newFieldname = newFieldname;
    }

    public String getOldFieldname() {
        return oldFieldname;
    }

    public void setOldFieldname(String oldFieldname) {
        this.oldFieldname = oldFieldname;
    }

    public boolean isOldShowText() {
        return oldShowText;
    }

    public void setOldShowText(boolean oldShowText) {
        this.oldShowText = oldShowText;
    }

    public String getNewFieldname() {
        return newFieldname;
    }

    public boolean isNewShowText() {
        return newShowText;
    }

    public void setNewShowText(boolean newShowText) {
        this.newShowText = newShowText;
    }

    public boolean isOldReconciliationKey() {
        return oldReconciliationKey;
    }

    public void setOldReconciliationKey(boolean oldReconciliationKey) {
        this.oldReconciliationKey = oldReconciliationKey;
    }

    public boolean isNewReconciliationKey() {
        return newReconciliationKey;
    }

    public void setNewReconciliationKey(boolean newReconciliationKey) {
        this.newReconciliationKey = newReconciliationKey;
    }

    public int getOldFieldnameOrderIndex() {
        return oldFieldnameOrderIndex;
    }

    public void setOldFieldnameOrderIndex(int oldFieldnameOrderIndex) {
        this.oldFieldnameOrderIndex = oldFieldnameOrderIndex;
    }

    public int getNewFieldnameOrderIndex() {
        return newFieldnameOrderIndex;
    }

    public void setNewFieldnameOrderIndex(int newFieldnameOrderIndex) {
        this.newFieldnameOrderIndex = newFieldnameOrderIndex;
    }

    public int getOldCornerDivisor() {
        return oldCornerDivisor;
    }

    public void setOldCornerDivisor(int oldCornerDivisor) {
        this.oldCornerDivisor = oldCornerDivisor;
    }

    public String getOldBarcodeAreaText() {
        return oldBarcodeAreaText;
    }

    public void setOldBarcodeAreaText(String oldBarcodeAreaText) {
        this.oldBarcodeAreaText = oldBarcodeAreaText;
    }

    public int getNewCornerDivisor() {
        return newCornerDivisor;
    }

    public void setNewCornerDivisor(int newCornerDivisor) {
        this.newCornerDivisor = newCornerDivisor;
    }

    public String getNewBarcodeAreaText() {
        return newBarcodeAreaText;
    }

    public void setNewBarcodeAreaText(String newBarcodeAreaText) {
        this.newBarcodeAreaText = newBarcodeAreaText;
    }

    public boolean isOldShowCorners() {
        return oldShowCorners;
    }

    public void setOldShowCorners(boolean oldShowCorners) {
        this.oldShowCorners = oldShowCorners;
    }

    public int getOldBarcodeType() {
        return oldBarcodeType;
    }

    public void setOldBarcodeType(int oldBarcodeType) {
        this.oldBarcodeType = oldBarcodeType;
    }

    public boolean isNewShowCorners() {
        return newShowCorners;
    }

    public void setNewShowCorners(boolean newShowCorners) {
        this.newShowCorners = newShowCorners;
    }

    public int getNewBarcodeType() {
        return newBarcodeType;
    }

    public void setNewBarcodeType(int newBarcodeType) {
        this.newBarcodeType = newBarcodeType;
    }

}
