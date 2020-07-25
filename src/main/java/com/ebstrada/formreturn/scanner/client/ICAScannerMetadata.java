package com.ebstrada.formreturn.scanner.client;

import java.awt.image.BufferedImage;
import java.io.File;

import com.ebstrada.formreturn.manager.util.image.ImageUtil;
import com.google.gson.internal.LinkedTreeMap;

public class ICAScannerMetadata {

    public enum Type {
        ACQUIRED, ACQUIRE, QUERY, STATECHANGE
    }


    private LinkedTreeMap response;
    private ICAScanner scanner;

    public BufferedImage getImage() throws Exception {
        String filename = (String) response.get("file");
        File file = new File(filename);
        if (file.exists()) {
            return ImageUtil.readImage(file, 1);
        } else {
            throw new Exception("File not found: " + filename);
        }
    }

    public ICAScanner getDevice() {
        return scanner;
    }

    public boolean isFinished() {
        return scanner.isBusy();
    }

    public void setResponse(LinkedTreeMap response) {
        this.response = response;
    }

    public LinkedTreeMap getResponse() {
        return this.response;
    }

    public void setDevice(ICAScanner scanner) {
        this.scanner = scanner;
    }

}
