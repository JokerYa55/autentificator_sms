/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import keycloak.bean.UserAttribute;
import keycloak.bean.UserEntity;
import keycloak.interfaces.daoInterface;
import org.jboss.logging.Logger;

/**
 *
 * @author vasil
 */
public class UserAttributeDAO implements daoInterface<UserAttribute, Long> {

    private EntityManager em;
    private Logger log = Logger.getLogger(getClass().getName());
    
    public UserAttributeDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public EntityManager getEM() {
        return this.em;
    }   

    public UserAttribute getUserAttributeByUserName(UserEntity userId, String name) {
        UserAttribute res = null;
        log.debug("getUserAttributeByUserName = > " + userId + " : " + name);
        try {
            TypedQuery<UserAttribute> namedQuery = em.createNamedQuery("findAttributeByUserName", UserAttribute.class);
            namedQuery.setParameter("userId", userId);
            namedQuery.setParameter("name", name);
            res = namedQuery.getSingleResult();
            
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        log.debug("res = > " + res);
        return res;
    }

}
