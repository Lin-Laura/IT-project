package game.util;

import game.core.CoreGameState;
import game.core.Keywords;
import game.core.Owner;
import game.core.UnitState;

import java.util.ArrayList;
import java.util.List;

public final class AbilityRules {

    private AbilityRules() {}

    // ---------- Keyword checks ----------

    public static boolean hasProvoke(UnitState u) {
        return u != null && u.keywords().contains(Keywords.PROVOKE);
    }

    public static boolean hasFlying(UnitState u) {
        return u != null && u.keywords().contains(Keywords.FLYING);
    }

    // ---------- Provoke (#22) ----------

    /**
     * Return list of enemy provoke units adjacent to (x,y).
     * Adjacent = 4-neighbour tiles.
     */
    public static List<UnitState> getAdjacentEnemyProvokers(CoreGameState state, Owner owner, int x, int y) {
        List<UnitState> res = new ArrayList<>();
        if (state == null) return res;

        List<int[]> adj = BoardUtils.getAdjacentTiles(x, y);
        for (int[] pos : adj) {
            int ax = pos[0];
            int ay = pos[1];
            UnitState other = state.getUnitAt(ax, ay);
            if (other != null && other.owner() != owner && hasProvoke(other)) {
                res.add(other);
            }
        }
        return res;
    }

    /**
     * True if unit is adjacent to at least one enemy provoke unit.
     */
    public static boolean isPinnedByProvoke(CoreGameState state, UnitState unit) {
        if (state == null || unit == null) return false;
        return !getAdjacentEnemyProvokers(state, unit.owner(), unit.x(), unit.y()).isEmpty();
    }

    /**
     * Provoke movement restriction:
     * If pinned by enemy provoke, treat as unable to move.
     */
    public static boolean canMoveConsideringProvoke(CoreGameState state, UnitState unit) {
        if (unit == null) return false;
        return !isPinnedByProvoke(state, unit);
    }

    /**
     * Provoke attack restriction:
     * If pinned by enemy provoke, valid targets are ONLY enemy units with provoke.
     */
    public static boolean isValidAttackTargetConsideringProvoke(CoreGameState state, UnitState attacker, UnitState target) {
        if (state == null || attacker == null || target == null) return false;
        if (target.owner() == attacker.owner()) return false;

        if (!isPinnedByProvoke(state, attacker)) {
            return true; // not pinned -> no restriction
        }

        // pinned -> only provoke targets allowed
        return hasProvoke(target);
    }

    // ---------- Flying (#24) ----------

    /**
     * For flying units: valid move tiles = any empty tile on board.
     * For non-flying units: returns empty list (movement range rules handled elsewhere).
     */
    public static List<int[]> getValidMoveTilesConsideringFlying(CoreGameState state, UnitState unit) {
        List<int[]> res = new ArrayList<>();
        if (state == null || unit == null) return res;

        if (!hasFlying(unit)) {
            return res;
        }

        for (int x = 0; x < CoreGameState.BOARD_WIDTH; x++) {
            for (int y = 0; y < CoreGameState.BOARD_HEIGHT; y++) {
                if (state.isEmpty(x, y)) {
                    res.add(new int[]{x, y});
                }
            }
        }
        return res;
    }
}