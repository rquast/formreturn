package com.ebstrada.formreturn.manager.util.image;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/*
 * Based on the Blob Extraction algorithm in Wikipedia (Jan 29, 2010).
 */


public class EquivalenceBlobExtraction implements BlobExtractor {

    private int rowCount = 0;
    private int columnCount = 0;

    private boolean debug = false;

    private boolean[] binarizedImage;

    private int[] blobIndexArray;

    private ArrayList<BubbleDetection> bubbleDetections = new ArrayList<BubbleDetection>();

    private int currentRegionIndex = 1;

    private int imageArea;
    private int imageWidth;

    private HashMap<Integer, Integer> pixelPositionRegionIndexes = new HashMap<Integer, Integer>();
    private BufferedImage binarizedImageBufferedImage;
    private BufferedImage labelFirstParseImage;
    private int[] labelFirstParseRegionIndexes;
    private BufferedImage labelSecondParseImage;
    private int[] labelSecondParseRegionIndexes;
    private BufferedImage combineInternalRegionsImage;
    private int[] combineInternalRegionsRegionIndexes;
    private BufferedImage bubbleDetectionImage;
    private int[] bubbleDetectionIndexes;
    private ArrayList<Set<Integer>> equivalentRegionSets = new ArrayList<Set<Integer>>();

    public EquivalenceBlobExtraction(boolean[] binarizedImage, int width, int rowCount,
        int columnCount) {
        this(binarizedImage, width, rowCount, columnCount, false);
    }

    public EquivalenceBlobExtraction(boolean[] binarizedImage, int width, int rowCount,
        int columnCount, boolean debug) {
        this.binarizedImage = binarizedImage;
        this.imageWidth = width;
        this.imageArea = binarizedImage.length;
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.debug = debug;

        if (debug) {
            binarizedImageBufferedImage = ImageUtil.getBufferedImage(binarizedImage, imageWidth);
        }

        process();
    }

    /* (non-Javadoc)
     * @see com.ebstrada.formreturn.manager.util.image.BlobExtractor#createAreaAndBlobIndexArray()
     */
    public void createAreaAndBlobIndexArray() {

        blobIndexArray = new int[this.imageArea];

        // recalculate area array, points and index array
        for (int position : pixelPositionRegionIndexes.keySet()) {
            int currentRegion = pixelPositionRegionIndexes.get(position);
            blobIndexArray[position] = currentRegion;
        }

    }

    /* (non-Javadoc)
     * @see com.ebstrada.formreturn.manager.util.image.BlobExtractor#getBubbleDetections()
     */
    @Override public ArrayList<BubbleDetection> getBubbleDetections() {
        return bubbleDetections;
    }

    /* (non-Javadoc)
     * @see com.ebstrada.formreturn.manager.util.image.BlobExtractor#getBlobIndexArray()
     */
    @Override public int[] getLabelIndexArray() {
        return blobIndexArray;
    }

    /* (non-Javadoc)
     * @see com.ebstrada.formreturn.manager.util.image.BlobExtractor#getBubbleDetection(int)
     */
    @Override public BubbleDetection getBubbleDetection(int regionIndex) {
        return bubbleDetections.get(regionIndex);
    }

