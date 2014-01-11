/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibo.cs.swarch.serca.ejb.tablemanagement.stateless.timing;

import javax.ejb.Local;

/**
 *
 * @author tappof
 */
@Local
public interface GameTimerLocal {

    void setNewTimer(String tableId, long interval);
    
}
