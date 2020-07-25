package com.ebstrada.formreturn.manager.gef.base;

import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.ui.PopupGenerator;
import com.ebstrada.formreturn.manager.gef.util.Localizer;

/**
 * A permanent Mode to catch right-mouse-button events and show a popup menu.
 * Needs-more-work: this is not fully implemented yet. It should ask the Fig
 * under the mouse what menu it should offer.
 */

public class ModePopup extends FigModifyingModeImpl {

    private static final long serialVersionUID = 288785293995576958L;
    private static final Log LOG = LogFactory.getLog(ModePopup.class);

    // //////////////////////////////////////////////////////////////
    // constructor

    public ModePopup(Editor par) {
        super(par);
    }

    // //////////////////////////////////////////////////////////////
    // accessors

    /**
     * Always false because I never want to get out of popup mode.
     */
    @Override public boolean canExit() {
        return false;
    }

    @Override public String instructions() {
        return " ";
    }

    public boolean showPopup(MouseEvent me) {
        int x = me.getX();
        int y = me.getY();
        Fig underMouse = editor.hit(x, y);

        // if no Fig is under the mouse, show the editor's popup menu
        if (underMouse == null) {

            // JPopupMenu editorPopup = editor.getPopupMenu();
            // modify for the editor popup
            JPopupMenu editorPopup = new JPopupMenu();

            editorPopup.setFont(UIManager.getFont("PopupMenu.font"));
            JMenu editMenu = new JMenu(Localizer.localize("PresentationGef", "Edit"));
            // -- paste
            JMenuItem pasteItem = new JMenuItem();
            pasteItem = editMenu.add(new PasteAction("Paste"));
            pasteItem.setFont(UIManager.getFont("MenuItem.font"));
            pasteItem.setText("Paste");
            // pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
            //    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            pasteItem.setIcon(new ImageIcon(getClass().getResource(
                "/com/ebstrada/formreturn/manager/ui/icons/mainmenu/paste_plain.png")));
            // editMenu.add(pasteItem);
            // editorPopup.add(editMenu);
            editorPopup.add(pasteItem);

            if (editorPopup != null) {
                // if the editor has a popup menu, show it
                me = editor.retranslateMouseEvent(me);
                editorPopup.show(me.getComponent(), me.getX(), me.getY());
                me.consume();
                return true;
            }
        }

        if (!(underMouse instanceof PopupGenerator)) {
            return false;
        }

        SelectionManager selectionManager = editor.getSelectionManager();
        if (!selectionManager.containsFig(underMouse)) {
            selectionManager.select(underMouse);
        } else {
            Vector selection = selectionManager.getFigs();
            Vector reassertSelection = new Vector(selection);
            selectionManager.select(reassertSelection);
        }

        Class commonClass = selectionManager.findCommonSuperClass();
        if (commonClass != null) {

            Object commonInstance = selectionManager.findFirstSelectionOfType(commonClass);

            if (commonInstance instanceof PopupGenerator) {

                PopupGenerator popupGenerator = (PopupGenerator) commonInstance;
                List actions = popupGenerator.getPopUpActions(me);

                JPopupMenu popup = new JPopupMenu();
                popup.setFont(UIManager.getFont("PopupMenu.font"));

                int size = actions.size();
                for (int i = 0; i < size; ++i) {
                    Object a = actions.get(i);
                    if (a instanceof AbstractAction) {
                        popup.add((AbstractAction) a);
                    } else if (a instanceof JMenu) {
                        ((JMenu) a).setFont(UIManager.getFont("Menu.font"));
                        popup.add((JMenu) a);
                    } else if (a instanceof JMenuItem) {
                        ((JMenuItem) a).setFont(UIManager.getFont("MenuItem.font"));
                        popup.add((JMenuItem) a);
                    } else if (a instanceof JSeparator) {
                        popup.add((JSeparator) a);
                    }
                }
                me = editor.retranslateMouseEvent(me);
                popup.show(editor.getJComponent(), me.getX(), me.getY());
                me.consume();
                return true;
            }

            if (commonInstance == null) {
                System.out.println("must be a common instance of fig...");

                // TODO: put group popup logic here!!!

            }


        }
        return false;
    }

    @Override public void mouseReleased(MouseEvent me) {
    }

    @Override public void mousePressed(MouseEvent me) {
        boolean popUpDisplayed = false;
        if (me.isPopupTrigger() || me.getModifiers() == InputEvent.BUTTON3_MASK) {
            popUpDisplayed = showPopup(me);
        }
    }

    @Override public void mouseClicked(MouseEvent me) {
    }
}
