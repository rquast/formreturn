package com.ebstrada.formreturn.manager.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.codabar.CodabarBean;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.code128.EAN128Bean;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.impl.datamatrix.DataMatrixBean;
import org.krysalis.barcode4j.impl.fourstate.RoyalMailCBCBean;
import org.krysalis.barcode4j.impl.fourstate.USPSIntelligentMail;
import org.krysalis.barcode4j.impl.fourstate.USPSIntelligentMailBean;
import org.krysalis.barcode4j.impl.int2of5.Interleaved2Of5Bean;
import org.krysalis.barcode4j.impl.pdf417.PDF417Bean;
import org.krysalis.barcode4j.impl.postnet.POSTNETBean;
import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.impl.upcean.EAN8Bean;
import org.krysalis.barcode4j.impl.upcean.UPCABean;
import org.krysalis.barcode4j.impl.upcean.UPCEBean;
import org.krysalis.barcode4j.tools.UnitConv;

import com.ebstrada.formreturn.manager.ui.Main;

public class BarcodeCreator {

    public static final double FUDGE_SCALAR = 12.0d;

    private static List<String[]> barcodeTypes = new ArrayList<String[]>();

    public BarcodeCreator() {
    }

    public static List<String> getBarcodeTypes() {
        if (barcodeTypes.size() <= 0) {
            resetBarcodeTypes();
        }

        List<String> descriptions = new ArrayList<String>();

        for (String[] typePair : barcodeTypes) {
            descriptions.add(typePair[0]);
        }
        return descriptions;
    }

    public static String getBarcodeIdentifier(String name) {
        if (barcodeTypes.size() <= 0) {
            resetBarcodeTypes();
        }
        for (String[] typePair : barcodeTypes) {
            if (typePair[0].equals(name)) {
                return typePair[1];
            }
        }
        return name;
    }

    public static void resetBarcodeTypes() {
        barcodeTypes = new ArrayList<String[]>();
        barcodeTypes.add(new String[] {"Form ID", "Form ID"});
        barcodeTypes.add(new String[] {"Code 128", "Code128"});
        barcodeTypes.add(new String[] {"Code 39", "Code39"});
        barcodeTypes.add(new String[] {"EAN-8", "EAN8"});
        barcodeTypes.add(new String[] {"EAN-13", "EAN13"});
        barcodeTypes.add(new String[] {"EAN-128", "EAN128"});
        barcodeTypes.add(new String[] {"USPS (US Postal Service)", "USPS"});
        barcodeTypes.add(new String[] {"POSTNET", "POSTNET"});
        barcodeTypes.add(new String[] {"Royal Mail CBC", "ROYALMAIL"});
        barcodeTypes.add(new String[] {"UPC-A", "UPCA"});
        barcodeTypes.add(new String[] {"UPC-E", "UPCE"});
        barcodeTypes.add(new String[] {"Interleaved 2 of 5", "Int2of5"});
        barcodeTypes.add(new String[] {"Codabar", "Codabar"});
        barcodeTypes.add(new String[] {"Data Matrix", "DataMatrix"});
        barcodeTypes.add(new String[] {"PDF417", "PDF417"});
    }

