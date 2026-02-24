package game.util;

import game.core.CoreGameState;
import game.core.Owner;

import java.util.ArrayList;
import java.util.List;

public final class BoardUtils {

    private BoardUtils() {}

    // ---------- basic coordinate helpers ----------

    public static boolean isInsideBoard(int x, int y) {
        return x >= 0 && x < CoreGameState.BOARD_WIDTH
                && y >= 0 && y < CoreGameState.BOARD_HEIGHT;
    }

    // ---------- required by story cards / other systems ----------

    public static boolean isTileEmpty(CoreGameState state, int x, int y) {
        return state != null && state.getUnitAt(x, y) == null && isInsideBoard(x, y);
    }

    /**
     * Adjacent = 4-neighbour (up, down, left, right).
     * Returns a list of int[2] pairs: {x, y}.
     */
    public static List<int[]> getAdjacentTiles(int x, int y) {
        int[][] dirs = new int[][] {
                { 1, 0 },  // right
                {-1, 0 },  // left
                { 0, 1 },  // down
                { 0,-1 }   // up
        };

        List<int[]> res = new ArrayList<>();
        for (int[] d : dirs) {
            int nx = x + d[0];
            int ny = y + d[1];
            if (isInsideBoard(nx, ny)) {
                res.add(new int[] { nx, ny });
            }
        }
        return res;
    }

    /**
     * Front tile depends on owner:
     * - HUMAN front = y - 1 (towards top)
     * - AI    front = y + 1 (towards bottom)
     * Returns int[2] {x, y} or null if out of board.
     */
    public static int[] getFrontTile(int x, int y, Owner owner) {
        int ny = (owner == Owner.HUMAN) ? (y - 1) : (y + 1);
        if (!isInsideBoard(x, ny)) return null;
        return new int[] { x, ny };
    }

    /**
     * Behind tile is opposite of front:
     * - HUMAN behind = y + 1
     * - AI    behind = y - 1
     * Returns int[2] {x, y} or null if out of board.
     */
    public static int[] getBehindTile(int x, int y, Owner owner) {
        int ny = (owner == Owner.HUMAN) ? (y + 1) : (y - 1);
        if (!isInsideBoard(x, ny)) return null;
        return new int[] { x, ny };
    }
}