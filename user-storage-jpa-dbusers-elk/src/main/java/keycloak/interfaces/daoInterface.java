/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keycloak.interfaces;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.jboss.logging.Logger;

/**
 * Интерфейс для работы JPA объектами
 * @author vasil
 * @param <T>
 * @param <V>
 */
public interface daoInterface<T, V> {

    /**
     *
     * @return
     */
    /**
     *
     * @return
     */
    public EntityManager getEM();

    /**
     *
     * @param id
     * @return
     */
//    public T getItem(V id);

    /**
     *
     * @return
     */
//    public List<T> getList();

    /**
     *
     * @param startIdx
     * @param stopIdx
     * @return
     */
//    public List<T> getList(V startIdx, V stopIdx);

    /**
     *
     * @param Item
     * @return
     */
    default public T addItem(T Item) {
        T res = null;
        try {
            EntityManager em = getEM();
            em.getTransaction().begin();
            em.merge(Item);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     *
     * @param Item
     * @return
     */
    default public boolean deleteItem(T Item) {
        boolean res = true;
        try {
            EntityManager em = getEM();
            em.getTransaction().begin();
            em.detach(Item);
            em.getTransaction().commit();
        } catch (Exception e) {
            res = false;
            e.printStackTrace();
        }
        return res;
    }

    /**
     *
     * @param Item
     * @return
     */
    default public boolean updateItem(T Item) {
        boolean res = true;
        try {
            EntityManager em = getEM();
            em.getTransaction().begin();
            em.merge(Item);
            em.getTransaction().commit();
        } catch (Exception e) {
            res = false;
            e.printStackTrace();
        }
        return res;
    }

    /**
     *
     * @param id
     * @param jpqName
     * @param cl
     * @return
     */
    default public T getItem(long id, String jpqName, Class<T> cl) {
        T res = null;
        try {
            EntityManager em = getEM();
            TypedQuery<T> namedQuery = em.createNamedQuery(jpqName, cl);
            namedQuery.setParameter("id", id);
            res = namedQuery.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     *
     * @param jpqName
     * @param cl
     * @return
     */
    default public List<T> getList(String jpqName, Class<T> cl) {
        List<T> res = null;
        try {
            EntityManager em = getEM();
            TypedQuery<T> namedQuery = em.createNamedQuery(jpqName, cl);
            //namedQuery.setParameter("id", id);
            res = namedQuery.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    //public List<T> getList(V startIdx, V stopIdx);
    default public List<T> getList(int startIdx, int countRec, String jpqName, Class<T> cl) {
        List<T> res = null;
        try {
            EntityManager em = getEM();
            TypedQuery<T> namedQuery = em.createNamedQuery(jpqName, cl);
            //namedQuery.setParameter("id", id);            
            res = namedQuery.setFirstResult(startIdx).setMaxResults(countRec).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
}
