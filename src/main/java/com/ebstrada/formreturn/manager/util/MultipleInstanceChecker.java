package com.ebstrada.formreturn.manager.util;

import java.awt.Window;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.ebstrada.formreturn.manager.ui.Main;

/**
 * Helper class to avoid running more than one concurrent instance of a Java
 * program.
 *
 * <h3>Rationale</h3>
 * With some applications it may be useless or even dangerous to run more than
 * one instance at the same time. This class helps a Java application to
 * determine if another instance is running already. It does so by trying to
 * reserve a server socket for a given port. If the server socket can be
 * reserved, this instance is the first one. Otherwise a user-defined byte
 * signature is sent to the running instance, which can then perform some action
 * (e.g. bringing its own window to the front of the desktop).
 *
 * <h3>Usage</h3>
 * Study the MultipleInstanceTest application that comes with this class in
 * order to find out how to integrate the check in your own application.
 *
 * @author Marco Schmidt
 */
public class MultipleInstanceChecker {
    /**
     * Length of byte[] signature in bytes.
     */
    public static final int SIGNATURE_LENGTH = 8;

    // result values for {@link #check()}.
    public static final int STATUS_FIRST_INSTANCE = 1;
    public static final int STATUS_INSTANCE_EXISTS = 2;
    public static final int STATUS_SECURITY_EXCEPTION = 3;

    private byte[] signature;
    private int socketPort;
    private ServerSocket serverSocket;
    private Window window;
    private boolean shutdown;


    public static class TerminalServerReqistryCheck {

        public static final boolean readRegistry(String location, String key) {
            try {
                Process process =
                    Runtime.getRuntime().exec("reg query " + '"' + location + "\" /v " + key);
                StreamReader reader = new StreamReader(process.getInputStream());
                reader.start();
                process.waitFor();
                reader.join();
                String output = reader.getResult();
                if (output.contains("REG_DWORD") && output.contains("0x1")) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }

        public static class StreamReader extends Thread {
            private InputStream is;
            private StringWriter sw = new StringWriter();

            public StreamReader(InputStream is) {
                this.is = is;
            }

            public void run() {
                try {
                    int c;
                    while ((c = is.read()) != -1)
                        sw.write(c);
                } catch (IOException e) {
                }
            }

            public String getResult() {
                return sw.toString();
            }
        }

    }


    /**
     * Create a MultipleInstanceChecker.
     *
     * @param applicationSignature a byte array of length 8 or larger
     * @param port                 number of the port for which a server socket is to be
     *                             reserved
     */
    public MultipleInstanceChecker(byte[] applicationSignature, int port) {
        if (applicationSignature == null || applicationSignature.length != SIGNATURE_LENGTH) {
            throw new IllegalArgumentException(
                "Signature length in bytes must be " + SIGNATURE_LENGTH);
        }
        signature = new byte[applicationSignature.length];
        System.arraycopy(applicationSignature, 0, signature, 0, applicationSignature.length);
        socketPort = port;
        shutdown = false;
    }

    public MultipleInstanceChecker(byte[] applicationSignature, int port, Window win) {
        this(applicationSignature, port);
        setWindow(win);
    }

    public static boolean isTerminalServer() {
        boolean isEnabled = TerminalServerReqistryCheck
            .readRegistry("HKLM\\SYSTEM\\CurrentControlSet\\Control\\Terminal Server", "TSEnabled");
        if (isEnabled == false) {
            return false;
        }
        boolean isDenyConnections = TerminalServerReqistryCheck
            .readRegistry("HKLM\\SYSTEM\\CurrentControlSet\\Control\\Terminal Server",
                "fDenyTSConnections");
        if (isDenyConnections) {
            return false;
        } else {
            return true;
        }
    }

    public int check() {
        try {
            serverSocket = new ServerSocket(socketPort);
            serverSocket.setSoTimeout(0);
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        Socket socket = null;
                        InputStream in = null;
                        // create a socket for incoming "call"
                        try {
                            socket = serverSocket.accept();
                        } catch (IOException ioe) {
                            continue;
                        }
                        if (shutdown) {
                            break;
                        }
                        // create InputStream to read from other process
                        try {
                            in = socket.getInputStream();
                        } catch (IOException ioe) {
                            close(socket, in);
                            continue;
                        }
                        // allocate data array
                        byte[] data = new byte[SIGNATURE_LENGTH];
                        // read SIGNATURE_LENGTH bytes from input
                        if (!readSignature(in, data)) {
                            close(socket, in);
                            continue;
                        }
                        close(socket, in);
                        boolean equal = true;
                        for (int i = 0; i < data.length; i++) {
                            if (data[i] != signature[i]) {
                                equal = false;
                                break;
                            }
                        }
                        if (equal) {
                            onRestart();
                        }
                    }
                    try {
                        serverSocket.close();
                    } catch (IOException ioe) {

                    }
                }
            }).start();
            return STATUS_FIRST_INSTANCE;
        } catch (IOException ioe) {
            // could not reserve server socket

            // try to send signature to other process, making it call
            // its onRestart method

            // try to get socket on that port
            Socket socket = null;
            try {
                socket = new Socket("127.0.0.1", socketPort);
            } catch (IOException ioe2) {
                return STATUS_INSTANCE_EXISTS;
            }

            // get stream to write to
            OutputStream out = null;
            try {
                out = socket.getOutputStream();
            } catch (IOException ioe2) {
                close(socket);
            }

            // write signature
            try {
                out.write(signature);
                out.flush();
            } catch (IOException ioe2) {
            }

            close(socket, out);
            return STATUS_INSTANCE_EXISTS;
        } catch (SecurityException se) {
            return STATUS_SECURITY_EXCEPTION;
        }
    }

    private void close(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ioe) {

            }
        }
    }

    private void close(Socket socket, InputStream in) {
        close(socket);
        if (in != null) {
            try {
                in.close();
            } catch (IOException ioe) {

            }
        }
    }

    private void close(Socket socket, OutputStream out) {
        close(socket);
        if (out != null) {
            try {
                out.close();
            } catch (IOException ioe) {

            }
        }
    }

    public void onRestart() {
        if (window != null) {
            this.window.toFront();
        }
    }

    private boolean readSignature(InputStream in, byte[] data) {
        int index = 0;
        while (index < data.length) {
            try {
                int numRead = in.read(data, index, data.length - index);
                if (numRead > 0) {
                    index += numRead;
                }
            } catch (IOException ioe) {
                return false;
            }
        }
        return true;
    }

    public void setWindow(Window win) {
        this.window = win;
    }

    public void shutdownServerSocketThread() {
        // set shutdown variable
        shutdown = true;
        // make thread accept once more and then react to shutdown==true
        try {
            new Socket(serverSocket.getInetAddress(), serverSocket.getLocalPort()).close();
        } catch (IOException ioe) {
        }
    }
}
