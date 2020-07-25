package com.ebstrada.formreturn.scanner.client;

import java.awt.image.BufferedImage;

public interface ScannerPanel {

    public void init() throws Exception;

    public boolean close();

    public void focusGained();

    public void setScannerClientDialog(ScannerClientDialog scannerClientDialog);

    public BufferedImage getPreviewImage();

}
