package com.ebstrada.formreturn.manager.util.image;

import ij.ImagePlus;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import net.sf.ij.jaiio.ImagePlusCreator;
import net.sf.ij.jaiio.UnsupportedImageModelException;
import cern.colt.function.IntIntProcedure;
import cern.colt.map.OpenIntIntHashMap;

import com.sun.media.jai.codec.ByteArraySeekableStream;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.TIFFDirectory;


import com.sun.media.jai.codecimpl.TIFFImage;
import com.sun.media.jai.codecimpl.TIFFImageDecoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;

import com.ebstrada.blobextractor.ComponentLabeler;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.recognition.reader.FormReaderException;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;
import com.ebstrada.formreturn.manager.util.preferences.persistence.ApplicationStatePreferences;

public class ImageUtil {

    private static Log log = LogFactory.getLog(ImageUtil.class);

    public static final int WHITEOUT_BLACK_LUMINANCE_CUTOFF = 200;

    private static final int SEQUENTIAL_BLACK_THRESHOLD = 5;

    public ImageUtil() {
    }

    public static int getNumberOfPagesInPDF(File pdfFile) throws Exception {

        PDDocument document = null;

        int pageCount = 0;

        try {
            document = PDDocument.load(pdfFile);
            if (document.isEncrypted()) {
                throw new Exception("Cannot read an encrypted PDF file.");
            }
            pageCount = document.getDocumentCatalog().getPages().getCount();
        } catch (Exception e) {
            if (document != null) {
                document.close();
            }
            throw e;
        }

        if (document != null) {
            document.close();
        }

        return pageCount;

    }

    public static BufferedImage getPDFPageImage(File pdfFile, int pageNumber) throws Exception {

        PDDocument document = null;

        BufferedImage pageImage = null;

        try {

            document = PDDocument.load(pdfFile);

            if (document.isEncrypted()) {
                throw new Exception("Cannot read an encrypted PDF file.");
            }

            // TODO: what is this code for?
            /*
            PDPageTree pages = document.getPages();
            PDPage page = pages.get(pageNumber - 1);
            if (page.getContents() != null) {
                PDResources resources = page.getResources();
                Iterable<COSName> names = resources.getXObjectNames();
                for (COSName name : names) {
                    PDXObject xobj = resources.getXObject(name);
                    if (xobj instanceof PDImageXObject) {
                        PDImageXObject ximage = (PDImageXObject) xobj;
                        BufferedImage bi = ximage.getImage();
                        if (bi.getType() <= 0) {
                            BufferedImage bi2 = new BufferedImage(bi.getWidth(), bi.getHeight(),
                                BufferedImage.TYPE_INT_RGB);
                            bi2.getGraphics().drawImage(bi, 0, 0, null);
                            return bi2;
                        }
                        return bi;
                    }
                }
            }
            */

            PDFRenderer pdfRenderer = new PDFRenderer(document);
            pageImage = pdfRenderer.renderImageWithDPI(pageNumber - 1, 400);
        } catch (Exception e) {
            if (document != null) {
                document.close();
            }
            throw e;
        }

        if (document != null) {
            document.close();
        }

        return pageImage;

    }

    private static BufferedImage getPDFPageImage(byte[] originalImageData, int pageNumber)
        throws Exception {
        BufferedImage pageImage = null;

        PDDocument document = null;
        ByteArrayInputStream bais = null;

        try {
            bais = new ByteArrayInputStream(originalImageData);
            document = PDDocument.load(bais);
            if (document.isEncrypted()) {
                throw new Exception("Cannot read an encrypted PDF file.");
            }
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            pageImage = pdfRenderer.renderImageWithDPI(pageNumber - 1, 400);
        } catch (Exception e) {
            if (document != null) {
                document.close();
            }
            if (bais != null) {
                bais.close();
            }
            throw e;
        }

        if (document != null) {
            document.close();
        }
        if (bais != null) {
            bais.close();
        }

        return pageImage;
    }

