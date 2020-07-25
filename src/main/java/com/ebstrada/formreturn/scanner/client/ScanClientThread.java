package com.ebstrada.formreturn.scanner.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

import javax.swing.JFrame;

import au.com.southsky.jfreesane.SanePasswordProvider;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;
import com.ebstrada.formreturn.manager.util.preferences.persistence.ApplicationStatePreferences;
import com.swingsane.business.auth.SwingSanePasswordProvider;
import com.swingsane.business.scanning.IScanService;
import com.swingsane.business.scanning.ScanServiceImpl;
import com.swingsane.preferences.IPreferredDefaults;
import com.swingsane.preferences.PreferredDefaultsImpl;

public class ScanClientThread extends Thread {

    public static boolean LINUX = (System.getProperty("os.name").toLowerCase().startsWith("linux"));

    public static boolean MAC_OS_X =
        (System.getProperty("os.name").toLowerCase().startsWith("mac os x"));

    public static boolean WINDOWS =
        (System.getProperty("os.name").toLowerCase().startsWith("windows"));

    private JFrame scf;

    private SanePasswordProvider getPasswordProvider() {
        return new SwingSanePasswordProvider(
            PreferencesManager.getSwingSanePreferences().getApplicationPreferences()
                .getSaneLogins());
    }

    private IPreferredDefaults getPreferredDefaults() {
        return new PreferredDefaultsImpl();
    }

    private IScanService getScanService() {
        IScanService scanService = new ScanServiceImpl();
        scanService.setPasswordProvider(getPasswordProvider());
        scanService.setSaneServiceIdentity(
            PreferencesManager.getSwingSanePreferences().getApplicationPreferences()
                .getSaneServiceIdentity());
        return scanService;
    }

    public boolean isScannerFrameActive() {
        if (scf == null) {
            return false;
        }
        if (scf.isVisible()) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override public void run() {
                    scf.toFront();
                    scf.repaint();
                }
            });
        }
        return scf.isVisible();
    }

    @Override public void run() {

        ApplicationStatePreferences applicationState = PreferencesManager.getApplicationState();

        if (WINDOWS && !(applicationState.isUsingSaneClient())) {

            try {
                scf = new ScannerClientFrame(new TwainPanel());
                scf.setTitle(Localizer.localize("UI", "FormReturnScannerFrameTitle"));
                scf.setVisible(true);
            } catch (Exception e) {

                Misc.showErrorMsg(Main.getInstance().getRootPane(), e.getLocalizedMessage());

                Misc.printStackTrace(e);

                if (scf != null) {
                    scf.dispose();
                    scf = null;
                }
            }

            return;

        } else if (MAC_OS_X && !(applicationState.isUsingSaneClient())) {

            try {
                scf = new ScannerClientFrame(new ICAPanel());
                scf.setTitle(Localizer.localize("UI", "FormReturnScannerFrameTitle"));
                scf.setVisible(true);
            } catch (Exception e) {

                Misc.showErrorMsg(Main.getInstance().getRootPane(), e.getLocalizedMessage());

                Misc.printStackTrace(e);

                if (scf != null) {
                    scf.dispose();
                    scf = null;
                }
            }

            return;

        } else {

            try {
                SwingSaneFrame swingSaneFrame =
                    new SwingSaneFrame(Main.getInstance().getRootPane().getTopLevelAncestor());
                swingSaneFrame
                    .setApplicationName(Localizer.localize("UI", "FormReturnScannerFrameTitle"));
                swingSaneFrame.setXstream(Main.getXstream());
                swingSaneFrame.setPreferences(PreferencesManager.getSwingSanePreferences());
                swingSaneFrame.setScanService(getScanService());
                swingSaneFrame.setPreferredDefaults(getPreferredDefaults());
                swingSaneFrame.initialize();
                swingSaneFrame.setVisible(true);
            } catch (Exception e) {

                Misc.showErrorMsg(Main.getInstance().getRootPane(), e.getLocalizedMessage());

                Misc.printStackTrace(e);

                if (scf != null) {
                    scf.dispose();
                    scf = null;
                }
            }

            return;

        }



    }
}
