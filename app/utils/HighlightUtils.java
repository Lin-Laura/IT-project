package utils;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;

import java.util.List;

/**
 * Story #31: highlight valid spell target tiles in red when a spell card is selected.
 */
public final class HighlightUtils {

    private HighlightUtils() {}

    /** Clears any previously highlighted target tiles (mode=0). */
    public static void clearHighlightedTiles(ActorRef out, GameState gameState) {
        if (out == null || gameState == null) return;
        if (gameState.highlightedTargetTiles.isEmpty()) return;

        for (String key : gameState.highlightedTargetTiles) {
            String[] parts = key.split(",");
            if (parts.length != 2) continue;
            try {
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                Tile tile = BasicObjectBuilders.loadTile(x, y);
                BasicCommands.drawTile(out, tile, 0);
            } catch (NumberFormatException ignored) {
            }
        }

        gameState.highlightedTargetTiles.clear();
    }

    /**
     * Highlights a list of tiles in red (mode=2) and tracks them in gameState.
     */
    public static void highlightTilesRed(ActorRef out, GameState gameState, List<int[]> tiles0Based) {
        if (out == null || gameState == null) return;
        if (tiles0Based == null || tiles0Based.isEmpty()) return;

        for (int[] xy : tiles0Based) {
            if (xy == null || xy.length < 2) continue;
            int x = xy[0];
            int y = xy[1];

            Tile tile = BasicObjectBuilders.loadTile(x, y);
            BasicCommands.drawTile(out, tile, 2);

            gameState.highlightedTargetTiles.add(x + "," + y);
        }
    }

    /** Unhighlights any currently selected card (if still present) and clears selection state. */
    public static void clearCardSelection(ActorRef out, GameState gameState) {
        if (out == null || gameState == null) return;

        if (gameState.selectedHandPos != null && gameState.selectedCardConfig != null) {
            Card card = BasicObjectBuilders.loadCard(gameState.selectedCardConfig, gameState.selectedHandPos, Card.class);
            if (card != null) {
                BasicCommands.drawCard(out, card, gameState.selectedHandPos, 0);
            }
        }

        gameState.selectedHandPos = null;
        gameState.selectedCardConfig = null;
        gameState.selectedCardIsUnit = false;
    }

    /** Convenience: clear both selection + target highlights. */
    public static void clearSelectionAndHighlights(ActorRef out, GameState gameState) {
        clearCardSelection(out, gameState);
        clearHighlightedTiles(out, gameState);
    }
}