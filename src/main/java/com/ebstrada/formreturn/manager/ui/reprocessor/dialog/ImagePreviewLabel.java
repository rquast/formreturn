package com.ebstrada.formreturn.manager.ui.reprocessor.dialog;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;

public class ImagePreviewLabel extends JLabel implements MouseListener {

    private SegmentStencilEditorDialog sasd;

    public ImagePreviewLabel() {
        setDoubleBuffered(true);
        addMouseListener(this);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (sasd != null) {
            sasd.renderFragmentImage((Graphics2D) g);
        }
    }

    public void mouseClicked(MouseEvent e) {
        // this shows the point on the image that was clicked if needed.
        // e.getPoint();
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public SegmentStencilEditorDialog getSasd() {
        return sasd;
    }

    public void setSasd(SegmentStencilEditorDialog sasd) {
        this.sasd = sasd;
        revalidate();
    }

}
