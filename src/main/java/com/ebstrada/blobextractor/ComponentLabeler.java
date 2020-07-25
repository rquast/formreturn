package com.ebstrada.blobextractor;

import java.awt.Rectangle;
import java.util.ArrayList;

import cern.colt.map.OpenIntIntHashMap;

import com.ebstrada.blobextractor.constants.BinarizedPixel;

/*
 * Component Labeler
 *
 * Adapted from the paper by Fu Chang et al:
 * "A Linear-Time Component-Labeling Algorithm Using Contour Tracing Technique"
 */


public class ComponentLabeler {

    public static final boolean BLACK = BinarizedPixel.BLACK;
    public static final boolean WHITE = BinarizedPixel.WHITE;

    public static final int[] DIRECTIONS = new int[] {0, 1, 2, 3, 4, 5, 6, 7};

    public static final int ISOLATED = 8;

    private ArrayList<Rectangle> areas = new ArrayList<Rectangle>();

    private boolean[] binarizedImage;

    private int[] labelIndexArray;

    private OpenIntIntHashMap pixelPositionLabels = new OpenIntIntHashMap();

    private int width;

    private int height;

    private double maximumBoxAreaPercentage;

    // current label value
    private int c = 1;

    // current position
    private int p = 0;

    // sideways counter
    private int sidewaysCounter = 0;

    // read the image sideways?
    private boolean readSideways = false;

    // equalize internal labels?
    private boolean equalizeInternalLabels = false;

    public ComponentLabeler(boolean[] binarizedImage, int width, boolean readSideways,
        boolean equalizeInternalLabels, double maximumBoxAreaPercentage) {
        this.readSideways = readSideways;
        this.equalizeInternalLabels = equalizeInternalLabels;
        this.binarizedImage = binarizedImage;
        this.width = width;
        this.maximumBoxAreaPercentage = maximumBoxAreaPercentage;
        addWhiteRowToTopAndBottom();
        addPaddingLeftAndRight();
        this.labelIndexArray = new int[this.binarizedImage.length];
        this.height = this.binarizedImage.length / this.width;
    }

    public void addWhiteRowToTopAndBottom() {
        boolean[] dest = new boolean[this.binarizedImage.length + (this.width * 2)];
        System.arraycopy(binarizedImage, 0, dest, width, this.binarizedImage.length);
        this.binarizedImage = dest;
    }

    public void addPaddingLeftAndRight() {
        int height = this.binarizedImage.length / this.width;
        int newWidth = this.width + 2;
        boolean[] dest = new boolean[this.binarizedImage.length + (2 * height)];
        for (int y = 0; y < height; y++) {
            System
                .arraycopy(this.binarizedImage, (y * width), dest, (y * newWidth) + 1, this.width);
        }
        this.binarizedImage = dest;
        this.width = newWidth;
    }

    public void parse() {
        int length = this.binarizedImage.length;
        while (this.p < length) {
            incrementP();
        }
    }

    private void setLabel(int position, int label) {

        int adjustedPosition = 0;
        int y = position / this.width;

        if (this.equalizeInternalLabels) {

            int x = position % this.width;

            Rectangle rect = null;

            if ((label - 1) >= this.areas.size()) {
                rect = new Rectangle(Integer.MAX_VALUE, Integer.MAX_VALUE, 1, 1);
                this.areas.add(rect);
            } else {
                rect = this.areas.get(label - 1);
            }

            if (x < rect.x) {
                rect.x = x;
            }
            if (y < rect.y) {
                rect.y = y;
            }
            rect.add(x, y);

        }
        this.labelIndexArray[position] = label;

        adjustedPosition = position - this.width - ((y - 1) * 2) - 1;
        this.pixelPositionLabels.put(adjustedPosition, label);
    }

    private boolean isValidRect(Rectangle rect) {

        double rectArea = rect.width * rect.height;

        double imageArea = this.binarizedImage.length;

        double maximumBoxArea = (this.maximumBoxAreaPercentage / 100) * imageArea;

        if (rectArea <= maximumBoxArea) {
            return true;
        } else {
            return false;
        }

    }

