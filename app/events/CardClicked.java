package events;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import commands.BasicCommands;
import game.core.CoreGameState;
import game.core.Owner;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Player;
import utils.BasicObjectBuilders;

public class CardClicked implements EventProcessor {

    @Override
    public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

        CoreGameState core = gameState.core();

        // only human turn can select
        if (core.activePlayer() != Owner.HUMAN) return;
        if (gameState.gameOver) return;

        int handPosition = message.get("position").asInt(); // 1..6
        if (handPosition < 1 || handPosition > core.getHuman().hand().size()) return;

        String cardConfig = core.getHuman().hand().get(handPosition - 1);
        Card card = BasicObjectBuilders.loadCard(cardConfig, handPosition, Card.class);
        if (card == null) return;

        // must be a unit card 
        if (!card.isCreature()) return;

        // must have enough mana to play
        if (core.getHuman().mana() < card.getManacost()) {
            BasicCommands.addPlayer1Notification(out, "Not enough mana", 2);
            return;
        }

        // store selection
        gameState.selectedHandPos = handPosition;
        gameState.selectedCardConfig = cardConfig;
        gameState.selectedCardIsUnit = true;

        // optional: visually highlight selected card
        BasicCommands.drawCard(out, card, handPosition, 1);
    }
}