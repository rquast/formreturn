package com.ebstrada.formreturn.manager.util.graph;

import com.ebstrada.formreturn.manager.gef.graph.presentation.JGraph;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;

public class GraphUtils {

    public static SizeAttributes getDefaultSizeAttributes(int graphType, int orientation) {
        return PreferencesManager.getDefaultSizeAttributes(graphType, orientation);
    }

    public static SizeAttributes getDefaultSizeAttributes(int graphType, int orientation,
        String presetName) {
        return PreferencesManager.getDefaultSizeAttributes(graphType, orientation, presetName);
    }

    public static boolean sizesMatch(SizeAttributes sizeAttributes1,
        SizeAttributes sizeAttributes2) {

        if (sizeAttributes1.getWidth() != sizeAttributes2.getWidth()) {
            return false;
        }

        if (sizeAttributes1.getHeight() != sizeAttributes2.getHeight()) {
            return false;
        }

        if (sizeAttributes1.getLeftMargin() != sizeAttributes2.getLeftMargin()) {
            return false;
        }

        if (sizeAttributes1.getRightMargin() != sizeAttributes2.getRightMargin()) {
            return false;
        }

        if (sizeAttributes1.getTopMargin() != sizeAttributes2.getTopMargin()) {
            return false;
        }

        if (sizeAttributes1.getBottomMargin() != sizeAttributes2.getBottomMargin()) {
            return false;
        }

        return true;
    }

    public static String getPresetName(SizeAttributes sizeAttributes) {
        // TODO: checks the dimensions if they match any presets, returns custom if doesnt match anything
        return "";
    }

    public static SizeAttributes getPresetSize(String presetName, int orientation) {
        // TODO: returns a SizeAttributes object with the preset sizes
        return new SizeAttributes();
    }

    public static boolean checkPageSettings(SizeAttributes sizeAttributes) {

        if (sizeAttributes.getWidth() < (sizeAttributes.getLeftMargin() + sizeAttributes
            .getRightMargin())) {
            String message = String
                .format(Localizer.localize("Util", "GraphUtilsLeftRightMarginTooBigMessage"),
                    (sizeAttributes.getLeftMargin() + sizeAttributes.getRightMargin()) + "",
                    sizeAttributes.getWidth() + "");
            String caption = Localizer.localize("Util", "GraphUtilsInvalidMarginSizeTitle");
            javax.swing.JOptionPane.showConfirmDialog(Main.getInstance(), message, caption,
                javax.swing.JOptionPane.DEFAULT_OPTION, javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (sizeAttributes.getHeight() < (sizeAttributes.getTopMargin() + sizeAttributes
            .getBottomMargin())) {
            String message = String
                .format(Localizer.localize("Util", "GraphUtilsTopBottomMarginTooBigMessage"),
                    (sizeAttributes.getTopMargin() + sizeAttributes.getBottomMargin()) + "",
                    sizeAttributes.getHeight() + "");
            String caption = Localizer.localize("Util", "GraphUtilsInvalidMarginSizeTitle");
            javax.swing.JOptionPane.showConfirmDialog(Main.getInstance(), message, caption,
                javax.swing.JOptionPane.DEFAULT_OPTION, javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;

    }

    public static boolean updateGraphBoundaries(SizeAttributes sizeAttributes, JGraph _graph) {
        // TODO:  fail if figs bigger than page and wont fit
        return true;
    }

    public static SizeAttributes updateOrientation(int orientation, SizeAttributes sizeAttributes) {
        // TODO: flip width and height
        // then flips left/right margin for top/bottom margin
        return new SizeAttributes();
    }

}