    public void process() {

        // add the default region bubbleDetection
        // bubbleDetections.add(new BubbleDetection(pixelPositionRegionIndexes, 0, imageWidth, imageArea));

        int currentPixelPosition = 0;
        int imageHeight = imageArea / imageWidth;

        for (int pos = 0; pos < imageArea; pos++) {

            int y = pos % imageHeight;
            int x = pos / imageHeight;
            currentPixelPosition = (y * imageWidth) + x;

            // skip if the comparator is white
            if (binarizedImage[currentPixelPosition] == WHITE) {
                continue;
            }

            int matchesCriterion = 0;
            Set<Integer> neighboringRegions = new TreeSet<Integer>();

            for (int direction = SOUTHWEST; direction <= NORTH; direction++) {

                int comparisonPixelPosition = 0;

                switch (direction) {
                    case SOUTHWEST:
                        // this prevents a wrapping match issue
                        if (y == (imageHeight - 1)) {
                            break;
                        }
                        comparisonPixelPosition = currentPixelPosition + this.imageWidth - 1;
                        break;
                    case WEST:
                        comparisonPixelPosition = currentPixelPosition - 1;
                        break;
                    case NORTHWEST:
                        // this prevents a wrapping match issue
                        if (y == 0) {
                            break;
                        }
                        comparisonPixelPosition = currentPixelPosition - this.imageWidth - 1;
                        break;
                    case NORTH:
                        // this prevents a wrapping match issue
                        if (y == 0) {
                            break;
                        }
                        comparisonPixelPosition = currentPixelPosition - this.imageWidth;
                        break;
                }

                // skip if comparison pixel position is less than or equal to 0
                if (comparisonPixelPosition <= 0) {
                    continue;
                }

                // skip if the comparator is white
                if (binarizedImage[comparisonPixelPosition] == WHITE) {
                    continue;
                }

                // get neighboring regions
                if (pixelPositionRegionIndexes.containsKey(comparisonPixelPosition)) {
                    neighboringRegions.add(pixelPositionRegionIndexes.get(comparisonPixelPosition));
                    matchesCriterion++;
                }

            }

            // if none of the neighbors fit the criterion...
            if (matchesCriterion == 0) {

                // then assign to region value of the region counter.
                pixelPositionRegionIndexes.put(currentPixelPosition, currentRegionIndex);

                // increment region counter.
                // BubbleDetection bubbleDetection = new BubbleDetection(pixelPositionRegionIndexes, currentRegionIndex, imageWidth, imageArea);
                // bubbleDetections.add(bubbleDetection);
                currentRegionIndex++;

            }

            // if only one neighbor fits the criterion...
            if (matchesCriterion == 1) {

                // assign pixel to that region.
                pixelPositionRegionIndexes
                    .put(currentPixelPosition, neighboringRegions.iterator().next());

            }

            // if multiple neighbors match...
            if (matchesCriterion > 1) {

                // and are all members of the same region...
                if (neighboringRegions.size() <= 1) {

                    // assign pixel to their region.
                    pixelPositionRegionIndexes
                        .put(currentPixelPosition, neighboringRegions.iterator().next());

                } else { // and are members of different regions...

                    // assign pixel to one of the regions (it doesn't matter which one).
                    pixelPositionRegionIndexes
                        .put(currentPixelPosition, neighboringRegions.iterator().next());

                    // indicate that all of these regions are the equivalent.
                    addEquivalentRegions(neighboringRegions);

                }
            }

        }

        // first parse output
        if (debug) {
            boolean[] output = new boolean[this.imageArea];
            labelFirstParseRegionIndexes = new int[this.imageArea];
            for (int i = 0; i < this.imageArea; i++) {
                if (pixelPositionRegionIndexes.get(i) != null) {
                    output[i] = BLACK;
                    labelFirstParseRegionIndexes[i] = pixelPositionRegionIndexes.get(i);
                } else {
                    output[i] = WHITE;
                    labelFirstParseRegionIndexes[i] = 0;
                }
            }
            labelFirstParseImage = ImageUtil.getBufferedImage(output, imageWidth);
        }

        combineEquivalentRegionIndexes();

        // second parse output
        if (debug) {
            boolean[] output = new boolean[this.imageArea];
            labelSecondParseRegionIndexes = new int[this.imageArea];
            for (int i = 0; i < this.imageArea; i++) {
                if (pixelPositionRegionIndexes.get(i) != null) {
                    output[i] = BLACK;
                    labelSecondParseRegionIndexes[i] = pixelPositionRegionIndexes.get(i);
                } else {
                    output[i] = WHITE;
                    labelSecondParseRegionIndexes[i] = 0;
                }
            }
            labelSecondParseImage = ImageUtil.getBufferedImage(output, imageWidth);
        }

        detectBubbles();

        createAreaAndBlobIndexArray();

        // combined output
        if (debug) {
            boolean[] output = new boolean[this.imageArea];
            combineInternalRegionsRegionIndexes = new int[this.imageArea];
            for (int i = 0; i < this.imageArea; i++) {
                if (pixelPositionRegionIndexes.get(i) != null) {
                    output[i] = BLACK;
                    combineInternalRegionsRegionIndexes[i] = pixelPositionRegionIndexes.get(i);
                } else {
                    output[i] = WHITE;
                    combineInternalRegionsRegionIndexes[i] = 0;
                }
            }
            combineInternalRegionsImage = ImageUtil.getBufferedImage(output, imageWidth);
        }

        // bubble detection output
        if (debug) {
            boolean[] output = new boolean[this.imageArea];
            bubbleDetectionIndexes = new int[this.imageArea];
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
            bubbleDetectionImage = ImageUtil.getRGBBufferedImage(output, imageWidth);
        }

    }

