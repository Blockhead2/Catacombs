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

import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
//  ZOMBIE        (CreatureType.ZOMBIE,50),
//  SKELETON      (CreatureType.SKELETON,40),
//  CREEPER       (CreatureType.CREEPER,6),
//  PIG_ZOMBIE    (CreatureType.PIG_ZOMBIE,30),
//  SPIDER        (CreatureType.SPIDER,30),
//  CAVE_SPIDER   (CreatureType.CAVE_SPIDER,20),
//  BLAZE         (CreatureType.BLAZE,40),
//  WOLF          (CreatureType.WOLF,30),
//  SILVERFISH    (CreatureType.SILVERFISH,10),
//  ENDERMAN      (CreatureType.ENDERMAN,40),
//  GHAST         (CreatureType.GHAST,30),
//  GIANT         (CreatureType.GIANT,100),
//  SLIME         (CreatureType.SLIME,30),
//  CHICKEN       (CreatureType.CHICKEN,15),
//  COW           (CreatureType.COW,30),
//  SQUID         (CreatureType.SQUID,20),
//  SHEEP         (CreatureType.SHEEP,20),
//  PIG           (CreatureType.PIG,25),
//  
//  // Special creatures
//  POWEREDCREEPER(CreatureType.CREEPER,12);

public class MobType {
  private int hps;
  private String name;
  private MobShape shape;
  private List<CatAbility> abilities;
  private List<CatLootList> loot;
  private String gold;
  private int speed = 20;
  
  public MobType(String name,String shape,int hps,String gold,List<CatAbility> abilities,List<CatLootList> loot) {
    this.name = name;
    this.shape = CatUtils.getEnumFromString(MobShape.class, shape);
    this.hps = hps;
    this.gold = gold;
    this.abilities = abilities;
    this.loot = loot;
  }
  
  @Override
  public String toString() {
    return name+" "+shape+" "+hps+" "+abilities+" "+loot;
  }
  
  public LivingEntity spawn(Location loc) {
    return shape.spawn(loc.getWorld(),loc);
  }
  
  public LivingEntity spawn(LivingEntity e) {
    return shape.spawn(e);
  }
  
  public List<CatAbility> getAbilities() {
    return abilities;
  }

  public void setAbilities(List<CatAbility> abilities) {
    this.abilities = abilities;
  }

  public int getSpeed() {
    return speed;
  }
  
  public String getGold() {
    return gold;
  }

  public void setGold(String gold) {
    this.gold = gold;
  }

  public int getHps() {
    return hps;
  }

  public void setHps(int hps) {
    this.hps = hps;
  }

  public List<CatLootList> getLoot() {
    return loot;
  }

  public void setLoot(List<CatLootList> loot) {
    this.loot = loot;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public MobShape getShape() {
    return shape;
  }

  public void setShape(MobShape shape) {
    this.shape = shape;
  }
  
  
}
