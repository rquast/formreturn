package com.ebstrada.formreturn.manager.util.image;

import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import cern.colt.map.OpenIntIntHashMap;

public class BubbleDetection {

    private OpenIntIntHashMap pixelPositionRegionIndexes;
    private int width;
    private int length;
    private ArrayList<Line2D> xLines;
    private ArrayList<Line2D> yLines;

    private ArrayList<Line2D[]> intersectingLines = new ArrayList<Line2D[]>();
    private Point2D centroidPoint;
    private int x1 = Integer.MAX_VALUE;
    private int x2;
    private int y1 = Integer.MAX_VALUE;
    private int y2;

    private int pixelCount = 0;

    private int minX = Integer.MAX_VALUE;
    private int minY = Integer.MAX_VALUE;
    private int maxX = 0;
    private int maxY = 0;

    private int whiteCount;

    private boolean debug = false;
    private int label;

    private int enclosedPixelCount = 0;
    private int[] outputArray;

    public static final int X_DIRECTION = 0;
    public static final int Y_DIRECTION = 1;

    public static final int MIN_POINT = 0;
    public static final int MAX_POINT = 1;


    public BubbleDetection(OpenIntIntHashMap pixelPositionRegionIndexes, int label, int width,
        int length) {
        this.pixelPositionRegionIndexes = pixelPositionRegionIndexes;
        this.label = label;
        this.width = width;
        this.length = length;
    }

    public void process() throws Exception {

        buildXLines();
        buildYLines();
        findIntersectingLines();
        calculateMetadata();

    }

    public void addPoint(int x, int y) {

        if (x < x1) {
            x1 = x;
        }
        if (x > x2) {
            x2 = x;
        }
        if (y < y1) {
            y1 = y;
        }
        if (y > y2) {
            y2 = y;
        }

        ++pixelCount;

    }

    public boolean getValueAtPoint(int x, int y) {
        int linearPoint = (y * width) + x;
        boolean value = false;
        Integer index = pixelPositionRegionIndexes.get(linearPoint);
        if (index == null) {
            value = false;
        } else if (index == label) {
            value = true;
        } else {
            value = false;
        }
        return value;
    }

    public Line2D findMinMaxBlackPoints(int direction, int index) throws Exception {

        Line2D line = new Line2D.Double();

        Integer[] points = new Integer[] {0, 0};

        switch (direction) {

            case X_DIRECTION: // left and right

                // search left
                for (int x = x1; x <= x2; ++x) {
                    if (getValueAtPoint(x, index) == BlobExtractor.BLACK) {
                        points[MIN_POINT] = x;
                        break;
                    }
                }

                // search right
                for (int x = x2; x >= x1; --x) {
                    if (getValueAtPoint(x, index) == BlobExtractor.BLACK) {
                        points[MAX_POINT] = x;
                        break;
                    }
                }

                line.setLine(points[MIN_POINT], index, points[MAX_POINT], index);

                break;

            case Y_DIRECTION: // up and down

                // search up
                for (int y = y1; y <= y2; ++y) {
                    if (getValueAtPoint(index, y) == BlobExtractor.BLACK) {
                        points[MIN_POINT] = y;
                        break;
                    }
                }

                // search down
                for (int y = y2; y >= y1; --y) {
                    if (getValueAtPoint(index, y) == BlobExtractor.BLACK) {
                        points[MAX_POINT] = y;
                        break;
                    }
                }

                line.setLine(index, points[MIN_POINT], index, points[MAX_POINT]);

                break;

        }

        // validate the points
        if ((points[MIN_POINT] == points[MAX_POINT]) || (points[MIN_POINT] > points[MAX_POINT])) {
            throw new Exception();
        }

        return line;

    }

    public void buildXLines() throws Exception {

        xLines = new ArrayList<Line2D>();

        for (int y = y1; y <= y2; ++y) {
            try {
                xLines.add(findMinMaxBlackPoints(X_DIRECTION, y));
            } catch (Exception ex) {
            }
        }

    }

    public void checkXLines(double yCentroid) throws Exception {

        int bubbleWidth = x2 - x1;
        int minLineWidth = (int) ((double) bubbleWidth * 0.3);

        double margin = (double) xLines.size() * 0.25d;
        int minCenterPoint = (int) (yCentroid - margin);
        int maxCenterPoint = (int) (yCentroid + margin);

        for (Line2D xLine : xLines) {

            int lineWidth = (int) (xLine.getX2() - xLine.getX1());

            if (lineWidth < minLineWidth) {
                if (xLine.getY1() > minCenterPoint && xLine.getY1() < maxCenterPoint) {
                    throw new Exception("line removal close to center");
                }
            } else {
                if (xLine.getX1() == 0) {
                    throw new Exception("X touches left");
                }
                if (xLine.getX2() == (width - 1)) {
                    throw new Exception("X touches right");
                }
            }
        }

    }

