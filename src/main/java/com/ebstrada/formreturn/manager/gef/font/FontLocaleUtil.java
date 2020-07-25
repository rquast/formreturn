package com.ebstrada.formreturn.manager.gef.font;

import java.util.Locale;

public class FontLocaleUtil {

    public static FontLocalesImpl getFontLocale(Locale locale) {
        return getFontLocale(locale.toString());
    }

    public static FontLocalesImpl getFontLocale(String locale) {
        for (FontLocalesImpl fontLocale : FontLocalesImpl.values()) {
            for (String childLocaleStr : fontLocale.getChildLocales()) {
                if (locale.equalsIgnoreCase(childLocaleStr)) {
                    return fontLocale;
                }
            }
        }
        return FontLocalesImpl.en;
    }

}
