package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameState;

//HG - need to import Basic Commands and Tile (SC8)
import comands.BasicCommands;
import structures.basic.Tile;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a tile.
 * The event returns the x (horizontal) and y (vertical) indices of the tile that was
 * clicked. Tile indices start at 1.
 * 
 * { 
 *   messageType = “tileClicked”
 *   tilex = <x index of the tile>
 *   tiley = <y index of the tile>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class TileClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();
		
		// HG - get tile that was clicked (SC8)
		Tile clickedTile = gameState.board[tilex-1][tiley-1];

		if (gameState.something == true) {
			// do some logic

			// HG - clear highlighted tiles when human player triggers action (SC8)
			if (gameState.tilesHighlighted && clickedTile != null && clickedTile.getHighlighted() != 0){
				clearBoardHighlights(out, gameState);
			}
		}

	}
	// HG - sets highlight state = 0 on each Tile and shows correct state to player with BasicCommands (SC8)
	public static void clearBoardHighlights (ActorRed out, GameState gameState){
		for (int x = 0; x < 9; x++){
			for (int y =0; y < 5; y ++){
				Tile tile = gameState.board[x][y];
				if (tile != null && tile.getHighlighted() != 0){
					tile.setHighlighted(0);
					BasicCommands.drawTile(out, tile, 0);
				}
			}
		}
		gameState.tilesHighlighted = false;
	}
}