    public static BarcodeGenerator getBarcodeGenerator(String barcodeType,
        String renderableBarcodeValue, double scalar, boolean quietZone, boolean showText,
        int barcodeHeight) {

        BarcodeGenerator bargen = null;

        if (barcodeType.toUpperCase().startsWith("CODE 128") || barcodeType.toUpperCase()
            .startsWith("CODE128") || barcodeType.toUpperCase().startsWith("FORM ID")) {
            bargen =
                getCode128Bean(renderableBarcodeValue, scalar, quietZone, showText, barcodeHeight);
        } else if (barcodeType.equalsIgnoreCase("CODE39") || barcodeType
            .equalsIgnoreCase("CODE 39")) {
            bargen =
                getCode39Bean(renderableBarcodeValue, scalar, quietZone, showText, barcodeHeight);
        } else if (barcodeType.equalsIgnoreCase("EAN8") || barcodeType.equalsIgnoreCase("EAN-8")) {
            bargen =
                getEAN8Bean(renderableBarcodeValue, scalar, quietZone, showText, barcodeHeight);
        } else if (barcodeType.equalsIgnoreCase("EAN13") || barcodeType
            .equalsIgnoreCase("EAN-13")) {
            bargen =
                getEAN13Bean(renderableBarcodeValue, scalar, quietZone, showText, barcodeHeight);
        } else if (barcodeType.equalsIgnoreCase("EAN128") || barcodeType
            .equalsIgnoreCase("EAN-128")) {
            bargen =
                getEAN128Bean(renderableBarcodeValue, scalar, quietZone, showText, barcodeHeight);
        } else if (barcodeType.equalsIgnoreCase("UPCA") || barcodeType.toUpperCase()
            .endsWith("UPC-A")) {
            bargen =
                getUPCABean(renderableBarcodeValue, scalar, quietZone, showText, barcodeHeight);
        } else if (barcodeType.equalsIgnoreCase("UPCE") || barcodeType.equalsIgnoreCase("UPC-E")) {
            bargen =
                getUPCEBean(renderableBarcodeValue, scalar, quietZone, showText, barcodeHeight);
        } else if (barcodeType.toUpperCase().startsWith("USPS")) {
            bargen =
                getUSPSBean(renderableBarcodeValue, scalar, quietZone, showText, barcodeHeight);
        } else if (barcodeType.equalsIgnoreCase("ROYALMAIL") || barcodeType
            .equalsIgnoreCase("ROYAL MAIL")) {
            bargen = getRoyalMailBean(renderableBarcodeValue, scalar, quietZone, showText,
                barcodeHeight);
        } else if (barcodeType.equalsIgnoreCase("POSTNET")) {
            bargen =
                getPOSTNETBean(renderableBarcodeValue, scalar, quietZone, showText, barcodeHeight);
        } else if (barcodeType.equalsIgnoreCase("DATA MATRIX") || barcodeType
            .equalsIgnoreCase("DATAMATRIX")) {
            bargen = getDataMatrixBean(renderableBarcodeValue, scalar, quietZone, showText,
                barcodeHeight);
        } else if (barcodeType.equalsIgnoreCase("PDF417")) {
            bargen =
                getPDF417Bean(renderableBarcodeValue, scalar, quietZone, showText, barcodeHeight);
        } else if (barcodeType.equalsIgnoreCase("STD2OF5") || barcodeType.toUpperCase()
            .endsWith("2 of 5")) {
            bargen =
                get2of5Bean(renderableBarcodeValue, scalar, quietZone, showText, barcodeHeight);
        } else if (barcodeType.equalsIgnoreCase("INT2OF5")) {
            bargen =
                get2of5Bean(renderableBarcodeValue, scalar, quietZone, showText, barcodeHeight);
        } else if (barcodeType.equalsIgnoreCase("2OF7") || barcodeType.toUpperCase()
            .equals("CODABAR") || barcodeType.toUpperCase().endsWith("(CODABAR)")) {
            bargen =
                getCodabarBean(renderableBarcodeValue, scalar, quietZone, showText, barcodeHeight);
        }

        return bargen;
    }

    public static Code128Bean getCode128Bean(String renderableBarcodeValue, double scalar,
        boolean quietZone, boolean showText, int barcodeHeight) {

        Code128Bean bean = new Code128Bean();

        bean.doQuietZone(quietZone);

        bean.setModuleWidth(UnitConv.pt2mm(6.0d * scalar));
        bean.setQuietZone(bean.getModuleWidth() * 6.0d);

        bean.setFontName(Main.getCachedFontManager().getDefaultFont().getFontName());
        bean.setFontSize(9.0d);

        if (barcodeHeight > 0) {
            bean.setBarHeight(UnitConv.pt2mm(barcodeHeight * scalar));
        } else {
            bean.setBarHeight(UnitConv.pt2mm(50 * scalar));
        }

        if (!showText) {
            bean.setMsgPosition(HumanReadablePlacement.HRP_NONE);
        }

        return bean;

    }

    private static BarcodeGenerator getCodabarBean(String renderableBarcodeValue, double scalar,
        boolean quietZone, boolean showText, int barcodeHeight) {
        CodabarBean bean = new CodabarBean();

        bean.doQuietZone(quietZone);

        bean.setModuleWidth(bean.getModuleWidth() * scalar * 4.0d);

        bean.setFontName(Main.getCachedFontManager().getDefaultFont().getFontName());
        bean.setFontSize(12.0d);

        if (barcodeHeight > 0) {
            bean.setBarHeight(UnitConv.pt2mm(barcodeHeight * scalar * 4.0d));
        } else {
            bean.setBarHeight(bean.getBarHeight() * 4.0d);
        }

        if (!showText) {
            bean.setMsgPosition(HumanReadablePlacement.HRP_NONE);
        }

        return bean;
    }

    private static BarcodeGenerator get2of5Bean(String renderableBarcodeValue, double scalar,
        boolean quietZone, boolean showText, int barcodeHeight) {
        Interleaved2Of5Bean bean = new Interleaved2Of5Bean();

        bean.doQuietZone(quietZone);

        bean.setModuleWidth(bean.getModuleWidth() * scalar * 4.0d);

        bean.setFontName(Main.getCachedFontManager().getDefaultFont().getFontName());
        bean.setFontSize(12.0d);

        if (barcodeHeight > 0) {
            bean.setBarHeight(UnitConv.pt2mm(barcodeHeight * scalar * 4.0d));
        } else {
            bean.setBarHeight(bean.getBarHeight() * 4.0d);
        }

        if (!showText) {
            bean.setMsgPosition(HumanReadablePlacement.HRP_NONE);
        }

        return bean;
    }

