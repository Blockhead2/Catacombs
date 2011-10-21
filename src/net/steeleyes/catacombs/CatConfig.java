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
import java.util.ArrayList;
import org.bukkit.util.config.Configuration;
//import org.bukkit.configuration.Configuration;
import org.bukkit.block.Block;
import org.bukkit.Material;

public class CatConfig extends Config implements ICatConfig {
  
  private transient List<CatMat> NaturalList = null;
  private transient List<CatMat> BreakList = null;

  public  Integer RadiusMax()              { return getSInt(ECatConfig.RadiusMax.getStr());  }
  public  String  HutType()                { return getSString(ECatConfig.HutType.getStr());  }
  public  Boolean UnderFill()              { return getSBoolean(ECatConfig.UnderFill.getStr());  }
  public  Boolean OverFill()               { return getSBoolean(ECatConfig.OverFill.getStr());  }
  public  Boolean ResetButton()            { return getSBoolean(ECatConfig.ResetButton.getStr());  }
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
  private Integer SmallEquipPct()          { return getSInt(ECatConfig.SmallEquipPct.getStr());  }
  private Integer MedEquipPct()            { return getSInt(ECatConfig.MedEquipPct.getStr());  }
  private Integer BigEquipPct()            { return getSInt(ECatConfig.BigEquipPct.getStr());  }
  public  List<String> TrapList()          { return getSStringList(ECatConfig.TrapList.getStr());  }
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
  private List<String> BreakBlocks()       { return getSStringList(ECatConfig.BreakBlocks.getStr());  }
  
  public  Boolean SmallEquipChance()       { return Chance(SmallEquipPct()); }
  public  Boolean MedEquipChance()         { return Chance(MedEquipPct()); }
  public  Boolean BigEquipChance()         { return Chance(BigEquipPct()); }
  public  Boolean MinorChance()            { return Chance(MossyPct()); }

  public  CatMat majorMat()                { return CatMat.parseMaterial(majorBlock());  } 
  public  CatMat minorMat()                { return CatMat.parseMaterial(minorBlock());  } 
  
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
    checkConfig();
  }
  
  private void checkConfig() {
    checkLoot(TrapList());
    checkLoot(LootSmallList());
    checkLoot(LootMediumList());
    checkLoot(LootBigList());
    CatMat.parseMaterial(majorBlock());
    CatMat.parseMaterial(minorBlock());
    NaturalList = cacheBlockMaterialList(NaturalBlocks());    
    BreakList = cacheBlockMaterialList(BreakBlocks());    
  }

  @Override
  public void setStyle(String style) {  // Recache material lists if style changes
    super.setStyle(style);
    NaturalList = cacheBlockMaterialList(NaturalBlocks());    
    BreakList = cacheBlockMaterialList(BreakBlocks());    
  }
  
  private List<CatMat> cacheBlockMaterialList(List<String> in) {
    List<CatMat> out = new ArrayList<CatMat>();
    for(String name:in) {
      CatMat item = CatMat.parseMaterial(name);
      if(item != null)
        out.add(item);        
    }
    return out;
  }

  public Boolean isNatural(Block blk) {
    CatMat mat = new CatMat(blk);
    for(CatMat i: NaturalList) {
      if(mat.equals(i))
        return true;
    }
    return false;
  }
  
  public Boolean isBreakable(Block blk) {
    CatMat mat = new CatMat(blk);
    for(CatMat i: BreakList) {
      if(mat.equals(i))
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

  public Material AirType() {
    if(Chance(AirWebPct()))
      return Material.WEB;
    return Material.AIR;
  }
}
