package com.ebstrada.formreturn.scanner.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jms.Message;

import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.util.Misc;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

public class ICAScanner {

    public static final int COLOR_BW = 0;
    public static final int COLOR_GRAY = 1;
    public static final int COLOR_RGB = 2;

    public static ICAScanner instance;

    private String jscPath;
    private ArrayList<String> scannerNames = new ArrayList<String>();
    private HashMap<String, LinkedTreeMap> scannerSettings = new HashMap<String, LinkedTreeMap>();
    private boolean scannerBusy = false;
    private ICAListener scannerListener;
    private String selectedScanner;
    private Thread acquireThread;
    private Process process;

    private static File tempDirectory = Files.createTempDir();

    public static ICAScanner getDevice() {

        if (instance == null) {
            tempDirectory.deleteOnExit();
            instance = new ICAScanner();
        }
        return instance;

    }

    public ICAScanner() {

        String classPath = System.getProperty("java.class.path");

        String[] classPathArray = classPath.split(System.getProperty("path.separator"));
        String firstPath = "";
        if (classPathArray.length > 1) {
            firstPath = classPathArray[0];
        } else {
            firstPath = classPath;
        }

        String endsWith = "lib" + File.separator + "formreturn.jar";

        endsWith = "FormReturn.app/Contents/Resources/Java/formreturn.jar";
        if (firstPath.endsWith(endsWith)) {
            String path = firstPath.substring(0, (firstPath.length() - endsWith.length()));
            String command = "FormReturn.app/Contents/Resources/jsonscan";
            if (System.getProperty("java.vendor").toLowerCase().startsWith("apple")) {
                command = "jsonscan";
            }
            jscPath = path + command;
        } else {
            String command = "/Applications/FormReturn.app/Contents/Resources/jsonscan";
            if (System.getProperty("java.vendor").toLowerCase().startsWith("apple")) {
                command = "/Applications/FormReturn/FormReturn.app/Contents/Resources/jsonscan";
            }
            jscPath = command;
        }

    }

    public boolean isBusy() {
        return scannerBusy;
    }

    public void addListener(ICAListener scannerListener) {
        this.scannerListener = scannerListener;
    }