    private static BarcodeGenerator getPDF417Bean(String renderableBarcodeValue, double scalar,
        boolean quietZone, boolean showText, int barcodeHeight) {
        PDF417Bean bean = new PDF417Bean();

        bean.doQuietZone(quietZone);

        bean.setModuleWidth(bean.getModuleWidth() * scalar * 4.0d);

        bean.setFontName(Main.getCachedFontManager().getDefaultFont().getFontName());
        bean.setFontSize(12.0d);

        if (barcodeHeight > 0) {
            bean.setBarHeight(UnitConv.pt2mm(barcodeHeight * scalar * 4.0d));
        } else {
            bean.setBarHeight(bean.getBarHeight() * 4.0d);
        }

        if (!showText) {
            bean.setMsgPosition(HumanReadablePlacement.HRP_NONE);
        }

        return bean;
    }

    private static BarcodeGenerator getDataMatrixBean(String renderableBarcodeValue, double scalar,
        boolean quietZone, boolean showText, int barcodeHeight) {

        DataMatrixBean bean = new DataMatrixBean();

        bean.setModuleWidth(bean.getModuleWidth() * scalar * FUDGE_SCALAR);

        return bean;

    }

    private static BarcodeGenerator getPOSTNETBean(String renderableBarcodeValue, double scalar,
        boolean quietZone, boolean showText, int barcodeHeight) {
        POSTNETBean bean = new POSTNETBean();

        bean.doQuietZone(quietZone);

        bean.setModuleWidth(bean.getModuleWidth() * scalar * 4.0d);

        bean.setFontName(Main.getCachedFontManager().getDefaultFont().getFontName());
        bean.setFontSize(12.0d);

        if (barcodeHeight > 0) {
            bean.setBarHeight(UnitConv.pt2mm(barcodeHeight * scalar * 4.0d));
        } else {
            bean.setBarHeight(bean.getBarHeight() * 4.0d);
        }

        if (!showText) {
            bean.setMsgPosition(HumanReadablePlacement.HRP_NONE);
        }

        return bean;
    }

    private static BarcodeGenerator getRoyalMailBean(String renderableBarcodeValue, double scalar,
        boolean quietZone, boolean showText, int barcodeHeight) {
        RoyalMailCBCBean bean = new RoyalMailCBCBean();

        bean.doQuietZone(quietZone);

        bean.setModuleWidth(bean.getModuleWidth() * scalar * 4.0d);

        bean.setFontName(Main.getCachedFontManager().getDefaultFont().getFontName());
        bean.setFontSize(12.0d);

        if (barcodeHeight > 0) {
            bean.setBarHeight(UnitConv.pt2mm(barcodeHeight * scalar * 4.0d));
        } else {
            bean.setBarHeight(bean.getBarHeight() * 4.0d);
        }

        if (!showText) {
            bean.setMsgPosition(HumanReadablePlacement.HRP_NONE);
        }

        return bean;
    }

    private static BarcodeGenerator getUSPSBean(String renderableBarcodeValue, double scalar,
        boolean quietZone, boolean showText, int barcodeHeight) {
        USPSIntelligentMailBean bean = new USPSIntelligentMailBean();

        bean.doQuietZone(quietZone);

        bean.setModuleWidth(bean.getModuleWidth() * scalar * 4.0d);

        bean.setFontName(Main.getCachedFontManager().getDefaultFont().getFontName());
        bean.setFontSize(12.0d);

        if (barcodeHeight > 0) {
            bean.setBarHeight(UnitConv.pt2mm(barcodeHeight * scalar * 4.0d));
        } else {
            bean.setBarHeight(bean.getBarHeight() * 4.0d);
        }

        if (!showText) {
            bean.setMsgPosition(HumanReadablePlacement.HRP_NONE);
        }

        return bean;
    }

    private static BarcodeGenerator getUPCEBean(String renderableBarcodeValue, double scalar,
        boolean quietZone, boolean showText, int barcodeHeight) {
        UPCEBean bean = new UPCEBean();

        bean.doQuietZone(quietZone);

        bean.setModuleWidth(bean.getModuleWidth() * scalar * 4.0d);

        bean.setFontName(Main.getCachedFontManager().getDefaultFont().getFontName());
        bean.setFontSize(12.0d);

        if (barcodeHeight > 0) {
            bean.setBarHeight(UnitConv.pt2mm(barcodeHeight * scalar * 4.0d));
        } else {
            bean.setBarHeight(bean.getBarHeight() * 4.0d);
        }

        if (!showText) {
            bean.setMsgPosition(HumanReadablePlacement.HRP_NONE);
        }

        return bean;
    }

