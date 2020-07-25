package com.ebstrada.formreturn.manager.gef.font;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;

import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("cachedFontFamily") public class CachedFontFamily implements NoObfuscation {

    @XStreamAlias("familyName") private String familyName;

    @XStreamAlias("localizedFamilyName") private String localizedFamilyName;

    @XStreamAlias("fontNames") private Map<String, Integer> fontNameMap =
        new HashMap<String, Integer>();

    @XStreamAlias("fontPostScriptNames") private Map<String, Integer> fontPostScriptNameMap =
        new HashMap<String, Integer>();

    @XStreamAlias("fontPostScriptNames") private Map<String, Integer> fontFileNameMap =
        new HashMap<String, Integer>();

    @XStreamAlias("cachedFontStyleBold") private CachedFont cachedFontStyleBold;

    @XStreamAlias("cachedFontStyleItalic") private CachedFont cachedFontStyleItalic;

    @XStreamAlias("cachedFontStylePlain") private CachedFont cachedFontStylePlain;

    @XStreamAlias("cachedFontStyleBoldItalic") private CachedFont cachedFontStyleBoldItalic;

    public CachedFontFamily(String familyName, String localizedFamilyName) {
        this.familyName = familyName;
        this.localizedFamilyName = localizedFamilyName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void addCachedFont(CachedFont cf) {
        int style = cf.getStyle();

        if (cf.getFontName().startsWith("Arial Black")) {
            this.fontNameMap.put("Arial Black", style);
            this.fontNameMap.put("Arial Black Normal", style);
            this.fontPostScriptNameMap.put("Arial Black", style);
            this.fontPostScriptNameMap.put("Arial Black Normal", style);
        } else {
            this.fontNameMap.put(cf.getFontName(), style);
            this.fontPostScriptNameMap.put(cf.getPostScriptName(), style);
        }


        this.fontFileNameMap.put(cf.getFontFileName(), style);
        switch (style) {
            case Font.BOLD:
                this.cachedFontStyleBold = cf;
                break;
            case Font.ITALIC:
                this.cachedFontStyleItalic = cf;
                break;
            case Font.PLAIN:
                this.cachedFontStylePlain = cf;
                break;
            case Font.BOLD + Font.ITALIC:
                this.cachedFontStyleBoldItalic = cf;
                break;
        }
    }

    public CachedFont getCachedFont(int style) {
        switch (style) {
            case Font.BOLD:
                return cachedFontStyleBold;
            case Font.ITALIC:
                return cachedFontStyleItalic;
            case Font.PLAIN:
                return cachedFontStylePlain;
            case Font.BOLD + Font.ITALIC:
                return cachedFontStyleBoldItalic;
        }
        return null;
    }

    public DefaultComboBoxModel getAvailableStylesList() {
        DefaultComboBoxModel availableStylesList = new DefaultComboBoxModel();
        if (cachedFontStylePlain != null) {
            availableStylesList.addElement("Plain");
        }
        if (cachedFontStyleBold != null) {
            availableStylesList.addElement("Bold");
        }
        if (cachedFontStyleItalic != null) {
            availableStylesList.addElement("Italic");
        }
        if (cachedFontStyleBoldItalic != null) {
            availableStylesList.addElement("Bold & Italic");
        }
        return availableStylesList;
    }

    public CachedFont getCachedFont(String fontName) {

        int style = Font.PLAIN;

        if (this.fontNameMap.get(fontName) != null) {
            style = this.fontNameMap.get(fontName);
        } else {
            if (this.fontPostScriptNameMap != null && fontName != null) {
                style = this.fontPostScriptNameMap.get(fontName);
            } else {
                System.out.println("uh oh.");
            }
        }

        return getCachedFont(style);
    }

    public CachedFont getCachedFontByFilename(String fontFileName) {
        int style = this.fontFileNameMap.get(fontFileName);
        return getCachedFont(style);
    }

    public String getLocalizedFamilyName() {
        return localizedFamilyName;
    }

    public void setLocalizedFamilyName(String localizedFamilyName) {
        this.localizedFamilyName = localizedFamilyName;
    }

}
