package com.ebstrada.formreturn.manager.ui.wizard;

import java.util.ArrayList;

public interface IWizardPanel {

    public IWizardPanel next() throws Exception;

    public IWizardPanel back() throws Exception;

    public void finish() throws Exception;

    public void cancel() throws Exception;

    public ArrayList<Integer> getActiveButtons();

}