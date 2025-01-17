package org.jskat.ai.sascha;

import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Rank;
import org.jskat.util.Suit;
import java.util.function.Function;

public final class Util {

    public static int suitePoints(final CardList pCards, final Suit pSuit) {
        return filterSuite(pCards, pSuit).getTotalValue();
    }

    public static int suiteNullWeakness(final CardList pCards, final Suit pSuit) {
        final CardList c = filterSuite(pCards, pSuit);

        Function<Rank, Boolean> has = (Rank r) -> {
            return c.contains(Card.getCard(pSuit, r));
        };
        if (c.size() == 0) {
            return 0;
        }
        if (has.apply(Rank.SEVEN)) {
            if (c.size() == 1)
                return 0;
            if (has.apply(Rank.EIGHT)) {
                if (c.size() == 2)
                    return 0;
                if (has.apply(Rank.NINE) || has.apply(Rank.TEN) || has.apply(Rank.JACK))
                    return 0;
            }
            if (has.apply(Rank.NINE)) {
                if (c.size() == 2)
                    return 0;
                if (has.apply(Rank.TEN) || has.apply(Rank.JACK))
                    return 0;
            }
            return c.size() - 1;
        }
        if (has.apply(Rank.EIGHT)) {
            if (c.size() == 1)
                return 0;
        }

        return c.size();
    }

    public static int suiteWeakness(final CardList pCards, final Suit pSuit) {
        final CardList c = filterSuite(pCards, pSuit);

        Function<Rank, Boolean> has = (Rank r) -> {
            return c.contains(Card.getCard(pSuit, r));
        };
        if (c.size() == 0) {
            return 0;
        }

        if (has.apply(Rank.ACE)) {
            if (c.size() == 1)
                return 0;
            if (has.apply(Rank.TEN))
                return 0;
            return 1;
        }
        if (c.size() == 1)
            return 4;

        if (has.apply(Rank.TEN)) {
            if (has.apply(Rank.KING) || c.size() > 2)
                return 2;
            return 3;
        }

        return 8;
    }

    public static int suiteStrengh(final CardList pCards, final Suit pSuit) {

        final CardList c = filterSuite(pCards, pSuit);

        if (c.size() == 0)
            return 0;

        if (c.contains(Card.getCard(pSuit, Rank.ACE))) {
            if (c.contains(Card.getCard(pSuit, Rank.TEN)))
                return 3;
            return 2;

        } else {
            if (c.contains(Card.getCard(pSuit, Rank.TEN))) {
                if (c.contains(Card.getCard(pSuit, Rank.KING)))
                    return 1;
                return -1;
            }
        }
        return -2;
    }

    public static String makeReadable(CardList cl) {
        String r = "[ ";
        for (Card c : cl) {
            r = r + c.getSuit().getShortString() + c.getRank().getShortString() + "; ";
        }
        return r + "]";
    }

    public static final CardList filterSuiteNull(final CardList pCards, final Suit pSuit) {
        CardList r = new CardList();
        pCards.forEach((c) -> {
            if (c.getSuit() == pSuit) {
                r.add(c);
            }
        });
        return r;
    }

    public static final CardList filterSuite(final CardList pCards, final Suit pSuit) {
        CardList r = new CardList();
        pCards.forEach((c) -> {
            if (c.getSuit() == pSuit && c.getRank() != Rank.JACK) {
                r.add(c);
            }
        });
        return r;
    }

    public static GameType GameTypeOfSuit(Suit s) {
        switch (s) {
            case CLUBS:
                return GameType.CLUBS;
            case DIAMONDS:
                return GameType.DIAMONDS;
            case HEARTS:
                return GameType.HEARTS;
            case SPADES:
                return GameType.SPADES;
            default:
                return GameType.GRAND;
        }

    }

    public static CardList getJacks(CardList cl) {
        var r = new CardList();
        for (Card c : cl) {
            if (c.getRank() == Rank.JACK)
                r.add(c);
        }
        return r;
    }

    public static int countJacks(CardList pCards) {
        int counter = 0;
        for (Card lCard : pCards) {
            if (lCard.getRank() == Rank.JACK) {
                counter++;
            }
        }
        return counter;
    }

}
