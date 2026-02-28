import static org.junit.Assert.*;

//creates fresh game state to not interfere with other tests
import org.junit.Before;
import org.junit.Test;

import game.core.CoreGameState;
import game.core.Owner;
import game.core.UnitState;

public class AvatarHealthDamageTest {
  private CoreGameState core;
  private UnitState humanAvatar;
  private UnitState aiAvatar;

  @Before
  public void setup(){
    core = new CoreGameState();

    //create new avatars
    humanAvatar = new UnitState(100, Owner.HUMAN, 4, 4, 2, 20);

    humanAvatar.setAvatar(true);

    core.placeUnit(humanAvatar, 2, 3);

    core.setHumanAvatar(humanAvatar);

    aiAvatar = new UnitState(200, Owner.AI, 4, 0, 2, 20);

    aiAvatar.setAvatar(true);

    core.placeUnit(aiAvatar, 8, 3);

    core.setAIAvatar(aiAvatar);
  }

  //-------- testing isAvatar------------

  //is avatar labeled as an avatar?
  @Test
  public void AvatarUnitAsAvatar(){
    assertTrue(humanAvatar.isAvatar());
    assertTrue(aiAvatar.isAvatar());
  }

  //Are regular units marked as avatars?
  @Test
  public void RegularUnitNotAvatar(){
    UnitState regular = new UnitState(1, Owner.HUMAN, 1, 1, 2, 20);
    assertFalse(regular.isAvatar());
  }

  //------ testing health -------------

  // does health get set to unit heath?
  @Test
  public void setHealthToUnitHealth(){
    humanAvatar.setHealth(15);
    assertEquals(15, humanAvatar.hp());
  }

  // does health return current hp?
  @Test
  public void healthReturnCorrectHp(){
    assertEquals(20, humanAvatar.hp());
  }

  //------- testing damage to unit ----------

  // does damage to the human avatar reduce health?
  @Test
  public void damageReducesUnitHealth(){
    core.applyDamageToUnit(humanAvatar, 10);
      assertEquals(10, humanAvatar.hp());
  }

  //does damage to avatar reduce player health?
  @Test
  public void damageReducePlayerHealth(){
    core.applyDamageToUnit(humanAvatar, 7);
    assertEquals(13, core.getHuman().health());
  }

  // does damage to ai reduce ai health?
  @Test 
  public void aiDamageAffectAiHealth(){
    core.applyDamageToUnit(aiAvatar, 5);
    assertEquals(15, core.getAI().health());
  }
  
  // does damage to the human avatar affect Ai?
  @Test
  public void humanDamageAffectOnAI(){
    core.applyDamageToUnit(humanAvatar, 3);
    assertEquals(20, core.getAI().health());
  }

  // ----- testing healing to unit ---------

  // does healing increase unit health? 
  @Test 
  public void healingIncreaseHealth(){
    core.applyDamageToUnit(humanAvatar, 5);
    core.applyHealingToUnit(humanAvatar, 4);

    assertEquals(19, humanAvatar.hp());
  }

  // does healing human avatar increase player health?
  @Test 
  public void healingAvatarAffectPlayerHealth(){
    core.applyDamageToUnit(humanAvatar, 3);
    core.applyHealingToUnit(humanAvatar,2);

    assertEquals(19, humanAvatar.hp());
  }

  //does health increase ai player health? 
  @Test 
  public void healingIncreaseHealthAI(){
    core.applyDamageToUnit(aiAvatar, 8);
    core.applyHealingToUnit(aiAvatar, 3);

    assertEquals(15, aiAvatar.hp());
  }

  // does healing a regular unit impact player health? 
  @Test 
  public void regularUnitHealingNoImpactOnPlayerHealth(){
    UnitState regular = new UnitState(1, Owner.HUMAN, 1, 1, 2, 20);
    core.placeUnit(regular, 5, 1);

    core.applyDamageToUnit(regular, 5);
    core.applyHealingToUnit(regular, 3);

    assertEquals(18, regular.hp());
    assertEquals(20, core.getHuman().health());
  }

}
