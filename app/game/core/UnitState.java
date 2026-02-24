package game.core;

import java.util.HashSet;
import java.util.Set;

public class UnitState {

    private final int id;
    private final Owner owner;

    private int x;
    private int y;

    private int attack;
    private int hp;
    private int maxHp;

    private boolean hasMovedThisTurn;
    private boolean hasAttackedThisTurn;
    private boolean isStunned;

    private final Set<String> keywords;

    public UnitState(int id,
                     Owner owner,
                     int x,
                     int y,
                     int attack,
                     int hp) {

        this.id = id;
        this.owner = owner;
        this.x = x;
        this.y = y;
        this.attack = attack;
        this.hp = hp;
        this.maxHp = hp;

        this.hasMovedThisTurn = false;
        this.hasAttackedThisTurn = false;
        this.isStunned = false;

        this.keywords = new HashSet<>();
    }

    // ---------------- getters ----------------

    public int id() { return id; }
    public Owner owner() { return owner; }
    public int x() { return x; }
    public int y() { return y; }
    public int attack() { return attack; }
    public int hp() { return hp; }
    public int maxHp() { return maxHp; }

    public boolean hasMovedThisTurn() { return hasMovedThisTurn; }
    public boolean hasAttackedThisTurn() { return hasAttackedThisTurn; }
    public boolean isStunned() { return isStunned; }

    public Set<String> keywords() { return keywords; }

    // ---------------- state update ----------------

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void takeDamage(int amount) {
        this.hp -= amount;
    }

    public boolean isDead() {
        return hp <= 0;
    }

    public void markMoved() {
        this.hasMovedThisTurn = true;
    }

    public void markAttacked() {
        this.hasAttackedThisTurn = true;
    }

    public void resetTurn() {
        this.hasMovedThisTurn = false;
        this.hasAttackedThisTurn = false;
    }

    public void stun() {
        this.isStunned = true;
    }

    public void clearStun() {
        this.isStunned = false;
    }
}