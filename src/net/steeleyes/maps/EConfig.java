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
package net.steeleyes.maps;

public enum EConfig {
  RoomMax        (".Room.Max",                      10),
  RoomMin        (".Room.Min",                       3),
  SpecialPct     (".SpecialPct",                    10),
  CorridorPct    (".CorridorPct",                   30),
  CorridorMax    (".Corridor.Max",                   9),
  CorridorMin    (".Corridor.Min",                   3),
  CorridorW2Pct  (".Corridor.Width2Pct",            40),
  CorridorW3Pct  (".Corridor.Width3Pct",            10),
  HiddenPct      (".Archway.Type.HiddenPct",        10),
  DoorPct        (".Archway.Type.DoorPct",          30),
  WebDoorPct     (".Archway.Type.WebDoorPct",       10),
  TrapPct        (".Room.Clutter.TrapPct",          10),
  SandPct        (".Room.Clutter.SandPct",          10),
  ChestPct       (".Room.Clutter.ChestPct",         35),
  SpawnerPct     (".Room.Clutter.SpawnerPct",       50),
  PoolPct        (".Room.Clutter.Pool.PoolPct",     15),
  FullPoolPct    (".Room.Clutter.Pool.FullPoolPct", 40),
  LavaPct        (".Room.Clutter.Pool.LavaPct",     30),
  ShroomPct      (".Room.Clutter.ShroomPct",        10),
  BenchPct       (".Room.Clutter.BenchPct",          3),
  AnvilPct       (".Room.Clutter.AnvilPct",          3),
  EnchantPct     (".Room.Clutter.EnchantPct",        3),
  OvenPct        (".Room.Clutter.OvenPct",           2),
  DoubleDoorPct  (".Archway.DoubleWidthPct",        60),
  MedHalfEmpty   (".Loot.Medium.HalfEmpty",      false);
  
  private String str;
  private Object def;
  
  private EConfig(String str, Object def) {
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
