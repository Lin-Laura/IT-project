import static org.junit.Assert.*;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.After;
import org.junit.Test;

import commands.BasicCommands;
import commands.DummyTell;
import game.core.Owner;
import game.util.WinLossRules;
import structures.GameState;

import java.util.ArrayList;
import java.util.List;

public class WinLossRulesTest {

    private static class CapturingTell implements DummyTell {
        List<ObjectNode> messages = new ArrayList<>();
        @Override public void tell(ObjectNode message) { messages.add(message); }
    }

    @After
    public void cleanup() {
        BasicCommands.altTell = null;
    }

    @Test
    public void humanHealthZero_shouldNotifyLoseOnce() {
        GameState gs = new GameState();

        CapturingTell cap = new CapturingTell();
        BasicCommands.altTell = cap;

        gs.core().getHuman().setHealth(0);

        WinLossRules.checkAndNotify(null, gs);

        assertTrue(gs.gameOver);
        assertEquals(Owner.AI, gs.winner);
        assertEquals(1, cap.messages.size());
        assertEquals("addPlayer1Notification", cap.messages.get(0).get("messagetype").asText());
        assertEquals("You lose!", cap.messages.get(0).get("text").asText());

        // Calling again should not spam notifications
        WinLossRules.checkAndNotify(null, gs);
        assertEquals(1, cap.messages.size());
    }

    @Test
    public void aiHealthZero_shouldNotifyWin() {
        GameState gs = new GameState();

        CapturingTell cap = new CapturingTell();
        BasicCommands.altTell = cap;

        gs.core().getAI().setHealth(0);

        WinLossRules.checkAndNotify(null, gs);

        assertTrue(gs.gameOver);
        assertEquals(Owner.HUMAN, gs.winner);
        assertEquals(1, cap.messages.size());
        assertEquals("You win!", cap.messages.get(0).get("text").asText());
    }
}