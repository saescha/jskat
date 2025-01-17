package org.jskat.ai.sascha.opponent;

import org.jskat.ai.sascha.AbstractPlayer;
import org.jskat.data.Trick;
import org.jskat.player.ImmutablePlayerKnowledge;
import org.jskat.util.Card;

public class LeftOpponentGrand extends AbstractPlayer {

    public LeftOpponentGrand(ImmutablePlayerKnowledge k) {
        super(k);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected Card foreHand() {
        return getPlayableCard();
    }

    @Override
    protected Card midHand(Card firstCard) {
        return getPlayableCard();
    }

    @Override
    protected Card rearHand(Card firstCard, Card secondCard) {
        return getPlayableCard();
    }

    @Override
    protected void beforeCard() {

    }

    @Override
    protected void afterTrick(Trick t) {

    }

}
