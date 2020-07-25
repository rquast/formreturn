package com.ebstrada.formreturn.manager.ui.editor.persistence;

import java.io.File;

import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("jarPlugin") public class JARPlugin implements NoObfuscation, Cloneable {

    private String guid;

    private String description = "";

    private String fileName;

    private transient File file;

    private transient long publicationJARId = 0;

    private transient byte[] jarData;

    public String getPluginGUID() {
        return guid;
    }

    public void setGUID(String pluginGUID) {
        this.guid = pluginGUID;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPublicationJARId() {
        return publicationJARId;
    }

    public void setPublicationJARId(long publicationJARId) {
        this.publicationJARId = publicationJARId;
    }

    public JARPlugin clone() {
        try {
            return (JARPlugin) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String toString() {
        return this.description.trim() + " (" + this.fileName + ")";
    }

    public void setJarData(byte[] jarData) {
        this.jarData = jarData;
    }

    public byte[] getJarData() {
        return jarData;
    }

}
