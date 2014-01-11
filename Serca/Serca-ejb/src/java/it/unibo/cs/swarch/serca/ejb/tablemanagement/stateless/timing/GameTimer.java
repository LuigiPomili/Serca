/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibo.cs.swarch.serca.ejb.tablemanagement.stateless.timing;

import it.unibo.cs.swarch.serca.ejb.stateless.regauth.UserManagementLocal;
import it.unibo.cs.swarch.serca.ejb.tablemanagement.singleton.TablesManagerLocal;
import it.unibo.cs.swarch.serca.ejb.tablemanagement.stateful.TableLocal;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ejb.AccessTimeout;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;

/**
 *
 * @author tappof
 */
//@RunAs("TimerAgent")
@PermitAll
@AccessTimeout(-1)
@Stateless
public class GameTimer implements GameTimerLocal {

    @Resource
    private TimerService timers;
    @EJB
    private TablesManagerLocal tablesManager;
    @EJB
    private UserManagementLocal usersManager;

    @Override
    public void setNewTimer(String tableId, long interval) {

        this.timers.createTimer(interval, tableId + "Timer");

    }

    @Timeout
    private void timeout(Timer event) {
        // NEW CODE
        Logger.getLogger(GameTimer.class.getSimpleName()).fine("TIMEOUT ON TABLE: " + ((String) event.getInfo()).replace("Timer", ""));
        boolean tableRemoved = false;
        String tableId = ((String) event.getInfo()).replace("Timer", "");
        TableLocal tl = tablesManager.getTableReferenceByTableId(tableId);
        if (tl != null) {

            boolean allTurnsExpired = tl.turnExpired();

            if (allTurnsExpired) {
                tablesManager.removeUserFromTable(tl.getMaker(), tl.getMaker());
            }
        }

    }
}
