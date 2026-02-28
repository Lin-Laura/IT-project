package events;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import commands.BasicCommands;
import game.core.CoreGameState;
import game.core.Owner;
import game.core.UnitState;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

import java.io.File;
import java.util.Arrays;

/**
 * Initialize event processor.
 * Story #1: draw 3 cards at start of Human Turn 1
 * Plus: draw tiles + avatars (minimal additions)
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
        // Create two generals (backend state)
        // ----------------------------------------------------
        // Player 1 avatar starts at tile [2,3] => (1,2) in 0-index
        UnitState humanGeneral = new UnitState(100, Owner.HUMAN, 1, 2, 2, 20);
        humanGeneral.setAvatar(true);


        // Player 2 avatar starts mirrored => tile [8,3] => (7,2) in 0-index
        UnitState aiGeneral    = new UnitState(200, Owner.AI,    7, 2, 2, 20);
        aiGeneral.setAvatar(true);

        /* re: merging conflict - hope
        i had these in my code for the healing/damage i'm gonna keep them for now
        but if they are totally useless i will delete
        */
      
        core.setHumanAvatar(humanGeneral);
        core.setAIAvatar(aiGeneral);
        
        core.placeUnit(humanGeneral, 1, 2);
        core.placeUnit(aiGeneral,    7, 2);

        
        // Start state

        core.setTurn(1, Owner.HUMAN);
        core.getHuman().setMana(2);
        core.getAI().setMana(2);

        // Reset actions
        core.resetUnitsForNewTurn(core.activePlayer());

        
        // DRAW BOARD TILES (9x5)
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 5; y++) {
                Tile t = BasicObjectBuilders.loadTile(x, y);
                BasicCommands.drawTile(out, t, 0);
            }
        }

        
        //  DRAW AVATARS (frontend rendering)
        // Keep SAME ids + coords as UnitState above to avoid breaking anything
        
        Tile humanTile = BasicObjectBuilders.loadTile(1, 2);
        Tile aiTile    = BasicObjectBuilders.loadTile(7, 2);

        Unit humanAvatar = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 100, Unit.class);
        humanAvatar.setPositionByTile(humanTile);
        BasicCommands.drawUnit(out, humanAvatar, humanTile);
        BasicCommands.setUnitAttack(out, humanAvatar, 2);
        BasicCommands.setUnitHealth(out, humanAvatar, 20);

        Unit aiAvatar = BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 200, Unit.class);
        aiAvatar.setPositionByTile(aiTile);
        BasicCommands.drawUnit(out, aiAvatar, aiTile);
        BasicCommands.setUnitAttack(out, aiAvatar, 2);
        BasicCommands.setUnitHealth(out, aiAvatar, 20);

        
        // STORY #1: Draw 3 cards at start of Human Turn 1
       
        for (int pos = 1; pos <= 3; pos++) {
            String cfg = core.getHuman().drawTopCardToHand(); // Story #2 handles discard when full
            if (cfg != null) {
                Card c = BasicObjectBuilders.loadCard(cfg, pos, Card.class);
                if (c != null) BasicCommands.drawCard(out, c, pos, 0);
            }
        }
    }
}