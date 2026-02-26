package events;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import commands.BasicCommands;
import game.core.CoreGameState;
import game.core.Owner;
import game.core.UnitState;
import game.util.SummonRules;
import structures.GameState;
import structures.basic.BetterUnit;
import structures.basic.Card;
import structures.basic.EffectAnimation;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class TileClicked implements EventProcessor {

    @Override
    public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

        CoreGameState core = gameState.core();

        if (gameState.gameOver) return;
        if (core.activePlayer() != Owner.HUMAN) return;

        int tilex = message.get("tilex").asInt(); // 1-based
        int tiley = message.get("tiley").asInt(); // 1-based

        int x = tilex - 1; // convert to 0-based for core board
        int y = tiley - 1;

        // Story #25 only triggers if a unit card was selected
        if (gameState.selectedHandPos == null) return;
        if (!gameState.selectedCardIsUnit) return;
        if (gameState.selectedCardConfig == null) return;

        // load selected card
        int selectedPos = gameState.selectedHandPos;
        Card card = BasicObjectBuilders.loadCard(gameState.selectedCardConfig, selectedPos, Card.class);
        if (card == null || !card.isCreature()) return;

        // validate tile
        if (!SummonRules.isValidSummonTile(core, Owner.HUMAN, x, y)) return;

        // validate mana
        int cost = card.getManacost();
        if (core.getHuman().mana() < cost) return;

        // 1) decrement mana + update UI
        core.getHuman().setMana(core.getHuman().mana() - cost);
        BasicCommands.setPlayer1Mana(out, new Player(core.getHuman().health(), core.getHuman().mana()));

        // 2) play summon animation
        Tile tile = BasicObjectBuilders.loadTile(tilex, tiley);
        EffectAnimation summonFx = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_summon);
        if (summonFx != null) BasicCommands.playEffectAnimation(out, summonFx, tile);

        // 3) create unit in backend
        int unitId = gameState.allocateUnitId();
        int atk = card.getBigCard().getAttack();
        int hp = card.getBigCard().getHealth();

        UnitState unitState = new UnitState(unitId, Owner.HUMAN, x, y, atk, hp);
        boolean placed = core.placeUnit(unitState, x, y);
        if (!placed) return;

        // 4) draw unit in frontend + set stats
        Unit unit = BasicObjectBuilders.loadUnit(card.getUnitConfig(), unitId, BetterUnit.class);
        if (unit == null) return;

        BasicCommands.drawUnit(out, unit, tile);
        BasicCommands.setUnitAttack(out, unit, atk);
        BasicCommands.setUnitHealth(out, unit, hp);

        //#13 story
        //remember this UI unit to delete later if it dies
        gameState.uiUnitById.put(unitId,unit);


        // 5) remove card from backend hand
        core.getHuman().hand().remove(selectedPos - 1);

        // 6) remove + re-render hand UI
        BasicCommands.deleteCard(out, selectedPos);
        for (int i = selectedPos; i <= core.getHuman().hand().size(); i++) {
            String cfg = core.getHuman().hand().get(i - 1);
            Card c = BasicObjectBuilders.loadCard(cfg, i, Card.class);
            if (c != null) BasicCommands.drawCard(out, c, i, 0);
        }

        // 7) clear selection
        gameState.selectedHandPos = null;
        gameState.selectedCardConfig = null;
        gameState.selectedCardIsUnit = false;
    }
}