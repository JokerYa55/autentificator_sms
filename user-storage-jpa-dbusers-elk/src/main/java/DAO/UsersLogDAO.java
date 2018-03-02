/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import keycloak.bean.UsersLog;
import keycloak.interfaces.daoInterface;
import org.jboss.logging.Logger;

/**
 *
 * @author vasil
 */
public class UsersLogDAO implements daoInterface<UsersLog, Long> {

    private static final Logger log = Logger.getLogger(UsersLogDAO.class);

    /**
     *
     */
    @PersistenceContext
    protected EntityManager em;

    /**
     * Конструктор
     *
     * @param em
     */
    public UsersLogDAO(EntityManager em) {
        this.em = em;
    }

    /**
     *
     * @return
     */
    @Override
    public EntityManager getEM() {
        return this.em;
    }
}
