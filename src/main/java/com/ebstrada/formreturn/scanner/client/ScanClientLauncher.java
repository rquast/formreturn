package com.ebstrada.formreturn.scanner.client;

public class ScanClientLauncher {

    private ScanClientThread sct;

    public ScanClientLauncher() {
        if (sct == null) {
            sct = new ScanClientThread();
            sct.start();
        }
    }

    public void start() {
        sct.start();
    }

    public boolean isRunning() {
        if (sct == null) {
            return false;
        }
        return sct.isAlive() || sct.isScannerFrameActive();
    }

    public void clearScanClientFrame() {
        sct = null;
        System.gc();
    }

}
