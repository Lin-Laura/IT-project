package events;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import game.core.CoreGameState;
import game.core.Owner;
import game.core.UnitState;
import structures.GameState;

/**
 * Initialize event processor.
 * This sets up a clean new game state in our CoreGameState.
 */
public class Initialize implements EventProcessor {

    @Override
    public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

        // 1) Mark initialized (used by template tests)
        gameState.gameInitialized = true;

        // optional dummy flag (safe)
        gameState.something = true;

        // 2) Get core state
        CoreGameState core = gameState.core();

        // 3) Reset everything to a clean new game
        core.resetGame();

        // 4) Create two generals (placeholder stats)
        // Convention: HUMAN bottom middle (4,4), AI top middle (4,0)
        UnitState humanGeneral = new UnitState(100, Owner.HUMAN, 4, 4, 2, 20);
        UnitState aiGeneral = new UnitState(200, Owner.AI, 4, 0, 2, 20);

        // 5) Place them on board
        core.placeUnit(humanGeneral, 4, 4);
        core.placeUnit(aiGeneral, 4, 0);

        // 6) Starting mana (minimal default)
        core.getHuman().setMana(2);
        core.getAI().setMana(2);

        // 7) Starting turn state
        core.setTurn(1, Owner.HUMAN);

        // 8) Reset actions for current player's units
        core.resetUnitsForNewTurn(core.activePlayer());
    }
}