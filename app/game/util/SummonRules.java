package game.util;

import game.core.CoreGameState;
import game.core.Owner;
import game.core.UnitState;

public final class SummonRules {

    private SummonRules() {}

    public static boolean isValidSummonTile(CoreGameState core, Owner owner, int x, int y) {
        if (core == null) return false;
        if (!BoardUtils.isInsideBoard(x, y)) return false;
        if (!core.isEmpty(x, y)) return false;

        // must be adjacent (8-direction) to any friendly unit
        for (UnitState u : core.getAllUnits()) {
            if (u != null && u.owner() == owner) {
                int dx = Math.abs(u.x() - x);
                int dy = Math.abs(u.y() - y);
                if (dx <= 1 && dy <= 1 && !(dx == 0 && dy == 0)) {
                    return true;
                }
            }
        }
        return false;
    }
}