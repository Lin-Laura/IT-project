import static org.junit.Assert.*;
import org.junit.Test;

import game.core.CoreGameState;
import game.core.Owner;
import game.core.UnitState;
import game.util.SummonRules;

public class SummonRulesTest {

    @Test
    public void summonTileMustBeEmptyAndAdjacentToFriendlyUnit() {
        CoreGameState core = new CoreGameState();

        // Place a friendly unit at (1,1) (0-based)
        UnitState friendly = new UnitState(1, Owner.HUMAN, 1, 1, 2, 20);
        assertTrue(core.placeUnit(friendly, 1, 1));

        // Adjacent empty tile => valid
        assertTrue(SummonRules.isValidSummonTile(core, Owner.HUMAN, 2, 2));

        // Non-adjacent => invalid
        assertFalse(SummonRules.isValidSummonTile(core, Owner.HUMAN, 8, 4));

        // Occupied => invalid
        UnitState block = new UnitState(2, Owner.AI, 2, 2, 1, 1);
        assertTrue(core.placeUnit(block, 2, 2));
        assertFalse(SummonRules.isValidSummonTile(core, Owner.HUMAN, 2, 2));
    }
}