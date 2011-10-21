package net.steeleyes.catacombs;

import net.steeleyes.maps.AltConfigBase;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.util.config.Configuration;
import org.bukkit.Material;

/**
 *
 * @author John Keay
 */
public class AltConfig extends AltConfigBase {
    
  private static final Map<String,Object> defaults = new HashMap<String,Object>(){
    {
      put("Admin.emptyChestonDelete", true);
      put("Admin.MessyCreepers", false);
      put("Admin.MessyEndermen", false);
      put("Admin.SecretDoorOff", false);
      put("Admin.SecretDoorOnlyInDungeon", false);
      put("Admin.GoldOff", false);
      put("Admin.CalmSpawns", false);
      put("Admin.ProtectSpawners", false);
      put("Admin.DungeonProtectOff", false);
      put("Admin.SaveDungeons", true);
      put("Admin.Economy", "any");
      put("Admin.BannedCommands", Arrays.asList("/spawn", "/kill", "/warp", "/setwarp","/home"));

      put(def_style+".RadiusMax", 12);

      put(def_style+".Depth.floor", 3);
      put(def_style+".Depth.room", 3);
      put(def_style+".Depth.roof", 1);
      put(def_style+".Depth.firstLevel", 2);

      put(def_style+".Block.MossyPct", 2);
      put(def_style+".Block.AirWebPct", 1);

      // Default mob is Zombie
      put(def_style+".Mob.Type.SpiderPct", 5);
      put(def_style+".Mob.Type.SkeletonPct", 25);
      put(def_style+".Mob.Type.WolfPct", 10);
      put(def_style+".Mob.Type.PigmanPct",10);
      put(def_style+".Mob.Type.CaveSpiderPct",10);

      put(def_style+".Mob.Gold.Min", 0);
      put(def_style+".Mob.Gold.Max", 10);
      
      put(def_style+".Loot.Small.List", Arrays.asList(
        "leather:10:1-6",
        "torch:50:1-30",
        "ink_sack:10:1-5",
        "wheat:10:1-5",
        "gold_ingot:10:1-5",
        "redstone:5:1-4",
        "glowstone_dust:15:1-6",
        "slime_ball:7:1",
        "iron_ingot:20:1-4",
        "arrow:10:1-25",
        "sulphur:10:1-5",
        "pumpkin:5:1",
        "flint:10:1-6",
        "gold_record:2:1",
        "green_record:2:1",
        "saddle:2:1",
        "diamond:1:1",
        "mossy_cobblestone:5:1-12",
        "obsidian:2:1-8",
        "golden_apple:2:1",
        "cookie:4:8",
        "bread:3:4",
        "apple:3:4",
        "cooked_fish:3:4",
        "cooked_beef:3:4",
        "cooked_chicken:3:4",
        "grilled_pork:3:4",
        "melon_seeds:2:1",
        "pumpkin_seeds:2:1",
        "bowl:7:1",
        "seeds:4:1-6",
        "book:7:1-4",
        "paper:7:1-4",
        "compass:5:1",
        "watch:5:1",
        "painting:5:1"   
      ));

      put(def_style+".Loot.Medium.List", new ArrayList<String>());
      
      put(def_style+".Loot.Big.List", Arrays.asList(
        "diamond:100:1-3",
        "golden_apple:30:1" 
      ));
      
      put(def_style+".Loot.Small.LeatherEquipPct", 10);
      put(def_style+".Loot.Medium.EquipPct", 100);
      put(def_style+".Loot.Big.EquipPct", 100);
      
      //put("MySQL.Enabled", false);
      //put("MySQL.Server.Address", "localhost");
      //put("MySQL.Server.Port", 3306);
      //put("MySQL.Database.Name", "Minecraft");
      //put("MySQL.Database.User.Name", "minecraft_user");
      //put("MySQL.Database.User.Password", "password");
      //put("MySQL.Database.Prefix", "cat_");
   
    }
  };
 
  public AltConfig(String filename) {
    super(filename);
    setDefaults(defaults);
    cnf.save();
        
    checkLoot("Loot.Small.List");
    checkLoot("Loot.Medium.List");
    checkLoot("Loot.Big.List");
  }
  
  public AltConfig(Configuration config) {
    super(config);
    setDefaults(defaults);
    cnf.save();
        
    checkLoot("Loot.Small.List");
    checkLoot("Loot.Medium.List");
    checkLoot("Loot.Big.List");
  } 
  public String SpawnerType() {
    int r = rnd.nextInt(100)+1;

    if(r<=getInt("Mob.Type.SpiderPct"))
      return "Spider";
    if(r<=getInt("Mob.Type.SpiderPct")+getInt("Mob.Type.SkeletonPct"))
      return "Skeleton";
    if(r<=getInt("Mob.Type.SpiderPct")+getInt("Mob.Type.SkeletonPct")+
            getInt("Mob.Type.WolfPct"))
      return "Wolf";
    if(r<=getInt("Mob.Type.SpiderPct")+getInt("Mob.Type.SkeletonPct")+
            getInt("Mob.Type.WolfPct")+getInt("Mob.Type.PigmanPct"))
      return "PigZombie";
    if(r<=getInt("Mob.Type.SpiderPct")+getInt("Mob.Type.SkeletonPct")+
            getInt("Mob.Type.WolfPct")+getInt("Mob.Type.PigmanPct")+
            getInt("Mob.Type.CaveSpiderPct"))
      return "CaveSpider";
    return "Zombie";
  }
  public int Gold() {
    return rnd.nextInt(getInt("GoldMax")-getInt("GoldMin")+1)+getInt("GoldMin");
  }
  
  public Material ShroomType() {
    if(Chance(50))
      return Material.RED_MUSHROOM;
    return Material.BROWN_MUSHROOM;
  }

  public Material CobbleType() {
    if(Chance(getInt("Block.MossyPct")))
      return Material.MOSSY_COBBLESTONE;
    return Material.COBBLESTONE;
  }

  public Material AirType() {
    if(Chance(getInt("Block.AirWebPct")))
      return Material.WEB;
    return Material.AIR;
  }
}
