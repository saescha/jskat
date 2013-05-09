/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.12.0
 * Copyright (C) 2013-05-09
 *
 * Licensed under the Apache License, Version 2.0. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jskat.ai.newalgorithm;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Rank;
import org.jskat.util.Suit;

public class AlgorithmGrand extends AbstractAlgorithmAI {
	private static final Logger log = Logger.getLogger(AlgorithmGrand.class);

	AlgorithmGrand(final AlgorithmAI p, GameType pGameType) {
		super(p, pGameType);
		
		log.debug("Defining player <" + myPlayer.getPlayerName() + "> as " + this.getClass().getName());
	}

	protected Card startGame() {
		System.out.println("Grand starts Game: "+knowledge.getPlayerPosition());
		
		return playStartGameCard(
				knowledge.getOwnCards(),
				knowledge.getTrickCards(),
				oPlayedCards,
				oNotOpponentCards,
				oSituation);
	}

	protected Card playForehandCard() {
		System.out.println("Grand plays Forehand-Card: "+knowledge.getPlayerPosition());
		
		return playForehandCard(
				knowledge.getOwnCards(),
				knowledge.getTrickCards(),
				oPlayedCards,
				oNotOpponentCards,
				oSituation);
	}

	protected Card playMiddlehandCard() {
		System.out.println("Grand plays Middlehand-Card: "+knowledge.getPlayerPosition());
		
		return playMiddlehandCard(
				knowledge.getOwnCards(),
				knowledge.getTrickCards(),
				oPlayedCards,
				oNotOpponentCards,
				oSituation);
	}

	protected Card playRearhandCard() {
		System.out.println("Grand plays Rearhand-Card: "+knowledge.getPlayerPosition());
		
		return playRearhandCard(
				knowledge.getOwnCards(),
				knowledge.getTrickCards(),
				oPlayedCards,
				oNotOpponentCards,
				oSituation);
	}

	@Override
	public CardList discardSkat(BidEvaluator pBid) {
		System.out.println("discardSkat");
		
		CardList tDiscardCards		= discardSkatCards(pBid, knowledge.getOwnCards());
//		knowledge.removeOwnCards(tDiscardCards.getImmutableCopy());
		
		oSituation.setCardsAfterDiscarding(knowledge.getOwnCards());
		
		return tDiscardCards;
	}

	// static methods for creating JUnit-tests and test cardplaybehavior
	public static Card playStartGameCard(CardList pCards, CardList pTrickCards, CardList pPlayedCards, CardList pNotOpponentCards, Situation pSituation) {
		pCards.sort(pSituation.getGameType());
		
		if(Helper.countJacks(pCards) == 4)
			return pCards.get((int)(Math.random()*3+1));
		else if(pCards.contains(Card.CJ) && (pCards.contains(Card.HJ) || pCards.contains(Card.DJ)))
			return pCards.get(0);
		else if(pCards.get(1) == Card.SJ)
			return pCards.get(1);
		else if(pCards.get(0) == Card.SJ)
			return pCards.get((int)(Math.random()*Helper.countJacks(pCards)));
		
		return playForehandCard(pCards, pTrickCards, pPlayedCards, pNotOpponentCards, pSituation);
	}
	
