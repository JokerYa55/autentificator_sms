/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keycloak.bean;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author vasil
 */
@Entity
@Table(name = "t_users_log")
@NamedQueries({
    @NamedQuery(name = "TUsersLog.findAll", query = "SELECT t FROM UsersLog t where t.flag = false and t.send_count<=:send_count")
    , @NamedQuery(name = "TUsersLog.findById", query = "SELECT t FROM UsersLog t WHERE t.id = :id and t.flag = false and t.send_count<=:send_count")
    , @NamedQuery(name = "TUsersLog.findByFlag", query = "SELECT t FROM UsersLog t WHERE t.flag = :flag and t.send_count<=:send_count")
    , @NamedQuery(name = "TUsersLog.findByNoSend", query = "SELECT t FROM UsersLog t WHERE t.flag = false and t.send_count<=10")
    , @NamedQuery(name = "TUsersLog.findByOperType", query = "SELECT t FROM UsersLog t WHERE t.operType = :operType and t.flag = false and t.send_count<=:send_count")
    , @NamedQuery(name = "TUsersLog.findByUserId", query = "SELECT t FROM UsersLog t WHERE t.userId = :userId and t.flag = false and t.send_count<=:send_count")
    , @NamedQuery(name = "TUsersLog.findByUsername", query = "SELECT t FROM UsersLog t WHERE t.username = :username and t.flag = false and t.send_count<=:send_count")
    , @NamedQuery(name = "TUsersLog.findByDateOper", query = "SELECT t FROM UsersLog t WHERE t.dateOper = :dateOper and t.flag = false and t.send_count<=:send_count")})
public class UsersLog implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "t_users_log_id_seq")
    @SequenceGenerator(name = "t_users_log_id_seq", sequenceName = "t_users_log_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @Column(name = "flag", columnDefinition = "boolean DEFAULT FALSE")
    private boolean flag;
    @Column(name = "oper_type")
    private String operType;
    @Basic(optional = false)
    @Column(name = "user_id")
    private Long userId;
    @Basic(optional = false)
    @Column(name = "username")
    private String username;
    @Basic(optional = false)
    @Column(name = "date_oper", columnDefinition = "timestamp without time zone DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOper;
    @Column(name = "send_count", nullable = false, columnDefinition = "integer DEFAULT 0")
    private Integer send_count;
    @Column(name = "info", nullable = true, columnDefinition = "text")
    private String info;
    @Column(name = "last_command", nullable = true, columnDefinition = "text")
    private String last_command;

    public UsersLog() {
    }

    public UsersLog(Long id) {
        this.id = id;
    }

    public UsersLog(Long id, boolean flag, Long userId, String username, Date dateOper) {
        this.id = id;
        this.flag = flag;
        this.userId = userId;
        this.username = username;
        this.dateOper = dateOper;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getOperType() {
        return operType;
    }

    public void setOperType(String operType) {
        this.operType = operType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getDateOper() {
        return dateOper;
    }

    public void setDateOper(Date dateOper) {
        this.dateOper = dateOper;
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
        if (!(object instanceof UsersLog)) {
            return false;
        }
        UsersLog other = (UsersLog) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public Integer getSend_count() {
        return send_count;
    }

    public void setSend_count(Integer send_count) {
        this.send_count = send_count;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getLast_command() {
        return last_command;
    }

    public void setLast_command(String last_command) {
        this.last_command = last_command;
    }

    @Override
    public String toString() {
        return "TUsersLog{" + "id=" + id + ", flag=" + flag + ", operType=" + operType + ", userId=" + userId + ", username=" + username + ", dateOper=" + dateOper + ", send_count=" + send_count + ", info=" + info + ", last_command=" + last_command + '}';
    }
}
