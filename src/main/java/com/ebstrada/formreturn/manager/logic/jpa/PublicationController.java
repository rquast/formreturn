package com.ebstrada.formreturn.manager.logic.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.ebstrada.formreturn.api.messaging.MessageNotification;
import com.ebstrada.formreturn.api.messaging.ProcessingStatusDialog;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.persistence.jpa.Publication;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.util.Misc;

public class PublicationController {

    public void removePublicationsById(long[] selectedPublicationIds,
        MessageNotification messageNotification) {

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return;
        }

        try {
            entityManager.getTransaction().begin();
            entityManager.flush();

            String sqlQry =
                "SELECT FORM.FORM_ID FROM PUBLICATION LEFT JOIN FORM ON PUBLICATION.PUBLICATION_ID = FORM.PUBLICATION_ID WHERE PUBLICATION.PUBLICATION_ID IN ("
                    + Misc.implode(selectedPublicationIds, ",") + ")";
            Query formQuery = entityManager.createNativeQuery(sqlQry);
            List<Long> formIds = formQuery.getResultList();

            int i = 1;
            for (Long formId : formIds) {

                if (formId == null) {
                    continue;
                }

                if (messageNotification.isInterrupted()) {
                    if (entityManager.getTransaction().isActive()) {
                        try {
                            entityManager.getTransaction().rollback();
                        } catch (Exception rbex) {
                            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(rbex);
                        }
                    }
                    return;
                }

                messageNotification.setMessage(String
                    .format(Localizer.localize("UI", "DeletingFormStatusMessageText"), i + "",
                        formIds.size() + ""));

                String deleteSql = "DELETE FROM FORM WHERE FORM_ID = " + formId;
                Query deleteFormQuery = entityManager.createNativeQuery(deleteSql);
                int result = deleteFormQuery.executeUpdate();
                i++;

                if (result <= 0) {
                    throw new Exception(String
                        .format(Localizer.localize("UI", "DeleteFormErrorMessageText"),
                            formId + ""));
                }

            }

            entityManager.getTransaction().commit();

            entityManager.getTransaction().begin();
            entityManager.flush();

            Query publicationQuery = entityManager.createNativeQuery(
                "SELECT * FROM PUBLICATION WHERE PUBLICATION_ID IN (" + Misc
                    .implode(selectedPublicationIds, ",") + ")", Publication.class);
            List<Publication> resultList = publicationQuery.getResultList();
            i = 1;
            for (Publication publication : resultList) {

                if (messageNotification.isInterrupted()) {
                    if (entityManager.getTransaction().isActive()) {
                        try {
                            entityManager.getTransaction().rollback();
                        } catch (Exception rbex) {
                            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(rbex);
                        }
                    }
                    return;
                }

                messageNotification.setMessage(String
                    .format(Localizer.localize("UI", "DeletingPublicationStatusMessageText"),
                        i + "", resultList.size() + ""));
                entityManager.remove(publication);
                i++;
            }

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            if (entityManager.getTransaction().isActive()) {
                try {
                    entityManager.getTransaction().rollback();
                } catch (Exception rbex) {
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(rbex);
                }
            }
            messageNotification.setException(ex);
            messageNotification.setInterrupted(true);
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
        } finally {
            entityManager.close();
        }

    }

    public Publication getPublicationById(long publicationId) {
        Publication publication = null;
        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return null;
        }

        try {
            publication = entityManager.find(Publication.class, publicationId);
        } catch (Exception ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
            return null;
        } finally {
            entityManager.close();
        }
        return publication;
    }

    public void renamePublication(long publicationId, String publicationName) {
        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return;
        }

        try {
            Publication publication = entityManager.find(Publication.class, publicationId);
            publication.setPublicationName(publicationName);
            entityManager.getTransaction().begin();
            entityManager.flush();
            entityManager.persist(publication);
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
        } finally {
            entityManager.close();
        }
    }

}
