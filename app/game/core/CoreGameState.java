package game.core;

import game.util.BoardUtils;

import java.util.ArrayList;
import java.util.List;

public class CoreGameState {

    public static final int BOARD_WIDTH = 9;
    public static final int BOARD_HEIGHT = 5;

    // IMPORTANT: keep board private. Do not expose for direct writing.
    //translate：重要：保持board私有。不要为直接写作而暴露！！！！
    private final UnitState[][] board;

    // ---- Player + turn state ----
    private final PlayerState human;
    private final PlayerState ai;

    private int turnNumber;
    private Owner activePlayer;

    public CoreGameState() {
        this.board = new UnitState[BOARD_WIDTH][BOARD_HEIGHT];

        // default values (can be adjusted later)
        this.human = new PlayerState(Owner.HUMAN, 20);
        this.ai = new PlayerState(Owner.AI, 20);

        this.turnNumber = 1;
        this.activePlayer = Owner.HUMAN;
    }

    // ---------------- Player access ----------------

    public PlayerState getHuman() {
        return human;
    }

    public PlayerState getAI() {
        return ai;
    }

    public PlayerState getPlayer(Owner owner) {
        return (owner == Owner.HUMAN) ? human : ai;
    }

    // ---------------- Turn access ----------------

    public int turnNumber() {
        return turnNumber;
    }

    public Owner activePlayer() {
        return activePlayer;
    }

    /**
     * End current turn and switch to the other player.
     * Convention:
     * - When it becomes HUMAN's turn, turnNumber increments (new round).
     * - At the start of the new active player's turn, reset their units' actions.
     */
    public void nextTurn() {
        activePlayer = (activePlayer == Owner.HUMAN) ? Owner.AI : Owner.HUMAN;

        // increment "round" when HUMAN starts again
        if (activePlayer == Owner.HUMAN) {
            turnNumber++;
        }

        resetUnitsForNewTurn(activePlayer);
    }

    /**
     * Reset move/attack flags for all units owned by the given player.
     */
    public void resetUnitsForNewTurn(Owner owner) {
        for (int x = 0; x < BOARD_WIDTH; x++) {
            for (int y = 0; y < BOARD_HEIGHT; y++) {
                UnitState u = board[x][y];
                if (u != null && u.owner() == owner) {
                    u.resetTurn();
                }
            }
        }
    }

    // ---------------- Game initialization helpers ----------------

    /**
     * Reset everything to a clean "new game" state.
     * Safe to call from Initialize event.
     */
    public void resetGame() {
        // 1) clear board
        for (int x = 0; x < BOARD_WIDTH; x++) {
            for (int y = 0; y < BOARD_HEIGHT; y++) {
                board[x][y] = null;
            }
        }

        // 2) reset players
        human.setHealth(20);
        ai.setHealth(20);

        human.setMana(0);
        ai.setMana(0);

        // clear hand/deck
        human.hand().clear();
        human.deck().clear();
        ai.hand().clear();
        ai.deck().clear();

        // 3) reset turn
        this.turnNumber = 1;
        this.activePlayer = Owner.HUMAN;
    }

    /**
     * Force-set turn number and active player.
     */
    public void setTurn(int turnNumber, Owner activePlayer) {
        this.turnNumber = turnNumber;
        this.activePlayer = activePlayer;
    }

    /**
     * Place unit using its own (x,y).
     */
    public boolean placeUnit(UnitState unit) {
        if (unit == null) return false;
        return placeUnit(unit, unit.x(), unit.y());
    }

    // ---------------- Board read access (safe) ----------------

    public UnitState getUnitAt(int x, int y) {
        if (!BoardUtils.isInsideBoard(x, y)) return null;
        return board[x][y];
    }

    public boolean isEmpty(int x, int y) {
        return getUnitAt(x, y) == null;
    }

    // ---------------- Board write access (safe) ----------------

    /**
     * Place a unit onto an empty tile.
     * Returns true if success, false if out-of-board or tile occupied.
     */
    public boolean placeUnit(UnitState unit, int x, int y) {
        if (unit == null) return false;
        if (!BoardUtils.isInsideBoard(x, y)) return false;
        if (board[x][y] != null) return false;

        board[x][y] = unit;
        unit.setPosition(x, y);
        return true;
    }

    /**
     * Remove unit at a tile.
     * Returns true if something was removed.
     */
    public boolean removeUnitAt(int x, int y) {
        if (!BoardUtils.isInsideBoard(x, y)) return false;
        if (board[x][y] == null) return false;

        board[x][y] = null;
        return true;
    }

    /**
     * Move a unit from (fromX,fromY) to (toX,toY).
     * Returns true if moved successfully.
     */
    public boolean moveUnit(int fromX, int fromY, int toX, int toY) {
        if (!BoardUtils.isInsideBoard(fromX, fromY)) return false;
        if (!BoardUtils.isInsideBoard(toX, toY)) return false;
        if (board[fromX][fromY] == null) return false;
        if (board[toX][toY] != null) return false;

        UnitState unit = board[fromX][fromY];
        board[fromX][fromY] = null;
        board[toX][toY] = unit;
        unit.setPosition(toX, toY);
        return true;
    }

    // ---------------- Convenience (combat / triggers) ----------------

    public List<UnitState> getAllUnits() {
        List<UnitState> res = new ArrayList<>();
        for (int x = 0; x < BOARD_WIDTH; x++) {
            for (int y = 0; y < BOARD_HEIGHT; y++) {
                if (board[x][y] != null) res.add(board[x][y]);
            }
        }
        return res;
    }

    // (Optional) internal access — try not to use in other modules
    UnitState[][] _boardInternal() {
        return board;
    }
}