    /* (non-Javadoc)
     * @see com.ebstrada.formreturn.manager.util.image.BlobExtractor#toString()
     */
    @Override public String toString() {

        StringBuilder output = new StringBuilder();

        if (blobIndexArray != null && blobIndexArray.length >= this.imageArea) {

            ArrayList<Integer[]> centroids = new ArrayList<Integer[]>();
            for (BubbleDetection bubbleDetection : bubbleDetections) {
                if (bubbleDetection.getPixelCount() == 0) {
                    continue;
                }
                centroids.add(
                    new Integer[] {bubbleDetection.getCenterX(), bubbleDetection.getCenterY()});
            }

            for (int i = 0; i < this.imageArea; i++) {

                if ((i % imageWidth) == 0 && i > 0) {
                    output.append("\n");
                }

                int x = i % imageWidth;
                int y = i / imageWidth;

                boolean isCentroid = false;

                for (Integer[] centroid : centroids) {
                    if (centroid[0] == x && centroid[1] == y) {
                        isCentroid = true;
                    }
                }

                if (isCentroid) {
                    output.append("*");
                } else if (blobIndexArray[i] > 0) {
                    output.append(blobIndexArray[i]);
                } else {
                    output.append("_");
                }

            }

        } else {
            for (int i = 0; i < this.imageArea; i++) {
                if ((i % imageWidth) == 0 && i > 0) {
                    output.append("\n");
                }
                if (pixelPositionRegionIndexes.get(i) != null) {
                    output.append(pixelPositionRegionIndexes.get(i));
                } else {
                    output.append("_");
                }
            }
        }

        output.append("\n");


        if (blobIndexArray != null && blobIndexArray.length >= this.imageArea) {
            for (int i = 1; i < this.bubbleDetections.size(); i++) {
                BubbleDetection bubbleDetection = bubbleDetections.get(i);
                if (bubbleDetection.getPixelCount() > 0) {
                    output.append("Region Number: " + i + " - ");
                    output.append("Pixel Count: " + bubbleDetection.getPixelCount() + " - ");
                    output.append(
                        "Center X,Y: " + bubbleDetection.getCenterX() + "," + bubbleDetection
                            .getCenterY() + " - ");
                    output.append(
                        "Rectangular Bounds: " + bubbleDetection.getRectangle().toString() + "\n");
                }
            }
        }

        return output.toString();
    }

    private void addEquivalentRegions(Set<Integer> neighboringRegions) {

        Iterator<Integer> nri = neighboringRegions.iterator();

        int firstRegion = nri.next();

        while (nri.hasNext()) {

            int nextRegion = nri.next();

            Set<Integer> regionSet = findRegionInAllRegionSets(firstRegion, nextRegion);

            if (regionSet != null) {

                appendToAllRegionSets(regionSet);

            } else {

                // create a new region set
                regionSet = new TreeSet<Integer>();
                regionSet.add(firstRegion);
                regionSet.add(nextRegion);
                appendToAllRegionSets(regionSet);

            }


        }

    }

    private Set<Integer> findRegionInAllRegionSets(int firstRegion, int region) {

        Set<Integer> foundInSetOne = null;
        Set<Integer> foundInSetTwo = null;

        for (Set<Integer> regionSet : equivalentRegionSets) {

            if (regionSet.contains(firstRegion) || regionSet.contains(region)) {
                if (foundInSetOne == null) {
                    foundInSetOne = regionSet;
                } else if (foundInSetTwo == null) {
                    foundInSetTwo = regionSet;
                    break;
                }
            }

        }

        if (foundInSetOne != null && foundInSetTwo != null) {

            mergeRegionSets(foundInSetOne, foundInSetTwo);

            return foundInSetOne;

        }
        if (foundInSetOne == null && foundInSetTwo == null) {
            return null;
        } else {
            return foundInSetOne;
        }

    }

