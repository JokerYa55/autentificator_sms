/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keycloak.storage.user;

import javax.naming.InitialContext;
import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

/**
 *
 * @author vasiliy.andricov
 */
public class EjbExampleUserStorageProviderFactory implements UserStorageProviderFactory<EjbExampleUserStorageProvider> {
    private static final Logger log = Logger.getLogger(EjbExampleUserStorageProviderFactory.class);
    @Override
    public EjbExampleUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        try {
            log.debug("----------------------------------------------------------------------");
            log.debug("create");
            InitialContext ctx = new InitialContext();
            EjbExampleUserStorageProvider provider = (EjbExampleUserStorageProvider)ctx.lookup("java:global/user-storage-jpa-dbuser-1/" + EjbExampleUserStorageProvider.class.getSimpleName());
            log.debug("provider = " + provider + "  -> " + EjbExampleUserStorageProvider.class.getSimpleName());
            provider.setModel(model);
            provider.setSession(session);
            return provider;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getId() {
        return "user-storage-dbuser1";
    }
    
    
}
