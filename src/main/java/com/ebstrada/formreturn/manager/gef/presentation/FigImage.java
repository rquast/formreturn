package com.ebstrada.formreturn.manager.gef.presentation;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.io.File;

import javax.swing.ImageIcon;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.bridge.ViewBox;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fop.svg.PDFGraphics2D;
import org.w3c.dom.svg.SVGDocument;

import com.ebstrada.formreturn.manager.gef.ui.PageAttributes;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.editor.dialog.FigImageProperties;
import com.ebstrada.formreturn.manager.ui.editor.panel.FigImagePanel;
import com.ebstrada.formreturn.manager.ui.editor.panel.EditorPanel;
import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("image") public class FigImage extends Fig
    implements ImageObserver, NoObfuscation, MouseListener {

    private static final long serialVersionUID = 1L;

    /**
     * The Image being rendered
     */
    protected transient Image _image;

    /**
     * The image resource filename
     */
    @XStreamAlias("imagefilename") protected String imageFileName;

    /**
     * Flag used to determine if the image should retain shape or not
     */
    @XStreamAlias("isRetainShape") protected boolean retainShape = false;

    @XStreamAlias("natrualImageSize") protected Rectangle naturalImageSize;

    private transient String workingDirName;

    private transient PageAttributes pageAttributes;

    private static Log LOG = LogFactory.getLog(FigImage.class);

    /**
     * Construct a new FigImage with the given position, size, and Image.
     */
    public FigImage(int x, int y, int w, int h, Image img) {
        super(x, y, w, h);
        _image = img;

    }

    /**
     * Construct a new FigImage w/ the given position and image.
     */
    public FigImage(int x, int y, Image i) {
        this(x, y, 0, 0, i);
        setSize(145, 95);
    }

    @Override public void createDrag(int anchorX, int anchorY, int x, int y, int snapX, int snapY,
        boolean released) {
        setLocation(snapX, snapY);
    }


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

    @Override public void paint(Object g) {
        paint(g, false);
    }

    @Override public void paint(Object g, boolean includeMargins) {
        paint(g, includeMargins, true);
    }

    public void paint(Object graphicContext, boolean includeMargins, boolean isEditor) {

        if (includeMargins == false) {
            setMarginOffset(0, 0);
        }

        if (_image == null) {
            if (getImageFileName() != null) {
                try {
                    if (getImageFileName().endsWith("svg") || getImageFileName().endsWith("SVG")) {

                        UserAgent userAgent = new UserAgentAdapter();
                        DocumentLoader loader = new DocumentLoader(userAgent);

                        BridgeContext ctx = new BridgeContext(userAgent, loader);
                        ctx.setDynamic(true);
                        GVTBuilder builder = new GVTBuilder();

                        SVGDocument svgDoc = (SVGDocument) loader.loadDocument(getImageFileURI());
                        GraphicsNode graphicsNode = builder.build(ctx, svgDoc);

                        float width = getWidth();
                        float height = getHeight();

                        if (isRetainShape()) {
                            int proportionateWidth = (int) getProportionateWidth(getHeight());
                            PageAttributes currentPageAttributes = getPageAttributes();
                            int croppedWidth = currentPageAttributes.getCroppedWidth();
                            if ((proportionateWidth + getX()) <= croppedWidth) {
                                setWidth(proportionateWidth);
                                width = proportionateWidth;
                            } else {
                                width = croppedWidth - getX();
                                setWidth((int) width);
                                height = (int) getProportionateHeight(width);
                                setHeight((int) height);
                            }
                        }

                        AffineTransform transform = ViewBox
                            .getPreserveAspectRatioTransform(svgDoc.getRootElement(), (float) width,
                                (float) height, ctx);

                        Rectangle2D SVGbounds = graphicsNode.getBounds();
                        naturalImageSize = SVGbounds.getBounds();
                        double dx = width / (SVGbounds.getWidth() + SVGbounds.getX());
                        double dy = height / (SVGbounds.getHeight() + SVGbounds.getY());

                        transform.scale(dx, dy);
                        Graphics2D graphics = (Graphics2D) ((Graphics2D) graphicContext).create();
                        graphics.translate(getX(), getY());
                        graphics.transform(transform);
                        graphicsNode.paint(graphics);

                    } else {
                        ImageIcon imageIcon = new ImageIcon(
                            getWorkingDirName() + System.getProperty("file.separator") + "images"
                                + System.getProperty("file.separator") + getImageFileName());
                        _image = imageIcon.getImage();
                        naturalImageSize = new Rectangle(0, 0, imageIcon.getIconWidth(),
                            imageIcon.getIconHeight());
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
                }
            }
        }

        Graphics g = (Graphics) graphicContext;
        if (_image != null) {
            if (isEditor && isRetainShape()) {
                int proportionateWidth = (int) getProportionateWidth(getHeight());
                PageAttributes currentPageAttributes = getPageAttributes();
                int croppedWidth = currentPageAttributes.getCroppedWidth();
                if ((proportionateWidth + getX()) <= croppedWidth) {
                    setWidth(proportionateWidth);
                    drawImage(g, _image, getX(), getY(), proportionateWidth, getHeight(), this);
                } else {
                    int maxWidth = croppedWidth - getX();
                    setWidth(maxWidth);
                    int maxHeight = (int) getProportionateHeight(maxWidth);
                    setHeight(maxHeight);
                    drawImage(g, _image, getX(), getY(), maxWidth, maxHeight, this);
                }
            } else {
                drawImage(g, _image, getX(), getY(), getWidth(), getHeight(), this);
            }
        }
    }

    public void drawImage(Graphics g, Image _image, int x, int y, int width, int height,
        ImageObserver observer) {
        if (g instanceof PDFGraphics2D) {
            AffineTransform at = new AffineTransform();
            at.translate(x, y);
            if (naturalImageSize != null) {
                double widthScale = (double) width / naturalImageSize.getWidth();
                double heightScale = (double) height / naturalImageSize.getHeight();
                at.scale(widthScale, heightScale);
            }
            ((PDFGraphics2D) g).drawImage(_image, at, observer);
        } else {
            g.drawImage(_image, x, y, width, height, observer);
        }
    }

    public Image getImage() {
        return _image;
    }

    public void setImage(Image _image) {
        this._image = _image;
    }

    public boolean isRetainShape() {
        return retainShape;
    }

    public double getProportionateWidth(double proportionateHeight) {
        double proportionateScale = 1;
        if (naturalImageSize == null) {
            proportionateScale = proportionateHeight / 16;
            return 16 * proportionateScale;
        } else {
            proportionateScale = proportionateHeight / naturalImageSize.getHeight();
        }
        return (naturalImageSize.getWidth() * proportionateScale);
    }

    public double getProportionateHeight(double proportionateWidth) {
        double proportionateScale = 1;
        if (naturalImageSize == null) {
            proportionateScale = proportionateWidth / 16;
            return 16 * proportionateScale;
        } else {
            proportionateScale = proportionateWidth / naturalImageSize.getWidth();
        }
        return (naturalImageSize.getHeight() * proportionateScale);
    }


    public void setRetainShape(boolean retainShape) {
        this.retainShape = retainShape;
        damage();
    }

    public Rectangle2D getNaturalImageSize() {
        return naturalImageSize;
    }

    @Override public EditorPanel getEditorPanel() {
        return new FigImagePanel();
    }

    private String getWorkingDirName() {
        if (this.workingDirName != null) {
            return this.workingDirName;
        } else {
            return getGraph().getDocumentPackage().getWorkingDirName();
        }
    }

    public void setWorkingDirName(String workingDirName) {
        this.workingDirName = workingDirName;
    }

    public String getImageFileURI() {
        String urlString = "";
        if (Main.WINDOWS) {
            urlString += "file:/";
        } else {
            urlString += "file://";
        }
        urlString += getWorkingDirName();
        urlString += "/images/" + getImageFileName();
        if (File.separatorChar == '\\') {
            urlString = urlString.replaceAll("\\\\", "/");
        }
        return urlString.replaceAll(" ", "%20");
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() > 1) {
            FigImageProperties fis = new FigImageProperties(Main.getInstance(), this);
            fis.setTitle(Localizer.localize("UI", "ImagePropertiesDialogTitle"));
            fis.setModal(true);
            fis.setVisible(true);
            fis.dispose();
            e.consume();
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

}
