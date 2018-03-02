/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keycloak.bean;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author vasil
 */
@Embeddable
public class UserRequiredActionPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "required_action")
    private String requiredAction;
    @Basic(optional = false)
    @Column(name = "user_id")
    private long userId;

    public UserRequiredActionPK() {
    }

    public UserRequiredActionPK(String requiredAction, long userId) {
        this.requiredAction = requiredAction;
        this.userId = userId;
    }

    public String getRequiredAction() {
        return requiredAction;
    }

    public void setRequiredAction(String requiredAction) {
        this.requiredAction = requiredAction;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (requiredAction != null ? requiredAction.hashCode() : 0);
        hash += (int) userId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserRequiredActionPK)) {
            return false;
        }
        UserRequiredActionPK other = (UserRequiredActionPK) object;
        if ((this.requiredAction == null && other.requiredAction != null) || (this.requiredAction != null && !this.requiredAction.equals(other.requiredAction))) {
            return false;
        }
        if (this.userId != other.userId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ru.sso.mavenproject1.TUserRequiredActionPK[ requiredAction=" + requiredAction + ", userId=" + userId + " ]";
    }
    
}
