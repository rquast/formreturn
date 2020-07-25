package com.ebstrada.formreturn.manager.ui.tab;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.EventListener;


public interface CloseListener extends EventListener {
    public void closeOperation(CloseListener cl, MouseEvent e, int overTabIndex);

    public void closeOperation(CloseListener cl, ActionEvent e, int overTabIndex);
}
