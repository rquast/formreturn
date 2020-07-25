package com.ebstrada.formreturn.manager.gef.font;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("cachedFontGroup") public class CachedFontGroup implements NoObfuscation {

    @XStreamAlias("cachedFontFamilies") private Map<String, CachedFontFamily> cachedFontFamilies =
        new HashMap<String, CachedFontFamily>();

    @XStreamAlias("localizedCachedFontFamilies") private Map<String, String>
        localizedCachedFontFamilies = new HashMap<String, String>();

    @XStreamAlias("cachedFontNames") private Map<String, String> cachedFontNames =
        new HashMap<String, String>();

    @XStreamAlias("cachedFontPostScriptNames") private Map<String, String>
        cachedFontPostScriptNames = new HashMap<String, String>();

    public Map<String, String> getCachedFontPostScriptNames() {
        return cachedFontPostScriptNames;
    }

    @XStreamAlias("cachedFontFileNames") private Map<String, String> cachedFontFileNames =
        new HashMap<String, String>();

    public CachedFontFamily getCachedFontFamily(String familyName) {
        return cachedFontFamilies.get(familyName);
    }

    public void setCachedFontFamily(CachedFontFamily cachedFontFamily) {
        cachedFontFamilies.put(cachedFontFamily.getFamilyName(), cachedFontFamily);
        localizedCachedFontFamilies
            .put(cachedFontFamily.getLocalizedFamilyName(), cachedFontFamily.getFamilyName());
    }

    public Map<String, CachedFontFamily> getCachedFontFamilies() {
        return cachedFontFamilies;
    }

    public CachedFont getCachedFont(String fontName) {
        String cachedFontName = cachedFontNames.get(fontName);
        if (cachedFontName == null) {
            // TRY THE POSTSCRIPT NAME (because of bug in osx)
            cachedFontName = cachedFontPostScriptNames.get(fontName);
        }
        CachedFontFamily cachedFontFamily = cachedFontFamilies.get(cachedFontName);
        if (fontName == null || cachedFontFamily == null) {
            return null;
        }
        CachedFont cachedFont = cachedFontFamily.getCachedFont(fontName);
        return cachedFont;
    }

    public CachedFont getCachedFontByFilename(String fontFileName) {
        return cachedFontFamilies.get(cachedFontFileNames.get(fontFileName))
            .getCachedFontByFilename(fontFileName);
    }

    public CachedFont getCachedFont(int style, String family) {
        CachedFontFamily cachedFontFamily = cachedFontFamilies.get(family);
        if (cachedFontFamily == null) {
            return null;
        }
        CachedFont cf = cachedFontFamily.getCachedFont(style);
        return cf;
    }

    public void addCachedFont(CachedFont cf) {
        CachedFontFamily cachedFontFamily = getCachedFontFamily(cf.getFamily());
        if (cachedFontFamily == null) {
            cachedFontFamily = new CachedFontFamily(cf.getFamily(), cf.getLocalizedFamily());
        }
        cachedFontFamily.addCachedFont(cf);
        cachedFontFamilies.put(cf.getFamily(), cachedFontFamily);
        localizedCachedFontFamilies.put(cf.getLocalizedFamily(), cf.getFamily());

        String fn =
            cf.getFontName(); // THIS IS NOT RETURNING THE FULL FONT NAME AS GAINED FROM THE FONT TABLE. IT IS USING THE OTHER ONE!
        String ff = cf.getFamily();
        String psn = cf.getPostScriptName();

        cachedFontNames.put(fn, ff);
        cachedFontPostScriptNames.put(psn, ff);

        cachedFontFileNames.put(cf.getFontFileName(), cf.getFamily());
    }

    public Map<String, String> getCachedFontNames() {
        return cachedFontNames;
    }

    public Map<String, String> getCachedFontFileNames() {
        return cachedFontFileNames;
    }

    public CachedFontFamily getLocalizedCachedFontFamily(String localizedFontFamily) {
        String familyName = localizedCachedFontFamilies.get(localizedFontFamily);
        return cachedFontFamilies.get(familyName);
    }

    public CachedFont getLocalizedCachedFont(int style, String localizedFontFamily) {
        String fontFamily = localizedCachedFontFamilies.get(localizedFontFamily);
        return getCachedFont(style, fontFamily);
    }

    public String getLocalizedCachedFontFamilyName(String familyName) {
        for (Entry<String, String> mapping : localizedCachedFontFamilies.entrySet()) {
            if (mapping.getValue().equals(familyName)) {
                return mapping.getKey();
            }
        }
        return familyName;
    }

}
