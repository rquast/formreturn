package com.ebstrada.formreturn.server.thread;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.apache.log4j.Logger;
import org.apache.tools.ant.util.FileUtils;

import com.ebstrada.formreturn.manager.persistence.JPAConfiguration;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.TemplateFormPageID;
import com.ebstrada.formreturn.manager.util.image.ImageUtil;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;
import com.ebstrada.formreturn.server.Main;
import com.swingsane.business.scanning.ScanEvent;
import com.swingsane.gui.controller.IScanEventHandler;

public class ScansWatcher implements Runnable {

    private static Logger LOG = Logger.getLogger(ScansWatcher.class);

    private WatchService myWatcher;

    private boolean previewMode = false;

    private IScanEventHandler scanEventHandler;

    private Component previewComponent;

    public ScansWatcher(WatchService myWatcher) {
        this.myWatcher = myWatcher;
    }

    public ScansWatcher(WatchService myWatcher, boolean previewMode) {
        this.myWatcher = myWatcher;
        this.previewMode = previewMode;
    }

    public Component getPreviewComponent() {
        return previewComponent;
    }

    public void setPreviewComponent(Component previewComponent) {
        this.previewComponent = previewComponent;
    }

    public void setScanEventHandler(IScanEventHandler scanEventHandler) {
        this.scanEventHandler = scanEventHandler;
    }

    @Override public void run() {
        try {
            WatchKey key = myWatcher.take();
            while (key != null) {
                for (WatchEvent<?> event : key.pollEvents()) {

                    final Path changed = (Path) event.context();
                    if (changed.endsWith(".preview_lock")) {
                        continue;
                    }

                    File lockFile = new File(
                        PreferencesManager.getScanDirectoryPath() + File.separator
                            + ".preview_lock");

                    if (previewMode && lockFile.exists() && (previewComponent == null
                        || previewComponent.isVisible() == false)) {
                        FileUtils.delete(lockFile);
                    }

                    try {
                        if (!previewMode) {
                            if (lockFile.exists()) {
                                continue;
                            }
                            processFile(changed);
                        } else {
                            processPreview(changed);
                        }
                    } catch (Exception e) {
                        LOG.error(e, e);
                    }
                }
                key.reset();
                key = myWatcher.take();
            }
        } catch (InterruptedException e) {
            LOG.info(e, e);
        }
    }

    private void processPreview(Path changed) {
        ScanEvent scanEvent = new ScanEvent("");
        File file = changed.toFile();
        BufferedImage acquiredImage;
        try {
            File imageFile = new File(
                PreferencesManager.getScanDirectoryPath() + File.separator + file.getName());
            imageFile.deleteOnExit();
            acquiredImage = ImageUtil.readImage(Misc.getBytesFromFile(imageFile), 1);
            scanEvent.setAcquiredImage(acquiredImage);
            scanEventHandler.scanPerformed(scanEvent);
        } catch (Exception e) {
            Misc.printStackTrace(e);
        }
    }

    private void processFile(Path changed) {
        JPAConfiguration jpaConfiguration = Main.getInstance().getJPAConfiguration();
        File file = changed.toFile();
        try {
            if (file == null) {
                return;
            }
            Misc.uploadImage(jpaConfiguration, new File(
                    PreferencesManager.getScanDirectoryPath() + File.separator + file.getName()),
                new TemplateFormPageID(), null);
            file.deleteOnExit();
        } catch (IOException e) {
            Misc.printStackTrace(e);
        }
    }

}