	public static Card playForehandCard(CardList pCards, CardList pTrickCards, CardList pPlayedCards, CardList pNotOpponentCards, Situation pSituation) {
		pCards.sort(pSituation.getGameType());
		
		CardList possibleCards = new CardList();

		// Count Aces
		int tAcesCount	= 0;
		if(pCards.contains(Card.CA))	tAcesCount++;	// Clubs
		if(pCards.contains(Card.SA))	tAcesCount++;	// Spades
		if(pCards.contains(Card.HA))	tAcesCount++;	// Hearts
		if(pCards.contains(Card.DA))	tAcesCount++;	// Diamonds
		
		// Count 10Duos (10 mit niedriger Karte)
		int t10DuoCount	= 0;
		if(!pCards.contains(Card.CA) && pCards.contains(Card.CT) && pCards.getSuitCount(Suit.CLUBS, false) > 1)		t10DuoCount++;	// Clubs
		if(!pCards.contains(Card.SA) && pCards.contains(Card.ST) && pCards.getSuitCount(Suit.SPADES, false) > 1)	t10DuoCount++;	// Spades
		if(!pCards.contains(Card.HA) && pCards.contains(Card.HT) && pCards.getSuitCount(Suit.HEARTS, false) > 1)	t10DuoCount++;	// Hearts
		if(!pCards.contains(Card.DA) && pCards.contains(Card.DT) && pCards.getSuitCount(Suit.DIAMONDS, false) > 1)	t10DuoCount++;	// Diamonds
		
		// Wenn nur noch Buben und eine weitere Karte -> weitere Karte wird zuletzt gespielt
		if(pCards.size() == Helper.countJacks(pCards)+1)
			return pCards.get(0);
		// Wenn noch ein Bube beim Gegner
		else if(Helper.countJacks(pNotOpponentCards) == 3 && pCards.get(0).getRank() == Rank.JACK) {
			// Wenn schlagbar
			if(pCards.get(0).getSuit() == Suit.CLUBS
					|| (pCards.get(0).getSuit() == Suit.SPADES && pPlayedCards.contains(Card.CJ))
					|| (pCards.get(0).getSuit() == Suit.HEARTS && pPlayedCards.contains(Card.CJ) && pPlayedCards.contains(Card.SJ))) {
				return pCards.get(0);
			}
			// Wenn nicht schlagbar aber 4 Asse oder 10Duo
			if(tAcesCount + t10DuoCount + pSituation.getBlankSuits().size() == 4) {

				// lange Suit spielen, bis gestochen wird (in 2 von 3 Faellen wird lange Farbe gespielt)
				if(pSituation.getRandomInt() != 0 && pCards.getSuitCount(pSituation.getLongestSuit(), false) > 0) {
					// Wenn hoechste Karte der Farbe vorhanden ist
					// UND bei den Gegnern noch mindestens 2 Karten der Farbe
					// UND beide Gegner sind noch nicht Blank auf der Farbe
					// (Wenn sie keine Truempfe mehr besitzen wird auch mit niedrigen Karten die Farbe leer gespielt)
					if(Helper.isHighestSuitCard(pCards.get(pCards.getFirstIndexOfSuit(pSituation.getLongestSuit(), false)), pPlayedCards, pTrickCards) 
							&& 7-pNotOpponentCards.getSuitCount(pSituation.getLongestSuit(), false) >= 2
							&& !pSituation.isLeftPlayerBlankOnColor(pSituation.getLongestSuit())
							&& !pSituation.isRightPlayerBlankOnColor(pSituation.getLongestSuit()))
						return pCards.get(pCards.getFirstIndexOfSuit(pSituation.getLongestSuit(), false));
					return pCards.get(pCards.getLastIndexOfSuit(pSituation.getLongestSuit(), false));
				}
				// erst alle Karten spielen, wo die zweite Karte anschlieÃƒÅ¸end die beste ist
				for(Suit lSuit : Suit.values()) {
					if(pCards.getSuitCount(lSuit, false) > 1) {
						Card possibleHighCard	= pCards.get(pCards.getFirstIndexOfSuit(lSuit, false));
						Card possibleSecondCard	= pCards.get(pCards.getFirstIndexOfSuit(lSuit, false)+1);
						
						CardList tAfterHighCard	= new CardList(pPlayedCards);
						tAfterHighCard.add(possibleHighCard);
						
						if(Helper.isHighestSuitCard(possibleHighCard, null, pPlayedCards, pTrickCards)
								&& Helper.isHighestSuitCard(possibleSecondCard, null, tAfterHighCard, pTrickCards))
							return possibleHighCard;
					}
				}
			}
		}
		// Wenn nicht schlagbar aber selbst noch 2 Buben
		else if(Helper.countJacks(pNotOpponentCards) == 3 && pCards.get(1).getRank() == Rank.JACK) {
			return pCards.get(0);
		}
		// Wenn keine Buben mehr beim Gegner
		else if(Helper.countJacks(pNotOpponentCards) == 4) {
			for(Suit lSuit : Suit.values()) {
				int  suitCount			= pCards.getSuitCount(lSuit, false);
				if(suitCount == 0)
					continue;
				Card possibleHighCard	= pCards.get(pCards.getFirstIndexOfSuit(lSuit, false));
				Card possibleLowCard	= pCards.get(pCards.getLastIndexOfSuit(lSuit, false));
				
				if(Helper.isHighestSuitCard(possibleHighCard, pSituation.getGameType(), pPlayedCards, pTrickCards)
						&& !(suitCount == 2 && possibleLowCard.getPoints() == 0))
					possibleCards.add(possibleHighCard);
			}
		}
		
		if(!possibleCards.isEmpty())
			return playRandomCard(possibleCards);
		
		return getRandomAllowedCard(pCards, null, pSituation.getGameType());
	}
	
