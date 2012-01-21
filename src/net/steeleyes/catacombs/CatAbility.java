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

import java.util.ArrayList;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class CatAbility {
  private static final int NEAR = 3;
  private static final int FAR  = 20;
  
  private EffectType effect;
  private TargetType type;
  private String name;
  private int after;
  private Entity ent=null;
  
  public enum EffectType {
    ARROW,
    FIRE_BALL,
    FIRE_DAMAGE,
    FIRE_START,
    LIGHTNING,
    ROOT,
    THROW,
    SUMMON,
    SPIN,
    WARP,
    FLOOD,
    SHUFFLE;        
  }
  
  public enum TargetType {
    TARGET,
    ONE_NEAR_ENT,
    ALL_NEAR_ENT,
    ONE_FAR_ENT,
    ALL_FAR_ENT,
    BLOCK,
    ONE_NEAR_BLK,
    ALL_NEAR_BLK,
    ONE_FAR_BLK,
    ALL_FAR_BLK;
  }
  
  public CatAbility(String name,EffectType effect, TargetType type,int after) {
    this.name = name;
    this.effect = effect;
    this.type = type;
    this.after = after;
  }
  
  public CatAbility(String name,String effect, String type,int after) {
    this.name = name;
    this.effect = CatUtils.getEnumFromString(EffectType.class, effect);
    this.type = CatUtils.getEnumFromString(TargetType.class, type);
    this.after = after;
  }
  
  @Override
  public String toString() {
    return name;
  }
  
  public List<Player> getTargets(CatMob from) {
    List<Player> list=null;//= new ArrayList<Player>();
    switch(type) {
      case TARGET: 
        Player target = from.getTarget();
        if(target!=null) {
          list =  new ArrayList<Player>();
          list.add(target);
        }
        break;
      case ONE_NEAR_ENT:
        list = CatUtils.getPlayerNear(from.getEntity(),NEAR);
        CatUtils.pickOne(list);
        break;
      case ALL_NEAR_ENT:
        list = CatUtils.getPlayerNear(from.getEntity(),NEAR);
        break;
      case ONE_FAR_ENT:
        list = CatUtils.getPlayerFar(from.getEntity(),NEAR,FAR);
        CatUtils.pickOne(list);
        break;
      case ALL_FAR_ENT:
        list = CatUtils.getPlayerFar(from.getEntity(),NEAR,FAR);
        break;
      default:
        list = new ArrayList<Player>();
    }
    return list;
  }
  
  public List<Player> getTargets(Block from) {
    List<Player> list = new ArrayList<Player>();
    return list;
  }
  // No melee
  // No ranged
  
  
}
