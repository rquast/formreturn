package com.ebstrada.formreturn.manager.logic.recognition.structure;

import java.awt.geom.Rectangle2D;

public abstract class FragmentRecognitionStructure {
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    private double percentX1;
    private double percentX2;
    private double percentY1;
    private double percentY2;

    private String fieldName;
    private String markFieldName;
    private int orderIndex;
    private int markOrderIndex;

    public void calculateX(int segmentWidth) {
        percentX1 = ((double) x / (double) segmentWidth) * 100.0d;
        percentX2 = ((double) (x + width) / (double) segmentWidth) * 100.0d;
    }

    public void calculateY(int segmentHeight) {
        percentY1 = ((double) y / (double) segmentHeight) * 100.0d;
        percentY2 = ((double) (y + height) / (double) segmentHeight) * 100.0d;
    }

    public Rectangle2D getRecognitionArea(Rectangle2D segmentboundary, int fragmentPadding) {

        double segmentWidth = segmentboundary.getWidth();
        double segmentHeight = segmentboundary.getHeight();

        double oldXPadding = segmentWidth * ((double) fragmentPadding / 100.0d);
        double oldYPadding = segmentHeight * ((double) fragmentPadding / 100.0d);

        double scaledX1 = Math.round(segmentWidth * (percentX1 / 100.0d));
        double scaledX2 = Math.round(segmentWidth * (percentX2 / 100.0d));
        double scaledY1 = Math.round(segmentHeight * (percentY1 / 100.0d));
        double scaledY2 = Math.round(segmentHeight * (percentY2 / 100.0d));

        double fragmentWidth = percentX2 - percentX1;
        double fragmentHeight = percentY2 - percentY1;

        double padding = 0.0d;

        if (fragmentWidth > fragmentHeight) {
            padding = (fragmentWidth * ((double) fragmentPadding / 2.0d)) / 2.0d;
        } else {
            padding = (fragmentHeight * ((double) fragmentPadding / 2.0d)) / 2.0d;
        }

        if (padding > oldXPadding || padding > oldYPadding) {
            return new Rectangle2D.Double(Math.round(segmentboundary.getX() + scaledX1 - padding),
                Math.round(segmentboundary.getY() + scaledY1 - padding),
                Math.round(scaledX2 - scaledX1 + (padding * 2.0d)),
                Math.round(scaledY2 - scaledY1 + (padding * 2.0d)));
        } else {
            scaledX1 = Math.round(segmentWidth * ((percentX1 - (double) fragmentPadding) / 100.0d));
            scaledX2 = Math.round(segmentWidth * ((percentX2 + (double) fragmentPadding) / 100.0d));
            scaledY1 =
                Math.round(segmentHeight * ((percentY1 - (double) fragmentPadding) / 100.0d));
            scaledY2 =
                Math.round(segmentHeight * ((percentY2 + (double) fragmentPadding) / 100.0d));

            return new Rectangle2D.Double(segmentboundary.getX() + scaledX1,
                segmentboundary.getY() + scaledY1, scaledX2 - scaledX1, scaledY2 - scaledY1);
        }

    }

    /**
     * @deprecated
     */
    public Rectangle2D oldGetRecognitionArea(Rectangle2D segmentboundary, int fragmentPadding) {

        // This way worked differently. It used the segment size rather
        // than the fragment to calculate percentage padding. Not good for
        // small segments or large fragments.

        double segmentWidth = segmentboundary.getWidth();
        double segmentHeight = segmentboundary.getHeight();
        double scaledX1 =
            Math.round(segmentWidth * ((percentX1 - (double) fragmentPadding) / 100.0d));
        double scaledX2 =
            Math.round(segmentWidth * ((percentX2 + (double) fragmentPadding) / 100.0d));
        double scaledY1 =
            Math.round(segmentHeight * ((percentY1 - (double) fragmentPadding) / 100.0d));
        double scaledY2 =
            Math.round(segmentHeight * ((percentY2 + (double) fragmentPadding) / 100.0d));

        return new Rectangle2D.Double(segmentboundary.getX() + scaledX1,
            segmentboundary.getY() + scaledY1, scaledX2 - scaledX1, scaledY2 - scaledY1);

    }

    public void setX(int _x) {
        x = _x;
    }

    public void setY(int _y) {
        y = _y;
    }

    public void setWidth(int _width) {
        width = _width;
    }

    public void setHeight(int _height) {
        height = _height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public double getPercentX1() {
        return percentX1;
    }

    public void setPercentX1(double percentX1) {
        this.percentX1 = percentX1;
    }

    public double getPercentX2() {
        return percentX2;
    }

    public void setPercentX2(double percentX2) {
        this.percentX2 = percentX2;
    }

    public double getPercentY1() {
        return percentY1;
    }

    public void setPercentY1(double percentY1) {
        this.percentY1 = percentY1;
    }

    public double getPercentY2() {
        return percentY2;
    }

    public void setPercentY2(double percentY2) {
        this.percentY2 = percentY2;
    }

    public String getMarkFieldName() {
        return markFieldName;
    }

    public void setMarkFieldName(String markFieldName) {
        this.markFieldName = markFieldName;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public int getMarkOrderIndex() {
        return markOrderIndex;
    }

    public void setMarkOrderIndex(int markOrderIndex) {
        this.markOrderIndex = markOrderIndex;
    }

}
