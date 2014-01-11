/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibo.cs.swarch.serca.ejb.tablemanagement.singleton;

import com.sun.grizzly.comet.CometContext;
import com.sun.grizzly.comet.CometEngine;
import it.unibo.cs.swarch.serca.ejb.stateless.regauth.UserManagementLocal;
import it.unibo.cs.swarch.serca.ejb.tablemanagement.stateful.TableLocal;
import it.unibo.cs.swarch.serca.protocol.jaxb.CreateTable;
import it.unibo.cs.swarch.serca.ejb.mappedentity.users.User;
import it.unibo.cs.swarch.serca.protocol.jaxb.TablesList.Table;
import it.unibo.cs.swarch.serca.protocol.jaxb.UsersList;
import it.unibo.cs.swarch.serca.protocol.translator.ProtocolTranslator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author tappof
 */
@Startup
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class TablesManager implements TablesManagerLocal {

    @Resource private TimerService timer;
    
    @EJB
    private UserManagementLocal userManagemet;
    
    @PersistenceContext(unitName = "Serca-ejbPU")
    private EntityManager em;
    
    private HashMap<String, TableLocal> tables;
    private boolean needsToUpdate;
    
    Logger logger;

    @PermitAll
    @Lock(LockType.WRITE)
    @Override
    public void setNeedsToUpdate(boolean needsToUpdate) {
        this.needsToUpdate = needsToUpdate;
    }
    
    @PostConstruct
    void initialize() {
        
        logger = Logger.getLogger(this.getClass().getSimpleName());
        
        userManagemet.setupBootUserStatus();
        
        tables = new HashMap<String, TableLocal>();
        
        CometEngine ce = CometEngine.getEngine();
        CometContext cc;
        
        if ((cc = ce.getCometContext("global")) == null) {
            cc = ce.register("global");
            cc.setExpirationDelay(30000 * 1000);
        }
        
        this.needsToUpdate = true;
        
        timer.createTimer(20000, "updateEvent");
        
    }
    
    @Timeout
    @Lock(LockType.READ)
    private void updateTimerEvent(Timer event) {
        
        logger.info("TIMEOUT!! needs to update: " + this.needsToUpdate);
        
        if(((String)event.getInfo()).equals("updateEvent")) {
            if(this.needsToUpdate = true) {
                updateUsersList();
                updateClientTableList();
            }
            timer.createTimer(userManagemet.getUserLoggedInRatio(), "updateEvent");
            this.needsToUpdate = false;
        }
    }
    

    @RolesAllowed("LOGGED_IN")
    @Lock(LockType.WRITE)
    @Override
    public TableLocal addTable(CreateTable req, String uid) {

        CreateTable ct = (CreateTable) req;
        TableLocal tl = lookupTableLocal();

        tl.setupTable(uid, ct.getBotsno());

        this.tables.put(tl.getMaker(), tl);

        logger.finest("Number of instanced tables: " + tables.size());

        this.needsToUpdate = true;
        
        return tl;
    }

    @RolesAllowed("LOGGED_IN")
    @Lock(LockType.WRITE)
    @Override
    public boolean subscribe(String uid, String tableId, boolean isPlayer) {

        if (this.tables.containsKey(tableId)) {
            TableLocal table = this.tables.get(tableId);
            this.needsToUpdate = true;
            if (isPlayer) {
                return table.addPlayer(uid);
            } else {
                return table.addWatcher(uid);
            }
            
        } else {
            return false;
        }

    }

    @PermitAll
    @Lock(LockType.READ)
    @Override
    public Collection<String> getUserSubscribedToTable(String tableId) {

        Collection<String> userStringList = new ArrayList<String>();
        TableLocal table = null;
        
        if ((table = this.tables.get(tableId)) != null) {
            return table.getPlayers();
        }
        
        return null;
    }

    @PermitAll
    @Lock(LockType.READ)
    @Override
    public boolean tableExists(String tableId) {
        return this.tables.containsKey(tableId);
    }

    @PermitAll
    @Lock(LockType.WRITE)
    @Override
    public void removeUserFromTable(String uid, String tableId) {

        if (uid.equals(tableId)) {
            removeTable(tableId);
        } else { //non è il creatore
            TableLocal table = this.tables.get(tableId);
            table.removeUserFromTableAndAddBot(uid);
        }

    }
    
    @PermitAll
    @Lock(LockType.WRITE)
    @Override
    public void removeTable(String tableId) {
        
        this.needsToUpdate = true;
        TableLocal table = this.tables.remove(tableId);
        table.remove();

    }

    @PermitAll
    @Lock(LockType.READ)
    @Override
    public String getTableIdByPlayerOrWatcher(String uid) {

        for (String maker : tables.keySet()) {
            
            if(maker.equals("uid"))
                return maker;
            
            TableLocal table = tables.get(maker);
            if (table.getPlayers().contains(uid) || table.getWatchers().contains(uid)) {
                return table.getMaker();
            }
        }

        return null;
    }
    
    @Lock(LockType.READ)
    @Override
    public TableLocal getTableReferenceByTableId(String tableId) {
        return this.tables.get(tableId);
    }

    @PermitAll
    @Lock(LockType.READ)
    @Override
    public void updateClientTableList() {

        it.unibo.cs.swarch.serca.protocol.jaxb.TablesList tsl = new it.unibo.cs.swarch.serca.protocol.jaxb.TablesList();

        List<Table> tablesToSend = tsl.getTable();
        for (String maker : tables.keySet()) {
            Table t = new Table();
            t.setId(maker);
            
            if (tables.get(maker).getPlayers().size() == 4) {
                t.setMembers("full");
            } else {
                int freeSits = 4 - tables.get(maker).getPlayers().size();
                t.setMembers(freeSits + " player/s allowed");
            }
            
            t.setWatchers(tables.get(maker).getWatchers().size());
            
            tablesToSend.add(t);
        }

        try {
            CometEngine.getEngine().getCometContext("global").notify(ProtocolTranslator.fromObjToXml(tsl));
        } catch (IOException ex) {
            Logger.getLogger(TablesManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @PermitAll
    @Lock(LockType.READ)
    @Override
    public void updateUsersList() {
        
            UsersList ul = new UsersList();
            
            List<User> rating = em.createNamedQuery("User.loggedInRating")
                                .setParameter("status", "NOT_LOGGED_IN")
                                .getResultList();
                    
            for(User u : rating) {
                UsersList.User user = new UsersList.User();
                user.setUid(u.getUserid());
                user.setStatus(u.getStatus().toString());
                user.setScore(u.getScore());
                ul.getUser().add(user);
            }
            
        try {    
            CometEngine.getEngine().getCometContext("global").notify(ProtocolTranslator.fromObjToXml(ul));
        } catch (IOException ex) {
            Logger.getLogger(TablesManager.class.getName()).log(Level.SEVERE, null, ex);
        }
                
        
    }
    
    //UTILS

    private TableLocal lookupTableLocal() {
        try {
            Context c = new InitialContext();
            return (TableLocal) c.lookup("java:global/Serca/Serca-ejb/Table!it.unibo.cs.swarch.serca.ejb.tablemanagement.stateful.TableLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private void persist(Object object) {
        em.persist(object);
    }

    
}
