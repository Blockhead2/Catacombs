// Derived from GarbageMule's MobAreana MACreature class

package net.steeleyes.catacombs;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Slime;

public enum MobShape {
  // Standard creatures
  ZOMBIE        (EntityType.ZOMBIE),
  SKELETON      (EntityType.SKELETON),
  CREEPER       (EntityType.CREEPER),
  PIG_ZOMBIE    (EntityType.PIG_ZOMBIE),
  SPIDER        (EntityType.SPIDER),
  CAVE_SPIDER   (EntityType.CAVE_SPIDER),
  BLAZE         (EntityType.BLAZE),
  WOLF          (EntityType.WOLF),
  SILVERFISH    (EntityType.SILVERFISH),
  ENDERMAN      (EntityType.ENDERMAN),
  GHAST         (EntityType.GHAST),
  GIANT         (EntityType.GIANT),
  SLIME         (EntityType.SLIME),
  CHICKEN       (EntityType.CHICKEN),
  COW           (EntityType.COW),
  SQUID         (EntityType.SQUID),
  SHEEP         (EntityType.SHEEP),
  PIG           (EntityType.PIG),
  
  // Special creatures
  POWEREDCREEPER(EntityType.CREEPER);

  private EntityType type;
  
  private MobShape(EntityType type) {
    this.type = type;  
  }
  
  public EntityType getType() {
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
  
  public static MobShape getType(EntityType c) {
    return getType(c.toString());
  }
  
  public static MobShape getType(String string) {
    return CatUtils.getEnumFromString(MobShape.class, string.replaceAll("[-\\.]", ""));
  }

  
}

