package com.ebstrada.formreturn.manager.logic.recognition.structure;

import com.ebstrada.formreturn.manager.util.NoObfuscation;

public class PublicationRecognitionStructure implements NoObfuscation {

    private double deskewThreshold = 1.05;
    private int luminanceCutOff = 200;
    private int markThreshold = 40;
    private int fragmentPadding = 1;

    private boolean performDeskew = true;

    public double getDeskewThreshold() {
        return deskewThreshold;
    }

    public void setDeskewThreshold(double deskewThreshold) {
        this.deskewThreshold = deskewThreshold;
    }

    public int getLuminanceCutOff() {
        return luminanceCutOff;
    }

    public void setLuminanceCutOff(int luminanceCutOff) {
        this.luminanceCutOff = luminanceCutOff;
    }

    public int getMarkThreshold() {
        return markThreshold;
    }

    public void setMarkThreshold(int markThreshold) {
        this.markThreshold = markThreshold;
    }

    public int getFragmentPadding() {
        return fragmentPadding;
    }

    public void setFragmentPadding(int fragmentPadding) {
        this.fragmentPadding = fragmentPadding;
    }

    public boolean isPerformDeskew() {
        return performDeskew;
    }

    public void setPerformDeskew(boolean performDeskew) {
        this.performDeskew = performDeskew;
    }

}
