package com.ebstrada.formreturn.manager.gef.presentation;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import com.ebstrada.formreturn.manager.gef.base.Editor;
import com.ebstrada.formreturn.manager.gef.base.Globals;

/**
 * A text pane for on screen editing of a FigTextLayout. TODO: This should not
 * be a singleton but should be an instance owned by Editor.
 */
public class FigTextEditor extends JPopupMenu {

    private double scale;

    private static final long serialVersionUID = 1L;

    private static final FigTextEditor INSTANCE = new FigTextEditor();

    private static FigTextLayoutEditorComponent _textEditorComponent;

    public FigTextEditor() {
        _textEditorComponent = new FigTextLayoutEditorComponent();
        add(_textEditorComponent);
        _textEditorComponent.addFocusListener(new FocusListener() {
            public void focusLost(FocusEvent evt) {
                endEditing();
            }

            public void focusGained(FocusEvent evt) {
            }
        });
    }

    public static FigTextEditor getInstance() {
        return FigTextEditor.INSTANCE;
    }

    public void init(FigText ft, InputEvent ie) {
        Editor ed = Globals.curEditor();
        scale = ed.getScale();

        JComponent invoker = Globals.curEditor().getJComponent();
        show(invoker, (int) (ft.getX() * scale), (int) (ft.getY() * scale));
        setPopupSize((int) (ft.getWidth() * scale), (int) (ft.getHeight() * scale));
        _textEditorComponent.init(ft, ie, this);
    }
    
    /*
    public void paintComponent(Graphics g) {
	Graphics2D g2 = (Graphics2D) g;
	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
	g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        super.paintComponent(g2);
    }
    */

    public void setText(String s) {
        _textEditorComponent.setText(s);
    }

    public String getText() {
        return _textEditorComponent.getText();
    }

    public FigText getFigText() {
        return _textEditorComponent.getFigText();
    }

    public void endEditing() {
        _textEditorComponent.endEditing();
    }

    public void cancelEditing() {
        _textEditorComponent.cancelEditing();
    }

    /**
     * @return the _textEditorComponent
     */
    public static FigTextLayoutEditorComponent get_textEditorComponent() {
        return _textEditorComponent;
    }

    /**
     * @param editorComponent the _textEditorComponent to set
     */
    public static void set_textEditorComponent(FigTextLayoutEditorComponent editorComponent) {
        _textEditorComponent = editorComponent;
    }

}
