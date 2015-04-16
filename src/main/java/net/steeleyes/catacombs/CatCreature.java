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

public enum CatCreature {
  // Standard creatures
  ZOMBIE        (EntityType.ZOMBIE,50),
  SKELETON      (EntityType.SKELETON,40),
  CREEPER       (EntityType.CREEPER,6),
  PIG_ZOMBIE    (EntityType.PIG_ZOMBIE,30),
  SPIDER        (EntityType.SPIDER,30),
  CAVE_SPIDER   (EntityType.CAVE_SPIDER,20),
  BLAZE         (EntityType.BLAZE,40),
  WOLF          (EntityType.WOLF,30),
  SILVERFISH    (EntityType.SILVERFISH,10),
  ENDERMAN      (EntityType.ENDERMAN,40),
  GHAST         (EntityType.GHAST,30),
  GIANT         (EntityType.GIANT,100),
  SLIME         (EntityType.SLIME,30),
  CHICKEN       (EntityType.CHICKEN,15),
  COW           (EntityType.COW,30),
  SQUID         (EntityType.SQUID,20),
  SHEEP         (EntityType.SHEEP,20),
  PIG           (EntityType.PIG,25),
  
  // Special creatures
  POWEREDCREEPER(EntityType.CREEPER,12);

  private EntityType type;
  private int hps;
  
  private CatCreature(EntityType type, int hps) {
    this.type = type;  
    this.hps = hps;
  }
  
  public EntityType getType() {
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
  
  public static CatCreature getType(EntityType c) {
    return getType(c.toString());
  }
  
  public static CatCreature getType(String string) {
    return CatUtils.getEnumFromString(CatCreature.class, string.replaceAll("[-\\.]", ""));
  }

  
}

