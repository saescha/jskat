package org.jskat.ai.sascha;

import java.util.Arrays;
import static org.assertj.core.api.Assertions.assertThat;

import org.jskat.ai.sascha.solo.SuitHelper;
import org.jskat.data.Trick;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Player;
import org.jskat.util.Rank;
import org.jskat.util.Suit;
import org.junit.jupiter.api.Test;

public class SuiteHelperTest {
    @Test
    public void testAlmostUnbeatable() {
        final CardList cards = new CardList(Arrays.asList(Card.CJ, Card.DJ, Card.CA, Card.CK, Card.CQ, Card.C8, Card.SQ,
                Card.HT, Card.H8, Card.D9));

        SuitHelper cut = new SuitHelper(Suit.CLUBS, cards);
        assertThat(cut.isUnbeatable()).isFalse();
        assertThat(cut.hasHighest()).isTrue();
        assertThat(cut.getThrowPriority()).isEqualTo(-1);
        assertThat((cut.getPullCard() == Card.CA)).isTrue();
        assertThat((cut.getClearCard())).isEqualTo(Card.CQ);
        assertThat(cut.getNeededClears()).isEqualTo(1);
        assertThat(cut.getNeededClears()).isEqualTo(1);
        assertThat(cut.isOwn(Rank.QUEEN)).isTrue();

        assertThat(cut.estimateLostTricks()).isEqualTo(1);
        assertThat(cut.comebacks()).isEqualTo(1);
        assertThat(cut.getThrowPriority()).isEqualTo(-1);
        // assertThat(cut.neededClears()).isEqualTo(1);
        assertThat(cut.getNeededClears()).isEqualTo(1);
        Trick t = new Trick(0, Player.FOREHAND);

        t.addCard(Card.CQ);
        t.addCard(Card.C7);
        t.addCard(Card.C9);
        cut.registerTrick(t);

        assertThat(cut.isUnbeatable()).isTrue();

        assertThat(cut.isOwn(Rank.QUEEN)).isFalse();
    }

    @Test
    public void testUnbeatable() {
        final CardList cards = new CardList(Arrays.asList(Card.HA, Card.HT, Card.HK, Card.H7));
        SuitHelper cut = new SuitHelper(Suit.HEARTS, cards);

        assertThat(cut.isUnbeatable()).isTrue();
        assertThat(cut.getClearCard()).isNull();
        assertThat(cut.getNeededClears()).isEqualTo(0);

    }

    @Test
    public void testAceTenSeven() {
        final CardList cards = new CardList(Arrays.asList(Card.HA, Card.HT, Card.H7));
        SuitHelper cut = new SuitHelper(Suit.HEARTS, cards);

        assertThat(cut.isUnbeatable()).isFalse();
        assertThat(cut.getClearCard()).isEqualTo(Card.H7);
        assertThat(cut.getNeededClears()).isEqualTo(0);

    }

    @Test
    public void testAceSeven() {
        final CardList cards = new CardList(Arrays.asList(Card.HA, Card.H7));
        SuitHelper cut = new SuitHelper(Suit.HEARTS, cards);

        assertThat(cut.isUnbeatable()).isFalse();
        assertThat(cut.getClearCard()).isEqualTo(Card.H7);
        assertThat(cut.getNeededClears()).isEqualTo(0);

    }

    @Test
    public void testAceNine() {
        final CardList cards = new CardList(Arrays.asList(Card.CJ, Card.DJ, Card.CA, Card.C9, Card.SQ,
                Card.HT, Card.H8, Card.D9));

        SuitHelper cut = new SuitHelper(Suit.CLUBS, cards);
        assertThat(cut.isUnbeatable()).isFalse();
        assertThat(cut.hasHighest()).isTrue();
        assertThat(cut.getThrowPriority()).isEqualTo(-1);
        assertThat((cut.getPullCard() == Card.CA)).isTrue();
        assertThat((cut.getThrowCard() == Card.C9)).isTrue();
    }

    @Test
    public void testBlankSeven() {
        final CardList cards = new CardList(Arrays.asList(Card.CJ, Card.DJ, Card.HA, Card.C7, Card.SQ,
                Card.HT, Card.H8, Card.D9));

        SuitHelper cut = new SuitHelper(Suit.CLUBS, cards);
        assertThat(cut.isUnbeatable()).isFalse();
        assertThat(cut.hasHighest()).isFalse();
        assertThat(cut.getThrowPriority()).isEqualTo(10);
        assertThat((cut.getThrowCard() == Card.C7)).isTrue();
    }

    @Test
    public void testTenSeven() {
        final CardList cards = new CardList(Arrays.asList(Card.CJ, Card.DJ, Card.HA, Card.CT, Card.C7, Card.SQ,
                Card.HT, Card.H8, Card.D9));

        SuitHelper cut = new SuitHelper(Suit.CLUBS, cards);
        assertThat(cut.isUnbeatable()).isFalse();
        assertThat(cut.hasHighest()).isFalse();
        assertThat(cut.getThrowPriority()).isEqualTo(-1);
        assertThat((cut.getClearCard())).isEqualTo(Card.C7);
        assertThat((cut.getClearCard())).isEqualTo(Card.C7);
    }

}
