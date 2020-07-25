package com.ebstrada.formreturn.manager.logic.export.image;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.lang.ArrayUtils;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.ebstrada.formreturn.manager.util.graph.SizeAttributes;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("imageExportPreferences") public class ImageExportPreferences
    implements NoObfuscation {

    private SizeAttributes sizeAttributes;
    private Collation collation = Collation.ALL_IMAGES_TOGETHER;

    @XStreamAlias("overlayList") private ArrayList<Overlay> overlay;

    private String file;
    private String imageFilePrefix;
    private int capturedDataColumnCount = 4;
    private float columnFontSize = 8f;
    private int sourceDataColumnCount = 4;
    private boolean rotateImage = true;
    private boolean timestampFilenamePrefix = false;

    public ImageExportPreferences() {
        overlay = new ArrayList<Overlay>();
        overlay.add(Overlay.CAPTURED_DATA);
        overlay.add(Overlay.FORM_SCORE);
        overlay.add(Overlay.GRADES);
        overlay.add(Overlay.INDIVIDUAL_SCORES);
        overlay.add(Overlay.PAGE_SCORE);
        overlay.add(Overlay.SOURCE_DATA);
    }

    public boolean isTimestampFilenamePrefix() {
        return timestampFilenamePrefix;
    }

    public void setTimestampFilenamePrefix(boolean timestampFilenamePrefix) {
        this.timestampFilenamePrefix = timestampFilenamePrefix;
    }

    public void setSizeAttributes(SizeAttributes sizeAttributes) {
        this.sizeAttributes = sizeAttributes;
    }

    public void setCollation(Collation collation) {
        this.collation = collation;
    }

    public void setOverlay(ArrayList<Overlay> overlay) {
        this.overlay = overlay;
    }

    public void setFile(File file) throws IOException {
        if (file != null) {
            this.file = file.getCanonicalPath();
        } else {
            this.file = null;
        }
    }

    public void setImageFilePrefix(String imageFilePrefix) {
        this.imageFilePrefix = imageFilePrefix;
    }

    public Collation getCollation() {
        return this.collation;
    }

    public String getFile() {
        return this.file;
    }

    public String getImageFilePrefix() {
        return this.imageFilePrefix;
    }

    public ArrayList<Overlay> getOverlay() {
        return this.overlay;
    }

    public PDRectangle getPDRectangle() {
        String sizeName = sizeAttributes.getName();
        String[] knownSizeNames = {"LETTER", "A0", "A1", "A2", "A3", "A4", "A5", "A6"};
        PDRectangle[] knownSizePDRectangles =
            {PDRectangle.LETTER, PDRectangle.A0, PDRectangle.A1, PDRectangle.A2, PDRectangle.A3,
                PDRectangle.A4, PDRectangle.A5, PDRectangle.A6};
        if (ArrayUtils.contains(knownSizeNames, sizeName.toUpperCase())) {
            int index = ArrayUtils.indexOf(knownSizeNames, sizeName.toUpperCase());
            return knownSizePDRectangles[index];
        } else {
            PDRectangle pdRectangle =
                new PDRectangle(sizeAttributes.getWidth(), sizeAttributes.getHeight());
            return pdRectangle;
        }
    }

    public void setCapturedDataColumnCount(int capturedDataColumnCount) {
        this.capturedDataColumnCount = capturedDataColumnCount;
    }

    public void setColumnFontSize(float columnFontSize) {
        this.columnFontSize = columnFontSize;
    }

    public void setSourceDataColumnCount(int sourceDataColumnCount) {
        this.sourceDataColumnCount = sourceDataColumnCount;
    }

    public void setRotateImage(boolean rotateImage) {
        this.rotateImage = rotateImage;
    }

    public int getSourceDataColumnCount() {
        if (this.sourceDataColumnCount == 0) {
            this.sourceDataColumnCount = 4;
        }
        return this.sourceDataColumnCount;
    }

    public int getCapturedDataColumnCount() {
        if (this.capturedDataColumnCount == 0) {
            this.capturedDataColumnCount = 4;
        }
        return this.capturedDataColumnCount;
    }

    public boolean isImageRotated() {
        return this.rotateImage;
    }

    public float getColumnFontSize() {
        if (this.columnFontSize == 0f) {
            this.columnFontSize = 8f;
        }
        return columnFontSize;
    }

    public SizeAttributes getSizeAttributes() {
        return this.sizeAttributes;
    }

}
