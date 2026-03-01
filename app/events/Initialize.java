package events;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

import java.io.File;
import java.util.Arrays;

public class Initialize implements EventProcessor {

    @Override
    public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

        // ---- flags used by template/tests ----
        gameState.gameInitialized = true;
        gameState.something = true;

        // ---- reset basic state  ----
        gameState.gameOver = false;
        gameState.winner = null;

        gameState.turnNumber = 1;
        gameState.activePlayer = "HUMAN";

        // Story #3: starting health = 20
        gameState.humanHealth = 20;
        gameState.aiHealth = 20;

        // clear runtime state
        gameState.boardUnits.clear();
        gameState.uiUnitById.clear();
        gameState.highlightedTargetTiles.clear();

        gameState.selectedHandPos = null;
        gameState.selectedCardConfig = null;
        gameState.selectedCardIsUnit = false;

        // ----------------------------------------------------
        // 1) Draw board tiles (9x5)
        // ----------------------------------------------------
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 5; y++) {
                Tile t = BasicObjectBuilders.loadTile(x, y);
                BasicCommands.drawTile(out, t, 0);
            }
        }

        // ----------------------------------------------------
        // 2) Draw avatars
        // ----------------------------------------------------
        int hx = 1, hy = 2;
        int ax = 7, ay = 2;

        Tile humanTile = BasicObjectBuilders.loadTile(hx, hy);
        Tile aiTile = BasicObjectBuilders.loadTile(ax, ay);

        Unit humanAvatar = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 100, Unit.class);
        humanAvatar.setPositionByTile(humanTile);
        BasicCommands.drawUnit(out, humanAvatar, humanTile);
        sleep(80);
        BasicCommands.setUnitAttack(out, humanAvatar, 2);
        sleep(80);
        BasicCommands.setUnitHealth(out, humanAvatar, 20);
        sleep(80);

        Unit aiAvatar = BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 200, Unit.class);
        aiAvatar.setPositionByTile(aiTile);
        BasicCommands.drawUnit(out, aiAvatar, aiTile);
        sleep(80);
        BasicCommands.setUnitAttack(out, aiAvatar, 2);
        sleep(80);
        BasicCommands.setUnitHealth(out, aiAvatar, 20);
        sleep(80);

        // track on board
        gameState.boardUnits.put(gameState.key(hx, hy), humanAvatar);
        gameState.boardUnits.put(gameState.key(ax, ay), aiAvatar);
        gameState.uiUnitById.put(100, humanAvatar);
        gameState.uiUnitById.put(200, aiAvatar);

        // ----------------------------------------------------
        // 3) Story #3: Set player UI health to 20
        // ----------------------------------------------------
        BasicCommands.setPlayer1Health(out, new Player(gameState.humanHealth, gameState.humanMana));
        BasicCommands.setPlayer2Health(out, new Player(gameState.aiHealth, gameState.aiMana));

        // ----------------------------------------------------
        // 4) Story #1: Draw 3 cards for human
        // ----------------------------------------------------
        File dir = new File("conf/gameconfs/cards/");
        String[] p1 = dir.list((d, name) -> name.startsWith("1_") && name.endsWith(".json"));

        if (p1 != null) {
            Arrays.sort(p1);
            for (int i = 0; i < 3 && i < p1.length; i++) {
                String cfg = "conf/gameconfs/cards/" + p1[i];
                int handPos = i + 1;
                int cardId = 1000 + handPos;

                Card c = BasicObjectBuilders.loadCard(cfg, cardId, Card.class);
                if (c != null) {
                    BasicCommands.drawCard(out, c, handPos, 0);
                }
            }
        }
    }

    // small UI sync delay
    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
        }
    }
}