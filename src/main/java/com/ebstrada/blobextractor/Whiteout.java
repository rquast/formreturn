package com.ebstrada.blobextractor;

import java.awt.Color;
import java.awt.image.BufferedImage;

import com.ebstrada.formreturn.manager.util.image.ImageUtil;

public class Whiteout {

    public static final int[] DIRECTIONS = new int[] {0, 1, 2, 3, 4, 5, 6, 7};

    public static final int ISOLATED = 8;

    private BufferedImage bufferedImage;

    private int whiteRGB = Color.WHITE.getRGB();

    private int width;

    private int height;

    // current label value
    private int c = 1;

    // current position
    private int p = 0;

    private int length;

    public Whiteout(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
        this.width = bufferedImage.getWidth();
        this.length = bufferedImage.getWidth() * bufferedImage.getHeight();
        this.height = bufferedImage.getHeight();
    }

    public void parse(int position) {
        this.p = position;
        int lastDirection = 5; // 7 is the start direction for an externalcontour
        turnWhite(this.p);
        traceContour(this.p, lastDirection);
    }


    private void turnWhite(int position) {
        turnWhite(position % width, position / height);
    }

    private void turnWhite(int x, int y) {
        this.bufferedImage.setRGB(x, y, whiteRGB);
    }

    private void traceContour(int position, int direction) {

        int startPosition = position;
        int secondPosition = -1;
        boolean tracing = true;

        while (tracing) {

            direction = traceDirection((direction + 2) % 8, position);

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
    private int traceDirection(int startDirection, int position) {

        int end = startDirection + 8;

        for (int i = startDirection; i < end; i++) {

            int lookDirection = i % 8;
            int lookPosition = getNextPosition(position, lookDirection);

            if (lookPosition < length && lookPosition >= 0) {
                if (isBlack(lookPosition)) {
                    turnWhite(lookPosition);
                    return lookDirection;
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

    private boolean isBlack(int x, int y) {
        return ImageUtil.isBlack2(bufferedImage, x, y, ImageUtil.WHITEOUT_BLACK_LUMINANCE_CUTOFF);
    }

    private boolean isBlack(int position) {
        return isBlack(position % width, position / height);
    }

}
