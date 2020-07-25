package com.ebstrada.formreturn.manager.ui.component.tree.node;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;

import com.ebstrada.formreturn.manager.persistence.jpa.Form;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.component.tree.RecordTree;
import com.ebstrada.formreturn.manager.ui.component.tree.RecordTreeNode;
import com.ebstrada.formreturn.manager.ui.component.tree.node.menu.PublicationPopupMenu;

public class PublicationNode extends RecordNode {

    private String name;

    @Override public void loadChildren() throws Exception {
        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();
        try {

            if (entityManager == null) {
                return;
            }

            Query formQuery = entityManager.createNativeQuery(
                "SELECT * FROM FORM WHERE PUBLICATION_ID = " + getRecordId()
                    + " ORDER BY FORM_ID DESC", Form.class);
            List<Form> resultList = formQuery.getResultList();

            for (Form form : resultList) {
                FormNode fn = new FormNode();
                fn.setRecordId(form.getFormId());
                fn.setFormPassword(form.getFormPassword());
                fn.setAggregateMark(form.getAggregateMark());
                fn.setErrorCount(form.getErrorCount());
                addChildNode(fn);
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
        return getRecordId() + " - " + getName();
    }

    @Override public Icon getIcon() {
        return new ImageIcon(getClass()
            .getResource("/com/ebstrada/formreturn/manager/ui/icons/tree/publication.png"));
    }

    public void setName(String publicationName) {
        this.name = publicationName;
    }

    public String getName() {
        return this.name;
    }

    @Override public JPopupMenu getPopupMenu(RecordTree tree, RecordTreeNode node) {
        return new PublicationPopupMenu(this, tree, node);
    }

}
