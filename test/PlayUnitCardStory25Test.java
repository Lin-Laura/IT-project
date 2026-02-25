import static org.junit.Assert.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.After;
import org.junit.Test;

import commands.BasicCommands;
import commands.DummyTell;
import events.TileClicked;
import game.core.Owner;
import game.core.UnitState;
import structures.GameState;

import java.util.ArrayList;
import java.util.List;

public class PlayUnitCardStory25Test {

    private static class CapturingTell implements DummyTell {
        List<ObjectNode> messages = new ArrayList<>();
        @Override public void tell(ObjectNode message) { messages.add(message); }
    }

    @After
    public void cleanup() {
        BasicCommands.altTell = null;
    }

    @Test
    public void whenUnitCardSelectedAndValidTileClicked_unitIsSummonedAndCardRemovedAndManaDecremented() throws Exception {
        GameState gs = new GameState();

        // Capture UI commands (so TileClicked can safely call BasicCommands with out=null)
        CapturingTell cap = new CapturingTell();
        BasicCommands.altTell = cap;

        // Make sure it's human's turn
        gs.core().setTurn(1, Owner.HUMAN);

        // Give human enough mana (Rock Pulveriser cost is 2 in the template set)
        gs.core().getHuman().setMana(9);

        // Put one unit card in hand (real file in template project)
        String unitCard = "conf/gameconfs/cards/1_7_c_u_rock_pulveriser.json";
        gs.core().getHuman().hand().add(unitCard);

        // Simulate "selected a unit card" (position 1)
        gs.selectedHandPos = 1;
        gs.selectedCardConfig = unitCard;
        gs.selectedCardIsUnit = true;

        // Place a friendly unit so adjacency rule is satisfied
        // Put friendly at (1,1), summon target at (2,2) (0-based)
        UnitState friendly = new UnitState(10, Owner.HUMAN, 1, 1, 2, 20);
        assertTrue(gs.core().placeUnit(friendly, 1, 1));

        // Click tile (tilex,tiley are 1-based in events)
        ObjectMapper om = new ObjectMapper();
        ObjectNode msg = om.createObjectNode();
        msg.put("tilex", 3); // 0-based x=2
        msg.put("tiley", 3); // 0-based y=2

        new TileClicked().processEvent(null, gs, msg);

        // ASSERT: card removed from hand
        assertEquals(0, gs.core().getHuman().hand().size());

        // ASSERT: unit placed at clicked tile (0-based 2,2)
        UnitState placed = gs.core().getUnitAt(2, 2);
        assertNotNull(placed);
        assertEquals(Owner.HUMAN, placed.owner());

        // ASSERT: mana decreased by the card cost (at least: should be less than before)
        assertTrue(gs.core().getHuman().mana() < 9);

        // ASSERT: selection cleared (important so it doesn't summon again)
        assertNull(gs.selectedHandPos);
        assertNull(gs.selectedCardConfig);
        assertFalse(gs.selectedCardIsUnit);
    }
}