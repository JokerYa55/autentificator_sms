package keycloak.storage.user;

import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

import javax.naming.InitialContext;

/**
 * @version 1
 */
public class EjbUserStorageProviderFactory implements UserStorageProviderFactory<EjbUserStorageProvider> {
    private static final Logger log = Logger.getLogger(EjbUserStorageProviderFactory.class);

    /**
     *
     * @param session
     * @param model
     * @return
     */
    @Override
    public EjbUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        try {
            log.debug("----------------------------------------------------------------------");
            log.debug("create");
            InitialContext ctx = new InitialContext();
            EjbUserStorageProvider provider = (EjbUserStorageProvider)ctx.lookup("java:global/user-storage-jpa-dbusers-elk/" + EjbUserStorageProvider.class.getSimpleName());
            log.debug("provider = " + provider + "  -> " + EjbUserStorageProvider.class.getSimpleName());
            provider.setModel(model);
            provider.setSession(session);
            return provider;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @return
     */
    @Override
    public String getId() {
        return "user-storage-dbuser-elk";
    }

    /**
     *
     * @return
     */
    @Override
    public String getHelpText() {
        return "JPA User Storage Provider for DBUsers1";
    }

    /**
     *
     */
    @Override
    public void close() {
        log.info("<<<<<< Closing factory");

    }
}
