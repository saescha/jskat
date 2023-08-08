package org.jskat.ai.sascha.solo;

import org.jskat.ai.newalgorithm.AlgorithmAI;
import org.jskat.ai.sascha.AbstractPlayer;
import org.jskat.ai.sascha.Util;
import org.jskat.data.Trick;
import org.jskat.player.ImmutablePlayerKnowledge;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Rank;
import org.jskat.util.Suit;

public class SuitPlayer extends AbstractPlayer {
    private boolean ignoreTrump;
    private int trumpCount;
    private boolean pulldown;

    public SuitPlayer(final AlgorithmAI p, final ImmutablePlayerKnowledge k) {
        super(p, k);
        trumpCount = Util.countJacks(k.getOwnCards()) + k.getOwnCards().getSuitCount(k.getTrumpSuit(), false);
        pulldown = (trumpCount < 5);
    }

    @Override
    protected Card foreHand() {
        if (pulldown)
            return pullDown();
        if (ignoreTrump) {
            return clearSuite();
        } else {
            return drawTump();
        }

    }

    @Override
    protected Card midHand(Card firstCard) {
        var sh = suits.get(firstCard.getSuit());
        if (sh == null) {
            return reactTrump();
        }
        if (sh.isEmpty()) {
            if (firstCard.getPoints() < 4 && shouldDiscard()) {
                return discardCard();
            } else {
                return trumpSuitCard();
            }
        } else {
            return midSuitCard(firstCard);
        }
    }

    private Card reactTrump(){
        if (k.getOwnCards().hasTrump(k.getGameType())){
            return drawTump();
        }else{
            return discardCard();
        }
    }

    private Card midSuitCard(Card firstCard) {
        SuitHelper sh = suits.get(firstCard.getSuit());
        if (sh.hasHighest())
            return sh.getPullCard();

        return sh.getDiscardCard();
    }

    private Card rearSuitCard(Card firstCard, Card secondCard) {
        SuitHelper sh = suits.get(firstCard.getSuit());
        if (sh == null) {
            return reactTrump();
        }
        if (sh.hasHighest())
            return sh.getPullCard();

        return sh.getDiscardCard();
    }

    private Card trumpSuitCard() {

        var order = trumpClearOrder();
        Card r = discardCard();

        for (Card c : order) {
            if (k.getOwnCards().contains(c))
                r = c;
        }

        return r;
    }

    private boolean shouldDiscard() {
        int discardPriority = 0;
        for (SuitHelper sh : this.suits.values()) {
            if (sh.getDiscardPriority() > discardPriority)
                discardPriority = sh.getDiscardPriority();
        }
        return (discardPriority > 3 || discardPriority > 0 && trumpCount > 5);
    }

    private Card discardCard() {
        Card r = getPlayableCard();
        int discardPriority = 0;
        for (SuitHelper sh : this.suits.values()) {
            if (sh.getDiscardPriority() > discardPriority) {
                discardPriority = sh.getDiscardPriority();
                r = sh.getDiscardCard();
            }
        }
        return r;
    }

    @Override
    protected Card rearHand(Card firstCard, Card seconCard) {
        var sh = suits.get(firstCard.getSuit());
        if (sh.isEmpty()) {
            if (firstCard.getPoints() + seconCard.getPoints() < 7 && shouldDiscard()) {
                return discardCard();
            } else {
                return trumpSuitCard();
            }
        } else {
            return rearSuitCard(firstCard, seconCard);
        }
    }

    private Card pullDown() {
        for (SuitHelper sh : this.suits.values()) {
            if (sh.isUnbeatable())
                return sh.getPullCard();
        }
        for (SuitHelper sh : this.suits.values()) {
            if (sh.hasHighest())
                return sh.getPullCard();
        }

        return getPlayableCard();
    }

    private Card getPlayableCard() {

        boolean isCardAllowed;
        var trick = k.getCurrentTrick().getCardList();

        for (final Card card : k.getOwnCards()) {
            if (trick.size() > 0 &&
                    rules.isCardAllowed(k.getGameType(), trick.get(0), k.getOwnCards(), card)) {
                isCardAllowed = true;
            } else {
                isCardAllowed = trick.size() == 0;
            }

            if (isCardAllowed) {
                return card;
            }
        }
        return null;

    }

    private boolean hasOpponentBeatingTrump(Card card) {

        for (Card c : oppCardList) {
            if (rules.isCardBeatsCard(k.getGameType(), card, c))
                return true;
        }
        return false;
    }

    private CardList suitPulldownOrder(Suit s) {

        return new CardList(Card.getCard(s, Rank.ACE), Card.getCard(s, Rank.TEN), Card.getCard(s, Rank.KING),
                Card.getCard(s, Rank.QUEEN), Card.getCard(s, Rank.NINE), Card.getCard(s, Rank.EIGHT),
                Card.getCard(s, Rank.SEVEN));
    }

    private CardList trumpClearOrder() {
        var s = k.getTrumpSuit();
        CardList result = new CardList(Card.DJ, Card.HJ, Card.getCard(s, Rank.NINE),
                Card.getCard(s, Rank.SEVEN), Card.getCard(s, Rank.EIGHT), Card.getCard(s, Rank.QUEEN),
                Card.getCard(s, Rank.KING), Card.SJ, Card.CJ, Card.getCard(s, Rank.TEN), Card.getCard(s, Rank.ACE));

        return result;
    }

    private CardList trumpPulldownOrder() {
        CardList result = new CardList(Card.CJ, Card.SJ, Card.HJ, Card.DJ);
        result.addAll(suitPulldownOrder(k.getTrumpSuit()));
        return result;
    }

    private Card clearSuite() {
        for (SuitHelper sh : this.suits.values()) {
            if (!sh.isUnbeatable() && sh.hasHighest() && !sh.isEmpty())
                return sh.getClearCard();
        }
        for (SuitHelper sh : this.suits.values()) {
            if (!sh.isUnbeatable() && !sh.isEmpty() && sh.getStartingSize() > 2)
                return sh.getClearCard();
        }
        for (SuitHelper sh : this.suits.values()) {
            if (!sh.isUnbeatable() && !sh.isEmpty())
                return sh.getClearCard();
        }

        return pullDown();
    }

    private Card drawTump() {

        int oppTrmpCount = (11 - k.getTrumpCount());
        k.getOwnCards().sort(k.getGameType());
        if (k.getTrumpCount() > 8) {

        }

        int unbeatable = 0;

        for (Card c : k.getOwnCards()) {
            if (!hasOpponentBeatingTrump(c))
                unbeatable++;
            else
                break;
        }
        var order = trumpPulldownOrder();

        if (Math.ceil(oppTrmpCount / 2.0) > unbeatable) {
            order = trumpClearOrder();
        }

        for (Card c : order) {
            if (k.getOwnCards().contains(c))
                return c;
        }
        return getPlayableCard();
    }

    @Override
    protected void beforeCard() {
        if (k.getTrumpCount() == 11)
            ignoreTrump = true;
    }

    @Override
    protected void afterTrick(Trick t) {

    }
}
