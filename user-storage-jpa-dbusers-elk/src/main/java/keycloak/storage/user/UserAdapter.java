/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keycloak.storage.user;

import javax.persistence.EntityManager;
import keycloak.bean.UserEntity;
import org.apache.log4j.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

/**
 *
 * @author vasiliy.andricov
 */
public class UserAdapter extends AbstractUserAdapterFederatedStorage {

    private static final Logger log = Logger.getLogger(UserAdapter.class);
    protected UserEntity entity;
    protected String keycloakId;
    protected EntityManager em;
    
    public UserAdapter(KeycloakSession session, RealmModel realm, ComponentModel storageProviderModel) {
        super(session, realm, storageProviderModel);
    }

    @Override
    public String getUsername() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setUsername(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
