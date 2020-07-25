package com.ebstrada.formreturn.manager.gef.font;

import java.awt.Font;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Locale;

import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("cachedFont") public class CachedFont implements NoObfuscation {

    @XStreamAlias("name") private String name;

    private transient Font font;

    @XStreamAlias("size") private float size = 10.0f;

    @XStreamAlias("family") private String family;

    @XStreamAlias("fullFontName") private String fullFontName;

    @XStreamAlias("postScriptName") private String postScriptName;

    @XStreamAlias("filename") private String filename;

    @XStreamAlias("style") private int style;

    @XStreamAlias("fsType") private int fsType;

    @XStreamAlias("directory") private String directory;

    @XStreamAlias("copyright") private String copyright = "";

    @XStreamAlias("license") private String license = "";

    @XStreamAlias("licenseURL") private String licenseURL = "";

    @XStreamAlias("version") private String version = "";

    @XStreamAlias("localizedFamily") private String localizedFamily;

    @XStreamAlias("localizedFullFontName") private String localizedFullFontName;

    @XStreamAlias("localizedName") private String localizedName;

    @XStreamAlias("fontLocale") private FontLocalesImpl fontLocale;

    private transient static final int fsSelectionItalicBit = 0x00001;
    private transient static final int fsSelectionBoldBit = 0x00020;
    private transient static final int fsSelectionRegularBit = 0x00040;

    public CachedFont(String directory, String filename) {
        this.fontLocale = FontLocaleUtil.getFontLocale(Locale.getDefault());
        this.filename = filename;
        this.directory = directory;
        this.fsType = readFsType();
        readFontNameTableData();
        readFsSelection();
    }

    // set the font style.
    public void readFsSelection() {
        RandomAccessFile raf = null;
        int fsSelection = 0;
        try {
            raf = new RandomAccessFile(new File(this.directory + File.separator + this.filename),
                "r");
            raf.seek(0);

            while (raf.getFilePointer() < raf.length() && raf.getFilePointer() < 256) {
                // 4f 53 2f 32 - OS/2
                int b1 = raf.readUnsignedByte();
                if (b1 == 0x4f) {
                    int b2 = raf.readUnsignedByte();
                    if (b2 == 0x53) {
                        int b3 = raf.readUnsignedByte();
                        if (b3 == 0x2f) {
                            int b4 = raf.readUnsignedByte();
                            if (b4 == 0x32) {
                                // found the block, skip 4 bytes
                                raf.skipBytes(4);
                                long blockPosition = readUnsignedLong(raf);
                                if (blockPosition > 0 && blockPosition < raf.length()) {
                                    raf.seek(blockPosition);
                                    raf.skipBytes(62);
                                    fsSelection = raf.readUnsignedShort();
                                    setStyles(fsSelection);
                                } else {
                                    CachedFontManager.getStyle(getFontName()); // fallback.
                                }
                            }
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
        } catch (EOFException e) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
        } catch (IOException e) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
        }

        if (raf != null) {
            try {
                raf.close();
            } catch (IOException e) {
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
            }
        }

    }


    // fsType is the TrueType embeddable permission flag
    public int readFsType() {
        RandomAccessFile raf = null;
        int fsType = CachedFontManager.FSTYPE_RESTRICTED_LICENSE_EMBEDDING;
        try {
            raf = new RandomAccessFile(new File(this.directory + File.separator + this.filename),
                "r");
            raf.seek(0);

            // we will disregard anything that doesn't have the windows TTF header
            long signature = readUnsignedLong(raf);
            if (signature != 0x00010000) {
                return CachedFontManager.FSTYPE_RESTRICTED_LICENSE_EMBEDDING;
            }

            while (raf.getFilePointer() < raf.length() && raf.getFilePointer() < 256) {
                // 4f 53 2f 32 - OS/2
                int b1 = raf.readUnsignedByte();
                if (b1 == 0x4f) {
                    int b2 = raf.readUnsignedByte();
                    if (b2 == 0x53) {
                        int b3 = raf.readUnsignedByte();
                        if (b3 == 0x2f) {
                            int b4 = raf.readUnsignedByte();
                            if (b4 == 0x32) {
                                // found the block, skip 4 bytes
                                raf.skipBytes(4);
                                long blockPosition = readUnsignedLong(raf);
                                if (blockPosition > 0 && blockPosition < raf.length()) {
                                    raf.seek(blockPosition);
                                    raf.skipBytes(8);
                                    fsType = raf.readUnsignedShort();
                                }
                                break;
                            }
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
        } catch (EOFException e) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
        } catch (IOException e) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
        }

        if (raf != null) {
            try {
                raf.close();
            } catch (IOException e) {
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
            }
        }

        return fsType;
    }

    public long readUnsignedLong(RandomAccessFile raf) throws IOException {
        long unsignedLong = raf.readUnsignedByte();
        unsignedLong = (unsignedLong << 8) + raf.readUnsignedByte();
        unsignedLong = (unsignedLong << 8) + raf.readUnsignedByte();
        unsignedLong = (unsignedLong << 8) + raf.readUnsignedByte();
        return unsignedLong;
    }

    public boolean localeContains(int languageID) {
        for (int fontLocaleLangId : ((FontLocales) fontLocale).getTTFLanguageId()) {
            if (fontLocaleLangId == languageID) {
                return true;
            }
        }
        return false;
    }

    public void readFontNameTableData() {
        RandomAccessFile raf = null;

        try {
            raf = new RandomAccessFile(
                this.directory + System.getProperty("file.separator") + this.filename, "r");
            raf.seek(0);

            while (raf.getFilePointer() < raf.length() && raf.getFilePointer() < 512) {
                // 6e 61 6d 65 - n a m e
                int b1 = raf.readUnsignedByte();
                if (b1 == 0x6e) {
                    int b2 = raf.readUnsignedByte();
                    if (b2 == 0x61) {
                        int b3 = raf.readUnsignedByte();
                        if (b3 == 0x6d) {
                            int b4 = raf.readUnsignedByte();
                            if (b4 == 0x65) {
                                readNameTable(raf);
                            }
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
        } catch (EOFException e) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
        } catch (IOException e) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
        }

        if (raf != null) {
            try {
                raf.close();
            } catch (IOException e) {
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
            }
        }

    }

    private void readNameTable(RandomAccessFile raf) throws IOException {

        raf.skipBytes(4); // skip checksum
        long offset = readUnsignedLong(raf);
        raf.seek(offset + 2);
        long i = raf.getFilePointer();
        int count = raf.readUnsignedShort();
        long j = raf.readUnsignedShort() + i - 2;
        i += 2 * 2;

        while (count-- > 0) {
            raf.seek(i);
            final int platformID = raf.readUnsignedShort();
            final int encodingID = raf.readUnsignedShort();
            final int languageID = raf.readUnsignedShort();

            int k = raf.readUnsignedShort();
            int l = raf.readUnsignedShort();

            if (((platformID == 1 || platformID == 3) && (encodingID == 0 || encodingID == 1))) {
                raf.seek(j + raf.readUnsignedShort());
                String txt = "";
                byte[] txtBytes = new byte[l];
                raf.readFully(txtBytes);

                String encoding = "ISO-8859-1";
                for (int c : txtBytes) {
                    c &= 0xff;
                    if (c == 0 || c >= 0x80) {
                        encoding = "UTF-16BE";
                        break;
                    }
                }

                txt = new String(txtBytes, encoding);

                if (k == 0) {
                    if (this.copyright == null || languageID == 1033 || languageID == 0) {
                        setCopyright(txt);
                    }
                }
                if (k == 4) {
                    if (this.fullFontName == null || languageID == 1033 || languageID == 0) {
                        setFullFontName(txt);
                        if (this.localizedName == null) {
                            setLocalizedFullFontName(txt);
                        }
                    }
                    if (localeContains(languageID)) {
                        setLocalizedFullFontName(txt);
                    }
                }
                if (k == 5) {
                    setVersion(txt);
                }
                if (k == 6) {
                    if (this.postScriptName == null || languageID == 1033 || languageID == 0) {
                        setPostScriptName(txt);
                    }
                }
                if (k == 1) {
                    if (this.family == null || languageID == 1033 || languageID == 0) {
                        setFamily(txt);
                        if (this.localizedFamily == null) {
                            setLocalizedFamily(txt);
                        }
                    }
                    if (localeContains(languageID)) {
                        setLocalizedFamily(txt);
                    }
                }
                if (k == 13) {
                    setLicense(txt);
                }
                if (k == 14) {
                    setLicenseURL(txt);
                }

            }

            i += 6 * 2;

        }

    }

    public String getFontName() {
        return getFullFontName();
    }

    public String getFontFileName() {
        return filename;
    }

    public String getFontDirectory() {
        return directory;
    }

    public Font getFont() {
        return font;
    }

    @Override public String toString() {
        return name;
    }

    public void setSize(float fontSize) {
        this.size = fontSize;
    }

    public String getFamily() {
        return family;
    }

    public int getStyle() {
        return style;
    }

    public int getFsType() {
        return fsType;
    }

    public String getCopyright() {
        return copyright;
    }

    private void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getLicense() {
        return license;
    }

    private void setLicense(String license) {
        this.license = license;
    }

    public String getLicenseURL() {
        return licenseURL;
    }

    private void setLicenseURL(String licenseURL) {
        this.licenseURL = licenseURL;
    }

    public String getVersion() {
        return version;
    }

    private void setVersion(String version) {
        this.version = version;
    }

    private void setFamily(String family) {
        this.family = family;
    }

    public String getFullFontName() {
        if (fullFontName == null || fullFontName.trim().length() <= 0) {
            return name;
        }
        return fullFontName;
    }

    private void setFullFontName(String fullFontName) {
        this.fullFontName = fullFontName;
    }

    public String getPostScriptName() {
        return postScriptName;
    }

    public void setPostScriptName(String postScriptName) {
        this.postScriptName = postScriptName;
    }

    public String getLocalizedFamily() {
        return localizedFamily;
    }

    public void setLocalizedFamily(String localizedFamily) {
        this.localizedFamily = localizedFamily;
    }

    public String getLocalizedFullFontName() {
        return localizedFullFontName;
    }

    public void setLocalizedFullFontName(String localizedFullFontName) {
        this.localizedFullFontName = localizedFullFontName;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public void setStyles(int fsSelection) {
        int italic = fsSelection & fsSelectionItalicBit;
        int bold = fsSelection & fsSelectionBoldBit;
        int regular = fsSelection & fsSelectionRegularBit;

        // Fallback to font name determination if flags don't make sense.
        if (regular != 0 && ((italic | bold) != 0)) {
            style = CachedFontManager.getStyle(getFontName());
            return;
        } else if ((regular | italic | bold) == 0) {
            style = CachedFontManager.getStyle(getFontName());
            return;
        }

        switch (bold | italic) {
            case fsSelectionItalicBit:
                style = Font.ITALIC;
                break;
            case fsSelectionBoldBit:
                style = Font.BOLD;
                break;
            case fsSelectionBoldBit | fsSelectionItalicBit:
                style = Font.BOLD + Font.ITALIC;
                break;
            default:
                style = Font.PLAIN;
        }
    }

    public void setFont(Font font) {
        this.font = font;
        if (this.name == null) {
            this.name = font.getFontName(((FontLocales) fontLocale).getLocale());
        }
        if (this.family == null) {
            this.family = font.getFamily();
        }
    }

}
