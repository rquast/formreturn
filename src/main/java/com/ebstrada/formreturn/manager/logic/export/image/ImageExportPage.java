package com.ebstrada.formreturn.manager.logic.export.image;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import org.apache.pdfbox.pdmodel.common.PDRectangle;

import com.ebstrada.formreturn.manager.util.graph.SizeAttributes;

public class ImageExportPage {

    private BufferedImage img;

    private PDRectangle pageRectangle;

    private SizeAttributes sizeAttributes;

    private boolean rotated = false;

    public ImageExportPage(BufferedImage img, PDRectangle pageRectangle,
        SizeAttributes sizeAttributes) {
        this.img = img;
        this.pageRectangle = pageRectangle;
        this.sizeAttributes = sizeAttributes;
    }

    public void rotate() {
        AffineTransform tx = new AffineTransform();
        tx.translate(img.getHeight() / 2, img.getWidth() / 2);
        tx.rotate((Math.PI / 2) * -1);
        tx.translate(-img.getWidth() / 2, -img.getHeight() / 2);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage newimg = new BufferedImage(img.getHeight(), img.getWidth(), img.getType());
        op.filter(img, newimg);
        this.img = newimg;
        this.img.flush();
        this.rotated = true;
    }

    public BufferedImage getImage() {
        return this.img;
    }

    public float getWidth() {
        return (pageRectangle.getUpperRightX() - pageRectangle.getLowerLeftX()) - (
            this.sizeAttributes.getLeftMargin() + this.sizeAttributes.getRightMargin());
    }

    public float getHeight() {
        float scale = getWidth() / (float) this.img.getWidth();
        return ((float) this.img.getHeight() * scale);
    }

    public float getLowerLeftX() {
        return pageRectangle.getLowerLeftX() + (float) this.sizeAttributes.getLeftMargin();
    }

    public float getLowerLeftY() {
        if (this.rotated) {
            return (pageRectangle.getUpperRightY() - pageRectangle.getLowerLeftY()) - getHeight();
        } else {
            return pageRectangle.getLowerLeftY();
        }
    }

    public PDRectangle getPageRectangle() {
        return this.pageRectangle;
    }

}
