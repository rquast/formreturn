package com.ebstrada.formreturn.manager.logic.recognition.reader;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.recognition.structure.BarcodeRecognitionData;
import com.ebstrada.formreturn.manager.logic.recognition.structure.PageRecognitionData;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.image.ImageUtil;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.oned.Code128Reader;


public class BarcodeReader {

    private BufferedImage sourceImage;

    private Float overlayZoom = 1.0f;

    private PageRecognitionData pageRecognitionData;

    private Code128Reader delegate;

    private LuminanceSource source;

    private int luminanceCutoff = 240; // luminance is high because of anti-aliasing

    public BarcodeReader(BufferedImage image, PageRecognitionData pageRecognitionData) {
        this.sourceImage = image;
        this.pageRecognitionData = pageRecognitionData;
    }

    public void process() throws Exception {
        process(false);
    }

    private Result[] resultVectorToArray(Vector<Result> results) throws ReaderException {
        if (results.isEmpty()) {
            throw NotFoundException.getNotFoundInstance();
        }
        int numResults = results.size();
        Result[] resultArray = new Result[numResults];
        for (int i = 0; i < numResults; i++) {
            resultArray[i] = (Result) results.elementAt(i);
        }
        return resultArray;
    }

    private Result[] decodeMultiple(BinaryBitmap image, Hashtable hints) throws ReaderException {
        Vector results = new Vector();
        while (true) {
            try {
                Result result = delegate.decode(image, hints);
                results.add(result);
            } catch (ReaderException re) {
                return resultVectorToArray(results);
            }
        }
    }

    public void process(boolean locateFormId) throws Exception {

        boolean formIdLocated = false;

        boolean segmentIdLocated = false;

        Result results[] = null;
        delegate = new Code128Reader();
        source = new BufferedImageLuminanceSource(sourceImage);
        BinaryBitmap bitmap = new BinaryBitmap(new GlobalHistogramBinarizer(source));
        Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        hints.put(DecodeHintType.SKIP_TO_ROW, new Integer(0));
        hints.put(DecodeHintType.IGNORE_AREAS, new Vector<Rectangle2D>());

        try {
            results = decodeMultiple(bitmap, hints);
        } catch (ReaderException re) {
            throw re;
        }

        if (results == null || results.length <= 0) {
            return;
        }

        for (Result result : results) {
            double x1 = result.getResultPoints()[0].getX();
            double y1 = result.getResultPoints()[0].getY();
            double x2 = result.getResultPoints()[1].getX();
            double y2 = result.getResultPoints()[1].getY();

            Hashtable metadata = result.getResultMetadata();

            int orientation = 0;
            if (metadata != null && metadata.containsKey(ResultMetadataType.ORIENTATION)) {
                orientation = (Integer) metadata.get(ResultMetadataType.ORIENTATION);
            }

            String barcodeValue = result.getText();
            BarcodeRecognitionData barcodeRecognitionData = new BarcodeRecognitionData();
            barcodeRecognitionData.setValue(barcodeValue);
            Rectangle2D barcodeBoundary =
                getBarcodeBoundary(new Rectangle2D.Double(x1, y1, (x2 - x1), (y2 - y1)),
                    this.sourceImage, this.luminanceCutoff);
            barcodeRecognitionData.setBarcodeBoundary(barcodeBoundary);
            barcodeRecognitionData.setOrientation(orientation);
            barcodeRecognitionData.setBarcodeFormat(result.getBarcodeFormat());
            barcodeRecognitionData.setX1(x1);
            barcodeRecognitionData.setX2(x2);
            barcodeRecognitionData.setY1(y1);
            barcodeRecognitionData.setY2(y2);

            BufferedImage barcodeImage = sourceImage
                .getSubimage((int) barcodeBoundary.getX(), (int) barcodeBoundary.getY(),
                    (int) barcodeBoundary.getWidth(), (int) barcodeBoundary.getHeight());
            barcodeRecognitionData.setBarcodeImage(barcodeImage);
            barcodeImage = null;
            barcodeBoundary = null;

            // look for form ID barcodes
            boolean isFormIDBarcode = false;
            if (pageRecognitionData.getFormIDBarcode() == null) {
                Pattern p = Pattern.compile("\\d+-\\d+");
                try {
                    Matcher m = p.matcher(barcodeValue);
                    m.lookingAt();
                    if (m.group().equals(barcodeValue)) {
                        isFormIDBarcode = true;
                    }
                } catch (Exception ex) {
                }
            }

            if (isFormIDBarcode && !(barcodeValue.startsWith("0-"))) {
                barcodeRecognitionData.setType(BarcodeRecognitionData.FORMID_BARCODE);
                pageRecognitionData.setFormIDBarcode(barcodeRecognitionData);
                formIdLocated = true;
            } else {

                int segmentBarcodeValue = -1;
                try {
                    segmentBarcodeValue = Integer.parseInt(barcodeValue);
                } catch (Exception ex) {
                    // do nothing.
                }

                if (barcodeValue.length() >= 2 && barcodeValue.length() < 8
                    && segmentBarcodeValue < 100 && segmentBarcodeValue > 0) {
                    barcodeRecognitionData.setType(BarcodeRecognitionData.SEGMENT_BARCODE);
                    pageRecognitionData.getSegmentBarcodes()
                        .put(Misc.parseIntegerString(barcodeValue), barcodeRecognitionData);
                    segmentIdLocated = true;
                } else {
                    barcodeRecognitionData.setType(BarcodeRecognitionData.CODE128);
                    pageRecognitionData.getCode128Barcodes().add(barcodeRecognitionData);
                }
            }

        }

        if (locateFormId && !formIdLocated) {
            throw new Exception(
                Localizer.localize("UI", "BarcodeReaderUnableToLocateFormIDMessage"));
        } else if (!locateFormId && !segmentIdLocated) {
            throw new Exception(Localizer.localize("UI", "SegmentBarcodeNotFound"));
        }

    }

