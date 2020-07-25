package com.ebstrada.formreturn.manager.logic.recognition.structure;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Comparator;

import javax.persistence.EntityManager;
import javax.swing.SwingUtilities;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.recognition.reader.FormReaderException;
import com.ebstrada.formreturn.manager.persistence.jpa.CheckBox;
import com.ebstrada.formreturn.manager.persistence.jpa.FragmentOmr;
import com.ebstrada.formreturn.manager.util.image.BlobExtractor;
import com.ebstrada.formreturn.manager.util.image.BubbleDetection;
import com.ebstrada.formreturn.manager.util.image.ImageUtil;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.dialog.BlobExtractionDebugDialog;

public class OMRMatrix {

    private OMRRecognitionStructure omrrs;

    private BufferedImage fragmentImage;

    private Vector<String> capturedData = new Vector<String>();

    private double markThreshold;

    private Map<Integer, OMRBox> allOmrBoxes = new HashMap<Integer, OMRBox>();

    private Map<Integer, OMRBox> maxAreaOmrBoxes = new HashMap<Integer, OMRBox>();

    private BlobExtractor blobExtraction;

    private OMRBox[][] omrBoxMatrix;

    private boolean debug = false;

    public static final int READ_STRING_LEFT_TO_RIGHT = 0;

    public static final int READ_STRING_TOP_TO_BOTTOM = 1;

    private int readDirection = READ_STRING_LEFT_TO_RIGHT;

    public static final int OLD_METHOD = 0;
    public static final int NEW_METHOD = 1;

    private static final int DIRECTION_UP = 0;
    private static final int DIRECTION_DOWN = 1;

    // this means the x search margin from one bubble in a column to the next
    public static final int X_CENTROID_COLUMN_SEARCH_MARGIN = 6;

    private int processMethod = NEW_METHOD;

    private int lowestToLeft;

    public OMRMatrix(OMRRecognitionStructure omrrs, BufferedImage fragmentImage,
        double markThreshold) {
        this.omrrs = omrrs;
        this.fragmentImage = fragmentImage;
        this.markThreshold = markThreshold;
        this.readDirection = omrrs.getReadDirection();
    }

    public void processDebug() throws FormReaderException {
        process(true);
    }

    public void process() throws FormReaderException {
        process(false);
    }

    public void process(boolean debug) throws FormReaderException {

        this.debug = debug;

        blobExtraction = ImageUtil
            .getBlobExtraction(fragmentImage, omrrs.getNumberOfRows(), omrrs.getNumberOfColumns(),
                debug);

        int boxCount = omrrs.getNumberOfRows() * omrrs.getNumberOfColumns();

        // use grid method
        if (boxCount > 1) {

            detectOMRBoxes();
            calculateMarkValues();

            // use count pixels method
        } else {

            createOMRBoxes();
            this.maxAreaOmrBoxes = getMaxAreaOMRBoxes();
            omrBoxMatrix = getOMRBoxMatrixByPixelCount(sortYKeys(maxAreaOmrBoxes));
            calculateMarkValues();

        }

    }

