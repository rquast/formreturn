package com.ebstrada.formreturn.scanner.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;

import javax.persistence.EntityManager;

import com.ebstrada.formreturn.manager.persistence.jpa.IncomingImage;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.RandomGUID;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;

public class ScanClientController {

    /*
     * RECEIVE PROTOCOL:
     *
     * (TOKEN): IMG: FILENAME
     * (TOKEN): SETTINGS_ACK
     *
     * SEND PROTOCOL:
     *
     * (TOKEN): IMG_ACK
     * (TOKEN): SETTINGS: json data
     *
     */


    private interface Actions {
        public void performAction(String token, String data) throws Exception;
    }


    private static HashMap<String, String> requests;

    private BufferedReader br;
    private static BufferedWriter bw;


    private enum ReceiveActions implements Actions {
        IMG {
            public void performAction(String token, String data) {

                File imageFile = new File(data);

                if (!(imageFile.exists()) || !(imageFile.canRead())) {
                    // try {
                    // sendImgNAck(token);
                    // } catch (IOException e) {
                    // }
                    return;
                }

                EntityManager entityManager = null;
                if (entityManager == null) {
                    // TODO: pick the right entity manager... either server or the other one.
                    entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();
                }

                try {

                    entityManager.getTransaction().begin();
                    if (imageFile != null && imageFile.exists() && !(imageFile.isDirectory())) {
                        byte[] imageData = Misc.getBytesFromFile(imageFile);
                        if (entityManager != null) {
                            IncomingImage incomingImage = new IncomingImage();
                            incomingImage.setCaptureTime(new Timestamp(System.currentTimeMillis()));
                            incomingImage.setIncomingImageData(imageData);
                            incomingImage.setIncomingImageName(imageFile.getName());
                            incomingImage.setNumberOfPages(1);
                            incomingImage.setMatchStatus((short) 0);
                            entityManager.persist(incomingImage);
                        }
                    }
                    entityManager.getTransaction().commit();
                    entityManager.close();

                    entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();
                    if (entityManager != null) {
                        entityManager.getTransaction().begin();
                        entityManager.flush();
                        entityManager.createNativeQuery("CALL CHECK_INCOMING_IMAGES()")
                            .executeUpdate();
                        entityManager.getTransaction().commit();
                        entityManager.close();
                    }
                    removeImage(imageFile);
                    // sendImgAck(token);
                } catch (Exception ex) {
                    // try {
                    // sendImgNAck(token);
                    // } catch (IOException e) {
                    // }
                } finally {
                    if (entityManager != null) {
                        if (entityManager.isOpen() && entityManager.getTransaction().isActive()) {
                            try {
                                entityManager.getTransaction().rollback();
                            } catch (Exception ex) {
                            }
                        }
                        if (entityManager.isOpen()) {
                            entityManager.close();
                        }
                    }
                }

            }

        }, SETTINGS {
            public void performAction(String token, String data) throws IOException {
                try {
                    updateSettings(data);
                    // sendSettingsAck(token);
                } catch (Exception ex) {
                    // sendSettingsNAck(token);
                }
            }
        }, SETTINGS_ACK {
            public void performAction(String token, String data) {
                settingsAck(token);
            }
        }, SETTINGS_NACK {
            public void performAction(String token, String data) throws Exception {
                // TODO: throw an exception.. can't set the settings

                // TODO: MAKE THIS A SCANNER CLIENT EXCEPTION!
                throw new Exception("Cannot set scanner client settings");

            }
        }
    }


    ;

    public ScanClientController(BufferedReader br, BufferedWriter bw) throws Exception {
        this.br = br;
        ScanClientController.bw = bw;
        ScanClientController.requests = new HashMap<String, String>();
        // sendSettings();
        processRequests();
        // nullify the static vars so there's no memory leak
        ScanClientController.bw = null;
        ScanClientController.requests = null;
    }

    private static void removeImage(File imageFile) {
        try {
            if (imageFile.getCanonicalPath()
                .startsWith(PreferencesManager.getHomeDirectoryPath())) {
                imageFile.delete();
            }
        } catch (IOException e) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
        }
    }

    private static void sendLine(String line) throws IOException {
        bw.write(line + "\n");
    }

    private void sendSettings() throws IOException {
        String data = getSettingsJSONData();
        String token = getToken();

        sendLine(token + ": SETTINGS: " + data);
        requests.put(token, "SETTINGS");
    }

    private static void sendSettingsAck(String token) throws IOException {
        sendLine(token + ": SETTINGS_ACK");
    }

    private static void sendSettingsNAck(String token) throws IOException {
        sendLine(token + ": SETTINGS_NACK");
    }

    private static void settingsAck(String token) {
        requests.remove(token);
    }

    private static void sendImgAck(String token) throws IOException {
        sendLine(token + ": IMG_ACK");
        requests.put(token, "IMG_ACK");
    }

    private static void sendImgNAck(String token) throws IOException {
        sendLine(token + ": IMG_NACK");
        requests.put(token, "IMG_NACK");
    }

    private static void updateSettings(String data) {
        // TODO: The data is a json object... read it

        // look at examples on json.org

    }

    private String getToken() {
        return (new RandomGUID()).toString();
    }

    private String getSettingsJSONData() {

        // TODO: Get the settings from the application config
        // and make a json object out of it (as a string)

        return "";
    }

    private void processRequests() throws Exception {
        String line;
        while ((line = br.readLine()) != null) {
            parseLine(line);
        }
    }

    private void parseLine(String line) throws Exception {

        String[] strarr1 = line.split(":");
        String token = strarr1[0].trim();
        String action = strarr1[1].trim();

        String[] strarr2 = line.split(":", 3);

        String data = "";
        if (strarr2.length > 2) {
            data = strarr2[2].trim();
        }

        for (ReceiveActions receiveAction : ReceiveActions.values()) {
            if (action.equalsIgnoreCase(receiveAction.toString())) {
                receiveAction.performAction(token, data);
                break;
            }
        }

    }

}
