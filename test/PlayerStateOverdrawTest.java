import static org.junit.Assert.*;
import org.junit.Test;

import game.core.Owner;
import game.core.PlayerState;

public class PlayerStateOverdrawTest {

    @Test
    public void whenHandIsFull_drawDiscardsTopCard() {
        PlayerState p = new PlayerState(Owner.HUMAN, 20);

        // Fill hand to max
        for (int i = 0; i < PlayerState.MAX_HAND_SIZE; i++) {
            assertTrue(p.addToHand("H" + i));
        }
        assertEquals(6, p.hand().size());

        // Add two cards to deck (top = index 0)
        p.deck().add("TOP");
        p.deck().add("NEXT");
        assertEquals(2, p.deck().size());

        // Draw => TOP discarded
        String drawn = p.drawTopCardToHand();

        assertNull(drawn);
        assertEquals(6, p.hand().size());
        assertEquals(1, p.deck().size());
        assertEquals("NEXT", p.deck().get(0));
    }

    @Test
    public void whenHandHasSpace_drawAddsCardToHand() {
        PlayerState p = new PlayerState(Owner.HUMAN, 20);

        // Hand size 5
        for (int i = 0; i < PlayerState.MAX_HAND_SIZE - 1; i++) {
            assertTrue(p.addToHand("H" + i));
        }
        assertEquals(5, p.hand().size());

        p.deck().add("TOP");

        String drawn = p.drawTopCardToHand();

        assertEquals("TOP", drawn);
        assertEquals(6, p.hand().size());
        assertEquals("TOP", p.hand().get(5));
        assertEquals(0, p.deck().size());
    }
}