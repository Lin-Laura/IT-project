// package events;

// import com.fasterxml.jackson.databind.JsonNode;

// import akka.actor.ActorRef;
// import structures.GameState;

// /**
//  * Indicates that the user has clicked an object on the game canvas, in this case
//  * the end-turn button.
//  * 
//  * { 
//  *   messageType = “endTurnClicked”
//  * }
//  * 
//  * @author Dr. Richard McCreadie
//  *
//  */
// public class EndTurnClicked implements EventProcessor{

// 	@Override
// 	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
// 	}

// }




package events; 

import com.fasterxml.jackson.databind.JsonNode;
import akka.actor.ActorRef;
import commands.BasicCommands;

import structures.GameState;
import structures.Player;
import structures.Card;
import structures.Deck;
import structures.Hand;

 	
public class EndTurnClicked implements EventProcessor {

    @Override
    public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
        if (gameState.isHumanTurn == false) {
            return; 
        }

        Player humanPlayer = gameState.humanPlayer; 

        Deck deck = humanPlayer.getDeck();
        Hand hand = humanPlayer.getHand();
        
        if (deck.getCards().size() > 0) {
            Card topCard = deck.getCards().remove(0); 
            hand.getCards().add(topCard);             
        }

        humanPlayer.setMana(0);

        gameState.isHumanTurn = false;
        gameState.turnNumber = gameState.turnNumber + 1;

        BasicCommands.addPlayer1Notification(out, "Opponent's Turn", 2);
    }
}
