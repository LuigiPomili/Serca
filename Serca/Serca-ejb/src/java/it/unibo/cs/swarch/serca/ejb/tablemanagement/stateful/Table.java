/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibo.cs.swarch.serca.ejb.tablemanagement.stateful;

import com.sun.grizzly.comet.CometContext;
import com.sun.grizzly.comet.CometEngine;
import it.unibo.cs.swarch.serca.ejb.mappedentity.users.User;
import it.unibo.cs.swarch.serca.ejb.stateless.regauth.UserManagementLocal;
import it.unibo.cs.swarch.serca.ejb.tablemanagement.stateless.timing.GameTimerLocal;
import it.unibo.cs.swarch.serca.gamestaff.pojo.Deck;
import it.unibo.cs.swarch.serca.gamestaff.pojo.Deck.Card;
import it.unibo.cs.swarch.serca.protocol.jaxb.Gameover;
import it.unibo.cs.swarch.serca.protocol.jaxb.HandFinished;
import it.unibo.cs.swarch.serca.protocol.jaxb.MoveReply;
import it.unibo.cs.swarch.serca.protocol.jaxb.UserHasLeftTheTable;
import it.unibo.cs.swarch.serca.protocol.translator.ProtocolTranslator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.AccessTimeout;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.Timer;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;

import javax.persistence.PersistenceContext;

/**
 *
 * @author tappof
 */
