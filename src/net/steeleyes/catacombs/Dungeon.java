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
import org.bukkit.World;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.steeleyes.maps.Direction;
import net.steeleyes.maps.Square;
import net.steeleyes.maps.PrePlanned;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import org.bukkit.block.BlockFace;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dungeon {
  private Catacombs plugin = null;
  private World world;
  private final ArrayList<CatLevel> levels = new ArrayList<CatLevel>();
  private String name;
  private String builder;
  private CatMat major;
  private CatMat minor;
  private CatMat floorMat;
  private CatMat roofMat;
    
  private Boolean running = false;
  private int     resetWarnings = 0;
  
  private int         did=-1;
  //private List<CatFlag> flags = new ArrayList<CatFlag>();
  //private List<CatLocation> locations = new ArrayList<CatLocation>();
  private CatFlag     bossKilled = null;
  private CatFlag     isEnabled = null;
  private CatFlag     resetTime = null;
  private CatFlag     resetMin = null;
  private CatFlag     resetMax = null;
  private CatFlag     roofFlg = null;
  private CatFlag     floorFlg = null;
  private CatLocation endChest = null;
  
  private static Map<String,PrePlanned> hut_list = null;
  
  private Boolean built = false;
  
  // New dungeon
  public Dungeon(Catacombs plugin,String name,World world){
    this.name = name;
    this.plugin = plugin;
    //this.cnf = cnf;
    this.world = world;
    this.major    = plugin.cnf.majorMat();
    this.minor    = plugin.cnf.minorMat();
    this.floorMat = plugin.cnf.floorMat();
    this.roofMat  = plugin.cnf.roofMat();
    setup_huts();
  }  
  
  // Re-load saved dungeon
  public Dungeon(Catacombs plugin,ResultSet rs) {
    try {  
      this.plugin = plugin;
      did = rs.getInt("did");
      built = true;
      name = rs.getString("dname");
      String wname = rs.getString("wname");
      builder = rs.getString("pname");
      world = plugin.getServer().getWorld(wname);
      if(world==null) {
        System.out.println("[Catacombs] World '"+wname+"' required for dungeon '"+name+"' can't be found (skipping dungeon)");
      } else {
        major = CatMat.parseMaterial(rs.getString("major"));
        minor = CatMat.parseMaterial(rs.getString("minor"));
        //enable = (rs.getInt("enable")!=0);
        
        ResultSet rs2 = plugin.sql.query("SELECT lid,type,room,roof,floor,xl,yl,zl,xh,yh,zh,sx,sy,sz,ex,ey,ez FROM levels2 WHERE did="+did+" ORDER BY yh DESC;");
        while(rs2.next()) {
          CatLevel clevel = new CatLevel(plugin,rs2,world,false); // ToDo: Remove legacy version of this
          add(clevel);
        }
        ResultSet rs3 = plugin.sql.query("SELECT fid,type,val FROM flags WHERE did="+did+";");
        while(rs3.next()) {
          CatFlag flag = new CatFlag(plugin,rs3);
          if (flag.matches(CatFlag.Type.BOSS_KILLED)) {
            bossKilled = flag;
          } else if(flag.matches(CatFlag.Type.IS_ENABLED)) {
            isEnabled = flag;
          } else if(flag.matches(CatFlag.Type.RESET_TIME)) {
            resetTime = flag;
          } else if(flag.matches(CatFlag.Type.RESET_MAX)) {
            resetMax = flag;
          } else if(flag.matches(CatFlag.Type.RESET_MIN)) {
            resetMin = flag;
          } else if(flag.matches(CatFlag.Type.ROOF)) {
            roofFlg = flag;
            roofMat = CatMat.parseMaterial(roofFlg.getString());
          } else if(flag.matches(CatFlag.Type.FLOOR)) {
            floorFlg = flag;
            floorMat = CatMat.parseMaterial(floorFlg.getString());
          } else {
            System.err.println("[Catacombs] ERROR: unrecognised dungeon flag="+flag);
          }
        }
        ResultSet rs4 = plugin.sql.query("SELECT xid,type,x,y,z FROM locations WHERE did="+did+";");
        while(rs4.next()) {
          CatLocation loc = new CatLocation(plugin,rs4);
          if (loc.matches(CatLocation.Type.END_CHEST)) {
            endChest = loc;
            //System.out.println("[Catacombs] loaded location (END_CHEST) "+loc);
          } else {
            System.err.println("[Catacombs] ERROR: unrecognised dungeon location="+loc);
          }
        }
        // New defaults
        if(floorFlg == null) {
          floorFlg = new CatFlag(CatFlag.Type.FLOOR,major.toString());
          floorFlg.saveDB(plugin.sql, did);
          floorMat = CatMat.parseMaterial(floorFlg.getString());
        }
        if(roofFlg == null) {
          roofFlg = new CatFlag(CatFlag.Type.ROOF,major.toString());
          roofFlg.saveDB(plugin.sql, did);
          roofMat = CatMat.parseMaterial(roofFlg.getString());
        }
        setupFlagsLocations();  // Set default flags and locations

      }
      //registerCubes(plugin.prot);
    } catch(Exception e) {
      System.err.println("[Catacombs] ERROR: "+e.getMessage());
    }
  }
  
  public void saveDB() {
    if(did<=0) {    
      plugin.sql.command("INSERT INTO dungeons"+
        "(version,dname,wname,pname,major,minor) VALUES"+
        "('"+plugin.info.getVersion()+"','"+name+"','"+world.getName()+"','"+builder+"','"+major+"','"+minor+"');");
      if(did<=0)
        did = plugin.sql.getLastId();

      for(CatLevel l: levels) {
        l.saveDB(plugin.sql,did);
      }
      setupFlagsLocations();
      resetMin.saveDB(plugin.sql, did);
      resetMax.saveDB(plugin.sql, did);
      resetTime.saveDB(plugin.sql, did);
      bossKilled.saveDB(plugin.sql, did);
      isEnabled.saveDB(plugin.sql, did);
      endChest.saveDB(plugin.sql, did);
      roofFlg.saveDB(plugin.sql, did);
      floorFlg.saveDB(plugin.sql, did);
    } else {
      System.err.println("[Catacombs] INTERNAL ERROR: Dungeon .db updates not implemented yet");
    }
  }
  
  public final void setupFlagsLocations() {
    if(endChest==null) {
      Block chest = getEndChest();
      endChest = new CatLocation(CatLocation.Type.END_CHEST,chest.getX(),chest.getY(),chest.getZ());
    }
    if(bossKilled == null)
      bossKilled = new CatFlag(CatFlag.Type.BOSS_KILLED,false);
    if(isEnabled == null)
      isEnabled = new CatFlag(CatFlag.Type.IS_ENABLED,true);
    if(resetTime == null)
      resetTime = new CatFlag(CatFlag.Type.RESET_TIME,(long)0);
    if(resetMax == null)
      resetMax = new CatFlag(CatFlag.Type.RESET_MAX,(long)0);
    if(roofFlg == null)
      roofFlg = new CatFlag(CatFlag.Type.ROOF,roofMat.toString());
    if(floorFlg == null)
      floorFlg = new CatFlag(CatFlag.Type.FLOOR,floorMat.toString());
    if(resetMin == null)
      resetMin = new CatFlag(CatFlag.Type.RESET_MIN,(long)0);
  }
    
  public Boolean isSuspended() {
    return !isEnabled.getBoolean();
  }
  
  public void setBuilt(Boolean built) {
    this.built = built;
  }
  
  public final void add(CatLevel l) {
    levels.add(l);
  }
  
  public World getWorld() {
    return world;
  }

  public void setWorld(World world) {
    this.world = world;
  }
  
  @Override
  public String toString() {
    String str = "Dungeon: name("+name+") builder("+builder+") world("+world.getName()+") levels="+levels.size();
    for(CatLevel l: levels) {
      str +=  l.summary();
    }
    return str;
  }
  
  public void show() {
    System.out.println("Dungeon: name("+name+") builder("+builder+") world("+world.getName()+") levels="+levels.size());
    for(CatLevel l: levels) {
      l.show();
    }
  }
  
  public List<String> getinfo() {
    List<String> info = new ArrayList<String>();
    info.add("Name: "+name);
    info.add("  World  : "+world.getName());
    info.add("  Builder: "+builder);
    info.add("  levels : "+levels.size());
    info.add("  major  : "+major);
    info.add("  minor  : "+minor);
    info.add("  built  : "+built);
    for(CatLevel l: levels) {
      info.add(" ");
      info.add("  level:"+l.summary());
      for(String s : l.getinfo())
        info.add("    "+s);
    }
    return info;
  }
  
  public List<String> dump() {
    CatLevel hut = getLowest(CatCuboid.Type.HUT);

    List<String> info = new ArrayList<String>();
    for(CatLevel l: levels) {
      for(String s : l.dump(hut.getTop())) {
        info.add(s);
      }
    }
    return info;
  }
  
  public List<String> map() {
    List<String> info = new ArrayList<String>();
    for(CatLevel l: levels) {
      for(String s : l.map()) {
        info.add(s);
      }
    }
    return info;
  }
  
  public void saveMap(String filename) {
    File f = new File(filename);
    try {
      FileWriter fstream = new FileWriter(f);
      BufferedWriter out = new BufferedWriter(fstream);
      int num = 0;
      for(CatLevel l: levels) {
        out.write("LEVEL,"+(num++)+"\r\n");
        out.write("WORLD,"+world.getName()+"\r\n");
        List<String> map = l.getMap();
        for(String s:map) {
          out.write(s+"\r\n");
        }
        out.write("\r\n");
      }
      out.close();
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }

  }  
  
  public CatMat getMajor() {
    return major;
  }

  public void setMajor(CatMat major) {
    this.major = major;
  }

  public CatMat getMinor() {
    return minor;
  }

  public void setMinor(CatMat minor) {
    this.minor = minor;
  }

  public String getName() {
    return name;
  }

  public Boolean isOk() {
    for(CatLevel l : levels)
      if(!l.isOk())
        return false;
    return true;
  }
 
//  public void guessMajor() {
//    for(CatLevel l: levels) {
//      CatMat m = l.getCube().guessMajorMat(l.getRoofDepth());
//      if(!m.is(Material.AIR)) {
//        major = m;
//        break;
//      }
//    }
//    if(major.is(Material.AIR)) {
//       System.err.println("[Catacombs] Can't figure out major mat for dungeon="+getName());
//    }
//  }
  
  public ArrayList<String> summary() {
    ArrayList<String> res = new ArrayList<String>();
    for(CatLevel l: levels) {
      res.add(l.summary());
    }
    return res;
  }
  
  public Boolean overlaps(Dungeon that) {
    if(world==null || that.world == null || !world.equals(that.world))
      return false;
    
    for(CatLevel a : levels) {
      for(CatLevel b : that.levels) {
        if(a.getCube().overlaps(b.getCube())) {
          return true;
        }
      }
    }
    return false;
  }  
  
  public Boolean isProtected(Block blk) {
    if(world==null || blk == null || !world.equals(blk.getWorld()))
      return false;
    
    if(isEnabled == null || !isEnabled.getBoolean())
      return false;
    
    for(CatLevel l : levels) {
      if(l.getCube().isInRaw(blk)) {
        return true;
      }
    }
    return false;
  }
  
  public Boolean isSuspended(Block blk) {
    if(world==null || blk == null || !world.equals(blk.getWorld()))
      return false;

    if(isEnabled.getBoolean())
      return false;
 
    for(CatLevel l : levels) {
      if(l.getCube().isInRaw(blk)) {
        return true;
      }
    }
    return false;
  }
  
  public Boolean isInRaw(Block blk) {
    if(world==null || blk == null || !world.equals(blk.getWorld()))
      return false;

    for(CatLevel l : levels) {
      if(l.getCube().isInRaw(blk)) {
        return true;
      }
    }
    return false;
  }

  public void setBuilder(String builder) {
    this.builder = builder;
  }

  public String getBuilder() {
    return builder;
  }

  public void prospect(Player p,int x,int y ,int z,Direction dir, int maxLevels) {
    builder = p.getName();
    levels.clear();
    
    CatLevel level = new CatLevel(plugin.cnf,world,x,y,z,getHut(plugin.cnf.HutType()),dir);
    levels.add(level);
    if(level.getBuild_ok() && maxLevels >0) {
      CatLevel from = level;
      System.out.println(" from can go lower="+from.getCan_go_lower());
      System.out.println(" from build ok="+from.getBuild_ok());
      System.out.println(" levels.size="+levels.size()+" maxLevels="+maxLevels);
      while(from.getCan_go_lower() && from.getBuild_ok() && levels.size() < maxLevels+1) {
        Direction tmp_dir = from.end_dir();
        if(tmp_dir != null) {
          tmp_dir = tmp_dir.turn180();
        } else {
          tmp_dir = Direction.ANY;
        }
        CatLevel lvl = new CatLevel(plugin.cnf,world,from.getBot(),tmp_dir);
        if(lvl.getBuild_ok()) {
          from.stealDirection(lvl);
          levels.add(lvl);
          from = lvl;
        } else {
          break;
        }
      }
    }
    if(maxLevels == 0)
      level.stealDirection(level);
    // Can't go down from the last level
    int last_level_num = levels.size()-1;
    levels.get(last_level_num).setEndSquare((last_level_num==0)?Square.FLOOR:Square.BIGCHEST);
    //level.show();

  }

  public void render(BlockChangeHandler handler) {
    for(CatLevel l : levels) {
      l.addLeveltoWorld(handler);
    }
    built = true;
  }
  
  public void delete(Catacombs plugin,BlockChangeHandler handler) {
    allPlayersToTop();
    for(CatLevel l : levels) {
      l.delete(plugin,handler);
    }
  }
  
  public void clearMonsters(Catacombs plugin) {
    for(CatLevel l : levels) {
      l.clearMonsters(plugin);
    }
  }
  
  public int fixSecretDoors() {
    int cnt = 0;
    for(CatLevel l : levels) {
      cnt += l.fixSecretDoors();
    }
    return cnt;
  } 
  
  public int fixDoors() {
    int cnt = 0;
    for(CatLevel l : levels) {
      cnt += l.fixDoors();
    }
    return cnt;
  }  
  
  public void reset() {
    allPlayersToTopProt();
    setBossKilled(false);
    for(CatLevel l : levels) {
      l.reset(plugin);
    }
  }
  
  public void suspend() {
    isEnabled.setBoolean(false);
    isEnabled.saveDB(plugin.sql, did);
    for(CatLevel l : levels) {
      l.suspend(plugin,roofMat);
    }
  }
  
  public void buildWindows(Material mat) {
    for(CatLevel l : levels) {
      l.buildWindows(mat);
    }
  }  
  public Boolean isEnabled() {
    return isEnabled.getBoolean();
  }
  
  public void enable() {
    isEnabled.setBoolean(true);
    isEnabled.saveDB(plugin.sql, did);
    for(CatLevel l : levels) {
      l.enable(roofMat);
    }
  } 
  
  public void setResetMinMax(String t) {
    Pattern p = Pattern.compile("((\\d+[smhd])+)-((\\d+[smhd])+)");
    Matcher m = p.matcher(t);
    Long lo,hi;
    if(m.find()) {
      Long a = CatUtils.parseTime(m.group(1));
      Long b = CatUtils.parseTime(m.group(3));
      lo = Math.min(a, b);
      hi = Math.max(a, b);
      //System.out.println("[Catacombs] a="+a+" b="+b+" lo="+lo+" hi="+hi+" a="+m.group(1)+" b="+m.group(3));
    } else {
      lo = hi = CatUtils.parseTime(t);
    }
    resetMax.setLong(hi);
    resetMin.setLong(lo);
    resetMax.saveDB(plugin.sql, did);
    resetMin.saveDB(plugin.sql, did);
    newResetTime();
  }
  
  public Boolean bossKilled() {
    if(bossKilled == null) {
      System.err.println("[Catacombs] INTERNAL ERROR: bossKilled flag is null (attempt to get)");
      return false;
    }
    return bossKilled.getBoolean();
  }
  
  public void setBossKilled(Boolean val) {
    if(bossKilled == null) {
      System.err.println("[Catacombs] INTERNAL ERROR: bossKilled flag is null (attempt to set)");
    } else {
      bossKilled.setBoolean(val);
      if(did <= 0)
        System.err.println("[Catacombs] INTERNAL ERROR: Attempt to set bossKilled on dungeon with no dungeon id "+name);
      else
        bossKilled.saveDB(plugin.sql, did);
    }
  }
    
  public void remove() {
    plugin.sql.removeDungeon(did);
  }
  
  public Location getSafePlace(Block blk) {
    Location loc = null;
    for(BlockFace dir : Arrays.asList(
      BlockFace.NORTH,BlockFace.EAST,
      BlockFace.SOUTH,BlockFace.WEST,BlockFace.UP)) {
      Block tmp = blk.getRelative(dir);
      if(tmp.getType()==Material.AIR) {
        loc = tmp.getLocation();
        break;
      }      
    }
    if(loc==null) {
      loc = blk.getLocation();
    }
    loc.setX(loc.getX()+0.5);
    loc.setZ(loc.getZ()+0.5);
    return loc;
  }
  
  public CatLevel getLowest(CatCuboid.Type type) {
    CatLevel lvl = null;
    int lowest = 128;
    for(CatLevel l : levels) {
      if(l.getCube().getType() == type && l.getCube().yl<lowest) {
        lowest = l.getCube().yl;
        lvl = l;
      }
    }
    if(lvl == null) 
      System.err.println("[Catacombs] ERROR: Can't find lowest "+type+" level for dungeon "+name);
    return lvl;
  }
  
  public Location getTopLocation() {
    CatLevel l = getLowest(CatCuboid.Type.HUT);
    if(l==null || l.getTop().y==0)
      return null;
    
    int depth = l.getRoofDepth()+l.getRoomDepth();
    return getSafePlace(world.getBlockAt(l.getTop().x,l.getTop().y-depth+1,l.getTop().z));
  }
  
  public Block getEndChest() {
    CatLevel l = getLowest(CatCuboid.Type.LEVEL);
    return l.getEndChestDoor();
  }
  
  public Location getBotLocation() {
    Block chest = getEndChest();
    return (chest==null)?null:getSafePlace(chest);
  }  
  
  public Boolean teleportToTop(Player player) {
    Location loc = getTopLocation();
    if(loc!=null) {
      teleport(player, loc);
      return true;
    }
    return false;
  }  
  
  public Boolean teleportToBot(Player player) {
    Location loc = getBotLocation();
    if(loc!=null) {
      teleport(player, loc);
      return true;
    }
    return false;
  }    
  
  // Check chunks are loaded and fresh
  public void teleport(Player p, Location loc) {
    World w = loc.getWorld();
    Chunk chunk = w.getChunkAt(loc);
    if (!w.isChunkLoaded(chunk))
      w.loadChunk(chunk);
    else
      w.refreshChunk(chunk.getX(), chunk.getZ());   
    p.teleport(loc);
  }

    
  public Boolean isBuilt() {
    return built;
  }

  public Boolean isNatural() {
    for (CatLevel l : levels) {
      if(l.getCube().isLevel() && !l.getCube().isNatural(plugin.cnf)) {
        return false;
      }
    }
    return true;
  }

  public Boolean isInhabited() {
    for(Player player : world.getPlayers()) {
      Location loc = player.getLocation();
      Block blk = loc.getBlock();
      int x = blk.getX();
      int y = blk.getY();
      int z = blk.getZ();
      for(CatLevel l : levels) {
        CatCuboid c =  l.getCube();
        if(c.isLevel() && c.isIn(x,y,z))
          return true;
      }
    }
    return false;
  }

  public List<Player> allPlayersInRaw() {
    List<Player> list = new ArrayList<Player>();
    for(Player player : world.getPlayers()) {
      if(isInRaw(player.getLocation().getBlock())) {
        list.add(player);
      }
    }
    return list;
  }
  
  public List<Player> allPlayersProtected() {
    List<Player> list = new ArrayList<Player>();
    for(Player player : world.getPlayers()) {
      if(isProtected(player.getLocation().getBlock())) {
        list.add(player);
      }
    }
    return list;
  }
  
  private void movePlayers(Location tloc, List<Player> list) {
    for(Player player : list) {
      if(tloc!=null) {
        player.teleport(tloc);
      } else {
        Location ploc = player.getLocation();
        Location safe_place = world.getHighestBlockAt(ploc).getLocation();
        player.teleport(safe_place);
      }      
    }
  }
  
  public void allPlayersToTop() {
    movePlayers(getTopLocation(),allPlayersInRaw());
  }
  
  public void allPlayersToTopProt() {
    movePlayers(getTopLocation(),allPlayersProtected());
  }  

  private static void setup_huts() {
    if(hut_list == null) {
      String name;
      hut_list = new HashMap<String,PrePlanned>();
      name = "default";
      hut_list.put(name,new PrePlanned(name,PrePlanned.Type.HUT, new String[] {
        "#######",
        "#oaTfo#",
        "#o.:.o#",
        "#t:V:t#",
        "#z.:.z#",
        "#Z.t.Z#",
        "###+###"
      })); 
      name = "small";
      hut_list.put(name,new PrePlanned(name,PrePlanned.Type.HUT, new String[] {
        " #D# ",
        "#:V:#",
        "#t:t#",
        "#o.o#",
        "#o.o#",
        " #+# "
      }));
//      name = "doors";
//      hut_list.put(name,new PrePlanned(name,PrePlanned.Type.HUT, new String[] {
//        "#++##",
//        "#.:.#",
//        "+:V:+",
//        "+.:.+",
//        "#++##"
//
//      }));
      name = "tiny";
      hut_list.put(name,new PrePlanned(name,PrePlanned.Type.HUT, new String[] {
        " D ",
        "DVD",
        "#:#"
      }));
      name = "pit";
      hut_list.put(name,new PrePlanned(name,PrePlanned.Type.HUT, new String[] {
        "~:~",
        ":V:",
        "~:~"
      }));
      name = "medium";
      hut_list.put(name,new PrePlanned(name,PrePlanned.Type.HUT, new String[] {
        "```~```~```",
        "`##GGGGG##`",
        "~#ZoZoZoZ#~",
        "`Gz.z.z.zG`",
        "`Gt.....tG`",
        "~Ga.....oG~",
        "`Gf.....:G`",
        "`#T....:VD`",
        "``#otxtoD``",
        "`~`##+##`~`",
        "```````````"
      }));
      name = "medium2";
      hut_list.put(name,new PrePlanned(name,PrePlanned.Type.HUT, new String[] {
        " #GG#####GG# ",
        "#oo.zZ#Zz.oo#",
        "#t....#....t#",
        "#oo.zZ#Zz.oo#",
        "#.....#.....#",
        "#oo.zZ#Zz.oo#",
        "#t....#....t#",
        "#oo.zZ#Zz.oo#",
        "###+#####+###",
        "#...tfTat...#",
        "#K..........#",
        "#K........#+#",
        "#K.e......G:#",
        "#K..t...t.BVD",
        " #GG##+##GGD "
      }));
      name = "large";
      hut_list.put(name,new PrePlanned(name,PrePlanned.Type.HUT, new String[] {
        "~```````````````~",
        "```##GGGGGGG##```",
        "``#oZoo.t.ooZo#``",
        "`#o.z.......z.o#`",
        "`Gt...........tG`",
        "`Go...........oG`",
        "`Go.....t.....aG`",
        "`GZz...o#o....TG`",
        "`Gt.....o.....tG`",
        "`Go...........fG`",
        "`#o.......oGGGD#`",
        "`#Zztoo.x.oGt:VD`",
        "`#######+###+#D#`",
        "`#oo.oo.x.oo.oo#`",
        "``#t....x....t#``",
        "```#GGG#+#GGG#```",
        "~``````~`~``````~"
      }));
      name = "testboss";
      hut_list.put(name,new PrePlanned(name,PrePlanned.Type.HUT, new String[] {
        "     D     ",
        "####D*D####",
        "#...t.t...#",
        "#.........#",
        "#.........#",
        "#t.......t#",
        "#....:....#",
        "#...:V:...#",
        "#...t:t...#",
        "#####+#####"
      }));
    }
  }

  public PrePlanned getHut(String name) {
    if(hut_list.containsKey(name)) {
      return hut_list.get(name);
    } else {
      System.err.println("[Catacombs] Hut type '"+name+"' not defined, using 'default'");
      System.out.println("[Catacombs] legal hut names "+hut_list.keySet());
    }
    return hut_list.get("default");
  }
  
  private void newResetTime() {
    if(resetMax.getLong()==0) {
      resetTime.setLong((long)0);
      resetTime.saveDB(plugin.sql, did);
    } else {
      long now = System.currentTimeMillis();
      int hi = (int)(resetMax.getLong()/1000);
      int lo = (int)(resetMin.getLong()/1000);
      long n;
      if(hi==lo)
        n = lo*1000;
      else
        n = (long)(plugin.cnf.nextInt(hi-lo)+lo)*1000;
      resetTime.setLong(now + n);
      resetTime.saveDB(plugin.sql, did);
      System.out.println("[Catacombs] Next reset ("+name+") "+CatUtils.formatTime(n));
    } 
    resetWarnings = 0;
  }
  
  public void maintain() {
    //System.out.println("[Catacombs] Calling regular process on "+name);
    if(!built)
      return;
    
    
    // Timed reset
    if(resetTime.getLong() > 0) {
      long now = System.currentTimeMillis();
      if(now > resetTime.getLong()) {
        List<Player> players = allPlayersInRaw();
        System.out.println("[Catacombs] Timed reset of Dungeon '"+name+"' "+players);
        for(Player player: players) {
          player.sendMessage("Dungeon '"+name+"' timed reset");
        }
        reset();
        newResetTime();
      } else {
        long delta = (resetTime.getLong() - now + 500)/1000;

        int cnt = 1;
        int newWarn = 0;
        for(long warn: new long[] {60*5,60*4,60*3,60*2,60,30,15,10,5}) {
          if(delta<=warn) {
            newWarn = cnt;
          }
          cnt++;
        }  
        if(newWarn > resetWarnings) {
          //String when = (delta>60)?(delta/60)+" min(s)":delta+" sec(s)";
          String when = CatUtils.formatTime(delta*1000);
          //System.out.println("[Catacombs] Timed reset Dungeon("+name+") "+players+" in "+when);
          List<Player> players = allPlayersInRaw();
          for(Player player: players) {
            player.sendMessage("Dungeon("+name+") will reset in "+when);
          }
          resetWarnings = newWarn;
        }
      }
    }

    // Are any players in the dungeon
    /*Boolean old_running = running;
    running = players.size() > 0;*/
//    if(old_running!=running) {
//      if(running)
//        System.out.println("[Catacombs] Activating Dungeon("+name+") "+players);
//      else
//        System.out.println("[Catacombs] De-activating Dungeon("+name+")");
//    }
    
    // Check Spawners
    
    // Dungeon ownership
        
  }
}
