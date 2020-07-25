package com.ebstrada.formreturn.manager.util.graph;

import java.util.ArrayList;
import java.util.List;

public class SizePresets {

    public static List<SizeAttributes[]> formSizeAttributes = new ArrayList<SizeAttributes[]>();

    public static List<SizeAttributes[]> segmentSizeAttributes = new ArrayList<SizeAttributes[]>();

    public static SizeAttributes getPresetSize(String presetName, int graphType, int orientation) {

        SizeAttributes sizeAttributes = new SizeAttributes();

        if (formSizeAttributes == null || formSizeAttributes.size() <= 0) {
            getFormSizeAttributes();
        }

        if (segmentSizeAttributes == null || segmentSizeAttributes.size() <= 0) {
            getSegmentSizeAttributes();
        }

        if (graphType == SizeAttributes.FORM) {

            for (SizeAttributes[] formSizeAttributePair : formSizeAttributes) {
                SizeAttributes portraitFormSizeAttribute =
                    formSizeAttributePair[(SizeAttributes.PORTRAIT - 1)];
                if (portraitFormSizeAttribute.getName().trim()
                    .equalsIgnoreCase(presetName.trim())) {
                    return formSizeAttributePair[(orientation - 1)];
                }
            }

        } else if (graphType == SizeAttributes.SEGMENT) {

            for (SizeAttributes[] segmentSizeAttributePair : segmentSizeAttributes) {
                SizeAttributes portraitSegmentSizeAttribute =
                    segmentSizeAttributePair[(SizeAttributes.PORTRAIT - 1)];
                if (portraitSegmentSizeAttribute.getName().trim()
                    .equalsIgnoreCase(presetName.trim())) {
                    return segmentSizeAttributePair[(orientation - 1)];
                }
            }

        }

        return sizeAttributes;

    }

