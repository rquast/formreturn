package com.ebstrada.formreturn.manager.gef.presentation;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.output.java2d.Java2DCanvasProvider;

import com.ebstrada.formreturn.manager.gef.base.Layer;
import com.ebstrada.formreturn.manager.gef.graph.MutableGraphSupport;
import com.ebstrada.formreturn.manager.gef.ui.PageAttributes;
import com.ebstrada.formreturn.manager.gef.undo.Memento;
import com.ebstrada.formreturn.manager.gef.undo.UndoManager;
import com.ebstrada.formreturn.manager.gef.undo.memento.FigAddMemento;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.editor.panel.FigBarcodePanel;
import com.ebstrada.formreturn.manager.ui.editor.panel.EditorPanel;
import com.ebstrada.formreturn.manager.util.BarcodeCreator;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("barcode") public class FigBarcode extends Fig
    implements ImageObserver, NoObfuscation {

    private static final long serialVersionUID = 1L;

    public static final int NOT_MARKER = 0;
    public static final int MARKER_TOP_RIGHT = 1;
    public static final int MARKER_BOTTOM_LEFT = 2;

    protected transient BarcodeGenerator bargen;

    protected transient Java2DCanvasProvider canvas;

    protected String barcodeType = "Form ID";
    protected String barcodeValue = "12345-67890";

    protected String recognitionValue = "01";
    protected int recognitionMarkerType = NOT_MARKER;
    protected int recognitionMarkerHeight = 18;

    protected int barHeight = 0;
    protected boolean useDefaultBarHeight = true;

    @XStreamAlias("isRetainShape") protected boolean retainShape = false;

    @XStreamAlias("showBarcodeText") protected boolean showText = false;

    @XStreamAlias("showQuietZone") protected boolean quietZone = false;

    @XStreamAlias("isValidBarcode") protected boolean validBarcode = true;

    @XStreamAlias("naturalImageSize") protected Rectangle naturalImageSize;

    @XStreamAlias("scalar") private double scalar = 1.0;

    private transient PageAttributes pageAttributes;

    private transient boolean revalidate = false;

    private transient static Log LOG = LogFactory.getLog(FigBarcode.class);

    private transient boolean allowFormIDBarcode = true;

    public FigBarcode(int x, int y, String barcodeType, String barcodeValue, double scalar,
        boolean quietZone, boolean showText) throws Exception {
        super(x, y, 0, 0);
        this.barcodeType = barcodeType;
        this.barcodeValue = barcodeValue;
        this.renderableBarcodeValue = barcodeValue + "";
        this.quietZone = quietZone;
        this.showText = showText;
        this.scalar = scalar;
        this.revalidate = true;
        resetBarcode();
    }


    public boolean isAllowFormIDBarcode() {
        return allowFormIDBarcode;
    }

    public void setAllowFormIDBarcode(boolean allowFormIDBarcode) {
        this.allowFormIDBarcode = allowFormIDBarcode;
    }

    public void updateBarcode(final String newBarcodeType, final String newBarcodeValue,
        final boolean newShowText, final boolean newQuietZone, final boolean newUseDefaultBarHeight,
        final Integer newBarHeight) throws Exception {

        if (!allowFormIDBarcode && newBarcodeType.equalsIgnoreCase("Form ID")) {
            throw new Exception(Localizer.localize("Base", "CannotAddFormIDBarcodeToSegment"));
        }

        if (UndoManager.getInstance().isGenerateMementos()) {

            Memento memento = new Memento() {

                private String oldBarcodeType = barcodeType;
                private String oldBarcodeValue = barcodeValue;
                private int oldBarHeight = barHeight;
                private boolean oldUseDefaultBarHeight = useDefaultBarHeight;
                private boolean oldShowText = showText;
                private boolean oldQuietZone = quietZone;

                public void undo() {
                    setBarcodeType(oldBarcodeType);
                    setBarcodeValue(oldBarcodeValue);
                    setShowText(oldShowText);
                    setQuietZone(oldQuietZone);
                    setUseDefaultBarHeight(oldUseDefaultBarHeight);
                    if (!oldUseDefaultBarHeight) {
                        setBarHeight(oldBarHeight);
                    }
                    try {
                        resetBarcode();
                    } catch (Exception e1) {
                    }
                    damage();
                    firePropChange("undo", null, null);
                }

                public void redo() {
                    setBarcodeType(newBarcodeType);
                    setBarcodeValue(newBarcodeValue);
                    setShowText(newShowText);
                    setQuietZone(newQuietZone);
                    setUseDefaultBarHeight(newUseDefaultBarHeight);
                    if (!newUseDefaultBarHeight) {
                        setBarHeight(newBarHeight);
                    }
                    try {
                        resetBarcode();
                    } catch (Exception e1) {
                    }
                    damage();
                    firePropChange("redo", null, null);
                }

                public void dispose() {
                }

                public String toString() {
                    return (isStartChain() ? "*" : " ") + "BarcodeMemento";
                }
            };
            UndoManager.getInstance().startChain();
            UndoManager.getInstance().addMemento(memento);


        }

        setBarcodeType(newBarcodeType);
        setBarcodeValue(newBarcodeValue);
        setShowText(newShowText);
        setQuietZone(newQuietZone);
        setUseDefaultBarHeight(newUseDefaultBarHeight);
        if (!newUseDefaultBarHeight) {
            setBarHeight(newBarHeight);
        }

        try {
            resetBarcode();
            damage();
        } catch (Exception e1) {
            damage();
            String message = String
                .format(Localizer.localize("GefBase", "InvalidBarcodeValueMessage"),
                    e1.getMessage());
            String caption = Localizer.localize("GefBase", "InvalidBarcodeValueTitle");
            javax.swing.JOptionPane.showConfirmDialog(getGraph().getRootPane(), message, caption,
                javax.swing.JOptionPane.DEFAULT_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE);
        }
    }

    public void clearRender() {
        this.bargen = null;
        this.canvas = null;
    }

    public boolean isRevalidate() {
        return revalidate;
    }

    public void setRevalidate(boolean revalidate) {
        this.revalidate = revalidate;
    }

    public void resetBarcode() throws Exception {
        try {
            this.bargen = BarcodeCreator
                .getBarcodeGenerator(getBarcodeType(), getRenderableBarcodeValue(), scalar,
                    isQuietZone(), isShowText(), useDefaultBarHeight ? 0 : barHeight);
            validBarcode = true;
            updateBarcodeDimension();
        } catch (Exception e) {
            validBarcode = false;
            try {
                renderInvalidBarcode();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
            }
            damage();
            throw (e);
        }
    }


    private void renderInvalidBarcode() throws IOException {
	/*
	InputStream istream = getClass().getResourceAsStream("/com/ebstrada/formreturn/manager/ui/images/nobarcode.svg");
	byte[] theBytes = new byte[istream.available()];
	istream.read(theBytes);
	_bais = new ByteArrayInputStream(theBytes);
	*/

        // I think we can render this as an image maybe?

    }

    // //////////////////////////////////////////////////////////////
    // Editor API

    @Override public void createDrag(int anchorX, int anchorY, int x, int y, int snapX, int snapY,
        boolean released) {

        if (released) {
            MutableGraphSupport.enableSaveAction();
            if (UndoManager.getInstance().isGenerateMementos()) {
                UndoManager.getInstance().startChain();
                UndoManager.getInstance().addMemento(new FigAddMemento(this));
            }

        }

        setLocation(snapX, snapY);
    }

    // //////////////////////////////////////////////////////////////
    // ImageObserver API

    public boolean imageUpdate(Image img, int infoflags, int x, int y, int w, int h) {
        boolean done = ((infoflags & (ERROR | FRAMEBITS | ALLBITS)) != 0);
        return !done;
    }

    public PageAttributes getPageAttributes() {
        if (this.pageAttributes != null) {
            return this.pageAttributes;
        } else {
            return getGraph().getPageAttributes();
        }
    }

    public void setPageAttributes(PageAttributes pageAttributes) {
        this.pageAttributes = pageAttributes;
    }

    public void postLoad() {
        updateSegmentBarcodes();
    }

    public void updateSegmentBarcodes() {

        PageAttributes currentPageAttributes = getPageAttributes();

        int croppedWidth = currentPageAttributes.getCroppedWidth();
        int croppedHeight = currentPageAttributes.getCroppedHeight();

        if (recognitionMarkerType == MARKER_TOP_RIGHT) {
            setX(croppedWidth - getWidth());
            setY(0);
        } else if (recognitionMarkerType == MARKER_BOTTOM_LEFT) {
            setX(0);
            setY(croppedHeight - getHeight());
        }

    }

    // //////////////////////////////////////////////////////////////
    // painting methods

    private transient Font _font;

    private transient String renderableBarcodeValue;

    private transient BarcodeDimension bardim;

    @Override public void paint(Object g) {
        paint(g, false);
    }

    public BarcodeDimension getBarcodeDimension() {
        return this.bardim;
    }

    public void updateBarcodeDimension() {
        if ((getBarcodeGenerator() != null) && (getRenderableBarcodeValue() != null)) {
            try {
                this.bardim = getBarcodeGenerator().calcDimensions(getRenderableBarcodeValue());
                if (this.revalidate) {
                    resizeFigToBarcodeSize();
                    this.revalidate = false;
                }
            } catch (IllegalArgumentException iae) {
                this.bardim = null;
            }
        } else {
            this.bardim = null;
        }
    }

    public void resizeFigToBarcodeSize() {
        if (bardim == null) {
            return;
        }
        setSize((int) Math.ceil(bardim.getWidth()), (int) Math.ceil(bardim.getHeight()));
    }

    public void transform(Graphics2D g2d) {

        if (getBarcodeDimension() != null) {
            double horzScale = (double) getWidth() / getBarcodeDimension().getWidthPlusQuiet();
            double vertScale = (double) getHeight() / getBarcodeDimension().getHeightPlusQuiet();
            double scale;
            double dx = 0;
            double dy = 0;
            if (horzScale < vertScale) {
                scale = horzScale;
                dy = (((double) getHeight() / scale) - getBarcodeDimension().getHeightPlusQuiet())
                    / 2.0d;
            } else {
                scale = vertScale;
                dx = (((double) getWidth() / scale) - getBarcodeDimension().getWidthPlusQuiet())
                    / 2.0d;
            }
            g2d.scale(scale, scale);
            g2d.translate(dx + ((double) getX() / scale), dy + ((double) getY() / scale));
        }

    }

    @Override public void paint(Object graphicContext, boolean includeMargins) {

        if (includeMargins == false) {
            setMarginOffset(0, 0);
        }

        if (_font != null) {
            ((Graphics) graphicContext).setFont(_font);
        }

        Graphics2D g2d = (Graphics2D) graphicContext;

        // this code below is to pin the barcode to the top right (for the marker type barcodes)
        PageAttributes currentPageAttributes = getPageAttributes();

        int croppedWidth = currentPageAttributes.getCroppedWidth();
        int croppedHeight = currentPageAttributes.getCroppedHeight();

        if (canvas == null) {
            canvas = new Java2DCanvasProvider(g2d, 0);
            try {
                resetBarcode();
            } catch (Exception ex) {
                Misc.printStackTrace(ex);
                return;
            }
        } else {
            canvas.setGraphics2D(g2d);
        }

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
            RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        // if (_bais != null) {
        if (this.canvas != null) {
            try {
		
		/*
		GVTBuilder builder = new GVTBuilder();
		
		UserAgent userAgent = new UserAgentAdapter();
		DocumentLoader loader = new DocumentLoader(userAgent);
		BridgeContext ctx = new BridgeContext(userAgent, loader);

		SVGDocument svgDoc = null;
		
		try {
		    String parser = XMLResourceDescriptor.getXMLParserClassName();
		    SAXSVGDocumentFactory svgDocFactory = new SAXSVGDocumentFactory(parser);
		    String uri = "http://bidon";
		    _bais.reset();
		    svgDoc = svgDocFactory.createSVGDocument(uri,_bais);
		} catch (IOException ex) { 
		    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
		}
		
		GraphicsNode graphicsNode = builder.build(ctx, svgDoc);
		*/
		
		/*
		float width = getWidth();
		float height = getHeight();

		AffineTransform transform = ViewBox.getPreserveAspectRatioTransform(
			svgDoc.getRootElement(), (float) width, (float) height, ctx);

		Rectangle2D SVGbounds = this.getBounds();
		naturalImageSize = SVGbounds.getBounds();
		
		int proportionateWidth = (int)getProportionateWidth(getHeight());

		if ( ( proportionateWidth + _x ) <= croppedWidth ) {
		    setWidth(proportionateWidth);
		    width = proportionateWidth;
		} else {
		    setX(croppedWidth - getWidth());
		}
		
		if ( (getHeight() + _y) > croppedHeight ) {
		   setY(croppedHeight - getHeight());
		}
		
		double dx = width / (SVGbounds.getWidth() + SVGbounds.getX());
		double dy = height / (SVGbounds.getHeight() + SVGbounds.getY());

		transform.scale(dx,dy);
		
		*/

                AffineTransform lastTransform = g2d.getTransform();
                try {
                    transform(g2d);
                    g2d.setColor(Color.black);
                    getBarcodeGenerator().generateBarcode(canvas, getRenderableBarcodeValue());
                } catch (Exception ex) {
                    drawInvalidBarcode(g2d, ex);
                } finally {
                    g2d.setTransform(lastTransform);
                }

            } catch (Exception e) {
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
            }

        }

    }

    public void drawInvalidBarcode(Graphics2D g2d, Exception ex) {
        g2d.setColor(Color.red);
        g2d.fillRect(getX(), getY(), getWidth(), getHeight());
        g2d.setColor(Color.black);
    }

    public BarcodeGenerator getBarcodeGenerator() {
        return this.bargen;
    }

    public boolean isRetainShape() {
        return retainShape;
    }

    public double getProportionateWidth(double proportionateHeight) {
        double proportionateScale = 1;
        proportionateScale = proportionateHeight / naturalImageSize.getHeight();
        return (naturalImageSize.getWidth() * proportionateScale);
    }

    public double getProportionateHeight(double proportionateWidth) {
        double proportionateScale = 1;
        proportionateScale = proportionateWidth / naturalImageSize.getWidth();
        return (naturalImageSize.getHeight() * proportionateScale);
    }

    public void deleteFromModel() {
        if (recognitionMarkerType == NOT_MARKER) {
            removeStarted = true;
            removeFromDiagram();
        }
    }

    public boolean isSelectable() {
        if (recognitionMarkerType == NOT_MARKER) {
            return true;
        }
        return false;
    }

    public void setRetainShape(boolean retainShape) {
        this.retainShape = retainShape;
        damage();
    }

    public Rectangle2D getNaturalImageSize() {
        return naturalImageSize;
    }

    public String getBarcodeType() {
        return barcodeType;
    }

    public void setBarcodeType(String barcodeType) {
        this.barcodeType = barcodeType;
    }

    public String getBarcodeValue() {
        return barcodeValue;
    }

    public void setBarcodeValue(String barcodeValue) {
        this.barcodeValue = barcodeValue;
        setRenderableBarcodeValue(barcodeValue + "");
    }

    public String getRecognitionValue() {
        return recognitionValue;
    }

    public void setRecognitionValue(String recognitionValue) {
        this.recognitionValue = recognitionValue;
    }

    public int getRecognitionMarkerType() {
        return recognitionMarkerType;
    }

    public void setRecognitionMarkerType(int recognitionMarkerType) {
        this.recognitionMarkerType = recognitionMarkerType;
    }

    public int getRecognitionMarkerHeight() {
        return recognitionMarkerHeight;
    }

    public void setRecognitionMarkerHeight(int recognitionMarkerHeight) {
        this.recognitionMarkerHeight = recognitionMarkerHeight;
    }

    public boolean isShowText() {
        return showText;
    }

    public void setShowText(boolean showText) {
        this.showText = showText;
    }

    public boolean isQuietZone() {
        return quietZone;
    }

    public void setQuietZone(boolean quietZone) {
        this.quietZone = quietZone;
    }

    public int getBarHeight() {
        return barHeight;
    }

    public void setBarHeight(int barHeight) {
        this.barHeight = barHeight;
    }

    public boolean isUseDefaultBarHeight() {
        return useDefaultBarHeight;
    }

    public void setUseDefaultBarHeight(boolean useDefaultBarHeight) {
        this.useDefaultBarHeight = useDefaultBarHeight;
    }

    @Override public EditorPanel getEditorPanel() {
        return new FigBarcodePanel();
    }

    public void setRenderableBarcodeValue(String renderableBarcodeValue) {
        this.renderableBarcodeValue = renderableBarcodeValue;
    }

    public String getRenderableBarcodeValue() {
        if (renderableBarcodeValue == null) {
            renderableBarcodeValue = getBarcodeValue();
        }
        return renderableBarcodeValue;
    }

}