    public void drawFormIDBarcodeBoundary(Graphics2D g2, int x_offset, int y_offset) {

        if (pageRecognitionData.getFormIDBarcode() == null) {
            return;
        }

        BarcodeRecognitionData formIDBarcode = pageRecognitionData.getFormIDBarcode();

        Rectangle2D barcodeBoundary = formIDBarcode.getBarcodeBoundary();

        g2.setStroke(new BasicStroke(1));

        g2.setColor(Color.MAGENTA);

        if (barcodeBoundary != null) {
            g2.draw(
                new Rectangle2D.Double(Math.round(barcodeBoundary.getX() * overlayZoom) + x_offset,
                    Math.round(barcodeBoundary.getY() * overlayZoom) + y_offset,
                    Math.round(barcodeBoundary.getWidth() * overlayZoom),
                    Math.round(barcodeBoundary.getHeight() * overlayZoom)));
            FontMetrics fm = g2.getFontMetrics();
            int stringWidth = fm.stringWidth(formIDBarcode.getValue());
            int stringHeight = fm.getHeight();
            g2.setColor(Color.BLACK);
            g2.setBackground(Color.BLACK);
            g2.fillRect((int) Math.round(barcodeBoundary.getX() * overlayZoom) + x_offset,
                (int) Math.round(barcodeBoundary.getY() * overlayZoom) - stringHeight + y_offset,
                stringWidth, stringHeight);
            g2.setColor(Color.WHITE);
            g2.drawString(formIDBarcode.getValue(),
                (int) Math.round(barcodeBoundary.getX() * overlayZoom) + x_offset,
                (int) Math.round(barcodeBoundary.getY() * overlayZoom) - 4 + y_offset);
            g2.setColor(Color.MAGENTA);
        }
    }

    public void drawSegmentBarcodeBoundary(Graphics2D g2, int x_offset, int y_offset) {

        if (pageRecognitionData.getSegmentBarcodes() == null) {
            return;
        }

        for (BarcodeRecognitionData segmentBarcode : pageRecognitionData.getSegmentBarcodes()
            .values()) {

            Rectangle2D barcodeBoundary = segmentBarcode.getBarcodeBoundary();

            g2.setStroke(new BasicStroke(1));

            g2.setColor(Color.BLUE);

            if (barcodeBoundary != null) {
                g2.draw(new Rectangle2D.Double(
                    Math.round(barcodeBoundary.getX() * overlayZoom) + x_offset,
                    Math.round(barcodeBoundary.getY() * overlayZoom) + y_offset,
                    Math.round(barcodeBoundary.getWidth() * overlayZoom),
                    Math.round(barcodeBoundary.getHeight() * overlayZoom)));
                FontMetrics fm = g2.getFontMetrics();
                int stringWidth = fm.stringWidth(segmentBarcode.getValue());
                int stringHeight = fm.getHeight();
                g2.setColor(Color.BLACK);
                g2.setBackground(Color.BLACK);
                g2.fillRect((int) Math.round(barcodeBoundary.getX() * overlayZoom) + x_offset,
                    (int) Math.round(barcodeBoundary.getY() * overlayZoom) - stringHeight
                        + y_offset, stringWidth, stringHeight);
                g2.setColor(Color.WHITE);
                g2.drawString(segmentBarcode.getValue(),
                    (int) Math.round(barcodeBoundary.getX() * overlayZoom) + x_offset,
                    (int) Math.round(barcodeBoundary.getY() * overlayZoom) - 4 + y_offset);
                g2.setColor(Color.BLUE);
            }
        }
    }

