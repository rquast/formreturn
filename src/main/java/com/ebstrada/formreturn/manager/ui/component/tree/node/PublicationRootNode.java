package com.ebstrada.formreturn.manager.ui.component.tree.node;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.persistence.jpa.Publication;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.component.tree.RecordTree;
import com.ebstrada.formreturn.manager.ui.component.tree.RecordTreeNode;
import com.ebstrada.formreturn.manager.ui.component.tree.node.menu.PublicationRootPopupMenu;

public class PublicationRootNode extends RecordNode {

    @Override public void loadChildren() throws Exception {

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();
        try {

            if (entityManager == null) {
                return;
            }

            Query publicationQuery = entityManager.createNativeQuery(
                "SELECT pub.PUBLICATION_ID, pub.PUBLICATION_NAME FROM PUBLICATION pub ORDER BY pub.PUBLICATION_ID DESC",
                Publication.class);
            List<Publication> resultList = publicationQuery.getResultList();

            for (Publication publication : resultList) {
                PublicationNode pn = new PublicationNode();
                pn.setRecordId(publication.getPublicationId());
                pn.setName(publication.getPublicationName());
                addChildNode(pn);
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
        return Localizer.localize("UI", "RecordTreePublicationsTitle");
    }

    @Override public Icon getIcon() {
        return new ImageIcon(
            getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/tree/database.png"));
    }

    @Override public JPopupMenu getPopupMenu(RecordTree tree, RecordTreeNode node) {
        return new PublicationRootPopupMenu(this, tree, node);
    }

}
