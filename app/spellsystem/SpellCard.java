package spellsystem;

import structures.basic.BigCard;
import structures.basic.Card;
import structures.basic.MiniCard;

public class SpellCard extends Card {

    private final String effectType;      // "DAMAGE", "HEAL", "DESTROY", "STUN"
    private final int effectValue;        // damage/heal amount; destroy/stun can be 0
    private final boolean requiresTarget; // whether this spell needs a target tile/unit

    public SpellCard(int id,
                     String cardname,
                     int manacost,
                     MiniCard miniCard,
                     BigCard bigCard,
                     String effectType,
                     int effectValue,
                     boolean requiresTarget) {

        // isCreature = false (spell card)
        // unitConfig = null (spell card does not summon a unit)
        super(id, cardname, manacost, miniCard, bigCard, false, null);


        this.effectType = effectType;
        this.effectValue = effectValue;
        this.requiresTarget = requiresTarget;
    }

    public String getEffectType() {
        return effectType;
    }

    public int getEffectValue() {
        return effectValue;
    }

    public boolean requiresTarget() {
        return requiresTarget;
    }
    
    public int getManaCost() {
    
            return super.getManacost();
}
}