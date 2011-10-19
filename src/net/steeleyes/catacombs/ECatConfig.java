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
import java.util.Arrays;

public enum ECatConfig {
  
    emptyChest              ("Admin.emptyChestonDelete", true),
    MessyCreepers           ("Admin.MessyCreepers", false),
    MessyEndermen           ("Admin.MessyEndermen", false),
    SecretDoorOff           ("Admin.SecretDoorOff", false),
    SecretDoorOnlyInDungeon ("Admin.SecretDoorOnlyInDungeon", false),
    GoldOff                 ("Admin.GoldOff", false),
    CalmSpawns              ("Admin.CalmSpawns", false),
    ProtectSpawners         ("Admin.ProtectSpawners", true),
    DungeonProtectOff       ("Admin.DungeonProtectOff", false),
    SaveDungeons            ("Admin.SaveDungeons", true),
    Economy                 ("Admin.Economy", "any"),
    BannedCommands          ("Admin.BannedCommands", Arrays.asList(
      "/spawn",
      "/kill",
      "/warp",
      "/setwarp",
      "/home"
    )),
    
    
    RadiusMax    (".RadiusMax", 12),
    UnderFill    (".UnderFill", false),
    OverFill     (".OverFill", false),
    HutType      (".Hut.Type", "default"),
    majorBlock   (".Block.Major", "cobblestone"),
    minorBlock   (".Block.Minor", "mossy_cobblestone"),
    floorDepth   (".Depth.floor", 3),
    roomDepth    (".Depth.room", 3),
    roofDepth    (".Depth.roof", 1),
    extraDepth   (".Depth.firstLevel", 2),
    MossyPct     (".Block.MossyPct", 2),
    AirWebPct    (".Block.AirWebPct", 1),
    NaturalBlocks(".Block.Natural", Arrays.asList(  // Most common blocks first for efficiency
      "stone",
      "dirt",
      "sand",
      "sandstone",
      "gravel",
      "coal_ore",
      "iron_ore",
      "redstone_ore",
      "gold_ore",
      "diamond_ore",
      "lapis_ore"
    )),    
    TrapList(".Trap.Ammo", Arrays.asList(  // Most common blocks first for efficiency
      "arrow:100:10"
    )),    
    
    SpiderPct    (".Mob.Type.SpiderPct", 5),
    SkeletonPct  (".Mob.Type.SkeletonPct", 25),
    WolfPct      (".Mob.Type.WolfPct", 10),
    PigmanPct    (".Mob.Type.PigmanPct",10),
    CaveSpiderPct(".Mob.Type.CaveSpiderPct",10),
    GoldMin      (".Mob.Gold.Min", 0),
    GoldMax      (".Mob.Gold.Max", 10),
    
    SmallEquipPct    (".Loot.Small.LeatherEquipPct", 10),
    MedEquipPct      (".Loot.Medium.EquipPct", 100),
    BigEquipPct      (".Loot.Big.EquipPct", 100),
    
    LootSmallList    (".Loot.Small.List", Arrays.asList(
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
    )),
 
    LootMediumList   (".Loot.Medium.List",new ArrayList<String>(
    )),
    
    LootBigList      (".Loot.Big.List", Arrays.asList(
      "diamond:100:1-3",
      "golden_apple:30:1" 
    ));


    //MySQLEnabled     ("MySQL.Enabled", false),
    //MySQLAddr        ("MySQL.Server.Address", "localhost"),
    //MySQLPort        ("MySQL.Server.Port", 3306),
    //MySQLdbName      ("MySQL.Database.Name", "Minecraft"),
    //MySQLuserName    ("MySQL.Database.User.Name", "minecraft_user"),
    //MySQLuserPass    ("MySQL.Database.User.Password", "password"),
    //MySQLdbPrefix    ("MySQL.Database.Prefix", "cat_"),

  private String str;
  private Object def;
  
  private ECatConfig(String str, Object def) {
    this.str = str;
    this.def = def;
  }
  
  public String getStr() {
    return str;
  }
  
  public Object getDef() {
    return def;
  }
}
