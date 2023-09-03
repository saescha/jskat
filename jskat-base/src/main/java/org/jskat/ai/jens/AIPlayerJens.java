package org.jskat.ai.jens;

import java.sql.Struct;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.jskat.ai.AbstractAIPlayer;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.data.GameSummary;
import org.jskat.player.ImmutablePlayerKnowledge;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.jskat.util.Suit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// import java.util.Random;

/**
 * Random player for testing purposes and driving the other players nuts.
 */
public class AIPlayerJens extends AbstractAIPlayer {
    private GameUtil u = new GameUtil();

    /**
     * Random generator for decision making.
     */
    // private final Random random = new Random();

    /**
     * Creates a new instance of AIPlayerRND.
     */
    public AIPlayerJens() {

        this("JensBot");
    }

    /**
     * Creates a new instance of AIPlayerRND.
     *
     * @param newPlayerName Player's name
     */
    public AIPlayerJens(final String newPlayerName) {
        setPlayerName("JensBot");
    }


    @Override
    public boolean pickUpSkat() {
        u.log("Method: pickUpSkat\n");

        return true;
    }

    @Override
    public boolean playGrandHand() {
        u.log("Method: playGrandHand\n");

        return false;
    }

    @Override
    public GameAnnouncement announceGame() {
        u.log("Method: announceGame\n");

        return u.setGame();
    }
    @Override
    public int bidMore(final int nextBidValue) {
        u.log("Method: bidMore\n");

        return u.maxBid(knowledge) >= nextBidValue ? nextBidValue : 0;
    }

    @Override
    public boolean holdBid(final int currBidValue) {
        u.log("Method: holdBid\n");

        return u.maxBid(knowledge) >= currBidValue;
    }

    int gameNum = 1;

    @Override
    public void startGame() {
        u.log("Method: startGame\n");
        u.log("Gametype: %s - Got: %s\n", knowledge.getGameType(), knowledge.getOwnCards());
        
    }

    @Override
    public Card playCard() {
        u.log("Method: playCard\n");
        return u.nextCard(this, knowledge);
    }

 
    @Override
    public CardList getCardsToDiscard() {
        u.log("Method: getCardsToDiscard\n");
        return u.discard(knowledge);
    }

    @Override
    public void prepareForNewGame() {
        setPlayerName("JensBot");

        u = new GameUtil();
        u.log("Method: prepareForNewGame\n");
        u.log("------ NEW GAME (%d) -------\n", gameNum++);
    }

    @Override
    public void finalizeGame() {
        u.log("Method: finalizeGame\n");
        // nothing to do for AIPlayerRND
    }

    @Override
    public boolean callContra() {
        u.log("Method: callContra\n");
        return false;
    }

    @Override
    public boolean callRe() {
        u.log("Method: callRe\n");

        return false;
    }


    // private class PlayerCard {
    //     public Player player = null;
    //     public Card card = null;

    //     PlayerCard(Player p, Card c) {
    //         player = p;
    //         card = c;
    //     }
    // }
    // private Vector<PlayerCard> trick = new Vector<PlayerCard>();
    // private HashMap<Player, Integer> scores = new HashMap<Player, Integer>();

    // @Override
    // /**
    //  * Informs the player about a card that was played
    //  *
    //  * @param player Player who played the card
    //  * @param card   Card that was played
    //  */
    // public void cardPlayed(Player player, Card card) {
    //     u.log("Method: cardPlayed\n");
    //     super.cardPlayed(player, card);

    //     trick.add(new PlayerCard(player, card));
    //     if (trick.size() == 3) {
    //         // Full
    //         var gt = knowledge.getGameType();
    //         var t1 = trick.get(0);
    //         var t2 = trick.get(1);
    //         var t3 = trick.get(2);
    //         var trickValue = knowledge.getTrickCards().getTotalValue();


    //         if (t1.card.beats(gt, t2.card) && t1.card.beats(gt, t3.card)) {
    //             scores.replace(t1.player, scores.get(t1.player) + trickValue);
    //             u.log("Trick goes to %s for %d", t1.player.name(), knowledge.getTrickCards());
    //         } else if (t2.card.beats(gt, t1.card) && t2.card.beats(gt, t3.card)) {
    //             scores.replace(t2.player, scores.get(t2.player) + trickValue);
    //             u.log("Trick goes to %s for %d", t2.player.name(), knowledge.getTrickCards());
    //         } else if (t3.card.beats(gt, t1.card) && t3.card.beats(gt, t2.card)) {
    //             scores.replace(t3.player, scores.get(t3.player) + trickValue);
    //             u.log("Trick goes to %s for %d", t3.player.name(), knowledge.getTrickCards());
    //         } else {
    //             u.log("WTF? Trick goes to NOONE??? for %d", knowledge.getTrickCards());
    //         }

    //         trick = new Vector<PlayerCard>();
    //     }
    // }  


    // @Override
    // public void setGameSummary(final GameSummary gameSummary) {
    //     super.setGameSummary(gameSummary);
    // }

}
