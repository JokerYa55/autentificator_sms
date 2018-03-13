/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sso_b2b.user.registry.beans;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author vasil
 */
@Entity
@Table(name = "t_registry_individual", schema = "user_registry")
public class Individual implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)    
    private Long id;
    @Column(name = "first_name", length = 100, nullable = false)
    private String firdtName;
    @Column(name = "last_name", length = 100, nullable = false)
    private String lastName;
    @Column(name = "third_name", length = 100, nullable = false)
    private String thirdName;
    @Column(name = "pasport_num", length = 11, nullable = true)
    private String pasport_num;
    @Column(name = "inn_num", length = 20, nullable = true)
    private String inn_num;
    @Column(name = "pfr_num", length = 20, nullable = true)
    private String pfr_num;
    @Column(name = "sso_id", length = 20, nullable = true)
    private Long sso_id;
    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Individual)) {
            return false;
        }
        Individual other = (Individual) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "sso_b2b.user.registry.beans.Individual[ id=" + id + " ]";
    }

    public String getFirdtName() {
        return firdtName;
    }

    public void setFirdtName(String firdtName) {
        this.firdtName = firdtName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getThirdName() {
        return thirdName;
    }

    public void setThirdName(String thirdName) {
        this.thirdName = thirdName;
    }

    public String getPasport_num() {
        return pasport_num;
    }

    public void setPasport_num(String pasport_num) {
        this.pasport_num = pasport_num;
    }

    public String getInn_num() {
        return inn_num;
    }

    public void setInn_num(String inn_num) {
        this.inn_num = inn_num;
    }

    public String getPfr_num() {
        return pfr_num;
    }

    public void setPfr_num(String pfr_num) {
        this.pfr_num = pfr_num;
    }
    
}
