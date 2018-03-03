package keycloak.storage.user;

import java.util.Iterator;
import org.jboss.logging.Logger;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import keycloak.bean.UserEntity;
import org.keycloak.models.GroupModel;

/**
 * Класс для представления пользователя внутри Keycloak
 *
 * @version 1
 * @author Vasiliy Andritsov
 *
 */
public class UserAdapter extends AbstractUserAdapterFederatedStorage {

    private static final Logger log = Logger.getLogger(UserAdapter.class);
    protected UserEntity entity;
    protected String keycloakId;
    protected EntityManager em;

    public UserAdapter(KeycloakSession session, RealmModel realm, ComponentModel model, UserEntity entity, EntityManager em) {
        super(session, realm, model);
        log.debug("UserAdapter CONSTRUCTOR => entity = " + entity);
        this.entity = entity;
        // внутренний ID
        keycloakId = StorageId.keycloakId(model, entity.getId().toString());
        log.debug("keycloakId => " + keycloakId);
        this.em = em;
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