    public String[] getDeviceNames() {

        this.scannerNames.clear();
        this.scannerSettings.clear();

        ProcessBuilder processBuilder = new ProcessBuilder(jscPath, "-l");
        processBuilder.redirectErrorStream(false); // merge stdout and stderr
        Process process = null;

        try {

            process = processBuilder.start();
            InputStream is = process.getInputStream();
            final InputStream eis = process.getErrorStream();

            // read the errors to stderr
            Thread errorReaderThread = new Thread() {
                @Override public void run() {
                    InputStreamReader besr = new InputStreamReader(eis);
                    BufferedReader ebr = new BufferedReader(besr);
                    String errorline;
                    try {
                        while ((errorline = ebr.readLine()) != null) {
                            System.err.println(errorline);
                        }
                    } catch (IOException e) {
                    }
                }
            };
            errorReaderThread.start();

            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            String response = null;
            String line;
            Gson gson = new Gson();
            while ((line = br.readLine()) != null) {

                if (line.equals("{")) {
                    line += br.readLine();
                }

                if (line.matches("^\\{\\s*\"resp.*")) {

                    if (response != null) {
                        LinkedTreeMap message = gson.fromJson(response, LinkedTreeMap.class);
                        processResponse(message);
                    }

                    // reset response
                    response = line;

                } else {
                    response += line;
                }

            }

            if (response != null) {
                LinkedTreeMap message = gson.fromJson(response, LinkedTreeMap.class);
                processResponse(message);
            }

            process = null;

        } catch (IOException e) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
        } catch (Exception e) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
        } finally {
            if (process != null) {
                process.destroy();
            }
        }

        return scannerNames.toArray(new String[scannerNames.size()]);

    }

    private void processResponse(LinkedTreeMap response) throws Exception {

        String responseType = (String) response.get("response");

        if (responseType.equalsIgnoreCase("acquired")) {
            ICAScannerMetadata metadata = new ICAScannerMetadata();
            metadata.setResponse(response);
            this.scannerListener.update(ICAScannerMetadata.Type.ACQUIRED, metadata);
            String filename = (String) response.get("file");
            File tempImageFile = new File(filename);
            if (tempImageFile.exists() && tempImageFile.canWrite()) {
                tempImageFile.delete();
            }
        } else if (responseType.equalsIgnoreCase("status")) {
            statusUpdate(response);
        } else if (responseType.equalsIgnoreCase("found")) {
            this.scannerNames.add((String) response.get("name"));
            if (this.selectedScanner == null) {
                this.selectedScanner = this.scannerNames.get(0);
            }
        } else if (responseType.equalsIgnoreCase("settings")) {
            this.scannerSettings.put((String) response.get("name"), response);
            refreshComponents(response);
        }

    }

    private void statusUpdate(LinkedTreeMap response) {
        try {
            ICAScannerMetadata metadata = new ICAScannerMetadata();
            metadata.setResponse(response);
            metadata.setDevice(instance);
            this.scannerListener.update(ICAScannerMetadata.Type.STATECHANGE, metadata);
        } catch (Exception ex) {
            Misc.showExceptionMsg(null, ex);
        }
    }

    private void refreshComponents(LinkedTreeMap response) {
        try {
            ICAScannerMetadata metadata = new ICAScannerMetadata();
            metadata.setResponse(response);
            metadata.setDevice(instance);
            this.scannerListener.update(ICAScannerMetadata.Type.QUERY, metadata);
        } catch (Exception ex) {
            Misc.showExceptionMsg(null, ex);
        }
    }

    public void select(String deviceName) {
        this.selectedScanner = deviceName;
    }

    public void acquire() {

        final ICAScanner scanner = this;

        this.acquireThread = new Thread() {

            @Override public void run() {

                scannerBusy = true;
                ICAScannerMetadata metadata = new ICAScannerMetadata();
                metadata.setDevice(scanner);

                try {
                    scannerListener.update(ICAScannerMetadata.Type.STATECHANGE, metadata);

                    scannerListener.update(ICAScannerMetadata.Type.ACQUIRE, metadata);

                    LinkedTreeMap settings = scannerListener.getSettings();
                    settings.put("base-directory", tempDirectory.getAbsolutePath());
                    Gson gson = new Gson();

                    ProcessBuilder processBuilder = new ProcessBuilder(jscPath, "-c");
                    processBuilder.redirectErrorStream(false); // merge stdout
                    // and stderr

                    try {

                        process = processBuilder.start();
                        InputStream is = process.getInputStream();
                        final InputStream eis = process.getErrorStream();

                        // read the errors to stderr
                        Thread errorReaderThread = new Thread() {
                            @Override public void run() {
                                InputStreamReader besr = new InputStreamReader(eis);
                                BufferedReader ebr = new BufferedReader(besr);
                                String errorline;
                                try {
                                    while ((errorline = ebr.readLine()) != null) {
                                        System.err.println(errorline);
                                    }
                                } catch (IOException e) {
                                }
                            }
                        };
                        errorReaderThread.start();

                        OutputStream os = process.getOutputStream();
                        OutputStreamWriter osw = new OutputStreamWriter(os);

                        InputStreamReader isr = new InputStreamReader(is);
                        BufferedReader br = new BufferedReader(isr);
                        BufferedWriter bw = new BufferedWriter(osw);

                        bw.write(gson.toJson(settings));
                        bw.close();

                        String response = null;
                        String line;

                        while ((line = br.readLine()) != null) {

                            if (line.equals("{")) {
                                line += br.readLine();
                            }

                            if (line.matches("^\\{\\s*\"resp.*")) {

                                if (response != null) {
                                    LinkedTreeMap message =
                                        gson.fromJson(response, LinkedTreeMap.class);
                                    processResponse(message);
                                }

                                // reset response
                                response = line;

                            } else {
                                response += line;
                            }

                        }

                        if (response != null) {
                            LinkedTreeMap message = gson.fromJson(response, LinkedTreeMap.class);
                            processResponse(message);
                        }

                        process = null;

                    } catch (IOException e) {
                        com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
                    } catch (Exception e) {
                        com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
                    } finally {
                        if (process != null) {
                            process.destroy();
                        }
                    }

                } catch (Exception ex) {

                    Misc.printStackTrace(ex);
                    Misc.showExceptionMsg(null, ex);

                } finally {

                    scannerBusy = false;
                    metadata.setResponse(null);
                    try {
                        scannerListener.update(ICAScannerMetadata.Type.STATECHANGE, metadata);
                    } catch (Exception ex) {
                        Misc.showExceptionMsg(null, ex);
                    }

                }

            }

        };

        this.acquireThread.start();

    }

    public void setCancel(boolean b) {
        if (this.process != null && this.process.isAlive()) {
            this.process.destroy();
        }
    }

    public void close() {
        // no need to close jsonscan
    }

    public HashMap<String, LinkedTreeMap> getScannerSettings() {
        return this.scannerSettings;
    }

    public String getSelectedScanner() {
        return this.selectedScanner;
    }

}
