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

import net.steeleyes.maps.Config;
import java.util.List;
import org.bukkit.util.config.Configuration;
//import org.bukkit.configuration.Configuration;
import org.bukkit.block.Block;
import org.bukkit.Material;

public class CatConfig extends Config implements ICatConfig {

  public  Integer RadiusMax()              { return getSInt(ECatConfig.RadiusMax.getStr());  }
  public  String  HutType()                { return getSString(ECatConfig.HutType.getStr());  }
  public  String  majorBlock()             { return getSString(ECatConfig.majorBlock.getStr());  }
  public  String  minorBlock()             { return getSString(ECatConfig.minorBlock.getStr());  }
  public  Integer floorDepth()             { return getSInt(ECatConfig.floorDepth.getStr());  }
  public  Integer roomDepth()              { return getSInt(ECatConfig.roomDepth.getStr());  }
  public  Integer roofDepth()              { return getSInt(ECatConfig.roofDepth.getStr());  }
  public  Integer extraDepth()             { return getSInt(ECatConfig.extraDepth.getStr());  }
  private Integer MossyPct()               { return getSInt(ECatConfig.MossyPct.getStr());  }
  private Integer AirWebPct()              { return getSInt(ECatConfig.AirWebPct.getStr());  }
  private Integer SpiderPct()              { return getSInt(ECatConfig.SpiderPct.getStr());  }
  private Integer SkeletonPct()            { return getSInt(ECatConfig.SkeletonPct.getStr());  }
  private Integer WolfPct()                { return getSInt(ECatConfig.WolfPct.getStr());  }
  private Integer PigmanPct()              { return getSInt(ECatConfig.PigmanPct.getStr());  }
  private Integer CaveSpiderPct()          { return getSInt(ECatConfig.CaveSpiderPct.getStr());  }
  private Integer GoldMin()                { return getSInt(ECatConfig.GoldMin.getStr());  }
  private Integer GoldMax()                { return getSInt(ECatConfig.GoldMax.getStr());  }
  private Integer LeatherEquipPct()        { return getSInt(ECatConfig.LeatherEquipPct.getStr());  }
  private Integer MedEquipPct()            { return getSInt(ECatConfig.MedEquipPct.getStr());  }
  private Integer BigEquipPct()            { return getSInt(ECatConfig.BigEquipPct.getStr());  }
  public  List<String> LootSmallList()     { return getSStringList(ECatConfig.LootSmallList.getStr());  }
  public  List<String> LootMediumList()    { return getSStringList(ECatConfig.LootMediumList.getStr());  }
  public  List<String> LootBigList()       { return getSStringList(ECatConfig.LootBigList.getStr());  }
 
  public  Boolean emptyChest()             { return getSBoolean(ECatConfig.emptyChest.getStr());  }
  public  Boolean MessyCreepers()          { return getSBoolean(ECatConfig.MessyCreepers.getStr());  }
  public  Boolean MessyEndermen()          { return getSBoolean(ECatConfig.MessyEndermen.getStr());  }
  public  Boolean ProtectSpawners()        { return getSBoolean(ECatConfig.ProtectSpawners.getStr());  }
  public  Boolean SecretDoorOff()          { return getSBoolean(ECatConfig.SecretDoorOff.getStr());  }
  public  Boolean SecretDoorOnlyInDungeon(){ return getSBoolean(ECatConfig.SecretDoorOnlyInDungeon.getStr());  }
  public  Boolean GoldOff()                { return getSBoolean(ECatConfig.GoldOff.getStr());  }
  public  Boolean CalmSpawns()             { return getSBoolean(ECatConfig.CalmSpawns.getStr());  }
  public  Boolean DungeonProtectOff()      { return getSBoolean(ECatConfig.DungeonProtectOff.getStr());  }
  public  Boolean SaveDungeons()           { return getSBoolean(ECatConfig.SaveDungeons.getStr());  }
  public  String  Economy()                { return getSString(ECatConfig.Economy.getStr());  }
  public  List<String> BannedCommands()    { return getSStringList(ECatConfig.BannedCommands.getStr());  }
  private List<String> NaturalBlocks()     { return getSStringList(ECatConfig.NaturalBlocks.getStr());  }
  
  public  Boolean LeatherEquipChance()     { return Chance(LeatherEquipPct()); }
  public  Boolean MedEquipChance()         { return Chance(MedEquipPct()); }
  public  Boolean BigEquipChance()         { return Chance(BigEquipPct()); }
  public  Boolean MinorChance()            { return Chance(MossyPct()); }

