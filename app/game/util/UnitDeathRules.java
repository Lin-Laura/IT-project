//story card #13 (unit death). the logic. final means it can't be changed
// will call it whenever damage happens
// when unit health = 0,
    //remove from CoreGameState + delete from UI -> BasicCommands.deleteUnit
package game.util;

import akka.actor.ActorRef;
import commands.BasicCommands;
import game.core.UnitState;
import structures.GameState;
import structures.basic.Unit;


public final class UnitDeathRules {
    private UnitDeathRules() {}

    public static void applyDamageAndHandleDeath(
            ActorRef out,
            GameState stateOfGame,
            UnitState target,
            int damageAmount) {

        //safety check
        if (stateOfGame == null || target == null) return;
        if (damageAmount <= 0) return;

        //1. backend damage
        //changes hp in UnitState
        target.takeDamage(damageAmount);

        //2.if backend unit is dead, remove from backend + UI
        if (target.isDead())  {
          int deadId = target.id();
          int x = target.x();
          int y = target.y();

          //remove this from the backend board
          stateOfGame.core().removeUnitAt(x,y);

          //find the UI unit
          Unit uiUnit = stateOfGame.uiUnitById.get(deadId);

          //remove from UI
          if (uiUnit != null) {

              //delection of UI only works if out is NOT null
              if (out != null) {
                  //used for UI delection
                  BasicCommands.deleteUnit(out, uiUnit);
              }
              //backend cleanup... always remove from mapping
              stateOfGame.uiUnitById.remove(deadId);
          }
        }


    }
}