	public static Card playMiddlehandCard(CardList pCards, CardList pTrickCards, CardList pPlayedCards, CardList pNotOpponentCards, Situation pSituation) {
		pCards.sort(pSituation.getGameType());
		Card tCardToBeat					= pTrickCards.get(0);
		Suit tSuit							= tCardToBeat.getSuit();
		
		CardList possibleCards				= new CardList();

		// Wenn ein Bube aufgespielt wurde
		if(tCardToBeat.getRank() == Rank.JACK && pCards.get(0).getRank() == Rank.JACK) {
			if(pCards.get(0).beats(pSituation.getGameType(), tCardToBeat))
				return getLowestBeatingCard(pCards, pSituation.getGameType(), tCardToBeat);
			return pCards.get(Helper.countJacks(pCards)-1);
		}
		// Wenn Karte bedient werden kann/muss
		if(pCards.getSuitCount(tSuit, false) > 0) {
			// Wenn schlagbar
			if(Helper.isHighestSuitCard(pCards.get(pCards.getFirstIndexOfSuit(tSuit, false)), pPlayedCards, pTrickCards)) {
				return pCards.get(pCards.getFirstIndexOfSuit(tSuit, false));
			}
			// Wenn nicht schlagbar
			return pCards.get(pCards.getLastIndexOfSuit(tSuit, false));
		}
		// Wenn Farbe blank ist
		// Wenn >= 10 Punkte im Stich && (kein Bube beim Gegner || CJ und noch mindestens ein weiterer Bube)
		if(tCardToBeat.getPoints() >= 10
				&& (Helper.countJacks(pNotOpponentCards) == 4
					|| (Helper.countJacks(pNotOpponentCards) >= 2 && pCards.contains(Card.CJ) && pCards.get(1).getRank() == Rank.JACK))) {
			return pCards.get(Helper.countJacks(pCards)-1);
		}
		
		possibleCards = getPossibleMaxValueCards(pCards, 0);
		if(!possibleCards.isEmpty())
			return playRandomCard(possibleCards);
		possibleCards = getPossibleMaxValueCards(pCards, 3);
		if(!possibleCards.isEmpty())
			return playRandomCard(possibleCards);
		possibleCards = getPossibleMaxValueCards(pCards, 4);
		if(!possibleCards.isEmpty())
			return playRandomCard(possibleCards);
		
		return getRandomAllowedCard(pCards, null, pSituation.getGameType());
	}
	
