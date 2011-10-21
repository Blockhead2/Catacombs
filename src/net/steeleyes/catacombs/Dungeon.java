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

import com.avaje.ebean.EbeanServer;
import org.bukkit.block.BlockFace;

public class Dungeon {
  private CatConfig cnf = null;
  private World world;
  private final ArrayList<CatLevel> levels = new ArrayList<CatLevel>();
  private String name;
  private String builder;
  private CatMat major;
  private CatMat minor;

  private static Map<String,PrePlanned> hut_list = null;
 
  private Boolean built = false;

  public void setBuilt(Boolean built) {
    this.built = built;
  }

  public Dungeon(String name,CatConfig cnf,World world){
    this.name = name;
    this.cnf = cnf;
    this.world = world;
    major = cnf.majorMat();
    minor = cnf.minorMat();
    setup_huts();
  }
  public Dungeon(String name,CatConfig cnf,World world,CatMat major, CatMat minor){
    this.name = name;
    this.cnf = cnf;
    this.world = world;
    this.major = major;
    this.minor = minor;
    setup_huts();
  }
  
  public void add(CatLevel l) {
    levels.add(l);
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
 
  public void guessMajor() {
    for(CatLevel l: levels) {
      CatMat m = l.getCube().guessMajorMat(l.getRoofDepth());
      if(!m.is(Material.AIR)) {
        major = m;
        break;
      }
    }
    if(major.is(Material.AIR)) {
       System.err.println("[Catacombs] Can't figure out major mat for dungeon="+getName());
    }
  }
  public void debugMajor() {
    for(CatLevel l: levels) {
      int roof = l.getCube().guessRoofSize();
      int room = l.getCube().guessRoomSize();
      CatMat m = l.getCube().guessMajorMat(roof);
      if(!m.is(Material.AIR)) {
        System.out.println("[Catacombs] Dungeon '"+name+"'  Major="+m+" roofDepth="+roof+" roomDepth="+room);
        major = m;
        break;
      }
    }
    if(major.is(Material.AIR)) {
       System.err.println("[Catacombs] Can't figure out major mat for dungeon="+getName());
    }
  }
  
  public ArrayList<String> summary() {
    ArrayList<String> res = new ArrayList<String>();
    for(CatLevel l: levels) {
      res.add(l.summary());
    }
    return res;
  }
  
  public Boolean overlaps(CatCuboid that) {
    for(CatLevel l : levels) {
      if(l.getCube().overlaps(that)) {
        return true;
      }
    }
    return false;
  }
  
  public Boolean isProtected(Block blk) {
    for(CatLevel l : levels) {
      if(l.getCube().isProtected(blk)) {
        return true;
      }
    }
    return false;
  }
  
  public Boolean isSuspened(Block blk) {
    for(CatLevel l : levels) {
      if(l.getCube().isSuspended(blk)) {
        return true;
      }
    }
    return false;
  }
  
  public Boolean isInRaw(Block blk) {
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
    
    CatLevel level = new CatLevel(cnf,world,x,y,z,getHut(cnf.HutType()),dir);
    levels.add(level);
    if(level.getBuild_ok() && maxLevels >0) {
      CatLevel from = level;
      while(from.getCan_go_lower() && from.getBuild_ok() && levels.size() < maxLevels+1) {
        Direction tmp_dir = from.end_dir();
        if(tmp_dir != null) {
          tmp_dir = tmp_dir.turn180();
        } else {
          tmp_dir = Direction.ANY;
        }
        CatLevel lvl = new CatLevel(cnf,world,from.getBot(),tmp_dir);
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
    level.show();

  }

  public void render(BlockChangeHandler handler) {
    for(CatLevel l : levels) {
      l.addLeveltoWorld(handler);
    }
    built = true;
  }
  
  public void delete(BlockChangeHandler handler) {
    allPlayersToTop();
    for(CatLevel l : levels) {
      l.delete(handler);
    }
  }
  
  public void reset() {
    allPlayersToTopProt();
    for(CatLevel l : levels) {
      l.reset();
    }
  }
  public void suspend(EbeanServer db) {
    for(CatLevel l : levels) {
      l.suspend(major);
    }
        
    if(db != null) {
      List<dbLevel> list = db.find(dbLevel.class).where().ieq("dname",name).findList();
      if (list != null && !list.isEmpty()) {
        for(dbLevel cube: list) {
          cube.setEnable(false);
          db.save(cube);
        }
      }
    }
  }
  
  public void enable(EbeanServer db) {
    for(CatLevel l : levels) {
      l.suspend(major);
    }

    if(db != null) {
      List<dbLevel> list = db.find(dbLevel.class).where().ieq("dname",name).findList();
      if (list != null && !list.isEmpty()) {
        for(dbLevel cube: list) {
          cube.setEnable(true);
          db.save(cube);
        }
      }
    }  
  }  
  
  public void remove(EbeanServer db) {
    if(db != null) {
      List<dbLevel> list = db.find(dbLevel.class).where().ieq("dname",name).findList();
      if (list != null && !list.isEmpty()) {
        for(dbLevel cube: list) {
          db.delete(cube);
        }
      }
    }  
  }  
  
  private Location getSafePlace(Block blk) {
    Location loc = null;
    for(BlockFace dir : Arrays.asList(
      BlockFace.NORTH,BlockFace.EAST,
      BlockFace.SOUTH,BlockFace.WEST,BlockFace.UP)) {
      Block tmp = blk.getRelative(BlockFace.NORTH);
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
    return lvl;
  }
  
  public Location getTopLocation() {
    CatLevel l = getLowest(CatCuboid.Type.HUT);
    if(l==null || l.getTop().y==0)
      return null;
    
    int depth = l.getRoofDepth()+l.getRoomDepth();
    return getSafePlace(world.getBlockAt(l.getTop().x,l.getTop().y-depth+1,l.getTop().z));
  }
  
  public Location getBotLocation() {
    CatLevel l = getLowest(CatCuboid.Type.LEVEL);
    if(l==null || l.getBot().y==0)
      return null;

    return getSafePlace(world.getBlockAt(l.getBot().x,l.getBot().y+5,l.getBot().z));
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
  
  
  public void saveDB(EbeanServer db) {
    int num = 0;
    
    // What a mess! Copy to a flat dedicated object for saving the key info
    for(CatLevel l : levels) {
      dbLevel n = new dbLevel();
      n.setDname(name);
      n.setWname(world.getName());
      n.setPname(builder);
      CatCuboid cube = l.getCube();
      n.setHut(cube.isHut());
      n.setXl(cube.xl);
      n.setYl(cube.yl);
      n.setZl(cube.zl);
      n.setXh(cube.xh);
      n.setYh(cube.yh);
      n.setZh(cube.zh);
      n.setSx(l.getTop().x);
      n.setSy(l.getTop().y);
      n.setSz(l.getTop().z);
      n.setEx(l.getBot().x);
      n.setEy(l.getBot().y);
      n.setEz(l.getBot().z);
      n.setDx(cube.dx());  // Dx and Dy strictly aren't needed, they could be computed from cube size
      n.setDy(cube.dz());  // 3D-2D so use dz for Y size
      n.setMap(l.getMap());
      n.setEnable(cube.getEnable());
      n.setNum(num++);
      db.save(n);
    }
  }
    
  public Boolean isBuilt() {
    return built;
  }

  public Boolean isNatural() {
    for (CatLevel l : levels) {
      if(l.getCube().isLevel() && !l.getCube().isNatural(cnf)) {
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
  
  public void allPlayersToTop() {
    for(Player player : world.getPlayers()) {
      Location ploc = player.getLocation();
      Block blk = player.getLocation().getBlock();
      if(isInRaw(blk)) {
        Location tloc = getTopLocation();
        if(tloc!=null) {
          player.teleport(tloc);
        } else {
          Location safe_place = world.getHighestBlockAt(ploc).getLocation();
          player.teleport(safe_place);
        }
      }
    }
  }
  public void allPlayersToTopProt() {
    for(Player player : world.getPlayers()) {
      Location ploc = player.getLocation();
      Block blk = player.getLocation().getBlock();
      if(isProtected(blk)) {
        Location tloc = getTopLocation();
        if(tloc!=null) {
          player.teleport(tloc);
        } else {
          Location safe_place = world.getHighestBlockAt(ploc).getLocation();
          player.teleport(safe_place);
        }
      }
    }
  }

  public void registerCubes(MultiWorldProtect prot) {
    String wld = world.getName();
    for(CatLevel l : levels) {
      prot.add(wld,l.getCube());
    }
  }
  
  public void unregisterCubes(MultiWorldProtect prot) {
    String wld = world.getName();
    for(CatLevel l : levels) {
      prot.remove(wld,l.getCube());
    }
  }

  private static void setup_huts() {
    if(hut_list == null) {
      String name;
      hut_list = new HashMap<String,PrePlanned>();
      name = "default";
      hut_list.put(name,new PrePlanned(name,PrePlanned.Type.HUT, new String[] {
        "#######",
        "#oATfo#",
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
        "#.:.#",
        "#t.t#",
        "#...#",
        " #+# "
      }));
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
        "...t...t...",
        ".##GGGGG##.",
        "t#ZoZoZoZ#t",
        ".Gz.z.z.zG.",
        ".Gt.....tG.",
        "tGA......Gt",
        ".Gf.....:G.",
        ".#T....:VD.",
        ".t$.txt.Dt.",
        "...##+##...",
        ">21x......."
      }));
      name = "large";
      hut_list.put(name,new PrePlanned(name,PrePlanned.Type.HUT, new String[] {
        "~```````````````~",
        "```##GGGGGGG##```",
        "``#.Zoo.t.ooZ.#``",
        "`#..z.......z..#`",
        "`Gt...........tG`",
        "`Go............G`",
        "`Go.....t.....AG`",
        "`GZz....#.....TG`",
        "`Gt...........tG`",
        "`Go...........fG`",
        "`#o........GGGD#`",
        "`#Zzt...x..Gt:VD`",
        "`#######+###+#D#`",
        "`#......x......#`",
        "``#t....x....t#``",
        "```#GGG#+#GGG#```",
        "~``````~`~``````~"
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
}