@Stateful
@ConcurrencyManagement
@AccessTimeout(-1)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class Table implements TableLocal {

    @PersistenceContext(unitName = "Serca-ejbPU")
    private EntityManager em;
    private String maker;
    private List<String> players;
    private List<String> watchers;
    private int turnOf;
    private int botNo;
    private boolean started;
    private String winner;
    private Deck deckAndGame;
    private Deck.Card[] cardOnTheTable;
    private Timer activeTimer;
    private int timerExpirationCounter;
    private int lastHandWinnerIndex;
    private final int userTurnLength = 4000; // * 5
    private final int botsTurnLength = 5000;
    
    @EJB
    private UserManagementLocal userManagement;
    @EJB
    private GameTimerLocal gt;
    Logger logger;

    @PostConstruct
    @Override
    public void initialize() {
        players = new ArrayList<String>();
        watchers = new ArrayList<String>();
        turnOf = 0;
        started = false;
        winner = "";
        botNo = 0;  //it could change during table setup
        cardOnTheTable = new Deck.Card[4];
        timerExpirationCounter = 0;

        logger = Logger.getLogger(this.getClass().getSimpleName());
    }

    @RolesAllowed("LOGGED_IN")
    @Override
    public void setupTable(String _maker, int _botNo) {
        this.maker = _maker;
        this.botNo = _botNo;

        userManagement.setUserStatus(_maker, User.userStatus.TABLECREATOR_WAITING);

        this.players.add(_maker);

        for (int i = 0; i < botNo; i++) {
            this.players.add("Bot" + i);
        }

        this.deckAndGame = new Deck();

        this.turnOf = this.lastHandWinnerIndex = this.deckAndGame.getFirstPlayer();

        if (botNo == 3) {
            startGame();
        }

    }

    @RolesAllowed("LOGGED_IN")
    @Override
    public boolean addPlayer(String uid) {

        if (this.players.size() < 4) {
            this.players.add(uid);

            if (this.players.size() == 4) {
                startGame();
            } else {
                userManagement.setUserStatus(uid, User.userStatus.PLAYER_WAITING);
            }

            return true;
        } else {
            return false;
        }

    }

    @RolesAllowed("LOGGED_IN")
    @Override
    public boolean addWatcher(String uid) {

        if (!this.started) {
            userManagement.setUserStatus(uid, User.userStatus.OBSERVER_WAITING);
        } else {
            userManagement.setUserStatus(uid, User.userStatus.OBSERVER);
        }

        this.watchers.add(uid);

        return true;
    }

    @PermitAll
    @Override
    public List<String> getPlayerCards(String uid) {

        int indexOfUser = findUserIndexByUid(uid);

        return deckAndGame.getMappedXmlUserCard(indexOfUser);
    }

    @PermitAll
    @Override
    public void removeUserFromTableAndAddBot(String uid) {

        String bot = "";
        if (this.players.contains(uid)) {

            bot = "Bot" + botNo;
            this.botNo++;

            int index = this.players.indexOf(uid);
            this.players.remove(uid);
            this.players.add(index, bot);
            
            UserHasLeftTheTable uhlt = new UserHasLeftTheTable();
            uhlt.setUserThatHasLeft(uid);
            uhlt.setBotName(bot);
            try {
                CometEngine.getEngine().getCometContext(this.maker + "Game").notify(ProtocolTranslator.fromObjToXml(uhlt));
            } catch (IOException ex) {
                Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            this.watchers.remove(uid);
        }

        if(userManagement.getUserStatus(uid) != User.userStatus.NOT_LOGGED_IN)
            userManagement.setUserStatus(uid, User.userStatus.LOGGED_IN);
    }

    private void startGame() {

        logger.warning("Game started on table: " + this.maker);

        this.started = true;

        for (String u : this.players) {
            if (userManagement.getUserStatus(u) != User.userStatus.TABLECREATOR_WAITING) {
                userManagement.setUserStatus(u, User.userStatus.PLAYER);
            } else {
                userManagement.setUserStatus(u, User.userStatus.TABLECREATOR);
            }
        }

        for (String u : this.watchers) {
            userManagement.setUserStatus(u, User.userStatus.OBSERVER);
        }

        this.turnOf = this.players.indexOf(getFirstPlayer());

        if (userManagement.getUserStatus(this.players.get(this.turnOf)) == User.userStatus.TABLECREATOR) {
            userManagement.setUserStatus(this.players.get(this.turnOf), User.userStatus.TABLECREATOR_ON_TURN);
        } else {
            userManagement.setUserStatus(this.players.get(this.turnOf), User.userStatus.PLAYER_ON_TURN);
        }

        if (getFirstPlayer().contains("Bot") && getFirstPlayer().length() < 6) {
            gt.setNewTimer(this.maker, this.botsTurnLength);
        } else {
            gt.setNewTimer(this.maker, this.userTurnLength);
        }

    }

    @Lock(LockType.WRITE)
    @Override
    public boolean turnExpired() {

        boolean turnIsReallyExpired = false;

        this.timerExpirationCounter++;

        // è un bot
        if (this.players.get(this.turnOf).length() < 6) {
            turnIsReallyExpired = true;
            timerExpirationCounter = 0;
        } else {
            if (this.timerExpirationCounter == 5 || this.cardOnTheTable[turnOf] != null) {
                turnIsReallyExpired = true;
                timerExpirationCounter = 0;
            } else {
                gt.setNewTimer(this.maker, this.userTurnLength);
            }
        }

        if (turnIsReallyExpired) {

            MoveReply mr = new MoveReply();

            if (userManagement.getUserStatus(this.players.get(this.turnOf)) == User.userStatus.TABLECREATOR_ON_TURN) {
                userManagement.setUserStatus(this.players.get(this.turnOf), User.userStatus.TABLECREATOR);
            } else {
                userManagement.setUserStatus(this.players.get(this.turnOf), User.userStatus.PLAYER);
            }

            if (this.cardOnTheTable[this.turnOf] == null) {
                if (isFirstCardOfTheHand()) {
                    this.cardOnTheTable[this.turnOf] = this.deckAndGame.ai(this.turnOf, null);
                } else {
                    this.cardOnTheTable[this.turnOf] = this.deckAndGame.ai(this.turnOf, this.cardOnTheTable[this.lastHandWinnerIndex].seed);
                }
            }

            mr.setMoveOf(this.players.get(this.turnOf));
            mr.setCard(this.cardOnTheTable[this.turnOf].num + this.cardOnTheTable[this.turnOf].seed.toString());

            if (handFinished()) {

                this.lastHandWinnerIndex = this.deckAndGame.computeHandWinner(cardOnTheTable, lastHandWinnerIndex);
                this.turnOf = this.lastHandWinnerIndex;
                mr.setNextTurnOf(this.players.get(this.turnOf));

                HandFinished hf = new HandFinished();
                hf.setHandWinner(this.players.get(this.turnOf));

                if (this.deckAndGame.isGameFinished()) {

                    this.winner = this.players.get(this.deckAndGame.computeGameWinner());

                    try {
                        mr.setNextTurnOf(null);
                        CometEngine.getEngine().getCometContext(maker + "Game").notify((String) ProtocolTranslator.fromObjToXml(mr));
                        Thread.sleep(500);
                        CometEngine.getEngine().getCometContext(maker + "Game").notify((String) ProtocolTranslator.fromObjToXml(hf));
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    return true;

                } else {
                    try {
                        CometEngine.getEngine().getCometContext(maker + "Game").notify((String) ProtocolTranslator.fromObjToXml(mr));
                        Thread.sleep(500);
                        CometEngine.getEngine().getCometContext(maker + "Game").notify((String) ProtocolTranslator.fromObjToXml(hf));
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                for (int i = 0; i < 4; i++) {
                    this.cardOnTheTable[i] = null;
                }

            } else {
                this.turnOf = (this.turnOf + 1) % 4;
                mr.setNextTurnOf(this.players.get(this.turnOf));
                try {
                    CometEngine.getEngine().getCometContext(maker + "Game").notify((String) ProtocolTranslator.fromObjToXml(mr));
                } catch (IOException ex) {
                    Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (this.players.get(this.turnOf).contains("Bot") && this.players.get(this.turnOf).length() < 6) {
                gt.setNewTimer(this.maker, this.botsTurnLength);
            } else {
                gt.setNewTimer(this.maker, this.userTurnLength);
            }

            if (userManagement.getUserStatus(this.players.get(this.turnOf)) == User.userStatus.TABLECREATOR) {
                userManagement.setUserStatus(this.players.get(this.turnOf), User.userStatus.TABLECREATOR_ON_TURN);
            } else {
                userManagement.setUserStatus(this.players.get(this.turnOf), User.userStatus.PLAYER_ON_TURN);
            }
            
            return false;
        }

        return false;

    }

    @Lock(LockType.WRITE)
    @Override
    public boolean moveOnTheTable(String cardId) {

        if (this.cardOnTheTable[turnOf] != null) {
            return false;
        }

        String cardNum = "";
        if (cardId.contains("10")) {
            cardNum = cardId.substring(0, 2);
        } else {
            cardNum = Character.toString(cardId.charAt(0));
        }

        String cardSeed = cardId.replaceAll(cardNum, "");

        Card c = this.deckAndGame.new Card(cardNum, Deck.Seed.valueOf(cardSeed));

        boolean validCard = false;
        if (isFirstCardOfTheHand()) {
            validCard = this.deckAndGame.moveCheck(null, turnOf, c);
        } else {
            validCard = this.deckAndGame.moveCheck(this.cardOnTheTable[this.lastHandWinnerIndex].seed, turnOf, c);
        }

        if (!validCard && this.cardOnTheTable[this.turnOf] == null) {
            return false;
        } else {
            this.cardOnTheTable[turnOf] = c;
        }
        return true;
    }

    @Override
    public String getXmlEncodedCardOnTableByUser(String uid) {
        int indexOfUser = findUserIndexByUid(uid);

        if (this.cardOnTheTable[indexOfUser] != null) {
            return this.cardOnTheTable[indexOfUser].num + this.cardOnTheTable[indexOfUser].seed.toString();
        }

        return null;
    }

    private boolean isFirstCardOfTheHand() {
        for (int i = 0; i < 4; i++) {
            if (this.cardOnTheTable[i] != null) {
                return false;
            }
        }
        return true;
    }

    private boolean handFinished() {
        for (int i = 0; i < 4; i++) {
            if (this.cardOnTheTable[i] == null) {
                return false;
            }
        }
        return true;
    }

    @PermitAll
    @Override
    public String getFirstPlayer() {
        try {
            return this.players.get(this.deckAndGame.getFirstPlayer());
        } catch (Exception e) {
            //TODO Correggere facendo ritornare null e aggiungere l'eventualità nell'xsd
            return "unknown";
        }
    }

    private int findUserIndexByUid(String uid) {
        return this.players.indexOf(uid);

    }

    /*
     * GETTER AND SETTER AUTOGENERATED
     */
    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public int getBotNo() {
        return botNo;
    }

    @Override
    public void setBotNo(int botNo) {
        this.botNo = botNo;
    }

    @Override
    public String getMaker() {
        return maker;
    }

    @Override
    public void setMaker(String maker) {
        this.maker = maker;
    }

    @Override
    public List<String> getPlayers() {
        return players;
    }

    @Override
    public void setPlayers(List<String> players) {
        this.players = players;
    }

    @Override
    public int getTurnOf() {
        return turnOf;
    }

    @Override
    public void setTurnOf(int turnOf) {
        this.turnOf = turnOf;
    }

    @Override
    public List<String> getWatchers() {
        return watchers;
    }

    @Override
    public void setWatchers(List<String> watchers) {
        this.watchers = watchers;
    }

    @Remove
    @Override
    public void remove() {

        CometEngine ce = CometEngine.getEngine();
        CometContext cc = ce.getCometContext(maker + "Game");

        Gameover go = new Gameover();
        go.setTableId(maker);

        if (this.winner.equals("")) {
            go.setReason("creator has left the game");
        } else {
            go.setReason("there is a winner");
            go.setWinnerIs(this.winner);
        }

        for (String uid : this.players) {

            if (userManagement.getUserStatus(uid) != User.userStatus.NOT_LOGGED_IN) {
                userManagement.setUserStatus(uid, User.userStatus.LOGGED_IN);
            }

            if (!this.winner.equals("")) {
                if (uid.equals(this.winner)) {
                    userManagement.incUserScrore(uid, 4);
                } else {
                    userManagement.incUserScrore(uid, -1);
                }
            }

        }

        try {
            cc.notify(ProtocolTranslator.fromObjToXml(go));
        } catch (IOException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            CometEngine.getEngine().getCometContext("global").notify(ProtocolTranslator.fromObjToXml(go));
        } catch (IOException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Uccido tutte le connessioni
        ce.unregister(this.maker + "Game");

    }
}
