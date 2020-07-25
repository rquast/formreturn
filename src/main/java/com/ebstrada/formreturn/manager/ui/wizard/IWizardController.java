package com.ebstrada.formreturn.manager.ui.wizard;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;

public interface IWizardController {

    public HashMap<String, Object> settings = new HashMap<String, Object>();

    public void back() throws Exception;

    public void next() throws Exception;

    public void cancel() throws Exception;

    public void finish() throws Exception;

    public JPanel getActivePanel();

    public ArrayList<Integer> getActiveButtons();

    public String getWizardTitle();

}
