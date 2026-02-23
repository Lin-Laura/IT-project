package spellsystem;

import structures.GameState;
import structures.basic.Player;
import structures.basic.Unit;

public class RulesEngine {

    
    public boolean isValidSpellTarget(GameState gameState, Player caster, SpellCard spellCard, int x, int y) {
        if (gameState == null || caster == null || spellCard == null) {
            return false;
        }

        Board board = gameState.getBoard();
        if (board == null) {
            return false;
        }

        // whether in bounds
        if (!board.isInBounds(x, y)) {
            return false;
        }

        // whether tile exists
        BoardTile tile = board.getTile(x, y);
        if (tile == null) {
            return false;
        }

        // target requirement
        Unit targetUnit = tile.getOccupyingUnit();
        if (spellCard.requiresTarget() && targetUnit == null) {
            return false;
        }

        
        return true;
    }
}