package game.util;

import akka.actor.ActorRef;
import commands.BasicCommands;
import game.core.Owner;
import structures.GameState;

/**
 * Game Win/Loss
 *
 * When a player's health reaches 0 or below, notify the human player
 * whether they won or lost. Only triggers once.
 */
public final class WinLossRules {

    private WinLossRules() {}

    public static void checkAndNotify(ActorRef out, GameState gameState) {
        if (gameState == null) return;
        if (gameState.gameOver) return; // already ended

        boolean humanDead = gameState.core().getHuman().isDead();
        boolean aiDead = gameState.core().getAI().isDead();

        if (!humanDead && !aiDead) return;

        gameState.gameOver = true;

        // If both reach 0 at same time, decide a rule.
        // Here: human loses if both dead (simple + consistent).
        if (humanDead) {
            gameState.winner = Owner.AI;
            BasicCommands.addPlayer1Notification(out, "You lose!", 5);
        } else {
            gameState.winner = Owner.HUMAN;
            BasicCommands.addPlayer1Notification(out, "You win!", 5);
        }
    }
}