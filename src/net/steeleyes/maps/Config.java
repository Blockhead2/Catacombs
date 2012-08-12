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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public class Config implements IConfig {
  protected static FileConfiguration fcnf;
  protected static final String def_style = "catacomb";
  protected String style                  = "catacomb";
  public final Random rnd = new Random();
  protected File filename;


  private Integer RoomMax()          { return getSInt(EConfig.RoomMax.getStr());  }
  private Integer RoomMin()          { return getSInt(EConfig.RoomMin.getStr());  }
  private Integer CorridorPct()      { return getSInt(EConfig.CorridorPct.getStr());  }
  private Integer SpecialPct()       { return getSInt(EConfig.SpecialPct.getStr());  }
  private Integer CorridorMax()      { return getSInt(EConfig.CorridorMax.getStr());  }
  private Integer CorridorMin()      { return getSInt(EConfig.CorridorMin.getStr());  }
  private Integer CorridorW2Pct()    { return getSInt(EConfig.CorridorW2Pct.getStr());  }
  private Integer CorridorW3Pct()    { return getSInt(EConfig.CorridorW3Pct.getStr());  }
  private Integer HiddenPct()        { return getSInt(EConfig.HiddenPct.getStr());  }
  private Integer DoorPct()          { return getSInt(EConfig.DoorPct.getStr());  }
  private Integer WebDoorPct()       { return getSInt(EConfig.WebDoorPct.getStr());  }
  private Integer TrapPct()          { return getSInt(EConfig.TrapPct.getStr());  }
  private Integer SandPct()          { return getSInt(EConfig.SandPct.getStr());  }
  private Integer ChestPct()         { return getSInt(EConfig.ChestPct.getStr());  }
  private Integer SpawnerPct()       { return getSInt(EConfig.SpawnerPct.getStr());  }
  private Integer PoolPct()          { return getSInt(EConfig.PoolPct.getStr());  }
  private Integer FullPoolPct()      { return getSInt(EConfig.FullPoolPct.getStr());  }
  private Integer LavaPct()          { return getSInt(EConfig.LavaPct.getStr());  }
  private Integer ShroomPct()        { return getSInt(EConfig.ShroomPct.getStr());  }
  private Integer BenchPct()         { return getSInt(EConfig.BenchPct.getStr());  }
  private Integer EnchantPct()       { return getSInt(EConfig.EnchantPct.getStr());  }
  private Integer AnvilPct()         { return getSInt(EConfig.AnvilPct.getStr());  }
  private Integer OvenPct()          { return getSInt(EConfig.OvenPct.getStr());  }
  private Integer DoubleDoorPct()    { return getSInt(EConfig.DoubleDoorPct.getStr());  }

  public  Boolean MedHalfEmpty()     { return getSBoolean(EConfig.MedHalfEmpty.getStr());  }
  public  Boolean SandChance()       { return Chance(SandPct()); }
  public  Boolean ChestChance()      { return Chance(ChestPct()); }
  public  Boolean SpawnerChance()    { return Chance(SpawnerPct()); }
  public  Boolean TrapChance()       { return Chance(TrapPct()); }
  public  Boolean PoolChance()       { return Chance(PoolPct()); }
  public  Boolean FullPoolChance()   { return Chance(FullPoolPct()); }
  public  Boolean CorridorChance()   { return Chance(CorridorPct()); }
  public  Boolean SpecialChance()    { return Chance(SpecialPct()); }
  public  Boolean ShroomChance()     { return Chance(ShroomPct()); }
  public  Boolean AnvilChance()      { return Chance(AnvilPct()); }
  public  Boolean BenchChance()      { return Chance(BenchPct()); }
  public  Boolean EnchantChance()    { return Chance(EnchantPct()); }
  public  Boolean OvenChance()       { return Chance(OvenPct()); }
  public  Boolean DoubleDoorChance() { return Chance(DoubleDoorPct()); }

  public Config(FileConfiguration fconfig) {
    filename = new File("plugins" + File.separator + "Catacombs" + File.separator + "config.yml");
    fcnf = fconfig;
    try {
      if(filename.exists()) {
        fcnf.load(filename);
      } else {
        System.out.println("[Catacombs] config file doesn't exist");
      }
      setDefaults();
      //System.out.println("[Catacombs] save to "+filename);
      fcnf.save(filename);
    } catch (Exception e) {
      System.err.println("[Catacombs] "+e.getMessage());
      e.printStackTrace(System.out);
    }
  }   
  
  private void setDefaults() {
    for(EConfig att : EConfig.values()) {
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
  
  public String getStyle() {
    return style;
  }

  public void setStyle(String style) {
    this.style = style;
  }

  
  public byte getBlockByte(String name) {
    if(name.contains(":")) {
      String tmp[] = name.split(":");
      try {
        return Byte.parseByte(tmp[1]);
      } catch(Exception e) {
      }
    }
    return -1;
  }  
  
  public Boolean checkLoot(List<String> list) {
    Boolean ok = true;
    for(String loot : list) {
      try {
        String tmp[] = loot.split(":");
        String matName = tmp[0];
        short code=0;

        if(matName.contains("/")) {
          String mat[] = matName.split("/");
          matName = mat[0];
          code = Short.parseShort(mat[1]);
        }
        Material m = Material.matchMaterial(matName);
        if(m==null)
          throw new RuntimeException("Unknown material '"+matName+"' must be a number or a valid bukkit material name");

        int pct = Integer.parseInt(tmp[1]);

        String rng[] = tmp[2].split("-");
        int min = Integer.parseInt(rng[0]);
        int max = min;
        if(rng.length > 1)
          max = Integer.parseInt(rng[1]);
        
      } catch (ArrayIndexOutOfBoundsException e) {
        System.err.println("[Catacombs] Wrong format expecting this 'dirt:45:1' or 'dirt:45:1-6' found '"+loot+"'");
        ok = false;     
      } catch (NumberFormatException e) {
        System.err.println("[Catacombs] Missing number expecting this 'dirt:45:1' or 'dirt:45:1-6' found '"+loot+"'");
        ok = false;
      } catch (Exception e) {
        System.err.println("[Catacombs] "+e.getMessage());
        ok = false;
      }
    } 
    return ok;
  }
  
  public Boolean Chance(int i) {
    return rnd.nextInt(100)+1 <= i;
  }

  public int CorridorType() {
    int r = rnd.nextInt(100)+1;
    if(r<=CorridorW3Pct())
      return 3;
    if(r<=CorridorW3Pct()+CorridorW2Pct())
      return 2;
    return 1;
  }

  public Square DoorType() {
    int r = rnd.nextInt(100)+1;
    if(r<=HiddenPct())
      return Square.HIDDEN;
    if(r<=HiddenPct()+DoorPct())
      return Square.DOOR;
    if(r<=HiddenPct()+DoorPct()+WebDoorPct())
      return Square.WEB;
    return Square.ARCH;
  }

  public Square PoolType() {
    int r = rnd.nextInt(100)+1;
    if(r<=LavaPct())
      return Square.LAVA;
    return Square.WATER;
  }

  public int CorridorSize() {
    return rnd.nextInt(CorridorMax()-CorridorMin()+1)+CorridorMin();
  }

  public int RoomSize() {
    return rnd.nextInt(RoomMax()-RoomMin()+1)+RoomMin();
  }

  public int nextInt(int i) {
    return rnd.nextInt(i);
  }

  public Boolean exists(String path) {
    Object property = null;
    if(path.substring(0,1).equals(".")) {
      property = fcnf.get(style + path);
      if (property == null) {
        property = fcnf.get(def_style + path);
      }
    }
    if (property == null) {
      property = fcnf.get(path);
    }
    path = path.substring(1);
    return property != null;    
  }

  protected Object getSP(String path) {
    Object property = null;
    if(path.substring(0,1).equals(".")) {
      property = fcnf.get(style + path);
      if (property == null) {
        property = fcnf.get(def_style + path);
      }
      path = path.substring(1);
    }
    if (property == null) {
      property = fcnf.get(path);
    }
    if(property == null) {
      System.err.println("[Catacombs] No configuration attribute called "+path+" or <style>."+path);
    }
    return property;    
  }

  protected void setSP(String path,Object val) {
    if(path.substring(0,1).equals(".")) {
      if(fcnf.get(style + path)!=null) {
        fcnf.set(style + path,(Object)val);
      }
      if(fcnf.get(def_style + path)!=null) {
        fcnf.set(def_style + path,(Object)val);
      }  
      path = path.substring(1);
    }
    if(fcnf.get(path)!=null) {
      fcnf.set(path,(Object)val);
    }       
  }  
  
  protected Boolean getSBoolean(String path) {
    Object o = getSP(path);
    if(o instanceof Boolean) {
      return (Boolean) o;
    }
    System.err.println("[Catacombs] Expecting '"+path+"' to be a Boolean, found "+o);
    return false;
  }
  
  protected Integer getSInt(String path) {
    Object o = getSP(path);
    if(o instanceof Integer) {
      return (Integer) o;
    } else if(o instanceof Double) {
      return ((Double)o).intValue();
    }
    System.err.println("[Catacombs] Expecting '"+path+"' to be an Integer/Double, found "+o);
    return 0;
  }
  
  protected Double getSDouble(String path) {
    Object o = getSP(path);
    if(o instanceof Double) {
      return (Double) o;
    } else if(o instanceof Integer) {
      return ((Integer)o).doubleValue();
    }
    System.err.println("[Catacombs] Expecting '"+path+"' to be a Double/Integer, found "+o);
    return 0.0;
  }
  
  protected String getSString(String path) {
    Object o = getSP(path);
    if(o instanceof String) {
      return (String) o;
    }
    return o.toString();
  }
  
  protected List<Boolean> getSBooleanList(String path) {
    Object o = getSP(path);
    ArrayList<Boolean> res = new ArrayList<Boolean>();
    if(o instanceof List) {
      for(Object i:(List)o) { 
        if(i instanceof Boolean) {
          res.add((Boolean)i);
        } else {
          System.err.println("[Catacombs] Expecting '"+path+"' to be a Boolean List, found "+i+" "+i.getClass());
        }
      }
      return res;
    }
    System.err.println("[Catacombs] Expecting '"+path+"' to be a Boolean List, found "+o.getClass());
    return res;
  }
  
  protected List<Integer> getSIntList(String path) {
    Object o = getSP(path);
    ArrayList<Integer> res = new ArrayList<Integer>();
    if(o instanceof List) {
      for(Object i:(List)o) { 
        if(i instanceof Integer) {
          res.add((Integer)i);
        } else if(i instanceof Double) {
          res.add(((Double)i).intValue());
        } else {
          System.err.println("[Catacombs] Expecting '"+path+"' to be an Integer/Double List, found "+i+" "+i.getClass());
        }
      }
      return res;
    }
    System.err.println("[Catacombs] Expecting '"+path+"' to be an Integer/Double List, found "+o.getClass());
    return res;
  }

  protected List<Double> getSDoubleList(String path) {
    Object o = getSP(path);
    ArrayList<Double> res = new ArrayList<Double>();
    if(o instanceof List) {
      for(Object i:(List)o) { 
        if(i instanceof Double) {
          res.add((Double)i);
        } else if(i instanceof Integer) {
          res.add(((Integer)i).doubleValue());
        } else {
          System.err.println("[Catacombs] Expecting '"+path+"' to be a Double/Integer List, found "+i+" "+i.getClass());
        }
      }
      return res;
    }
    System.err.println("[Catacombs] Expecting '"+path+"' to be a Double/Integer List, found "+o.getClass());
    return res;
  }

  protected List<String> getSStringList(String path) {
    Object o = getSP(path);
    ArrayList<String> res = new ArrayList<String>();
    if(o instanceof List) {
      for(Object i:(List)o) { 
        if(i instanceof String) {
          res.add((String)i);
        } else {
          res.add(i.toString());
        }
      }
      return res;
    }
    System.err.println("[Catacombs] Expecting '"+path+"' to be a String List, found "+o.getClass());
    return res;
  }    
  
  public void setSInt(String path, Integer val) {
    setSP(path,(Object) val);
  }
}