    public void checkYLines(double xCentroid) throws Exception {

        int height = length / width;

        int bubbleHeight = y2 - y1;
        int minLineHeight = (int) ((double) bubbleHeight * 0.3);

        double margin = (double) yLines.size() * 0.25d;
        int minCenterPoint = (int) (xCentroid - margin);
        int maxCenterPoint = (int) (xCentroid + margin);

        for (Line2D yLine : yLines) {

            int lineHeight = (int) (yLine.getY2() - yLine.getY1());

            if (lineHeight < minLineHeight) {
                if (yLine.getX1() > minCenterPoint && yLine.getX1() < maxCenterPoint) {
                    throw new Exception("line removal close to center");
                }
            } else {
                if (yLine.getY1() == 0) {
                    throw new Exception("Y touches top");
                }
                if (yLine.getY2() == (height - 1)) {
                    throw new Exception("Y touches bottom");
                }
            }

        }

    }

    public void buildYLines() throws Exception {

        yLines = new ArrayList<Line2D>();

        for (int x = x1; x <= x2; ++x) {
            try {
                yLines.add(findMinMaxBlackPoints(Y_DIRECTION, x));
            } catch (Exception ex) {
            }
        }

    }

    public void findIntersectingLines() {

        int minYLineLength = (int) (((double) (y2 - y1)) * 0.15);
        int minXLineLength = (int) (((double) (x2 - x1)) * 0.15);

        for (Line2D xLine : xLines) {

            int xLineLength = (int) (xLine.getX2() - xLine.getX1());

            for (Line2D yLine : yLines) {
                if (xLine.intersectsLine(yLine)) {

                    int yLineLength = (int) (yLine.getY2() - yLine.getY1());

                    // make sure that it meets the threshold requirements - first by line length
                    if (xLineLength <= minXLineLength || yLineLength <= minYLineLength) {
                        continue;
                    }

                    intersectingLines.add(new Line2D[] {xLine, yLine});

                }
            }
        }

    }

    public void calculateMetadata() throws Exception {

        // get the centroid

        int ySum = 0;
        int xSum = 0;
        int divisor = intersectingLines.size();

        if (debug) {
            outputArray = new int[this.length];
        }

        for (Line2D[] lines : intersectingLines) {

            Line2D xLine = lines[X_DIRECTION];
            Line2D yLine = lines[Y_DIRECTION];

            xSum += (int) yLine.getX1();
            ySum += (int) xLine.getY1();

            int x = (int) yLine.getX1();
            int y = (int) xLine.getY1();

            if (x < minX) {
                minX = x;
            }
            if (x > maxX) {
                maxX = x;
            }
            if (y < minY) {
                minY = y;
            }
            if (y > maxY) {
                maxY = y;
            }

            int linearPosition = (y * width) + x;

            if (!(this.pixelPositionRegionIndexes.containsKey(linearPosition))) {
                ++whiteCount;
            }

            ++enclosedPixelCount;

            if (debug) {
                if (pixelPositionRegionIndexes.containsKey(linearPosition)) {
                    outputArray[linearPosition] = pixelPositionRegionIndexes.get(linearPosition);
                }
            }

        }

        double xCentroid = (double) xSum / (double) divisor;
        double yCentroid = (double) ySum / (double) divisor;

        centroidPoint = new Point2D.Double(Math.round(xCentroid), Math.round(yCentroid));

        checkXLines(yCentroid);
        checkYLines(xCentroid);

        if (debug) {
            int linearPosition = (int) ((yCentroid * width) + xCentroid);
            outputArray[linearPosition] = -1;
        }

    }

    public Point2D getCentroidPoint() {
        return centroidPoint;
    }

    public int getWhiteCount() {
        return whiteCount;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public Rectangle2D getRectangle() {
        return new Rectangle(minX, minY, (maxX - minX), (maxY - minY));
    }

    public int getPixelCount() {
        return pixelCount;
    }

    public void setPixelCount(int pixelCount) {
        this.pixelCount = pixelCount;
    }

    public int getMinX() {
        return minX;
    }

    public void setMinX(int minX) {
        this.minX = minX;
    }

    public int getMinY() {
        return minY;
    }

    public void setMinY(int minY) {
        this.minY = minY;
    }

    public int getMaxX() {
        return maxX;
    }

    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    public int getCenterX() {
        return (int) centroidPoint.getX();
    }

    public int getCenterY() {
        return (int) centroidPoint.getY();
    }

    public int getLabel() {
        return label;
    }

    public int getEnclosedPixelCount() {
        return enclosedPixelCount;
    }

    public int[] getOutputArray() {
        return outputArray;
    }



}