    private void mergeRegionSets(Set<Integer> setOne, Set<Integer> setTwo) {
        for (int region : setTwo) {
            setOne.add(region);
        }
        equivalentRegionSets.remove(setTwo);
    }

    private void appendToAllRegionSets(Set<Integer> regionSet) {
        equivalentRegionSets.add(regionSet);
    }

    private void combineEquivalentRegionIndexes() {

        ArrayList<Integer> fromRegionList = new ArrayList<Integer>();
        ArrayList<Integer> toRegionList = new ArrayList<Integer>();

        for (Set<Integer> regionSet : equivalentRegionSets) {

            Iterator<Integer> rsi = regionSet.iterator();

            int masterRegion = rsi.next();

            while (rsi.hasNext()) {
                toRegionList.add(masterRegion);
                fromRegionList.add(rsi.next());
            }

        }

        // change any region in the pixelPositionRegionIndexes to the master region if it is in the changes list
        for (int position : pixelPositionRegionIndexes.keySet()) {
            int currentRegion = pixelPositionRegionIndexes.get(position);
            if (fromRegionList.contains(currentRegion)) {
                int fromRegionIndex = fromRegionList.indexOf(currentRegion);
                pixelPositionRegionIndexes.put(position, toRegionList.get(fromRegionIndex));
            }
        }

    }

    private void detectBubbles() {

        for (int position : pixelPositionRegionIndexes.keySet()) {
            int currentRegion = pixelPositionRegionIndexes.get(position);
            BubbleDetection bubbleDetection = bubbleDetections.get(currentRegion);
            int y = (position / imageWidth);
            int x = (position % imageWidth);
            bubbleDetection.addPoint(x, y);
        }
        for (BubbleDetection bubbleDetection : bubbleDetections) {
            if (bubbleDetection.getPixelCount() == 0) {
                continue;
            }
            bubbleDetection.setDebug(debug);
            try {
                bubbleDetection.process();
            } catch (Exception ex) {
                // TODO: we'll need to fix this if we ever use equivalence blob extraction again.
            }
        }

        int checkboxArea = this.imageArea / (rowCount * columnCount);

        // go through otherRegionsInside for each bubble
        // if contains other regions inside - lookup the bubbleDetection
        // merge the details of the other bubble detection with this one (eg, pixel count etc)
        // set the internal bubble to a new (empty) bubble

        Set<Integer> allInternalRegions = new HashSet<Integer>();

        for (int i = 0; i < bubbleDetections.size(); i++) {

            BubbleDetection bubbleDetection = bubbleDetections.get(i);
            if (bubbleDetection.getPixelCount() == 0) {
                continue;
            }

            /*
             * not applicable for contour extraction
             */
	    /*
	    ArrayList<Integer> otherRegionsInside = bubbleDetection.getOtherRegionsInside();
	    
	    allInternalRegions.addAll(otherRegionsInside);
	    */

        }

        for (int i = 0; i < bubbleDetections.size(); i++) {

            BubbleDetection bubbleDetection = bubbleDetections.get(i);
            if (bubbleDetection.getPixelCount() == 0) {
                continue;
            }


            /*
             * not applicable for contour extraction
             */
	    /*
	    ArrayList<Integer> otherRegionsInside = bubbleDetection.getOtherRegionsInside();
	    
	    if ( otherRegionsInside.size() > 0 ) {
		for ( int otherRegion: otherRegionsInside ) {
		    if ( !(allInternalRegions.contains(bubbleDetection.getLabel())) ) {
			bubbleDetection.setPixelCount(bubbleDetection.getPixelCount() + bubbleDetections.get(otherRegion).getPixelCount());
			bubbleDetections.set(otherRegion, new BubbleDetection(pixelPositionRegionIndexes, 0, imageWidth, imageArea));
		    } else {
			// cross linked bubbles - an internal bubble thinks it contains its parent
			if ( bubbleDetection.getRectangle().contains(bubbleDetections.get(otherRegion).getRectangle())) {
			    bubbleDetection.setPixelCount(bubbleDetection.getPixelCount() + bubbleDetections.get(otherRegion).getPixelCount());
			    bubbleDetections.set(otherRegion, new BubbleDetection(pixelPositionRegionIndexes, 0, imageWidth, imageArea));
			}
		    }
		}
	    }
	    */

            Rectangle2D regionRectangle = bubbleDetection.getRectangle();
            int rectangularArea = (int) (regionRectangle.getWidth() * regionRectangle.getHeight());

            // also remove anything that is a line (vertical or horizontal)
            // the proportions of a line would be 1/10th or less of the width is considered a line
            double max = 0;
            double min = 0;
            if (regionRectangle.getWidth() > regionRectangle.getHeight()) {
                max = regionRectangle.getWidth();
                min = regionRectangle.getHeight();
            } else {
                max = regionRectangle.getHeight();
                min = regionRectangle.getWidth();
            }

            // also, check that the enclosed pixels is at least 80 percent of the total number of pixels
            double enclosedPixelCount = bubbleDetection.getEnclosedPixelCount();
            double pixelCount = bubbleDetection.getPixelCount();

            if (((checkboxArea * 1.25) < rectangularArea) || ((max / min) > 10) || (
                enclosedPixelCount < (pixelCount * 0.75))) {

                // remove bubble
                // bubbleDetections.set(bubbleDetection.getLabel(), new BubbleDetection(pixelPositionRegionIndexes, 0, imageWidth, imageArea));

            }

        }

    }

