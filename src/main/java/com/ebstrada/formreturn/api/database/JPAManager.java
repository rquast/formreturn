package com.ebstrada.formreturn.api.database;

import javax.persistence.EntityManager;

import com.ebstrada.formreturn.manager.util.Misc;

public class JPAManager {

    public static EntityManager getEntityManager() {
        if (Misc.isServerInstance()) {
            return com.ebstrada.formreturn.server.Main.getInstance().getJPAConfiguration()
                .getEntityManager();
        } else {
            return com.ebstrada.formreturn.manager.ui.Main.getInstance().getJPAConfiguration()
                .getEntityManager();
        }
    }

}