	public static Card playRearhandCard(CardList pCards, CardList pTrickCards, CardList pPlayedCards, CardList pNotOpponentCards, Situation pSituation) {
		pCards.sort(pSituation.getGameType());
		Card tForehandCard					= pTrickCards.get(0);
		Suit tSuit							= tForehandCard.getSuit();
		Card tMiddlehandCard				= pTrickCards.get(1);
		CardList possibleCards				= new CardList();

		Card tCardToBeat	= tForehandCard;
		if(tMiddlehandCard.beats(pSituation.getGameType(), tCardToBeat))
			tCardToBeat		= tMiddlehandCard;
		
		// Wenn Vorhand-Karte bedient werden kann/muss
		if(pCards.getSuitCount(tSuit, false) > 0) {
			Card possibleHighCard			= pCards.get(pCards.getFirstIndexOfSuit(tSuit, false));	// highest Card
			int possibleBeatingCardIndex	= pCards.getLastIndexOfSuit(tSuit, false);	// lowest Card
			
			if(pCards.getSuitCount(tSuit, false) == 1)
				return possibleHighCard;

			// Wenn schlagbar
			if(possibleHighCard.beats(pSituation.getGameType(), tCardToBeat)) {
				while(!pCards.get(possibleBeatingCardIndex).beats(pSituation.getGameType(), tCardToBeat)) {
					possibleBeatingCardIndex --;
				}
				return pCards.get(possibleBeatingCardIndex);
			}
			// Wenn nicht schlagbar
			return pCards.get(pCards.getLastIndexOfSuit(tSuit, false));
		}
		// Wenn Farbe blank ist
		else {
			// Wenn >= 10 Punkte im Stich && (kein Bube beim Gegner || CJ und noch mindestens ein weiterer Bube)
			if(tForehandCard.getPoints() + tMiddlehandCard.getPoints() >= 10
					&& (Helper.countJacks(pNotOpponentCards) == 4
						|| (Helper.countJacks(pNotOpponentCards) >= 2 && pCards.contains(Card.CJ) && pCards.get(1).getRank() == Rank.JACK))) {
				return pCards.get(Helper.countJacks(pCards)-1);
			}
			// Wenn < 10 -> Farbe abwerfen
			else {
				possibleCards = getPossibleMaxValueCards(pCards, 0);
				if(!possibleCards.isEmpty())
					return playRandomCard(possibleCards);
				possibleCards = getPossibleMaxValueCards(pCards, 3);
				if(!possibleCards.isEmpty())
					return playRandomCard(possibleCards);
				possibleCards = getPossibleMaxValueCards(pCards, 4);
				if(!possibleCards.isEmpty())
					return playRandomCard(possibleCards);
			}
		}
		
		return getRandomAllowedCard(pCards, null, pSituation.getGameType());
	}

