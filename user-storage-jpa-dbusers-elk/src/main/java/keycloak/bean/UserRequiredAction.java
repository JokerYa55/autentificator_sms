/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keycloak.bean;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vasil
 */
@Entity
@Table(name = "t_user_required_action")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UserRequiredAction.findAll", query = "SELECT t FROM UserRequiredAction t")
    , @NamedQuery(name = "UserRequiredAction.findByRequiredAction", query = "SELECT t FROM UserRequiredAction t WHERE t.tUserRequiredActionPK.requiredAction = :requiredAction")
    , @NamedQuery(name = "UserRequiredAction.findByUserId", query = "SELECT t FROM UserRequiredAction t WHERE t.tUserRequiredActionPK.userId = :userId")})
public class UserRequiredAction implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected UserRequiredActionPK tUserRequiredActionPK;
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private UserEntity tUsers;

    public UserRequiredAction() {
    }

    public UserRequiredAction(UserRequiredActionPK tUserRequiredActionPK) {
        this.tUserRequiredActionPK = tUserRequiredActionPK;
    }

    public UserRequiredAction(String requiredAction, long userId) {
        this.tUserRequiredActionPK = new UserRequiredActionPK(requiredAction, userId);
    }

    public UserRequiredActionPK getTUserRequiredActionPK() {
        return tUserRequiredActionPK;
    }

    public void setTUserRequiredActionPK(UserRequiredActionPK tUserRequiredActionPK) {
        this.tUserRequiredActionPK = tUserRequiredActionPK;
    }

    public UserEntity gettUsers() {
        return tUsers;
    }

    public void settUsers(UserEntity tUsers) {
        this.tUsers = tUsers;
    }

   

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (tUserRequiredActionPK != null ? tUserRequiredActionPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserRequiredAction)) {
            return false;
        }
        UserRequiredAction other = (UserRequiredAction) object;
        if ((this.tUserRequiredActionPK == null && other.tUserRequiredActionPK != null) || (this.tUserRequiredActionPK != null && !this.tUserRequiredActionPK.equals(other.tUserRequiredActionPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ru.sso.mavenproject1.TUserRequiredAction[ tUserRequiredActionPK=" + tUserRequiredActionPK + " ]";
    }
    
}
