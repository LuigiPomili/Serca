/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibo.cs.swarch.serca.ejb.tablemanagement.stateful;

import java.util.List;
import javax.ejb.Local;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

/**
 *
 * @author tappof
 */
@Local
public interface TableLocal {
    
    void initialize();

    void remove();

    void setupTable(String _maker, int _botNo);

    public int getBotNo();

    public void setBotNo(int botNo);

    public java.lang.String getMaker();

    public void setMaker(java.lang.String maker);

    public java.util.List<String> getPlayers();

    public void setPlayers(java.util.List<String> players);

    public int getTurnOf();

    public void setTurnOf(int turnOf);

    public java.util.List<String> getWatchers();

    public void setWatchers(java.util.List<String> watchers);

    void removeUserFromTableAndAddBot(String uid);

    boolean addPlayer(String uid);

    boolean addWatcher(String uid);

    List<String> getPlayerCards(String parameter);

    public boolean isStarted();

    String getFirstPlayer();

    public boolean moveOnTheTable(java.lang.String cardId);

    public boolean turnExpired();

    public java.lang.String getXmlEncodedCardOnTableByUser(java.lang.String uid);
}