    public void drawUnknownBarcodeBoundary(Graphics2D g2, int x_offset, int y_offset) {

        if (pageRecognitionData.getCode128Barcodes() == null) {
            return;
        }

        for (BarcodeRecognitionData unknownBarcode : pageRecognitionData.getCode128Barcodes()) {

            Rectangle2D barcodeBoundary = unknownBarcode.getBarcodeBoundary();

            g2.setStroke(new BasicStroke(1));

            g2.setColor(Color.BLUE);

            if (barcodeBoundary != null) {
                g2.draw(new Rectangle2D.Double(
                    Math.round(barcodeBoundary.getX() * overlayZoom) + x_offset,
                    Math.round(barcodeBoundary.getY() * overlayZoom) + y_offset,
                    Math.round(barcodeBoundary.getWidth() * overlayZoom),
                    Math.round(barcodeBoundary.getHeight() * overlayZoom)));
                FontMetrics fm = g2.getFontMetrics();
                int stringWidth = fm.stringWidth(unknownBarcode.getValue());
                int stringHeight = fm.getHeight();
                g2.setColor(Color.BLACK);
                g2.setBackground(Color.BLACK);
                g2.fillRect((int) Math.round(barcodeBoundary.getX() * overlayZoom) + x_offset,
                    (int) Math.round(barcodeBoundary.getY() * overlayZoom) - stringHeight
                        + y_offset, stringWidth, stringHeight);
                g2.setColor(Color.WHITE);
                g2.drawString(unknownBarcode.getValue(),
                    (int) Math.round(barcodeBoundary.getX() * overlayZoom) + x_offset,
                    (int) Math.round(barcodeBoundary.getY() * overlayZoom) - 4 + y_offset);
                g2.setColor(Color.BLUE);
            }
        }
    }

    public void drawDetectionOverlay(Graphics2D g2, int x_offset, int y_offset) {
        drawFormIDBarcodeBoundary(g2, x_offset, y_offset);
        drawSegmentBarcodeBoundary(g2, x_offset, y_offset);
        drawUnknownBarcodeBoundary(g2, x_offset, y_offset);
    }

    public static boolean isWhiteRow(int innerX1, int innerX2, int y, BufferedImage sourceImage,
        int luminanceCutoff) {

        int whiteCount = 0;
        int blackCount = 0;
        for (int x = innerX1; x <= innerX2; ++x) {
            if (ImageUtil.isBlack(sourceImage, x, y, luminanceCutoff)) {
                blackCount++;
            } else {
                whiteCount++;
            }
        }

        // make it a threshold, not completely white
        // if only 10 percent black, it's all white
        int percentageBlack = (int) (((double) blackCount) / ((double) whiteCount) * 100.0d);
        if (percentageBlack <= 10) {
            return true;
        } else {
            return false;
        }

    }

    public static int getBiggestBarSize(int innerX1, int innerX2, int y, BufferedImage sourceImage,
        int luminanceCutoff) {

        int tmpCounter = 0;
        int biggestBarSize = 0;

        for (int x = innerX1; x <= innerX2; ++x) {
            if (!(ImageUtil.isBlack(sourceImage, x, y, luminanceCutoff))) {
                if (tmpCounter != 0) {
                    if (biggestBarSize < tmpCounter || biggestBarSize == 0) {
                        biggestBarSize = tmpCounter;
                    }
                }
                tmpCounter = 0;
            } else {
                tmpCounter++;
            }
        }

        return biggestBarSize;

    }

