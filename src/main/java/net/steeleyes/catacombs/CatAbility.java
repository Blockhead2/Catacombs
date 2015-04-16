/*  This file is part of Catacombs.

    Catacombs is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Catacombs is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Catacombs.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * @author John Keay  <>(@Steeleyes, @Blockhead2)
 * @copyright Copyright (C) 2011
 * @license GNU GPL <http://www.gnu.org/licenses/>
*/
package net.steeleyes.catacombs;

import org.bukkit.configuration.file.FileConfiguration;

public class CatAbility {
  private static final int FAR  = 30;
  
  private EffectType effect;
  private AreaType area;
  private FocusType focus;
  private String name;
    
  private int after=0;
  private int range=3;
  private int duration=5;
  private InterruptMethod castInterrupt=InterruptMethod.NONE;
  private InterruptMethod durationInterrupt=InterruptMethod.NONE;
  
  public enum InterruptMethod {
    NONE,
    ANY,
    DAMAGE,
    HIT,
    SHOOT,
    TAUNT,
    FEED,
    HEAL;
  }
  
  private enum EffectDuration { INSTANT,DURATION };
  private enum EffectBuff { HARM,BUFF };
  
  public enum EffectType {
    // Instant
    ARROW,
    FIRE_BALL,
    LIGHTNING,
    THROW,
    SUMMON,
    SPIN,
    WARP,
    SHUFFLE,
    STARVE,
    DRAIN_EXP,
    EXTINGUISH,

    // Duration
    FIRE_DAMAGE(EffectDuration.DURATION),
    FIRE_START(EffectDuration.DURATION),
    ROOT(EffectDuration.DURATION), 
    FLOOD(EffectDuration.DURATION), 
    POISON(EffectDuration.DURATION), 
    DARKNESS(EffectDuration.DURATION),
    MINES(EffectDuration.DURATION),
    
    // Buffs
    HEAL(EffectBuff.BUFF),
    STRENGTH(EffectBuff.BUFF,EffectDuration.DURATION),
    ARMOUR(EffectBuff.BUFF,EffectDuration.DURATION),
    REGEN(EffectBuff.BUFF,EffectDuration.DURATION);
    
    private Boolean instant = true;
    private Boolean buff = false;
    
    private EffectType() {  
    }
    
    private EffectType(EffectDuration dur) {
      instant = dur==EffectDuration.INSTANT;
    }
    
    private EffectType(EffectBuff b) {
      buff = b==EffectBuff.BUFF;
    }
    
    private EffectType(EffectBuff b,EffectDuration d) {
      buff = b==EffectBuff.BUFF;
      instant = d==EffectDuration.INSTANT;
    }
    
    public Boolean isBuff() {
      return buff;
    }
    
    public Boolean isInstant() {
      return instant;
    }
  }
  
  public enum FocusType {
    SELF,
    TARGET,
    NEAR_PLAYER,
    FAR_PLAYER,
    PLAYER,
    SELF_LOC,
    TARGET_LOC,
    NEAR_PLAYER_LOC,
    FAR_PLAYER_LOC,
    PLAYER_LOC;
  }
  
  public enum AreaType {
    SELF,
    FOCUS,

    PLAYER_NEAR_FOCUS,
    PLAYERS_NEAR_FOCUS,
    PLAYER_FAR_FOCUS,
    PLAYERS_FAR_FOCUS,
    
    MOB_NEAR_FOCUS,
    MOBS_NEAR_FOCUS,
    MOB_FAR_FOCUS,
    MOBS_FAR_FOCUS;
  }
  
