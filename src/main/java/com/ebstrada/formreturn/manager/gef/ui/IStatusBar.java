package com.ebstrada.formreturn.manager.gef.ui;

/**
 * Interface that must be implemented by any window that wants to take
 * responsibility for displaying status bar information (e.g., "Loading
 * File...".
 */

public interface IStatusBar {
    public void showStatus(String s);
}
