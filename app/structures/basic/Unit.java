package structures.basic;

import akka.actor.ActorRef;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import commands.BasicCommands;
import structures.GameState;

/**
 * This is a representation of a Unit on the game board.
 * A unit has a unique id (this is used by the front-end.
 * Each unit has a current UnitAnimationType, e.g. move,
 * or attack. The position is the physical position on the
 * board. UnitAnimationSet contains the underlying information
 * about the animation frames, while ImageCorrection has
 * information for centering the unit on the tile. 
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Unit {

	@JsonIgnore
	protected static ObjectMapper mapper = new ObjectMapper(); // Jackson Java Object Serializer, is used to read java objects from a file
	
	int id;
	UnitAnimationType animation;
	Position position;
	UnitAnimationSet animations;
	ImageCorrection correction;
	
	public Unit() {}
	int attack;
	int health;
	private boolean canMove =false;
	private boolean canAttack =false;

	private boolean moveAndAttack=false;
	public Unit other;
	
	public Unit(int id, UnitAnimationSet animations, ImageCorrection correction) {
		super();
		this.id = id;
		this.animation = UnitAnimationType.idle;
		
		position = new Position(0,0,0,0);
		this.correction = correction;
		this.animations = animations;
	}
	
	public Unit(int id, UnitAnimationSet animations, ImageCorrection correction, Tile currentTile) {
		super();
		this.id = id;
		this.animation = UnitAnimationType.idle;
		
		position = new Position(currentTile.getXpos(),currentTile.getYpos(),currentTile.getTilex(),currentTile.getTiley());
		this.correction = correction;
		this.animations = animations;
	}
	
	
	
	public Unit(int id, UnitAnimationType animation, Position position, UnitAnimationSet animations,
			ImageCorrection correction) {
		super();
		this.id = id;
		this.animation = animation;
		this.position = position;
		this.animations = animations;
		this.correction = correction;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public UnitAnimationType getAnimation() {
		return animation;
	}
	public void setAnimation(UnitAnimationType animation) {
		this.animation = animation;
	}

	public ImageCorrection getCorrection() {
		return correction;
	}

	public void setCorrection(ImageCorrection correction) {
		this.correction = correction;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public UnitAnimationSet getAnimations() {
		return animations;
	}

	public void setAnimations(UnitAnimationSet animations) {
		this.animations = animations;
	}
	
	/**
	 * This command sets the position of the Unit to a specified
	 * tile.
	 * @param tile
	 */
	@JsonIgnore
	public void setPositionByTile(Tile tile) {
		position = new Position(tile.getXpos(),tile.getYpos(),tile.getTilex(),tile.getTiley());
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public int getAttack() {
		return attack;
	}

	public int getHealth() {
		return health;
	}

	public boolean isCanMove() {
		return canMove;
	}

	public void setCanMove(boolean canMove) {
		this.canMove = canMove;
	}

	public boolean isCanAttack() {
		return canAttack;
	}

	public void setCanAttack(boolean attack) {
		canAttack = attack;
	}

	public boolean isMoveAndAttack() {
		return moveAndAttack;
	}

	public void setMoveAndAttack(boolean moveAndAttack) {
		this.moveAndAttack = moveAndAttack;
	}

	public void attack(ActorRef out, GameState gameState,Unit other)
	{
		BasicCommands.playUnitAnimation(out, this, UnitAnimationType.attack);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		other.setHealth(other.getHealth()-attack);
		BasicCommands.setUnitHealth(out,other,other.getHealth());
		if (other.getId()==0)
		{
			gameState.getHumanPlayer().setHealth(other.getHealth());
			BasicCommands.setPlayer1Health(out,gameState.getHumanPlayer());
			if (other.health<=0)
			{
				BasicCommands.addPlayer1Notification(out,"You lose",100);
			}
		}else if (other.getId()==-1)
		{
			gameState.getAiPlayer().setHealth(other.getHealth());
			BasicCommands.setPlayer2Health(out,gameState.getAiPlayer());
			if (other.health<=0)
			{
				BasicCommands.addPlayer1Notification(out,"You win",100);
			}
		}
		BasicCommands.playUnitAnimation(out, this, UnitAnimationType.idle);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (other.getHealth()<=0)
		{
			BasicCommands.playUnitAnimation(out,other,UnitAnimationType.death);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			BasicCommands.deleteUnit(out,other);
			gameState.aiUnits.remove(other);
			gameState.playerUnits.remove(other);
		}else
		{
			BasicCommands.playUnitAnimation(out,other,UnitAnimationType.attack);
			health-=other.attack;
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			BasicCommands.setUnitHealth(out,this,health);
			if (getId()==0)
			{
				gameState.getHumanPlayer().setHealth(health);
				BasicCommands.setPlayer1Health(out,gameState.getHumanPlayer());
				if (health<=0)
				{
					BasicCommands.addPlayer1Notification(out,"You lose",100);
				}
			}else if (getId()==-1)
			{
				gameState.getAiPlayer().setHealth(health);
				BasicCommands.setPlayer2Health(out,gameState.getAiPlayer());
				if (health<=0)
				{
					BasicCommands.addPlayer1Notification(out,"You win",100);
				}
			}
			if (health<=0)
			{
				BasicCommands.playUnitAnimation(out,this,UnitAnimationType.death);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				BasicCommands.deleteUnit(out,this);
				gameState.aiUnits.remove(this);
				gameState.playerUnits.remove(this);
			}
			BasicCommands.playUnitAnimation(out,other,UnitAnimationType.idle);
		}
	}
}
