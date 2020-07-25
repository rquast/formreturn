package com.ebstrada.formreturn.manager.util.preferences.persistence;

import com.ebstrada.formreturn.manager.util.NoObfuscation;

public class ScannerPreferences implements NoObfuscation {

    transient public static final int USE_FORMRETURN_SCANNING_SOFTWARE = 0;
    transient public static final int USE_VENDOR_SCANNING_SOFTWARE = 1;

    private int scanningSoftware = USE_FORMRETURN_SCANNING_SOFTWARE;

    transient public static final int MEMORY_TRANSFER_MODE = 0;
    transient public static final int NATIVE_TRANSFER_MODE = 1;
    transient public static final int FILE_TRANSFER_MODE = 2;

    private int transferMode = MEMORY_TRANSFER_MODE;

    private int resolution = 200;

    transient public static final int BLACK_AND_WHITE = 0;
    transient public static final int GRAYSCALE = 1;
    transient public static final int COLOR = 2;

    private int colorMode = BLACK_AND_WHITE;

    private int pageSize = -1; // if less than 0, show set dialog

    transient public static final int SINGLE_PAGE = 0;
    transient public static final int DUPLEX = 1;

    private int scanSides = SINGLE_PAGE;

    private int blackThreshold = -1;

    private boolean useDefaultBlackThreshold = true;
    private String documentType;

    public int getScanningSoftware() {
        return scanningSoftware;
    }

    public void setScanningSoftware(int scanningSoftware) {
        this.scanningSoftware = scanningSoftware;
    }

    public int getTransferMode() {
        return transferMode;
    }

    public void setTransferMode(int transferMode) {
        this.transferMode = transferMode;
    }

    public int getResolution() {
        return resolution;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    public int getColorMode() {
        return colorMode;
    }

    public void setColorMode(int colorMode) {
        this.colorMode = colorMode;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getScanSides() {
        return scanSides;
    }

    public void setScanSides(int scanSides) {
        this.scanSides = scanSides;
    }

    public int getBlackThreshold() {
        return blackThreshold;
    }

    public void setBlackThreshold(int blackThreshold) {
        this.blackThreshold = blackThreshold;
    }

    public boolean isUseDefaultBlackThreshold() {
        return useDefaultBlackThreshold;
    }

    public void setUseDefaultBlackThreshold(boolean useDefaultBlackThreshold) {
        this.useDefaultBlackThreshold = useDefaultBlackThreshold;
    }

    public String getDocumentType() {
        return this.documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }



}
