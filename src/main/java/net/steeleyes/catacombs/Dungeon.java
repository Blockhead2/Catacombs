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
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;

public class Dungeon extends Region implements Listener {
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
  
  private Mob boss = null;
  
  private transient Clan clan = null;
  private transient Cuboid bbox = null;
  
  // New dungeon
  public Dungeon(Catacombs plugin,String name,World world){
    this.name = name;
    this.plugin = plugin;
    this.world = world;
    this.major    = plugin.getCnf().majorMat();
    this.minor    = plugin.getCnf().minorMat();
    this.floorMat = plugin.getCnf().floorMat();
    this.roofMat  = plugin.getCnf().roofMat();
    clan = new Clan(plugin,this);
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
      clan = new Clan(plugin,this);
      if(world==null) {
        System.out.println("[Catacombs] World '"+wname+"' required for dungeon '"+name+"' can't be found (skipping dungeon)");
      } else {
        major = CatMat.parseMaterial(rs.getString("major"));
        minor = CatMat.parseMaterial(rs.getString("minor"));
        //enable = (rs.getInt("enable")!=0);
        
        ResultSet rs2 = plugin.getSql().query("SELECT lid,type,room,roof,floor,xl,yl,zl,xh,yh,zh,sx,sy,sz,ex,ey,ez FROM levels2 WHERE did="+did+" ORDER BY yh DESC;");
        while(rs2.next()) {
          CatLevel clevel = new CatLevel(plugin,rs2,world,false); // ToDo: Remove legacy version of this
          add(clevel);
        }
        ResultSet rs3 = plugin.getSql().query("SELECT fid,type,val FROM flags WHERE did="+did+";");
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
        ResultSet rs4 = plugin.getSql().query("SELECT xid,type,x,y,z FROM locations WHERE did="+did+";");
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
          floorFlg.saveDB(plugin.getSql(), did);
          floorMat = CatMat.parseMaterial(floorFlg.getString());
        }
        if(roofFlg == null) {
          roofFlg = new CatFlag(CatFlag.Type.ROOF,major.toString());
          roofFlg.saveDB(plugin.getSql(), did);
          roofMat = CatMat.parseMaterial(roofFlg.getString());
        }
        setupFlagsLocations();  // Set default flags and locations
        updateBBox();
        registerListener();
      }
    } catch(Exception e) {
      System.err.println("[Catacombs] ERROR: "+e.getMessage());
    }
  }
  
  public void render(BlockChangeHandler handler) throws Exception {
    int lvl = 0;
    for(CatLevel l : levels) {
      String[] text = new String[4];
      text[0] = name;
      if(lvl==0) {
        text[1] = "Levels:"+(levels.size()-1);
      } else {
        text[1] = "Level:"+lvl;
      }
      l.addLeveltoWorld(handler,text);
      lvl++;
    }
    built = true;
    updateBBox();
    registerListener();
  }
    
  public void remove() {
    plugin.getSql().removeDungeon(did);
    unregisterListener();
  }
  
  public void unrender(Catacombs plugin,BlockChangeHandler handler) {
    allPlayersToTop();
    for(CatLevel l : levels) {
      l.delete(plugin,handler);
    }
  }  
  
  public void saveDB() {
    if(did<=0) {    
      plugin.getSql().command("INSERT INTO dungeons"+
        "(version,dname,wname,pname,major,minor) VALUES"+
        "('"+plugin.getInfo().getVersion()+"','"+name+"','"+world.getName()+"','"+builder+"','"+major+"','"+minor+"');");
      if(did<=0)
        did = plugin.getSql().getLastId();

      for(CatLevel l: levels) {
        l.saveDB(plugin.getSql(),did);
      }
      setupFlagsLocations();
      resetMin.saveDB(plugin.getSql(), did);
      resetMax.saveDB(plugin.getSql(), did);
      resetTime.saveDB(plugin.getSql(), did);
      bossKilled.saveDB(plugin.getSql(), did);
      isEnabled.saveDB(plugin.getSql(), did);
      endChest.saveDB(plugin.getSql(), did);
      roofFlg.saveDB(plugin.getSql(), did);
      floorFlg.saveDB(plugin.getSql(), did);
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
  
  private void updateBBox() {
    for(CatLevel l:levels) {
      Cuboid c = l.getCube();
      if(bbox==null) {
        bbox = new Cuboid(c.xl,c.yl,c.zl,c.xh,c.yh,c.zh);
      } else {
        bbox.union(c);
      }
    }
  }
  
  private void registerListener() {
    PluginManager pm = plugin.getServer().getPluginManager();
    pm.registerEvents(this, plugin);
  }
  
  private void unregisterListener() {
    HandlerList.unregisterAll(this);
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
  public void regionMobDeath(LivingEntity ent) {
    if(boss != null && ent.equals(boss.getEnt())) {
      System.out.println("Boss death in dungeon "+name+" "+ent);
      boss = null;
    } else {
      System.out.println("Monster death in dungeon "+name+" "+ent);  
    }
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
    assert(that!=null);
    assert(world!=null);
    assert(that.world!=null);
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
    if(bbox == null || blk==null || world == null) {
      return false;
    }
    if(bbox.isIn(blk.getX(), blk.getY(), blk.getZ()) &&
            world.equals(blk.getWorld()) &&
            isEnabled != null &&
            isEnabled.getBoolean()) {
      for(CatLevel l : levels) {
        if(l.getCube().isInRaw(blk)) {
          return true;
        }
      }
    }
    return false;
  }
  
  public Boolean isSuspended(Block blk) {
    if(bbox == null || blk==null || world == null) {
      return false;
    }
    if(bbox.isIn(blk.getX(), blk.getY(), blk.getZ()) &&
            world.equals(blk.getWorld()) &&
            isEnabled != null &&
            !isEnabled.getBoolean()) {
      for(CatLevel l : levels) {
        if(l.getCube().isInRaw(blk)) {
          return true;
        }
      }
    }
    return false;
  }
  
  public Boolean isInRaw(Block blk) {
    if(bbox == null || blk==null || world == null) {
      return false;
    }
    if(bbox.isIn(blk.getX(), blk.getY(), blk.getZ()) &&
            world.equals(blk.getWorld())) {
      for(CatLevel l : levels) {
        if(l.getCube().isInRaw(blk)) {
          return true;
        }
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
    
    CatLevel level = new CatLevel(plugin.getCnf(),world,x,y,z,getHut(plugin.getCnf().HutType()),dir);
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
        CatLevel lvl = new CatLevel(plugin.getCnf(),world,from.getBot(),tmp_dir);
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

  public void clearMonsters(Catacombs plugin) {
    for(CatLevel l : levels) {
      l.clearMonsters(plugin);
    }
  }
  
  public int changeDoorsToIron() {
    int cnt = 0;
    for(CatLevel l : levels) {
      cnt += l.changeDoorsToIron();
    }
    return cnt;
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
    isEnabled.saveDB(plugin.getSql(), did);
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
    isEnabled.saveDB(plugin.getSql(), did);
    for(CatLevel l : levels) {
      l.enable(roofMat);
    }
  } 
  
  public String getResetTime() {
    long now = System.currentTimeMillis();
    return CatUtils.formatTime(resetTime.getLong()-now);
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
    resetMax.saveDB(plugin.getSql(), did);
    resetMin.saveDB(plugin.getSql(), did);
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
        bossKilled.saveDB(plugin.getSql(), did);
    }
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
    int lowest = world.getMaxHeight()+1;
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

  public int getBlockDepth(Block blk) {
    if(isInRaw(blk)) {
      int cnt = 0;
      for(CatLevel l : levels) {
        CatCuboid c =  l.getCube();
        if(c.isInRaw(blk))
          return cnt;
        cnt++;
      }
    }
    return -1;
  }
    
  public Boolean isBuilt() {
    return built;
  }

  public Boolean isNatural() {
    for (CatLevel l : levels) {
      if(l.getCube().isLevel() && !l.getCube().isNatural(plugin.getCnf())) {
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
        "#Zpt.Z#",
        "###+###"
      })); 
      name = "small";
      hut_list.put(name,new PrePlanned(name,PrePlanned.Type.HUT, new String[] {
        " #D# ",
        "#:V:#",
        "#p:t#",
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
        "#:#",
        "p``"
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
        "``#otxtpD``",
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
        "#K.........p#",
        "#K........#+#",
        "#K.e......G:#",
        "#K..tp..t.BVD",
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
        "``#t...px....t#``",
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
        "#...t:t.p.#",
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
      resetTime.saveDB(plugin.getSql(), did);
    } else {
      long now = System.currentTimeMillis();
      int hi = (int)(resetMax.getLong()/1000);
      int lo = (int)(resetMin.getLong()/1000);
      long n;
      if(hi==lo)
        n = lo*1000;
      else
        n = (long)(plugin.getCnf().nextInt(hi-lo)+lo)*1000;
      resetTime.setLong(now + n);
      resetTime.saveDB(plugin.getSql(), did);
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
  
  /////////////////////////////////////////////////////////////////////////////
  //
  //    BLOCK Events
  //
  /////////////////////////////////////////////////////////////////////////////
  
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBlockPlace(BlockPlaceEvent event){
    Block blk = event.getBlockPlaced();
    
    // Special feature to allow players to put torches in dungeons in worldguard zones
    if(event.isCancelled()) {
      if(isInRaw(blk) && plugin.getCnf().isPlaceable(blk)) {
        event.setCancelled(false);
      }
      return;
    }
    
    if(isProtected(blk) && !plugin.getCnf().isPlaceable(blk)) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onBlockBreak(BlockBreakEvent event){
    Block blk = event.getBlock();
    if(isProtected(blk) && !event.isCancelled() && !plugin.getCnf().isBreakable(blk)) {
      if(blk.getType() == Material.MOB_SPAWNER) {
        if(plugin.getCnf().ProtectSpawners()) {
          Player player = event.getPlayer();
          player.sendMessage("Put a torch on opposite sides to stop spawns");
          event.setCancelled(true);
        }     
        return;        
      }
      event.setCancelled(true);   
    }
  }
  
  @EventHandler(priority = EventPriority.LOW)
  public void onBlockSpread(BlockSpreadEvent event){
    Block blk = event.getBlock();
    if(isInRaw(blk) && !event.isCancelled()) {
      BlockState state = event.getNewState();
      if(blk.getType() == Material.DIRT && state.getType() == Material.GRASS) {
        event.setCancelled(true);
      }   
    }
  }
  
  @EventHandler(priority = EventPriority.LOW)
  public void onBlockIgnite(BlockIgniteEvent event){
    Block blk = event.getBlock();
    if(isInRaw(blk) && !event.isCancelled()) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler(priority = EventPriority.LOW)
  public void onBlockBurn(BlockBurnEvent event){
    Block blk = event.getBlock();
    if(isInRaw(blk) && !event.isCancelled()) {
      event.setCancelled(true);
    }
  }  
  
  @EventHandler(priority = EventPriority.LOW)
  public void onBlockDamage(BlockDamageEvent event) {
    Block blk = event.getBlock();
    if(!isInRaw(blk))
      return;
    CatUtils.toggleSecretDoor(blk);
  }
  
  
  /////////////////////////////////////////////////////////////////////////////
  //
  //    Player Events
  //
  /////////////////////////////////////////////////////////////////////////////
   
  @EventHandler(priority = EventPriority.LOW)
  public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
    Block blk = event.getBlockClicked();
    if (isProtected(blk) && !event.isCancelled()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onPlayerBucketFill(PlayerBucketFillEvent event) {
    Block blk = event.getBlockClicked();
    if (isProtected(blk) && !event.isCancelled()) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
    Player player = event.getPlayer();
    Block blk = player.getLocation().getBlock();
    if (isProtected(blk) && !event.isCancelled() &&
            !plugin.hasPermission(player, "catacombs.admin")) {
      for(String cmd:plugin.getCnf().BannedCommands()) {
        if (event.getMessage().equals(cmd)) {
          player.sendMessage("'"+cmd+"' is blocked in dungeons");
          event.setCancelled(true);
          return;
        }
      }
    }
  }
  
  @EventHandler(priority = EventPriority.LOW)
  public void onPlayerInteract(PlayerInteractEvent event) {
    Block blk = event.getClickedBlock();
    if (isInRaw(blk) && !event.isCancelled()) {
      Material mat = blk.getType();
      if (mat == Material.STONE_BUTTON) {
        Block below = blk.getRelative(BlockFace.DOWN);
        // ToDo: Check against dungeon end location instead
        if(below.getType() == Material.CHEST) {
          if (plugin.getCnf().ResetButton()) {
            plugin.Commands(null, new String[]{"reset", name});
          } else if (plugin.getCnf().RecallButton()) {
            teleportToTop(event.getPlayer());
            //plugin.Commands(event.getPlayer(), new String[]{"recall"});
          }
        }
      } else if(plugin.getCnf().ClickIronDoor() && mat == Material.IRON_DOOR_BLOCK) {
        Block below = blk.getRelative(BlockFace.DOWN);
        if(below.getType() == Material.IRON_DOOR_BLOCK) {
          below.setData((byte)(below.getData() ^ 4));
        }
        Block above = blk.getRelative(BlockFace.UP);
        if(above.getType() == Material.IRON_DOOR_BLOCK) {
          blk.setData((byte)(blk.getData() ^ 4)); // The lower block has open/closed bit
        }        
//      } else if(mat == Material.GOLD_BLOCK) {
//        MobTypes types = plugin.getMobtypes();
//        MobType z = types.get("Zombie");
//        if(z != null) {
//          Location loc = getSafePlace(blk);
//          Mob mob = clan.spawnMob(z,loc,true);
//          if(boss == null) {
//            boss = mob;
//          }
//        }
      }

    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onPlayerMove(PlayerMoveEvent event) {
    Block after = event.getTo().getBlock();
    if(plugin.getCnf().NoArmourInDungeon() && !event.isCancelled()) {
      Block before = event.getFrom().getBlock();
      if(!before.equals(after) && isProtected(after)) {   // Player has moved to a new block
        Player player = event.getPlayer();
        PlayerInventory inv = player.getInventory();
        if(inv.getBoots() != null ||
                inv.getChestplate()!=null ||
                inv.getHelmet()!=null ||
                inv.getLeggings()!=null) {
          if(getBlockDepth(after)>0) {
            teleportToTop(player);
            player.sendMessage("You'll need to remove your armour");
          }
        }
      }      
    }
  }
  
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerTeleport(PlayerTeleportEvent event) {
    Player player = event.getPlayer();
    Block from = event.getFrom().getBlock();
    Block to = event.getTo().getBlock();
//    if(isInRaw(to)) {
//      System.out.println("[Catacombs] Teleport to "+name+" "+event.getCause());
//    }
//    if(isInRaw(from)) {
//      System.out.println("[Catacombs] Teleport from "+name+" "+event.getCause());
//    }
    if(event.getCause() == TeleportCause.COMMAND) {
      if(plugin.getCnf().NoTeleportIn() && isProtected(to) &&
              !plugin.hasPermission(player, "catacombs.admin")) {
        player.sendMessage("Teleporting to a place inside a dungeon is disabled");
        event.setCancelled(true);
      } else if(plugin.getCnf().NoTeleportOut() && isProtected(from) &&
              !plugin.hasPermission(player, "catacombs.admin")) {
        player.sendMessage("Teleporting to a place outside a dungeon is disabled");
        event.setCancelled(true);
      }
    }
  }
  /////////////////////////////////////////////////////////////////////////////
  //
  //    Inventory Events
  //
  /////////////////////////////////////////////////////////////////////////////
  
  @EventHandler(priority = EventPriority.LOW)
  public void onInventoryClick(InventoryClickEvent event){
    HumanEntity human = event.getWhoClicked();
    Block blk = human.getLocation().getBlock();
    if(isProtected(blk) &&
            human instanceof Player &&
            plugin.getCnf().NoArmourInDungeon() &&
            getBlockDepth(blk)>0) {
      Player player = (Player) human;
      int slot = event.getSlot();
      if(slot>=36 && slot<=39 && event.getCursor().getType()!=Material.AIR) {
        event.setCancelled(true);
        player.sendMessage("No armour in this dungeon");
      }
    }
  }
  
  /////////////////////////////////////////////////////////////////////////////
  //
  //    Entity Events
  //
  /////////////////////////////////////////////////////////////////////////////
  
  @EventHandler(priority = EventPriority.LOW)
  public void onEntityExplode(EntityExplodeEvent event){
    List<Block> list = event.blockList();  
    if(!event.isCancelled() && any_protected(list)) {
      list.clear();
    }
  }
  
  private Boolean any_protected(List<Block> list) {
    for(Block b : list) {
      if(isProtected(b)) {
        return true;
      }
    }
    return false;
  }
  
  @EventHandler(priority = EventPriority.LOW)
  public void onEntityChangeBlock(EntityChangeBlockEvent event) {
    Block blk = event.getBlock();
    Entity ent = event.getEntity();
    if(isInRaw(blk) && !event.isCancelled() && ent instanceof Enderman) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler(priority = EventPriority.LOW)
  public void onEntityDamage(EntityDamageEvent evt) {
    Entity ent = evt.getEntity();
    Block blk = ent.getLocation().getBlock();
    if(isInRaw(blk) && !evt.isCancelled()) {
      if(plugin.getCnf().NoPvPInDungeon() &&
              ent instanceof Player &&
              CatUtils.getDamager(evt) instanceof Player) {
        evt.setCancelled(true);  
      }
    }
  }
  
  @EventHandler(priority = EventPriority.LOW)
  public void onCreatureSpawn(CreatureSpawnEvent event) {
    Block blk = event.getLocation().getBlock();
    if(isInRaw(blk) && !event.isCancelled()) {
      SpawnReason reason = event.getSpawnReason();
      if(isEnabled!=null && !isEnabled.getBoolean()) {
        event.setCancelled(true);
      } else if(reason == SpawnReason.SPAWNER && blk.getLightLevel()>10) {
        event.setCancelled(true);
      }
      LivingEntity ent = event.getEntity();
      EntityType t = event.getEntityType();
      if(t == EntityType.WOLF) {
        ((Wolf)ent).setAngry(true);
      } else if(t == EntityType.PIG_ZOMBIE) {
        ((PigZombie)ent).setAngry(true);
      }
    }
  }
  
  @EventHandler(priority = EventPriority.LOW)
  public void onEntityDeath(EntityDeathEvent event) {
    LivingEntity damagee = event.getEntity();
    Block blk = damagee.getLocation().getBlock();
    if(isInRaw(blk)) { 
      if(event instanceof PlayerDeathEvent) {  // Player death
        PlayerDeathEvent pde = (PlayerDeathEvent) event;
        Player player = (Player) damagee;
        if(plugin.getCnf().DeathExpKept()>0) // Don't drop any exp if some will be retained.
          pde.setDroppedExp(0);
        int expLevel = player.getLevel();
        pde.setNewLevel((int)(expLevel*plugin.getCnf().DeathExpKept()));
        if(plugin.getCnf().DeathKeepGear()) {
          plugin.getPlayers().saveGear(player);
          pde.getDrops().clear(); // We'll handle the items, don't drop them yet
        }
        if(plugin.getCnf().RespawnInHut()) {
          plugin.getPlayers().setRespawn(player,getTopLocation());
        }
      } else {  // Monster death
        if(plugin.getCnf().MobDropReductionChance()) {
          System.out.println("[Catacombs] Mob drop loot is cancelled");
          event.getDrops().clear();
        }
        if (!plugin.getCnf().GoldOff()) {
          EntityDamageEvent ede = damagee.getLastDamageCause();
          Entity damager = CatUtils.getDamager(ede);
          if (damager instanceof Player) {
            double gold = plugin.getCnf().Gold();
            String bal = CatUtils.giveCash(plugin.getCnf(), damager, gold);
            if (bal != null && gold > 0) {
              ((Player) damager).sendMessage(gold + " coins (" + bal + ")");
            }
          }
        }      
      }
    }    
      
  }  
  
}
