package game.util;

import game.core.Owner;
import game.core.UnitState;
import structures.GameState;
import structures.basic.Unit;

public class UnitDeathRulesTest {
    public static void main(String[]args){
        GameState stateOfGame = new GameState();
        int deadId = 123;
        int x = 4;
        int y = 4;

        // backend unit with hp =3
        UnitState target = new UnitState(deadId, Owner.HUMAN,x,y,2,3);
        boolean placed = stateOfGame.core().placeUnit(target, target.x(), target.y());
        System.out.println("DID WE PLACE IT?! " + placed);

        // confirming it again that unit exists before any damage
        System.out.println("Before damage, unit does exists at (x,y)" +
                (stateOfGame.core().getUnitAt(target.x(),target.y()) != null));

        //fake/test UI unit. will only need id for mapping lookup
        Unit uiUnit = new Unit();
        uiUnit.setId(deadId);

        //mapping --- id -> UI unit
        stateOfGame.uiUnitById.put(deadId,uiUnit);

        UnitDeathRules.applyDamageAndHandleDeath(
                null,
                stateOfGame,
                target,
                3);

        boolean stillOnBoard =
                (stateOfGame.core().getUnitAt(target.x(), target.y()) != null);
        boolean stillInMap = stateOfGame.uiUnitById.containsKey(deadId);

        System.out.println("After damage, unit exists at (x,y)??" + stillOnBoard);
        System.out.println("Was the backend removed from board?? " + (!stillOnBoard));

        System.out.println("Was UI mapping removed?! " + (!stillInMap));

        if (!stillOnBoard && !stillInMap) {
            System.out.println("UnitDeathRulesTest Passsseddd!!");
        }
        else{
            System.out.println("UnitDeathRulesTest FAILED :((");
        }
    }
}
