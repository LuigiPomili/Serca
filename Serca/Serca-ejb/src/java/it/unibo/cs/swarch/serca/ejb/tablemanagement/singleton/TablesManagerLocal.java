/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibo.cs.swarch.serca.ejb.tablemanagement.singleton;

import it.unibo.cs.swarch.serca.ejb.tablemanagement.stateful.TableLocal;
import it.unibo.cs.swarch.serca.protocol.jaxb.CreateTable;
import java.util.Collection;
import javax.ejb.Local;

/**
 *
 * @author tappof
 */
@Local
public interface TablesManagerLocal {
    
    @javax.ejb.Lock(value = javax.ejb.LockType.WRITE)
    public TableLocal addTable(CreateTable req, String uid);

    @javax.ejb.Lock(value = javax.ejb.LockType.READ)
    public java.lang.String getTableIdByPlayerOrWatcher(java.lang.String uid);

    void updateClientTableList();

    @javax.ejb.Lock(value = javax.ejb.LockType.WRITE)
    void removeTable(String tableId);

    void removeUserFromTable(String uid, String tableId);

    boolean tableExists(String tableId);

    boolean subscribe(String uid, String tableId, boolean isPlayer);

    Collection<String> getUserSubscribedToTable(String tableId);

    void updateUsersList();

    TableLocal getTableReferenceByTableId(String tableId);

    public void setNeedsToUpdate(boolean needsToUpdate);

}
