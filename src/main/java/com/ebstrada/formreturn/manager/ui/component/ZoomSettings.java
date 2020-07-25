package com.ebstrada.formreturn.manager.ui.component;

public class ZoomSettings {

    private boolean zoomToFit = true;

    private String zoomLevel = "10%";

    public boolean isZoomToFit() {
        return zoomToFit;
    }

    public void setZoomToFit(boolean zoomToFit) {
        this.zoomToFit = zoomToFit;
    }

    public String getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(String zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

}
