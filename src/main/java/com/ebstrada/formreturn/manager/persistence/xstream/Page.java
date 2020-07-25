package com.ebstrada.formreturn.manager.persistence.xstream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.ui.PageAttributes;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.thoughtworks.xstream.annotations.XStreamAlias;


@XStreamAlias("page") public class Page implements NoObfuscation {

    @XStreamAlias("version") private String version = Main.VERSION;

    @XStreamAlias("attributes") private PageAttributes pageAttributes;

    @XStreamAlias("figs") private ArrayList<Fig> figs;

    public PageAttributes getPageAttributes() {
        return pageAttributes;
    }

    public void setPageAttributes(PageAttributes pageAttributes) {
        this.pageAttributes = pageAttributes;
    }

    public ArrayList<Fig> getFigs() {
        return figs;
    }

    public void setFigs(List<Fig> figs) {

        this.figs = new ArrayList<Fig>();
        for (Iterator<Fig> it = figs.iterator(); it.hasNext(); ) {
            this.figs.add((Fig) it.next());
        }

    }

    public void removeFig(Fig fig) {
        if (this.figs != null && this.figs.contains(fig)) {
            this.figs.remove(fig);
        }
    }

}
