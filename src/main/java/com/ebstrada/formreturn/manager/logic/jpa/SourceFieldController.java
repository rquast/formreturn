package com.ebstrada.formreturn.manager.logic.jpa;

import javax.persistence.EntityManager;

import com.ebstrada.formreturn.manager.persistence.jpa.DataSet;
import com.ebstrada.formreturn.manager.persistence.jpa.SourceField;
import com.ebstrada.formreturn.manager.ui.Main;

public class SourceFieldController {

    public SourceFieldController() {
    }

    public SourceField createNewSourceField(long dataSetId, String sourceFieldName,
        int orderIndex) {

        SourceField newSourceField = new SourceField();
        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return null;
        }
        try {
            entityManager.getTransaction().begin();
            entityManager.flush();
            newSourceField.setSourceFieldName(sourceFieldName);
            newSourceField.setSourceFieldType("STRING");
            newSourceField.setOrderIndex(orderIndex);
            DataSet ds = entityManager.find(DataSet.class, dataSetId);
            newSourceField.setDataSetId(ds);
            entityManager.persist(newSourceField);
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
        return newSourceField;
    }

    public void renameSourceField(long sourceFieldId, String sourceFieldName) {

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return;
        }

        try {
            SourceField sourceField = entityManager.find(SourceField.class, sourceFieldId);
            sourceField.setSourceFieldName(sourceFieldName);
            entityManager.getTransaction().begin();
            entityManager.flush();
            entityManager.persist(sourceField);
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

    public boolean removeSourceFieldById(long sourceFieldId) {

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return false;
        }

        try {
            SourceField sourceField = entityManager.find(SourceField.class, sourceFieldId);
            entityManager.getTransaction().begin();
            entityManager.flush();
            entityManager.remove(sourceField);
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
        return true;
    }

    public SourceField getSourceFieldById(long sourceFieldId) {
        SourceField sourceField = null;

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return null;
        }

        try {
            sourceField = entityManager.find(SourceField.class, sourceFieldId);
        } catch (Exception ex) {
            if (entityManager.getTransaction().isActive()) {
                try {
                    entityManager.getTransaction().rollback();
                } catch (Exception rbex) {
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(rbex);
                }
            }
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
            return null;
        } finally {
            entityManager.close();
        }
        return sourceField;
    }
}
