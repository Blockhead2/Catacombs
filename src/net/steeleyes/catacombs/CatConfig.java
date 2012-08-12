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
import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public class CatConfig extends Config implements ICatConfig {
  
  private List<CatMat> NaturalList = new ArrayList<CatMat>();
  private List<CatMat> BreakList = new ArrayList<CatMat>();
  private List<CatMat> PlaceList = new ArrayList<CatMat>();

  public  Integer RadiusMax()              { return getSInt(ECatConfig.RadiusMax.getStr());  }
  public  String  HutType()                { return getSString(ECatConfig.HutType.getStr());  }
  public  Boolean UnderFill()              { return getSBoolean(ECatConfig.UnderFill.getStr());  }
  public  Boolean OverFill()               { return getSBoolean(ECatConfig.OverFill.getStr());  }
  public  Boolean ResetButton()            { return getSBoolean(ECatConfig.ResetButton.getStr());  }
  public  Boolean RecallButton()           { return getSBoolean(ECatConfig.RecallButton.getStr());  }
  private String  majorBlock()             { return getSString(ECatConfig.majorBlock.getStr());  }
  private String  minorBlock()             { return getSString(ECatConfig.minorBlock.getStr());  }
  private String  floorBlock()             { return getSString(ECatConfig.floorBlock.getStr());  }
  private String  roofBlock()              { return getSString(ECatConfig.roofBlock.getStr());  }
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
  private Integer BlazePct()               { return getSInt(ECatConfig.BlazePct.getStr());  }
  private Integer EndermanPct()            { return getSInt(ECatConfig.EndermanPct.getStr());  }
  private Integer SlimePct()               { return getSInt(ECatConfig.SlimePct.getStr());  }
  private Integer CreeperPct()             { return getSInt(ECatConfig.CreeperPct.getStr());  }
  private Integer IronDoorPct()            { return getSInt(ECatConfig.IronDoorPct.getStr());  }
  //private Integer SilverFishPct()          { return getSInt(ECatConfig.SilverFishPct.getStr());  }
  private Double GoldMin()                 { return getSDouble(ECatConfig.GoldMin.getStr());  }
  private Double GoldMax()                 { return getSDouble(ECatConfig.GoldMax.getStr());  }
  private Integer SmallEquipPct()          { return getSInt(ECatConfig.SmallEquipPct.getStr());  }
  private Integer MobDropReductionPct()    { return getSInt(ECatConfig.MobDropReductionPct.getStr());  }
  private Integer MedEquipPct()            { return getSInt(ECatConfig.MedEquipPct.getStr());  }
  private Integer MedSmallPct()            { return getSInt(ECatConfig.MedSmallPct.getStr());  }
  private Integer BigEquipPct()            { return getSInt(ECatConfig.BigEquipPct.getStr());  }
  private Integer BigSmallPct()            { return getSInt(ECatConfig.BigSmallPct.getStr());  }
  public  List<String> TrapList()          { return getSStringList(ECatConfig.TrapList.getStr());  }
  public  List<String> LootSmallList()     { return getSStringList(ECatConfig.LootSmallList.getStr());  }
  public  List<String> LootMediumList()    { return getSStringList(ECatConfig.LootMediumList.getStr());  }
  public  List<String> LootBigList()       { return getSStringList(ECatConfig.LootBigList.getStr());  }
 
  public  Boolean emptyChest()             { return getSBoolean(ECatConfig.emptyChest.getStr());  }
  public  Boolean NoTeleportIn()           { return getSBoolean(ECatConfig.NoTeleportIn.getStr());  }
  public  Boolean NoTeleportOut()          { return getSBoolean(ECatConfig.NoTeleportOut.getStr());  }
  public  Boolean ProtectSpawners()        { return getSBoolean(ECatConfig.ProtectSpawners.getStr());  }
  public  Boolean SecretDoorOnlyInDungeon(){ return getSBoolean(ECatConfig.SecretDoorOnlyInDungeon.getStr());  }
  public  Boolean GoldOff()                { return getSBoolean(ECatConfig.GoldOff.getStr());  }
  public  Boolean NoArmourInDungeon()      { return getSBoolean(ECatConfig.NoArmourInDungeon.getStr());  }
  public  Boolean NoPvPInDungeon()         { return getSBoolean(ECatConfig.NoPvPInDungeon.getStr());  }
  public  Boolean RespawnInHut()           { return getSBoolean(ECatConfig.RespawnInHut.getStr());  }
  public  Boolean MobsSpawnOnlyUnderground(){ return getSBoolean(ECatConfig.MobsSpawnOnlyUnderground.getStr());  }
  public  Boolean MobsSpawnOnlyInDungeons(){ return getSBoolean(ECatConfig.MobsSpawnOnlyInDungeons.getStr());  }
  //public  Boolean BossEnabled()            { return getSBoolean(ECatConfig.BossEnabled.getStr());  }
  public  Boolean AdvancedCombat()         { return false;  }
  //public  Boolean AdvancedCombat()         { return getSBoolean(ECatConfig.AdvancedCombat.getStr());  }
  public  Integer GroupRadius()            { return getSInt(ECatConfig.GroupRadius.getStr());  }
  public  Integer GroupDepth()             { return getSInt(ECatConfig.GroupDepth.getStr());  }
  public  Double  GroupHpFactor()          { return getSDouble(ECatConfig.GroupHpFactor.getStr());  }
  public  Integer SpawnRadius()            { return getSInt(ECatConfig.SpawnRadius.getStr());  }
  public  Integer SpawnDepth()             { return getSInt(ECatConfig.SpawnDepth.getStr());  }
  public  Integer MonsterRadius()          { return getSInt(ECatConfig.MonsterRadius.getStr());  }
  public  Integer MonsterMax()             { return getSInt(ECatConfig.MonsterMax.getStr());  }
  public  Integer DeathGearCost()          { return getSInt(ECatConfig.DeathGearCost.getStr());  }
  public  Boolean DeathKeepGear()          { return getSBoolean(ECatConfig.DeathKeepGear.getStr());  }
  public  Double  DeathExpKept()           { return getSDouble(ECatConfig.DeathExpKept.getStr());  }
  public  Boolean ClickIronDoor()          { return getSBoolean(ECatConfig.ClickIronDoor.getStr());  }
  public  String  Economy()                { return getSString(ECatConfig.Economy.getStr());  }
  public  List<String> BannedCommands()    { return getSStringList(ECatConfig.BannedCommands.getStr());  }
  private List<String> NaturalBlocks()     { return getSStringList(ECatConfig.NaturalBlocks.getStr());  }
  private List<String> BreakBlocks()       { return getSStringList(ECatConfig.BreakBlocks.getStr());  }
  private List<String> PlaceBlocks()       { return getSStringList(ECatConfig.PlaceBlocks.getStr());  }
  
  public  Boolean SmallEquipChance()       { return Chance(SmallEquipPct()); }
  public  Boolean MobDropReductionChance() { return Chance(MobDropReductionPct()); }
  public  Boolean MedSweepOre()            { return getSBoolean(ECatConfig.MedSweepOre.getStr()); }
  public  Boolean MedEquipChance()         { return Chance(MedEquipPct()); }
  public  Boolean MedSmallChance()         { return Chance(MedSmallPct()); }
  public  Boolean BigEquipChance()         { return Chance(BigEquipPct()); }
  public  Boolean BigSmallChance()         { return Chance(BigSmallPct()); }
  public  Boolean MinorChance()            { return Chance(MossyPct()); }

  public  CatMat majorMat()                { return CatMat.parseMaterial(majorBlock());  } 
  public  CatMat minorMat()                { return CatMat.parseMaterial(minorBlock());  } 
  public  CatMat floorMat()                { return CatMat.parseMaterial(floorBlock());  } 
  public  CatMat roofMat()                 { return CatMat.parseMaterial(roofBlock());  } 
  
  public  void setRadiusMax(int r)         { setSInt(ECatConfig.RadiusMax.getStr(),r); }

  
  // Legacy support
//  public Boolean MySQLEnabled() {
//    return exists("MySQL.Enabled") && getSBoolean("MySQL.Enabled");
//  }
//  public String MySQLPrefix()     { return getSString("MySQL.Database.Prefix"); }
//  public String MySQLdbName()     { return getSString("MySQL.Database.Name"); }
//  public String MySQLuserName()   { return getSString("MySQL.Database.User.Name"); }
//  public String MySQLuserPass()   { return getSString("MySQL.Database.User.Password"); }
//  public String MySQLAddr()       { return getSString("MySQL.Server.Address"); }
//  public int    MySQLPort()       { return getSInt("MySQL.Server.Port"); }  
  
  public CatConfig(FileConfiguration config) {
    super(config);
    try {
      setDefaults();
      if(!fcnf.contains("grand")) {
        fcnf.set("grand.Archway.DoubleWidthPct",100);
        fcnf.set("grand.Depth.room",4);
        fcnf.set("grand.Corridor.Width3Pct",30);
        fcnf.set("grand.Corridor.Width2Pct",70);
        fcnf.set("grand.Corridor.Max",10);
        fcnf.set("grand.Corridor.Min",3);
        fcnf.set("grand.Room.Max",10);
        fcnf.set("grand.Room.Min",3);
        fcnf.set("grand.Room.Clutter.ChestPct",55);
        fcnf.set("grand.Room.Clutter.SpawnerPct",70);
        fcnf.set("grand.CorridorPct",20);
        fcnf.set("grand.RadiusMax",30);       
        fcnf.set("grand.SpecialPct",35);       
        fcnf.set("grand.Hut.Type","medium");       
      }

      fcnf.save(filename);
      checkConfig();
    } catch (Exception e) {
      System.err.println("[Catacombs] "+e.getMessage());
    }
  }
  
  public Boolean noFlag(String path) { 
    if(!(fcnf.contains(path) && fcnf.getBoolean(path))) {
      try {
        fcnf.set(path,true);
        fcnf.save(filename);
      } catch (Exception e) {
        System.err.println("[Catacombs] "+e.getMessage());
      }
      return true;
    }  
    return false;
  }
  
  private void setDefaults() {
    for(ECatConfig att : ECatConfig.values()) {
      String path = att.getStr();
      if(path.substring(0,1).equals("."))
        path = def_style+path;
      if(fcnf.get(path)==null) {
        Object val = att.getDef();
        //System.out.println("[Catacombs] Setting "+path+" = "+val);
        fcnf.set(path,val);
      }
    }  
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
    PlaceList = cacheBlockMaterialList(PlaceBlocks());    
  }

  @Override
  public void setStyle(String style) {  // Recache material lists if style changes
    super.setStyle(style);
    checkConfig();
    //NaturalList = cacheBlockMaterialList(NaturalBlocks());    
    //BreakList = cacheBlockMaterialList(BreakBlocks());    
    //PlaceList = cacheBlockMaterialList(PlaceBlocks());    
  }
  
  private List<CatMat> cacheBlockMaterialList(List<String> in) {
    List<CatMat> out = new ArrayList<CatMat>();
    for(String name:in) {
      try {
      CatMat item = CatMat.parseMaterial(name);
      if(item != null)
        out.add(item);
      } catch (Exception e) {
        System.err.println("[Catacombs] "+e);
        e.printStackTrace(System.err);
      }
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
      if(mat.getMat() == i.getMat())
        return true;
    }
    return false;
  } 
  
  public Boolean isPlaceable(Block blk) {
    CatMat mat = new CatMat(blk);
    for(CatMat i: PlaceList) {
      if(mat.getMat() == i.getMat())
        return true;
    }
    return false;
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
    if(r<=SpiderPct()+SkeletonPct()+
            WolfPct()+PigmanPct()+
            CaveSpiderPct()+BlazePct())
      return "Blaze";
    if(r<=SpiderPct()+SkeletonPct()+
            WolfPct()+PigmanPct()+
            CaveSpiderPct()+BlazePct()+
            CreeperPct())
      return "Creeper";
    if(r<=SpiderPct()+SkeletonPct()+
            WolfPct()+PigmanPct()+
            CaveSpiderPct()+BlazePct()+
            CreeperPct()+EndermanPct())
      return "Enderman";
    if(r<=SpiderPct()+SkeletonPct()+
            WolfPct()+PigmanPct()+
            CaveSpiderPct()+BlazePct()+
            CreeperPct()+EndermanPct() +
            SlimePct())
      return "Slime";
    return "Zombie";
  }
  
  public Double Gold() {
    double a = 0.0;
    double max = GoldMax();
    double min = GoldMin();
    if(max-min<0.001)
      a=min;
    else
      a=(Math.random() * (max-min))+min;
    return Round(a,2);
  }
  
  private static double Round(double Rval, int Rpl) {
    double p = (double) Math.pow(10, Rpl);
    Rval = Rval * p;
    double tmp = Math.round(Rval);
    return (double) tmp / p;
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
  public Material DoorMaterial() {
    if(Chance(IronDoorPct()))
      return Material.IRON_DOOR_BLOCK;
    return Material.WOODEN_DOOR;
  }
}