    public static int getNumberOfPagesInTiff(byte[] originalImageData) throws Exception {
        SeekableStream ss = new ByteArraySeekableStream(originalImageData);
        String[] decoders = ImageCodec.getDecoderNames(ss);
        if (decoders == null || decoders.length <= 0) {
            try {
                return getNumberOfPagesInPDF(originalImageData);
            } catch (Exception ex) {
                String msg = Localizer.localize("Server", "InvalidImageFileFormatMessage");
                FormReaderException fre =
                    new FormReaderException(FormReaderException.INVALID_IMAGE_FORMAT, msg);
                throw fre;
            }
        }

        if (decoders[0].contains("jpeg") || decoders[0].contains("png")) {
            return 1;
        }

        ImageDecoder decoder = ImageCodec.createImageDecoder(decoders[0], ss, null);
        return decoder.getNumPages();
    }

    private static int getNumberOfPagesInPDF(byte[] originalImageData) throws Exception {
        PDDocument document = null;
        ByteArrayInputStream bais = null;
        int pageCount = 0;

        try {
            bais = new ByteArrayInputStream(originalImageData);
            document = PDDocument.load(bais);
            if (document.isEncrypted()) {
                throw new Exception("Cannot read an encrypted PDF file.");
            }
            pageCount = document.getDocumentCatalog().getPages().getCount();
        } catch (Exception e) {
            if (document != null) {
                document.close();
            }
            if (bais != null) {
                bais.close();
            }
            throw e;
        }

        if (document != null) {
            document.close();
        }

        if (bais != null) {
            bais.close();
        }

        return pageCount;
    }

    public static int getNumberOfPagesInTiff(File imageFile) throws Exception {

        String fileName = imageFile.getName();
        if (fileName.toLowerCase().endsWith(".pdf")) {
            return getNumberOfPagesInPDF(imageFile);
        }

        SeekableStream ss = new FileSeekableStream(imageFile);
        String[] decoders = ImageCodec.getDecoderNames(ss);
        if (decoders == null || decoders.length <= 0) {
            String msg = Localizer.localize("Server", "InvalidImageFileFormatMessage");
            FormReaderException fre =
                new FormReaderException(FormReaderException.INVALID_IMAGE_FORMAT, msg);
            throw fre;
        }
        ImageDecoder decoder = ImageCodec.createImageDecoder(decoders[0], ss, null);
        int numPages = decoder.getNumPages();
        ss.close();
        ss = null;
        return numPages;
    }

    public static BufferedImage readImage(byte[] originalImageData, int pageNumber)
        throws Exception {

        SeekableStream ss = new ByteArraySeekableStream(originalImageData);
        String[] decoders = ImageCodec.getDecoderNames(ss);

        if (decoders == null || decoders.length <= 0) {
            try {
                return getPDFPageImage(originalImageData, pageNumber);
            } catch (Exception ex) {
                String msg = Localizer.localize("Server", "InvalidImageFileFormatMessage");
                FormReaderException fre =
                    new FormReaderException(FormReaderException.INVALID_IMAGE_FORMAT, msg);
                throw fre;
            }
        }

        if (decoders[0].contains("jpeg")) {
            return ImageIO.read(new ByteArrayInputStream(originalImageData));
        }

        ImageDecoder decoder = ImageCodec.createImageDecoder(decoders[0], ss, null);
        int numPages = decoder.getNumPages();
        if (pageNumber <= numPages) {
            return convertRenderedImage(decoder.decodeAsRenderedImage(pageNumber - 1), decoders);
        } else {
            throw new Exception(String
                .format(Localizer.localize("Util", "ImageUtilCannotFindPageNumber"),
                    pageNumber + ""));
        }
    }

    public static BufferedImage readImage(File imageFile, int pageNumber) throws Exception {

        String fileName = imageFile.getName();
        if (fileName.toLowerCase().endsWith(".pdf")) {
            return getPDFPageImage(imageFile, pageNumber);
        }

        if (fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg")) {
            return ImageIO.read(imageFile);
        }

        if (fileName.toLowerCase().endsWith(".png")) {
            return ImageIO.read(imageFile);
        }

        SeekableStream ss = new FileSeekableStream(imageFile);
        String[] decoders = ImageCodec.getDecoderNames(ss);

        if (decoders == null || decoders.length <= 0) {
            String msg = Localizer.localize("Server", "InvalidImageFileFormatMessage");
            FormReaderException fre =
                new FormReaderException(FormReaderException.INVALID_IMAGE_FORMAT, msg);
            throw fre;
        }
        ImageDecoder decoder = ImageCodec.createImageDecoder(decoders[0], ss, null);
        int numPages = decoder.getNumPages();
        if (pageNumber <= numPages) {
            return convertRenderedImage(decoder.decodeAsRenderedImage(pageNumber - 1), decoders);
        } else {
            throw new Exception(String
                .format(Localizer.localize("Util", "ImageUtilCannotFindPageNumber"),
                    pageNumber + ""));
        }

    }