    private static BarcodeGenerator getUPCABean(String renderableBarcodeValue, double scalar,
        boolean quietZone, boolean showText, int barcodeHeight) {
        UPCABean bean = new UPCABean();

        bean.doQuietZone(quietZone);

        bean.setModuleWidth(bean.getModuleWidth() * scalar * 4.0d);

        bean.setFontName(Main.getCachedFontManager().getDefaultFont().getFontName());
        bean.setFontSize(12.0d);

        if (barcodeHeight > 0) {
            bean.setBarHeight(UnitConv.pt2mm(barcodeHeight * scalar * 4.0d));
        } else {
            bean.setBarHeight(bean.getBarHeight() * 4.0d);
        }

        if (!showText) {
            bean.setMsgPosition(HumanReadablePlacement.HRP_NONE);
        }

        return bean;
    }

    private static BarcodeGenerator getEAN128Bean(String renderableBarcodeValue, double scalar,
        boolean quietZone, boolean showText, int barcodeHeight) {
        EAN128Bean bean = new EAN128Bean();

        bean.doQuietZone(quietZone);

        bean.setModuleWidth(bean.getModuleWidth() * scalar * 4.0d);

        bean.setFontName(Main.getCachedFontManager().getDefaultFont().getFontName());
        bean.setFontSize(12.0d);

        if (barcodeHeight > 0) {
            bean.setBarHeight(UnitConv.pt2mm(barcodeHeight * scalar * 4.0d));
        } else {
            bean.setBarHeight(bean.getBarHeight() * 4.0d);
        }

        if (!showText) {
            bean.setMsgPosition(HumanReadablePlacement.HRP_NONE);
        }

        return bean;
    }

    private static BarcodeGenerator getEAN13Bean(String renderableBarcodeValue, double scalar,
        boolean quietZone, boolean showText, int barcodeHeight) {

        EAN13Bean bean = new EAN13Bean();

        bean.doQuietZone(quietZone);

        bean.setModuleWidth(bean.getModuleWidth() * scalar * 4.0d);

        bean.setFontName(Main.getCachedFontManager().getDefaultFont().getFontName());
        bean.setFontSize(12.0d);

        if (barcodeHeight > 0) {
            bean.setBarHeight(UnitConv.pt2mm(barcodeHeight * scalar * 4.0d));
        } else {
            bean.setBarHeight(80.0d);
        }

        if (!showText) {
            bean.setMsgPosition(HumanReadablePlacement.HRP_NONE);
        }

        return bean;
    }

    private static BarcodeGenerator getEAN8Bean(String renderableBarcodeValue, double scalar,
        boolean quietZone, boolean showText, int barcodeHeight) {
        EAN8Bean bean = new EAN8Bean();

        bean.doQuietZone(quietZone);

        bean.setModuleWidth(bean.getModuleWidth() * scalar * 4.0d);

        bean.setFontName(Main.getCachedFontManager().getDefaultFont().getFontName());
        bean.setFontSize(12.0d);

        if (barcodeHeight > 0) {
            bean.setBarHeight(UnitConv.pt2mm(barcodeHeight * scalar * 4.0d));
        } else {
            bean.setBarHeight(80.0d);
        }

        if (!showText) {
            bean.setMsgPosition(HumanReadablePlacement.HRP_NONE);
        }

        return bean;
    }

    private static BarcodeGenerator getCode39Bean(String renderableBarcodeValue, double scalar,
        boolean quietZone, boolean showText, int barcodeHeight) {

        Code39Bean bean = new Code39Bean();

        bean.doQuietZone(quietZone);

        bean.setModuleWidth(bean.getModuleWidth() * scalar * FUDGE_SCALAR);

        // unique to Code39
        if (bean.getModuleWidth() >= 10.0d) {
            bean.setIntercharGapWidth(bean.getModuleWidth() * 3.0d);
        } else {
            bean.setIntercharGapWidth(bean.getModuleWidth());
        }
        bean.setChecksumMode(ChecksumMode.CP_AUTO);
        bean.setExtendedCharSetEnabled(true);
        bean.setDisplayChecksum(true);
        bean.setDisplayStartStop(true);

        bean.setFontName(Main.getCachedFontManager().getDefaultFont().getFontName());
        bean.setFontSize(10.0d);

        if (barcodeHeight > 0) {
            bean.setBarHeight(UnitConv.pt2mm(barcodeHeight * scalar));
        }

        if (!showText) {
            bean.setMsgPosition(HumanReadablePlacement.HRP_NONE);
        }

        return bean;

    }

}
