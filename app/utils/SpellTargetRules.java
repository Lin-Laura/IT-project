package game.util;

import game.core.CoreGameState;
import game.core.Owner;
import game.core.UnitState;
import structures.basic.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Story #31 helper: decides which board tiles are valid targets for a spell. */
public final class SpellTargetRules {

    private SpellTargetRules() {}

    /** @return list of int[]{x,y} target tiles (0-based) */
    public static List<int[]> getValidTargetTiles(CoreGameState core, Card spell, Owner caster) {
        if (core == null || spell == null || caster == null) return Collections.emptyList();
        String name = spell.getCardname();
        if (name == null) return Collections.emptyList();

        String n = name.trim().toLowerCase();
        Owner enemy = (caster == Owner.HUMAN) ? Owner.AI : Owner.HUMAN;

        if (n.equals("truestrike") || n.equals("beamshock")) {
            return tilesWithUnitsOwnedBy(core, enemy, true);
        }

        if (n.equals("dark terminus")) {
            return tilesWithUnitsOwnedBy(core, enemy, false);
        }

        if (n.equals("sundrop elixir")) {
            return tilesWithAnyUnit(core);
        }

        if (n.equals("horn of the forsaken")) {
            UnitState avatar = (caster == Owner.HUMAN) ? core.getHumanAvatar() : core.getAIAvatar();
            if (avatar == null) return Collections.emptyList();
            List<int[]> res = new ArrayList<>();
            res.add(new int[] { avatar.x(), avatar.y() });
            return res;
        }

        if (n.equals("wraithling swarm")) {
            UnitState avatar = (caster == Owner.HUMAN) ? core.getHumanAvatar() : core.getAIAvatar();
            if (avatar == null) return Collections.emptyList();

            List<int[]> res = new ArrayList<>();
            for (int[] xy : BoardUtils.getAdjacentTiles(avatar.x(), avatar.y())) {
                if (xy != null && BoardUtils.isTileEmpty(core, xy[0], xy[1])) {
                    res.add(new int[] { xy[0], xy[1] });
                }
            }
            return res;
        }

        return Collections.emptyList();
    }

    private static List<int[]> tilesWithAnyUnit(CoreGameState core) {
        List<int[]> res = new ArrayList<>();
        for (UnitState u : core.getAllUnits()) {
            if (u == null) continue;
            res.add(new int[] { u.x(), u.y() });
        }
        return res;
    }

    /** @param includeAvatar if false, avatars are excluded ("creature" only). */
    private static List<int[]> tilesWithUnitsOwnedBy(CoreGameState core, Owner owner, boolean includeAvatar) {
        List<int[]> res = new ArrayList<>();
        for (UnitState u : core.getAllUnits()) {
            if (u == null) continue;
            if (u.owner() != owner) continue;
            if (!includeAvatar && u.isAvatar()) continue;
            res.add(new int[] { u.x(), u.y() });
        }
        return res;
    }
}