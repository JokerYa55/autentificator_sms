/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sso_b2b.user.registry.DAO;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.jboss.logging.Logger;

/**
 *
 * @author vasil
 * @param <T>
 * @param <V>
 */
public class abstractClassDAO<T, V> implements daoInterface<T, V> {

    final Logger log = Logger.getLogger(getClass().getName());
    private EntityManager em;

    public abstractClassDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public EntityManager getEM() {
        return this.em;
    }

    @Override
    public Logger getLog() {
        return this.log;
    }

}