  public  CatMat majorMat()                { return getBlockMaterial(majorBlock());  } 
  public  CatMat minorMat()                { return getBlockMaterial(minorBlock());  } 
  public  byte majorByte()                 { return getBlockByte(majorBlock());  } 
  public  byte minorByte()                 { return getBlockByte(minorBlock());  } 
  
  public  void setRadiusMax(int r)         { setSInt(ECatConfig.RadiusMax.getStr(),r); }

  
  // Legacy support
  public Boolean MySQLEnabled() {
    return exists("MySQL.Enabled") && getSBoolean("MySQL.Enabled");
  }
  public String MySQLPrefix()     { return getSString("MySQL.Database.Prefix"); }
  public String MySQLdbName()     { return getSString("MySQL.Database.Name"); }
  public String MySQLuserName()   { return getSString("MySQL.Database.User.Name"); }
  public String MySQLuserPass()   { return getSString("MySQL.Database.User.Password"); }
  public String MySQLAddr()       { return getSString("MySQL.Server.Address"); }
  public int    MySQLPort()       { return getSInt("MySQL.Server.Port"); }  
  
  public CatConfig(Configuration config) {
    super(config);
    setDefaults();
    cnf.save();
  }
  
  public void checkConfig() {
    checkLoot(LootSmallList());
    checkLoot(LootMediumList());
    checkLoot(LootBigList());
    checkBlockMaterial(majorBlock());
    checkBlockMaterial(minorBlock());    
    checkBlockMaterialList(NaturalBlocks());    
  }
    
  public Boolean checkBlockMaterialList(List<String> list) {
    Boolean ok = true;
    for(String name:list) {
      if(!checkBlockMaterial(name))
        ok = false;
    }
    return ok;
  }
  
  public Boolean checkBlockMaterial(String name) {
    CatMat mat = getBlockMaterial(name);
    if(mat==null) {
      System.err.println("[Catacombs] Unknown material '"+name+"' must be a number or a valid bukkit block material name or name:code or number:code");
      return false;
    }
    return true;
  }
  
  public CatMat getBlockMaterial(String name) {
    CatMat m = null;
    byte code = -1;
    if(name.contains(":")) {
      String tmp[] = name.split(":");
      name = tmp[0];
      try {
        code = Byte.parseByte(tmp[1]);
      } catch(Exception e) {
      }
    }
    Material mat = Material.matchMaterial(name);
    if(mat == null || !mat.isBlock())
      return null;
    if(code>=0)
      return new CatMat(mat,code);
    return new CatMat(mat);
  }
  
  public Boolean isNatural(Block blk) {
    String mname = blk.getType().toString().toLowerCase();
    for(String n: NaturalBlocks()) {
      if(n.equalsIgnoreCase(mname))
        return true;
    }
    return false;
  }
  
  private void setDefaults() {
    for(ECatConfig att : ECatConfig.values()) {
      String path = att.getStr();
      if(path.substring(0,1).equals("."))
        path = def_style+path;
      
      if(cnf.getProperty(path)==null) {
        Object val = att.getDef();
        //System.out.println("Setting "+path+" = "+val);
        cnf.setProperty(path,val);
      }
    }  
  }  
  
  public String SpawnerType() {
    int r = rnd.nextInt(100)+1;

    if(r<=SpiderPct())
      return "Spider";
    if(r<=SpiderPct()+SkeletonPct())
      return "Skeleton";
    if(r<=SpiderPct()+SkeletonPct()+
            WolfPct())
      return "Wolf";
    if(r<=SpiderPct()+SkeletonPct()+
            WolfPct()+PigmanPct())
      return "PigZombie";
    if(r<=SpiderPct()+SkeletonPct()+
            WolfPct()+PigmanPct()+
            CaveSpiderPct())
      return "CaveSpider";
    return "Zombie";
  }
  public Integer Gold() {
    return rnd.nextInt(GoldMax()-GoldMin()+1)+GoldMin();
  }
  
  public Material ShroomType() {
    if(Chance(50))
      return Material.RED_MUSHROOM;
    return Material.BROWN_MUSHROOM;
  }

  @Deprecated
  public CatMat CobbleType() {
    if(Chance(MossyPct()))
      return minorMat();
    return majorMat();
  }

  public Material AirType() {
    if(Chance(AirWebPct()))
      return Material.WEB;
    return Material.AIR;
  }
}