	public static CardList discardSkatCards(BidEvaluator pBid, CardList pOwnCards) {
		CardList tCards							= new CardList(pOwnCards);
		tCards.sort(GameType.GRAND);
		
		CardList tDiscardCards					= new CardList();
		CardList t1ToDiscard					= new CardList();
		ArrayList<CardList> t2ToDiscard			= new ArrayList<CardList>();
		CardList t1PossibleDiscard				= new CardList();
		ArrayList<CardList> t2PossibleDiscard	= new ArrayList<CardList>();
		
		for(Suit lSuit : Suit.values()) {
			if(tCards.getSuitCount(lSuit, false) == 0)
				continue;
			
			int lFirstIndex	= tCards.getFirstIndexOfSuit(lSuit, false);
			int lLastIndex	= tCards.getLastIndexOfSuit(lSuit, false);
			Card lFirstCard	= tCards.get(lFirstIndex);
			Card lLastCard	= tCards.get(lLastIndex);
			
			// Wenn nur eine Karte der Farbe und diese ist nicht das Ass
			if(lFirstIndex == lLastIndex) {
				if(lFirstCard.getRank() != Rank.ACE) {
					// Wenn sie Punkte bringt dann druecken
					if(lFirstCard.getPoints() != 0) {
						t1ToDiscard.add(lFirstCard);
					}
					else {
						tDiscardCards.add(lFirstCard);
					}
				}
			}
			// Wenn der Spieler 2 Karten der Farbe auf der Hand hat
			else if(lLastIndex-lFirstIndex == 1) {
				// Wenn die hohe Karte das Ass ist
				if(lFirstCard.getRank() == Rank.ACE) {
					// Wenn die niedrige Karte die 10 oder der K ist
					if(tCards.get(lFirstIndex+1).getRank() == Rank.TEN
							|| tCards.get(lFirstIndex+1).getRank() == Rank.KING)
						continue;
					else {
						t1ToDiscard.add(lLastCard);
					}
				}
				// Wenn die hohe Karte die 10 ist
				else if(lFirstCard.getRank() == Rank.TEN) {
					continue;
				}
			}
			// Wenn der Spieler >=3 Karten der Farbe auf der Hand hat
			else {
				if(lFirstCard.getRank() == Rank.ACE) {
					// Wenn A, 10, K -> A und 10 druecken
					if(lLastCard.getRank() == Rank.KING) {
						CardList t = new CardList();
						t.add(lFirstCard);
						t.add(tCards.get(lFirstIndex+1));
						t2ToDiscard.add(t);
						t1PossibleDiscard.add(lFirstCard);
					}
					// Wenn zweite Karte eine 10 -> 
					else if(tCards.get(lFirstIndex+1).getRank() == Rank.TEN) {
						t1ToDiscard.add(lFirstCard);
					}
					else {
						CardList t = new CardList();
						t.add(lFirstCard);
						t.add(tCards.get(lFirstIndex+1));
						t2PossibleDiscard.add(t);
						t1PossibleDiscard.add(lFirstCard);
					}
				}
				else if(lFirstCard.getRank() == Rank.TEN) {
					// Wenn 10, K -> 10 druecken
					if(lLastCard.getRank() == Rank.KING)
						t1ToDiscard.add(lFirstCard);
					else if(tCards.get(lFirstIndex+1).getRank() == Rank.QUEEN)
						t1ToDiscard.add(tCards.get(lFirstIndex+1));
					else
						t1PossibleDiscard.add(lLastCard);
				}
				CardList t = new CardList();
				t.add(lFirstCard);
				t.add(tCards.get(lFirstIndex+1));
				t2PossibleDiscard.add(t);
			}
		}
		
		// Wenn 2x blank moeglich
		while(tDiscardCards.size() > 2)
			tDiscardCards.remove((int)(Math.random() * tDiscardCards.size()));
		
		if(tDiscardCards.size() == 0) {
			for(Card lCardList : t1ToDiscard) {
				tDiscardCards.add(lCardList);
			}
			for(Card lCardList : t1PossibleDiscard) {
				tDiscardCards.add(lCardList);
			}
			while(tDiscardCards.size() > 2)
				tDiscardCards.remove(tDiscardCards.size()-1);
		}
		
		if(tDiscardCards.size() == 1) {
			if(!t1ToDiscard.isEmpty())
				tDiscardCards.add(t1ToDiscard.get((int)(Math.random() * t1ToDiscard.size())));
			if(tDiscardCards.size() != 2 && !t1PossibleDiscard.isEmpty())
				tDiscardCards.add(t1PossibleDiscard.get((int)(Math.random() * t1PossibleDiscard.size())));
		}
		
		if(tDiscardCards.size() != 2 && !t2ToDiscard.isEmpty()) {
			tDiscardCards = t2ToDiscard.get((int)(Math.random() * t2ToDiscard.size()));
		}
		
		if(tDiscardCards.size() != 2 && !t2PossibleDiscard.isEmpty()) {
			tDiscardCards = t2PossibleDiscard.get((int)(Math.random() * t2PossibleDiscard.size()));
		}
		
		return tDiscardCards;
	}
}