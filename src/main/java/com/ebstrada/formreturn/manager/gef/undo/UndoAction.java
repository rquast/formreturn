//$Id: UndoAction.java 1153 2008-11-30 16:14:45Z bobtarling $
//Copyright (c) 1996-99 The Regents of the University of California. All
//Rights Reserved. Permission to use, copy, modify, and distribute this
//software and its documentation without fee, and without a written
//agreement is hereby granted, provided that the above copyright notice
//and this paragraph appear in all copies.  This software program and
//documentation are copyrighted by The Regents of the University of
//California. The software program and documentation are supplied "AS
//IS", without any accompanying services from The Regents. The Regents
//does not warrant that the operation of the program will be
//uninterrupted or error-free. The end-user understands that the program
//was developed for research purposes and is advised not to rely
//exclusively on the program for any reason.  IN NO EVENT SHALL THE
//UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
//SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
//ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
//THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
//SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
//WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
//MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
//PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
//CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
//UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

package com.ebstrada.formreturn.manager.gef.undo;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.SwingUtilities;

/**
 * An action to undo from the undo stack.
 *
 * @author Bob Tarling
 */
public class UndoAction extends AbstractAction implements PropertyChangeListener {

    /**
     * Construct the undo action with a display name
     *
     * @param name
     */
    public UndoAction(String name) {
        super(name);
        UndoManager.getInstance().addPropertyChangeListener(this);
        setEnabled(false);
    }

    /**
     * Construct the undo action with a display name and icon.
     *
     * @param name
     * @param icon
     */
    public UndoAction(String name, Icon icon) {
        super(name, icon);
        UndoManager.getInstance().addPropertyChangeListener(this);
        setEnabled(false);
    }

    /**
     * Perform the undo action.
     */
    public void actionPerformed(ActionEvent e) {

        // invokeLater is here because it's a horrible hack to make the undo manager invoke
        // after everything has finished drawing.

        // part of the reason why we need to replace the editor with swef.

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                UndoManager.getInstance().undo();
            }
        });
    }

    /**
     * When the undo stack is down to zero we are disabled.
     */
    public void propertyChange(PropertyChangeEvent event) {
        if ("canUndo".equals(event.getPropertyName())) {
            setEnabled("true".equals(event.getNewValue()));
        }
    }
}
