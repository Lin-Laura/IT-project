package events;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import commands.BasicCommands;
import game.core.CoreGameState;
import game.core.Owner;
import game.core.UnitState;
import structures.GameState;
import structures.basic.Card;
import utils.BasicObjectBuilders;

import java.io.File;
import java.util.Arrays;

/**
 * Initialize event processor.
 * Story #1: draw 3 cards at start of Human Turn 1
 */
public class Initialize implements EventProcessor {

    @Override
    public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

        // 1) Mark initialized (used by template tests)
        gameState.gameInitialized = true;
        gameState.something = true;

        // 2) Get core state
        CoreGameState core = gameState.core();

        // 3) Reset everything
        core.resetGame();

        // ----------------------------------------------------
        // Build decks from conf/gameconfs/cards/
        // ----------------------------------------------------
        File dir = new File("conf/gameconfs/cards/");
        String[] p1 = dir.list((d, name) -> name.startsWith("1_") && name.endsWith(".json"));
        String[] p2 = dir.list((d, name) -> name.startsWith("2_") && name.endsWith(".json"));

        if (p1 != null) {
            Arrays.sort(p1); // stable "top of deck"
            for (String f : p1) core.getHuman().deck().add("conf/gameconfs/cards/" + f);
        }
        if (p2 != null) {
            Arrays.sort(p2);
            for (String f : p2) core.getAI().deck().add("conf/gameconfs/cards/" + f);
        }

        // ----------------------------------------------------
        // Create two generals and connect to health/damage
        // ----------------------------------------------------
        UnitState humanGeneral = new UnitState(100, Owner.HUMAN, 4, 4, 2, 20);
        
        humanGeneral.setAvatar(true);

        UnitState aiGeneral = new UnitState(200, Owner.AI, 4, 0, 2, 20);
        
        aiGeneral.setAvatar(true);

        core.placeUnit(humanGeneral, 4, 4);
        core.placeUnit(aiGeneral, 4, 0);

        core.setHumanAvatar(humanGeneral);
        core.setAIAvatar(aiGeneral);
        
        // ----------------------------------------------------
        // Start state
        // ----------------------------------------------------
        core.setTurn(1, Owner.HUMAN);

        // Story #4 (minimal start) — many templates start with 2 mana
        core.getHuman().setMana(2);
        core.getAI().setMana(2);

        // Reset actions
        core.resetUnitsForNewTurn(core.activePlayer());

        // ----------------------------------------------------
        // STORY #1: Draw 3 cards at start of Human Turn 1
        // ----------------------------------------------------
        for (int pos = 1; pos <= 3; pos++) {
            String cfg = core.getHuman().drawTopCardToHand(); // Story #2 handles discard when full
            if (cfg != null) {
                Card c = BasicObjectBuilders.loadCard(cfg, pos, Card.class);
                if (c != null) BasicCommands.drawCard(out, c, pos, 0);
            }
        }
    }
}