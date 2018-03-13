package keycloak.storage.user;

import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.cache.CachedUserModel;
import org.keycloak.models.cache.OnUserCache;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;

import javax.ejb.Local;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import keycloak.bean.UserEntity;
import static keycloak.storage.util.hashUtil.encodeToHex;
import static keycloak.storage.util.hashUtil.genSalt;
import static keycloak.storage.util.hashUtil.md5;
import static keycloak.storage.util.hashUtil.sha1;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.common.util.Time;
import org.keycloak.models.ClientModel;
import org.keycloak.models.FederatedIdentityModel;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.UserConsentModel;
import org.keycloak.storage.federated.UserFederatedStorageProvider;

/**
 * @version 1
 */
@Stateful
@Local(EjbExampleUserStorageProvider.class)
public class EjbExampleUserStorageProvider implements UserStorageProvider,
        UserLookupProvider,
        UserRegistrationProvider,
        UserQueryProvider,
        CredentialInputUpdater,
        CredentialInputValidator,
        OnUserCache,
        UserFederatedStorageProvider {

    private static final Logger log = Logger.getLogger(EjbExampleUserStorageProvider.class);
    public static final String PASSWORD_CACHE_KEY = UserAdapter.class.getName() + ".password";
    public static final String SALT_CACHE_KEY = UserAdapter.class.getName() + ".salt";
    public static final String HASH_TYPE_CACHE_KEY = UserAdapter.class.getName() + ".hash_type";
    //public static final int SECRET_QUESTION_SIZE = 6;

    @PersistenceContext
    protected EntityManager em;
    private ComponentModel model;
    private KeycloakSession session;

    @Override
    public void close() {
        log.debug("close");
    }

    /**
     *
     * @param id
     * @param realm
     * @return
     */
    @Override
    public UserModel getUserById(String id, RealmModel realm) {
        log.info(String.format("getUserById => \n\tid = %s\n\trealm = %s", id, realm));
        UserModel result = null;
        try {
            Long persistenceId = new Long(StorageId.externalId(id));
            log.debug("persistenceId => " + persistenceId);
            UserEntity entity = em.find(UserEntity.class, persistenceId);
            if (entity == null) {
                log.info("could not find user by id: " + id);
                result = null;
            } else {
                log.debug("ID => " + entity.getId().toString());
                result = new UserAdapter(session, realm, model, entity, em);
            }
        } catch (Exception e) {
            log.log(Logger.Level.ERROR, e);
        }
        return result;
    }

    /**
     *
     * @param username
     * @param realm
     * @return
     */
    @Override
    public UserModel getUserByUsername(String username, RealmModel realm) {
        log.info(String.format("getUserByUsername => \n\tusername = %s\n\trealm = %s", username, realm));
        UserModel result = null;
        try {
            TypedQuery<UserEntity> query = null;
            if (!username.contains("+7")) {
                log.debug("FIND BY USERNAME");
                query = em.createNamedQuery("getUserByUsername", UserEntity.class);
                query.setParameter("username", username);
            } else {
                log.debug("FIND BY PHONE => " + username.substring(2));
                query = em.createNamedQuery("getUserByPhone", UserEntity.class);
                query.setParameter("phone", username.substring(1));
            }

            List<UserEntity> resultList = query.getResultList();
            if (resultList.isEmpty()) {
                log.info("could not find username: " + username);
                return null;
            } else {
                result = new UserAdapter(session, realm, model, resultList.get(0), em);
            }
        } catch (Exception e) {
            log.log(Logger.Level.ERROR, e);
        }
        return result;
    }

    /**
     *
     * @param string
     * @param rm
     * @return
     */
    @Override
    public UserModel getUserByEmail(String email, RealmModel realm) {
        log.info(String.format("getUserByEmail \n\temail = %s \n\trealm = %s", email, realm));
        TypedQuery<UserEntity> query = em.createNamedQuery("getUserByEmail", UserEntity.class);
        query.setParameter("email", email);
        List<UserEntity> result = query.getResultList();
        if (result.isEmpty()) {
            return null;
        }
        return new UserAdapter(session, realm, model, result.get(0), em);
    }

    @Override
    public UserModel addUser(RealmModel realm, String username) {
        log.info(String.format("addUser => \n\trealm = %s\n\tusername = %s", realm, username));
        UserModel result = null;
        try {
            UserEntity entity = new UserEntity();
            entity.setUsername(username);
            entity.setUser_status(0);
            entity.setCreate_date(new Date());
            em.persist(entity);
            UserAdapter user = new UserAdapter(session, realm, model, entity, em);
            result = user;
        } catch (Exception e) {
            log.log(Logger.Level.ERROR, e);
        }
        return result;
    }

    /**
     *
     * @param realm
     * @param user
     * @return
     */
    @Override
    public boolean removeUser(RealmModel realm, UserModel user) {
        log.info(String.format("removeUser \n\trealm = %s \n\tuser = %s", realm, user));
        boolean res = false;
        try {
            Long persistenceId = new Long(StorageId.externalId(user.getId()));
            UserEntity entity = em.find(UserEntity.class, persistenceId);
            if (entity == null) {
                return false;
            }
            //em.remove(entity);
            entity.setUser_status(1);
            entity.setEmail(null);
            entity.setPhone(null);
            em.merge(entity);
            res = true;
        } catch (Exception e) {
            log.log(Logger.Level.ERROR, e);
        }
        return res;
    }

    /**
     * Возвращает количество пользователей
     *
     * @param realm
     * @return
     */
    @Override
    public int getUsersCount(RealmModel realm) {
        log.info(String.format("removeUser \n\trealm = %s", realm));
        Object count = em.createNamedQuery("getUserCount").getSingleResult();
        return ((Number) count).intValue();
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm) {
        log.info(String.format("getUsers => %s", realm));
        return getUsers(realm, -1, -1);
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm, int firstResult, int maxResults) {
        log.info(String.format("getUsers_1\n\n\trealm = %s\n\tfirstResult = #s\n\tmaxResults = %s\n\tem=%s", realm.getName(), firstResult, maxResults, em));
        List<UserModel> users = null;
        try {
            TypedQuery<UserEntity> query = em.createNamedQuery("getAllUsers", UserEntity.class);
            if (firstResult != -1) {
                query.setFirstResult(firstResult);
            }
            if (maxResults != -1) {
                query.setMaxResults(maxResults);
            }
            List<UserEntity> results = query.getResultList();
            users = new LinkedList<>();
            for (UserEntity entity : results) {
                users.add(new UserAdapter(session, realm, model, entity, em));
            }

        } catch (Exception e) {
            log.log(Logger.Level.ERROR, e);
        }
        return users;
    }

    /**
     * Метод осуществляет поиск пользователя
     *
     * @param search - строка поиска
     * @param realm - realm id
     * @return - список найденых пользователей
     */
    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm) {
        log.info(String.format("searchForUser_1 \n\tsearch = %s \n\trealm = %s", search, realm));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Метод осуществляет поиск пользователей по строке search и возвращает
     * постранично
     *
     * @param search
     * @param realm
     * @param firstResult - номер первого элемента из результата
     * @param maxResults - кол-во записей
     * @return
     */
    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm, int firstResult, int maxResults) {
        log.info(String.format("searchForUser_2 \n\tsearch = %s \n\trealm = %s \n\tfirdtResult = %s\n\tmaxResults = %s", search, realm, firstResult, maxResults));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Метод осуществляет поиск пользователей по параметрам переданым в params
     *
     * @param params
     * @param realm
     * @return
     */
    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm) {
        log.info(String.format("searchForUser_3 \n\tparams = %s \n\trealm = %s", params, realm));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param params
     * @param realm
     * @param firstResult
     * @param maxResults
     * @return
     */
    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm, int firstResult, int maxResults) {
        log.info(String.format("searchForUser_4 \n\tparams = %s \n\trealm = %s \n\tfirdtResult = %s\n\tmaxResults = %s", params, realm, firstResult, maxResults));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     * @param group
     * @param firstResult
     * @param maxResults
     * @return
     */
    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group, int firstResult, int maxResults) {
        log.info(String.format("getGroupMembers \n\trealm = %s\n\tgroup = %s \n\tfirdtResult = %s\n\tmaxResults = %s", realm, group, firstResult, maxResults));
        return Collections.EMPTY_LIST;
    }

    /**
     *
     * @param realm
     * @param group
     * @return
     */
    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group) {
        log.info(String.format("getGroupMembers_1 \n\trealm = %s\n\tgroup = %s", realm, group));
        return Collections.EMPTY_LIST;
    }

    /**
     *
     * @param attrName
     * @param attrValue
     * @param realm
     * @return
     */
    @Override
    public List<UserModel> searchForUserByUserAttribute(String attrName, String attrValue, RealmModel realm) {
        log.info(String.format("searchForUserByUserAttribute \n\tattrName = %s\n\tattrValue = %s\n\trealm = %s", attrName, attrValue, realm));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param credentialType
     * @return
     */
    @Override
    public boolean supportsCredentialType(String credentialType) {
        log.info(String.format("supportsCredentialType \n\tcredentialType = %s", credentialType));
        CredentialModel.TOTP.equals(credentialType);
        return CredentialModel.PASSWORD.equals(credentialType);
    }

    /**
     *
     * @param realm
     * @param user
     * @param input
     * @return
     */
    @Override
    public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
        log.info(String.format("updateCredential \n\trealm = %s\n\tuser = %s\n\tinput = %s", realm, user, input));
        try {
            if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) {
                return false;
            }
            UserCredentialModel cred = (UserCredentialModel) input;
            UserAdapter adapter = getUserAdapter(user);
            log.info(String.format("UserCredModel value = %s \n\tUserCredModel type = %s", cred.getValue(), cred.getType()));
            adapter.setPassword(cred.getValue());
        } catch (Exception e) {
            log.log(Logger.Level.ERROR, e);
        }
        return false;
    }

    /**
     *
     * @param realm
     * @param user
     * @param credentialType
     */
    @Override
    public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {
        log.info(String.format("disableCredentialType \n\trealm = %s \n\tuser = %s\n\t credentialType = %s", realm, user, credentialType));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     * @param user
     * @return
     */
    @Override
    public Set<String> getDisableableCredentialTypes(RealmModel realm, UserModel user) {
        log.info(String.format("getDisableableCredentialTypes \n\t realm = %s\n\t user = %s", realm, user));
        if (getUserAdapter(user).getPassword() != null) {
            log.info("\n\tpassword is not null : " + getUserAdapter(user).getPassword());
            Set<String> set = new HashSet<>();
            set.add(CredentialModel.PASSWORD);
            log.info("\n\treturn CredentialModel.PASSWORD = " + CredentialModel.PASSWORD);
            return set;
        } else {
            return Collections.emptySet();
        }
    }

    /**
     *
     * @param realm
     * @param user
     * @param credentialType
     * @return
     */
    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        log.info(String.format("isConfiguredFor \n\t realm = %s\n\t user = %s \n\tcredentialType = %s", realm, user, credentialType));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Проверка введеного пароля на правильность
     *
     * @param realm
     * @param user
     * @param input
     * @return
     */
    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        log.info(String.format("isValid \n\trealm = %s\n\tuser = %s \n\tinput = %s", realm, user, input));
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) {
            return false;
        }

        UserCredentialModel cred = (UserCredentialModel) input;
        String password = getPassword(user);
        String salt = getSalt(user);
        log.info(String.format("\n\tpassword = %s \n\tsalt = %s", password, salt));

        boolean flag = false;
        log.info(String.format("hash_type = %s", getHashType(user)));
        switch ((getHashType(user)).toLowerCase()) {
            case "md5":
                log.info("\n{"
                        + "\n\tcred device= " + cred.getDevice()
                        //+ "\n\tuserpass    = " + cred.getValue()
                        + "\n\tsalt        = " + salt
                        + "\n\tpassword    = " + password
                        + "\n\tuserpass    = " + encodeToHex(md5(cred.getValue() + salt))
                        //+ "\n\tuserpass    = " + hashUtil.md5ToString(cred.getValue() + salt)
                        + "\n}");
                flag = (password != null) && ((password).equals(encodeToHex(md5(cred.getValue() + salt))));
                //flag = (password != null) && ((password).equals(hashUtil.md5ToString(cred.getValue() + salt)));
                //log.info("res = " + flag);
                //return flag;
                break;
            case "sha1":
                log.info("\n{"
                        + "\n\tcred device= " + cred.getDevice()
                        // + "\n\tuserpass    = " + cred.getValue()
                        + "\n\tsalt        = " + salt
                        + "\n\tpassword    = " + password
                        + "\n\tuserpass    = " + encodeToHex(sha1(cred.getValue() + salt))
                        //+ "\n\tuserpass    = " + hashUtil.sha1ToString(cred.getValue() + salt)
                        + "\n}");

                flag = (password != null) && ((password).equals(encodeToHex(sha1(cred.getValue() + salt))));
                //flag = (password != null) && ((password).equals(hashUtil.sha1ToString(cred.getValue() + salt)));

                // log.info("res = " + flag);
                // return flag;
                //(password != null) && ((password).equals(encodeToHex(sha1(cred.getValue() + salt))));
                break;
            default:
                log.info("\n\tcred device= " + cred.getDevice()
                        + "\n\tpassword = " + password
                //     + "\n\tuserpass = " + cred.getValue()
                );
                flag = (password != null) && ((password).equals(cred.getValue()));
                break;
            // log.info("res = " + flag);
            // return flag;
        }
        log.info("res = " + flag);
        return flag;
    }

    /**
     *
     * @param realm
     * @param userCash
     * @param user
     */
    @Override
    public void onCache(RealmModel realm, CachedUserModel userCash, UserModel user) {
        log.info(String.format("onCache \n\t realm = %s\n\t userCash = %s \n\tuser = %s", realm, userCash, user));
        String password = ((UserAdapter) user).getPassword();
        String salt = ((UserAdapter) user).getSalt();
        String hash_type = ((UserAdapter) user).getHashType();
        log.info(String.format("PASSWORD_CACHE_KEY = %s \n\tPASSWORD = %s", PASSWORD_CACHE_KEY, password));
        if (password != null) {
            log.info(String.format("Add PASSWORD in CACHE password = %s", password));
            userCash.getCachedWith().put(PASSWORD_CACHE_KEY, password);
        }

        log.info(String.format("SALT_CACHE_KEY = %s \n\tsalt = %s", SALT_CACHE_KEY, salt));
        if (salt != null) {
            log.info(String.format("Add SALT in CASHE salt = %s", salt));
            userCash.getCachedWith().put(SALT_CACHE_KEY, salt);
        }

        log.info(String.format("HASH_TYPE_CACHE_KEY = %s \n\thash_type = %s", HASH_TYPE_CACHE_KEY, hash_type));
        if (hash_type != null) {
            log.info(String.format("Add HASH_TYPE in CASHE hash_type = %s", hash_type));
            userCash.getCachedWith().put(HASH_TYPE_CACHE_KEY, hash_type);
        }
    }

    /**
     *
     * @param realm
     * @param firstResult
     * @param maxResults
     * @return
     */
    @Override
    public List<String> getStoredUsers(RealmModel realm, int firstResult, int maxResults) {
        log.info(String.format("getStoredUsers \n\t realm = %s\n\t firstResult = %s \n\tmaxResults = %s", realm, firstResult, maxResults));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     * @return
     */
    @Override
    public int getStoredUsersCount(RealmModel realm) {
        log.info(String.format("getStoredUsersCount \n\t realm = %s", realm));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     */
    @Override
    public void preRemove(RealmModel realm) {
        log.info(String.format("preRemove \n\t realm = %s", realm));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     * @param group
     */
    @Override
    public void preRemove(RealmModel realm, GroupModel group) {
        log.info(String.format("preRemove_1 \n\t realm = %s \n\tgroup = %s", realm, group));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     * @param role
     */
    @Override
    public void preRemove(RealmModel realm, RoleModel role) {
        log.info(String.format("preRemove_2 \n\t realm = %s \n\trole = %s", realm, role));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     * @param client
     */
    @Override
    public void preRemove(RealmModel realm, ClientModel client) {
        log.info(String.format("preRemove_2 \n\t realm = %s \n\tclient = %s", realm, client));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param protocolMapper
     */
    @Override
    public void preRemove(ProtocolMapperModel protocolMapper) {
        log.info(String.format("preRemove_3 \n\tprotocolMapper = %s", protocolMapper));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     * @param user
     */
    @Override
    public void preRemove(RealmModel realm, UserModel user) {
        log.info(String.format("preRemove_4 \n\t realm = %s \n\tuser = %s", realm, user));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     * @param component
     */
    @Override
    public void preRemove(RealmModel realm, ComponentModel component) {
        log.info(String.format("preRemove_5 \n\t realm = %s \n\tcomponent = %s", realm, component));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     * @param string
     * @param string1
     * @param string2
     */
    @Override
    public void setSingleAttribute(RealmModel realm, String string, String string1, String string2) {
        log.info(String.format("setSingleAttribute \n\t realm = %s \n\tstring = %s \n\tstring1 = %s \n\tstring2 = %s", realm, string, string1, string2));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     * @param string
     * @param string1
     * @param list
     */
    @Override
    public void setAttribute(RealmModel realm, String string, String string1, List<String> list) {
        log.info(String.format("setAttribute \n\t realm = %s \n\tstring = %s \n\tstring1 = %s \n\tlist = %s", realm, string, string1, list.size()));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     * @param string
     * @param string1
     */
    @Override
    public void removeAttribute(RealmModel realm, String string, String string1) {
        log.info(String.format("removeAttribute \n\t realm = %s \n\tstring = %s \n\tstring1 = %s", realm, string, string1));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     * @param string
     * @return
     */
    @Override
    public MultivaluedHashMap<String, String> getAttributes(RealmModel realm, String string) {
        log.info(String.format("getAttributes \n\t realm = %s \n\tstring = %s", realm, string));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     * @param string
     * @param string1
     * @return
     */
    @Override
    public List<String> getUsersByUserAttribute(RealmModel realm, String string, String string1) {
        log.info(String.format("getUsersByUserAttribute \n\t realm = %s \n\tstring = %s \n\tstring1 = %s", realm, string, string1));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param fim
     * @param realm
     * @return
     */
    @Override
    public String getUserByFederatedIdentity(FederatedIdentityModel fim, RealmModel realm) {
        log.info(String.format("getUserByFederatedIdentity \n\tfim = %s \n\trealm = %s", fim, realm));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     * @param string
     * @param fim
     */
    @Override
    public void addFederatedIdentity(RealmModel realm, String string, FederatedIdentityModel fim) {
        log.info(String.format("addFederatedIdentity \n\trealm = %s \n\tfim = %s", realm, fim));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     * @param string
     * @param string1
     * @return
     */
    @Override
    public boolean removeFederatedIdentity(RealmModel realm, String string, String string1) {
        log.info(String.format("removeFederatedIdentity \n\trealm = %s \n\tstring = %s \n\tstring1 = %s", realm, string, string1));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     * @param string
     * @param fim
     */
    @Override
    public void updateFederatedIdentity(RealmModel realm, String string, FederatedIdentityModel fim) {
        log.info(String.format("updateFederatedIdentity \n\trealm = %s \n\tstring = %s \n\tfim = %s", realm, string, fim));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param string
     * @param realm
     * @return
     */
    @Override
    public Set<FederatedIdentityModel> getFederatedIdentities(String string, RealmModel realm) {
        log.info(String.format("getFederatedIdentities \n\tstring = %s \n\trealm = %s", string, realm));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param string
     * @param string1
     * @param realm
     * @return
     */
    @Override
    public FederatedIdentityModel getFederatedIdentity(String string, String string1, RealmModel realm) {
        log.info(String.format("getFederatedIdentity \n\tstring = %s \n\tstring1 = %s \n\trealm = %s", string, string1, realm));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     * @param string
     * @param ucm
     */
    @Override
    public void addConsent(RealmModel realm, String string, UserConsentModel ucm) {
        log.info(String.format("addConsent \n\trealm = %s \n\tstring = %s \n\tucm = %s", realm, string, ucm));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     * @param string
     * @param string1
     * @return
     */
    @Override
    public UserConsentModel getConsentByClient(RealmModel realm, String string, String string1) {
        log.info(String.format("getConsentByClient \n\trealm = %s \n\tstring = %s \n\tstring1 = %s", realm, string, string1));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     * @param string
     * @return
     */
    @Override
    public List<UserConsentModel> getConsents(RealmModel realm, String string) {
        log.info(String.format("getConsents \n\trealm = %s \n\tstring = %s", realm, string));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     * @param string
     * @param ucm
     */
    @Override
    public void updateConsent(RealmModel realm, String string, UserConsentModel ucm) {
        log.info(String.format("updateConsent \n\trealm = %s \n\tstring = %s \n\tucm = %s", realm, string, ucm));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     * @param string
     * @param string1
     * @return
     */
    @Override
    public boolean revokeConsentForClient(RealmModel realm, String string, String string1) {
        log.info(String.format("revokeConsentForClient \n\trealm = %s \n\tstring = %s \n\tstring1 = %s", realm, string, string1));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     * @param string
     * @return
     */
    @Override
    public Set<GroupModel> getGroups(RealmModel realm, String string) {
        log.info(String.format("getGroups \n\trealm = %s \n\tstring = %s", realm, string));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     * @param string
     * @param group
     */
    @Override
    public void joinGroup(RealmModel realm, String string, GroupModel group) {
        log.info(String.format("joinGroup \n\trealm = %s \n\tstring = %s \n\tgroup = %s", realm, string, group));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     * @param string
     * @param group
     */
    @Override
    public void leaveGroup(RealmModel realm, String string, GroupModel group) {
        log.info(String.format("leaveGroup \n\trealm = %s \n\tstring = %s \n\tgroup = %s", realm, string, group));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     * @param group
     * @param firstResult
     * @param maxResults
     * @return
     */
    @Override
    public List<String> getMembership(RealmModel realm, GroupModel group, int firstResult, int maxResults) {
        log.info(String.format("getMembership \n\trealm = %s \n\tgroup = %s \n\tfirstResult = %s \n\tmaxResults = %s", realm, group, firstResult, maxResults));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     * @param string
     * @return
     */
    @Override
    public Set<String> getRequiredActions(RealmModel realm, String string) {
        log.info(String.format("getRequiredActions \n\trealm = %s \n\tstring = %s", realm, string));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     * @param string
     * @param string1
     */
    @Override
    public void addRequiredAction(RealmModel realm, String string, String string1) {
        log.info(String.format("addRequiredAction \n\trealm = %s \n\tstring = %s \n\tstring1 = %s", realm, string, string1));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     * @param string
     * @param string1
     */
    @Override
    public void removeRequiredAction(RealmModel realm, String string, String string1) {
        log.info(String.format("removeRequiredAction \n\trealm = %s \n\tstring = %s \n\tstring1 = %s", realm, string, string1));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     * @param string
     * @param rm1
     */
    @Override
    public void grantRole(RealmModel realm, String string, RoleModel rm1) {
        log.info(String.format("grantRole \n\trealm = %s \n\tstring = %s \n\trml = %s", realm, string, rm1));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     * @param string
     * @return
     */
    @Override
    public Set<RoleModel> getRoleMappings(RealmModel realm, String string) {
        log.info(String.format("getRoleMappings \n\trealm = %s \n\tstring = %s", realm, string));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     * @param string
     * @param rm1
     */
    @Override
    public void deleteRoleMapping(RealmModel realm, String string, RoleModel rm1) {
        log.info(String.format("deleteRoleMapping \n\trealm = %s \n\tstring = %s \n\trml = %s", realm, string, rm1));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     * @param string
     * @param cm
     */
    @Override
    public void updateCredential(RealmModel realm, String string, CredentialModel cm) {
        log.info(String.format("updateCredential_1 \n\trealm = %s \n\tstring = %s \n\tcm = %s", realm, string, cm));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     * @param string
     * @param cm
     * @return
     */
    @Override
    public CredentialModel createCredential(RealmModel realm, String string, CredentialModel cm) {
        log.info(String.format("createCredential \n\trealm = %s \n\tstring = %s \n\tcm = %s", realm, string, cm));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     * @param string
     * @param string1
     * @return
     */
    @Override
    public boolean removeStoredCredential(RealmModel realm, String string, String string1) {
        log.info(String.format("removeStoredCredential \n\trealm = %s \n\tstring = %s \n\tstring1 = %s", realm, string, string1));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param realm
     * @param string
     * @param string1
     * @return
     */
    @Override
    public CredentialModel getStoredCredentialById(RealmModel realm, String string, String string1) {
        log.info(String.format("getStoredCredentialById \n\trealm = %s \n\tstring = %s \n\tstring1 = %s", realm, string, string1));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<CredentialModel> getStoredCredentials(RealmModel realm, String string) {
        log.info(String.format("getStoredCredentials \n\trealm = %s \n\tstring = %s", realm, string));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<CredentialModel> getStoredCredentialsByType(RealmModel realm, String string, String string1) {
        log.info(String.format("getStoredCredentialsByType \n\trealm = %s \n\tstring = %s \n\tstring1 = %s", realm, string, string1));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CredentialModel getStoredCredentialByNameAndType(RealmModel realm, String string, String string1, String string2) {
        log.info(String.format("getStoredCredentialByNameAndType \n\trealm = %s \n\tstring = %s \n\tstring1 = %s \n\tstring2 = %s", realm, string, string1, string2));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ComponentModel getModel() {
        return model;
    }

    public void setModel(ComponentModel model) {
        this.model = model;
    }

    public KeycloakSession getSession() {
        return session;
    }

    public void setSession(KeycloakSession session) {
        this.session = session;
    }

    /**
     * Получает пользоателя. Если пользователь есть в кеше то берет из кеша
     *
     * @param user
     * @return
     */
    public UserAdapter getUserAdapter(UserModel user) {
        log.info(String.format("getUserAdapter \n\tuser = %s", user));
        UserAdapter adapter = null;
        if (user instanceof CachedUserModel) {
            log.info("User in cache Keycloak");
            adapter = (UserAdapter) ((CachedUserModel) user).getDelegateForUpdate();
        } else {
            log.info("User not in cache Keycloak");
            adapter = (UserAdapter) user;
        }
        return adapter;
    }

    /**
     * Функция получает значение пароля из БД или из Кеша в зависимости есть ли
     * пользователь в кеше
     *
     * @param user
     * @return
     */
    private String getPassword(UserModel user) {
        log.info(String.format("getPassword \n\tuser = %s", user));
        log.info(String.format("Class type user = %s", user.getClass().getName()));
        String password = null;
        if (user instanceof CachedUserModel) {
            log.info("User in cache");
            password = (String) ((CachedUserModel) user).getCachedWith().get(PASSWORD_CACHE_KEY);
        } else if (user instanceof UserAdapter) {
            log.info("User not in cache");
            password = ((UserAdapter) user).getPassword();
        }
        log.info(String.format("password => ", password));
        return password;
    }

    /**
     * Получает значение salt
     *
     * @param user
     * @return
     */
    private String getSalt(UserModel user) {
        log.info(String.format("getSalt \n\tuser = %s", user));
        log.info(String.format("Class type user = %s", user.getClass().getName()));
        String salt = null;
        if (user instanceof CachedUserModel) {
            log.info("User in cache");
            salt = (String) ((CachedUserModel) user).getCachedWith().get(SALT_CACHE_KEY);
        } else if (user instanceof UserAdapter) {
            log.info("User not in cache");
            salt = ((UserAdapter) user).getSalt();
        }
        log.info(String.format("salt = %s", salt));
        return salt;
    }

    /**
     * Функция возвращает тип hesh пароля
     *
     * @param user
     * @return
     */
    private String getHashType(UserModel user) {
        log.info(String.format("getHashType \n\tid = %s", user.getId()));
        String res = null;
        if (user instanceof CachedUserModel) {
            log.info("User in cache");
            res = (String) ((CachedUserModel) user).getCachedWith().get(HASH_TYPE_CACHE_KEY);
        } else if (user instanceof UserAdapter) {
            log.info("User not in cache");
            res = user.getFirstAttribute("hash_type");
        }
        log.info(String.format("getHashType \n\tres = %s", res));
        return res;
    }
}