    public static List<SizeAttributes[]> getFormSizeAttributes() {

        formSizeAttributes = new ArrayList<SizeAttributes[]>();
        SizeAttributes[] sizeAttributes = new SizeAttributes[2];

        sizeAttributes[(SizeAttributes.PORTRAIT - 1)] = new SizeAttributes();
        sizeAttributes[(SizeAttributes.PORTRAIT - 1)].setName("A4");
        sizeAttributes[(SizeAttributes.PORTRAIT - 1)].setOrientation(SizeAttributes.PORTRAIT);
        sizeAttributes[(SizeAttributes.PORTRAIT - 1)].setWidth(595);
        sizeAttributes[(SizeAttributes.PORTRAIT - 1)].setHeight(842);
        sizeAttributes[(SizeAttributes.PORTRAIT - 1)].setLeftMargin(30);
        sizeAttributes[(SizeAttributes.PORTRAIT - 1)].setRightMargin(30);
        sizeAttributes[(SizeAttributes.PORTRAIT - 1)].setTopMargin(20);
        sizeAttributes[(SizeAttributes.PORTRAIT - 1)].setBottomMargin(20);
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)] = new SizeAttributes();
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)].setName("A4");
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)].setOrientation(SizeAttributes.LANDSCAPE);
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)].setWidth(842);
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)].setHeight(595);
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)].setLeftMargin(20);
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)].setRightMargin(20);
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)].setTopMargin(30);
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)].setBottomMargin(30);
        formSizeAttributes.add(sizeAttributes);

        sizeAttributes = new SizeAttributes[2];
        sizeAttributes[(SizeAttributes.PORTRAIT - 1)] = new SizeAttributes();
        sizeAttributes[(SizeAttributes.PORTRAIT - 1)].setName("Letter");
        sizeAttributes[(SizeAttributes.PORTRAIT - 1)].setOrientation(SizeAttributes.PORTRAIT);
        sizeAttributes[(SizeAttributes.PORTRAIT - 1)].setWidth(612);
        sizeAttributes[(SizeAttributes.PORTRAIT - 1)].setHeight(792);
        sizeAttributes[(SizeAttributes.PORTRAIT - 1)].setLeftMargin(30);
        sizeAttributes[(SizeAttributes.PORTRAIT - 1)].setRightMargin(30);
        sizeAttributes[(SizeAttributes.PORTRAIT - 1)].setTopMargin(20);
        sizeAttributes[(SizeAttributes.PORTRAIT - 1)].setBottomMargin(20);
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)] = new SizeAttributes();
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)].setName("Letter");
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)].setOrientation(SizeAttributes.LANDSCAPE);
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)].setWidth(792);
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)].setHeight(612);
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)].setLeftMargin(20);
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)].setRightMargin(20);
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)].setTopMargin(30);
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)].setBottomMargin(30);
        formSizeAttributes.add(sizeAttributes);

        return formSizeAttributes;
    }

    public static List<SizeAttributes[]> getSegmentSizeAttributes() {

        segmentSizeAttributes = new ArrayList<SizeAttributes[]>();

        // A4

        SizeAttributes[] sizeAttributes = new SizeAttributes[2];

        sizeAttributes[(SizeAttributes.PORTRAIT - 1)] = new SizeAttributes();
        sizeAttributes[(SizeAttributes.PORTRAIT - 1)].setName("A4");
        sizeAttributes[(SizeAttributes.PORTRAIT - 1)].setOrientation(SizeAttributes.PORTRAIT);
        sizeAttributes[(SizeAttributes.PORTRAIT - 1)].setWidth(535);
        sizeAttributes[(SizeAttributes.PORTRAIT - 1)].setHeight(802);
        sizeAttributes[(SizeAttributes.PORTRAIT - 1)].setLeftMargin(0);
        sizeAttributes[(SizeAttributes.PORTRAIT - 1)].setRightMargin(0);
        sizeAttributes[(SizeAttributes.PORTRAIT - 1)].setTopMargin(0);
        sizeAttributes[(SizeAttributes.PORTRAIT - 1)].setBottomMargin(0);
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)] = new SizeAttributes();
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)].setName("A4");
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)].setOrientation(SizeAttributes.LANDSCAPE);
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)].setWidth(802);
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)].setHeight(535);
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)].setLeftMargin(0);
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)].setRightMargin(0);
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)].setTopMargin(0);
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)].setBottomMargin(0);
        segmentSizeAttributes.add(sizeAttributes);

        // LETTER

        sizeAttributes = new SizeAttributes[2];

        sizeAttributes[(SizeAttributes.PORTRAIT - 1)] = new SizeAttributes();
        sizeAttributes[(SizeAttributes.PORTRAIT - 1)].setName("Letter");
        sizeAttributes[(SizeAttributes.PORTRAIT - 1)].setOrientation(SizeAttributes.PORTRAIT);
        sizeAttributes[(SizeAttributes.PORTRAIT - 1)].setWidth(552);
        sizeAttributes[(SizeAttributes.PORTRAIT - 1)].setHeight(752);
        sizeAttributes[(SizeAttributes.PORTRAIT - 1)].setLeftMargin(0);
        sizeAttributes[(SizeAttributes.PORTRAIT - 1)].setRightMargin(0);
        sizeAttributes[(SizeAttributes.PORTRAIT - 1)].setTopMargin(0);
        sizeAttributes[(SizeAttributes.PORTRAIT - 1)].setBottomMargin(0);
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)] = new SizeAttributes();
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)].setName("Letter");
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)].setOrientation(SizeAttributes.LANDSCAPE);
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)].setWidth(752);
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)].setHeight(552);
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)].setLeftMargin(0);
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)].setRightMargin(0);
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)].setTopMargin(0);
        sizeAttributes[(SizeAttributes.LANDSCAPE - 1)].setBottomMargin(0);
        segmentSizeAttributes.add(sizeAttributes);

        return segmentSizeAttributes;
    }

}
