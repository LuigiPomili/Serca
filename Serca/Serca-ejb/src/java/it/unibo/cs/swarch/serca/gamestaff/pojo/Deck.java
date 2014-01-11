/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibo.cs.swarch.serca.gamestaff.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 *
 * @author tappof
 */
public class Deck {

    private int firstPlayer;
    private List<Card> unassigned = new ArrayList<Card>();
    private HashMap<Integer, ArrayList<Card>> assigned = new HashMap<Integer, ArrayList<Card>>();
    private HashMap<Integer, ArrayList<Card>> taken = new HashMap<Integer, ArrayList<Card>>();
    private boolean heartsEnabled = false;
    private final String[] nums = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};

    public static enum Seed {

        C, D, H, S
    }

    public class Card {

        public String num;
        public Deck.Seed seed;
        public int value;

        public Card(String num, Deck.Seed seed) {
            this.num = num;
            this.seed = seed;
            for (int i = 0; i < 13; i++) {
                if (this.num.equals(nums[i])) {
                    this.value = i+1;
                }
            }
        }

        public Card(String num, Deck.Seed seed, int value) {
            this.num = num;
            this.seed = seed;
            this.value = value;
        }

        @Override
        public boolean equals(Object c) {
            Card card = null;
            if (getClass() == c.getClass()) {
                card = (Card) c;
                if (this.num.equals(card.num) && this.seed == card.seed && this.value == card.value) {
                    return true;
                }
            }

            return false;

        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 67 * hash + (this.num != null ? this.num.hashCode() : 0);
            hash = 67 * hash + (this.seed != null ? this.seed.hashCode() : 0);
            hash = 67 * hash + this.value;
            return hash;
        }
    }

    public Deck() {
        for (Seed s : Deck.Seed.values()) {
            int value = 0;
            for (String num : this.nums) {
                this.unassigned.add(new Card(num, s, ++value));
            }
        }

        for (int user = 0; user < 4; user++) {

            this.assigned.put(user, new ArrayList<Card>(13));
            this.taken.put(user, new ArrayList<Card>());

            for (int cardNo = 0; cardNo < 13; cardNo++) {
                Card c = this.unassigned.remove(new Random().nextInt(unassigned.size()));
                if (c.seed == Deck.Seed.C && c.num.equals("2")) {
                    this.firstPlayer = user;
                }
                
                this.assigned.get(user).add(c);
            }

        }

    }

    public int getFirstPlayer() {
        return firstPlayer;
    }

    public List<String> getMappedXmlUserCard(int indexOfUser) {

        List<String> cards = new ArrayList<String>();

        for (Card c : this.assigned.get(indexOfUser)) {
            cards.add(c.num + c.seed.toString());
        }

        return cards;
    }

    public boolean moveCheck(Deck.Seed s, int indexOfUser, Card cu1) {
        if (!this.assigned.get(indexOfUser).contains(cu1)) {
            return false;
        }

        boolean firstTurn = (this.assigned.get(this.firstPlayer).size() == 13);

        //prima carta della partita
        if (firstTurn && s == null) {
            return (cu1.seed == Deck.Seed.C && cu1.num.equals("2"));
        }

        //prima carta di tutte le altre mani
        if (!firstTurn && s == null) {
            if (cu1.seed == Deck.Seed.H && !this.heartsEnabled && !onlyHearts(indexOfUser)) {
                return false;
            } else if (onlyHearts(indexOfUser)) {
                this.heartsEnabled = true;
                return true;
            } else {
                return true;
            }
        }

        //altre carte di una qualsiasi mano (incluso controllo sulla peppa alla prima mano)
        if (s == cu1.seed) {
            return true;
        } else {
            if (playerHasTheSeed(indexOfUser, s)) {
                return false;
            } else {
                if (firstTurn && onlyPenaltyCards(indexOfUser) && cu1.seed != Deck.Seed.H) {
                    return true;
                }

                if (cu1.seed == Deck.Seed.H) {

                    if (firstTurn && !onlyPenaltyCards(indexOfUser)) {
                        return false;
                    }

                    this.heartsEnabled = true;
                }

                return true;
            }
        }
    }

    public Card ai(int indexOfUser, Deck.Seed s) {

        for (int c = 0; c < this.assigned.get(indexOfUser).size(); c++) {
            if (moveCheck(s, indexOfUser, this.assigned.get(indexOfUser).get(c))) {
                return this.assigned.get(indexOfUser).get(c);
            }
        }

        return null;

    }

    public int computeHandWinner(Card[] cardOnTable, int firstPlayer) {

        int maxIndex = firstPlayer;

        for (int i = 0; i < 4; i++) {
            if (cardOnTable[i].seed == cardOnTable[firstPlayer].seed && cardOnTable[i].value > cardOnTable[maxIndex].value) {
                maxIndex = i;
            }
        }

        for (int i = 0; i < 4; i++) {
            this.taken.get(maxIndex).add(this.assigned.get(i).remove(this.assigned.get(i).indexOf(cardOnTable[i])));
        }

        return maxIndex;

    }

    public boolean isGameFinished() {
        if (this.assigned.get(0).isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public int computeGameWinner() {

        int[] scores = new int[4];

        for (int user = 0; user < 4; user++) {
            if (this.taken.get(user).contains(new Card("Q", Seed.S))) {
                scores[user] += 13;
            }
            for (int cardIndex = 0; cardIndex < this.taken.get(user).size(); cardIndex++) {
                if (this.taken.get(user).get(cardIndex).seed == Deck.Seed.H) {
                    scores[user]++;
                }
            }
        }

        int minIndex = 0;
        for (int i = 0; i < 4; i++) {
            if (scores[i] < scores[minIndex]) {
                minIndex = i;
            }
        }

        return minIndex;

    }

    private boolean onlyHearts(int indexOfUser) {
        for (Card c : this.assigned.get(indexOfUser)) {
            if (c.seed != Deck.Seed.H) {
                return false;
            }
        }
        return true;
    }

    private boolean onlyPenaltyCards(int indexOfUser) {
        for (Card c : this.assigned.get(indexOfUser)) {
            if (c.seed != Deck.Seed.H && !(c.seed == Deck.Seed.S && c.num.equals("Q"))) {
                return false;
            }
        }
        return true;
    }

    private boolean isPenalityCard(Card c) {
        return (c.seed == Deck.Seed.H || (c.seed == Deck.Seed.S && c.num.equals("Q")));
    }

    private boolean playerHasTheSeed(int indexOfUser, Deck.Seed seed) {
        for (Card c : this.assigned.get(indexOfUser)) {
            if (c.seed == seed) {
                return true;
            }
        }
        return false;
    }
}
