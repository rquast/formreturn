package com.ebstrada.formreturn.manager.util.preferences.persistence;

import java.util.ArrayList;
import java.util.List;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.util.NoObfuscation;

public class PublicationPreferences implements NoObfuscation {

    public final static transient int FORM_ID_RECONCILE_WITH_SOURCE_DATA_RECORD = 1;
    public final static transient int RECONCILE_KEY_WITH_SOURCE_DATA_RECORD_NO_CREATE = 2;
    public final static transient int RECONCILE_KEY_WITH_SOURCE_DATA_RECORD_CREATE_NEW = 3;

    private boolean collatePDFPages = true;

    private boolean errorDuplicateScans = false;

    private int defaultPublicationType = FORM_ID_RECONCILE_WITH_SOURCE_DATA_RECORD;

    public boolean isCollatePDFPages() {
        return collatePDFPages;
    }

    public void setCollatePDFPages(boolean collatePDFPages) {
        this.collatePDFPages = collatePDFPages;
    }

    public boolean isErrorDuplicateScans() {
        return errorDuplicateScans;
    }

    public void setErrorDuplicateScans(boolean errorDuplicateScans) {
        this.errorDuplicateScans = errorDuplicateScans;
    }

    public int getDefaultPublicationType() {
        return defaultPublicationType;
    }

    public void setDefaultPublicationType(int defaultPublicationType) {
        this.defaultPublicationType = defaultPublicationType;
    }

    public static List<String> getPublicationTypes() {
        ArrayList<String> publicationTypes = new ArrayList<String>();
        publicationTypes.add(Localizer.localize("Util", "PublicationType0"));
        publicationTypes.add(Localizer.localize("Util", "PublicationType1"));
        publicationTypes.add(Localizer.localize("Util", "PublicationType2"));
        return publicationTypes;
    }

}
