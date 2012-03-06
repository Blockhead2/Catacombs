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

public enum MobShape {
  // Standard creatures
  ZOMBIE        (CreatureType.ZOMBIE),
  SKELETON      (CreatureType.SKELETON),
  CREEPER       (CreatureType.CREEPER),
  PIG_ZOMBIE    (CreatureType.PIG_ZOMBIE),
  SPIDER        (CreatureType.SPIDER),
  CAVE_SPIDER   (CreatureType.CAVE_SPIDER),
  BLAZE         (CreatureType.BLAZE),
  WOLF          (CreatureType.WOLF),
  SILVERFISH    (CreatureType.SILVERFISH),
  ENDERMAN      (CreatureType.ENDERMAN),
  GHAST         (CreatureType.GHAST),
  GIANT         (CreatureType.GIANT),
  SLIME         (CreatureType.SLIME),
  CHICKEN       (CreatureType.CHICKEN),
  COW           (CreatureType.COW),
  SQUID         (CreatureType.SQUID),
  SHEEP         (CreatureType.SHEEP),
  PIG           (CreatureType.PIG),
  
  // Special creatures
  POWEREDCREEPER(CreatureType.CREEPER);

  private CreatureType type;
  
  private MobShape(CreatureType type) {
    this.type = type;  
  }
  
  public CreatureType getType() {
    return type;
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
  
  public static MobShape getType(CreatureType c) {
    return getType(c.toString());
  }
  
  public static MobShape getType(String string) {
    return CatUtils.getEnumFromString(MobShape.class, string.replaceAll("[-\\.]", ""));
  }

  
}

