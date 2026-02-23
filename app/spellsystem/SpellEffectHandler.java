package spellsystem;

import structures.GameState;
import structures.basic.Player;
import structures.basic.Unit;

public class SpellEffectHandler {

    public boolean applySpell(GameState gameState,
                              Player caster,
                              SpellCard spellCard,
                              Unit targetUnit,
                              int targetX,
                              int targetY) {
        if (gameState == null || caster == null || spellCard == null) {
            return false;
        }

        // if target is required but no target -> false
        if (spellCard.requiresTarget() && targetUnit == null) {
            return false;
        }

       
        String effectType = String.valueOf(spellCard.getEffectType()).toUpperCase();

        switch (effectType) {
            case "DAMAGE":
                // #26 Direct Damage 
                if (targetUnit == null) return false;
                int newHp = Math.max(0, targetUnit.getHealth() - spellCard.getEffectValue());
                targetUnit.setHealth(newHp);

                // If a unit dies, try to remove it from the board 
                if (targetUnit.getHealth() <= 0) {
                    gameState.getBoard().removeUnit(targetUnit);
                }
                return true;

            case "HEAL":
                // #27 Heal 
                if (targetUnit == null) return false;
                int healedHp = Math.min(
                        targetUnit.getHealth() + spellCard.getEffectValue(),
                        targetUnit.getMaxHealth()
                );
                targetUnit.setHealth(healedHp);
                return true;

            case "DESTROY":
                // #28 Destroy Unit 
                if (targetUnit == null) return false;
                if (targetUnit.isAvatar()) return false;

                targetUnit.setHealth(0);
                gameState.getBoard().removeUnit(targetUnit);
                return true;

            case "STUN":
                // #29 Stun 
                if (targetUnit == null) return false;
                if (targetUnit.isAvatar()) return false; 


                System.out.println("[Stun Applied] unitId=" + targetUnit.getId());
                return true;

            default:
                
                return false;
        }
    }
}