/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibo.cs.swarch.serca.ejb.mappedentity.users;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author tappof
 */
@Entity
@Table(name = "usertable")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
    @NamedQuery(name = "User.purifyByStatus", query = "SELECT u FROM User u WHERE u.status <> :status"),
    @NamedQuery(name = "User.findByUserid", query = "SELECT u FROM User u WHERE u.userid = :userid"),
    @NamedQuery(name = "User.findByPwd", query = "SELECT u FROM User u WHERE u.pwd = :pwd"),
    @NamedQuery(name = "User.findByName", query = "SELECT u FROM User u WHERE u.name = :name"),
    @NamedQuery(name = "User.findBySurname", query = "SELECT u FROM User u WHERE u.surname = :surname"),
    @NamedQuery(name = "User.findByMail", query = "SELECT u FROM User u WHERE u.mail = :mail"),
    @NamedQuery(name = "User.findByScore", query = "SELECT u FROM User u WHERE u.score = :score"),
    @NamedQuery(name = "User.loggedInRating", query = "SELECT u FROM User u WHERE u.status <> :status ORDER BY u.score")})
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "userid")
    private String userid;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "pwd")
    private String pwd;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "surname")
    private String surname;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "mail")
    private String mail;
    @Basic(optional = false)
    @NotNull
    @Column(name = "score")
    private int score;
    @Basic(optional = false)
    @NotNull
    @Column(name = "status")
    private String status;
    
    public static enum userStatus {
        UNREGISTERED, NOT_LOGGED_IN, LOGGED_IN, TABLECREATOR, PLAYER, OBSERVER, TABLECREATOR_WAITING, TABLECREATOR_ON_TURN, PLAYER_WAITING, OBSERVER_WAITING, PLAYER_ON_TURN , BOT
    }                                                                           
    
    public userStatus getStatus() {
        return userStatus.valueOf(status);
    }

    public void setStatus(userStatus status) {
        System.out.println("status of user " + this.getUserid() + " updated to " + status.toString());
        this.status = status.toString();
    }

    public User() {
    }

    public User(String userid) {
        this.userid = userid;
    }

    public User(String userid, String pwd, String name, String surname, String mail, int score) {
        this.userid = userid;
        this.pwd = pwd;
        this.name = name;
        this.surname = surname;
        this.mail = mail;
        this.score = score;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userid != null ? userid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.userid == null && other.userid != null) || (this.userid != null && !this.userid.equals(other.userid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.unibo.cs.swarch.serca.ejb.mappedentity.users.User[ userid=" + userid + " ]";
    }
    
}
