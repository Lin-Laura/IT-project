package events;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import game.core.CoreGameState;
import game.core.Owner;
import structures.GameState;

public class EndTurnClicked implements EventProcessor {

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		CoreGameState core = gameState.core();

		// (Story #5) mana drain: mana is cleared at the end of the current player's turn
		Owner current = core.activePlayer();
		core.getPlayer(current).setMana(0);

		// Switch round
		core.nextTurn();

		// Story #4) mana gain: New turn starts mana = turnNumber + 1
		int manaForThisTurn = core.turnNumber() + 1;
		core.getPlayer(core.activePlayer()).setMana(manaForThisTurn);
	}
}