    public static BufferedImage binarizeImage(BufferedImage image, int luminanceCutOff,
        boolean despeckle) {

        BufferedImage newImg =
            new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);

        // just copy the image if it is already a binary image.
        if (image.getType() == BufferedImage.TYPE_BYTE_BINARY) {
            Graphics2D g2d = newImg.createGraphics();
            g2d.drawImage(image, 0, 0, null);
            newImg.flush();
            g2d.dispose();
            return newImg;
        }


        WritableRaster raster = newImg.getRaster();

        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        // int blackSpeckleCount = 0;
        // int whiteSpeckleCount = 0;

        final int BLACK = 0;
        final int WHITE = 1;

        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {

                if (isBlack(image, x, y, luminanceCutOff)) {
                    raster.setSample(x, y, 0, 0);
                } else {
                    raster.setSample(x, y, 0, 1);
                }

                if (despeckle) {

                    if (x < 4 || x >= (imageWidth - 4)) {
                        continue;
                    }

                    int sum = 0;

                    for (int offset = 0; offset < 5; offset++) {
                        if (offset == 2) {
                            continue;
                        }
                        int xpos = (x - offset);
                        sum += raster.getSample(xpos, y, 0);
                    }

                    int xpos = (x - 2);

                    // 0 = black
                    // 1 = white

                    if (xpos > 0) {
                        if (sum == 4) {
                            if (raster.getSample(xpos, y, 0) != WHITE) {
                                raster.setSample(xpos, y, 0, WHITE);
                                // ++blackSpeckleCount; // changed black speckle to white
                            }
                        } else if (sum == 0) {
                            if (raster.getSample(xpos, y, 0) != BLACK) {
                                raster.setSample(xpos, y, 0, BLACK);
                                // ++whiteSpeckleCount; // changed white speckle to black
                            }
                        }
                    }

                }

            }
        }

        if (despeckle) {

            // 0 = black
            // 1 = white

            int heightMax = imageHeight - 6;
            for (int y = 6; y < heightMax; y++) {
                for (int x = 0; x < imageWidth; x++) {

                    int sum = 0;

                    for (int offset = 0; offset < 7; offset++) {
                        if (offset == 3) {
                            continue;
                        }
                        int ypos = (y - offset);
                        sum += raster.getSample(x, ypos, 0);
                    }

                    int ypos = (y - 3);

                    if (ypos > 0) {
                        if (sum == 6) {
                            if (raster.getSample(x, ypos, 0) != WHITE) {
                                raster.setSample(x, ypos, 0, WHITE);
                                // ++blackSpeckleCount; // changed black speckle to white
                            }
                        } else if (sum == 0) {
                            if (raster.getSample(x, ypos, 0) != BLACK) {
                                raster.setSample(x, ypos, 0, BLACK);
                                // ++whiteSpeckleCount; // changed white speckle to black
                            }
                        }
                    }

                }
            }
	    
	    /*

	    System.out.println("blackSpeckleCount: " + blackSpeckleCount + " - whiteSpeckleCount: " + whiteSpeckleCount);
	    
	    double totalPixelCount = imageWidth * imageHeight;
	    
	    double blackSpecklePercent = 0;
	    double whiteSpecklePercent = 0;
	    
	    blackSpecklePercent = (blackSpeckleCount / totalPixelCount) * 100.0d;
	    whiteSpecklePercent = (whiteSpeckleCount / totalPixelCount) * 100.0d;
	    
	    System.out.println("black speckle percent: " + blackSpecklePercent + " - white speckle percent: " + whiteSpecklePercent);
	    
	    */

        }

        newImg.flush();

        return newImg;
    }

    public static BufferedImage binarizeImageAndInvert(BufferedImage image, int luminanceCutOff) {

        BufferedImage newImg =
            new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        int blackCount = 0;
        int whiteCount = 0;

        WritableRaster raster = newImg.getRaster();

        int imageSize = image.getWidth() * image.getHeight();
        int imageWidth = image.getWidth();
        for (int i = 0; i < imageSize; i++) {
            int y = i / imageWidth;
            int x = i - (y * imageWidth);
            if (isBlack(image, x, y, luminanceCutOff)) {
                raster.setSample(x, y, 0, 1);
                blackCount++;
            } else {
                raster.setSample(x, y, 0, 0);
                whiteCount++;
            }
        }

        if (whiteCount > blackCount) {
            return image;
        }

        newImg.flush();
        return newImg;
    }

    public static BufferedImage oldConvertRenderedImage(RenderedImage img, String[] decoders) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        
	/*
	ColorModel cm = img.getColorModel();
	int width = img.getWidth();
	int height = img.getHeight();
	WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
	boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
	Hashtable properties = new Hashtable();
	String[] keys = img.getPropertyNames();
	if (keys!=null) {
	    for (int i = 0; i < keys.length; i++) {
		properties.put(keys[i], img.getProperty(keys[i]));
	    }
	}
	BufferedImage result = new BufferedImage(cm, raster, isAlphaPremultiplied, properties);
	img.copyData(raster);
	return result;
	*/

        ColorModel cm = img.getColorModel();
        int width = img.getWidth();
        int height = img.getHeight();
        WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
        BufferedImage preimg = new BufferedImage(cm, raster, false, null);
        img.copyData(raster);

        if (img instanceof TIFFImage) {
            TIFFImage ti = (TIFFImage) img;
            try {
                Object o = ti.getProperty("tiff_directory");
                if (o instanceof TIFFDirectory) {

                    TIFFDirectory dir = (TIFFDirectory) o;

                    int compression = (int) dir.getFieldAsLong(TIFFImageDecoder.TIFF_COMPRESSION);
                    switch (compression) {
                        case TIFFImage.COMP_FAX_G3_1D:
                        case TIFFImage.COMP_FAX_G3_2D:
                        case TIFFImage.COMP_FAX_G4_2D:
                            BufferedImageOp op =
                                new AffineTransformOp(AffineTransform.getScaleInstance(1, 2),
                                    new RenderingHints(RenderingHints.KEY_INTERPOLATION,
                                        RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR));
                            return op.filter(preimg, null);
                        default:
                    }

                }
            } catch (Exception ex) {
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
            }
        }

        return preimg;
    }

    public static void invertBinary(BufferedImage src) {

        if (src.getType() != BufferedImage.TYPE_BYTE_BINARY) {
            throw new IllegalArgumentException(
                Localizer.localize("Util", "ImageUtilNotABinaryImageMessage"));
        }

        byte[] data = ((java.awt.image.DataBufferByte) src.getRaster().getDataBuffer()).getData();

        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (~data[i] & 0xff);
        }
    }

    public static BufferedImage convertRenderedImage(RenderedImage img, String[] decoders)
        throws UnsupportedImageModelException {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        WritableRaster wr = ImagePlusCreator.forceTileUpdate(img);
        ImagePlus im;

        if (decoders[0].equalsIgnoreCase("GIF") || decoders[0].equalsIgnoreCase("JPEG")) {
            // Convert the way ImageJ does (ij.io.Opener.openJpegOrGif())
            BufferedImage bi = new BufferedImage(img.getColorModel(), wr, false, null);
            im = ImagePlusCreator.create(wr, img.getColorModel());
            im.setImage(bi);
	    /*
	    if (im.getType() == ImagePlus.COLOR_RGB) {
		// Convert RGB to gray if all bands are equal
		Opener.convertGrayJpegTo8Bits(im);
	    }
	    */
        } else {
            im = ImagePlusCreator.create(wr, img.getColorModel());
	    /*
	    if (im.getType() == ImagePlus.COLOR_RGB) {
		// Convert RGB to gray if all bands are equal
		Opener.convertGrayJpegTo8Bits(im);
	    }
	    */

            if (img instanceof TIFFImage) {
                TIFFImage ti = (TIFFImage) img;
                try {
                    Object o = ti.getProperty("tiff_directory");
                    if (o instanceof TIFFDirectory) {

                        TIFFDirectory dir = (TIFFDirectory) o;


			/*
			 * This code is a test of the despeckle filter in imagej
			 * 
			 * and 0.2 or 0.1 instead of 1 as the radius
			 * 
                    RankFilters filter = new RankFilters();
                    filter.rank(im.getProcessor(), 1, RankFilters.MEDIAN);
                    im.updateAndDraw(); 
			 */

                        BufferedImage preimg = im.getBufferedImage();

                        int compression =
                            (int) dir.getFieldAsLong(TIFFImageDecoder.TIFF_COMPRESSION);
                        switch (compression) {
                            case TIFFImage.COMP_FAX_G3_1D:
                            case TIFFImage.COMP_FAX_G3_2D:
                            case TIFFImage.COMP_FAX_G4_2D:

                                // resize image to the same scale
                                if (dir.isTagPresent(TIFFImageDecoder.TIFF_X_RESOLUTION) && dir
                                    .isTagPresent(TIFFImageDecoder.TIFF_Y_RESOLUTION)) {
                                    double x_res =
                                        dir.getFieldAsDouble(TIFFImageDecoder.TIFF_X_RESOLUTION);
                                    double y_res =
                                        dir.getFieldAsDouble(TIFFImageDecoder.TIFF_Y_RESOLUTION);

                                    double x_scale = 1.0d;
                                    double y_scale = 1.0d;

                                    if (x_res != y_res) {
                                        if (x_res > y_res) {
                                            y_scale = x_res / y_res;
                                        } else if (y_res > x_res) {
                                            x_scale = y_res / x_res;
                                        }

                                        BufferedImageOp op = new AffineTransformOp(
                                            AffineTransform.getScaleInstance(x_scale, y_scale),
                                            new RenderingHints(RenderingHints.KEY_INTERPOLATION,
                                                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR));
                                        preimg = op.filter(preimg, null);

                                    }

                                }

                                if (dir.isTagPresent(
                                    TIFFImageDecoder.TIFF_PHOTOMETRIC_INTERPRETATION)) {
                                    long photo = dir.getFieldAsLong(
                                        TIFFImageDecoder.TIFF_PHOTOMETRIC_INTERPRETATION);
                                    if (photo == 1) { // 1 = min-is-black ... 0 = min-is-white
                                        preimg = binarizeImageAndInvert(preimg, 165);
                                    }
                                }

                                return preimg;

                            default:
                                return im.getBufferedImage();
                        }

                    }
                } catch (Exception ex) {
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
                }
            }

        }

        return im.getBufferedImage();

    }

    public static boolean isBlack2(BufferedImage image, int x, int y, int luminanceCutOff) {

        // return white on areas outside of image boundaries
        if ((x < 0) || (y < 0) || (x > (image.getWidth() - 1)) || (y > (image.getHeight() - 1))) {
            return false;
        }
	
	/*
	if ( image.getType() == BufferedImage.TYPE_BYTE_BINARY ) {    
	    WritableRaster raster = image.getRaster();
	    int pixelRGBValue = raster.getSample(x, y, 0);
	    if ( pixelRGBValue == 0 ) {
		return true;
	    } else {
		return false;
	    }
	}
	*/

        int pixelRGBValue;
        int r;
        int g;
        int b;
        double luminance = 0.0;

        try {
            pixelRGBValue = image.getRGB(x, y);
            r = (pixelRGBValue >> 16) & 0xff;
            g = (pixelRGBValue >> 8) & 0xff;
            b = (pixelRGBValue >> 0) & 0xff;
            luminance = (r * 0.299) + (g * 0.587) + (b * 0.114);
        } catch (Exception e) {
            // ignore.
        }

        if (luminance < luminanceCutOff) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isBlack(BufferedImage image, int x, int y, int luminanceCutOff) {

        // return white on areas outside of image boundaries
        if ((x < 0) || (y < 0) || (x > (image.getWidth() - 1)) || (y > (image.getHeight() - 1))) {
            return false;
        }

        if (image.getType() == BufferedImage.TYPE_BYTE_BINARY) {
            WritableRaster raster = image.getRaster();
            int pixelRGBValue = raster.getSample(x, y, 0);
            if (pixelRGBValue == 0) {
                return true;
            } else {
                return false;
            }
        }

        int pixelRGBValue;
        int r;
        int g;
        int b;
        double luminance = 0.0;

        try {
            pixelRGBValue = image.getRGB(x, y);
            r = (pixelRGBValue >> 16) & 0xff;
            g = (pixelRGBValue >> 8) & 0xff;
            b = (pixelRGBValue >> 0) & 0xff;
            luminance = (r * 0.299) + (g * 0.587) + (b * 0.114);
        } catch (Exception e) {
            // ignore.
        }

        return (luminance < luminanceCutOff);
    }

    public static BlobExtractor getBlobExtraction(BufferedImage image, int rowCount,
        int columnCount, boolean debug) {

        // binarize
        boolean[] src = new boolean[(image.getWidth() * image.getHeight())];

        if (image.getType() != BufferedImage.TYPE_BYTE_BINARY) {
            int position = 0;
            for (int i = 0; i < image.getHeight(); i++) {
                for (int j = 0; j < image.getWidth(); j++) {
                    if (ImageUtil.isBlack(image, j, i, 200)) {
                        src[position] = true;
                    } else {
                        src[position] = false;
                    }
                    ++position;
                }
            }
        } else {
            int position = 0;
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    if (image.getRaster().getSample(x, y, 0) == 0) {
                        src[position] = true;
                    } else {
                        src[position] = false;
                    }
                    ++position;
                }
            }
        }

        // extract
        BlobExtractor be =
            new ContourBlobExtraction(src, image.getWidth(), rowCount, columnCount, debug);

        return be;

    }

    public static BufferedImage rotate(BufferedImage image, double angle, int cx, int cy) {
        int width = image.getWidth(null);
        int height = image.getHeight(null);

        int minX, minY, maxX, maxY;
        minX = minY = maxX = maxY = 0;

        int[] corners = {0, 0, width, 0, width, height, 0, height};

        double theta = Math.toRadians(angle);
        for (int i = 0; i < corners.length; i += 2) {
            int x =
                (int) (Math.cos(theta) * (corners[i] - cx) - Math.sin(theta) * (corners[i + 1] - cy)
                    + cx);
            int y =
                (int) (Math.sin(theta) * (corners[i] - cx) + Math.cos(theta) * (corners[i + 1] - cy)
                    + cy);

            if (x > maxX) {
                maxX = x;
            }

            if (x < minX) {
                minX = x;
            }

            if (y > maxY) {
                maxY = y;
            }

            if (y < minY) {
                minY = y;
            }

        }

        cx = (cx - minX);
        cy = (cy - minY);

        BufferedImage bi = new BufferedImage((maxX - minX), (maxY - minY), image.getType());
        Graphics2D g2 = bi.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
            RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

        g2.setBackground(Color.white);
        g2.fillRect(0, 0, bi.getWidth(), bi.getHeight());

        AffineTransform at = new AffineTransform();
        at.rotate(theta, cx, cy);

        g2.setTransform(at);
        g2.drawImage(image, -minX, -minY, null);
        g2.dispose();

        return bi;

    }

    public static byte[] getPNGByteArray(BufferedImage sourceImage) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] binarizedImageData = null;
        ImageIO.write(sourceImage, "png", baos);
        binarizedImageData = baos.toByteArray();
        if (baos != null) {
            try {
                baos.close();
            } catch (IOException e) {
            }
        }
        return binarizedImageData;
    }

    public static BufferedImage getRGBBufferedImage(boolean[] binarizedImage, int imageWidth) {

        GraphicsDevice gd =
            GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();

        int imageHeight = binarizedImage.length / imageWidth;
        BufferedImage newImg =
            gc.createCompatibleImage(imageWidth, imageHeight, Transparency.OPAQUE);

        int white = Color.white.getRGB();
        int black = Color.black.getRGB();

        for (int i = 0; i < binarizedImage.length; i++) {
            int y = i / imageWidth;
            int x = i % imageWidth;
            if (binarizedImage[i] == true) {
                newImg.setRGB(x, y, black);
            } else {
                newImg.setRGB(x, y, white);
            }
        }

        newImg.flush();
        return newImg;

    }

    public static BufferedImage getBufferedImage(boolean[] binarizedImage, int imageWidth) {

        GraphicsDevice gd =
            GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();

        int imageHeight = binarizedImage.length / imageWidth;
        BufferedImage newImg =
            gc.createCompatibleImage(imageWidth, imageHeight, Transparency.OPAQUE);

        int white = Color.white.getRGB();
        int black = Color.black.getRGB();

        for (int i = 0; i < binarizedImage.length; i++) {
            int y = i / imageWidth;
            int x = i % imageWidth;
            if (binarizedImage[i] == true) {
                newImg.setRGB(x, y, black);
            } else {
                newImg.setRGB(x, y, white);
            }
        }

        newImg.flush();
        return newImg;

    }

    public static BufferedImage resizePageImageIfTooBig(int maxSize, BufferedImage sourceImage) {

        if (sourceImage.getWidth() > maxSize || sourceImage.getHeight() > maxSize) {

            int width = 0;
            int height = 0;
            if (sourceImage.getWidth() > sourceImage.getHeight()) {
                width = maxSize;
                height = (int) Math.round(
                    ((double) maxSize / (double) sourceImage.getWidth()) * (double) sourceImage
                        .getHeight());
            } else {
                height = maxSize;
                width = (int) Math.round(
                    ((double) maxSize / (double) sourceImage.getHeight()) * (double) sourceImage
                        .getWidth());
            }

            BufferedImage bufferedImage = new BufferedImage(width, height, sourceImage.getType());
            Graphics2D g = bufferedImage.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            AffineTransform at = AffineTransform
                .getScaleInstance((double) width / sourceImage.getWidth(),
                    (double) height / sourceImage.getHeight());
            g.drawRenderedImage(sourceImage, at);
            bufferedImage.flush();
            g.dispose();
            return bufferedImage;

        } else {
            return null;
        }
    }

    public static BufferedImage getCompatibleBufferedImage(BufferedImage binarizedImage) {

        GraphicsDevice gd =
            GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();

        BufferedImage bufferedImage =
            gc.createCompatibleImage(binarizedImage.getWidth(), binarizedImage.getHeight(),
                Transparency.OPAQUE);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(binarizedImage, 0, 0, null);
        bufferedImage.flush();
        g2d.dispose();
        return bufferedImage;

    }

    public static BufferedImage blurImage(BufferedImage bi) {
        ApplicationStatePreferences appState = PreferencesManager.getApplicationState();
        if (appState.isBlurIncomingImages() != true) {
            return bi;
        }
        BufferedImage biDest =
            new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_RGB);

        float data[] = {0.111f, 0.111f, 0.111f, 0.111f, 0.111f, 0.111f, 0.111f, 0.111f, 0.111f};

        Kernel kernel = new Kernel(3, 3, data);
        ConvolveOp convolve = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        convolve.filter(bi, biDest);
        return biDest;
    }

    public static BufferedImage removeBlackBorders(BufferedImage bi) {

        int width = bi.getWidth();
        int height = bi.getHeight();

        int x = 1;
        int y = 1;

        int sequentialBlack = 0;

        // increment x until width - 1 is reached
        for (; x < (width - 1); x++) {
            if (isBlack2(bi, x, y, WHITEOUT_BLACK_LUMINANCE_CUTOFF)) {
                sequentialBlack++;
            } else {
                continue;
            }
            if (sequentialBlack > SEQUENTIAL_BLACK_THRESHOLD) {
                whiteoutBorders(bi, sequentialBlack, x, y);
                return bi;
            }
        }

        // increment y until height - 1 is reached
        for (; y < (height - 1); y++) {
            if (isBlack2(bi, x, y, WHITEOUT_BLACK_LUMINANCE_CUTOFF)) {
                sequentialBlack++;
            } else {
                continue;
            }
            if (sequentialBlack > SEQUENTIAL_BLACK_THRESHOLD) {
                whiteoutBorders(bi, sequentialBlack, x, y);
                return bi;
            }
        }

        // decrement x until 0 is reached
        for (; x >= 1; x--) {
            if (isBlack2(bi, x, y, WHITEOUT_BLACK_LUMINANCE_CUTOFF)) {
                sequentialBlack++;
            } else {
                continue;
            }
            if (sequentialBlack > SEQUENTIAL_BLACK_THRESHOLD) {
                whiteoutBorders(bi, sequentialBlack, x, y);
                return bi;
            }
        }

        // decrement y until 0 is reached
        for (; y >= 1; y--) {
            if (isBlack2(bi, x, y, WHITEOUT_BLACK_LUMINANCE_CUTOFF)) {
                sequentialBlack++;
            } else {
                continue;
            }
            if (sequentialBlack > SEQUENTIAL_BLACK_THRESHOLD) {
                whiteoutBorders(bi, sequentialBlack, x, y);
                return bi;
            }
        }

        return bi;

    }

    private static void whiteoutBorders(BufferedImage image, int sequentialBlack, int lastX,
        int lastY) {

        System.out.println(
            "sequentail black found: " + sequentialBlack + " before and at: " + lastX + ","
                + lastY);

        int width = image.getWidth();
        int height = image.getHeight();

        // binarize
        boolean[] src = new boolean[(image.getWidth() * image.getHeight())];

        if (image.getType() != BufferedImage.TYPE_BYTE_BINARY) {
            int position = 0;
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (ImageUtil.isBlack(image, j, i, 200)) {
                        src[position] = true;
                    } else {
                        src[position] = false;
                    }
                    ++position;
                }
            }
        } else {
            int position = 0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (image.getRaster().getSample(x, y, 0) == 0) {
                        src[position] = true;
                    } else {
                        src[position] = false;
                    }
                    ++position;
                }
            }
        }

        ComponentLabeler componentLabeler =
            new ComponentLabeler(src, image.getWidth(), true, true, 100);
        componentLabeler.parse();
        OpenIntIntHashMap labels = componentLabeler.getPixelPositionLabels();

        int x = 0;
        int y = 0;
        int position = 0;

        for (; x < width; x++) {
            position = ((y + 1) * width) + (x + 1);
            if (labels.containsKey(position)) {
                floodFillLabel(image, componentLabeler, position, labels.get(position));
            }
        }
        for (; y < height; y++) {
            position = ((y + 1) * width) + (x + 1);
            if (labels.containsKey(position)) {
                floodFillLabel(image, componentLabeler, position, labels.get(position));
            }
        }
        for (; x >= 0; x--) {
            position = ((y + 1) * width) + (x + 1);
            if (labels.containsKey(position)) {
                floodFillLabel(image, componentLabeler, position, labels.get(position));
            }
        }
        for (; y >= 0; y--) {
            position = ((y + 1) * width) + (x + 1);
            if (labels.containsKey(position)) {
                floodFillLabel(image, componentLabeler, position, labels.get(position));
            }
        }

    }

    private static void floodFillLabel(final BufferedImage image, ComponentLabeler componentLabeler,
        int position, int label) {

        final OpenIntIntHashMap positionLabels = componentLabeler.getPixelPositionLabels();

        IntIntProcedure condition = new IntIntProcedure() {

            public int firstLabel = -1;

            public boolean apply(int key, int value) {
                if (firstLabel == -1) {
                    firstLabel = value;
                }
                if (value == firstLabel) {
                    whiten(image, key);
                    positionLabels.removeKey(key);
                    return true;
                } else {
                    return false;
                }
            }
        };

        positionLabels.forEachPair(condition);

    }

    private static void whiten(BufferedImage image, int position) {
        int width = image.getWidth();
        int height = image.getHeight();

        int blobWidth = width + 2;
        int blobHeight = height + 2;

        int blobX = position % blobWidth;
        int blobY = position % blobHeight;

        if (blobX < width && blobY < height) {
            image.setRGB(blobX, blobY, Color.WHITE.getRGB());
        }
    }

}
