package com.ebstrada.formreturn.manager.logic.recognition.reader;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Hashtable;
import java.util.Vector;

import com.ebstrada.formreturn.manager.logic.recognition.structure.BarcodeRecognitionStructure;
import com.ebstrada.formreturn.manager.util.image.ImageUtil;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.GlobalHistogramBinarizer;

public class BarcodeImageReader {

    private Reader delegate;
    private BufferedImageLuminanceSource source;
    private String barcodeValue;
    private float x1;
    private float y1;
    private float x2;
    private float y2;
    private BufferedImage sourceImage;
    private Rectangle2D barcodeBoundary;

    private int luminanceCutoff = 240;

    public BarcodeImageReader(BarcodeRecognitionStructure bcrs, BufferedImage fragmentImage) {

        this.sourceImage = fragmentImage;

        Result results[] = null;

        int barcodeType = bcrs.getBarcodeType();

        // do a switch on the barcode type to set the zxing reader
        delegate = BarcodeReaderTypes.getDelegate(barcodeType);

        source = new BufferedImageLuminanceSource(fragmentImage);
        BinaryBitmap bitmap = new BinaryBitmap(new GlobalHistogramBinarizer(source));
        Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        hints.put(DecodeHintType.SKIP_TO_ROW, new Integer(0));
        hints.put(DecodeHintType.IGNORE_AREAS, new Vector<Rectangle2D>());

        try {
            results = decodeMultiple(bitmap, hints);
        } catch (ReaderException re) {
            return;
        }

        if (results.length <= 0) {
            return;
        }

        for (Result result : results) {
            x1 = result.getResultPoints()[0].getX();
            y1 = result.getResultPoints()[0].getY();
            x2 = result.getResultPoints()[1].getX();
            y2 = result.getResultPoints()[1].getY();
            barcodeValue = result.getText();
            barcodeBoundary = BarcodeReader
                .getBarcodeBoundary(new Rectangle2D.Double(x1, y1, (x2 - x1), (y2 - y1)),
                    this.sourceImage, this.luminanceCutoff);
        }

    }

    private Result[] decodeMultiple(BinaryBitmap image, Hashtable hints) throws ReaderException {
        Vector results = new Vector();
        while (true) {
            try {
                Result result = delegate.decode(image, hints);
                results.add(result);
                if (result.getBarcodeFormat().equals(BarcodeFormat.QR_CODE)) {
                    return resultVectorToArray(results);
                }
            } catch (ReaderException re) {
                return resultVectorToArray(results);
            }
        }
    }

    private Result[] resultVectorToArray(Vector<Result> results) throws ReaderException {
        if (results.isEmpty()) {
            NotFoundException.getNotFoundInstance();
        }
        int numResults = results.size();
        Result[] resultArray = new Result[numResults];
        for (int i = 0; i < numResults; i++) {
            resultArray[i] = (Result) results.elementAt(i);
        }
        return resultArray;
    }

    public float getX2() {
        return x2;
    }

    public void setX2(float x2) {
        this.x2 = x2;
    }

    public String getBarcodeValue() {
        return barcodeValue;
    }

    public float getX1() {
        return x1;
    }

    public float getY1() {
        return y1;
    }

    public float getY2() {
        return y2;
    }

    public BufferedImage getSourceImage() {
        return sourceImage;
    }

    public Rectangle2D getBarcodeBoundary() {
        return barcodeBoundary;
    }

}
