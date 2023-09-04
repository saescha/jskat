package org.jskat.ai.jens;

import org.jskat.ai.AbstractAIPlayer;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.player.ImmutablePlayerKnowledge;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.jskat.util.Rank;
import org.jskat.util.Suit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class GameUtil {
    public boolean log = false;
    private static final Logger l = LoggerFactory.getLogger(AIPlayerJens.class);

    GameType gameType = GameType.PASSED_IN;
    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-hhmmss");
    Date ts = new Date();

    public GameUtil() {
    }

    ImmutablePlayerKnowledge k = null;
    int currentMaxBid = 0;
    boolean playGrand = false;
    boolean playClubs = false;
    boolean playSpades = false;
    boolean playHearts = false;
    boolean playDiamonds = false;
    boolean playZero = false;

    int maxBid(ImmutablePlayerKnowledge knowledge) {
        calculateBid(knowledge);
        return currentMaxBid;
    }

    void calculateBid(ImmutablePlayerKnowledge knowledge) {
        if (k == knowledge) {
            // No need to recalculate
            return;
        } else {
            k = knowledge;
            currentMaxBid = 0;
            playGrand = false;
            playClubs = false;
            playSpades = false;
            playHearts = false;
            playDiamonds = false;
            playZero = false;
        }

        CardList cards = knowledge.getOwnCards();

        cards.sort(GameType.GRAND);

        log("BIDDING CALCULATION\n");
        log("My cards: %s\n", cards);

        // Jack multiplier
        int numJacks = (cards.hasJack(Suit.CLUBS) ? 1 : 0) + (cards.hasJack(Suit.SPADES) ? 1 : 0)
                + (cards.hasJack(Suit.HEARTS) ? 1 : 0) + (cards.hasJack(Suit.DIAMONDS) ? 1 : 0);
        int playJacks = 1;
        boolean have = false;
        if (cards.hasJack(Suit.CLUBS)) {
            have = true;
            playJacks++;
            if (cards.hasJack(Suit.SPADES)) {
                playJacks++;
                if (cards.hasJack(Suit.HEARTS)) {
                    playJacks++;
                    if (cards.hasJack(Suit.DIAMONDS)) {
                        playJacks++;
                    }
                }
            }
        } else {
            playJacks++;
            if (!cards.hasJack(Suit.SPADES)) {
                playJacks++;
                if (!cards.hasJack(Suit.HEARTS)) {
                    playJacks++;
                    if (!cards.hasJack(Suit.DIAMONDS)) {
                        playJacks++;
                    }
                }
            }
        }

        log("Number of Jacks: %d (play: %d)\n", numJacks, playJacks);

        // Game Type
        int gameBaseValue = 0;

        // check grand
        CardList grandCards = knowledge.getOwnCards();
        grandCards.removeAll(CardList.getPerfectGrandSuitHand());
        if (grandCards.size() < 3) {
            // Play grand
            playGrand = true;
            gameBaseValue = 24;

            if (have) {
                currentMaxBid = playJacks * gameBaseValue;
            } else {
                currentMaxBid = (numJacks - 1) * gameBaseValue;
            }
            log("Maxbid %d (GRAND, perfect cards -%d)", currentMaxBid, grandCards.size());
            return;
        }

        int numClubs = cards.getSuitCount(Suit.CLUBS, false);
        boolean clubsAce = cards.contains(Card.CA);
        boolean clubsTen = cards.contains(Card.CT);
        boolean clubsKing = cards.contains(Card.CK);
        boolean clubsQueen = cards.contains(Card.CQ);

        int numSpades = cards.getSuitCount(Suit.SPADES, false);
        boolean spadesAce = cards.contains(Card.SA);
        boolean spadesTen = cards.contains(Card.ST);
        boolean spadesKing = cards.contains(Card.SK);
        boolean spadesQueen = cards.contains(Card.SQ);

        int numHearts = cards.getSuitCount(Suit.HEARTS, false);
        boolean heartsAce = cards.contains(Card.HA);
        boolean heartsTen = cards.contains(Card.HT);
        boolean heartsKing = cards.contains(Card.HK);
        boolean heartsQueen = cards.contains(Card.HQ);

        int numDiamonds = cards.getSuitCount(Suit.DIAMONDS, false);
        boolean diamondsAce = cards.contains(Card.DA);
        boolean diamondsTen = cards.contains(Card.DT);
        boolean diamondsKing = cards.contains(Card.DK);
        boolean diamondsQueen = cards.contains(Card.DQ);

        int valueClubs = (clubsAce ? 5 : 0) + (clubsTen ? 4 : 0) + (clubsKing ? 2 : 0) + (clubsQueen ? 1 : 0)
                + numClubs;
        int valueSpades = (spadesAce ? 5 : 0) + (spadesTen ? 4 : 0) + (spadesKing ? 2 : 0) + (spadesQueen ? 1 : 0)
                + numSpades;
        int valueHearts = (heartsAce ? 5 : 0) + (heartsTen ? 4 : 0) + (heartsKing ? 2 : 0) + (heartsQueen ? 1 : 0)
                + numHearts;
        int valueDiamonds = (diamondsAce ? 5 : 0) + (diamondsTen ? 4 : 0) + (diamondsKing ? 2 : 0)
                + (diamondsQueen ? 1 : 0) + numDiamonds;
        // int allValue = valueClubs + valueSpades + valueHearts + valueDiamonds;
        int numAces = (clubsAce ? 1 : 0) + (spadesAce ? 1 : 0) + (heartsAce ? 1 : 0) + (diamondsAce ? 1 : 0);
        int numTens = (clubsTen ? 1 : 0) + (spadesTen ? 1 : 0) + (heartsTen ? 1 : 0) + (diamondsTen ? 1 : 0);
        

        log("Cards values: C: %d\tS: %d\tH: %d\tD: %d\n", valueClubs, valueSpades, valueHearts, valueDiamonds);

        if (valueClubs < 3 && valueSpades < 3 && valueHearts < 3 && valueDiamonds < 3) {
            // TODO: Play zero
            playZero = true;
            currentMaxBid = 23;
            log("Maxbid %d (ZERO GAME)", currentMaxBid);
            return;

        } else if (valueClubs < 10 && valueSpades < 10 && valueHearts < 10 && valueDiamonds < 10) {
            log("Maxbid %d (NO GAME)", currentMaxBid);
            return;
        }

        boolean bid = false;
        int hesitancy = 0;

        int playValue = 8;
        if (numAces >= 3 || numAces >= 2 && numTens >= 2) {
            playValue--;
        }

        if (valueClubs >= playValue && valueClubs >= valueSpades && valueClubs >= valueHearts
                && valueClubs >= valueDiamonds) {
            bid = true;
            playClubs = true;
            gameBaseValue = 12;
            log("Willing to play Clubs (cards value %d)", valueClubs);
        } else if (valueSpades >= playValue && valueSpades >= valueClubs && valueSpades >= valueHearts
                && valueSpades >= valueDiamonds) {
            bid = true;
            playSpades = true;
            gameBaseValue = 11;
            log("Willing to play Spades (cards value %d)", valueSpades);
        } else if (valueHearts >= playValue && valueHearts >= valueClubs && valueHearts >= valueSpades
                && valueHearts >= valueDiamonds) {
            bid = true;
            playHearts = true;
            gameBaseValue = 10;
            log("Willing to play Hearts (cards value %d)", valueHearts);
        } else if (valueDiamonds >= playValue && valueDiamonds >= valueClubs && valueDiamonds >= valueSpades
                && valueDiamonds >= valueHearts) {
            bid = true;
            playDiamonds = true;
            gameBaseValue = 9;
            log("Willing to play Diamonds (cards value %d)", valueDiamonds);
        } else {
            switch (cards.getMostFrequentSuit()) {
                case CLUBS:
                    gameBaseValue = 12;
                    if (cards.getSuitCount(Suit.CLUBS, false) > 4) {
                        bid = true;
                        hesitancy = 1;
                    } else {
                        bid = false;
                    }
                case SPADES:
                    gameBaseValue = 11;
                    if (cards.getSuitCount(Suit.SPADES, false) > 4) {
                        bid = true;
                        hesitancy = 1;
                    } else {
                        bid = false;
                    }
                case HEARTS:
                    gameBaseValue = 12;
                    if (cards.getSuitCount(Suit.HEARTS, false) > 4) {
                        bid = true;
                        hesitancy = 1;
                    } else {
                        bid = false;
                    }
                case DIAMONDS:
                    gameBaseValue = 12;
                    if (cards.getSuitCount(Suit.DIAMONDS, false) > 4) {
                        bid = true;
                        hesitancy = 1;
                    } else {
                        bid = false;
                    }
            }
        }

        if (!bid) {
            log("Maxbid %d (NOT WILLING TO PLAY)", currentMaxBid);
            return;
        }

        if (have) {
            currentMaxBid = (playJacks - hesitancy) * gameBaseValue;
        } else {
            currentMaxBid = (Math.min(playJacks, numJacks) - hesitancy) * gameBaseValue;
        }
        log("Maxbid %d", currentMaxBid);
    }

    public CardList discard(ImmutablePlayerKnowledge knowledge) {

        final CardList result = new CardList();

        CardList discardableCards = new CardList(knowledge.getOwnCards());

        if (playGrand) {
            gameType = GameType.GRAND;
        } else if (playClubs) {
            gameType = GameType.CLUBS;
        } else if (playSpades) {
            gameType = GameType.SPADES;
        } else if (playHearts) {
            gameType = GameType.HEARTS;
        } else if (playHearts) {
            gameType = GameType.DIAMONDS;
        } else if (playZero) {
            gameType = GameType.NULL;
        } else {
            gameType = GameType.NULL;
        }
        log("gametype set to %s\n", gameType);

        discardableCards.sort(gameType);

        // just discard two random cards
        var disCard = discardableCards.get(discardableCards.size() - 1);
        result.add(disCard);

        disCard = discardableCards.get(discardableCards.size() - 2);
        result.add(disCard);

        log("Discarding: %s\n", result);
        return result;
    }

    public GameAnnouncement setGame() {

        // u.log("position: %d\n", knowledge.getPlayerPosition());
        // u.log("bids: %d, %d, %d\n", knowledge.getHighestBid(Player.FOREHAND),
        // knowledge.getHighestBid(Player.MIDDLEHAND),
        // knowledge.getHighestBid(Player.REARHAND));

        final GameAnnouncementFactory factory = GameAnnouncement.getFactory();

        log("Setting game type: %s\n", gameType);
        factory.setGameType(gameType);

        factory.setOuvert(false);
        factory.setHand(false);
        factory.setSchneider(false);
        factory.setSchwarz(false);

        // select a random game type (without RAMSCH and PASSED_IN)
        // final GameType gameType =
        // GameType.values()[random.nextInt(GameType.values().length - 2)];
        // if (false && Boolean.valueOf(random.nextBoolean())) {
        // factory.setOuvert(true);
        // if (gameType != GameType.NULL) {
        // factory.setHand(true);
        // factory.setSchneider(true);
        // factory.setSchwarz(true);
        // }
        // }

        return factory.getAnnouncement();
    }

    public Card nextCard(AbstractAIPlayer player, ImmutablePlayerKnowledge knowledge) {
        if (playZero) {
            return nextCardZero(player, knowledge);
        } else if (playGrand) {
            return nextCardGrand(player, knowledge);
        } else {
            return nextCardGeneral(player, knowledge);
        }
    }
    
    private Card nextCardGrand(AbstractAIPlayer player, ImmutablePlayerKnowledge knowledge) {
        var gt = knowledge.getGameType();
        CardList trickCards = knowledge.getTrickCards();
        CardList possibleCards = player.getPlayableCards(trickCards);

        possibleCards.sort(gt);

        Card card = null;
        
        if (trickCards.size() == 0) {
            Card lowest = null;
            for (Card c : possibleCards) {
                if (c == Card.CA || c == Card.SA || c == Card.HA || c == Card.DA) {
                    card = c;
                }
                if (lowest == null || c.getPoints() < lowest.getPoints()) {
                    card = c;
                }
            }

            if (card == null)  {
                card = lowest;
            }
            return card;
        } else {
            return nextCardGeneral(player, knowledge);
        }
    }

    private Card nextCardZero(AbstractAIPlayer player, ImmutablePlayerKnowledge knowledge) {
        var gt = knowledge.getGameType();
        CardList trickCards = knowledge.getTrickCards();
        CardList possibleCards = player.getPlayableCards(trickCards);

        possibleCards.sort(gt);
        

        Card card = possibleCards.get(possibleCards.size() - 1);

        if (trickCards.size() == 0) {
            for (Card c : possibleCards) {
                if (!c.beats(gameType, card)) {
                    card = c;
                }
            }
        } else if (trickCards.size() == 1) {
            for (Card c : possibleCards) {
                if (!c.beats(gameType, trickCards.get(0)) && !c.beats(gameType, card) && c.beats(gameType, card)) {
                    card = c;
                }
            }
        } else if (trickCards.size() == 2) {
            for (Card c : possibleCards) {
                if (!c.beats(gameType, trickCards.get(0)) && !c.beats(gameType, trickCards.get(1)) && c.beats(gameType, card)) {
                    card = c;
                }
            }
        }

        String trick1 = trickCards.size() > 0 ? trickCards.get(0).toString() : "__";
        String trick2 = trickCards.size() > 1 ? trickCards.get(1).toString() : "__";
        log("Stack is %s, %s (%02d)- playing %s of %s\n", trick1, trick2, trickCards.getTotalValue(), card, possibleCards);
        return card;
    }

    private Card nextCardGeneral(AbstractAIPlayer player, ImmutablePlayerKnowledge knowledge) {
        var gt = knowledge.getGameType();

        // first find all possible cards
        CardList trickCards = knowledge.getTrickCards();
        CardList possibleCards = player.getPlayableCards(trickCards);

        possibleCards.sort(gt);

        Card card = null;
        try {
            if (trickCards.size() == 0) {
                for (Card c : possibleCards) {
                    if (!c.isTrump(gameType)) {
                        boolean bail = false;
                        for (Player p : knowledge.getOpponentPartyMembers()) {
                            if (!knowledge.couldHaveSuit(p, c.getSuit())) {
                                bail = true;
                            }
                        }
                        if (bail) {
                            break;
                        }


                        Suit s = c.getSuit();
                        Card ace = Card.getCard(s, Rank.ACE);
                        Card ten = Card.getCard(s, Rank.TEN);
                        Card king = Card.getCard(s, Rank.KING);

                        if (
                            c.getRank() == Rank.ACE ||
                            c.getRank() == Rank.TEN && !knowledge.isCardOutstanding(ace) ||
                            c.getRank() == Rank.KING && !knowledge.isCardOutstanding(ace) && !knowledge.isCardOutstanding(ten) ||
                            c.getRank() == Rank.KING && !knowledge.isCardOutstanding(ace) && !knowledge.isCardOutstanding(ten) && !knowledge.isCardOutstanding(king)
                        ) {
                            card = c;
                        }
                    }
                }

                if (card == null) {
                    card = possibleCards.get(possibleCards.size() - 1);
                }
            } else {
                for (int i = 0; i < possibleCards.size(); i++) {
                    var c = possibleCards.get(i);

                    Boolean beatsTrick = (trickCards.size() == 1 && c.beats(gt, trickCards.get(0))) ||
                            (trickCards.size() == 2 && c.beats(gt, trickCards.get(0))
                                    && c.beats(gt, trickCards.get(1)));

                    if (beatsTrick && (!c.name().endsWith("J") || trickCards.getTotalValue() >= 10)) {
                        card = c;
                        break;
                    }
                }
            }

        } catch (Exception ex) {
            log("WTF? %s - %s\n", ex.getMessage(), ex.getStackTrace().toString());
        }

        if (card == null) {
            card = possibleCards.get(possibleCards.size() - 1);
        }

        String trick1 = trickCards.size() > 0 ? trickCards.get(0).toString() : "__";
        String trick2 = trickCards.size() > 1 ? trickCards.get(1).toString() : "__";
        log(
            "Stack is %s, %s (%02d)- playing %s of %s\n",
            trick1, trick2, trickCards.getTotalValue(), card, possibleCards
        );
        return card;

    }

    public void log(String message, Object... args) {
        if (!log) {
            return;
        }

        
        String logFile = String.format("skat-%s-%s.log", gameType, df.format(ts));
        try {
            FileWriter fw = new FileWriter(logFile, true);
            fw.write(String.format("[Jens] " + message, args));
            fw.flush();
            fw.close();
        }catch(Exception ex) {
            l.error("WTF? {}", ex);    
        }

        l.info(String.format("[Jens] " + message, args));
    }
}
