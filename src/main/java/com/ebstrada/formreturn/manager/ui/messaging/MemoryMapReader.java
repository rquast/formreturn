package com.ebstrada.formreturn.manager.ui.messaging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.util.Misc;

public class MemoryMapReader extends Thread {

    private File file;

    public MemoryMapReader(File file) {
        this.file = file;
    }

    public void run() {

        RandomAccessFile raf = null;

        try {

            raf = new RandomAccessFile(this.file, "rw");
            FileChannel fc = raf.getChannel();

            long fileSize = fc.size();

            MappedByteBuffer mem = fc.map(FileChannel.MapMode.READ_WRITE, 0, fileSize);


            while (true) {

                if (fileSize != fc.size()) {
                    fileSize = fc.size();
                    mem = fc.map(FileChannel.MapMode.READ_WRITE, 0, fileSize);
                }

                if (mem.hasRemaining()) {
                    Charset charset = Charset.forName("UTF-8");
                    CharsetDecoder decoder = charset.newDecoder();
                    CharBuffer charBuffer = decoder.decode(mem);
                    String filename = charBuffer.toString();
                    Main.getInstance().open(new File(filename.trim()));
                    mem.clear();
                    raf.setLength(0);
                    mem = fc.map(FileChannel.MapMode.READ_WRITE, 0, 0);
                    fileSize = 0;
                }

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                }

            } // while

        } catch (FileNotFoundException e1) {
            Misc.printStackTrace(e1);
        } catch (IOException e1) {
            Misc.printStackTrace(e1);
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    Misc.printStackTrace(e);
                }
            }
        }

    }
}
