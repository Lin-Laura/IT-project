package game.core;

import game.util.AbilityRules;

public class CoreEngineTest {

    public static void main(String[] args) {

        // ---------------- Basic board test ----------------
        CoreGameState s = new CoreGameState();

        UnitState u1 = new UnitState(1, Owner.HUMAN, 0, 0, 2, 10);

        System.out.println("place: " + s.placeUnit(u1, 4, 4)); // true
        System.out.println("occupied: " + (s.getUnitAt(4, 4) != null)); // true

        System.out.println("move: " + s.moveUnit(4, 4, 4, 3)); // true
        System.out.println("old empty: " + s.isEmpty(4, 4)); // true
        System.out.println("new occupied: " + (s.getUnitAt(4, 3) != null)); // true

        System.out.println("remove: " + s.removeUnitAt(4, 3)); // true
        System.out.println("now empty: " + s.isEmpty(4, 3)); // true

        // ---------------- Provoke test (#22) ----------------
        CoreGameState t = new CoreGameState();

        UnitState human = new UnitState(10, Owner.HUMAN, 4, 4, 2, 10);
        UnitState aiProvoker = new UnitState(20, Owner.AI, 4, 3, 2, 10);
        aiProvoker.keywords().add(Keywords.PROVOKE);

        t.placeUnit(human, 4, 4);
        t.placeUnit(aiProvoker, 4, 3);

        System.out.println("pinned: " + AbilityRules.isPinnedByProvoke(t, human)); // true
        System.out.println("canMove: " + AbilityRules.canMoveConsideringProvoke(t, human)); // false
        System.out.println("canAttackProvoker: " +
                AbilityRules.isValidAttackTargetConsideringProvoke(t, human, aiProvoker)); // true

        // ---------------- Flying test (#24) ----------------
        CoreGameState f = new CoreGameState();

        UnitState flyer = new UnitState(30, Owner.HUMAN, 4, 4, 2, 10);
        flyer.keywords().add(Keywords.FLYING);

        // occupy flyer tile and one extra blocker tile
        f.placeUnit(flyer, 4, 4);
        UnitState blocker = new UnitState(31, Owner.AI, 1, 1, 2, 10);
        f.placeUnit(blocker, 1, 1);

        System.out.println("hasFlying: " + AbilityRules.hasFlying(flyer)); // true
        System.out.println("flyingMovesCount: " +
                AbilityRules.getValidMoveTilesConsideringFlying(f, flyer).size()); // 43 (45 - 2)
    }
}