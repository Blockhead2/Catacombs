package net.steeleyes.maps;

import org.bukkit.util.config.Configuration;

/**
 *
 * @author John Keay
 */
public class LevelConfig extends LevelBase {
  public int ChestPct;
  public int SpawnerPct;
  public int PoolPct;
  public int FullPoolPct;
  public int LavaPct;
  public int HiddenPct;
  public int DoorPct;
  public int DoorWebPct;
  public int DoubleDoorPct;
  public int CorridorPct;
  public int CorridorW2Pct;
  public int CorridorW3Pct;
  public int CorridorMax;
  public int CorridorMin;
  public int RoomMin;
  public int RoomMax;
  public int BenchPct;
  public int OvenPct;
  public int ShroomPct;
  public int SandPct;


  public LevelConfig(Configuration config,String style) {
    super(config,style);

    CorridorPct  = getInt(style+".CorridorPct", 30);

    // Default corridor width = 1
    CorridorW2Pct= getInt(style+".Corridor.Width2Pct", 40);
    CorridorW3Pct= getInt(style+".Corridor.Width3Pct", 10);

    CorridorMax  = getInt(style+".Corridor.Max", 9);
    CorridorMin  = getInt(style+".Corridor.Min", 3);

    RoomMin      = getInt(style+".Room.Min", 3);
    RoomMax      = getInt(style+".Room.Max", 10);

    BenchPct     = getInt(style+".Room.Clutter.BenchPct", 3);
    OvenPct      = getInt(style+".Room.Clutter.OvenPct", 2);
    ShroomPct    = getInt(style+".Room.Clutter.ShroomPct", 10);

    ChestPct     = getInt(style+".Room.Clutter.ChestPct", 35);
    SpawnerPct   = getInt(style+".Room.Clutter.SpawnerPct", 50);
    PoolPct      = getInt(style+".Room.Clutter.Pool.PoolPct", 15);
    FullPoolPct  = getInt(style+".Room.Clutter.Pool.FullPoolPct", 40);
    LavaPct      = getInt(style+".Room.Clutter.Pool.LavaPct", 30);
    SandPct      = getInt(style+".Room.Clutter.SandPct", 10);

    // Default is archway
    HiddenPct    = getInt(style+".Archway.Type.HiddenPct", 10);
    DoorPct      = getInt(style+".Archway.Type.DoorPct", 20);
    DoorWebPct   = getInt(style+".Archway.Type.DoorWebPct", 10);

    DoubleDoorPct= getInt(style+".Archway.DoubleWidthPct", 60);

    //config.save(); //Save the config!  // Leave this to levels above.
  }

  public Boolean SandChance()     { return Chance(SandPct); }
  public Boolean ChestChance()    { return Chance(ChestPct); }
  public Boolean SpawnerChance()  { return Chance(SpawnerPct); }
  public Boolean PoolChance()     { return Chance(PoolPct); }
  public Boolean FullPoolChance() { return Chance(FullPoolPct); }
  public Boolean CorridorChance() { return Chance(CorridorPct); }
  public Boolean ShroomChance()   { return Chance(ShroomPct); }
  public Boolean BenchChance()    { return Chance(BenchPct); }
  public Boolean OvenChance()     { return Chance(OvenPct); }
  public Boolean DoubleDoorPct()  { return Chance(DoubleDoorPct); }

  public int CorridorType() {
    int r = rnd.nextInt(100)+1;
    if(r<=CorridorW3Pct)
      return 3;
    if(r<=CorridorW3Pct+CorridorW2Pct)
      return 2;
    return 1;
  }

  public Square DoorType() {
    int r = rnd.nextInt(100)+1;
    if(r<=HiddenPct)
      return Square.HIDDEN;
    if(r<=HiddenPct+DoorPct)
      return Square.DOOR;
    if(r<=HiddenPct+DoorPct+DoorWebPct)
      return Square.WEB;
    return Square.ARCH;
  }

  public Square PoolType() {
    int r = rnd.nextInt(100)+1;
    if(r<=LavaPct)
      return Square.LAVA;
    return Square.WATER;
  }

  public int CorridorSize() {
    return rnd.nextInt(CorridorMax-CorridorMin+1)+CorridorMin;
  }

  public int RoomSize() {
    return rnd.nextInt(RoomMax-RoomMin+1)+RoomMin;
  }

}
