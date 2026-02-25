package structures;

import game.core.CoreGameState;

/**
 * Template-facing wrapper.
 * Keep this class stable: other modules import structures.GameState.
 */
public class GameState {

	// ---- Compatibility flags (events may still reference these) ----
	public boolean gameInitialized = false;   // MUST match Initiali！z！e.java spelling
	public boolean something = false;

	// ---- Game end state ----
	public boolean gameOver = false;
	public game.core.Owner winner = null; // HUMAN or AI

	// --- Card selection state ---
	public Integer selectedHandPos = null;     // 1..6
	public String selectedCardConfig = null;   // path to conf/gameconfs/cards/*.json
	public boolean selectedCardIsUnit = false;

	// --- Unit id generator ---
	public int nextUnitId = 1000;
	public int allocateUnitId() {
    return nextUnitId++;
	}

	// ---- Real game data ----
	private final CoreGameState core;
	public boolean gameInitalized;

	public GameState() {
		this.core = new CoreGameState();
	}

	public CoreGameState core() {
		return core;
	}
}