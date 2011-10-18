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

import java.util.Set;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.entity.Player;
import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.steeleyes.maps.Direction;

import com.nijikokun.catacombsregister.payment.Method;
import com.nijikokun.catacombsregister.payment.Methods;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.ArrayList;
import java.util.List;

import com.lennardf1989.catacombsbukkitex.MyDatabase;
import com.avaje.ebean.EbeanServer;
import java.io.File;
import org.bukkit.block.BlockFace;

/**
 * 
 *
 * 
Release v0.7
  - Added /cat scatter <name> <depth> <radius> <max distance> command to plan and
    build dungeons at some distance.
  - Added option to protect spawners inside dungeons from destruction.
  - Added a couple of beds to the hut.
  - Fixed bug, removed extra loot on double chest refills.
  - Changed secret door code so it will work with any block (not just cobble/mossycobble)
  - Added options to allow catacombs to be built from almost any pair of (non-gravity effected
    blocks).
  - Added code to allow special (predefined) rooms to be included in the dungeon
  - Dungeon builds happen over time rather than all at once.
  
Release v0.6
  - A big update to the Catacombs configuration system. The existing configuration
    file (config.yml) should still be ok (except for any custom loot lists you've
    done - sorry not auto converting this time because I mentioned it was temporary
    I will convert if the format changes again).
    Old files will have some redundant variables in it so rname config.yml and regen
    if you can.
  - The chest loot is now fully configurable. See the examples in the config file.
    Create a line for each type of loot you want the chest to possibly contain.
    Each line should consist of 3 parts with ':' between them (no spaces).
    <name of the item>:<percentage chance>:<num>     or
    <name of the item>:<percentage chance>:<min_num>-<max_num>
    The names must be valid bukkit names see here for the names
    http://jd.bukkit.org/apidocs/org/bukkit/Material.html
    The name check is case insensitive. 
  - Added /style to report the dungeon style (default is 'catacomb')
  - Added /style <name> to change to the dungeon planner/loot gen to a different style
    For example if you carefully add the lines below to your config.yml and then
    '/cat style grand' the values below will override the default 'catacomb'
    values where they both exist. Notice even the loot can be customized. Changing
    styles before calling '/cat reset <dungeon>' is handy if you want to define
    several of your own custom loot lists to refill chests with. Be warned the
    style is server wide and resets to 'catacomb' when you log.

grand:
    Archway:
        DoubleWidthPct: 100
    Depth:
        room: 4
    Corridor:
        Width3Pct: 20
        Width2Pct: 80
        Max: 15
        Min: 6
    Room:
        Max: 20
        Min: 8
        Clutter:
            ChestPct: 50
    CorridorPct: 20
    RadiusMax: 40
    Loot:
        Medium:
            List:
            - torch:100:32

  - Added supported economy tools to the softdepend list in plugin.yml
  - Added a configuration option to allow admins to block certain commands when
    players are inside an enabled dungeon. The default list is this:
Admin:
    BannedCommands:
    - /spawn
    - /kill
    - /warp
    - /setwarp
    - /home
 

Release v0.5
  - Removed the need for a MySQL database (moved to a wrapped version of bukkit's
    integrated sqlite and java persistence to save the data - thanks to
    lennardf1989 for making the handy MyDatabase wrapper available).
    Existing MySQL databases will be converted into the new format during the
    first run. After this the existance of the sqlite database will mean the
    MySQL configuration options will be un-used.
    The raw map for each level is now also saved to the database which means that
    in future releases I'll be able to restore webs and mob spawners when
    a dungeon is reset.
  - Part fixed the delete dungeon option. This now works without crashing the client
    by avoiding deleting the chests.
  - Replaced my cash for monsters scheme with 'Register' (Nijikokun) which provides
    support for multiple economy plugins (iConomy4/5/6, EssentialsEco,
    Currency, BOSEconomy6/7). The configuation variable Admin.Economy can be
    set to any of these values to iConomy/BOSEconomy/Essentials/Currency to
    control which one is used if several are present.
  - Added admin configuration to turn off the listener that prevents Endermen
    picking up blocks. The default is 'false' which stops Endermen messing up
    the dungeon (maybe making it impassable).
  - Added a '/cat suspend <name>' command to allow admins to light up a dungeon,
    to stop monsters spawning and to disable the block protection so that
    changes can be made. Also added '/cat enable <name>' to cancel suspend.
  - Added a '/cat which' command to tell you the name of the dungeon you are in.
  - Added a '/cat goto' command to teleport you to the top of the dungeon
  - Added a '/cat recall' command that will teleport players to the top of
    the dungeon if they are within 4 blocks of the final chest on the bottom level.
  - Stopped creepers outside dungeons damaging the dungeon.
  - Fixed a multiworld bug when admins where in a different world to the dungeon
    they were deleting/unprotecting etc
  - Remember to Credit  lennardf1989.bukkitex.MyDatabase for Persistence &
    Nijikokun for Register

Release v0.4
  - Stop Endermen moving blocks in dungeons as they can block corridors/rooms
  - Added CaveSpider spawners
  - Implemented '/cat plan <name> <depth> <radius>' command
  - Added a '/cat resetall' command to reset all dungeons. This is handy on
    servers where some dungeons have been created and the admins want to
    schedule an automatic periodic reset (using a command scheduling plugin).
  - Added crude loot configuration into cconfig.yml (plan on adding more flexible
    scheme as some point in the future).
  - Added code to kick players to the surface if they are in a dungeon being
    reset or deleted.
  - Getting strange client side crashes with bukkit 1185 when deleting dungeons, this
    feature (delete) has been disabled for the moment.
 
Release v0.3
  - Added Soul Sand into the floor in some rooms to slow movement down
  - Increased the number of initial mushrooms in rooms that contain them
  - Integrated with iConomy. If iConomy is present it will add looted coins to
    the users balance. Set "Admin.GoldOff = true" in the configuration to disable
    Monsters in dungeons dropping gold. ...Gold.Min and ...Gold.Max can be used
    to configure the amount of coins dropped (N.B these are currently whole numbers
    not fractions.
  - Added a "/cat reset <name>" command to reset a dungeon. This command closes doors,
    clears torches, refills the chests. Chests in the hut are left unchanged, other
    chests are cleared before being restocked. Mushrooms, spawners and webs aren't
    restocked if they have been destroyed.
  - Added some extra tidy up during dungeon delete. One up shot of this is the
    trap door in the top hut is now destroyed correctly.
  - Changed block place,damage,break routines to integrate better with other plugins
 *
 *
 * TODO list
 *   - Should use a ServerListener for establishing contact with plugins.
 *
 */

