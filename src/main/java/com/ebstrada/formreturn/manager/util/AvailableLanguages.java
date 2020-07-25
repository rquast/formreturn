package com.ebstrada.formreturn.manager.util;

import java.util.ArrayList;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;

import com.ebstrada.formreturn.manager.gef.font.FontLocalesImpl;

public class AvailableLanguages {

    private ArrayList<String> languages;

    private ArrayList<String> codes;

    public AvailableLanguages() {
        languages = new ArrayList<String>();
        codes = new ArrayList<String>();
        for (FontLocalesImpl fontLocale : FontLocalesImpl.values()) {
            languages.add(
                fontLocale.getLanguageName() + " - (" + fontLocale.getLocalizedLanguageName()
                    + ")");
            codes.add(fontLocale.name());
        }
    }

    public ComboBoxModel getLanguageComboBoxModel() {
        DefaultComboBoxModel lcbm = new DefaultComboBoxModel();
        for (String language : languages) {
            lcbm.addElement(language);
        }
        return lcbm;
    }

    public String getCodeAtIndex(int index) {
        return codes.get(index);
    }

}
