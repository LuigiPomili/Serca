/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibo.cs.swarch.serca.ejb.mappedentity.users;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author tappof
 */
@Entity
@Table(name = "grouptable")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UserGroups.findAll", query = "SELECT u FROM UserGroups u"),
    @NamedQuery(name = "UserGroups.findByUserid", query = "SELECT u FROM UserGroups u WHERE u.userGroupsPK.userid = :userid"),
    @NamedQuery(name = "UserGroups.findByGroupid", query = "SELECT u FROM UserGroups u WHERE u.userGroupsPK.groupid = :groupid")})
public class UserGroups implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected UserGroupsPK userGroupsPK;

    public UserGroups() {
    }

    public UserGroups(UserGroupsPK userGroupsPK) {
        this.userGroupsPK = userGroupsPK;
    }

    public UserGroups(String userid, String groupid) {
        this.userGroupsPK = new UserGroupsPK(userid, groupid);
    }

    public UserGroupsPK getUserGroupsPK() {
        return userGroupsPK;
    }

    public void setUserGroupsPK(UserGroupsPK userGroupsPK) {
        this.userGroupsPK = userGroupsPK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userGroupsPK != null ? userGroupsPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserGroups)) {
            return false;
        }
        UserGroups other = (UserGroups) object;
        if ((this.userGroupsPK == null && other.userGroupsPK != null) || (this.userGroupsPK != null && !this.userGroupsPK.equals(other.userGroupsPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.unibo.cs.swarch.serca.ejb.mappedentity.users.UserGroups[ userGroupsPK=" + userGroupsPK + " ]";
    }
    
}
