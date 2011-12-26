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
import org.bukkit.Material;
import org.bukkit.block.Block;
import net.steeleyes.maps.IConfig;

public interface ICatConfig extends IConfig { 
  public  Integer RadiusMax();
  public  Integer floorDepth();
  public  Integer roomDepth();
  public  Integer roofDepth();
  public  Integer extraDepth();

  public  String  HutType();
  public  Boolean UnderFill();
  public  Boolean OverFill();
  public  Boolean ResetButton();
  public  String  majorBlock();
  public  String  minorBlock();
  public  Boolean emptyChest();
  public  Boolean MessyCreepers();
  public  Boolean MessyEndermen();
  public  Boolean ProtectSpawners();
  public  Boolean SecretDoorOff();
  public  Boolean SecretDoorOnlyInDungeon();
  public  Boolean GoldOff();
  public  Boolean CalmSpawns();
  public  Boolean DungeonProtectOff();
  public  Boolean SaveDungeons();
  public  Boolean AdvancedCombat();
  public  Integer GroupRadius();
  public  Integer GroupDepth();
  public  Double  GroupHpFactor();
  public  Integer SpawnRadius();
  public  Integer SpawnDepth();
  public  Integer MonsterRadius();
  public  Integer MonsterMax();
  public  Integer DeathGearCost();
  public  Boolean DeathKeepGear();
  public  Double  DeathExpKept();
  //public  Boolean BossEnabled();

  public  String       Economy();
  public  List<String> BannedCommands();
  public  String       SpawnerType();
  public  Integer      Gold();
  public  Material     ShroomType();
  public  Material     AirType();
  public  Boolean      isNatural(Block b);
  public  Boolean      isBreakable(Block b);
  
  public  List<String> TrapList();
  public  List<String> LootSmallList();
  public  List<String> LootMediumList();
  public  List<String> LootBigList();

  public  Boolean SmallEquipChance();
  public  Boolean MedEquipChance();
  public  Boolean BigEquipChance();
  public  Boolean MinorChance();

  public  CatMat majorMat();
  public  CatMat minorMat(); 

  
//  public  Boolean MySQLEnabled();
//  public  String MySQLPrefix();
//  public  String MySQLdbName();
//  public  String MySQLuserName();
//  public  String MySQLuserPass();
//  public  String MySQLAddr();
//  public  int    MySQLPort();
  
  public  void setRadiusMax(int r);
}
