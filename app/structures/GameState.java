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