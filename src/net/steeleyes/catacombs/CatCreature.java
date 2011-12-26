// Derived from GarbageMule's MobAreana MACreature class

package net.steeleyes.catacombs;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Slime;

public enum CatCreature {
  // Standard creatures
  ZOMBIE        (CreatureType.ZOMBIE,50),
  SKELETON      (CreatureType.SKELETON,40),
  CREEPER       (CreatureType.CREEPER,6),
  PIG_ZOMBIE    (CreatureType.PIG_ZOMBIE,30),
  SPIDER        (CreatureType.SPIDER,30),
  CAVE_SPIDER   (CreatureType.CAVE_SPIDER,20),
  BLAZE         (CreatureType.BLAZE,40),
  WOLF          (CreatureType.WOLF,30),
  SILVERFISH    (CreatureType.SILVERFISH,10),
  ENDERMAN      (CreatureType.ENDERMAN,40),
  GHAST         (CreatureType.GHAST,30),
  GIANT         (CreatureType.GIANT,100),
  SLIME         (CreatureType.SLIME,30),
  CHICKEN       (CreatureType.CHICKEN,15),
  COW           (CreatureType.COW,30),
  SQUID         (CreatureType.SQUID,20),
  SHEEP         (CreatureType.SHEEP,20),
  PIG           (CreatureType.PIG,25),
  
  // Special creatures
  POWEREDCREEPER(CreatureType.CREEPER,12);

  private CreatureType type;
  private int hps;
  
  private CatCreature(CreatureType type, int hps) {
    this.type = type;  
    this.hps = hps;
  }
  
  public CreatureType getType() {
    return type;
  }
  
  public int getHits() {
    return hps;
  }  
  public LivingEntity spawn(World world, Location loc) {
    LivingEntity e = world.spawnCreature(loc, type);
    return spawn(e);
  }
  
  public LivingEntity spawn(LivingEntity e) {
    switch(this) {
      case WOLF:
        ((Wolf) e).setAngry(true);
        break;
      case PIG_ZOMBIE:
        ((PigZombie) e).setAngry(true);
        break;
      case POWEREDCREEPER:
        ((Creeper) e).setPowered(true);
        break;
      case SLIME:
        ((Slime) e).setSize(2);
        break;
      default:
        break;
    }   
    return e;
  }
  
  public static CatCreature getType(CreatureType c) {
    return getType(c.toString());
  }
  
  public static CatCreature getType(String string) {
    return CatUtils.getEnumFromString(CatCreature.class, string.replaceAll("[-\\.]", ""));
  }

  
}

