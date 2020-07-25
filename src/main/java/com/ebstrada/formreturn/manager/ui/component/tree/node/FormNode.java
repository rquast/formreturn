package com.ebstrada.formreturn.manager.ui.component.tree.node;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.persistence.jpa.FormPage;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.component.tree.RecordTree;
import com.ebstrada.formreturn.manager.ui.component.tree.RecordTreeNode;
import com.ebstrada.formreturn.manager.ui.component.tree.node.menu.FormPopupMenu;

public class FormNode extends RecordNode {

    private long errorCount;
    private double aggregateMark;
    private String formPassword;

    private long formPageCount = 0;
    private long formPagesProcessed = 0;

    @Override public void loadChildren() throws Exception {
        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();
        try {

            if (entityManager == null) {
                return;
            }

            Query formPageQuery = entityManager.createNativeQuery(
                "SELECT * FROM FORM_PAGE WHERE FORM_ID = " + getRecordId()
                    + " ORDER BY FORM_PAGE_NUMBER ASC", FormPage.class);
            List<FormPage> resultList = formPageQuery.getResultList();

            for (FormPage formPage : resultList) {
                FormPageNode fpn = new FormPageNode();
                fpn.setRecordId(formPage.getFormPageId());
                fpn.setPageNumber(formPage.getFormPageNumber());
                fpn.setErrorCount(formPage.getErrorCount());
                fpn.setProcessedTime(formPage.getProcessedTime());
                fpn.setFormPassword(getFormPassword());
                if (formPage.getProcessedTime() != null) {
                    ++formPagesProcessed;
                }
                fpn.setAggregateMark(formPage.getAggregateMark());
                addChildNode(fpn);
                ++formPageCount;
            }
            childNodesLoaded = true;
        } catch (org.apache.openjpa.persistence.PersistenceException pe) {
            throw pe;
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    @Override public String toString() {

        String str = String
            .format(Localizer.localize("UI", "RecordTreeFormNodeFormIDLabel"), getRecordId() + "");
        if (formPagesProcessed > 0) {
            str += String.format(Localizer.localize("UI", "RecordTreeFormNodeFormScoreLabel"),
                getAggregateMark() + "");
            if (getErrorCount() > 0) {
                str += " " + String
                    .format(Localizer.localize("UI", "RecordTreeFormNodeErrorCountLabel"),
                        getErrorCount() + "");
            }
        }

        return str;
    }

    @Override public Icon getIcon() {
        return new ImageIcon(getClass().getResource(
            "/com/ebstrada/formreturn/manager/ui/icons/tree/form" + getFlagString() + ".png"));
    }

    public String getFlagString() {
        if (errorCount > 0) {
            return "_red";
        }
        if (formPagesProcessed > 0 && (formPagesProcessed < formPageCount)) {
            return "_yellow";
        }
        if (formPagesProcessed > 0 && (formPagesProcessed >= formPageCount)) {
            return "_green";
        }
        return "";
    }

    public void setErrorCount(long errorCount) {
        this.errorCount = errorCount;
    }

    public void setAggregateMark(double aggregateMark) {
        this.aggregateMark = aggregateMark;
    }

    public void setFormPassword(String formPassword) {
        this.formPassword = formPassword;
    }

    public long getErrorCount() {
        return errorCount;
    }

    public double getAggregateMark() {
        return aggregateMark;
    }

    public String getFormPassword() {
        return formPassword;
    }

    @Override public JPopupMenu getPopupMenu(RecordTree tree, RecordTreeNode node) {
        return new FormPopupMenu(this, tree, node);
    }

}
