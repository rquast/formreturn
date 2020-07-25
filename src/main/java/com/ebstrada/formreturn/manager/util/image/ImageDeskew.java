package com.ebstrada.formreturn.manager.util.image;

import java.awt.image.BufferedImage;

/**
 * Image Deskew Class
 */
public class ImageDeskew {

    // the source image
    private BufferedImage sourceImage;

    // the range of angles to search for lines
    private double cAlphaStart = -20;
    private double cAlphaStep = 0.2;
    private int cSteps = 40 * 5;

    // pre-calculation of sin and cos
    private double[] cSinA;
    private double[] cCosA;

    // range of d
    private double cDMin;
    private double cDStep = 1.0;
    private int cDCount;

    // count of points that fit in a line
    private int[] cHMatrix;

    private short luminanceThreshold;

    private BufferedImage binarizedImage;


    // representation of a line in the image
    public class HoughLine {

        // count of points in the line
        public int count = 0;

        // index in matrix.
        public int index = 0;

        // the line is represented as all x, y that solve y * cos(alpha) - x *
        // sin(alpha) = d
        public double alpha;
        public double d;

    }

    // constructor
    public ImageDeskew(BufferedImage sourceImage, short luminanceThreshold, boolean despeckle) {
        this.sourceImage = sourceImage;
        this.luminanceThreshold = luminanceThreshold;
        binarizedImage = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(),
            BufferedImage.TYPE_BYTE_BINARY);
        if (sourceImage.getType() == BufferedImage.TYPE_BYTE_BINARY) {
            binarizedImage =
                sourceImage.getSubimage(0, 0, sourceImage.getWidth(), sourceImage.getHeight());
        } else {
            binarizedImage =
                ImageUtil.binarizeImage(this.sourceImage, this.luminanceThreshold, despeckle);
        }
    }

    // calculate the skew angle of the image cImage
    public double getSkewAngle() throws Exception {
        ImageDeskew.HoughLine[] hl;
        double sum = 0.0;
        double count = 0.0;

        // perform Hough Transformation
        calc();

        // top 20 of the detected lines in the image
        hl = getTop(20);

        if (hl.length >= 20) {

            // average angle of the lines
            for (int i = 0; i < 19; i++) {
                sum += hl[i].alpha;
                count += 1.0;
            }

            return (sum / count);

        } else {
            return 0.0d;
        }

    }

    // calculate the count lines in the image with most points
    private ImageDeskew.HoughLine[] getTop(int count) {

        ImageDeskew.HoughLine[] hl;
        hl = new ImageDeskew.HoughLine[count];
        for (int i = 0; i < count; i++) {
            hl[i] = new ImageDeskew.HoughLine();
        }

        ImageDeskew.HoughLine tmp;
        int j = 0;
        int alphaIndex;
        int dIndex;

        for (int i = 0; i < (count - 1); i++) {
            hl[i] = new ImageDeskew.HoughLine();
        }

        for (int i = 0; i < (this.cHMatrix.length - 1); i++) {
            if (this.cHMatrix[i] > hl[count - 1].count) {
                hl[count - 1].count = this.cHMatrix[i];
                hl[count - 1].index = i;
                j = count - 1;
                while ((j > 0) && (hl[j].count > hl[j - 1].count)) {
                    tmp = hl[j];
                    hl[j] = hl[j - 1];
                    hl[j - 1] = tmp;
                    j -= 1;
                }
            }
        }

        for (int i = 0; i < (count - 1); i++) {
            dIndex = hl[i].index / this.cSteps; // integer division, no
            // remainder
            alphaIndex = hl[i].index - dIndex * this.cSteps;
            hl[i].alpha = getAlpha(alphaIndex);
            hl[i].d = dIndex + this.cDMin;
        }

        return hl;

    }

    // Hough Transformation
    private void calc() throws Exception {

        int hMin = 2; // (int) ((this.cImage.getHeight()) / 4.0);
        int hMax = (int) (this.sourceImage.getHeight())
            - 2; // (int) ((this.cImage.getHeight()) * 3.0 / 4.0);

        init();

        if (hMin >= hMax) {
            throw new Exception();
        }

        for (int y = hMin; y < hMax; y++) {
            for (int x = 1; x < (this.sourceImage.getWidth() - 2); x++) {
                // only lower edges are considered
                if (isBlack(x, y)) {
                    if (!isBlack(x, (y + 1))) {
                        calc(x, y);
                    }
                }
            }
        }

    }

    private boolean isBlack(int x, int y) {
        if (binarizedImage.getRaster().getSample(x, y, 0) == 0) {
            return true;
        } else {
            return false;
        }
    }

    // calculate all lines through the point (x,y)
    private void calc(int x, int y) throws Exception {
        double d;
        int dIndex;
        int index;

        for (int alpha = 0; alpha < (this.cSteps - 1); alpha++) {
            d = y * this.cCosA[alpha] - x * this.cSinA[alpha];
            dIndex = (int) (d - this.cDMin);
            index = dIndex * this.cSteps + alpha;
            this.cHMatrix[index] += 1;
        }
    }

    private void init() {

        double angle;

        // pre-calculation of sin and cos
        this.cSinA = new double[this.cSteps - 1];
        this.cCosA = new double[this.cSteps - 1];

        for (int i = 0; i < (this.cSteps - 1); i++) {
            angle = getAlpha(i) * Math.PI / 180.0;
            this.cSinA[i] = Math.sin(angle);
            this.cCosA[i] = Math.cos(angle);
        }

        // range of d
        this.cDMin = -this.sourceImage.getWidth();
        this.cDCount = (int) (2.0 * ((this.sourceImage.getWidth() + this.sourceImage.getHeight()))
            / this.cDStep);
        this.cHMatrix = new int[this.cDCount * this.cSteps];

    }

    public double getAlpha(int index) {
        return this.cAlphaStart + (index * this.cAlphaStep);
    }

    public BufferedImage getBinarizedImage() {
        return binarizedImage;
    }

}
