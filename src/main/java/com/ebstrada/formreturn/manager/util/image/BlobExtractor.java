package com.ebstrada.formreturn.manager.util.image;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public interface BlobExtractor {

    public static final boolean BLACK = true;
    public static final boolean WHITE = false;
    public static final int SOUTHWEST = 0;
    public static final int WEST = 1;
    public static final int NORTHWEST = 2;
    public static final int NORTH = 3;

    public abstract ArrayList<BubbleDetection> getBubbleDetections();

    public abstract int[] getLabelIndexArray();

    public abstract BubbleDetection getBubbleDetection(int regionIndex);

    public abstract String toString();

    public abstract BufferedImage getBinarizedImage();

    public abstract BufferedImage getFirstParseImage();

    public abstract int[] getFirstParseLabels();

    public abstract BufferedImage getSecondParseImage();

    public abstract int[] getSecondParseLabels();

    public abstract BufferedImage getCombinedImage();

    public abstract int[] getCombinedLabels();

    public abstract BufferedImage getBubbleDetectionImage();

    public abstract int[] getBubbleDetectionLabels();

}