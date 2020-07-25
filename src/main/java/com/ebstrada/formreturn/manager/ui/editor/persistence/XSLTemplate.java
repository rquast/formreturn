package com.ebstrada.formreturn.manager.ui.editor.persistence;

import java.io.File;

import com.ebstrada.formreturn.manager.persistence.jpa.PublicationXSL;
import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("xslTemplate") public class XSLTemplate implements NoObfuscation, Cloneable {

    private String guid;

    private String description = "";

    private String fileName;

    private transient File file;

    private transient long publicationXSLId = 0;

    public String getTemplateGUID() {
        return guid;
    }

    public void setGUID(String templateGUID) {
        this.guid = templateGUID;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTemplateDescription() {
        return description;
    }

    public void setDescription(String templateDescription) {
        this.description = templateDescription;
    }

    public long getPublicationXSLId() {
        return publicationXSLId;
    }

    public void setPublicationXSLId(long publicationXSLId) {
        this.publicationXSLId = publicationXSLId;
    }

    public XSLTemplate clone() {
        try {
            return (XSLTemplate) super.clone();
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

    public void restore(PublicationXSL publicationXSL) {
        setGUID(publicationXSL.getGuid());
        setFileName(publicationXSL.getFileName());
        setDescription(publicationXSL.getDescription());
        setPublicationXSLId(publicationXSL.getPublicationXSLId());
    }

}
