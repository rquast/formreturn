package com.ebstrada.formreturn.manager.gef.presentation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.io.File;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.font.Font2D;

import com.ebstrada.formreturn.manager.gef.font.CachedFont;
import com.ebstrada.formreturn.manager.gef.font.CachedFontManager;
import com.ebstrada.formreturn.manager.gef.persistence.ExportAttributes;
import com.ebstrada.formreturn.manager.gef.properties.PropCategoryManager;
import com.ebstrada.formreturn.manager.gef.undo.Memento;
import com.ebstrada.formreturn.manager.gef.undo.UndoManager;
import com.ebstrada.formreturn.manager.logic.publish.PDFDocumentExporter;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.editor.dialog.TextPropertyDialog;
import com.ebstrada.formreturn.manager.ui.editor.panel.EditorMultiPanel;
import com.ebstrada.formreturn.manager.ui.editor.panel.FigTextMultiPanel;
import com.ebstrada.formreturn.manager.ui.editor.panel.FigTextPanel;
import com.ebstrada.formreturn.manager.ui.editor.panel.EditorPanel;
import com.ebstrada.formreturn.manager.ui.frame.EditorFrame;
import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("text") public class FigText extends Fig
    implements KeyListener, MouseListener, NoObfuscation {

    private static final long serialVersionUID = 1L;

    private static Log log = LogFactory.getLog(FigText.class);

    public String fontFamily;

    public int fontStyle;

    public float fontSize;

    public static final int IGNORE = 0;

    public static final int INSERT = 1;

    public static final int END_EDITING = 2;

    private int returnAction = FigText.IGNORE;

    private int tabAction = FigText.IGNORE;

    /**
     * Constants to specify text justification.
     */
    public static final int JUSTIFY_LEFT = 0;

    public static final int JUSTIFY_RIGHT = 1;

    public static final int JUSTIFY_CENTER = 2;

    public static final int JUSTIFY_JUSTIFIED = 3;

    /**
     * Minimum size of a FigTextLayout object.
     */
    public static final int MIN_TEXT_WIDTH = 30;

    public static final int MIN_TEXT_HEIGHT = 20;

    /**
     * Font info.
     */
    private transient Font _font;

    private transient FontMetrics _fm;

    /**
     * Color of the actual text characters.
     */
    @XStreamAlias("textForegroundColor") private Color _textColor = Color.black;

    /**
     * Color to be drawn behind the actual text characters. Note that this will
     * be a smaller area than the bounding box which is filled with FillColor.
     */
    @XStreamAlias("textBackgroundColor") private Color _textFillColor = Color.white;

    /**
     * True if the area behind individual characters is to be filled with
     * TextColor.
     */
    @XStreamAlias("isTextFilled") private boolean _textFilled = false;

    /**
     * True if the text should be editable. False for read-only.
     */
    @XStreamAlias("isEditable") private boolean editable = true;

    /**
     * True if the text should be underlined. needs-more-work.
     */
    @XStreamAlias("isUnderlined") private boolean _underline = false;

    /**
     * Extra spacing between lines. Default is 0 pixels.
     */
    @XStreamAlias("lineSpacing") private int _lineSpacing = 0;

    /**
     * Internal margins between the text and the edge of the rectangle.
     */
    @XStreamAlias("topMargin") private int _topMargin = 0;

    @XStreamAlias("bottomMargin") private int _botMargin = 0;

    @XStreamAlias("leftMargin") private int _leftMargin = 0;

    @XStreamAlias("rightMargin") private int _rightMargin = 0;

    /**
     * Text justification can be JUSTIFY_LEFT, JUSTIFY_RIGHT, or JUSTIFY_CENTER.
     */
    @XStreamAlias("justification") private int _justification = FigText.JUSTIFY_LEFT;

    /**
     * The current string to display. This is in an encoded format and so should
     * never be directly available to the client code. Client code can use the
     * accessor methods to view and amend this value with no knowledge of the
     * encoding.
     */
    @XStreamAlias("textValue") private String _curText;

    @XStreamAlias("fontIsEmbedded") private boolean embedded;

    private int fsType;

    private String fontFileName;

    private transient String renderableText;

    // //////////////////////////////////////////////////////////////
    // static initializer

    private static final Log LOG = LogFactory.getLog(FigText.class);

    /**
     * This puts the text properties on the "Text" and "Style" pages of the
     * org.tigris.gef.ui.TabPropFrame.
     */
    static {
        PropCategoryManager.categorizeProperty("Text", "font");
        PropCategoryManager.categorizeProperty("Text", "underline");
        PropCategoryManager.categorizeProperty("Text", "lineSpacing");
        PropCategoryManager.categorizeProperty("Text", "topMargin");
        PropCategoryManager.categorizeProperty("Text", "botMargin");
        PropCategoryManager.categorizeProperty("Text", "leftMargin");
        PropCategoryManager.categorizeProperty("Text", "rightMargin");
        PropCategoryManager.categorizeProperty("Text", "text");
        PropCategoryManager.categorizeProperty("Style", "justification");
        PropCategoryManager.categorizeProperty("Style", "textFilled");
        PropCategoryManager.categorizeProperty("Style", "textFillColor");
        PropCategoryManager.categorizeProperty("Style", "textColor");
    }

    // //////////////////////////////////////////////////////////////
    // constructors

    /**
     * Construct a new FigTextLayout with the given position, size, color,
     * string, font, and font size. Text string is initially empty and centered.
     */
    public FigText(int x, int y, int w, int h, Color textColor, String familyName, int fontSize) {
        super(x, y, w, h);
        _x = x;
        _y = y;
        _w = w;
        _h = h;
        _textColor = textColor;
        setFont(new Font(familyName, Font.PLAIN, fontSize));
        _justification = FigText.JUSTIFY_LEFT;
        _curText = "";
        setDefaultFont();
        setFilled(false);
    }

    public FigText(int x, int y, int w, int h, Color textColor, Font font) {
        this(x, y, w, h, textColor, font.getName(), font.getSize());
    }

    /**
     * Construct a new FigTextLayout with the given position and size
     */
    public FigText(int x, int y, int w, int h) {
        super(x, y, w, h);
        _x = x;
        _y = y;
        _w = w;
        _h = h;
        _justification = FigText.JUSTIFY_LEFT;
        _curText = "";
        setDefaultFont();
        setFilled(false);
    }

    @Override public void postLoad() {
        if (_font == null) {
            getFont();
        }
    }

    @Override public void postSave() {
        // System.out.println("Doing postSave()");
    }

    @Override public void preSave() {
        // System.out.println("Doing preSave()");
    }

    private void setDefaultFont() {
        CachedFontManager cachedFontManager = Main.getCachedFontManager();
        Font defaultFont = cachedFontManager.getDefaultFont();
        setFont(defaultFont.deriveFont(10.0f));
    }

    // //////////////////////////////////////////////////////////////
    // accessors

    /**
     * Reply a string that indicates how the text is justified: Left, Center, or
     * Right.
     */
    public String getJustificationByName() {
        if (_justification == FigText.JUSTIFY_LEFT) {
            return "Left";
        } else if (_justification == FigText.JUSTIFY_CENTER) {
            return "Center";
        } else if (_justification == FigText.JUSTIFY_RIGHT) {
            return "Right";
        } else if (_justification == FigText.JUSTIFY_JUSTIFIED) {
            return "Justified";
        }
        FigText.LOG.error("internal error, unknown text alignment");
        return "Unknown";
    }

    /**
     * Set the text justification given one of these strings: Left, Center, or
     * Right.
     */
    public void setJustificationByName(String justifyString) {
        if (justifyString.equals("Left")) {
            _justification = FigText.JUSTIFY_LEFT;
        } else if (justifyString.equals("Center")) {
            _justification = FigText.JUSTIFY_CENTER;
        } else if (justifyString.equals("Right")) {
            _justification = FigText.JUSTIFY_RIGHT;
        } else if (justifyString.equals("Justified")) {
            _justification = FigText.JUSTIFY_JUSTIFIED;
        }
        _fm = null;
    }

    // //////////////////////////////////////////////////////////////
    // accessors and modifiers

    /**
     * Get the font metrics.
     */
    protected FontMetrics getFontMetrics() {
        return _fm;
    }

    public Color getTextColor() {
        return _textColor;
    }

    public void setTextColor(Color c) {
        firePropChange("textColor", _textColor, c);
        _textColor = c;
    }

    public Color getTextFillColor() {
        return _textFillColor;
    }

    public void setTextFillColor(Color c) {
        firePropChange("textFillColor", _textFillColor, c);
        _textFillColor = c;
    }

    public boolean getTextFilled() {
        return _textFilled;
    }

    public void setTextFilled(boolean b) {
        firePropChange("textFilled", _textFilled, b);
        _textFilled = b;
    }

    public boolean getEditable() {
        return editable;
    }

    public void setEditable(boolean e) {
        firePropChange("editable", editable, e);
        editable = e;
    }

    public boolean getUnderline() {
        return _underline;
    }

    public void setUnderline(boolean b) {
        firePropChange("underline", _underline, b);
        _underline = b;
    }

    public int getJustification() {
        return _justification;
    }

    public void setJustification(int align) {
        firePropChange("justification", getJustification(), align);
        _justification = align;
    }

    public int getLineSpacing() {
        return _lineSpacing;
    }

    public void setLineSpacing(int s) {
        firePropChange("lineSpacing", _lineSpacing, s);
        _lineSpacing = s;
    }

    public int getTopMargin() {
        return _topMargin;
    }

    public void setTopMargin(int m) {
        firePropChange("topMargin", _topMargin, m);
        _topMargin = m;
    }

    public int getBotMargin() {
        return _botMargin;
    }

    public void setBotMargin(int m) {
        firePropChange("botMargin", _botMargin, m);
        _botMargin = m;
    }

    public int getLeftMargin() {
        return _leftMargin;
    }

    public void setLeftMargin(int m) {
        firePropChange("leftMargin", _leftMargin, m);
        _leftMargin = m;
    }

    public int getRightMargin() {
        return _rightMargin;
    }

    public void setRightMargin(int m) {
        firePropChange("rightMargin", _rightMargin, m);
        _rightMargin = m;
    }

    private String getWorkingDirName() {
        return getGraph().getDocumentPackage().getWorkingDirName();
    }

    public Font getFont() {
        if (_font == null) {

            CachedFont cf = Main.getInstance().getCachedFontManager()
                .getCachedFont(getFontStyle(), getFontFamily());

            // if can't load the selected font, try load the embedded font
            if (cf == null && isEmbedded()) {
                Main.getInstance().getCachedFontManager()
                    .registerEmbeddedFont(getWorkingDirName(), getFontFileName());
                cf = Main.getInstance().getCachedFontManager()
                    .getCachedFont(getFontStyle(), getFontFamily());
            }

            // if can't load the embedded font, load the default font
            if (cf == null) {
                Font defaultFont = Main.getInstance().getCachedFontManager().getDefaultFont();
                setFont(defaultFont.deriveFont(getFontSize()));
            } else {
                setFont(cf.getFont().deriveFont(getFontSize()));
            }
        }
        return _font;
    }

    public void setFont(Font f) {
        firePropChange("font", _font, f);
        _font = f;
        _fm = null;
        fontSize = f.getSize();
        CachedFont cf = Main.getInstance().getCachedFontManager().getCachedFont(f.getFontName());
        fsType = cf.getFsType();
        fontFamily = cf.getFamily();
        fontStyle = cf.getStyle();
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String familyName) {
        this.fontFamily = familyName;
        CachedFont cf = Main.getInstance().getCachedFontManager()
            .getCachedFont(getFontStyle(), getFontFamily());
        Font f = cf.getFont().deriveFont(getFontSize());
        setFont(f);
    }

    public float getFontSize() {
        return this.fontSize;
    }

    public void setFontSize(float size) {
        this.fontSize = size;
        CachedFont cf = Main.getInstance().getCachedFontManager()
            .getCachedFont(getFontStyle(), getFontFamily());
        Font f = cf.getFont().deriveFont(getFontSize());
        setFont(f);
    }

    public File getFontFile() {
        CachedFont cf = Main.getInstance().getCachedFontManager()
            .getCachedFont(getFontStyle(), getFontFamily());
        return new File(cf.getFontDirectory() + "/" + cf.getFontFileName());
    }

    public boolean getItalic() {
        return _font.isItalic();
    }

    public void setItalic(boolean b) {
        int style = (getBold() ? Font.BOLD : 0) + (b ? Font.ITALIC : 0);
        Font f = new Font(_font.getFamily(), style, _font.getSize());
        setFont(f);
    }

    public boolean getBold() {
        return _font.isBold();
    }

    public void setBold(boolean b) {
        int style = (b ? Font.BOLD : 0) + (getItalic() ? Font.ITALIC : 0);
        setFont(new Font(_font.getFamily(), style, _font.getSize()));
    }

    /**
     * Specifies what action the control should take on return press.
     *
     * @param action values are IGNORE, INSERT or END_EDITING
     */
    public void setReturnAction(int action) {
        returnAction = action;
    }

    /**
     * Specifies what action the control should take on tab press.
     *
     * @param action values are IGNORE, INSERT or END_EDITING
     */
    public void setTabAction(int action) {
        tabAction = action;
    }

    /**
     * Discover what action the control will take on tab press.
     *
     * @return IGNORE, INSERT or END_EDITING
     */
    public int getTabAction() {
        return tabAction;
    }

    /**
     * Discover what action the control will take on return press.
     *
     * @return IGNORE, INSERT or END_EDITING
     */
    public int getReturnAction() {
        return returnAction;
    }

    /**
     * Remove the last char from the current string line and return the new
     * string. Called whenever the user hits the backspace key. Needs-More-Work:
     * Very slow. This will eventually be replaced by full text editing... if
     * there are any volunteers to do that...
     */
    public String deleteLastCharFromString(String s) {
        int len = Math.max(s.length() - 1, 0);
        char[] chars = s.toCharArray();
        return new String(chars, 0, len);
    }

    /**
     * Delete the last char from the current string. Called whenever the user
     * hits the backspace key
     */
    public void deleteLastChar() {
        _curText = deleteLastCharFromString(_curText);
    }

    /**
     * Append a character to the current String .
     */
    public void append(char c) {
        setText(_curText + c);
    }

    /**
     * Append the given String to the current String.
     */
    public void append(String s) {
        setText(_curText + s);
    }

    /**
     * Set the give string to be the current string of this fig. Update the
     * current font and font metrics first.
     *
     * @param str      String to be set at this object.
     * @param graphics Graphics context for the operation.
     */
    public void setText(String str, Graphics graphics) {
        if (graphics != null) {
            _fm = graphics.getFontMetrics(_font);
        }

        setText(str);
    }

    /**
     * Sets the given string to the current string of this fig.
     *
     * @param s
     */
    public void setText(String s) {
        FigTextEditor editor = FigTextEditor.getInstance();
        String newText = s;
        if (editor.isVisible() && editor.getFigText() == this && !_curText.equals(newText)) {
            editor.cancelEditing();
        }
        _curText = newText;
    }

    /**
     * Set the give string to be the current string of this fig. Update the
     * current font and font metrics first.
     *
     * @param str      String to be set at this object.
     * @param graphics Graphics context for the operation.
     */
    void setTextFriend(String str, Graphics graphics) {
        if (graphics != null) {
            _fm = graphics.getFontMetrics(_font);
        }

        setTextFriend(str);
    }

    /**
     * Sets the given string to the current string of this fig.
     *
     * @param s
     */
    void setTextFriend(String s) {
        if (UndoManager.getInstance().isGenerateMementos() && getOwner(this) == null) {
            Memento memento = new Memento() {
                String oldText = _curText;

                @Override public void undo() {
                    _curText = oldText;
                    redraw();
                    firePropChange("undo", null, null);
                }

                @Override public void redo() {
                    _curText = oldText;
                    redraw();
                    firePropChange("redo", null, null);
                }

                @Override public void dispose() {
                }
            };
            UndoManager.getInstance().addMemento(memento);
        }
        _curText = s;
    }

    /**
     * Determine the owner of the given Fig by recursing up through groups until
     * an owner is found
     */
    private Object getOwner(Fig fig) {
        Object owner = fig.getOwner();
        if (owner != null) {
            return owner;
        }
        Fig figGroup = fig.getGroup();
        if (figGroup == null) {
            return null;
        } else {
            return getOwner(figGroup);
        }
    }

    /**
     * Get the String held by this FigTextLayout. Multi-line text is represented
     * by newline characters embedded in the String. USED BY PGML.tee
     */
    public String getText() {
        return _curText;
    }

    /**
     * Get the String held by this FigTextLayout. Multi-line text is represented
     * by newline characters embedded in the String.
     */
    public String getTextFriend() {
        return _curText;
    }

    @Override public void paint(Object g) {
        paint(g, false);
    }

    @Override public void paint(Object graphicContext, boolean includeMargins) {

        if (includeMargins == false) {
            setMarginOffset(0, 0);
        }

        Graphics2D graphics2D = null;
        FontRenderContext frc;

        graphics2D = (Graphics2D) graphicContext;

        if (_font != null) {
            ((Graphics) graphicContext).setFont(_font);
        }

        frc = graphics2D.getFontRenderContext();
        _fm = ((Graphics) graphicContext).getFontMetrics(_font);

        float chunkX = (new Integer(getX() + _leftMargin)).floatValue();
        float chunkY = (new Integer(getY() + _topMargin)).floatValue();

        if (_filled) {
            Color OriginalColor = graphics2D.getColor();
            graphics2D.setColor(_fillColor);
            graphics2D.fillRect(getX(), getY(), _w, _h);
            graphics2D.setColor(OriginalColor);
        }

        Map<TextAttribute, ?> fontAttributes = _font.getAttributes();

        String[] lines = null;

        if (this.renderableText != null) {
            lines = getRenderableText().split("\n|\r\n|\r");
        } else {
            lines = _curText.split("\n|\r\n|\r");
        }

        Dimension size = getSize();
        float formatWidth = size.width;

        float drawPosY = chunkY;

        for (int lineIndex = 0; lineIndex < lines.length; lineIndex++) {

            // if an empty return, move down one font size.
            if (lines[lineIndex].length() < 1) {
                drawPosY += _font.getSize();
                continue;
            }

            AttributedString atext = new AttributedString(lines[lineIndex], fontAttributes);
            AttributedCharacterIterator paragraph = atext.getIterator();

            int paragraphStart = paragraph.getBeginIndex();
            int paragraphEnd = paragraph.getEndIndex();

            LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(paragraph, frc);

            lineMeasurer.setPosition(paragraphStart);

            while (lineMeasurer.getPosition() < paragraphEnd) {
                TextLayout layout = lineMeasurer.nextLayout(formatWidth);
                drawPosY += layout.getAscent();

                if (drawPosY > (getY() + _h + _topMargin)) {
                    break;
                }

                float drawPosX = 0;

                if (_justification == FigText.JUSTIFY_JUSTIFIED) {

                    layout = layout.getJustifiedLayout(formatWidth);

                    if (layout.isLeftToRight()) {
                        drawPosX = 0 + chunkX;
                    } else {
                        drawPosX = formatWidth - layout.getAdvance() + chunkX;
                    }

                } else if (_justification == FigText.JUSTIFY_RIGHT) {

                    drawPosX = formatWidth - layout.getAdvance() + chunkX;

                } else if (_justification == FigText.JUSTIFY_CENTER) {

                    drawPosX = ((formatWidth - layout.getAdvance()) / 2) + chunkX;

                } else {

                    // default: align is left

                    if (layout.isLeftToRight()) {
                        drawPosX = 0 + chunkX;
                    } else {
                        drawPosX = formatWidth - layout.getAdvance() + chunkX;
                    }

                }

                graphics2D = (Graphics2D) graphicContext;
                Color OriginalColor = graphics2D.getColor();

                graphics2D.setColor(_lineColor);
                layout.draw(graphics2D, drawPosX, drawPosY);

                graphics2D.setColor(OriginalColor);

                drawPosY += layout.getDescent() + layout.getLeading();
            }

        }

    }

    /**
     * Draws the given string starting at the given position. The position
     * indicates the baseline of the text. This method enables subclasses of
     * FigTextLayout to either change the displayed text or the starting
     * position.
     *
     * @param graphics Graphic context for drawing the string.
     * @param curLine  The current text to be drawn.
     * @param xPos     X-Coordinate of the starting point.
     * @param yPos     Y-Coordinate of the starting point.
     */
    protected void drawString(Graphics graphics, String curLine, int xPos, int yPos,
        ExportAttributes exportAttributes) {
    }

    /**
     * Mouse clicks are handled differently that the default Fig behavior so
     * that it is easier to select text that is not filled. Needs-More-Work:
     * should actually check the individual text rectangles.
     */
    @Override public boolean hit(Rectangle r) {
        int cornersHit = countCornersContained(r.x, r.y, r.width, r.height);
        return cornersHit > 0;
    }

    @Override public Dimension getMinimumSize() {
        Dimension d;

        if (_fm != null) {
            d = new Dimension(FigText.MIN_TEXT_WIDTH, _fm.getHeight());
        } else if (_font != null) {
            d = new Dimension(FigText.MIN_TEXT_WIDTH, _font.getSize());
        } else {
            d = new Dimension(FigText.MIN_TEXT_WIDTH, FigText.MIN_TEXT_HEIGHT);
        }

        return d;

    }

    // //////////////////////////////////////////////////////////////
    // event handlers: KeyListener implemtation

    /**
     * When the user presses a key when a FigTextLayout is selected, that key
     * should be added to the current string and we start editing.
     */
    public void keyTyped(KeyEvent ke) {
        // This code must be in keyTyped rather than keyPressed.
        // If in keyPressed some platforms will automatically add the pressed
        // key to the editor when it opens others do not.
        // Using keyTyped it is not automatically added and we do so ourselves
        // if it is not some control character.
        if (isStartEditingKey(ke) && editable) {
            ke.consume();

            FigTextEditor te = startTextEditor(ke);
            if (!Character.isISOControl(ke.getKeyChar())) {
                te.setText(te.getText() + ke.getKeyChar());
            }
        }
    }

    public void keyPressed(KeyEvent ke) {
    }

    public void keyReleased(KeyEvent ke) {
    }

    protected boolean isStartEditingKey(KeyEvent ke) {
        if (ke.getModifiers() == Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) {
            return false;
        }
        return (!Character.isISOControl(ke.getKeyChar()));
    }

    // //////////////////////////////////////////////////////////////
    // event handlers: KeyListener implemtation

    public void mouseClicked(MouseEvent me) {
        if (me.isConsumed()) {
            return;
        }
        if (me.getClickCount() >= 2 && editable) {
            EditorFrame df = Main.getInstance().getSelectedFrame();
            if (df.isFigFullyVisible(this)) {
                startTextEditor(me);
            } else {
                TextPropertyDialog tpd = new TextPropertyDialog(this);
                tpd.setAlignment(_justification);
                tpd.setVisible(true);
            }
            me.consume();
        }
    }

    public void mousePressed(MouseEvent me) {
    }

    public void mouseReleased(MouseEvent me) {
    }

    public void mouseEntered(MouseEvent me) {
    }

    public void mouseExited(MouseEvent me) {
    }

    public FigTextEditor startTextEditor(InputEvent ie) {
        FigTextEditor te = FigTextEditor.getInstance();
        te.init(this, ie);
        return te;
    }

    @Override public EditorPanel getEditorPanel() {
        return new FigTextPanel();
    }

    @Override public EditorMultiPanel getEditorMultiPanel() {
        return new FigTextMultiPanel();
    }

    public int getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(int fontStyle) {
        this.fontStyle = fontStyle;
        CachedFont cf = Main.getInstance().getCachedFontManager()
            .getCachedFont(getFontStyle(), getFontFamily());
        Font f = cf.getFont().deriveFont(getFontSize());
        setFont(f);
    }

    public void setEmbedded(boolean embedded) {
        this.embedded = embedded;
    }

    public boolean isEmbedded() {
        return embedded;
    }

    public int getFsType() {
        return fsType;
    }

    public String getFontFileName() {
        return this.fontFileName;
    }

    public void setFontFileName(String fontFileName) {
        this.fontFileName = fontFileName;
    }

    public void setRenderableText(String renderableText) {
        this.renderableText = renderableText;
    }

    public String getRenderableText() {
        if (renderableText == null) {
            return _curText;
        }
        return renderableText;
    }

}
