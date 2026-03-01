package events;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.BetterUnit;
import structures.basic.Card;
import structures.basic.EffectAnimation;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;
import utils.HighlightUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TileClicked implements EventProcessor {

    @Override
    public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

        if (gameState.gameOver) return;
        if (!"HUMAN".equals(gameState.activePlayer)) return;

        int tilex = message.get("tilex").asInt();
        int tiley = message.get("tiley").asInt();


        int x = tilex;
        int y = tiley;

        // Must have selected a unit card
        if (gameState.selectedHandPos == null) return;
        if (!gameState.selectedCardIsUnit) return;
        if (gameState.selectedCardConfig == null) return;

        int selectedPos = gameState.selectedHandPos;

        // Load selected card
        Card card = BasicObjectBuilders.loadCard(gameState.selectedCardConfig, 1000 + selectedPos, Card.class);
        if (card == null || !card.isCreature()) return;

        // Tile must be empty
        if (gameState.boardUnits.containsKey(gameState.key(x, y))) return;

        // Simple summon rule (stable + easy):
        // can summon only within 1 tile of human avatar at (1,2)
        if (!isWithinOneTile(x, y, 1, 2)) return;

        // Mana check
        int cost = card.getManacost();
        if (gameState.humanMana < cost) {
            BasicCommands.addPlayer1Notification(out, "Not enough mana", 2);
            return;
        }

        // 1) decrement mana + update UI
        gameState.humanMana -= cost;
        BasicCommands.setPlayer1Mana(out, new Player(gameState.humanHealth, gameState.humanMana));

        // 2) play summon animation
        Tile tile = BasicObjectBuilders.loadTile(tilex, tiley);
        EffectAnimation summonFx = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_summon);
        if (summonFx != null) BasicCommands.playEffectAnimation(out, summonFx, tile);

        // 3) create unit id + stats
        int unitId = gameState.allocateUnitId();
        int atk = card.getBigCard().getAttack();
        int hp = card.getBigCard().getHealth();

        // 4) draw unit in frontend + set stats
        Unit unit = BasicObjectBuilders.loadUnit(card.getUnitConfig(), unitId, BetterUnit.class);
        if (unit == null) return;

        unit.setPositionByTile(tile);
        BasicCommands.drawUnit(out, unit, tile);
        BasicCommands.setUnitAttack(out, unit, atk);
        BasicCommands.setUnitHealth(out, unit, hp);

        // Track on board (UI)
        gameState.boardUnits.put(gameState.key(x, y), unit);
        gameState.uiUnitById.put(unitId, unit);

        // 5) remove selected card from hand UI
        // Delete selected position, then redraw remaining cards compacted.
        BasicCommands.deleteCard(out, selectedPos);

        List<String> hand = getCurrentHumanHandConfigs();
        if (hand.size() >= selectedPos) {
            hand.remove(selectedPos - 1);
        }

        // redraw up to 6
        for (int i = 0; i < hand.size() && i < 6; i++) {
            String cfg = hand.get(i);
            int handPos = i + 1;
            Card c = BasicObjectBuilders.loadCard(cfg, 1000 + handPos, Card.class);
            if (c != null) BasicCommands.drawCard(out, c, handPos, 0);
        }
        // clear any leftover slots if hand shrank
        for (int pos = hand.size() + 1; pos <= 6; pos++) {
            BasicCommands.deleteCard(out, pos);
        }

        // 6) clear selection/highlights
        HighlightUtils.clearHighlightedTiles(out, gameState);
        gameState.selectedHandPos = null;
        gameState.selectedCardConfig = null;
        gameState.selectedCardIsUnit = false;
    }

    private boolean isWithinOneTile(int x, int y, int ax, int ay) {
        int dx = Math.abs(x - ax);
        int dy = Math.abs(y - ay);
        return dx <= 1 && dy <= 1 && !(dx == 0 && dy == 0);
    }

    /**
     * Temporary: derive current hand configs from sorted 1_*.json.
     * We assume UI hand shows the earliest cards in sorted order, up to 6.
     */
    private List<String> getCurrentHumanHandConfigs() {
        File dir = new File("conf/gameconfs/cards/");
        String[] p1 = dir.list((d, name) -> name.startsWith("1_") && name.endsWith(".json"));
        if (p1 == null) return new ArrayList<>();

        Arrays.sort(p1);

        List<String> res = new ArrayList<>();
        for (int i = 0; i < p1.length && i < 6; i++) {
            res.add("conf/gameconfs/cards/" + p1[i]);
        }
        return res;
    }
}