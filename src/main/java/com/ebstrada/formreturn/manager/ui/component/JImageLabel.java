package com.ebstrada.formreturn.manager.ui.component;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;

import com.ebstrada.formreturn.manager.ui.dialog.BarcodeDetectionDetailsDialog;
import com.ebstrada.formreturn.manager.ui.dialog.MarkDetectionDetailsDialog;
import com.ebstrada.formreturn.manager.ui.editor.RecognitionPreviewPanel;
import com.ebstrada.formreturn.manager.ui.reprocessor.dialog.AdjustImageDialog;

public class JImageLabel extends JLabel implements MouseListener {

    private static final long serialVersionUID = 1L;

    public static final int RENDER_PREVIEW = 1;
    public static final int RENDER_TEST_IMAGE = 2;
    public static final int RENDER_MARK_AREA_IMAGE = 3;
    public static final int RENDER_CAPTURED_IMAGE_PREVIEW = 4;
    public static final int RENDER_BARCODE_AREA_IMAGE = 5;
    public static final int RENDER_BLOB_EXTRACTION_DEBUG_IMAGE = 6;
    public static final int RENDER_REPROCESSOR_IMAGE_PREVIEW = 7;

    private int renderType = RENDER_PREVIEW;

    private RecognitionPreviewPanel viewer;

    private MarkDetectionDetailsDialog mddd;

    private BarcodeDetectionDetailsDialog bddd;

    private ImagePreviewPanel ipd;

    private BlobExtractionDebugPanel bed;

    private AdjustImageDialog aid;

    public JImageLabel() {
        setDoubleBuffered(true);
        addMouseListener(this);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (viewer != null) {
            if (renderType == RENDER_PREVIEW) {
                viewer.renderPreview((Graphics2D) g);
            } else if (renderType == RENDER_TEST_IMAGE) {
                viewer.renderTestImage((Graphics2D) g);
            }
        } else if (mddd != null) {
            if (renderType == RENDER_MARK_AREA_IMAGE) {
                mddd.renderMarkAreaImage((Graphics2D) g);
            }
        } else if (ipd != null) {
            if (renderType == RENDER_CAPTURED_IMAGE_PREVIEW) {
                ipd.renderImagePreview((Graphics2D) g);
            }
        } else if (bddd != null) {
            if (renderType == RENDER_BARCODE_AREA_IMAGE) {
                bddd.renderBarcodeAreaImage((Graphics2D) g);
            }
        } else if (bed != null) {
            if (renderType == RENDER_BLOB_EXTRACTION_DEBUG_IMAGE) {
                bed.renderImagePreview((Graphics2D) g);
            }
        } else if (aid != null) {
            if (renderType == RENDER_REPROCESSOR_IMAGE_PREVIEW) {
                aid.renderImagePreview((Graphics2D) g);
            }
        }

    }

    public void setViewer(RecognitionPreviewPanel viewer) {
        this.viewer = viewer;
        revalidate();
    }

    public int getRenderType() {
        return renderType;
    }

    public void setRenderType(int renderType) {
        this.renderType = renderType;
    }

    public void mouseClicked(MouseEvent e) {
        if (viewer != null) {
            if (renderType == RENDER_PREVIEW) {
                viewer.previewClicked(e.getPoint(), e.isShiftDown());
            } else if (renderType == RENDER_TEST_IMAGE) {
                viewer.testImageClicked(e.getPoint());
            }
        } else if (ipd != null) {
            if (renderType == RENDER_CAPTURED_IMAGE_PREVIEW) {
                ipd.imagePreviewClicked(e.getPoint(), e.isShiftDown());
            }
        } else if (bed != null) {
            if (renderType == RENDER_BLOB_EXTRACTION_DEBUG_IMAGE) {
                bed.imagePreviewClicked(e.getPoint());
            }
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void setMddd(MarkDetectionDetailsDialog mddd) {
        this.mddd = mddd;
        revalidate();
    }

    public void setIpd(ImagePreviewPanel ipd) {
        this.ipd = ipd;
        revalidate();
    }

    public void setBddd(BarcodeDetectionDetailsDialog bddd) {
        this.bddd = bddd;
        revalidate();
    }

    public void setBed(BlobExtractionDebugPanel blobExtractionDebugPanel) {
        this.bed = blobExtractionDebugPanel;
        revalidate();
    }

    public void setAid(AdjustImageDialog aid) {
        this.aid = aid;
        revalidate();
    }

}
