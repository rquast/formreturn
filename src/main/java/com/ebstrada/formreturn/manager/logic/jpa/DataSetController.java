package com.ebstrada.formreturn.manager.logic.jpa;

import javax.persistence.EntityManager;

import com.ebstrada.formreturn.manager.persistence.jpa.DataSet;
import com.ebstrada.formreturn.manager.ui.Main;

public class DataSetController {

    public DataSetController() {
    }

    public DataSet createNewDataSet(String dataSetName) {
        DataSet newDataSet = new DataSet();

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return null;
        }

        try {
            entityManager.getTransaction().begin();
            entityManager.flush();
            newDataSet.setDataSetName(dataSetName);
            entityManager.persist(newDataSet);
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
            return null;
        } finally {
            entityManager.close();
        }

        return newDataSet;
    }

    public DataSet getDataSetById(long dataSetId) {
        DataSet dataSet = null;

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return null;
        }

        try {
            dataSet = entityManager.find(DataSet.class, dataSetId);
        } catch (Exception ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
            return null;
        } finally {
            entityManager.close();
        }
        return dataSet;
    }

    public void renameDataSet(long dataSetId, String dataSetName) {
        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return;
        }
        try {
            DataSet dataSet = entityManager.find(DataSet.class, dataSetId);
            dataSet.setDataSetName(dataSetName);
            entityManager.getTransaction().begin();
            entityManager.flush();
            entityManager.persist(dataSet);
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

    public boolean removeDataSet(DataSet dataSet) {

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return false;
        }

        try {
            entityManager.getTransaction().begin();
            entityManager.flush();
            entityManager.remove(dataSet);
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

    public boolean removeDataSetById(long dataSetId) {

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return false;
        }

        try {
            DataSet dataSet = entityManager.find(DataSet.class, dataSetId);
            entityManager.getTransaction().begin();
            entityManager.flush();
            entityManager.remove(dataSet);
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

}
