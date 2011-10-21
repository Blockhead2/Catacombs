package net.steeleyes.maps;

import java.io.File;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.util.config.Configuration;
import org.bukkit.Material;

/**
 *
 * @author John Keay
 */
public class AltConfigBase {
  protected static Configuration cnf;
  protected static final String def_style = "catacombs";
  protected String style                  = "catacombs";
  protected final Random rnd = new Random();
  protected final String filename;
  
  private static final Map<String,Object> defaults = new HashMap<String,Object>(){
    {
      put(def_style+".CorridorPct", 30);

      // Default corridor width = 1
      put(def_style+".Corridor.Width2Pct", 40);
      put(def_style+".Corridor.Width3Pct", 10);

      put(def_style+".Corridor.Max", 9);
      put(def_style+".Corridor.Min", 3);

      put(def_style+".Room.Min", 3);
      put(def_style+".Room.Max", 10);

      put(def_style+".Room.Clutter.BenchPct", 3);
      put(def_style+".Room.Clutter.OvenPct", 2);
      put(def_style+".Room.Clutter.ShroomPct", 10);

      put(def_style+".Room.Clutter.ChestPct", 35);
      put(def_style+".Room.Clutter.SpawnerPct", 50);
      put(def_style+".Room.Clutter.Pool.PoolPct", 15);
      put(def_style+".Room.Clutter.Pool.FullPoolPct", 40);
      put(def_style+".Room.Clutter.Pool.LavaPct", 30);
      put(def_style+".Room.Clutter.SandPct", 10);

      // Default is archway
      put(def_style+".Archway.Type.HiddenPct", 10);
      put(def_style+".Archway.Type.DoorPct", 20);
      put(def_style+".Archway.Type.DoorWebPct", 10);
      put(def_style+".Archway.DoubleWidthPct", 60);
    }
  };
 
  public AltConfigBase(String file) {
    this.filename = file;
    cnf = new Configuration(new File("plugins" + File.separator + "Catacombs",filename));
    cnf.load();
    setDefaults(defaults);
    cnf.save();
  }
  
  public AltConfigBase(Configuration config) {
    this.filename = "config.yml";        
    cnf = config;
    cnf.load();
    setDefaults(defaults);
    cnf.save();
  }  
  
  protected final void setDefaults(Map<String,Object> defs) {
    for(String key: defs.keySet()) {
      if (cnf.getProperty(key) == null) {
        Object val = defs.get(key);
        System.out.println("Setting "+key+" = "+val);
        cnf.setProperty(key,val);
      }
    }    
  }

  public String getStyle() {
    return style;
  }

  public void setStyle(String style) {
    this.style = style;
  }
  
  public Boolean checkLoot(String s) {
    Boolean ok = true;
    for(String loot : getStringList(s)) {
      String tmp[] = loot.split(":");
      Material m = Material.matchMaterial(tmp[0]);
      if(m!=null) {
        //System.out.print("[Catacombs] "+m+" ("+tmp[1]+"%) ("+tmp[2]+")"); 
      } else {
        System.err.print("[Catacombs] Unknown material "+tmp[0]+" in "+filename);
        ok = false;
      }
    } 
    return ok;
  }
  
  public Boolean Chance(int i) {
    return rnd.nextInt(100)+1 <= i;
  }
  public Boolean SandChance()     { return Chance(getInt("Room.Clutter.SandPct")); }
  public Boolean ChestChance()    { return Chance(getInt("Room.Clutter.ChestPct")); }
  public Boolean SpawnerChance()  { return Chance(getInt("Room.Clutter.SpawnerPct")); }
  public Boolean PoolChance()     { return Chance(getInt("Room.Clutter.Pool.PoolPct")); }
  public Boolean FullPoolChance() { return Chance(getInt("Room.Clutter.Pool.FullPoolPct")); }
  public Boolean CorridorChance() { return Chance(getInt("CorridorPct")); }
  public Boolean ShroomChance()   { return Chance(getInt("Room.Clutter.ShroomPct")); }
  public Boolean BenchChance()    { return Chance(getInt("Room.Clutter.BenchPct")); }
  public Boolean OvenChance()     { return Chance(getInt("Room.Clutter.OvenPct")); }
  public Boolean DoubleDoorPct()  { return Chance(getInt("Archway.DoubleWidthPct")); }

