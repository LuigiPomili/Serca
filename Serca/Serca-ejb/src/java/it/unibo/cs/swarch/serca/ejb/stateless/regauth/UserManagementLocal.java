package it.unibo.cs.swarch.serca.ejb.stateless.regauth;

import it.unibo.cs.swarch.serca.ejb.mappedentity.users.User;

public interface UserManagementLocal {



    
    public static enum authenticationResult {
        LOGGED_IN, ALREADY_LOGGED_IN, INVALID_USER_OR_PASSWORD
    }
    
    public boolean registration(it.unibo.cs.swarch.serca.protocol.jaxb.Registration newCredentials);

    public it.unibo.cs.swarch.serca.ejb.stateless.regauth.UserManagement.authenticationResult authentication(it.unibo.cs.swarch.serca.protocol.jaxb.Login creadentials);

    public it.unibo.cs.swarch.serca.ejb.mappedentity.users.User.userStatus getUserStatus(java.lang.String uid);

    public it.unibo.cs.swarch.serca.ejb.mappedentity.users.User findUserByUid(java.lang.String uid);
        
    public void incUserScrore(java.lang.String uid, int value);

    void logout(String uid);

    void setUserStatus(String uid, User.userStatus newStatus);

    void setupBootUserStatus();

    int getUserLoggedInRatio();
    
}