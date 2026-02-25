package game.core;

import java.util.ArrayList;
import java.util.List;

public class PlayerState {

    public static final int MAX_HAND_SIZE = 6;

    private final Owner owner;

    private int health;
    private int mana;

    // temporary: String as card id (later can be CardState)
    private final List<String> hand;
    private final List<String> deck;

    public PlayerState(Owner owner, int startingHealth) {
        this.owner = owner;
        this.health = startingHealth;
        this.mana = 0;
        this.hand = new ArrayList<>();
        this.deck = new ArrayList<>();
    }

    public Owner owner() { return owner; }

    public int health() { return health; }
    public int mana() { return mana; }

    public void setMana(int mana) { this.mana = mana; }
    public void addMana(int amount) { this.mana += amount; }

    public void takeDamage(int amount) { this.health -= amount; }
    public void setHealth(int health) { this.health = health; }

    public List<String> hand() { return hand; }
    public List<String> deck() { return deck; }

    public boolean addToHand(String cardId) {
        if (hand.size() >= MAX_HAND_SIZE) return false;
        hand.add(cardId);
        return true;
    }

    public void removeFromHand(String cardId) {
        hand.remove(cardId);
    }

    /**
     *  Overdraw
     *
     * Draws the top card from the deck.
     * If hand is full (6 cards), the card is discarded.
     *
     * @return cardId if added to hand, null if discarded or deck empty
     */
    public String drawTopCardToHand() {

        if (deck.isEmpty()) return null;

        // top of deck = index 0 (deck is not shuffled)
        String cardId = deck.remove(0);

        // try to add to hand
        boolean added = addToHand(cardId);

        if (added) {
            return cardId;
        }

        // hand full → card discarded
        return null;
    }
}