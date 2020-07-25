package com.ebstrada.formreturn.manager.ui.tab;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.JComponent;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGLocatable;
import org.w3c.dom.svg.SVGRect;

import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;
import com.ebstrada.formreturn.manager.util.preferences.persistence.ApplicationStatePreferences;

public class DesktopTabbedPaneBackground {

    private SVGDocument backgroundDoc;
    private SVGDocument logoDoc;
    private SVGDocument showGuideDoc;

    private BridgeContext ctx;

    private HashMap<String, ScalingRectangle> rectangles;
    private HashMap<String, String> helpGUIDs;

    private int margin = 20;

    private double xScale;

    private double yScale;

    private double xOffset;
    private GraphicsNode logoGN;
    private GraphicsNode backgroundGN;
    private GraphicsNode showGuideGN;
    private double bgWidth;
    private double bgHeight;

    private boolean showGuide = true;
    private int logoWidth;
    private int logoHeight;
    private int showGuideWidth;
    private int showGuideHeight;
    private Rectangle showGuideRect;
    private JComponent parent;
    private GVTBuilder builder;

    private boolean debug = false;


    @SuppressWarnings("serial") private class ScalingRectangle extends Rectangle2D.Double {

        public ScalingRectangle(float x, float y, float w, float h) {
            super((double) x, (double) y, (double) w, (double) h);
        }

        public double getX() {
            return (super.getX() * getXScale()) + getXOffset();
        }

        public double getY() {
            return (super.getY() * getYScale());
        }

        public double getWidth() {
            return (super.getWidth() * getXScale());
        }

        public double getHeight() {
            return (super.getHeight() * getYScale());
        }

        public double getCenterX() {
            return (super.getCenterX() * getXScale()) + getXOffset();
        }

        public double getCenterY() {
            return (super.getCenterY() * getYScale());
        }

    }

    public double getYScale() {
        return this.yScale;
    }

    public double getXScale() {
        return this.xScale;
    }

    public double getXOffset() {
        return this.xOffset;
    }

    public DesktopTabbedPaneBackground(JComponent component, String country, String language) {

        // TODO: localize the backgrounds

        this.parent = component;
        ApplicationStatePreferences appState = PreferencesManager.getApplicationState();
        this.showGuide = appState.getShowGuide();
        String xmlParser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory df = new SAXSVGDocumentFactory(xmlParser);
        try {
            if (language.toLowerCase().equals("en")) {
                this.backgroundDoc = df.createSVGDocument(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/ui/images/background.svg")
                    .toURI().toString());
                this.showGuideDoc = df.createSVGDocument(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/ui/images/show_guide.svg")
                    .toURI().toString());
            } else if (language.toLowerCase().equals("es")) {
                this.backgroundDoc = df.createSVGDocument(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/ui/images/background_es.svg")
                    .toURI().toString());
                this.showGuideDoc = df.createSVGDocument(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/ui/images/show_guide_es.svg")
                    .toURI().toString());
            }
            this.logoDoc = df.createSVGDocument(getClass()
                .getResource("/com/ebstrada/formreturn/manager/ui/images/formreturn_gray.svg")
                .toURI().toString());
            UserAgent userAgent = new UserAgentAdapter();
            DocumentLoader loader = new DocumentLoader(userAgent);
            this.ctx = new BridgeContext(userAgent, loader);
            this.ctx.setDynamicState(BridgeContext.DYNAMIC);
            builder = new GVTBuilder();
            loadLogo();
            loadGuide();
            loadShowGuide();
        } catch (IOException e) {
            Misc.printStackTrace(e);
        } catch (URISyntaxException e) {
            Misc.printStackTrace(e);
        }
    }

    private void loadLogo() {
        if (logoGN != null) {
            return;
        }
        logoGN = builder.build(ctx, logoDoc);
        logoGN.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        logoGN.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
            RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        logoWidth = (int) ctx.getDocumentSize().getWidth();
        logoHeight = (int) ctx.getDocumentSize().getHeight();
    }

    private void loadGuide() {
        if (backgroundDoc == null || backgroundGN != null) {
            return;
        }
        backgroundGN = builder.build(ctx, backgroundDoc);
        backgroundGN
            .setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        backgroundGN.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
            RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        bgWidth = ctx.getDocumentSize().getWidth();
        bgHeight = ctx.getDocumentSize().getHeight();
    }

    private void loadShowGuide() {
        if (showGuideDoc == null || showGuideGN != null) {
            return;
        }
        showGuideGN = builder.build(ctx, showGuideDoc);
        showGuideGN
            .setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        showGuideGN.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
            RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        showGuideWidth = (int) ctx.getDocumentSize().getWidth();
        showGuideHeight = (int) ctx.getDocumentSize().getHeight();
    }

