package com.ebstrada.formreturn.manager.ui.pqm.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.recognition.reader.FormReaderException;
import com.ebstrada.formreturn.manager.persistence.jpa.IncomingImage;
import com.ebstrada.formreturn.manager.ui.Main;

public class ProcessingQueueModel {

    private int unidentifiedImagesSize = 0;
    private int unidentifiedImagesOffset = 0;
    private int unidentifiedImagesLimit = 1000;
    private int unprocessedImagesSize = 0;
    private int unprocessedImagesOffset = 0;
    private int unprocessedImagesLimit = 1000;

    private long[] selectedUnprocessedImageIds = new long[] {};
    private long[] selectedUnidentifiedImageIds = new long[] {};

    public TableModel getUnidentifiedImagesModel() {

        DefaultTableModel dtm = new DefaultTableModel() {

            private static final long serialVersionUID = 1L;

            Class[] columnTypes =
                new Class[] {String.class, String.class, String.class, String.class};
            boolean[] columnEditable = new boolean[] {false, false, false, false};

            @Override public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }

            @Override public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnEditable[columnIndex];
            }
        };
        dtm.addColumn("ID");
        dtm.addColumn(Localizer.localize("UI", "ProcessingQueueUnidentifiedImageNameColumnName"));
        dtm.addColumn(Localizer.localize("UI", "ProcessingQueueUnidentifiedReasonColumnName"));
        dtm.addColumn(Localizer.localize("UI", "ProcessingQueueUnidentifiedTimeColumnName"));

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return dtm;
        }

        try {

            Query query = entityManager.createNamedQuery("IncomingImage.count");
            Number countResult = (Number) query.getSingleResult();
            unidentifiedImagesSize = countResult.intValue();

            Query incomingImageQuery = entityManager.createNativeQuery(
                "SELECT ii.INCOMING_IMAGE_NAME, ii.INCOMING_IMAGE_ID FROM INCOMING_IMAGE ii WHERE MATCH_STATUS = -1",
                IncomingImage.class);
            incomingImageQuery.setFirstResult(unidentifiedImagesOffset);
            incomingImageQuery.setMaxResults(unidentifiedImagesLimit);

            List<IncomingImage> resultList = incomingImageQuery.getResultList();
            Iterator<IncomingImage> resultListIterator = resultList.iterator();
            while (resultListIterator.hasNext()) {

                IncomingImage incomingImage = resultListIterator.next();

                int errorType = incomingImage.getMatchErrorType();
                FormReaderException fre = new FormReaderException(errorType);

                Timestamp timestamp = incomingImage.getCaptureTime();
                String dateString = new SimpleDateFormat().format(timestamp.getTime());

                dtm.addRow(new String[] {incomingImage.getIncomingImageId() + "",
                    incomingImage.getIncomingImageName(), fre.getErrorTitle(), dateString});

            }

        } catch (org.apache.openjpa.persistence.PersistenceException pe) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(pe);
            return dtm;
        } finally {
            entityManager.close();
        }

        return dtm;
    }

    public TableModel getUnprocessedImagesModel() {

        DefaultTableModel dtm = new DefaultTableModel() {

            private static final long serialVersionUID = 1L;

            Class[] columnTypes = new Class[] {String.class, String.class};
            boolean[] columnEditable = new boolean[] {false, false};

            @Override public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }

            @Override public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnEditable[columnIndex];
            }
        };
        dtm.addColumn("ID");
        dtm.addColumn(Localizer.localize("UI", "ProcessingQueueUnprocessedImageNameColumnName"));


        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return dtm;
        }
        try {

            Query query = entityManager.createNamedQuery("IncomingImage.count");
            Number countResult = (Number) query.getSingleResult();
            unprocessedImagesSize = countResult.intValue();

            Query incomingImageQuery = entityManager.createNativeQuery(
                "SELECT ii.INCOMING_IMAGE_NAME, ii.INCOMING_IMAGE_ID FROM INCOMING_IMAGE ii WHERE MATCH_STATUS = 0",
                IncomingImage.class);
            incomingImageQuery.setFirstResult(unprocessedImagesOffset);
            incomingImageQuery.setMaxResults(unprocessedImagesLimit);

            List<IncomingImage> resultList = incomingImageQuery.getResultList();
            Iterator<IncomingImage> resultListIterator = resultList.iterator();
            while (resultListIterator.hasNext()) {
                IncomingImage incomingImage = resultListIterator.next();
                dtm.addRow(new String[] {incomingImage.getIncomingImageId() + "",
                    incomingImage.getIncomingImageName()});
            }

        } catch (org.apache.openjpa.persistence.PersistenceException pe) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(pe);
            return dtm;
        } finally {
            entityManager.close();
        }

        return dtm;

    }

    public byte[] getUnidentifiedImage() {

        if (getSelectedUnidentifiedImageIds() == null
            || getSelectedUnidentifiedImageIds().length <= 0) {
            return null;
        }

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();
        if (entityManager == null) {
            return null;
        }

        byte[] imageData;

        try {
            IncomingImage incomingImage =
                entityManager.find(IncomingImage.class, getSelectedUnidentifiedImageIds()[0]);
            imageData = incomingImage.getIncomingImageData();
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
                entityManager = null;
            }
        }

        return imageData;

    }

    public String getUnidentifiedImageName() {

        if (getSelectedUnidentifiedImageIds() == null
            || getSelectedUnidentifiedImageIds().length <= 0) {
            return null;
        }

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();
        if (entityManager == null) {
            return null;
        }

        String imageName;

        try {
            IncomingImage incomingImage =
                entityManager.find(IncomingImage.class, getSelectedUnidentifiedImageIds()[0]);
            imageName = incomingImage.getIncomingImageName();
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }

        return imageName;

    }

    public long[] getSelectedUnidentifiedImageIds() {
        return selectedUnidentifiedImageIds;
    }

    public void setSelectedUnidentifiedImageIds(long[] selectedUnidentifiedImageIds) {
        this.selectedUnidentifiedImageIds = selectedUnidentifiedImageIds;
    }

    public long[] getSelectedUnprocessedImageIds() {
        return selectedUnprocessedImageIds;
    }

    public void setSelectedUnprocessedImageIds(long[] selectedUnprocessedImageIds) {
        this.selectedUnprocessedImageIds = selectedUnprocessedImageIds;
    }

    public boolean deleteUnprocessedImages(long[] selectedUnprocessedImageIds) {
        if (selectedUnprocessedImageIds.length > 0) {
            String message =
                Localizer.localize("UI", "ProcessingQueueConfirmRemoveUnprocessedImageMessage");
            String caption =
                Localizer.localize("UI", "ProcessingQueueConfirmRemoveUnprocessedImageTitle");
            int ret = javax.swing.JOptionPane
                .showConfirmDialog(Main.getInstance(), message, caption,
                    javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.QUESTION_MESSAGE);
            if (ret == javax.swing.JOptionPane.YES_OPTION) {
                for (int i = 0; i < selectedUnprocessedImageIds.length; i++) {

                    EntityManager entityManager =
                        Main.getInstance().getJPAConfiguration().getEntityManager();

                    if (entityManager == null) {
                        return false;
                    }

                    try {
                        entityManager.getTransaction().begin();
                        entityManager.flush();
                        IncomingImage incomingImage =
                            entityManager.find(IncomingImage.class, selectedUnprocessedImageIds[i]);
                        entityManager.remove(incomingImage);
                        entityManager.getTransaction().commit();
                    } catch (Exception ex) {
                        if (entityManager.getTransaction().isActive()) {
                            try {
                                entityManager.getTransaction().rollback();
                            } catch (Exception rbex) {
                                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(rbex);
                            }
                        }
                        com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
                        return false;
                    } finally {
                        entityManager.close();
                    }

                }
                return true;
            }
        }
        return false;
    }

    public boolean deleteUnidentifiedImages() {
        if (selectedUnidentifiedImageIds.length > 0) {
            String message =
                Localizer.localize("UI", "ProcessingQueueConfirmRemoveUnidentifiedImageMessage");
            String caption =
                Localizer.localize("UI", "ProcessingQueueConfirmRemoveUnidentifiedImageTitle");
            int ret = javax.swing.JOptionPane
                .showConfirmDialog(Main.getInstance(), message, caption,
                    javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.QUESTION_MESSAGE);
            if (ret == javax.swing.JOptionPane.YES_OPTION) {
                for (int i = 0; i < selectedUnidentifiedImageIds.length; i++) {

                    EntityManager entityManager =
                        Main.getInstance().getJPAConfiguration().getEntityManager();

                    if (entityManager == null) {
                        return false;
                    }

                    try {
                        entityManager.getTransaction().begin();
                        entityManager.flush();
                        IncomingImage incomingImage = entityManager
                            .find(IncomingImage.class, selectedUnidentifiedImageIds[i]);
                        entityManager.remove(incomingImage);
                        entityManager.getTransaction().commit();
                    } catch (Exception ex) {
                        if (entityManager.getTransaction().isActive()) {
                            try {
                                entityManager.getTransaction().rollback();
                            } catch (Exception rbex) {
                                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(rbex);
                            }
                        }
                        com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
                        return false;
                    } finally {
                        entityManager.close();
                    }

                }
                return true;
            }
        }
        return false;
    }


}
