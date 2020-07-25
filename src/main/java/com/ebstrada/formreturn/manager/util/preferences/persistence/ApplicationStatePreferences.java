package com.ebstrada.formreturn.manager.util.preferences.persistence;

import java.util.Locale;

import com.ebstrada.formreturn.manager.gef.font.FontLocaleUtil;
import com.ebstrada.formreturn.manager.util.NoObfuscation;

public class ApplicationStatePreferences implements NoObfuscation {

    private int screenWidth;

    private int screenHeight;

    private int x;

    private int y;

    private boolean maximized = false;

    private boolean confirmExit = true;

    private boolean multipleInstanceCheckerEnabled = true;

    private boolean launchServerOnStartup = true;

    private boolean connectToDBOnStartup = true;

    private boolean statusBarEnabled = true;

    private boolean quickLauncherEnabled = true;

    private String locale;

    private boolean showGuide = true;

    private boolean usingSaneClient = false;

    private boolean blurIncomingImages = false;

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isMaximized() {
        return maximized;
    }

    public void setMaximized(boolean maximized) {
        this.maximized = maximized;
    }

    public boolean isConfirmExit() {
        return confirmExit;
    }

    public void setConfirmExit(boolean confirmExit) {
        this.confirmExit = confirmExit;
    }

    public boolean isMultipleInstanceCheckerEnabled() {
        return multipleInstanceCheckerEnabled;
    }

    public void setMultipleInstanceCheckerEnabled(boolean multipleInstanceCheckerEnabled) {
        this.multipleInstanceCheckerEnabled = multipleInstanceCheckerEnabled;
    }

    public boolean isLaunchServerOnStartup() {
        return launchServerOnStartup;
    }

    public void setLaunchServerOnStartup(boolean launchServerOnStartup) {
        this.launchServerOnStartup = launchServerOnStartup;
    }

    public boolean isConnectToDBOnStartup() {
        return connectToDBOnStartup;
    }

    public void setConnectToDBOnStartup(boolean connectToDBOnStartup) {
        this.connectToDBOnStartup = connectToDBOnStartup;
    }

    public boolean isStatusBarEnabled() {
        return statusBarEnabled;
    }

    public void setStatusBarEnabled(boolean statusBarEnabled) {
        this.statusBarEnabled = statusBarEnabled;
    }

    public boolean isQuickLauncherEnabled() {
        return quickLauncherEnabled;
    }

    public void setQuickLauncherEnabled(boolean quickLauncherEnabled) {
        this.quickLauncherEnabled = quickLauncherEnabled;
    }

    public String getLocale() {
        if (locale == null) {
            locale = FontLocaleUtil.getFontLocale(Locale.getDefault()).name();
        }
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public void setShowGuide(boolean showGuide) {
        this.showGuide = showGuide;
    }

    public boolean getShowGuide() {
        return this.showGuide;
    }

    public boolean isUsingSaneClient() {
        return this.usingSaneClient;
    }

    public void setUsingSaneClient(boolean usingSaneClient) {
        this.usingSaneClient = usingSaneClient;
    }

    public boolean isBlurIncomingImages() {
        return blurIncomingImages;
    }

    public void setBlurIncomingImages(boolean blurIncomingImages) {
        this.blurIncomingImages = blurIncomingImages;
    }

}
