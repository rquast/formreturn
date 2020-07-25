package com.ebstrada.formreturn.manager.logic.recognition.structure;

import java.awt.geom.Rectangle2D;

import com.ebstrada.formreturn.manager.util.image.BubbleDetection;

public class OMRBox {

    private int regionIndex;

    private String value;

    private BubbleDetection bubbleDetection;

    private int detectionPosition;

    private int centroidDetectionDistance;

    private boolean marked = false;

    public OMRBox(int regionIndex, BubbleDetection bubbleDetection) {
        this.regionIndex = regionIndex;
        this.bubbleDetection = bubbleDetection;
    }

    public Rectangle2D getRectangle() {
        return bubbleDetection.getRectangle();
    }

    public int getRegionIndex() {
        return regionIndex;
    }

    public void setSilhouetteIndex(int silhouetteIndex) {
        this.regionIndex = silhouetteIndex;
    }

    public int getPixelCount() {
        return bubbleDetection.getPixelCount();
    }

    public int getWhiteCount() {
        return bubbleDetection.getWhiteCount();
    }

    public int getCenterX() {
        return bubbleDetection.getCenterX();
    }

    public int getCenterY() {
        return bubbleDetection.getCenterY();
    }

    public int getMinX() {
        return bubbleDetection.getMinX();
    }

    public int getMinY() {
        return bubbleDetection.getMinY();
    }

    public int getMaxX() {
        return bubbleDetection.getMaxX();
    }

    public int getMaxY() {
        return bubbleDetection.getMaxY();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getAreaPixelCount() {

        int width = bubbleDetection.getMaxX() - bubbleDetection.getMinX();
        int height = bubbleDetection.getMaxY() - bubbleDetection.getMinY();
        int area = width * height;

        return area;
    }

    public int getDetectionPosition() {
        return detectionPosition;
    }

    public void setDetectionPosition(int detectionPosition) {
        this.detectionPosition = detectionPosition;
    }

    public int getCentroidDetectionDistance() {
        return centroidDetectionDistance;
    }

    public void setCentroidDetectionDistance(int centroidDetectionDistance) {
        this.centroidDetectionDistance = centroidDetectionDistance;
    }

    public int getEnclosedPixelCount() {
        return bubbleDetection.getEnclosedPixelCount();
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public boolean isMarked() {
        return marked;
    }

}