    private static boolean isBlackInRange(int minY, int maxY, int x, int y, BufferedImage bi,
        int lum) {

        // return false immediately if the pixel is white.
        if (!(ImageUtil.isBlack(bi, x, y, lum))) {
            return false;
        }

        // start trace to find out how tall the bar is on the Y axis from the x,y point.
        int lowestY = Integer.MAX_VALUE;
        int highestY = 0;

        // figure out what the appropriate min/max is for this bar

        // go toward 0
        for (lowestY = y; lowestY > 0; lowestY--) {
            if (!(ImageUtil.isBlack(bi, x, lowestY, lum))) {
                break;
            }
        }

        // go toward maximum height
        int imageHeight = bi.getHeight();
        for (highestY = y; highestY < imageHeight; highestY++) {
            if (!(ImageUtil.isBlack(bi, x, highestY, lum))) {
                break;
            }
        }

        // if the bar fits within the min/max range, return true
        float barcodeHeight = maxY - minY;
        float acceptableRange = barcodeHeight * 0.20f;

        // test min range
        if (Math.abs(minY - lowestY) > acceptableRange) {
            return false;
        }

        // test max range
        if (Math.abs(highestY - maxY) > acceptableRange) {
            return false;
        }

        // return true, is black and is in the right range to be a barcode bar.
        return true;

    }

    public static Rectangle2D getActualBarcodeBounds(int innerX1, int innerX2, int y,
        int biggestBarSize, BufferedImage sourceImage, int luminanceCutoff) {

        int sourceImageWidth = sourceImage.getWidth();
        int sourceImageHeight = sourceImage.getHeight();
        int outerX1 = 0;
        int outerX2 = sourceImageWidth;
        int whiteCounter = 0;

        int quietZoneLength = biggestBarSize * 3;

        // find minY
        int minY = 0;
        for (minY = y; minY >= 0; --minY) {
            if (isWhiteRow(innerX1, innerX2, minY, sourceImage, luminanceCutoff)) {
                break;
            }
        }
        ++minY;

        // find maxY
        int maxY = y;
        for (maxY = y; maxY < sourceImageHeight; ++maxY) {
            if (isWhiteRow(innerX1, innerX2, maxY, sourceImage, luminanceCutoff)) {
                break;
            }
        }
        --maxY;

        // go left, find max
        for (int x = innerX1; x >= 0; --x) {
            if (isBlackInRange(minY, maxY, x, y, sourceImage, luminanceCutoff)) {
                whiteCounter = 0;
            } else {
                ++whiteCounter;
                if (whiteCounter >= quietZoneLength || x == 0) {
                    outerX1 = x + whiteCounter;
                    break;
                }
            }
        }

        // go right, find max
        whiteCounter = 0;
        for (int x = innerX2; x < sourceImageWidth; ++x) {
            if (isBlackInRange(minY, maxY, x, y, sourceImage, luminanceCutoff)) {
                whiteCounter = 0;
            } else {
                ++whiteCounter;
                if (whiteCounter >= quietZoneLength || x == (sourceImageWidth - 1)) {
                    outerX2 = x - whiteCounter;
                    break;
                }
            }
        }

        return new Rectangle(outerX1, minY, outerX2 - outerX1, maxY - minY);

    }

    public static Rectangle2D getBarcodeBoundary(Rectangle2D startToEndPattern,
        BufferedImage sourceImage, int luminanceCutoff) {

        int innerX1 = (int) startToEndPattern.getX();
        int innerX2 = (int) (innerX1 + startToEndPattern.getWidth());
        int sourceImageHeight = sourceImage.getHeight();

        int yOrig = (int) startToEndPattern.getY();

        // this code makes sure that there is no anti-aliasing that the
        // barcode is detecting.. make sure the y pos is correct
        int y = yOrig;
        int i = 0;
        while (true) {
            int yUp = yOrig + i;
            int yDown = yOrig - i;
            if (yUp >= sourceImageHeight) {
                break;
            }
            if (yDown < 0) {
                break;
            }
            if (!(isWhiteRow(innerX1, innerX2, yDown, sourceImage, luminanceCutoff))) {
                y = yDown;
                break;
            }
            if (!(isWhiteRow(innerX1, innerX2, yUp, sourceImage, luminanceCutoff))) {
                y = yUp;
                break;
            }
            i++;
        }

        int biggestBarSize = getBiggestBarSize(innerX1, innerX2, y, sourceImage, luminanceCutoff);

        // x, y, width, height
        return getActualBarcodeBounds(innerX1, innerX2, y, biggestBarSize, sourceImage,
            luminanceCutoff);
    }

    public Float getOverlayZoom() {
        return overlayZoom;
    }

    public void setOverlayZoom(Float overlayZoom) {
        this.overlayZoom = overlayZoom;
    }

}