    public void paintComponent(Graphics g, Rectangle r) {

        int logoXPos = (int) r.getWidth() - logoWidth - margin; // (old align right)
        int logoYPos = (int) r.getHeight() - logoHeight - margin;

        g.setColor(Color.white);
        g.fillRect(0, 0, (int) r.getWidth(), (int) r.getHeight());
        try {

            // paint logo
            g.translate(logoXPos, logoYPos);
            logoGN.paint((Graphics2D) g);
            g.translate(-logoXPos, -logoYPos);

            if (backgroundDoc == null || showGuideDoc == null) {
                g.dispose();
                return;
            }

            // set scale
            xScale = r.getWidth() / bgWidth;
            yScale = r.getHeight() / bgHeight;
            double scale = Math.min(xScale, yScale);
            xScale = scale;
            yScale = scale;

            if (showGuide) {
                // scale and paint background
                AffineTransform affineTransform = AffineTransform.getScaleInstance(xScale, yScale);
                backgroundGN.setTransform(affineTransform);
                xOffset = (r.getWidth() - (xScale * bgWidth)) / 2d;
                g.translate((int) xOffset, 0);
                backgroundGN.paint((Graphics2D) g);
                g.translate(-((int) xOffset), 0);
            } else {

                int showGuideXPos = margin; // (old align right)
                int showGuideYPos = (int) r.getHeight() - showGuideHeight - margin;
                if (this.showGuideRect == null) {
                    this.showGuideRect = new Rectangle(showGuideXPos, showGuideYPos, showGuideWidth,
                        showGuideHeight);
                } else {
                    this.showGuideRect
                        .setBounds(showGuideXPos, showGuideYPos, showGuideWidth, showGuideHeight);
                }

                // paint show guide
                g.translate(showGuideXPos, showGuideYPos);
                showGuideGN.paint((Graphics2D) g);
                g.translate(-showGuideXPos, -showGuideYPos);

            }

            addLinks(g);

            if (debug) {
                drawRectangleOverlay(g);
            }

            g.dispose();

        } catch (Exception e) {
            Misc.printStackTrace(e);
        }
    }

    private void drawRectangleOverlay(Graphics g) {
        g.setColor(Color.red);

        // g.fillRect(0 ,0, 100, 200);

        for (ScalingRectangle rect : this.rectangles.values()) {
            int x = (int) rect.getX();
            int y = (int) rect.getY();
            int w = (int) rect.getWidth();
            int h = (int) rect.getHeight();
            g.fillRect(x, y, w, h);
        }
    }

    public void addLinks(Graphics g) {
        if (this.rectangles != null) {
            return;
        }
        this.rectangles = new HashMap<String, ScalingRectangle>();
        this.helpGUIDs = new HashMap<String, String>();
        addLink("step1", g);
        addLink("step2", g);
        addLink("step3", g);
        addLink("step4", g);
        addLink("step5", g);
        addLink("step6", g);
        addLink("step7", g);
        addLink("step8", g);
        addLink("step9", g);
        addLink("step10", g);
        addLink("step11", g);
        addLink("closeHelp", g);
    }

    public void addLink(String id, Graphics g) {
        Element element = this.backgroundDoc.getElementById(id);
        if (element instanceof SVGLocatable) {
            SVGLocatable locatableElement = ((SVGLocatable) element);
            SVGRect box = locatableElement.getBBox();
            ScalingRectangle rect =
                new ScalingRectangle(box.getX(), box.getY(), box.getWidth(), box.getHeight());
            rectangles.put(id, rect);
            helpGUIDs.put(id, XLinkSupport.getXLinkHref(element));
        }
    }

    public void openLink(String helpGUID) {
        String url = "file://" + Misc.getHelpDirectory() + "/?topic=" + helpGUID;
        Misc.openURL(url);
    }

    public boolean isHoveringHyperlink(Point point) {
        if (this.rectangles == null) {
            return false;
        }
        if (showGuide) {
            for (ScalingRectangle rect : this.rectangles.values()) {
                if (rect.contains(point)) {
                    return true;
                }
            }
        } else {
            if (this.showGuideRect.contains(point)) {
                return true;
            }
        }
        return false;
    }

    public void mouseClicked(MouseEvent e) {
        if (e.isConsumed()) {
            return;
        }
        e.consume();
        if (this.rectangles == null) {
            return;
        }
        Point point = e.getPoint();
        if (showGuide) {
            for (String id : this.rectangles.keySet()) {
                if (this.rectangles.get(id).contains(point)) {
                    if (id.equals("closeHelp")) {
                        closeHelp();
                        return;
                    } else {
                        openLink(helpGUIDs.get(id));
                    }
                }
            }
        } else {
            if (this.showGuideRect.contains(point)) {
                toggleShowGuide();
                loadGuide();
                parent.repaint();
            }
        }
    }

    private void toggleShowGuide() {
        if (showGuide) {
            showGuide = false;
        } else {
            showGuide = true;
        }
        ApplicationStatePreferences appState = PreferencesManager.getApplicationState();
        appState.setShowGuide(showGuide);
        try {
            PreferencesManager.savePreferences(Main.getXstream());
        } catch (IOException e) {
            Misc.printStackTrace(e);
        }
    }

    private void closeHelp() {
        toggleShowGuide();
        loadShowGuide();
        parent.repaint();
    }

    public void mouseMoved(MouseEvent e, DesktopTabbedPane desktopTabbedPane) {
        if (e.isConsumed()) {
            return;
        }
        e.consume();
        Point point = e.getPoint();
        if (isHoveringHyperlink(point)) {
            setPointerCursor(desktopTabbedPane);
        } else {
            setArrowCursor(desktopTabbedPane);
        }
    }

    private void setArrowCursor(DesktopTabbedPane desktopTabbedPane) {
        if (desktopTabbedPane.getCursor().getType() != Cursor.DEFAULT_CURSOR) {
            desktopTabbedPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    private void setPointerCursor(DesktopTabbedPane desktopTabbedPane) {
        if (desktopTabbedPane.getCursor().getType() != Cursor.HAND_CURSOR) {
            desktopTabbedPane.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }

}
