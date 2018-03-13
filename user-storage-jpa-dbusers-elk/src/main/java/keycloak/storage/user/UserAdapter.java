package keycloak.storage.user;

import java.util.Collection;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import keycloak.bean.UserAttribute;
import keycloak.bean.UserEntity;
import static keycloak.storage.util.hashUtil.encodeToHex;
import static keycloak.storage.util.hashUtil.genSalt;
import static keycloak.storage.util.hashUtil.sha1;
import org.keycloak.models.GroupModel;

/**
 * Класс для представления пользователя внутри Keycloak
 *
 * @version 1
 * @author Vasiliy Andritsov
 *
 */
public class UserAdapter extends AbstractUserAdapterFederatedStorage {

    public static final String SECRET_QUESTION = "SECRET_QUESTION";
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
        return entity.getUsername();
    }

    @Override
    public void setUsername(String username) {
        entity.setUsername(username);
    }

    /**
     * Возвращает строку записаную в БД в поле password
     *
     * @return - возвращает пароль сохраненный в БД
     */
    public String getPassword() {
        log.debug(String.format("getPassword = %s", entity.getPassword()));
        return entity.getPassword();
    }

    /**
     * Записывает hash пароля и незашифрованный пароль в БД
     *
     * @param password - пароль пользователя в незашифрованом виде
     */
    public void setPassword(String password) {
        log.debug("UserAdapter  setPassword => " + password);
        String salt = genSalt();
        //encodeToHex(UUID.randomUUID().toString().getBytes());
        log.debug("salt => " + password);
        entity.setPassword(encodeToHex(sha1(password + salt)));
        //entity.setPassword(sha1ToString(password + salt));
        log.debug("password => " + entity.getPassword());
        entity.setHash_type("sha1");
        entity.setSalt(salt);
        //entity.setPassword_not_hash(password);
    }

    /**
     *
     * @param hash
     */
    public void setHash(String hash) {
        log.info("setHash => " + hash);
        entity.setHash(hash);
    }

    /**
     * Получает значение для Salt из БД
     *
     * @return
     */
    public String getSalt() {
        return entity.getSalt();
    }

    /**
     * Устанавливает значение для Salt
     *
     * @param salt
     */
    public void setSalt(String salt) {
        entity.setSalt(salt);
    }

    /**
     *
     * @return
     */
    @Override
    public String getEmail() {
        return entity.getEmail();
    }

    /**
     *
     * @param email
     */
    @Override
    public void setEmail(String email) {
        entity.setEmail(email);
    }

    @Override
    public String getLastName() {
        return entity.getLastName();
    }

    /**
     *
     * @param lastName
     */
    @Override
    public void setLastName(String lastName) {
        entity.setLastName(lastName);
    }

    /**
     *
     * @return
     */
    @Override
    public String getFirstName() {
        return entity.getFirstName();
    }

    /**
     *
     * @param firstName
     */
    @Override
    public void setFirstName(String firstName) {
        entity.setFirstName(firstName);
    }

    /**
     *
     * @return
     */
    @Override
    public String getId() {
        return keycloakId;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isEnabled() {
        return entity.isEnabled();
    }

    /**
     *
     * @param enabled
     */
    @Override
    public void setEnabled(boolean enabled) {
        entity.setEnabled(enabled);
    }

    /**
     *
     * @param verified
     */
    @Override
    public void setEmailVerified(boolean verified) {
        entity.setEmail_verified(verified);
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isEmailVerified() {
        return entity.isEmail_verified();
    }

    /**
     *
     * @param name
     * @return
     */
    @Override
    public List<String> getAttribute(String name) {
        log.info(String.format("getAttribute name = %s", name));
        List<String> res = new LinkedList<>();

        if (name.contains("id_app_")) {
            Collection<UserAttribute> attrList = entity.getUserAttributeCollection();
            if (attrList != null) {
                attrList.forEach((t) -> {
                    if (t.isVisible_flag()) {
                        if (name.equals(t.getName())) {
                            res.add(t.getValue());
                        }
                    }
                });
            }
        } else {
            switch (name) {
                case "phone":
                    if (entity.getPhone() != null) {
                        res.add(entity.getPhone());
                    } else {
                        res.add("");
                    }
                    break;
                case "thirdName":
                    if (entity.getThirdName() != null) {
                        res.add(entity.getThirdName());
                    } else {
                        res.add("");
                    }
                    break;
                case "region":
                    if (entity.getUser_region() != null) {
                        res.add(entity.getUser_region().toString());
                    } else {
                        res.add("");
                    }
                    break;

                case "hash_type":
                    if (entity.getHash_type() != null) {
                        res.add(entity.getHash_type());
                    } else {
                        res.add("");
                    }
                    break;
                case "EMAIL_VERIFIED":
                    if (entity.isEmail_verified()) {
                        res.add("true");
                    } else {
                        res.add("false");
                    }
                    break;
                default:
                    return super.getAttribute(name);
            }
        }

        return res;
    }

    /**
     *
     * @param name
     * @param values
     */
    @Override
    public void setAttribute(String name, List<String> values) {
        log.info(String.format("setAttribute \n\tname = %s\n\tvalues = %s", name, values));
        UserAttribute attrib;
        //Collection<UserAttribute> attrList = entity.getUserAttributeCollection();
        if ((values.get(0) != null) && (values.get(0).length() > 0)) {
            Pattern p = Pattern.compile("^id_app_[0-9]+$");;
            Matcher m = p.matcher(name);
            if (m.matches()) {
                // Вставляем настраиваемые аттрибуты                        
                attrib = new UserAttribute(name, values.get(0), entity, true);
                entity.addUserAttribute(attrib);
            } else {
                // Вставляем аттрибуты хранящиеся в основной таблице
                switch (name) {
                    case "phone":
                        entity.setPhone(values.get(0));
                        break;
                    case "hash":
                        entity.setHash(values.get(0));
                        break;
                    case "salt":
                        entity.setSalt(values.get(0));
                        break;
                    case "hash_type":
                        entity.setHash_type(values.get(0));
                        break;
                    case "thirdName":
                        entity.setThirdName(values.get(0));
                        break;
                    case "firstName":
                        entity.setFirstName(values.get(0));
                        break;
                    case "lastName":
                        entity.setLastName(values.get(0));
                        break;
                    case "region":
                        entity.setUser_region(new Integer(values.get(0)));
                        break;
                    case "description":
                        entity.setDescription(values.get(0));
                        break;
                    case "user_status":
                        entity.setUser_status(new Integer(values.get(0)));
                        break;
                    case "EMAIL_VERIFIED":
                        entity.setEmail_verified(Boolean.parseBoolean(values.get(0)));
                        break;
                    case SECRET_QUESTION:
                        entity.setSecret_question(values.get(0));
                        break;
                    default:
                        super.setAttribute(name, values);
                        break;
                }
            }
            p = null;
            m = null;
        }
    }

    /**
     *
     * @param name
     * @return
     */
    @Override
    public String getFirstAttribute(String name) {
        log.info(String.format("getFirstAttribute \n\tname = %s", name));
        switch (name) {
            case "phone":
                return entity.getPhone();
            case "region":
                return entity.getUser_region().toString();
            case "salt":
                return entity.getSalt();
            case "hash_type":
                return entity.getHash_type();
            case "thirdName":
                return entity.getThirdName();
            case "user_status":
                return entity.getUser_status().toString();
            case SECRET_QUESTION:
                return entity.getSecret_question();
            default:
                return super.getFirstAttribute(name);
        }
    }

    /**
     *
     * @param name
     * @param value
     */
    @Override
    public void setSingleAttribute(String name, String value) {
        switch (name) {
                    case "phone":
                        entity.setPhone(value);
                        break;
                    case "hash":
                        entity.setHash(value);
                        break;
                    case "salt":
                        entity.setSalt(value);
                        break;
                    case "hash_type":
                        entity.setHash_type(value);
                        break;
                    case "thirdName":
                        entity.setThirdName(value);
                        break;
                    case "firstName":
                        entity.setFirstName(value);
                        break;
                    case "lastName":
                        entity.setLastName(value);
                        break;
                    case "region":
                        entity.setUser_region(new Integer(value));
                        break;
                    case "description":
                        entity.setDescription(value);
                        break;
                    case "user_status":
                        entity.setUser_status(new Integer(value));
                        break;
                    case "EMAIL_VERIFIED":
                        entity.setEmail_verified(Boolean.parseBoolean(value));
                        break;
                    case SECRET_QUESTION:
                        entity.setSecret_question(value);
                        break;
                    default:
                        super.setSingleAttribute(name, value);
                        break;
                }
    }

    
    
    public String getHashType() {
        return entity.getHash_type();
    }
    
    public String getSecretQuestions(){
        return entity.getSecret_question();
    }
}