    /* (non-Javadoc)
     * @see com.ebstrada.formreturn.manager.util.image.BlobExtractor#getBinarizedImage()
     */
    @Override public BufferedImage getBinarizedImage() {
        return binarizedImageBufferedImage;
    }

    /* (non-Javadoc)
     * @see com.ebstrada.formreturn.manager.util.image.BlobExtractor#getLabelFirstParseImage()
     */
    @Override public BufferedImage getFirstParseImage() {
        return labelFirstParseImage;
    }

    /* (non-Javadoc)
     * @see com.ebstrada.formreturn.manager.util.image.BlobExtractor#getLabelFirstParseRegionIndexes()
     */
    @Override public int[] getFirstParseLabels() {
        return labelFirstParseRegionIndexes;
    }

    /* (non-Javadoc)
     * @see com.ebstrada.formreturn.manager.util.image.BlobExtractor#getLabelSecondParseImage()
     */
    @Override public BufferedImage getSecondParseImage() {
        return labelSecondParseImage;
    }

    /* (non-Javadoc)
     * @see com.ebstrada.formreturn.manager.util.image.BlobExtractor#getLabelSecondParseRegionIndexes()
     */
    @Override public int[] getSecondParseLabels() {
        return labelSecondParseRegionIndexes;
    }

    /* (non-Javadoc)
     * @see com.ebstrada.formreturn.manager.util.image.BlobExtractor#getCombineInternalRegionsImage()
     */
    @Override public BufferedImage getCombinedImage() {
        return combineInternalRegionsImage;
    }

    /* (non-Javadoc)
     * @see com.ebstrada.formreturn.manager.util.image.BlobExtractor#getCombineInternalRegionsRegionIndexes()
     */
    @Override public int[] getCombinedLabels() {
        return combineInternalRegionsRegionIndexes;
    }

    /* (non-Javadoc)
     * @see com.ebstrada.formreturn.manager.util.image.BlobExtractor#getBubbleDetectionImage()
     */
    @Override public BufferedImage getBubbleDetectionImage() {
        return bubbleDetectionImage;
    }

    /* (non-Javadoc)
     * @see com.ebstrada.formreturn.manager.util.image.BlobExtractor#getBubbleDetectionIndexes()
     */
    @Override public int[] getBubbleDetectionLabels() {
        return bubbleDetectionIndexes;
    }

}
