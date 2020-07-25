package com.ebstrada.formreturn.manager.util.image;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import cern.colt.function.IntIntProcedure;
import cern.colt.map.OpenIntIntHashMap;

import com.ebstrada.blobextractor.ComponentLabeler;

public class ContourBlobExtraction implements BlobExtractor {

    private int rows = 0;

    private int columns = 0;

    private int width;

    private int length;

    private boolean debug;

    private ComponentLabeler componentLabeler;

    private ArrayList<BubbleDetection> bubbleDetections = new ArrayList<BubbleDetection>();

    private int[] labelIndexArray;

    private BufferedImage binarizedImageBufferedImage;

    private int[] bubbleDetectionIndexes;

    private int[] labelFirstParseRegionIndexes;

    private BufferedImage labelFirstParseImage;

    private BufferedImage bubbleDetectionImage;

    public ContourBlobExtraction(boolean[] src, int width, int rows, int columns, boolean debug) {
        this.width = width;
        this.length = src.length;
        this.rows = rows;
        this.columns = columns;
        this.debug = debug;
        this.componentLabeler =
            new ComponentLabeler(src, width, true, true, getMaximumBoxAreaPercentage());

        if (debug) {
            binarizedImageBufferedImage = ImageUtil.getBufferedImage(src, width);
        }

        process();
    }

    private double getMaximumBoxAreaPercentage() {
        return 100.d / (double) (this.rows * this.columns);
    }

    private void process() {
        this.componentLabeler.parse();
        for (int i = 0; i <= this.componentLabeler.getAreas().size(); i++) {
            bubbleDetections.add(
                new BubbleDetection(this.componentLabeler.getPixelPositionLabels(), i, width,
                    length));
        }
        detectBubbles();
        createLabelArray();
    }

    private void detectBubbles() {

        OpenIntIntHashMap pixelPositionLabels = this.componentLabeler.getPixelPositionLabels();

        // calculate pixel count and min max of dimension
        pixelPositionLabels.forEachPair(new IntIntProcedure() {
            public boolean apply(int position, int label) {
                BubbleDetection bubbleDetection = bubbleDetections.get(label);
                int y = (position / width);
                int x = (position % width);
                bubbleDetection.addPoint(x, y);
                return true;
            }
        });

        ArrayList<Integer> removeRegions = new ArrayList<Integer>();

        // calculate intersecting lines and enclosed white pixels
        for (BubbleDetection bubbleDetection : bubbleDetections) {
            if (bubbleDetection.getPixelCount() == 0) {
                continue;
            }
            bubbleDetection.setDebug(this.debug);
            try {
                bubbleDetection.process();
            } catch (Exception ex) {
                removeRegions.add(bubbleDetection.getLabel());
            }
        }

        int checkboxArea = this.length / (this.rows * this.columns);

        for (int i = 0; i < bubbleDetections.size(); i++) {

            BubbleDetection bubbleDetection = bubbleDetections.get(i);

            if (removeRegions.size() > 0 && removeRegions.contains(bubbleDetection.getLabel())) {

                // remove bubble
                bubbleDetections.set(bubbleDetection.getLabel(),
                    new BubbleDetection(pixelPositionLabels, 0, this.width, this.length));
                continue;

            }

            if (bubbleDetection.getPixelCount() == 0) {
                continue;
            }

            Rectangle2D labelRectangle = bubbleDetection.getRectangle();
            int rectangularArea = (int) (labelRectangle.getWidth() * labelRectangle.getHeight());

            // also remove anything that is a line (vertical or horizontal)
            // the proportions of a line would be 1/10th or less of the width is considered a line
            double max = 0;
            double min = 0;
            if (labelRectangle.getWidth() > labelRectangle.getHeight()) {
                max = labelRectangle.getWidth();
                min = labelRectangle.getHeight();
            } else {
                max = labelRectangle.getHeight();
                min = labelRectangle.getWidth();
            }

            // also, check that the enclosed pixels is at least 80 percent of the total number of pixels
            double enclosedPixelCount = bubbleDetection.getEnclosedPixelCount();
            double pixelCount = bubbleDetection.getPixelCount();

            if (((checkboxArea * 1.25) < rectangularArea) || ((max / min) > 10) || (
                enclosedPixelCount < (pixelCount * 0.75))) {

                // remove bubble
                bubbleDetections.set(bubbleDetection.getLabel(),
                    new BubbleDetection(pixelPositionLabels, 0, this.width, this.length));

            }

        }

    }

    public void createLabelArray() {

        this.labelIndexArray = new int[this.length];
        final OpenIntIntHashMap pixelPositionLabels =
            this.componentLabeler.getPixelPositionLabels();

        pixelPositionLabels.forEachPair(new IntIntProcedure() {
            public boolean apply(int position, int label) {
                int currentRegion = pixelPositionLabels.get(position);
                labelIndexArray[position] = currentRegion;
                return true;
            }
        });

        // first parse output
        if (debug) {
            boolean[] output = new boolean[this.length];
            labelFirstParseRegionIndexes = new int[this.length];
            for (int i = 0; i < this.length; i++) {
                if (pixelPositionLabels.containsKey(i)) {
                    output[i] = BLACK;
                    labelFirstParseRegionIndexes[i] = pixelPositionLabels.get(i);
                } else {
                    output[i] = WHITE;
                    labelFirstParseRegionIndexes[i] = 0;
                }
            }
            labelFirstParseImage = ImageUtil.getBufferedImage(output, width);
        }

        // bubble detection output
        if (debug) {
            boolean[] output = new boolean[this.length];
            bubbleDetectionIndexes = new int[this.length];
            for (BubbleDetection bubbleDetection : getBubbleDetections()) {
                int[] outputArray = bubbleDetection.getOutputArray();
                if (outputArray == null) {
                    continue;
                }
                for (int i = 0; i < outputArray.length; i++) {
                    if (output[i] == WHITE) {
                        if (outputArray[i] > 0) {
                            output[i] = BLACK;
                            bubbleDetectionIndexes[i] = outputArray[i];
                        } else {
                            output[i] = WHITE;
                            bubbleDetectionIndexes[i] = 0;
                        }
                    }
                }
            }
            bubbleDetectionImage = ImageUtil.getRGBBufferedImage(output, width);
        }


    }

    @Override public BufferedImage getBinarizedImage() {
        return binarizedImageBufferedImage;
    }

    @Override public BufferedImage getFirstParseImage() {
        return labelFirstParseImage;
    }

    @Override public int[] getFirstParseLabels() {
        return labelFirstParseRegionIndexes;
    }

    @Override public BufferedImage getSecondParseImage() {
        return null;
    }

    @Override public int[] getSecondParseLabels() {
        return null;
    }

    @Override public BufferedImage getCombinedImage() {
        return null;
    }

    @Override public int[] getCombinedLabels() {
        return null;
    }

    @Override public BufferedImage getBubbleDetectionImage() {
        return bubbleDetectionImage;
    }

    @Override public int[] getBubbleDetectionLabels() {
        return bubbleDetectionIndexes;
    }

    @Override public ArrayList<BubbleDetection> getBubbleDetections() {
        return this.bubbleDetections;
    }

    @Override public int[] getLabelIndexArray() {
        return this.labelIndexArray;
    }

    @Override public BubbleDetection getBubbleDetection(int label) {
        return this.bubbleDetections.get(label);
    }

}
