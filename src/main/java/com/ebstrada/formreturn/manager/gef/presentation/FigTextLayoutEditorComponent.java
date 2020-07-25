package com.ebstrada.formreturn.manager.gef.presentation;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.ebstrada.formreturn.manager.gef.base.Editor;
import com.ebstrada.formreturn.manager.gef.base.Globals;
import com.ebstrada.formreturn.manager.gef.undo.UndoManager;
import com.ebstrada.formreturn.manager.ui.Main;

@SuppressWarnings("serial") public class FigTextLayoutEditorComponent extends JTextPane
    implements PropertyChangeListener, DocumentListener, KeyListener {

    private FigText figText;

    private static int _extraSpace = 2;

    @SuppressWarnings("unused") private static Border _border =
        BorderFactory.createLineBorder(Color.gray);

    private static boolean _makeBrighter = false;

    private static Color _backgroundColor = null;

    private static Color _foregroundColor = null;

    // TODO: Lets try and remove this and have the only reference
    // from Editor
    private transient static FigTextEditor _activeTextEditor;

    public FigTextLayoutEditorComponent() {
    }

    public static void configure(int extraSpace, Border b, boolean makeBrighter,
        Color backgroundColor, Color foregroundColor) {
        FigTextLayoutEditorComponent._extraSpace = extraSpace;
        FigTextLayoutEditorComponent._border = b;
        FigTextLayoutEditorComponent._makeBrighter = makeBrighter;
        FigTextLayoutEditorComponent._backgroundColor = backgroundColor;
        FigTextLayoutEditorComponent._foregroundColor = foregroundColor;
    }

    public void init(FigText ft, InputEvent ie, FigTextEditor figTextEditor) {

        if (Main.MAC_OS_X) {
            int MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
            getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_A, MASK), "select-all");
            getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_C, MASK), "copy");
            getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_X, MASK), "cut");
            getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_V, MASK), "paste");
        }

        setVisible(true);

        figText = ft;
        Editor ce = Globals.curEditor();

        UndoManager.getInstance().startChain();
        figText.firePropChange("editing", false, true);
        figText.addPropertyChangeListener(this);

        Rectangle bbox = ft.getBounds();
        setSize(ft.getWidth(), ft.getHeight());

        Color figTextBackgroundColor = ft.getFillColor();
        Color myBackground;
        if (FigTextLayoutEditorComponent._makeBrighter && !figTextBackgroundColor
            .equals(Color.white)) {
            myBackground = figTextBackgroundColor.brighter();
        } else if (FigTextLayoutEditorComponent._backgroundColor != null) {
            myBackground = FigTextLayoutEditorComponent._backgroundColor;
        } else {
            myBackground = figTextBackgroundColor;
        }

        Color figTextForegroundColor = ft.getLineColor();
        Color myForeground;
        if (FigTextLayoutEditorComponent._foregroundColor != null) {
            myForeground = FigTextLayoutEditorComponent._foregroundColor;
        } else {
            myForeground = figTextForegroundColor;
        }

        setBackground(myBackground);
        setForeground(myForeground);

        double scale = ce.getScale();
        bbox.x = (int) Math.round(bbox.x * scale);
        bbox.y = (int) Math.round(bbox.y * scale);

        if (scale > 1) {
            bbox.width = (int) Math.round(bbox.width * scale);
            bbox.height = (int) Math.round(bbox.height * scale);
        }
        setBounds(bbox.x - FigTextLayoutEditorComponent._extraSpace,
            bbox.y - FigTextLayoutEditorComponent._extraSpace,
            bbox.width + FigTextLayoutEditorComponent._extraSpace * 2,
            bbox.height + FigTextLayoutEditorComponent._extraSpace * 2);
        String text = ft.getTextFriend();
        FigTextLayoutEditorComponent._activeTextEditor = figTextEditor;
        setText(text);
        addKeyListener(this);
        requestFocus();
        getDocument().addDocumentListener(this);
        setSelectionStart(0);
        setSelectionEnd(getDocument().getLength());
        MutableAttributeSet attr = new SimpleAttributeSet();
        if (ft.getJustification() == FigText.JUSTIFY_CENTER) {
            StyleConstants.setAlignment(attr, StyleConstants.ALIGN_CENTER);
        }
        if (ft.getJustification() == FigText.JUSTIFY_RIGHT) {
            StyleConstants.setAlignment(attr, StyleConstants.ALIGN_RIGHT);
        }
        if (ft.getJustification() == FigText.JUSTIFY_JUSTIFIED) {
            StyleConstants.setAlignment(attr, StyleConstants.ALIGN_JUSTIFIED);
        }
        Font font = ft.getFont();

        setFont(font.deriveFont(ft.getFontSize()));
        StyleConstants.setFontFamily(attr, font.getFamily());
        StyleConstants.setFontSize(attr, (int) Math.round((font.getSize() * scale)));
        StyleConstants.setForeground(attr, myForeground);
        setParagraphAttributes(attr, true);
        if (ie instanceof KeyEvent) {
            setSelectionStart(getDocument().getLength());
            setSelectionEnd(getDocument().getLength());
        }
    }

    // disabled antialiasing because of issues with the cursor being in the wrong position when editing.
    /*
    public void paintComponent(Graphics g) {
	Graphics2D g2 = (Graphics2D) g;
	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
	g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        super.paintComponent(g2);
    }
    */

    public void propertyChange(PropertyChangeEvent pve) {
        updateFigText();
    }

    public void endEditing() {
        Editor ce = Globals.curEditor();
        updateFigText();
        setVisible(false);
        if (figText == null) {
            return;
        }
        figText.endTrans();
        figText.removePropertyChangeListener(this);
        figText.firePropChange("editing", true, false);
        removeKeyListener(this);
        FigTextLayoutEditorComponent._activeTextEditor = null;
        ce.setActiveTextEditor(null);
    }

    public void cancelEditing() {
        setVisible(false);
        if (figText == null) {
            return;
        }
        figText.endTrans();
        figText.removePropertyChangeListener(this);
        removeKeyListener(this);
        FigTextLayoutEditorComponent._activeTextEditor = null;
    }

    public static synchronized FigTextEditor getActiveTextEditor() {
        return FigTextLayoutEditorComponent._activeTextEditor;
    }

    public static synchronized void remove() {
        if (FigTextLayoutEditorComponent._activeTextEditor != null) {
            FigTextEditor old = FigTextLayoutEditorComponent._activeTextEditor;
            FigTextLayoutEditorComponent._activeTextEditor = null;
            old.endEditing();
        }
    }

    // //////////////////////////////////////////////////////////////
    // event handlers for KeyListener implementaion
    public void keyTyped(KeyEvent ke) {
    }

    public void keyReleased(KeyEvent ke) {
    }

    /**
     * End editing on enter or tab if configured. Also ends on escape or F2.
     * This is coded on keypressed rather than keyTyped as keyTyped may already
     * have applied the key to the underlying document.
     */
    public void keyPressed(KeyEvent ke) {

        if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
            if (figText.getReturnAction() == FigText.END_EDITING) {
                endEditing();
                ke.consume();
            }
        } else if (ke.getKeyCode() == KeyEvent.VK_TAB) {
            if (figText.getTabAction() == FigText.END_EDITING) {
                endEditing();
                ke.consume();
            }
        } else if (ke.getKeyCode() == KeyEvent.VK_F2) {
            endEditing();
            ke.consume();
        } else if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
            // needs-more-work: should revert to orig text.
            endEditing();
            ke.consume();
        }
    }

    // //////////////////////////////////////////////////////////////
    // event handlers for DocumentListener implementaion

    public void insertUpdate(DocumentEvent e) {
        updateFigText();
    }

    public void removeUpdate(DocumentEvent e) {
        updateFigText();
    }

    public void changedUpdate(DocumentEvent e) {
        updateFigText();
    }

    // //////////////////////////////////////////////////////////////
    // internal utility methods

    protected void updateFigText() {
        if (figText == null) {
            return;
        }
        String text = getText();

        figText.setTextFriend(text, getGraphics());
    }

    public FigText getFigText() {
        return figText;
    }
}
