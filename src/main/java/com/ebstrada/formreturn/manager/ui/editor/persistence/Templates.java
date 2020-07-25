package com.ebstrada.formreturn.manager.ui.editor.persistence;

import java.util.ArrayList;

import javax.swing.DefaultListModel;

import com.ebstrada.formreturn.manager.persistence.jpa.PublicationXSL;
import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("templates") public class Templates implements NoObfuscation, Cloneable {

    private ArrayList<XSLTemplate> xslTemplates = new ArrayList<XSLTemplate>();

    public ArrayList<XSLTemplate> getXSLTemplates() {
        return xslTemplates;
    }

    public Templates clone() {
        Templates clone = null;

        try {
            clone = (Templates) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }

        ArrayList<XSLTemplate> cloneXSLTemplates = new ArrayList<XSLTemplate>();

        for (XSLTemplate xslTemplate : this.xslTemplates) {
            cloneXSLTemplates.add((XSLTemplate) xslTemplate.clone());
        }

        clone.setXSLTemplates(cloneXSLTemplates);

        return clone;
    }

    private void setXSLTemplates(ArrayList<XSLTemplate> xslTemplates) {
        this.xslTemplates = xslTemplates;
    }

    public DefaultListModel getXSLTemplatesListModel() {
        DefaultListModel dlm = new DefaultListModel();

        for (XSLTemplate xslTemplate : this.xslTemplates) {
            dlm.addElement(xslTemplate);
        }

        return dlm;
    }

    public static XSLTemplate createXSLTemplate(PublicationXSL publicationXSL) {

        XSLTemplate xslTemplate = new XSLTemplate();

        xslTemplate.setFile(null);
        xslTemplate.setFileName(publicationXSL.getFileName());
        xslTemplate.setPublicationXSLId(publicationXSL.getPublicationXSLId());
        xslTemplate.setDescription(publicationXSL.getDescription());
        xslTemplate.setGUID(publicationXSL.getGuid());

        return xslTemplate;
    }

    public void restore(PublicationXSL publicationXSL) {
        XSLTemplate xslTemplate = new XSLTemplate();
        xslTemplate.restore(publicationXSL);
        xslTemplates.add(xslTemplate);
    }

}
