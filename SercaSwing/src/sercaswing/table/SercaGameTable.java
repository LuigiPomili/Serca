/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sercaswing.table;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.CardAdapter;
import ch.aplu.jcardgame.CardGame;
import ch.aplu.jcardgame.Deck;
import ch.aplu.jcardgame.Hand;
import ch.aplu.jcardgame.RowLayout;
import ch.aplu.jcardgame.StackLayout;
import ch.aplu.jgamegrid.GGExitListener;
import ch.aplu.jgamegrid.Location;
import ch.aplu.jgamegrid.TextActor;

import it.unibo.cs.swarch.serca.clientsideconnectionlibrary.SercaConnectionManager.SercaConnectionManager;
import it.unibo.cs.swarch.serca.protocol.jaxb.CreateTable;
import it.unibo.cs.swarch.serca.protocol.jaxb.CreateTableReply;
import it.unibo.cs.swarch.serca.protocol.jaxb.Gameover;
import it.unibo.cs.swarch.serca.protocol.jaxb.HandFinished;
import it.unibo.cs.swarch.serca.protocol.jaxb.Move;
import it.unibo.cs.swarch.serca.protocol.jaxb.MoveReply;
import it.unibo.cs.swarch.serca.protocol.jaxb.Problem;
import it.unibo.cs.swarch.serca.protocol.jaxb.Subscription;
import it.unibo.cs.swarch.serca.protocol.jaxb.SubscriptionReply;
import it.unibo.cs.swarch.serca.protocol.jaxb.UserHasJoinedTheTable;
import it.unibo.cs.swarch.serca.protocol.jaxb.UserHasLeftTheTable;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class SercaGameTable extends CardGame implements PropertyChangeListener {

    private JTextField localChatWriter;
    private String tableId;
    private String serverUrl;
    private String uid;
    private String pwd;
    private boolean createNewTable;
    private int botNo;
    private boolean isPlayer;
    private boolean isYourTurn = false;
    private boolean isGameStarted = false;
    private SercaConnectionManager scm = null;
    private String[] sittedPlayers;
    private TextActor[] players;
    //
    private Hand[] hands;
    private Hand[] ontablecards = new Hand[4];
    private Hand[] takencards = new Hand[4];
    private Hand unusedcards;

    class MyCardValues implements Deck.CardValues {

        public int[] values(Enum suit) {
            int[] defaultValues = new int[]{1, 1, 1, 0, 0, 0, 0, 1, 1, 1};
            return defaultValues;
        }
    }

    public enum Suit {

        SPADES {

            public String toXmlSuit() {
                return "S";
            }
        },
        HEARTS {

            public String toXmlSuit() {
                return "H";
            }
        },
        DIAMONDS {

            public String toXmlSuit() {
                return "D";
            }
        },
        CLUBS {

            public String toXmlSuit() {
                return "C";
            }
        };

        public static Suit convert(char _seed) {

            switch (_seed) {
                case 'S':
                    return SPADES;

                case 'H':
                    return HEARTS;

                case 'D':
                    return DIAMONDS;

                case 'C':
                    return CLUBS;

                default:
                    return null;
            }
        }

        public String toXmlSuit() {
            return this.toXmlSuit();
        }
    }

    public enum Rank {

        ACE {

            public String toXmlRank() {
                return "A";
            }
        },
        KING {

            public String toXmlRank() {
                return "K";
            }
        },
        QUEEN {

            public String toXmlRank() {
                return "Q";
            }
        },
        JACK {

            public String toXmlRank() {
                return "J";
            }
        },
        TEN {

            public String toXmlRank() {
                return "10";
            }
        },
        NINE {

            public String toXmlRank() {
                return "9";
            }
        },
        EIGHT {

            public String toXmlRank() {
                return "8";
            }
        },
        SEVEN {

            public String toXmlRank() {
                return "7";
            }
        },
        SIX {

            public String toXmlRank() {
                return "6";
            }
        },
        FIVE {

            public String toXmlRank() {
                return "5";
            }
        },
        FOUR {

            public String toXmlRank() {
                return "4";
            }
        },
        THREE {

            public String toXmlRank() {
                return "3";
            }
        },
        TWO {

            public String toXmlRank() {
                return "2";
            }
        };

        public static Rank convert(String _rank) {
            if (_rank.equals("A")) {
                return ACE;
            }
            if (_rank.equals("2")) {
                return TWO;
            }
            if (_rank.equals("3")) {
                return THREE;
            }
            if (_rank.equals("4")) {
                return FOUR;
            }
            if (_rank.equals("5")) {
                return FIVE;
            }
            if (_rank.equals("6")) {
                return SIX;
            }
            if (_rank.equals("7")) {
                return SEVEN;
            }
            if (_rank.equals("8")) {
                return EIGHT;
            }
            if (_rank.equals("9")) {
                return NINE;
            }
            if (_rank.equals("10")) {
                return TEN;
            }
            if (_rank.equals("J")) {
                return JACK;
            }
            if (_rank.equals("Q")) {
                return QUEEN;
            }
            if (_rank.equals("K")) {
                return KING;
            }

            return null;

        }

        public String toXmlRank() {
            return this.toXmlRank();
        }
    }
    private Deck deck =
            new Deck(Suit.values(), Rank.values(), "cover", new MyCardValues());

    public SercaGameTable(SercaConnectionManager _scm, boolean _createNewTable, String _tableId, boolean _isPlayer, int _botNo, String _serverUrl, JTextField _localChatWriter) {
        super(900, 700);

        this.setVisible(false);
        this.localChatWriter = _localChatWriter;
        this.serverUrl = _serverUrl;
        this.scm = _scm;
        this.uid = this.scm.getUid();
        this.pwd = this.scm.getPwd();

        this.sittedPlayers = new String[4];
        for (int i = 0; i < 4; i++) {
            this.sittedPlayers[i] = null;
        }

        this.createNewTable = _createNewTable;
        if (this.createNewTable) {
            this.tableId = this.scm.getUid();
            this.isPlayer = true;
            this.botNo = _botNo;
        } else {
            this.tableId = _tableId;
            this.isPlayer = _isPlayer;
            this.botNo = 0;
        }

        if (this.createNewTable) {
            CreateTable ct = new CreateTable();
            ct.setBotsno(this.botNo);

            try {
                this.scm.requestServerStreamingService(this.serverUrl + "/admintables", ct, SercaConnectionManager.ConnectionsName.GAME, this);
            } catch (IOException ex) {
                Logger.getLogger(SercaGameTable.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            Subscription s = new Subscription();
            s.setKind((isPlayer) ? "player" : "watcher");
            s.setTableId(this.tableId);
            try {
                this.scm.requestServerStreamingService(this.serverUrl + "/admintables", s, SercaConnectionManager.ConnectionsName.GAME, this);
            } catch (IOException ex) {
                Logger.getLogger(SercaGameTable.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        addExitListener(new GGExitListener() {

            @Override
            public boolean notifyExit() {

                if (scm != null) {
                    scm.closeStreamingConnections(SercaConnectionManager.ConnectionsName.GAME);
                }

                localChatWriter.setEnabled(false);
                localChatWriter.setEditable(false);
                stopGameThread();
                setVisible(false);

                return false;
            }
        });


        setPosition(0, 0);

        hands = new Hand[4];

        for (int i = 0; i < 4; i++) {
            ontablecards[i] = new Hand(deck);
        }
        for (int i = 0; i < 4; i++) {
            takencards[i] = new Hand(deck);
        }

        this.setLocation(0, 0);

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        Object reply = evt.getNewValue();

        if (reply instanceof CreateTableReply) {
            CreateTableReply ctr = (CreateTableReply) reply;

            if (ctr.isCreated()) {

                for (int i = 0; i < 4; i++) {
                    hands[i] = new Hand(this.deck);
                }

                this.sittedPlayers[0] = this.scm.getUid();

                initPlayersArray();

                changePlayerName(0, this.scm.getUid());
                dealing(0, ctr.getCardsList().getCard());
                setPlayersLayout();

                for (int i = 0; i < this.botNo; i++) {
                    this.sittedPlayers[i + 1] = "Bot" + i;
                    changePlayerName(i + 1, "Bot" + i);
                    dealing(i + 1, ctr.getCardsList().getCard());
                    setPlayersLayout();
                }

                this.isGameStarted = ctr.isGameIsStarted();
                System.out.println(ctr.getFirstPlayer());
                this.isYourTurn = (ctr.getFirstPlayer() != null && ctr.getFirstPlayer().equals(this.uid)) ? true : false;
                hands[0].sort(Hand.SortType.SUITPRIORITY, true);
                
                this.localChatWriter.setEnabled(true);
                this.localChatWriter.setEditable(true);
                this.setVisible(true);
            } else {
                scm.closeStreamingConnections(SercaConnectionManager.ConnectionsName.GAME);
            }


        } else if (reply instanceof SubscriptionReply) {

            SubscriptionReply sr = (SubscriptionReply) reply;

            List<SubscriptionReply.UsersAlreadySubscribed.SubscribedUser> subscribedUsers = null;
            
            if (sr.getResult().equals("subscribed")) {

                subscribedUsers = sr.getUsersAlreadySubscribed().getSubscribedUser();
                
                for (int i = 0; i < 4; i++) {
                    hands[i] = new Hand(deck);
                }
                initPlayersArray();


                if (isPlayer) {
                    if (sr.getResult().equals("subscribed")) {

                        int playerIndex;
                        for (playerIndex = 0; playerIndex < subscribedUsers.size(); playerIndex++) {
                            if (subscribedUsers.get(playerIndex).getUid().equals(this.scm.getUid())) {
                                break;
                            }
                        }

                        this.sittedPlayers[0] = this.scm.getUid();
                        changePlayerName(0, this.scm.getUid());
                        dealing(0, subscribedUsers.get(playerIndex).getCardsList().getCard());

                        int whiteSpace = 4 - playerIndex;

                        for (int i = 0; i < playerIndex; i++) {
                            int pos = i + whiteSpace;
                            this.sittedPlayers[pos] = subscribedUsers.get(i).getUid();
                            changePlayerName(pos, this.sittedPlayers[pos]);
                            dealing(pos, (this.isPlayer) ? null : subscribedUsers.get(i).getCardsList().getCard());
                        }

                        setPlayersLayout();
                        
                        hands[0].sort(Hand.SortType.SUITPRIORITY, true);
                    }
                } else {
                    if (sr.getResult().equals("subscribed")) {
                        int playerIndex = 0;
                        for (SubscriptionReply.UsersAlreadySubscribed.SubscribedUser su : subscribedUsers) {
                            this.sittedPlayers[playerIndex] = su.getUid();
                            changePlayerName(playerIndex, su.getUid());
                            List<String> cards = su.getCardsList().getCard();
                            if (su.getCardOnTable() != null) {
                                cards.remove(su.getCardOnTable());
                                ontablecards[playerIndex].insert(new Card(deck, Suit.convert(su.getCardOnTable().charAt(su.getCardOnTable().length() - 1)), Rank.convert(su.getCardOnTable().substring(0, su.getCardOnTable().length() - 1)), true), true);
                            }
                            dealing(playerIndex, cards);
                            hands[playerIndex].sort(Hand.SortType.SUITPRIORITY, true);
                            playerIndex++;
                        }
                    }

                    setPlayersLayout();
                }
                this.localChatWriter.setEnabled(true);
                this.localChatWriter.setEditable(true);
                this.setVisible(true);
            } else {
                this.setVisible(false);
            }

        } else if (reply instanceof UserHasJoinedTheTable) {

            UserHasJoinedTheTable uhjt = (UserHasJoinedTheTable) reply;
            if (!uhjt.getUserThatHasJoined().equals(this.uid)) {
                int playerIndex;
                for (playerIndex = 0; playerIndex < 4; playerIndex++) {
                    if (this.sittedPlayers[playerIndex] == null) {
                        break;
                    }
                }

                this.sittedPlayers[playerIndex] = uhjt.getUserThatHasJoined();

                changePlayerName(playerIndex, uhjt.getUserThatHasJoined());
                dealing(playerIndex, null);
                this.isGameStarted = uhjt.isGameStarted();
                this.isYourTurn = (uhjt.getFirstPlayer().equals(this.uid)) ? true : false;
                
                setPlayersLayout();
            } else {
                this.isGameStarted = uhjt.isGameStarted();
                this.isYourTurn = (uhjt.getFirstPlayer().equals(this.uid)) ? true : false;
                
                setPlayersLayout();
            }
        } else if (reply instanceof UserHasLeftTheTable) {

            UserHasLeftTheTable uhlt = (UserHasLeftTheTable) reply;

            int playerIndex;
            for (playerIndex = 0; playerIndex < 4; playerIndex++) {
                if (this.sittedPlayers[playerIndex].equals(uhlt.getUserThatHasLeft())) {
                    break;
                }
            }

            this.sittedPlayers[playerIndex] = uhlt.getBotName();
            changePlayerName(playerIndex, uhlt.getBotName());

        } else if (reply instanceof MoveReply) {

            MoveReply mr = (MoveReply) reply;

            String strCard = mr.getCard();

            Card c = new Card(deck, Suit.convert(strCard.charAt(strCard.length() - 1)), Rank.convert(strCard.substring(0, strCard.length() - 1)), true);

            int playerIndex;
            for (playerIndex = 0; playerIndex < 4; playerIndex++) {
                if (this.sittedPlayers[playerIndex].equals(mr.getMoveOf())) {
                    break;
                }
            }

            //if (!this.sittedPlayers[playerIndex].equals(this.uid) && this.hands[playerIndex]) {
            if (!this.hands[playerIndex].contains(c)) {
                hands[playerIndex].removeFirst(isPlayer);
                hands[playerIndex].insert(c, true);
            }

            Card cardToRemove = hands[playerIndex].getCard(Suit.convert(strCard.charAt(strCard.length() - 1)), Rank.convert(strCard.substring(0, strCard.length() - 1)));
            cardToRemove.setVerso(false);
            System.out.println("cardToRemove" + cardToRemove);
            hands[playerIndex].transfer(cardToRemove, ontablecards[playerIndex], true);

            this.isYourTurn = (mr.getNextTurnOf() != null && mr.getNextTurnOf().equals(this.uid)) ? true : false;


        } else if (reply instanceof HandFinished) {

            HandFinished hf = (HandFinished) reply;



            int playerIndex;
            for (playerIndex = 0; playerIndex < 4; playerIndex++) {
                if (this.sittedPlayers[playerIndex].equals(hf.getHandWinner())) {
                    break;
                }
            }

            System.out.println("hand winner is " + hf.getHandWinner() + " " + playerIndex + this.sittedPlayers[playerIndex]);
            for (int i = 0; i < 4; i++) {
                ontablecards[i].transfer(ontablecards[i].get(0), takencards[playerIndex], true);
            }

            takencards[playerIndex].removeAll(true);

            this.isYourTurn = (hf.getHandWinner().equals(this.uid)) ? true : false;


        } else if (reply instanceof Gameover) {

            removeAllActors();

            Gameover go = (Gameover) reply;

            String message = go.getReason().equals("there is a winner") ? "The winner is " + go.getWinnerIs() : "Creator passed away";

            TextActor gameoverMessage = new TextActor(message, Color.ORANGE, getBgColor(), new Font(Font.SERIF, Font.BOLD, 36));
            addActor(gameoverMessage, new Location(this.getSize().height / 2, (this.getSize().width - 300) / 2));

            this.isGameStarted = false;

            this.scm.closeStreamingConnections(SercaConnectionManager.ConnectionsName.GAME);

        } else if (reply instanceof Problem) {
        }
    }

    private void initPlayersArray() {
        this.players = new TextActor[4];

        this.players[0] = new TextActor("Player 1", Color.WHITE, getBgColor(), getFont());
        addActor(this.players[0], new Location(430, 680));
        this.players[1] = new TextActor(true, "Player 2", Color.WHITE, getBgColor(), getFont());
        addActor(this.players[1], new Location(870, 390), 270);
        this.players[2] = new TextActor(true, "Player 3", Color.WHITE, getBgColor(), getFont());
        addActor(this.players[2], new Location(490, 10), 180);
        this.players[3] = new TextActor(true, "Player 4", Color.WHITE, getBgColor(), getFont());
        addActor(this.players[3], new Location(20, 310), 90);

    }

    private void changePlayerName(int playerIndex, String newName) {
        Location loc = this.players[playerIndex].getLocation();
        this.removeActor(this.players[playerIndex]);
        this.players[playerIndex] = new TextActor(newName, Color.WHITE, getBgColor(), getFont());
        addActor(this.players[playerIndex], loc);
    }

    private void dealing(int playerIndex, List<String> cards) {
        if (cards != null) {
            for (String card : cards) {
                hands[playerIndex].insert(new Card(this.deck, Suit.convert(card.charAt(card.length() - 1)), Rank.convert(card.substring(0, card.length() - 1))), true);
            }
        } else {
            for (int i = 0; i < 13; i++) {
                hands[playerIndex].insert(new Card(deck, i), isPlayer);
            }
        }

    }

    private void setPlayersLayout() {

        for (int i = 0; i < 4; i++) {
            ontablecards[i].setVerso(false);
        }

        RowLayout player0Layout = new RowLayout(new Location(450, 590), 500);
        hands[0].setView(this, player0Layout);
        hands[0].setTouchEnabled(true);
        hands[0].putOnTopEnabled(true);
        hands[0].draw();
        ontablecards[0].setView(this, new StackLayout(new Location(450, 400)));
        ontablecards[0].draw();
        
        hands[0].addCardListener(new CardAdapter() {

            public void leftDoubleClicked(Card card) {
                System.out.println("STO CLICCANDO SU UNA CARTA" + isPlayer + " " + isGameStarted + " " + "" + isYourTurn);
                if (isPlayer && isGameStarted && isYourTurn) {
                    isYourTurn = false;
                    Move m = new Move();
                    m.setCard(((Rank) card.getRank()).toXmlRank() + ((Suit) card.getSuit()).toXmlSuit());
                    try {
                        SercaConnectionManager.SingleRequestReplyResponse serverReply = scm.singleRequestReplyService(serverUrl + "/move", m, null);
                        if(serverReply.getHttpStatusCode() == 200 && serverReply.getReturnedData() != null) {
                            MoveReply mr = (MoveReply)serverReply.getReturnedData();
                            if(mr.getMoveOf().equals(scm.getUid()) && mr.getNextTurnOf().equals(scm.getUid())) {
                                System.out.println("WRONG CARD");
                                isYourTurn = true;
                            }
                        }
                    } catch (ProtocolException ex) {
                        Logger.getLogger(SercaGameTable.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(SercaGameTable.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        RowLayout player1Layout = new RowLayout(new Location(780, 350), 300);
        player1Layout.setRotationAngle(270);
        hands[1].setVerso(isPlayer);
        hands[1].setTouchEnabled(!isPlayer);
        hands[1].setView(this, player1Layout);
        hands[1].draw();
        ontablecards[1].setView(this, new StackLayout(new Location(500, 350)));
        ontablecards[1].draw();

        RowLayout player2Layout = new RowLayout(new Location(450, 100), 300);
        player2Layout.setRotationAngle(180);
        hands[2].setVerso(isPlayer);
        hands[2].setTouchEnabled(!isPlayer);
        hands[2].setView(this, player2Layout);
        hands[2].draw();
        ontablecards[2].setView(this, new StackLayout(new Location(450, 300)));
        ontablecards[2].draw();

        RowLayout player3Layout = new RowLayout(new Location(110, 350), 300);
        player3Layout.setRotationAngle(90);
        hands[3].setVerso(isPlayer);
        hands[3].setTouchEnabled(!isPlayer);
        hands[3].setView(this, player3Layout);
        hands[3].draw();
        ontablecards[3].setView(this, new StackLayout(new Location(400, 350)));
        ontablecards[3].draw();


        /**
         * 
         */
        RowLayout takencards0Layout = new RowLayout(new Location(450, 590), 0);
        takencards[0].setVerso(false);
        takencards[0].setView(this, takencards0Layout);
        takencards[0].draw();

        RowLayout takencards1Layout = new RowLayout(new Location(780, 350), 0);
        takencards[1].setVerso(false);
        takencards[1].setView(this, takencards1Layout);
        takencards[1].draw();

        RowLayout takencards2Layout = new RowLayout(new Location(450, 100), 0);
        takencards[2].setVerso(false);
        takencards[2].setView(this, takencards2Layout);
        takencards[2].draw();

        RowLayout takencards3Layout = new RowLayout(new Location(110, 350), 0);
        takencards[3].setVerso(false);
        takencards[3].setView(this, takencards3Layout);
        takencards[3].draw();

    }
}
