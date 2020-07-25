package com.ebstrada.formreturn.manager.gef.font;

import java.util.Locale;

// For a complete list of TTF language ID's, visit:
// http://www.microsoft.com/typography/otspec/name.htm

// To translate the localized names into escaped unicode, visit:
// http://people.w3.org/rishida/tools/conversion/

// For a list of Java Locales, visit:
// http://www.roseindia.net/tutorials/I18N/locales-list.shtml


public enum FontLocalesImpl implements FontLocales {
    en {
        public Locale getLocale() {
            return Locale.ENGLISH;
        }

        public String getLanguageName() {
            return "English";
        }

        public String getLocalizedLanguageName() {
            return "English";
        }

        public int[] getTTFLanguageId() {
            return new int[] {0x0000, 0x0409, 0x0809};
        }

        public String[] getChildLocales() {
            return new String[] {"en", "en_US", "en_GB", "en_AU"};
        }
    }, nl {
        public Locale getLocale() {
            return new Locale("nl");
        }

        public String getLanguageName() {
            return "Dutch";
        }

        public String getLocalizedLanguageName() {
            return "Nederlands";
        }

        public int[] getTTFLanguageId() {
            return new int[] {0x0813, 0x0413};
        }

        public String[] getChildLocales() {
            return new String[] {"nl", "nl_NL", "nl_BE"};
        }
    }, de {
        public Locale getLocale() {
            return new Locale("de");
        }

        public String getLanguageName() {
            return "German";
        }

        public String getLocalizedLanguageName() {
            return "Deutsch";
        }

        public int[] getTTFLanguageId() {
            return new int[] {0x0C07, 0x0407, 0x1407, 0x1007, 0x0807};
        }

        public String[] getChildLocales() {
            return new String[] {"de", "de_DE", "de_AT", "de_LU", "de_CH"};
        }
    }, ar {
        public Locale getLocale() {
            return new Locale("ar");
        }

        public String getLanguageName() {
            return "Arabic";
        }

        public String getLocalizedLanguageName() {
            return "\u0627\u0644\u0639\u0631\u0628\u064a\u0647";
        }

        public int[] getTTFLanguageId() {
            return new int[] {0x1401, 0x3C01, 0x0C01, 0x0801, 0x2C01, 0x3401, 0x3001, 0x1001,
                0x1801, 0x2001, 0x4001, 0x0401, 0x2801, 0x1C01, 0x3801, 0x2401};
        }

        public String[] getChildLocales() {
            return new String[] {"ar", "ar_DZ", "ar_BH", "ar_EG", "ar_IQ", "ar_JO", "ar_KW",
                "ar_LB", "ar_LY", "ar_MA", "ar_OM", "ar_QA", "ar_SD", "ar_SY", "ar_TN", "ar_AE",
                "ar_YE"};
        }
    }, fr {
        public Locale getLocale() {
            return new Locale("fr");
        }

        public String getLanguageName() {
            return "French";
        }

        public String getLocalizedLanguageName() {
            return "Fran\u00e7ais";
        }

        public int[] getTTFLanguageId() {
            return new int[] {0x080C, 0x0C0C, 0x040C, 0x140C, 0x180C, 0x100C};
        }

        public String[] getChildLocales() {
            return new String[] {"fr", "fr_FR", "fr_CA", "fr_CH", "fr_LU", "fr_BE"};
        }
    }, it {
        public Locale getLocale() {
            return new Locale("it");
        }

        public String getLanguageName() {
            return "Italian";
        }

        public String getLocalizedLanguageName() {
            return "Italiano";
        }

        public int[] getTTFLanguageId() {
            return new int[] {0x0410, 0x0810};
        }

        public String[] getChildLocales() {
            return new String[] {"it", "it_IT", "it_CH"};
        }
    }, pt_BR {
        public Locale getLocale() {
            return new Locale("pt", "BR");
        }

        public String getLanguageName() {
            return "Brazilian Portuguese";
        }

        public String getLocalizedLanguageName() {
            return "Portugu\u00EAs do Brasil";
        }

        public int[] getTTFLanguageId() {
            return new int[] {0x0416, 0x0816};
        }

        public String[] getChildLocales() {
            return new String[] {"pt_BR"};
        }
    }, zh_CN {
        public Locale getLocale() {
            return new Locale("zh", "CN");
        }

        public String getLanguageName() {
            return "Simplified Chinese";
        }

        public String getLocalizedLanguageName() {
            return "\u7b80\u4f53\u4e2d\u6587";
        }

        public int[] getTTFLanguageId() {
            return new int[] {0x0804, 0x0850};
        }

        public String[] getChildLocales() {
            return new String[] {"zh_CN"};
        }
    }, el_GR {
        public Locale getLocale() {
            return new Locale("el", "GR");
        }

        public String getLanguageName() {
            return "Greek";
        }

        public String getLocalizedLanguageName() {
            return "\u0395\u03bb\u03bb\u03b7\u03bd\u03b9\u03ba\u03ac";
        }

        public int[] getTTFLanguageId() {
            return new int[] {0x0408};
        }

        public String[] getChildLocales() {
            return new String[] {"el_GR"};
        }
    }, iw {
        public Locale getLocale() {
            return new Locale("iw");
        }

        public String getLanguageName() {
            return "Hebrew";
        }

        public String getLocalizedLanguageName() {
            return "\u05e2\u05d1\u05e8\u05d9\u05ea";
        }

        public int[] getTTFLanguageId() {
            return new int[] {0x040d};
        }

        public String[] getChildLocales() {
            return new String[] {"iw", "iw_IL"};
        }
    }, tr_TR {
        public Locale getLocale() {
            return new Locale("tr", "TR");
        }

        public String getLanguageName() {
            return "Turkish";
        }

        public String getLocalizedLanguageName() {
            return "T\u00fcrk\u00e7e";
        }

        public int[] getTTFLanguageId() {
            return new int[] {0x041F};
        }

        public String[] getChildLocales() {
            return new String[] {"tr_TR"};
        }
    }, zh_TW {
        public Locale getLocale() {
            return new Locale("zh", "TW");
        }

        public String getLanguageName() {
            return "Traditional Chinese";
        }

        public String getLocalizedLanguageName() {
            return "\u7e41\u9ad4\u4e2d\u6587";
        }

        public int[] getTTFLanguageId() {
            return new int[] {0x0404};
        }

        public String[] getChildLocales() {
            return new String[] {"zh_TW"};
        }
    }, ko_KR {
        public Locale getLocale() {
            return new Locale("ko", "KR");
        }

        public String getLanguageName() {
            return "Korean";
        }

        public String getLocalizedLanguageName() {
            return "\ud55c\uad6d\uc5b4";
        }

        public int[] getTTFLanguageId() {
            return new int[] {0x0412};
        }

        public String[] getChildLocales() {
            return new String[] {"ko_KR"};
        }
    }, es {
        public Locale getLocale() {
            return new Locale("es");
        }

        public String getLanguageName() {
            return "Spanish";
        }

        public String getLocalizedLanguageName() {
            return "Espa\u00f1ol";
        }

        public int[] getTTFLanguageId() {
            return new int[] {0x2C0A, 0x400A, 0x340A, 0x240A, 0x140A, 0x1C0A, 0x300A, 0x440A,
                0x100A, 0x480A, 0x080A, 0x4C0A, 0x180A, 0x3C0A, 0x280A, 0x500A, 0x0C0A, 0x040A,
                0x540A, 0x380A, 0x200A};
        }

        public String[] getChildLocales() {
            return new String[] {"es", "es_ES"};
        }
    }, ja {
        public Locale getLocale() {
            return new Locale("ja");
        }

        public String getLanguageName() {
            return "Japanese";
        }

        public String getLocalizedLanguageName() {
            return "\u65e5\u672c\u8a9e";
        }

        public int[] getTTFLanguageId() {
            return new int[] {0x0411};
        }

        public String[] getChildLocales() {
            return new String[] {"ja", "ja_JP"};
        }
    }, th {
        public Locale getLocale() {
            return new Locale("th");
        }

        public String getLanguageName() {
            return "Thai";
        }

        public String getLocalizedLanguageName() {
            return "\u0e20\u0e32\u0e29\u0e32\u0e44\u0e17\u0e22";
        }

        public int[] getTTFLanguageId() {
            return new int[] {0x041E};
        }

        public String[] getChildLocales() {
            return new String[] {"th", "th_TH"};
        }
    }
}
