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

public class Dungeon {
  private CatConfig cnf = null;
  private World world;
  private final ArrayList<CatLevel> levels = new ArrayList<CatLevel>();
  private String name;
  private String builder;
  private CatMat major;
  private CatMat minor;
  private Boolean enable = true;
  
  private Boolean bossKilled = false;
  //private CatArena arena = null;

  private static Map<String,PrePlanned> hut_list = null;
  
  private List<CatFlag> flags = new ArrayList<CatFlag>();
  private List<CatLocation> locations = new ArrayList<CatLocation>();
 
  private Boolean built = false;
  

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
  
  public void setBuilt(Boolean built) {
    this.built = built;
  }
  
  public void add(CatLevel l) {
    levels.add(l);
  }
  
  public Boolean bossKilled() {
    return bossKilled;
  }
  
  public World getWorld() {
    return world;
  }
  
  public void setBossKilled(Boolean val) {
    bossKilled = val;
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
  
  public Boolean triggerEncounter(Catacombs plugin, Block blk) {
    return false;
  }
  
  public void stopEncounter(Boolean won) {
  }
  
  public ArrayList<String> summary() {
    ArrayList<String> res = new ArrayList<String>();
    for(CatLevel l: levels) {
      res.add(l.summary());
    }
    return res;
  }
  
  public Boolean overlaps(Dungeon that) {
    for(CatLevel a : levels) {
      for(CatLevel b : that.levels) {
        if(a.getCube().overlaps(b.getCube())) {
          return true;
        }
      }
    }
    return false;
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
  
  public void reset(Catacombs plugin) {
    allPlayersToTopProt();
    bossKilled=false;
    for(CatLevel l : levels) {
      l.reset(plugin);
    }
  }
  
  public void suspend(Catacombs plugin,CatSQL sql) {
    enable = false;
    bossKilled=true;
    for(CatLevel l : levels) {
      l.suspend(plugin,major);
    }
    sql.suspendDungeon(name);
  }
  
  public void enable(CatSQL sql) {
    enable = true;
    bossKilled = false;
    for(CatLevel l : levels) {
      l.enable(major);
    }
    sql.enableDungeon(name);
  }  
  
  public void remove(CatSQL sql) {
    sql.removeDungeon(name);
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
    int depth = l.getFloorDepth(); 
    return getSafePlace(world.getBlockAt(l.getBot().x,l.getBot().y+depth,l.getBot().z));
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
  
  
  public void saveDB(Catacombs plugin, CatSQL sql) {
    System.err.print("Need to implent Dungeon.saveDB()");
    
    int i = 0;
    for(CatLevel l: levels) {
      CatCuboid cube = l.getCube();
      int hut = (cube.isHut())?1:0;
      Vector top = l.getTop();
      Vector bot = l.getBot();
      int en = (cube.isEnabled())?1:0;
      sql.command("INSERT INTO levels"+
        "(dname,wname,pname,hut,enable,xl,yl,zl,xh,yh,zh,sx,sy,sz,ex,ey,ez,dx,dy,num,map) VALUES"+
        "('"+name+"','"+world.getName()+"','"+builder+"',"+hut+","+en+
          ","+cube.xl+","+cube.yl+","+cube.zl+
          ","+cube.xh+","+cube.yh+","+cube.zh+
          ","+top.x+","+top.y+","+top.z+
          ","+bot.x+","+bot.y+","+bot.z+
          ","+cube.dx()+","+cube.dz()+","+i+",''"+
        ");");
      i++;
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
}
