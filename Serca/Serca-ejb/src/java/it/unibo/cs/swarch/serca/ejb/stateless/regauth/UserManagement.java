/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibo.cs.swarch.serca.ejb.stateless.regauth;

import it.unibo.cs.swarch.serca.ejb.mappedentity.users.User;
import it.unibo.cs.swarch.serca.ejb.mappedentity.users.UserGroups;
import it.unibo.cs.swarch.serca.ejb.tablemanagement.singleton.TablesManagerLocal;
import it.unibo.cs.swarch.serca.protocol.jaxb.Login;
import javax.ejb.Stateless;
import it.unibo.cs.swarch.serca.protocol.jaxb.Registration;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author tappof
 */
@Stateless
public class UserManagement implements UserManagementLocal {

    @EJB
    TablesManagerLocal tablesManager;
    @PersistenceContext(unitName = "Serca-ejbPU")
    private EntityManager em;
    Logger logger;

    @PostConstruct
    public void initialize() {
        logger = Logger.getLogger(this.getClass().getSimpleName());
    }

    @Override
    public boolean registration(Registration newCredentials) {

        try {
            if (findUserByUid(newCredentials.getUid()) == null) {
                User newUser = new User(newCredentials.getUid(),
                        newCredentials.getPwd(),
                        newCredentials.getName(),
                        newCredentials.getSurname(),
                        newCredentials.getMail(),
                        0);
                newUser.setStatus(User.userStatus.NOT_LOGGED_IN);
                UserGroups newUserGroup = new UserGroups(newCredentials.getUid(), "user");

                persist(newUser);
                persist(newUserGroup);
                return true;

            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public authenticationResult authentication(Login creadentials) {

        logger.info("Authentication requested for " + creadentials.getUid());
        User user = findUserByUid(creadentials.getUid());

        //utente inesistente
        if (user == null) {
            return authenticationResult.INVALID_USER_OR_PASSWORD;
        }

        if (user.getPwd().equals(creadentials.getPwd())) {
            //tablesManager.setNeedsToUpdate(true);
            switch (user.getStatus()) {
                //nessun problema
                case NOT_LOGGED_IN:
                    user.setStatus(User.userStatus.LOGGED_IN);
                    em.flush();
                    return authenticationResult.LOGGED_IN;
                //utente già loggato
                default:
                    return authenticationResult.ALREADY_LOGGED_IN;
            }
        } else {
            //password errata
            return authenticationResult.INVALID_USER_OR_PASSWORD;
        }
    }

    @Override
    public void logout(String uid) {

        findUserByUid(uid).setStatus(User.userStatus.NOT_LOGGED_IN);
        //tablesManager.setNeedsToUpdate(true);
        em.flush();
    }

    @Override
    public User.userStatus getUserStatus(String uid) {
        User user = findUserByUid(uid);
        if (user != null) {
            return user.getStatus();
        } else {
            //l'utente non esiste, utile per distinguere i bot
            return User.userStatus.UNREGISTERED;
        }
    }

    @Override
    public User findUserByUid(String uid) {
        User u = em.find(User.class, uid);
        if (u != null) {
            em.refresh(u);
        }
        return u;
    }

    private void persist(Object object) {
        em.persist(object);
    }

    @Override
    public void setUserStatus(String uid, User.userStatus newStatus) {
        User user = findUserByUid(uid);
        if (user != null) {
            user.setStatus(newStatus);
            em.flush();
        }
        //tablesManager.setNeedsToUpdate(true);
    }

    public void incUserScrore(String uid, int value) {
        User u = findUserByUid(uid);

        if (u != null) {
            u.setScore(u.getScore() + value);

            em.flush();
        }
        //tablesManager.setNeedsToUpdate(true);
    }

    @Override
    public void setupBootUserStatus() {

        List<User> users = em.createNamedQuery("User.findAll").getResultList();

        for (User u : users) {
            u.setStatus(User.userStatus.NOT_LOGGED_IN);
        }

        em.flush();

    }

    @Override
    public int getUserLoggedInRatio() {

        List<User> loggedInUser = em.createNamedQuery("User.purifyByStatus").setParameter("status", User.userStatus.NOT_LOGGED_IN.toString()).getResultList();

        double ratio = Math.pow(loggedInUser.size(), 1.756366) + 5000;

        int intRatio = (int) ratio;

        if (intRatio > 60000) {
            intRatio = 60000;
        }

        return intRatio;
    }
}