  public int CorridorType() {
    int r = rnd.nextInt(100)+1;
    if(r<=getInt("Corridor.Width3Pct"))
      return 3;
    if(r<=getInt("Corridor.Width3Pct")+getInt("Corridor.Width2Pct"))
      return 2;
    return 1;
  }

  public Square DoorType() {
    int r = rnd.nextInt(100)+1;
    if(r<=getInt("Archway.Type.HiddenPct"))
      return Square.HIDDEN;
    if(r<=getInt("Archway.Type.HiddenPct")+getInt("Archway.Type.DoorPct"))
      return Square.DOOR;
    if(r<=getInt("Archway.Type.HiddenPct")+getInt("Archway.Type.DoorPct")+
            getInt("Archway.Type.DoorWebPct"))
      return Square.WEB;
    return Square.ARCH;
  }

  public Square PoolType() {
    int r = rnd.nextInt(100)+1;
    if(r<=getInt("Room.Clutter.Pool.LavaPct"))
      return Square.LAVA;
    return Square.WATER;
  }

  public int CorridorSize() {
    return rnd.nextInt(getInt("Corridor.Max")-getInt("Corridor.Min")+1)+getInt("Corridor.Min");
  }

  public int RoomSize() {
    return rnd.nextInt(getInt("Room.Max")-getInt("Room.Min")+1)+getInt("Room.Min");
  }

  public int nextInt(int i) {
    return rnd.nextInt(i);
  }

  public Boolean exists(String path) {
    Object property;
    property = cnf.getProperty(style + "." + path);
    if (property == null) {
      property = cnf.getProperty(def_style + "." + path);
    }
    if (property == null) {
      property = cnf.getProperty(path);
    }
    if(property == null) {
      return false;
    }
    return true;    
  }
  
  protected Object getP(String path) {
    Object property;
    property = cnf.getProperty(style + "." + path);
    if (property == null) {
      property = cnf.getProperty(def_style + "." + path);
    }
    if (property == null) {
      property = cnf.getProperty(path);
    }
    if(property == null) {
      System.err.println("[Catacombs] No configuration attribute called "+path);
    }
    return property;    
  }
  protected void setP(String path,Object val) {
    if(cnf.getProperty(style + "." + path)!=null) {
      cnf.setProperty(style + "." + path,(Object)val);
    }
    if(cnf.getProperty(def_style + "." + path)!=null) {
      cnf.setProperty(def_style + "." + path,(Object)val);
    }    
    if(cnf.getProperty(path)!=null) {
      cnf.setProperty(path,(Object)val);
    }       
  }  
  @SuppressWarnings("unchecked")
  public Boolean getBoolean(String path) {
    return (Boolean) getP(path);
  }
  @SuppressWarnings("unchecked")
  public Integer getInt(String path) {
    return (Integer) getP(path);
  }
  @SuppressWarnings("unchecked")
  public Double getDouble(String path) {
    return (Double) getP(path);
  }
  @SuppressWarnings("unchecked")
  public String getString(String path) {
    return (String) getP(path);
  }
  @SuppressWarnings("unchecked")
  public List<Boolean> getBooleanList(String path) {
    return (List<Boolean>) getP(path);
  }
  @SuppressWarnings("unchecked")
  public List<Integer> getIntList(String path) {
    return (List<Integer>) getP(path);
  }
  @SuppressWarnings("unchecked")
  public List<Double> getDoubleList(String path) {
    return (List<Double>) getP(path);
  }
  @SuppressWarnings("unchecked")
  public List<String> getStringList(String path) {
    return (List<String>) getP(path);
  }    
  public void setInt(String path, Integer val) {
    setP(path,(Object) val);
  }
}