    private void detectOMRBoxes() throws FormReaderException {

        TreeMap<Integer, BubbleDetection> positionMap = null;

        try {
            positionMap = getOMRBoxPositionMap();
        } catch (FormReaderException frex) {
            frex.setCapturedDataFieldName(omrrs.getFieldName());
            throw frex;
        }

        Stack<TreeMap<Integer, BubbleDetection>> accumulatorStack =
            new Stack<TreeMap<Integer, BubbleDetection>>();

        int imageHeight = fragmentImage.getHeight();
        int imageWidth = fragmentImage.getWidth();

        int rowCount = omrrs.getNumberOfRows();
        int columnCount = omrrs.getNumberOfColumns();

        // the middle y position
        int middleY = lowestToLeft / imageWidth;

        for (int yUp = middleY, yDown = middleY + 1;
             yUp >= 0 && yDown < imageHeight; --yUp, ++yDown) {
            for (int direction = DIRECTION_UP; direction <= DIRECTION_DOWN; ++direction) {
                for (int x = 0; x < imageWidth; ++x) {

                    int linearPosition = 0;
                    if (direction == DIRECTION_UP) {
                        linearPosition = (imageWidth * yUp) + x;
                    } else if (direction == DIRECTION_DOWN) {
                        linearPosition = (imageWidth * yDown) + x;
                    }

                    if (positionMap.containsKey(linearPosition)) {

                        // search up and down and find all boxes for this column
                        TreeMap<Integer, BubbleDetection> column =
                            findOMRBubblesInColumn(linearPosition, positionMap, rowCount);

                        // if the number of boxes found in this column is the expected number
                        if (column == null || column.size() < rowCount) {
                            continue;
                        } else {
                            // add to accumulator stack
                            accumulatorStack.add(column);
                        }

                        // if accumulator map size is equal to or greater than number of columns in this fragment
                        if (accumulatorStack.size() >= columnCount) {

                            TreeMap<Integer, TreeMap<Integer, BubbleDetection>>
                                averageColumnPositions =
                                new TreeMap<Integer, TreeMap<Integer, BubbleDetection>>();

                            // get the x location of each column (scan all of the x columns and take the average)
                            for (TreeMap<Integer, BubbleDetection> col : accumulatorStack) {
                                Set<Integer> columnPositions = col.keySet();
                                double total = 0.0d;
                                for (int position : columnPositions) {
                                    total += (position % imageWidth); // x position
                                }

                                int average =
                                    (int) Math.round(total / (double) columnPositions.size());

                                averageColumnPositions.put(average, col);

                            }

                            if (columnCount == 1) {
                                // set the omr matrix up based on the column data!
                                createOMRBoxMatrix(averageColumnPositions);

                                return;
                            }

                            int minX = Integer.MAX_VALUE;
                            int maxX = 0;

                            // get the total length of all the columns
                            for (int average : averageColumnPositions.keySet()) {

                                if (average < minX) {
                                    minX = average;
                                }

                                if (average > maxX) {
                                    maxX = average;
                                }

                            }

                            double length = maxX - minX;

                            boolean columnsAreEquidistant = false;

                            // skip this result if the length of the find is less than 25 percent of the width of the fragment
                            if (length > ((double) imageWidth * 0.25)) {

                                int portion = (int) Math.round(length / (columnCount - 1));

                                // determine if the average fits into the portions within the correct amount of error margin

                                int errorMargin = (int) ((double) length * 0.05);

                                int i = 0;
                                for (int average : averageColumnPositions.keySet()) {
                                    average = average - minX;

                                    if (Math.abs((portion * i) - average) <= errorMargin
                                        || average <= 0) {
                                        columnsAreEquidistant = true;
                                    } else {
                                        columnsAreEquidistant = false;
                                        break;
                                    }
                                    ++i;
                                }

                            }

                            if (columnsAreEquidistant) {

                                // set the omr matrix up based on the column data!
                                createOMRBoxMatrix(averageColumnPositions);

                                if (debug) {
                                    final OMRMatrix thisMatrix = this;
                                    SwingUtilities.invokeLater(new Runnable() {
                                        public void run() {

                                            Frame owner;

                                            if (Main.getInstance() != null) {
                                                owner = Main.getInstance();
                                            } else if (com.ebstrada.formreturn.server.ServerGUI
                                                .getInstance() != null) {
                                                owner = com.ebstrada.formreturn.server.ServerGUI
                                                    .getInstance().getServerFrame();
                                            } else {
                                                owner = null;
                                            }

                                            BlobExtractionDebugDialog bed =
                                                new BlobExtractionDebugDialog(owner, thisMatrix);
                                            bed.setVisible(true);
                                        }
                                    });
                                }

                                return;

                            } else {

                                // else remove the first column on the bottom of the stack and continue the search
                                accumulatorStack.pop();

                            }

                        } // else the number of boxes was not expected, so just continue

                    }

                }

            }

        }

        if (debug) {
            final OMRMatrix thisMatrix = this;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {

                    Frame owner;

                    if (Main.getInstance() != null) {
                        owner = Main.getInstance();
                    } else if (com.ebstrada.formreturn.server.ServerGUI.getInstance() != null) {
                        owner =
                            com.ebstrada.formreturn.server.ServerGUI.getInstance().getServerFrame();
                    } else {
                        owner = null;
                    }

                    BlobExtractionDebugDialog bed =
                        new BlobExtractionDebugDialog(owner, thisMatrix);
                    bed.setVisible(true);
                }
            });
        }

        FormReaderException fre = new FormReaderException(FormReaderException.MISSING_CHECKBOX);
        fre.setCapturedDataFieldName(omrrs.getFieldName());
        throw fre;

    }

    private void createOMRBoxMatrix(
        TreeMap<Integer, TreeMap<Integer, BubbleDetection>> averageColumnPositions) {

        int rowCount = omrrs.getNumberOfRows();
        int columnCount = omrrs.getNumberOfColumns();
        omrBoxMatrix = new OMRBox[rowCount][columnCount];

        // sort the columns etc and create the omr boxes
        int currentColumn = 0;
        for (int averageColumnPosition : averageColumnPositions.keySet()) {
            TreeMap<Integer, BubbleDetection> column =
                averageColumnPositions.get(averageColumnPosition);
            int currentRow = 0;
            for (BubbleDetection bubble : column.values()) {
                OMRBox omrBox = new OMRBox(bubble.getLabel(), bubble);
                omrBoxMatrix[currentRow][currentColumn] = omrBox;
                currentRow++;
            }
            currentColumn++;
        }

        // set the values of each of the boxes (old way and new way)
        ArrayList<CheckBoxRecognitionStructure> cbrs = omrrs.getCheckBoxRecognitionStructures();
        if (cbrs == null || cbrs.size() <= 0) {
            String[][] characterData = omrrs.getCharacterData();
            for (int i = 0; i < characterData.length; ++i) {
                for (int j = 0; j < characterData[0].length; ++j) {
                    OMRBox omrBox = omrBoxMatrix[i][j];
                    omrBox.setValue(characterData[i][j]);
                }
            }
        } else {
            for (CheckBoxRecognitionStructure cbs : cbrs) {
                OMRBox omrBox = omrBoxMatrix[cbs.getRow()][cbs.getColumn()];
                omrBox.setValue(cbs.getCheckBoxValue());
            }
        }

    }

    private TreeMap<Integer, BubbleDetection> findOMRBubblesInColumn(int linearPosition,
        TreeMap<Integer, BubbleDetection> positionMap, int rowCount) {

        TreeMap<Integer, BubbleDetection> column = new TreeMap<Integer, BubbleDetection>();

        int lastUpwardLinearPosition = linearPosition;
        int lastDownwardLinearPosition = linearPosition;

        int imageHeight = fragmentImage.getHeight();
        int imageWidth = fragmentImage.getWidth();

        // make sure the first bubble was added to the column
        BubbleDetection currentBubble = positionMap.get(linearPosition);

        if (currentBubble != null && !(column.containsKey(linearPosition))) {

            // add the current bubble to the column stack
            column.put(lastUpwardLinearPosition, currentBubble);

            // remove the current bubble from the positionMap
            positionMap.remove(lastUpwardLinearPosition);

        }

        for (int currentRow = 1; currentRow < rowCount && rowCount > 1; rowCount++) {
            for (int direction = DIRECTION_UP; direction <= DIRECTION_DOWN; ++direction) {
                if (direction == DIRECTION_UP) {

                    if (lastUpwardLinearPosition == 0) {
                        --currentRow;
                        continue;
                    }

                    // look from the current bubble upwards
                    int x = lastUpwardLinearPosition % imageWidth;
                    int y = lastUpwardLinearPosition / imageWidth;
                    int searchMargin = X_CENTROID_COLUMN_SEARCH_MARGIN;
                    int xMin = x - searchMargin;
                    if (xMin < 0) {
                        xMin = 0;
                    }
                    int xMax = x + searchMargin;
                    if (xMax >= imageWidth) {
                        xMax = imageWidth - 1;
                    }

                    BubbleDetection nextBubble = null;

                    for (int yPos = y; yPos >= 0; --yPos) {
                        for (int xPos = xMin; xPos <= xMax; ++xPos) {
                            int currentLinearPosition = (imageWidth * yPos) + xPos;
                            if (positionMap.containsKey(currentLinearPosition)) {
                                nextBubble = positionMap.get(currentLinearPosition);
                                if (nextBubble != null) {
                                    lastUpwardLinearPosition = currentLinearPosition;
                                    break;
                                }
                            }
                        }

                        if (nextBubble != null) {
                            break;
                        }

                    }

                    if (nextBubble != null) {

                        // add the current bubble to the column stack
                        column.put(lastUpwardLinearPosition, nextBubble);

                        // remove the current bubble from the positionMap
                        positionMap.remove(lastUpwardLinearPosition);

                    } else {

                        // nothing was found, set last position to 0
                        lastUpwardLinearPosition = 0;

                    }

                } else if (direction == DIRECTION_DOWN) {

                    if (lastDownwardLinearPosition == imageHeight) {
                        --currentRow;
                        continue;
                    }

                    // look from the current bubble downwards
                    int x = lastDownwardLinearPosition % imageWidth;
                    int y = lastDownwardLinearPosition / imageWidth;
                    int searchMargin = X_CENTROID_COLUMN_SEARCH_MARGIN;
                    int xMin = x - searchMargin;
                    if (xMin < 0) {
                        xMin = 0;
                    }
                    int xMax = x + searchMargin;
                    if (xMax >= imageWidth) {
                        xMax = imageWidth - 1;
                    }

                    BubbleDetection nextBubble = null;

                    for (int yPos = y; yPos < imageHeight; ++yPos) {
                        for (int xPos = xMin; xPos <= xMax; ++xPos) {
                            int currentLinearPosition = (imageWidth * yPos) + xPos;
                            if (positionMap.containsKey(currentLinearPosition)) {
                                nextBubble = positionMap.get(currentLinearPosition);
                                if (nextBubble != null) {
                                    lastDownwardLinearPosition = currentLinearPosition;
                                    break;
                                }
                            }
                        }

                        if (nextBubble != null) {
                            break;
                        }

                    }

                    if (nextBubble != null) {

                        // add the current bubble to the column stack
                        column.put(lastDownwardLinearPosition, nextBubble);

                        // remove the current bubble from the positionMap
                        positionMap.remove(lastDownwardLinearPosition);

                    } else {

                        // nothing was found, set last position to imageHeight
                        lastDownwardLinearPosition = imageHeight;

                    }

                }

            }

            // break out if both position extermities have been reached
            if (lastUpwardLinearPosition <= 0 && lastDownwardLinearPosition >= imageHeight) {
                break;
            }

        }

        return column;

    }

    private TreeMap<Integer, BubbleDetection> getOMRBoxPositionMap() throws FormReaderException {

        TreeMap<Integer, BubbleDetection> positionMap = new TreeMap<Integer, BubbleDetection>();

        // find the lowest euclidian distance to the left mid point
        // and don't wrap the distance from the back - use X,Y to find distance
        int lowestEuclidianDistance = Integer.MAX_VALUE;
        lowestToLeft = 0;
        int x1 = 0;
        int y1 = fragmentImage.getHeight() / 2;

        int enclosedCutOff = 0;
        int boxCount = omrrs.getNumberOfRows() * omrrs.getNumberOfColumns();
        ArrayList<Integer> enclosedAreas = new ArrayList<Integer>();
        for (BubbleDetection bubbleDetection : blobExtraction.getBubbleDetections()) {
            enclosedAreas.add(bubbleDetection.getEnclosedPixelCount());
        }

        int smallestAreaIndex = enclosedAreas.size() - boxCount;

        if (enclosedAreas.size() <= 0 || smallestAreaIndex < 0) {
            throw new FormReaderException(FormReaderException.MISSING_CHECKBOX);
        }

        Collections.sort(enclosedAreas);

        double smallestArea = enclosedAreas.get(smallestAreaIndex);
        enclosedCutOff = (int) Math.round(smallestArea * 0.75);

        for (BubbleDetection bubbleDetection : blobExtraction.getBubbleDetections()) {

            int regionIndex = bubbleDetection.getLabel();

            // skip region 0, it is the background
            if (regionIndex == 0 || bubbleDetection.getPixelCount() == 0
                || bubbleDetection.getEnclosedPixelCount() <= enclosedCutOff) {
                continue;
            }

            // get the region
            int x = bubbleDetection.getCenterX();
            int y = bubbleDetection.getCenterY();

            int width = fragmentImage.getWidth();

            int linearPosition = (width * y) + x;

            int euclidianDistanceToMiddle = (int) Math.round(
                Math.sqrt(((x - x1) * (x - x1)) + ((y - y1) * (y - y1)))); // pythagorean formula
            if (lowestEuclidianDistance > euclidianDistanceToMiddle) {
                lowestEuclidianDistance = euclidianDistanceToMiddle;
                lowestToLeft = linearPosition;
            }

            positionMap.put(new Integer(linearPosition), bubbleDetection);

        }

        return positionMap;

    }

    public boolean isRecognitionKey() {
        return omrrs.isReconciliationKey();
    }

    public boolean isCombineColumnCharacters() {
        return omrrs.isCombineColumnCharacters();
    }

    private OMRBox[][] getOMRBoxMatrixByPixelCount(Integer[] sortedYKeysArray) {

        int rowCount = omrrs.getNumberOfRows();
        int columnCount = omrrs.getNumberOfColumns();

        OMRBox[][] matrix = new OMRBox[rowCount][columnCount];
        String cbarray[][] = new String[rowCount][columnCount];

        ArrayList<CheckBoxRecognitionStructure> cbrs = omrrs.getCheckBoxRecognitionStructures();
        if (cbrs != null) {
            for (CheckBoxRecognitionStructure cb : cbrs) {
                cbarray[cb.getRow()][cb.getColumn()] = cb.getCheckBoxValue();
            }
        }

        for (int i = 0; i < rowCount; i++) {

            Map<Integer, OMRBox> unsortedRow = new HashMap<Integer, OMRBox>();
            for (int j = 0; j < columnCount; j++) {
                unsortedRow.put(j, allOmrBoxes.get(sortedYKeysArray[((i * columnCount) + j)]));
            }

            // SORT UNSORTED ROW
            Integer[] sortedRowKeys = sortXKeys(unsortedRow);
            for (int j = 0; j < sortedRowKeys.length; j++) {
                matrix[i][j] = unsortedRow.get(sortedRowKeys[j]);
                if (getProcessMethod() == NEW_METHOD) {
                    matrix[i][j].setValue(cbarray[i][j]);
                } else {
                    matrix[i][j].setValue(omrrs.getCharacterData()[i][j]);
                }
            }

        }

        return matrix;
    }

    private Integer[] sortYKeys(Map<Integer, OMRBox> omrBoxes) {
        YValueComparator yvc = new YValueComparator(omrBoxes);
        List keys = new ArrayList(omrBoxes.keySet());
        Collections.sort(keys, yvc);

        Integer[] sortedKeys = new Integer[keys.size()];
        for (int i = 0; i < keys.size(); i++) {
            sortedKeys[i] = (Integer) keys.get(i);
        }
        return sortedKeys;
    }

    private Integer[] sortXKeys(Map<Integer, OMRBox> omrBoxes) {
        XValueComparator xvc = new XValueComparator(omrBoxes);
        List keys = new ArrayList(omrBoxes.keySet());
        Collections.sort(keys, xvc);

        Integer[] sortedKeys = new Integer[keys.size()];
        for (int i = 0; i < keys.size(); i++) {
            sortedKeys[i] = (Integer) keys.get(i);
        }
        return sortedKeys;
    }

    private class AreaPixelCountComparator implements Comparator<Object> {
        Map<Integer, OMRBox> omrBoxes;

        public AreaPixelCountComparator(Map<Integer, OMRBox> omrBoxes) {
            this.omrBoxes = omrBoxes;
        }

        public int compare(Object arg0, Object arg1) {
            if (!omrBoxes.containsKey(arg0) || !omrBoxes.containsKey(arg1)) {
                return 0;
            }

            OMRBox omrBox1 = omrBoxes.get((Integer) arg0);
            OMRBox omrBox2 = omrBoxes.get((Integer) arg1);

            if (omrBox1.getAreaPixelCount() < omrBox2.getAreaPixelCount()) {
                return 1;
            } else if (omrBox1.getAreaPixelCount() == omrBox2.getAreaPixelCount()) {
                return 0;
            } else {
                return -1;
            }
        }

    }


    private class YValueComparator implements Comparator<Object> {
        Map<Integer, OMRBox> omrBoxes;

        public YValueComparator(Map<Integer, OMRBox> omrBoxes) {
            this.omrBoxes = omrBoxes;
        }

        public int compare(Object arg0, Object arg1) {
            if (!omrBoxes.containsKey(arg0) || !omrBoxes.containsKey(arg1)) {
                return 0;
            }

            OMRBox omrBox1 = omrBoxes.get((Integer) arg0);
            OMRBox omrBox2 = omrBoxes.get((Integer) arg1);

            if (omrBox1.getCenterY() > omrBox2.getCenterY()) {
                return 1;
            } else if (omrBox1.getCenterY() == omrBox2.getCenterY()) {
                return 0;
            } else {
                return -1;
            }
        }

    }


    private class XValueComparator implements Comparator<Object> {
        Map<Integer, OMRBox> omrBoxes;

        public XValueComparator(Map<Integer, OMRBox> omrBoxes) {
            this.omrBoxes = omrBoxes;
        }

        public int compare(Object arg0, Object arg1) {
            if (!omrBoxes.containsKey(arg0) || !omrBoxes.containsKey(arg1)) {
                return 0;
            }

            OMRBox omrBox1 = omrBoxes.get((Integer) arg0);
            OMRBox omrBox2 = omrBoxes.get((Integer) arg1);

            if (omrBox1.getCenterX() > omrBox2.getCenterX()) {
                return 1;
            } else if (omrBox1.getCenterX() == omrBox2.getCenterX()) {
                return 0;
            } else {
                return -1;
            }
        }

    }

    public Map<Integer, OMRBox> getMaxAreaOMRBoxes() throws FormReaderException {
        Map<Integer, OMRBox> tob = new HashMap<Integer, OMRBox>();
        int numberOfResults = omrrs.getNumberOfRows() * omrrs.getNumberOfColumns();

        AreaPixelCountComparator apcc = new AreaPixelCountComparator(allOmrBoxes);
        List keys = new ArrayList(allOmrBoxes.keySet());
        Collections.sort(keys, apcc);

        for (int i = 0; i < numberOfResults; i++) {
            try {
                Integer regionIndex = (Integer) keys.get(i);
                tob.put(regionIndex, allOmrBoxes.get(regionIndex));
            } catch (Exception ex) {
                String message = String
                    .format(Localizer.localize("UI", "OMRMatrixCheckboxCountIncorrectMessage"),
                        omrrs.getFieldName());
                FormReaderException fre =
                    new FormReaderException(FormReaderException.MISSING_CHECKBOX, message);
                fre.setCapturedDataFieldName(omrrs.getFieldName());
                throw fre;
            }
        }

        return tob;
    }

    public void createOMRBoxes() {

        int[] regionIndexArray = blobExtraction.getLabelIndexArray();

        for (int regionIndex : regionIndexArray) {

            if (regionIndex == 0) {
                continue;
            }

            OMRBox omrBox = null;

            if (allOmrBoxes.containsKey(new Integer(regionIndex))) {
                omrBox = allOmrBoxes.get(new Integer(regionIndex));
            } else {
                omrBox = new OMRBox(regionIndex, blobExtraction.getBubbleDetection(regionIndex));
                allOmrBoxes.put(new Integer(regionIndex), omrBox);
            }

        }

    }

    public Vector<Integer> getAllMarkIndexes() {
        Vector<Integer> indexes = new Vector<Integer>();
        if (omrBoxMatrix == null) {
            System.out.println("omrBoxMatrix is null!");
        }
        for (int i = 0; i < omrBoxMatrix.length; i++) {
            for (int j = 0; j < omrBoxMatrix[0].length; j++) {
                indexes.add(omrBoxMatrix[i][j].getRegionIndex());
            }
        }

        return indexes;
    }

    public BufferedImage getBlobExtractionImage(Vector<Integer> displayedRegions,
        boolean firstRun) {

        int[] blobExtractionRegionArray = blobExtraction.getLabelIndexArray();

        if (firstRun) {
            displayedRegions = getAllMarkIndexes();
        }

        int width = fragmentImage.getWidth();
        int height = fragmentImage.getHeight();

        BufferedImage bufimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) bufimage.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, bufimage.getWidth(), bufimage.getHeight());

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int regionIndex = blobExtractionRegionArray[x + y * width];
                if (displayedRegions.contains(new Integer(regionIndex))) {
                    bufimage.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }

        for (int i = 0; i < omrBoxMatrix.length; i++) {
            for (int j = 0; j < omrBoxMatrix[0].length; j++) {
                OMRBox omrBox = omrBoxMatrix[i][j];
                Rectangle2D r2d = omrBox.getRectangle();
                bufimage.setRGB((int) r2d.getX(), (int) r2d.getY(), Color.RED.getRGB());
                bufimage.setRGB((int) (r2d.getX() + r2d.getWidth()), (int) r2d.getY(),
                    Color.RED.getRGB());
                bufimage.setRGB((int) r2d.getX(), (int) (r2d.getY() + r2d.getHeight()),
                    Color.RED.getRGB());
                bufimage.setRGB((int) (r2d.getX() + r2d.getWidth()),
                    (int) (r2d.getY() + r2d.getHeight()), Color.RED.getRGB());

                if (omrBox.getDetectionPosition() == -1) {
                    int centerX = omrBox.getCenterX();
                    int centerY = omrBox.getCenterY();
                    bufimage.setRGB(centerX, centerY, Color.ORANGE.getRGB());
                }

                if (omrBox.getDetectionPosition() > 0) {
                    int position = omrBox.getDetectionPosition();
                    int y = position / width;
                    int x = position - (y * width);
                    bufimage.setRGB(x, y, Color.GREEN.getRGB());
                }

            }
        }

        return bufimage;

    }

    private void calculateMarkValues() {
        if (omrBoxMatrix != null) {
            for (int i = 0; i < omrBoxMatrix.length; i++) {
                for (int j = 0; j < omrBoxMatrix[i].length; j++) {

                    double pixelCount = omrBoxMatrix[i][j].getPixelCount();
                    double whiteCount = omrBoxMatrix[i][j].getWhiteCount();

                    double calculatedThreshold = pixelCount / whiteCount;
                    if (calculatedThreshold > markThreshold) {
                        omrBoxMatrix[i][j].setMarked(true);
                        capturedData.add(omrBoxMatrix[i][j].getValue());
                    } else {
                        omrBoxMatrix[i][j].setMarked(false);
                    }
                }
            }
        }
    }

    public void applyMarkValuesToFragmentOmr(FragmentOmr fragmentOmr, EntityManager entityManager) {

        List<CheckBox> checkBoxSet = fragmentOmr.getCheckBoxCollection();
        if (checkBoxSet == null || checkBoxSet.size() <= 0) {
            if (fragmentOmr.getCharacterData() != null) {
                // create a new checkbox set based on fragmentOmr.getCharacterData()
                String[][] capturedDataArr = fragmentOmr.getCharacterData();
                for (int row = 0; row < capturedDataArr.length; ++row) {
                    for (int column = 0; column < capturedDataArr[0].length; ++column) {
                        CheckBox cb = new CheckBox();
                        cb.setFragmentOmrId(fragmentOmr);
                        cb.setFragmentXRatio(0.0d);
                        cb.setFragmentYRatio(0.0d);
                        cb.setCheckBoxValue(capturedDataArr[row][column]);
                        cb.setRowNumber((short) row);
                        cb.setColumnNumber((short) column);
                        if (omrBoxMatrix[row][column] != null && omrBoxMatrix[row][column]
                            .isMarked()) {
                            cb.setCheckBoxMarked((short) 1);
                        } else {
                            cb.setCheckBoxMarked((short) 0);
                        }
                        entityManager.persist(cb);
                    }
                }
                return;
            }
        }

        for (CheckBox cb : checkBoxSet) {
            int row = cb.getRowNumber();
            int column = cb.getColumnNumber();
            if (omrBoxMatrix[row][column] != null && omrBoxMatrix[row][column].isMarked()) {
                cb.setCheckBoxMarked((short) 1);
            } else {
                cb.setCheckBoxMarked((short) 0);
            }
            entityManager.persist(cb);
        }

    }

    public String getCapturedString() {
        return getCapturedString(this.readDirection);
    }

    public String getCapturedString(int direction) {

        String markString = "";

        if (omrBoxMatrix != null) {

            switch (direction) {

                case READ_STRING_LEFT_TO_RIGHT:

                    for (int i = 0; i < omrBoxMatrix[0].length; i++) {
                        for (int j = 0; j < omrBoxMatrix.length; j++) {

                            double pixelCount = omrBoxMatrix[j][i].getPixelCount();
                            double whiteCount = omrBoxMatrix[j][i].getWhiteCount();

                            if (pixelCount == 0) {
                                continue;
                            }

                            double calculatedThreshold = pixelCount / whiteCount;
                            if (calculatedThreshold > markThreshold) {
                                markString += omrBoxMatrix[j][i].getValue();
                            }

                        }
                    }

                    break;

                case READ_STRING_TOP_TO_BOTTOM:

                    for (int i = 0; i < omrBoxMatrix.length; i++) {
                        for (int j = 0; j < omrBoxMatrix[0].length; j++) {

                            double pixelCount = omrBoxMatrix[i][j].getPixelCount();
                            double whiteCount = omrBoxMatrix[i][j].getWhiteCount();

                            if (pixelCount == 0) {
                                continue;
                            }

                            double calculatedThreshold = pixelCount / whiteCount;
                            if (calculatedThreshold > markThreshold) {
                                markString += omrBoxMatrix[i][j].getValue();
                            }

                        }
                    }

                    break;

            }

        }

        return markString;

    }

    public Vector<String> getCapturedData() {
        return capturedData;
    }

    public BlobExtractor getBlobExtraction() {
        return blobExtraction;
    }

    public OMRRecognitionStructure getOmrrs() {
        return omrrs;
    }

    public double getMarkThreshold() {
        return markThreshold;
    }

    public BufferedImage getFragmentImage() {
        return fragmentImage;
    }

    public Map<Integer, OMRBox> getOmrBoxes() {
        return allOmrBoxes;
    }

    public void setOmrBoxes(Map<Integer, OMRBox> omrBoxes) {
        this.allOmrBoxes = omrBoxes;
    }

    public OMRBox[][] getOmrBoxMatrix() {
        return omrBoxMatrix;
    }

    public BlobExtractor getDebugBlobExtraction() {
        return blobExtraction = ImageUtil
            .getBlobExtraction(fragmentImage, omrrs.getNumberOfRows(), omrrs.getNumberOfColumns(),
                true);
    }

    public int getProcessMethod() {
        return processMethod;
    }

    public void setProcessMethod(int processMethod) {
        this.processMethod = processMethod;
    }

}
