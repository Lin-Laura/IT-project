package structures;
//HG - importing Tile (SC8)
import structures.basic.Tile;

/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class GameState {

	
	public boolean gameInitalised = false;
	
	public boolean something = false;

	//HG - created 9 x 5 game board (SC8)
	public Tile[][] board = new Tile[9][5];
	//HG - no actions or tile clicks will initalize highlighting if not humans turn (SC8)
	public boolean isHumanPlayerTurn = false;
	//HG - tracks state of the tiles being highlighted (SC8)
	public boolean isTilesHighlighted = false;
}
