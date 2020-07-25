package com.ebstrada.formreturn.manager.gef.font;

import java.util.Locale;

public interface FontLocales {
    public Locale getLocale();

    public String getLanguageName();

    public String getLocalizedLanguageName();

    public int[] getTTFLanguageId();

    public String[] getChildLocales();
}
