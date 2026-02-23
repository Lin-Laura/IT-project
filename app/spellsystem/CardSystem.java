package spellsystem;


import structures.GameState;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Unit;

public class CardSystem {

    private final ActionValidator actionValidator;
    private final SpellEffectHandler spellEffectHandler;

    public CardSystem(ActionValidator actionValidator, SpellEffectHandler spellEffectHandler) {
        this.actionValidator = actionValidator;
        this.spellEffectHandler = spellEffectHandler;
    }

   
    public boolean playSpellCard(GameState gameState, Player player, int cardId, int targetX, int targetY) {
        if (gameState == null || player == null) {
            return false;
        }

        // get card from hand
        Card card = player.getHand().getCardById(cardId);
        if (card == null) {
            return false;
        }

        // insure card is spellcard
        if (!(card instanceof SpellCard)) {
            return false;
        }

        SpellCard spellCard = (SpellCard) card;

        
        boolean valid = actionValidator.validatePlaySpell(gameState, player, spellCard, targetX, targetY);
        if (!valid) {
            return false;
        }

        // get tile and unit
        BoardTile targetTile = gameState.getBoard().getTile(targetX, targetY);
        Unit targetUnit = (targetTile == null) ? null : targetTile.getOccupyingUnit();

        // cost mana
        player.setMana(player.getMana() - spellCard.getManaCost());

        // play the animation
        // TODO: connect frontend animation command
        // e.g. uiCommandSink.playEffectAnimation(spellCard.getAnimationType(), targetX, targetY);
        System.out.println("[Spell Animation] " + spellCard.getName() + " at (" + targetX + "," + targetY + ")");

        // apply spell effect
        boolean effectApplied = spellEffectHandler.applySpell(gameState, player, spellCard, targetUnit, targetX, targetY);
        if (!effectApplied) {
            return false;
        }

        // remove the card from hand
        boolean removed = player.getHand().removeCard(cardId);
        if (!removed) {
            return false;
        }

        

        return true;
    }
}