package spellsystem;
import structures.GameState;
import structures.basic.Player;

public class ActionValidator {

    private final RulesEngine rulesEngine;

    public ActionValidator(RulesEngine rulesEngine) {
        this.rulesEngine = rulesEngine;
    }

    
    public boolean validatePlaySpell(GameState gameState, Player player, SpellCard spellCard, int targetX, int targetY) {
        if (gameState == null || player == null || spellCard == null) {
            return false;
        }

        // judge whether the turn is correct
        if (gameState.getCurrentPlayer() != player) {
            return false;
}

        // judge whether the mena is enough
        if (player.getMana() < spellCard.getManaCost()) {
            return false;
        }

        // judge whether the target is valid
        return rulesEngine.isValidSpellTarget(gameState, player, spellCard, targetX, targetY);
    }
}