  public CatAbility(FileConfiguration fcnf, String name,String path) {
    this.name = name;
    String s_effect = fcnf.getString(path+".effect","ARROW");
    String s_focus = fcnf.getString(path+".focus","TARGET");
    String s_area = fcnf.getString(path+".area","TARGET");
    effect = CatUtils.getEnumFromString(EffectType.class,s_effect);
    area = CatUtils.getEnumFromString(AreaType.class, s_area);
    focus = CatUtils.getEnumFromString(FocusType.class, s_focus);

    after = fcnf.getInt(path+".after",0);
    range = fcnf.getInt(path+".range",3);
    duration = fcnf.getInt(path+".duration",(effect.isInstant())?0:5);
    
    String castI = fcnf.getString(path+".castInterrupt","NONE");
    String durationI = fcnf.getString(path+".durationInterrupt","NONE");
    castInterrupt = CatUtils.getEnumFromString(InterruptMethod.class,castI);
    durationInterrupt = CatUtils.getEnumFromString(InterruptMethod.class,durationI);
  }
 
  @Override
  public String toString() {
    return name+" "+effect+" "+focus+" "+area;
  }

  public Boolean isInstant() {
    return effect.isInstant();
  }
  
  public Boolean isBuff() {
    return effect.isBuff();
  }

  public EffectType getEffect() {
    return effect;
  }

  public String getName() {
    return name;
  }

  public AreaType getArea() {
    return area;
  }
  
  public FocusType getFocus() {
    return focus;
  }  
  public int getAfter() {
    return after;
  }

  public void setAfter(int after) {
    this.after = after;
  }

  public InterruptMethod getCastInterrupt() {
    return castInterrupt;
  }

  public void setCastInterrupt(InterruptMethod castInterrupt) {
    this.castInterrupt = castInterrupt;
  }

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public InterruptMethod getDurationInterrupt() {
    return durationInterrupt;
  }

  public void setDurationInterrupt(InterruptMethod durationInterrupt) {
    this.durationInterrupt = durationInterrupt;
  }

  public int getRange() {
    return range;
  }

  public void setRange(int range) {
    this.range = range;
  }

  
//  public List<LivingEntity> getTargets(CatMob from) {
//    List<LivingEntity> list=null;//= new ArrayList<Player>();
//    LivingEntity tmp;
//    switch(area) {
//      case SELF: 
//        tmp = from.getEntity();
//        if(tmp!=null) {
//          list =  new ArrayList<LivingEntity>();
//          list.add(tmp);
//        }
//        break;
////      case FOCUS: 
////        tmp = from.getTarget();
////        if(tmp!=null) {
////          list =  new ArrayList<LivingEntity>();
////          list.add(tmp);
////        }
////        break;
//      case PLAYER_NEAR_FOCUS:
//        list = CatUtils.getPlayersNear(from.getEntity(),range);
//        CatUtils.pickOne(list);
//        break;
//      case PLAYERS_NEAR_FOCUS:
//        list = CatUtils.getPlayersNear(from.getEntity(),range);
//        break;
//      case PLAYER_FAR_FOCUS:
//        list = CatUtils.getPlayersFar(from.getEntity(),range,FAR);
//        CatUtils.pickOne(list);
//        break;
//      case PLAYERS_FAR_FOCUS:
//        list = CatUtils.getPlayersFar(from.getEntity(),range,FAR);
//        break;
//      default:
//        System.err.println("[Catacombs] Target area "+area+" isn't implemented yet");
//        list = new ArrayList<LivingEntity>();
//        break;
//    }
//    return list;
//  }
//  
//  public List<Player> getTargets(Block from) {
//    List<Player> list = new ArrayList<Player>();
//    return list;
//  }
  // No melee
  // No ranged

  public void captureFocus(CatMob from) {
    // snapshot focus details into the mob using the ability
  }
  
  public void captureTargets(CatMob from) {
    // snapshot targets details into the mob using the ability
  }    

  private void startCasting(CatMob mob) {
    // clear interrupted

    // if after==0
    //   endCasting

    // else
    //   Root the caster
    //   Show bubbles
    //   Inform nearby players
    //   Set casting flag on mob
    //   capture focus info
   }
  
  private void endCasting(CatMob mob) {
    // cancel if interrupted
    // capture target info
    // Inform nearby players
    // apply effect
    
    // if not instant
    //   clear interrupted
    //   set up regular application
    //   set up end application
    
  }
  
  
}
