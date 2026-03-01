// package events;


// import com.fasterxml.jackson.databind.JsonNode;

// import akka.actor.ActorRef;
// import structures.GameState;

// /**
//  * Indicates that the user has clicked an object on the game canvas, in this case a card.
//  * The event returns the position in the player's hand the card resides within.
//  * 
//  * { 
//  *   messageType = “cardClicked”
//  *   position = <hand index position [1-6]>
//  * }
//  * 
//  * @author Dr. Richard McCreadie
//  *
//  */
// public class CardClicked implements EventProcessor{

// 	@Override
// 	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
// 		int handPosition = message.get("position").asInt();
		
		
// 	}

// }
package events;

import com.fasterxml.jackson.databind.JsonNode;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.Player;
import structures.Card;
import structures.Tile;

public class CardClicked implements EventProcessor {

    @Override
    public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
        
        if (gameState.isHumanTurn == false) {
            return;
        }

        Player humanPlayer = gameState.humanPlayer;
        int handPosition = message.get("position").asInt();
        int realIndex = handPosition - 1;
        
        if (realIndex < 0 || humanPlayer.getHand().getCards().size() <= realIndex) {
            return;
        }
        
        Card clickedCard = humanPlayer.getHand().getCards().get(realIndex);

        if (humanPlayer.getMana() >= clickedCard.getManacost()) {
            
            BasicCommands.addPlayer1Notification(out, "Card Selected!", 2);

            for (int i = 0; i < gameState.board.getTiles().size(); i++) {
                Tile tile = gameState.board.getTiles().get(i);
                
                if (tile.getUnit() == null) {
                    BasicCommands.drawTile(out, tile, 1);
                }
            }
            
        } else {
            BasicCommands.addPlayer1Notification(out, "Not enough mana!", 2);
        }
    }
}