    private void incrementP() {

        if (isBlack(this.p)) {

            boolean isContourPoint = false;

            if (isUnlabeled(this.p) && isWhiteAboveP()) {

                if (this.equalizeInternalLabels) {
                    int x = this.p % this.width;
                    int y = this.p / this.width;

                    boolean found = false;

                    for (int i = 0; i < this.areas.size(); i++) {
                        Rectangle rect = this.areas.get(i);
                        if (isValidRect(rect) && rect.contains(x, y)) {
                            this.c = (i + 1);
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        this.c = this.areas.size() + 1;
                    }

                }

                int lastDirection = 5; // 7 is the start direction for an external contour
                setLabel(this.p, c);
                traceContour(this.p, lastDirection, this.labelIndexArray[this.p]);
                this.c++;
                isContourPoint = true;
            }

            if (isWhiteBelowP()) {
                int lastDirection = 1; // 3 is the start direction for an internal contour
                if (this.labelIndexArray[this.p] == 0) {
                    assignLeftNeighborLabelToPosition(this.p);
                }
                traceContour(this.p, lastDirection, this.labelIndexArray[this.p]);
                isContourPoint = true;
            }

            if (!isContourPoint) {
                assignLeftNeighborLabelToPosition(this.p);
            }

        }

        if (this.readSideways) {
            int y = this.sidewaysCounter % this.height;
            int x = this.sidewaysCounter / this.height;
            this.p = (y * this.width) + x + 1;
            this.sidewaysCounter++;
        } else {
            this.p++;
        }

    }

    private void assignLeftNeighborLabelToPosition(int position) {
        if (position >= 0 && position < this.labelIndexArray.length) {
            if (this.labelIndexArray[position - 1] > 0) {
                setLabel(position, this.labelIndexArray[position - 1]);
            }
        }
    }

    private void traceContour(int position, int direction, int label) {

        int startPosition = position;
        int secondPosition = -1;
        boolean tracing = true;

        while (tracing) {

            direction = traceDirection((direction + 2) % 8, position, label);

            if (direction == ISOLATED) {
                tracing = false;
                break;
            }

            int nextPosition = getNextPosition(position, direction);

            if (secondPosition == -1) {
                secondPosition = nextPosition;
            } else {
                if (position == startPosition && nextPosition == secondPosition) {
                    tracing = false;
                    break;
                }
            }

            position = nextPosition;

            direction = (direction + 4) % 8;

        }

    }

    /*
     * +-+-+-+
     * |5|6|7|
     * +-+-+-+
     * |4|P|0|
     * +-+-+-+
     * |3|2|1|
     * +-+-+-+
     */
    private int traceDirection(int startDirection, int position, int label) {

        int end = startDirection + 8;

        for (int i = startDirection; i < end; i++) {

            int lookDirection = i % 8;
            int lookPosition = getNextPosition(position, lookDirection);

            if (lookPosition < this.binarizedImage.length && lookPosition >= 0) {
                if (isBlack(lookPosition)) {
                    setLabel(lookPosition, label); // found an unlabelled component, label it
                    return lookDirection;
                } else {
                    // mark white (with negative number to show that it has already been visited)
                    this.labelIndexArray[lookPosition] = -1;
                }
            }

        }

        return ISOLATED;

    }

    private int getNextPosition(int position, int direction) {
        switch (direction) {
            case 0:
                return position + 1;
            case 1:
                return position + this.width + 1;
            case 2:
                return position + this.width;
            case 3:
                return position + this.width - 1;
            case 4:
                return position - 1;
            case 5:
                return position - this.width - 1;
            case 6:
                return position - this.width;
            case 7:
                return position - this.width + 1;
            default:
                return -1;
        }
    }

    private boolean isBlack(int position) {
        if (this.binarizedImage[position] == BLACK) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isUnlabeled(int position) {
        if (position >= 0 && position < this.labelIndexArray.length) {
            if (this.labelIndexArray[position] == 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean isWhiteAboveP() {
        int aboveP = this.p - this.width;
        if (aboveP >= 0) {
            if (this.binarizedImage[aboveP] == WHITE) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private boolean isWhiteBelowP() {
        int belowP = this.p + this.width;
        if (belowP >= 0 && belowP < this.labelIndexArray.length) {
            if (this.labelIndexArray[belowP] == 0 && this.binarizedImage[belowP] == WHITE) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public OpenIntIntHashMap getPixelPositionLabels() {
        return pixelPositionLabels;
    }

    public ArrayList<Rectangle> getAreas() {
        return areas;
    }

}
