package events;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import commands.BasicCommands;
import game.core.CoreGameState;
import game.core.Owner;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Player;
import utils.BasicObjectBuilders;
import utils.HighlightUtils;

public class EndTurnClicked implements EventProcessor {

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		HighlightUtils.clearSelectionAndHighlights(out, gameState);
		
		CoreGameState core = gameState.core();
		Owner current = core.activePlayer();

		// ----------------------------------------------------
		// Story #1 + #2: draw 1 card at end of turn (discard if hand full => cfg null)
		// ----------------------------------------------------
		String cfg = core.getPlayer(current).drawTopCardToHand();

		if (current == Owner.HUMAN && cfg != null) {
			int pos = core.getHuman().hand().size();
			Card c = BasicObjectBuilders.loadCard(cfg, pos, Card.class);
			if (c != null) BasicCommands.drawCard(out, c, pos, 0);
		}

		// ----------------------------------------------------
		// Story #5: Mana Drain -> 0 (and update UI)
		// ----------------------------------------------------
		if (current == Owner.HUMAN) {
			core.getHuman().setMana(0);
			BasicCommands.setPlayer1Mana(out, new Player(core.getHuman().health(), core.getHuman().mana()));
		} else {
			core.getAI().setMana(0);
			BasicCommands.setPlayer2Mana(out, new Player(core.getAI().health(), core.getAI().mana()));
		}

		// ----------------------------------------------------
		// Switch turn
		// ----------------------------------------------------
		core.nextTurn();
		Owner next = core.activePlayer();

		// ----------------------------------------------------
		// Story #4: Mana Gain = turnNumber + 1 (and update UI)
		// ----------------------------------------------------
		int manaForThisTurn = core.turnNumber() + 1;

		if (next == Owner.HUMAN) {
			core.getHuman().setMana(manaForThisTurn);
			BasicCommands.setPlayer1Mana(out, new Player(core.getHuman().health(), core.getHuman().mana()));
		} else {
			core.getAI().setMana(manaForThisTurn);
			BasicCommands.setPlayer2Mana(out, new Player(core.getAI().health(), core.getAI().mana()));
		}

		core.resetUnitsForNewTurn(next);
	}
}