public class Catacombs extends JavaPlugin {
  public MultiWorldProtect  prot;
  public CatConfig          cnf;
  public CatPermissions     permissions;
  public Dungeons           dungeons;
  public MyDatabase         database = null;
  public BlockChangeHandler handler;
      
  public PluginDescriptionFile info;
  public Boolean           debug=false;
  public Boolean           enabled= false;
  
  private final CatBlockListener   blockListener   = new CatBlockListener(this);
  private final CatEntityListener  entityListener  = new CatEntityListener(this);
  private final CatPlayerListener  playerListener  = new CatPlayerListener(this);
  private final CatServerListener  serverListener  = new CatServerListener(this);

  @Override
  public void onLoad() {
    cnf = new CatConfig(getConfiguration());
    cnf.checkConfig();
    info = this.getDescription();
    System.out.print("[" + info.getName() + "] version " + info.getVersion()+ " is loaded");
  }
  
  public void onEnable(){
    if(!enabled) {
      permissions = new CatPermissions(this.getServer());
      prot = new MultiWorldProtect();

      if(needToConvert()) {
        if(cnf.SaveDungeons())
          setupDatabase();  
        System.out.print("[" + info.getName() + "] found legacy MySQL database (attempting to convert)");
         CatDatabase sql = new CatDatabase(cnf);
         if(cnf.SaveDungeons()) {
           ConvertMySQL(sql,getDatabase());
           dungeons = new Dungeons(this,getDatabase());  
         } else {
           System.out.print("[" + info.getName() + "] Can't convert database (SaveDungeons is false)");
           dungeons = new Dungeons(this,null);  
         }
      } else {
        if(cnf.SaveDungeons())
          setupDatabase();  
        dungeons = new Dungeons(this,getDatabase());  
      }

      PluginManager pm = this.getServer().getPluginManager();
      if(!cnf.DungeonProtectOff()) {
        pm.registerEvent(Event.Type.BLOCK_PLACE,      blockListener,  Event.Priority.Low, this);
        pm.registerEvent(Event.Type.BLOCK_BREAK,      blockListener,  Event.Priority.Low, this);
      }
      if(!cnf.SecretDoorOff())
        pm.registerEvent(Event.Type.BLOCK_DAMAGE,     blockListener,  Event.Priority.Low, this);
      if(!cnf.GoldOff()) {
        pm.registerEvent(Event.Type.ENTITY_DEATH,     entityListener, Event.Priority.Low, this);
      }
      pm.registerEvent(Event.Type.BLOCK_IGNITE,       blockListener,  Event.Priority.Low, this);
      pm.registerEvent(Event.Type.PLAYER_BUCKET_FILL, playerListener, Event.Priority.Low, this);
      pm.registerEvent(Event.Type.PLAYER_BUCKET_EMPTY,playerListener, Event.Priority.Low, this);
      //pm.registerEvent(Event.Type.BLOCK_PHYSICS,    blockListener,  Event.Priority.High, this);

      //pm.registerEvent(Event.Type.PLAYER_LOGIN,   playerListener, Event.Priority.Low, this);

      pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Event.Priority.Highest, this);

      if(!cnf.CalmSpawns())
        pm.registerEvent(Event.Type.CREATURE_SPAWN, entityListener, Event.Priority.Low, this);
      if(!cnf.MessyCreepers())
        pm.registerEvent(Event.Type.ENTITY_EXPLODE, entityListener, Event.Priority.Low, this);

      if(!cnf.MessyEndermen())
        pm.registerEvent(Event.Type.ENDERMAN_PICKUP, entityListener, Event.Priority.Low, this);

      pm.registerEvent(Event.Type.PLUGIN_ENABLE, serverListener, Event.Priority.Low, this);
      pm.registerEvent(Event.Type.PLUGIN_DISABLE, serverListener, Event.Priority.Low, this);

      handler = new BlockChangeHandler();
      this.getServer().getScheduler().scheduleSyncRepeatingTask(this,handler,40,20);
      
      enabled = true;
    }
  }
  
  public Boolean needToConvert() {
    String maindirectory = "plugins" + File.separator + info.getName();
    File file = new File(maindirectory + File.separator + info.getName() + ".db");
    Boolean there = file.exists();
    //System.out.println("[" + info.getName() + "] file exists? "+there);
    return !there && cnf.MySQLEnabled();
  }
  
  public void ConvertMySQL(CatDatabase sql,EbeanServer db) { 
    if(sql!=null) {

      for (String wname : sql.getWorlds()) {
        System.out.println("[" + info.getName() + "] convert world:"+wname);
        World world = getServer().getWorld(wname);
        if(world == null) {
          System.err.println("[" + info.getName() + "] Can't find the world '"+wname+"' referred to by the MySQL database");
          return;
        }
        for (String dname : sql.getDungeons(wname)) {
          System.out.println("[" + info.getName() + "]           dungeon:"+dname);
          for (CatCuboid cube: sql.getDungeonCubes(dname)) {
            dbLevel lvl = new dbLevel();
            lvl.setLegacy(dname, wname, cube.xl, cube.yl, cube.zl, cube.xh, cube.yh, cube.zh, cube.isHut());
            db.save(lvl);
          }
        }
      }       
    }
  }
  
  public void onDisable(){
    Methods.reset();
    System.out.println("[catacombs] plugin has been disabled!");
    enabled = false;
  }
  
  private void testDatabase() {
    List<dbLevel> list = getDatabase().find(dbLevel.class).where().findList();
    if (list == null || list.isEmpty()) {
      System.out.println("No entries match that pattern");
    } else {
      for(dbLevel cube: list)
        System.out.println("---->"+cube);
    }    
  }
  
  private void setupDatabase() {
    database = new MyDatabase(this) {
      @Override
      protected java.util.List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(dbLevel.class);        
        return list;
      };
    };
    database.initializeDatabase(
      "org.sqlite.JDBC",
      "jdbc:sqlite:{DIR}{NAME}.db",
      "bukkit",
      "walrus",
      "SERIALIZABLE",
      false,
      false
    );    
  }
  
  @Override
  public EbeanServer getDatabase() {
    if(database == null)
      return null;
    return database.getDatabase();
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    if(sender instanceof Player) {
      Player player = (Player) sender;
      return Commands(player,args);
    }
    return false;
  }

  public Boolean Commands(Player p,String [] args) {
    try {
      if(args.length <1) {
        help(p);
      } else if(cmd(p,args,"plan","dn")) {
        int depth = Integer.parseInt(args[2]);
        planDungeon(p,args[1],depth);
      } else if(cmd(p,args,"plan","dnn")) {
        int depth = Integer.parseInt(args[2]);
        int radius = Integer.parseInt(args[3]);
        planDungeon(p,args[1],depth,radius);
      } else if(cmd(p,args,"scatter","dnnn")) {
        int depth = Integer.parseInt(args[2]);
        int radius = Integer.parseInt(args[3]);
        int dist = Integer.parseInt(args[4]);
        scatterDungeon(p,args[1],depth,radius,dist);
      } else if(cmd(p,args,"build","p")) {
        buildDungeon(p,args[1]);
      } else if(cmd(p,args,"list")) {
        p.sendMessage("Catacombs: "+dungeons.getNames());
      } else if(cmd(p,args,"gold")) {
        String pname = p.getName();
        Method meth = Methods.getMethod();
        if(meth != null) {
          double bal = meth.getAccount(pname).balance();
          p.sendMessage("You have "+meth.format(bal)); 
        }
      } else if(cmd(p,args,"delete","D")) {
        deleteDungeon(p,args[1]);
      } else if(cmd(p,args,"reset")) {
        Dungeon dung = dungeons.which(p.getLocation().getBlock());
        if(dung!=null)
          resetDungeon(p,dung.getName());
        else 
          p.sendMessage("Not in a dungeon"); 
      } else if(cmd(p,args,"reset","D")) {
        resetDungeon(p,args[1]);
      } else if(cmd(p,args,"suspend")) {
        Dungeon dung = dungeons.which(p.getLocation().getBlock());
        if(dung!=null) {
          suspendDungeon(p,dung.getName());
          p.sendMessage("Suspend "+dung.getName()); 
        } else 
          p.sendMessage("Not in a dungeon"); 
      } else if(cmd(p,args,"suspend","D")) {
        suspendDungeon(p,args[1]);
        p.sendMessage("Suspend "+args[1]); 
      } else if(cmd(p,args,"enable")) {
        Dungeon dung = dungeons.which(p.getLocation().getBlock());
        if(dung!=null) {
          enableDungeon(p,dung.getName());
          p.sendMessage("Enable "+dung.getName()); 
        }else 
          p.sendMessage("Not in a dungeon"); 
      } else if(cmd(p,args,"enable","D")) {
        enableDungeon(p,args[1]);
        p.sendMessage("Enable "+args[1]); 
      } else if(cmd(p,args,"style")) {
        p.sendMessage("Dungeon style="+cnf.getStyle()); 
      } else if(cmd(p,args,"style","s")) {
        cnf.setStyle(args[1]);
      } else if(cmd(p,args,"resetall")) {
        for(String name : dungeons.getNames()) {
          System.out.println("Reseting dungeon '"+name+"'");
          resetDungeon(p,name);
        }
      } else if(cmd(p,args,"unprot","D")) {
        unprotDungeon(p,args[1]);
      } else if(cmd(p,args,"goto")) {
        Dungeon dung = dungeons.which(p.getLocation().getBlock());
        if(dung!=null) {
          gotoDungeon(p,dung.getName());
          p.sendMessage("Goto "+dung.getName()); 
        }else 
          p.sendMessage("Not in a dungeon"); 
      } else if(cmd(p,args,"goto","D")) {
        gotoDungeon(p,args[1]);
        p.sendMessage("Goto "+args[1]); 
      } else if(cmd(p,args,"end")) {
        Dungeon dung = dungeons.which(p.getLocation().getBlock());
        if(dung!=null) {
          gotoDungeonEnd(p,dung.getName());
          p.sendMessage("Goto end "+dung.getName()); 
        }else 
          p.sendMessage("Not in a dungeon");         
      } else if(cmd(p,args,"end","D")) {
        gotoDungeonEnd(p,args[1]);
        p.sendMessage("Goto end "+args[1]); 
      } else if(cmd(p,args,"recall")) {
        Dungeon dung = dungeons.which(p.getLocation().getBlock());
        if(dung != null) {
          Location ploc = p.getLocation();
          Location eloc = dung.getBotLocation();
          double dist = ploc.distance(eloc);
          if(dist <= 4) {
            gotoDungeon(p,dung.getName());
          } else {
            p.sendMessage("'"+dung.getName()+"' too far from the final chest"); 
          }
        } else {
          p.sendMessage("Not in a dungeon"); 
        }
      } else if(cmd(p,args,"which") || cmd(p,args,"?")) {
        Dungeon dung = dungeons.which(p.getLocation().getBlock());
        if(dung == null) {
          p.sendMessage("Not in a dungeon"); 
        } else {
          p.sendMessage("Dungeon '"+dung.getName()+"'");           
        }
      } else if(cmd(p,args,"test")) {
        debug = !debug;
        Dungeon dung = dungeons.which(p.getLocation().getBlock());
        dung.guessMajor();
        //testDatabase();
        p.sendMessage("[catacombs] Direction "+getCardinalDirection(p));
      } else {
        help(p);
      }
    } catch (IllegalAccessException e) {
      p.sendMessage(e.getMessage());
    } catch (Exception e) {
      p.sendMessage(e.getMessage());
    }
    return true;
  }

  public void help(Player p) {
    p.sendMessage("/cat plan    <name> <#levels> [<radius>]");
    p.sendMessage("/cat scatter <name> <#levels> <radius> <distance>");
    p.sendMessage("/cat build   <name>");
    p.sendMessage("/cat delete  <name>");
    p.sendMessage("/cat unprot  <name>");
    p.sendMessage("/cat suspend [<name>]");
    p.sendMessage("/cat enable  [<name>]");
    p.sendMessage("/cat goto    [<name>]");
    p.sendMessage("/cat end     [<name>]");
    p.sendMessage("/cat reset   [<name>]");
    p.sendMessage("/cat resetall");
    p.sendMessage("/cat recall");
    p.sendMessage("/cat style [<style_name>]");
    p.sendMessage("/cat list");
    p.sendMessage("/cat gold");
  }

  private Boolean cmd(Player player,String [] args,String s) throws IllegalAccessException,Exception {
    return cmd(player,args,s,"");
  }

  private Boolean cmd(Player player,String [] args,String s,String arg_types) throws IllegalAccessException,Exception {
    if(args[0].equalsIgnoreCase(s)) {
      if(!permissions.hasPermission(player, "catacombs."+s)) {
        throw new IllegalAccessException("You don't have permission for /cat "+s);
      }
      if(args.length != arg_types.length()+1) {
        return false;
      }
      for(int c=0;c<arg_types.length();c++) {
        String arg = args[c+1];
        char t = arg_types.charAt(c);
        if(t == 'D') {           // Built Dungeon
          if(!dungeons.isBuilt(arg)) {
            throw new Exception("'"+arg+"' is not a built dungeon");
          }
        } else if(t == 'p') {    // Planned Dungeon (ready to be build)
          if(!dungeons.exists(arg)) {
            throw new Exception("'"+arg+"' is not a planned dungeon");
          }
          if(dungeons.isBuilt(arg)) {
            throw new Exception("'"+arg+"' has already been built");
          }
          if(!dungeons.isOk(arg)) {
            throw new Exception("'"+arg+"' had issue during planning");
          }
        } else if(t == 'd') {    // Non existing dungeon
          if(dungeons.isBuilt(arg)) {
            throw new Exception("'"+arg+"' is already built");
          } 
        } else if(t == 's') {    // A string

        } else if(t=='n') {     // A number
          try {
            Integer.parseInt(arg);
          } catch (NumberFormatException e) {
            throw new Exception("Expecting arg#"+(c+1)+" to be a number (found '"+arg+"')");
          }
        }
      }
      if(debug)
        System.out.println("Player:"+player.getName()+" Command:"+args[0]);
      return true;
    }
    return false;
  }

  public void planDungeon(Player p,String dname, int depth, int radius) {
    int tmp_radius = cnf.RadiusMax();
    cnf.setRadiusMax(radius);
    planDungeon(p,dname,depth);
    cnf.setRadiusMax(tmp_radius);
  }

  public void planDungeon(Player p,String dname, int depth) {
    Location loc = p.getLocation();
    Block blk = loc.getBlock();
    Dungeon dung = new Dungeon(dname,cnf,p.getWorld());
    dung.prospect(p,blk.getX(),blk.getY(),blk.getZ(),getCardinalDirection(p),depth);
    dung.show();
    dungeons.add(dname,dung);
    for(String msg : dung.summary())
      p.sendMessage(msg);
    if(dung.isOk())
      p.sendMessage("'"+dname+"' is good and ready to be built");
    else
      p.sendMessage("'"+dname+"' is incomplete (usually too small for final room/stair)");
  }
  
  public void scatterDungeon(Player p, String dname,int depth, int radius, int distance) {
    Block start_blk = p.getLocation().getBlock();
    World world = start_blk.getWorld();
    
    int x,z;
    Block safe_blk = null;
    Dungeon dung = new Dungeon(dname,cnf,world);
    int att1=0;
    do {
      int att2 = 0;
      do {  // Attempts to find a natural surface block to start from
        x = start_blk.getX()+cnf.nextInt(distance<<1)-distance;
        z = start_blk.getZ()+cnf.nextInt(distance<<1)-distance;
        Location loc = world.getBlockAt(x,start_blk.getY(),z).getLocation();
        safe_blk = world.getHighestBlockAt(loc).getLocation().getBlock();
        safe_blk = safe_blk.getRelative(BlockFace.DOWN);
        if(!CatCuboid.isBlockNatural(safe_blk))
          safe_blk = null;
        att2++;
      } while (safe_blk == null && att2 < 20);
      if(safe_blk!=null) { // Now see if it's a good dungeon location
        Direction dir = Direction.any(cnf.rnd);
        int mx = safe_blk.getX()+dir.dx(3);
        int my = safe_blk.getY()+1;
        int mz = safe_blk.getZ()+dir.dy(3);
        
        int tmp_radius = cnf.RadiusMax();
        cnf.setRadiusMax(radius);
        dung.prospect(p,mx,my,mz,dir,depth);
        cnf.setRadiusMax(tmp_radius);
        if(dung.isOk()) {  // TODO: Need to call buildDungeon in here
          dung.show();
          dungeons.add(dname,dung);
          dung.saveDB(getDatabase());
          dung.registerCubes(/*sql,*/prot);
          dung.render(handler);
          for(String msg : dung.summary())
            p.sendMessage(msg);
          p.sendMessage("'"+dname+"' has been built");
        }
      }
      att1++;

    } while(!dung.isOk() && att1 < 30);
    if(!dung.isOk()) {
      p.sendMessage("Failed to find a good location for dungeon '"+dname+"'");
    }
  
  }
  
  public void buildDungeon(Player p,String dname) {
    if(dungeons.exists(dname)) {
      Dungeon dung = dungeons.get(dname);
      if(dung.isBuilt()) {
        System.out.println("[catacombs] Dungeon "+dname+" has already been built");
      } else if(!dung.isNatural()) {
        p.sendMessage("Loaction of '"+dname+"' is no longer solid-natural (replan)");
      } else {
        p.sendMessage("Building "+dname);
        dung.saveDB(getDatabase());
        dung.registerCubes(/*sql,*/prot);
        dung.render(handler);
        handler.add(p);
      }
    } else {
      p.sendMessage("Dungeon "+dname+" doesn't exist");
    }
  }

  public void suspendDungeon(Player p,String dname) {
    dungeons.suspend(dname,getDatabase());
  }
  public void gotoDungeon(Player p,String dname) {
    Dungeon dung = dungeons.get(dname);
    dung.teleportToTop(p); 
  }
  public void gotoDungeonEnd(Player p,String dname) {
    Dungeon dung = dungeons.get(dname);
    dung.teleportToBot(p); 
  }  
  public void enableDungeon(Player p,String dname) {
    dungeons.enable(dname,getDatabase());
  }  
  
  public void deleteDungeon(Player p,String dname) {
    Dungeon dung = dungeons.get(dname);
    Set<CatCuboid> cubes = dung.getCubes();
    dung.allPlayersToTop();  // Everyone to the top, the dungeon is going away
    for(CatCuboid c: cubes) {
      //c.clearPlayers();
      c.clearMonsters();
    }
    for(CatCuboid c: cubes) {
      c.unrender(handler,cnf.emptyChest());
    }
    handler.add(p);
    dungeons.remove(dname,prot,getDatabase());
  }
  
  public void resetDungeon(Player p,String dname) {
    Dungeon dung = dungeons.get(dname);
    Set<CatCuboid> cubes = dung.getCubes();
    dung.allPlayersToTopProt();  // Players to top only to top if enabled
    for(CatCuboid c: cubes) {
      if(!c.isHut()) {
        c.clearBlock(Material.TORCH);
        c.refillChests(cnf);
      }
      //c.clearPlayers();
      c.clearMonsters();
      c.closeDoors();
    }
  }
  
  public void unprotDungeon(Player p,String dname) {
    dungeons.remove(dname,prot,getDatabase());
  }

  public static Direction getCardinalDirection(Player p) {
      float y = p.getLocation().getYaw();
      if( y < 0 ){y += 360;}
      y %= 360;
      return getDirection(y);
  }

  private static Direction getDirection(double rot) {
    if (0 <= rot && rot < 45.0) {
      return Direction.SOUTH;
    } else if (45.0 <= rot && rot < 135.0) {
      return Direction.WEST;
    } else if (135.0 <= rot && rot < 225.0) {
      return Direction.NORTH;
    } else if (225.0 <= rot && rot < 315.0) {
      return Direction.EAST;
    } else if (315.0 <= rot && rot < 360.0) {
      return Direction.SOUTH;
    } else {
      return null;
    }
  }
}
