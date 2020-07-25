package com.ebstrada.formreturn.manager.ui.editor.persistence;

import java.util.ArrayList;

import javax.swing.DefaultListModel;

import com.ebstrada.formreturn.manager.persistence.jpa.PublicationJAR;
import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("plugins") public class Plugins implements NoObfuscation, Cloneable {

    private ArrayList<JARPlugin> jarPlugins = new ArrayList<JARPlugin>();

    public ArrayList<JARPlugin> getJARPlugins() {
        return jarPlugins;
    }

    public Plugins clone() {
        Plugins clone = null;

        try {
            clone = (Plugins) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }

        ArrayList<JARPlugin> cloneJARPlugins = new ArrayList<JARPlugin>();

        for (JARPlugin jarPlugin : this.jarPlugins) {
            cloneJARPlugins.add((JARPlugin) jarPlugin.clone());
        }

        clone.setJARPlugins(cloneJARPlugins);

        return clone;
    }

    private void setJARPlugins(ArrayList<JARPlugin> jarPlugins) {
        this.jarPlugins = jarPlugins;
    }

    public DefaultListModel getJARPluginsListModel() {
        DefaultListModel dlm = new DefaultListModel();

        for (JARPlugin jarPlugin : this.jarPlugins) {
            dlm.addElement(jarPlugin);
        }

        return dlm;
    }

    public static JARPlugin createJARPlugin(PublicationJAR publicationJAR) {

        JARPlugin jarPlugin = new JARPlugin();

        jarPlugin.setFile(null);
        jarPlugin.setFileName(publicationJAR.getFileName());
        jarPlugin.setPublicationJARId(publicationJAR.getPublicationJARId());
        jarPlugin.setDescription(publicationJAR.getDescription());
        jarPlugin.setGUID(publicationJAR.getGuid());
        jarPlugin.setJarData(publicationJAR.getJarData());

        return jarPlugin;
    }

}
