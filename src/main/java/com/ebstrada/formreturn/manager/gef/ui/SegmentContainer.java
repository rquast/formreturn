package com.ebstrada.formreturn.manager.gef.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import com.ebstrada.formreturn.manager.persistence.xstream.Document;
import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.thoughtworks.xstream.annotations.XStreamAlias;

public class SegmentContainer implements Serializable, NoObfuscation {

    private static final long serialVersionUID = 1L;

    @XStreamAlias("segments") private ArrayList<Document> segments = new ArrayList<Document>();

    @XStreamAlias("segmentLinkIDs") private ArrayList linkIDs = new ArrayList();

    private String defaultSelectedSegment = "Random";

    private String linkFieldname = "";

    public SegmentContainer() {

    }

    public void addSegment(Document segment) {
        segments.add(segment);
    }

    public void removeSegment(Document segment) {
        if (segments.contains(segment)) {
            segments.remove(segments.indexOf(segment));
        }
    }

    public void removeSegment(int index) {
        segments.remove(index);
    }

    public int getListSize() {
        return segments.size();
    }

    public Document getSegment(int index) {
        if (segments.size() <= 0) {
            return null;
        }

        try {
            return segments.get(index);
        } catch (Exception ex) {
            return null;
        }
    }

    public int getNumberOfSegments() {
        return segments.size();
    }

    public String getLinkID(int index) {
        return (String) linkIDs.get(index);
    }

    public int getLinkID(String linkID) {

        for (int i = 0; i < linkIDs.size(); i++) {
            if (linkIDs.get(i).equals(linkID)) {
                return i;
            }
        }
        return -1;
    }

    public ArrayList getLinkIDs() {
        return linkIDs;
    }

    public void setLinkIDs(ArrayList newLinkIDs) {
        linkIDs = newLinkIDs;
    }

    public void setDefaultSegment(String defaultSelectedSegment) {
        this.defaultSelectedSegment = defaultSelectedSegment;
    }

    public String getDefaultSelectedSegment() {
        return this.defaultSelectedSegment;
    }

    public void setLinkFieldname(String linkFieldname) {
        this.linkFieldname = linkFieldname;
    }

    public String getLinkFieldname() {
        return this.linkFieldname;
    }

    public ArrayList<Document> getSegments() {
        return segments;
    }

}
