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

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.entity.Player;
import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.entity.Entity;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import net.steeleyes.maps.Direction;

import org.bukkit.plugin.PluginDescriptionFile;

import java.util.List;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import org.bukkit.block.BlockFace;

/**
 * 
Release v2.1
* Added configuration option for random anvils in dungeons.
* Added work around for client side crash when chests in dungeons are deleted.
* Fixed code so beds in dungeons are deleted correctly without dropping a bed.
* Added work around for light levels not updating when the torches are removed
  during a reset and no players are nearby.

Release v2.0
* Added an option to allow the button at the end to recall the presser rather
  than resetting the dungeon. If both reset and recall options are true then the
  reset wins.
* Fixed an issue in the order the permissions plugins were being loaded relative
  to Catacombs.
* Re-enabled replacing chests when dungeons get deleted. This will cause a client
  side crash due to a bucket/minecraft problem, but it's probably better to do this
  than leave the empty chests buried underground.
* Updated deprecated function calls

Release v1.9
* Fixed some issues that occurred when players died in dungeons and the code tried
  to restore their gear. These issues were caused by changes in the way bukkit
  handled empty slots [you used to get Material.AIR returned now you more sensibly get
  a null pointer]
 
Release v1.8
* Fixed the code so deleted and unprotected dungeons really do get removed from the
  database.
* Minecraft 1.2.3 change the way doors work. In Catacombs I fixed the hinge
  positions for double doors in new dungeons and added code to go around at
  start up and fix any existing legacy dungeons. Any open doors will be closed
  by this operation. Also changed the reset door closing code to function correctly
  with the new scheme.
   
Release v1.7
* Changed the default wepif.yml file that gets created so that DinnerBone's permission
  handler (aka Bukkit permissions API) is off by default. It will not change wepif.yml
  files that already exist, so if you are not running worldedit and you'd
  like Catacombs to create a new file for you, remove the old one and restart the
  server. This should help protect users with simpler setups from running into
  permissions problems. Admins actually using Bukkit permissions API will need
  to enable it manually in the wepif.yml file.

Release v1.6
* Fixed a null pointer exception you get when all the permission resolvers are disabled.

Release v1.5
* Cake blocks are now restored on fences with air above them on reset. Partially
  eaten cake is also restored to full.
* Added 2 additional block types to the config file to control the floor and ceiling
  blocks. For legacy dungeons these will default to the major block. In the config
  file the default for both of these will be cobblestone for new dungeons.
* Prevented grass spreading out from under the wolf spawners across a dirt floor.
* Added feature that should allow torches to be placed in dungeons even when they
  are protected by other plugins (like worldguard)
* Updated my permissions handler to use sk89q's WEPIF permissions handler code.
  Hopefully this will improve integration with permissions handlers in general.
* Changed player death options so they function in dungeons even without the
  AdvancedCombat option being set.
* Updated the Event code to the new bukkit style. This makes the following admin
  configurations options now obsolete (because there is no time to be saved by not
  calling the listeners)
  MessyCreepers, MessyEndermen, SecretDoorOff, CalmSpawns & DungeonProtectOff
* Record the world name and the dungeon start coordinate in the map files from now on.
* Updated the Enderman pickup and place events to use the latest versions of the
  bukkit calls (EntityChangeBlockEvent).
  
Release v1.4
* Added extra code to fix any broken secret doors at start up.
* Fixed a bug with the MobsOnlySpawnUnderground option (however mobs will still
  spawn in forests and cave mouthes so be warned).
* Added code to allow small amounts (fractions) if cash to be given for monster kills
 
Release v1.3
* Fixed potion loot so that splash potions can be created. The syntax is
  like this potion/16425:10:1 (this means a 10% chance of splash potion of
  strength II).
* The first room of each level can now be a special room
* Added code to allow special rooms to be placed in any of 4 orientations.
* Changed code stairs down re-use the special room code, allowing more interesting
  final rooms in the future.
* Added config percentage for Creeper spawners
* Added option to control PvP damage inside dungeons (by default it is off).
* Fixed Advanced combat bug when TNT explodes near a player.
* Fixed bugs caused by dungeons in worlds that no longer exist.
* Added config option to cancel most monsters spawning on the surface
  (monsters will still spawn under trees, overhangs and in caves or dungeons).
* Added config option to cancel all monsters spawns except those in dungeons.
* Added config option to prevent any ore displaced by the dungeon level
  from being collected in the/a medium chest on the same level.
* Fix plugin clash with WorldGuard that stopped secret doors working
  in dungeons that are worldguard protected.
* Changed the code so empty and filling buckets is only prevented in protected dungeons
* Big tidy up on the dungeon protection code, made suspend/enable an attribute of
  the dungeon rather than the level. In theory this shouldn't have changed the
  functionality but it's possible I might have broken something.
* Added timed reset feature for dungeons. '/cat time <name> <time>' The command
  changes the reset timer for the named dungeon (or the dungeon you are in).
  The times are specified as sequence of numbers followed by letters (s=second,
  m=minute,h=hour,d=day). For example '/cat time dungeon1 5h30m' means dungeon1
  will be reset every 5 and a half hours (starting from now). The time can also
  be a range, so 5h30m-6h30m would set the dungeon up to reset every 5.5 to 6.5 hours.
  The automatic reset is disabled by setting the reset time to zero (0s or 0m or 0h or 0d)
* Made some changes to hopefully avoid some secret door operation issues.
* Levers in dungeons now get cleared too when a dungeon is reset.
 
Release v1.2
* Added chance of finding enchanting tables (with book cases)
* Added potion making stands on the top of work benches.
* Worked around bug with tough AdvancedCombat creatures getting killed in one shot
  by enchanted weapons.
* Added a '/cat unplan <name>' command to discard a planned dungeon that hasn't
  been built. This is useful when unbuilt dungeons can't be built because they overlap.
* Fixed code so cash isn't give for kills outside the dungeon.
* Added options to automatically charge players to keep their gear on them when
  they die in dungeons. This saves cluttering the dungeons and everybody having
  to swap and sort out equipment every time anybody dies.
* 85% of experience levels are now saved for player deaths in dungeons.
* Fixed an AdvancedCombat bug there healers were able to heal dead players.
* Added taunt and aggro reducing moves to AdvancedCombat
* Sorted out some item durability balances for AdvancedCombat
* Fixed threat transfer bug on player death
* Added some more special rooms and fixed a couple of minor mapping bugs
* Added silverfish spawners (disabled them - don't have enough bukkit hooks yet)
* Removed legacy support for old MySQL databases (people have had plenty of
  time to convert to the new format), also removed use of lennardf1989 handy
  MyDatabase wrapper. There's nothing wrong with the wrapper itself it just
  seems much easier to work directly with sqlite (or MySQL) than the javax
  persistence routines. Now this is gone it is easier to save extra info
  to the database (which I'll be doing in v1.3).
* Prevented gold messages when players get 0 gold. Made the AdvancedCombat
  rewards obey the GoldOff configuration.

Release v1.1
* Added option to allow Advanced Combat to be enabled.

Release v1.0
* Added an option for blaze spawners
* Changed the default options so that secret doors will only work in dungeons by default
* Fixed the code so that dungeon resets don't reset secret doors when they
  are disabled in the config file.
* Added a style called 'grand' to the default config file that builds dungeons with
  wider corridors and larger rooms (more suitable for groups).
  Use '/cat style grand' to select this, then plan and build as normal.
* Added a new syntax to allow items with byte codes (like dyes and potions) to
  be given as loot. Put a '/' after the item name followed by the byte code
  e.g 'potion/1:10:1' would give one regen potion in 10% of the chests
* Added a '/cat reload' command to reload the config file in game (or from the console).
* Fixed the code so that arrow kills get rewarded too.
* Turned off a rather verbose console message that occurred when new options were being
  added to the config file.

Release v0.9
* Fixed an in-game Null pointer exception during planning caused by special end rooms
* Fixed bug which caused lava to be able to ignite things in dungeons
* Fixed problem getting stuck in walls when teleporting to the dungeon end
* Fixed problem with blocks getting messed up (byte code was lost) when reseting secret doors
* Fixed serious bug in the dungeon overlap checking code (now also reports offending dungeon)
* Confirmed spawner protection config option works. Set this to 'true' to force players to
  place torches around spawners to disable them rather than removing them.
* Converted code to use bukkit's new configuration code to avoid deprecated warnings during compile
* Dungeon maps now get saved as text files when dungeons are built
* Added a series of debug commands to help identify user problems (list <name>, dump, map)
* '/cat which' or '/cat ?' will now report the name of the dungeon in the crosshairs
  if you aren't in a dungeon (and it's close enough).
* Integrated lots of code related to boss mobs that will guard the final chest
  currently disabled while I run some more tests on it (due in v1.0)

 * 
 **/

/**
Release v0.8
* Added arrow traps in random locations. Contents of the dispensers is configurable
* Added configuration to totally fill the area above the ceiling with dungeon blocks
* Added configuration to allow a dungeon reset button at the end of the dungeon
* Updates to allow most commands to be sent from the console (plan, scatter and gold
  commands need to be done in game at present).
* Added configuration to allow control over which blocks are breakable (these settings
  are server wide - not per dungeon). The default list contains torch,web and mushrooms.
 
* Added code to ensure chunks are loaded/fresh to solve teleport problems.
* Stopped Endermen placing blocks (only picking up blocks was blocked previously)

Release v0.7
  - Added a couple of beds to the hut.
  - Fixed bug, removed extra loot on double chest refills.
  - Changed secret door code so it will work with any block (not just cobble/mossycobble)
  - Changed suspend/enable dungeon commands so they work with ceilings that aren't cobble/mossycobble
  - Added options to allow catacombs to be built from almost any pair of (non-gravity effected
    blocks). These are configured as part of the style in config.yml.
    Choose the blocks carefully. Don't come crying to me when the secret doors in
    your melon/pumpkin dungeon don't work or when your sand dungeons collapse.
    To control data values use "wool:13", "smooth_brick:2" etc for blue wool or
    cracked bricks respectively. The names must be valid bukkit names see here for
    the names http://jd.bukkit.org/apidocs/org/bukkit/Material.html The name check
    is not case sensitive. 
  - Added code to allow special (predefined) rooms to be included in the dungeon.
  - Added configurations so you can choose the size of "Hut" you get at the top
    of the dungeon. Current options are default, small, tiny, pit, medium, large
  - Dungeon builds happen over time rather than all at once.
  - Added configuration to allow admins to control which blocks are considered
    natural. For example. If you add 'air' to the list (set the floor depth to 4
    or set the UnderFill configuration to make sure you have room for secret doors)
    you can build down from the sky, for example.
  - Added '/cat end <name>' to teleport admins to the end of the dungeon
  - Changed reset/enable/suspend/goto/end commands so they default to the dungeon
    you are in if a dungeon name isn't given.
  - Changed '/cat list' so it lists the dungeons in alphabetical order.
  - Changed code so delete and reset commands teleport any players inside the dungeon
    up into the relative safety of the hut first (not the surface as before)
  - Added checks to make sure dungeons can't be build that intersect
  - Added checks to ensure dungeons are still all natural when you build them
  - Uploaded the source code to https://github.com/Blockhead2/Catacombs
  - Added code to prevent water and lava being picked up or put down in dungeon
  - Added code to prevent lava pools burning your woolen or wood dungeons down.
  - *EXPERIMENTAL* Added /cat scatter <name> <depth> <radius> <max distance> command to plan and
    build dungeons at some distance. I got stuck when teleporting to them after
    they were built, maybe ok if you walk. YOU HAVE BEEN WARNED EMPTY YOUR INVENTORY
  - *EXPERIMENTAL* Added option to protect spawners inside dungeons from destruction.
    I'm cancelling the event but the spawners are still breaking (need to debug)
  - Known bukkit/minecraft issue. Client will crash if you build over a chest.
  
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
 *
 */

public class Catacombs extends JavaPlugin {
  public  CatConfig             cnf;
  private CatPermissions        permissions;
  public  Dungeons              dungeons;
  public  CatSQL                sql=null;
  private BlockChangeHandler    handler;
  private DungeonHandler        dhandler;

  public  Monsters              monsters;
  public  Players               players = new Players();
  //public  MobTypes              mobtypes;

  public  PluginDescriptionFile info;
  public  Boolean               debug=false;
  private Boolean               enabled= false;
  
  private File mapdir;
    
  private final CatBlockListener   blockListener   = new CatBlockListener(this);
  private final CatEntityListener  entityListener  = new CatEntityListener(this);
  private final CatPlayerListener  playerListener  = new CatPlayerListener(this);
  private Economy economy;

  @Override
  public void onLoad() {
    cnf = new CatConfig(getConfig());
    info = this.getDescription();
    //mobtypes = new MobTypes(getConfig());
    monsters = new Monsters(this);
    
    mapdir = new File("plugins" + File.separator + info.getName() + File.separator + "maps");
    if(!mapdir.exists()){
      mapdir.mkdir();
    }
  }
  
  @Override
  public void onEnable(){
    if(!enabled) {
      //permissions = new CatPermissions(this.getServer());
      permissions = new CatPermissions(this);

      if(cnf.SaveDungeons())
        setupDatabase();  
      dungeons = new Dungeons(this,sql); 
      if(cnf.noFlag("Admin.Patches.DoorFixDone")) {
        System.out.println("[" + info.getName() + "] Fixing Wooden/Iron doors (for MC 1.2.3 and above)");
        dungeons.fixDoors();
      }

      PluginManager pm = this.getServer().getPluginManager();
      pm.registerEvents(blockListener, this);
      pm.registerEvents(entityListener, this);
      pm.registerEvents(playerListener, this);

      handler = new BlockChangeHandler(this);
      this.getServer().getScheduler().scheduleSyncRepeatingTask(this,handler,40,20);

      dhandler = new DungeonHandler(this);
      this.getServer().getScheduler().scheduleSyncRepeatingTask(this,dhandler,40,20);
     
      // Clear the dungeons of mobs so we can manage those that spawn
      dungeons.clearMonsters(this);
      
      if(cnf.noFlag("Admin.Patches.SecretDoorFixDone")) {
        System.out.println("[" + info.getName() + "] Fixing Secret doors");
        dungeons.fixSecretDoors();
      }
      enabled = true;

      if (!setupEconomy()) {
        System.out.println("[" + info.getName() + "] No economy system found.");
      }
    }
  }

  private boolean setupEconomy() {
    if (getServer().getPluginManager().getPlugin("Vault") == null) {
      return false;
    }
    RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
    if (rsp == null) {
      return false;
    }
    economy = rsp.getProvider();
    return economy != null;
  }
  
  @Override
  public void onDisable(){
    enabled = false;
  }
  
  private void setupDatabase() {
    sql = new CatSQL("plugins"+File.separator+"Catacombs"+File.separator+"Catacombs.db");
    
    Boolean hasLevels = sql.tableExists("levels");
    Boolean hasDungeons = sql.tableExists("dungeons");
    sql.createTables();
    if(hasLevels && !hasDungeons) {
      System.out.println("[Catacombs] Converting old dungeon data to new format (slow)");
      sql.Convert2(this);
    }
  }  

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    if(sender instanceof Player) {
      Player player = (Player) sender;
      return Commands(player,args);
    }
    return Commands(null,args);
  }

  public Boolean Commands(Player p,String [] args) {
    if(false && debug) {
      if(p==null) {
        System.out.println("[Catacombs] Command sent from console");
      } else {
        System.out.println("[Catacombs] Command sent by player("+p.getName()+")");
      }
      System.out.println("[Catacombs] number of arguments="+args.length);
      int i=1;
      for(String s:args) {
        System.out.println("[Catacombs]   argument "+i+" is '"+s+"'");
      }
    }
    try {
      if(args.length <1) {
        help(p);
        
      // PLAN ********************************************************  
      } else if(cmd(p,args,"plan","dn")) {
        int depth = Integer.parseInt(args[2]);
        planDungeon(p,args[1],depth);
      } else if(cmd(p,args,"plan","dnn")) {
        int depth = Integer.parseInt(args[2]);
        int radius = Integer.parseInt(args[3]);
        planDungeon(p,args[1],depth,radius);
        
      // SCATTER ********************************************************  
      } else if(cmd(p,args,"scatter","dnnn")) {
        int depth = Integer.parseInt(args[2]);
        int radius = Integer.parseInt(args[3]);
        int dist = Integer.parseInt(args[4]);
        scatterDungeon(p,args[1],depth,radius,dist);
        
      // UNPLAN ********************************************************  
      } else if(cmd(p,args,"unplan","p")) {
        if(dungeons.exists(args[1]))
          dungeons.remove(args[1]);
                
      // BUILD ********************************************************  
      } else if(cmd(p,args,"build","p")) {
        buildDungeon(p,args[1]);
        
      // LIST ********************************************************  
      } else if(cmd(p,args,"list")) {
        inform(p,"Catacombs: "+dungeons.getNames());
      } else if(cmd(p,args,"list","D")) {
        List<String> i = dungeons.getinfo(args[1]);
        for(String s: i) {
          System.out.println(s);
        }
        //inform(p,"Catacombs: "+dungeons.getNames());
        
      } else if(cmd(p,args,"dump","D")) {
        File f = new File("plugins" + File.separator + "Catacombs","dmp_"+args[1]+".txt");
        FileWriter fstream = new FileWriter(f);
        BufferedWriter out = new BufferedWriter(fstream);
        for(String s: dungeons.dump(args[1])) {
          out.write(s+"\r\n");
        }
        out.close();
        
      // MAP ********************************************************
      } else if(cmd(p,args,"map")) {
        Dungeon dung = getDungeon(p);
        if(dung!=null) {
          for(String s: dung.map()) {
            System.out.println(s);
          }
        } 
      } else if(cmd(p,args,"map","D")) {
        for(String s: dungeons.map(args[1])) {
          System.out.println(s);
        }

      // GOLD ********************************************************  
      } else if(cmd(p,args,"gold")) {
        if(p!=null) {
          String pname = p.getName();
          if(economy != null) {
            double bal = economy.getBalance(pname);
            inform(p,"You have "+economy.format(bal)); 
          }
        }
        
      // DELETE  ********************************************************
      } else if(cmd(p,args,"delete","D")) {
        deleteDungeon(p,args[1]);
        
      // RESET  ********************************************************
      } else if(cmd(p,args,"reset")) {
        Dungeon dung = getDungeon(p);
        if(dung!=null) {
          resetDungeon(p,dung.getName());
          inform(p,"Reset "+dung.getName()); 
        }  
      } else if(cmd(p,args,"reset","D")) {
        resetDungeon(p,args[1]);
        inform(p,"Reset "+args[1]); 
      
      // SUSPEND  ********************************************************
      } else if(cmd(p,args,"suspend")) {
        Dungeon dung = getDungeon(p);
        if(dung!=null) {
          suspendDungeon(p,dung.getName());
          inform(p,"Suspend "+dung.getName()); 
        } 
      } else if(cmd(p,args,"suspend","D")) {
        suspendDungeon(p,args[1]);
        inform(p,"Suspend "+args[1]); 
        
      // ENABLE  ********************************************************
      } else if(cmd(p,args,"enable")) {
        Dungeon dung = getDungeon(p);
        if(dung!=null) {
          enableDungeon(p,dung.getName());
          inform(p,"Enable "+dung.getName()); 
        }
      } else if(cmd(p,args,"enable","D")) {
        enableDungeon(p,args[1]);
        inform(p,"Enable "+args[1]); 
        
      // TIME  ********************************************************
      } else if(cmd(p,args,"time","t")) {
        Dungeon dung = getDungeon(p);
        if(dung!=null) {
          setResetMinMax(p,dung.getName(),args[1]);
          inform(p,dung.getName()+" reset in "+args[1]); 
        }
      } else if(cmd(p,args,"time","Dt")) {
        setResetMinMax(p,args[1],args[2]);
        inform(p,args[1]+" reset in "+args[2]); 
              
      // STYLE  ********************************************************
      } else if(cmd(p,args,"style")) {
        inform(p,"Dungeon style="+cnf.getStyle()); 
      } else if(cmd(p,args,"style","s")) {
        cnf.setStyle(args[1]);
        inform(p,"Dungeon style="+cnf.getStyle()); 
        
      // RESETALL ******************************************************** 
      } else if(cmd(p,args,"resetall")) {
        for(String name : dungeons.getNames()) {
          inform(p,"Reseting dungeon '"+name+"'");
          resetDungeon(p,name);
        }
        
      // UNPROT  ********************************************************
      } else if(cmd(p,args,"unprot","D")) {
        unprotDungeon(p,args[1]);
        
      // GOTO  ********************************************************
      } else if(cmd(p,args,"goto")) {
        Dungeon dung = getDungeon(p);
        if(dung!=null) {
          gotoDungeon(p,dung.getName());
          inform(p,"Goto "+dung.getName()); 
        } 
      } else if(cmd(p,args,"goto","D")) {
        gotoDungeon(p,args[1]);
        inform(p,"Goto "+args[1]); 
        
      // END  ********************************************************
      } else if(cmd(p,args,"end")) {
        Dungeon dung = getDungeon(p);
        if(dung!=null) {
          gotoDungeonEnd(p,dung.getName());
          inform(p,"Goto end "+dung.getName()); 
        } 
      } else if(cmd(p,args,"end","D")) {
        gotoDungeonEnd(p,args[1]);
        inform(p,"Goto end "+args[1]); 
        
      // RECALL ********************************************************  
      } else if(cmd(p,args,"recall")) {
        Dungeon dung = getDungeon(p);
        if(dung != null) {
          Location ploc = p.getLocation();
          Location eloc = dung.getBotLocation();
          double dist = ploc.distance(eloc);
          if(dist <= 4) {
            gotoDungeon(p,dung.getName());
          } else {
            inform(p,"'"+dung.getName()+"' too far from the final chest"); 
          }
        }
        
      // WHICH  
      } else if(cmd(p,args,"which") || cmd(p,args,"?")) {
        Dungeon dung = getDungeon(p);
        if(dung!=null)
          inform(p,"Dungeon '"+dung.getName()+"'"); 
        
      // RELOAD  
      } else if(cmd(p,args,"reload")) {
        cnf = new CatConfig(getConfig());
        inform(p,"config reloaded");
        
      // TEST  
      } else if(cmd(p,args,"test")) {
        debug = !debug;
        //Location loc = p.getLocation();
        //Block blk = loc.getBlock();
        //Block surface = p.getWorld().getHighestBlockAt(loc);
        //System.out.println("[Catacombs] "+blk+"("+blk.getType()+") "+surface+"("+surface.getType()+")");
        //Dungeon dung = dungeons.which(p.getLocation().getBlock());
        //dung.saveDB();

        //Dungeon dung = dungeons.which(p.getLocation().getBlock());
        //dungeons.fixWoodenDoors();
        //dung.guessMajor();
        //testDatabase();
        //p.sendMessage("[catacombs] Direction "+getCardinalDirection(p));
        //inform(p,"[catacombs] Direction "+getCardinalDirection(p));
        

      // DEBUG
      } else if(cmd(p,args,"debug")) {
        //Dungeon dung = dungeons.which(p.getLocation().getBlock());
        //dung.buildWindows(Material.IRON_FENCE);
      } else {
        help(p);
      }
    } catch (IllegalAccessException e) {
      inform(p,e);
    } catch (Exception e) {
      inform(p,e);
    }
    return true;
  }
  
  public Dungeon getDungeon(Player p) {
    if(p==null) {
      inform(p,"Need to specify the dungeon by name in the console");
    } else {
      Dungeon dung = dungeons.which(p.getLocation().getBlock());
      if(dung!=null)
        return dung;
      dung = dungeons.which(p.getTargetBlock(null, 1000));
      if(dung!=null)
        return dung;
      inform(p,"Not in a dungeon (or not looking at one)");    
    }
    return null;
  }

  public void help(Player p) {
    inform(p,"/cat scatter <name> <#levels> <radius> <distance>");
    inform(p,"/cat unplan  <name>");
    inform(p,"/cat delete  <name>");
    inform(p,"/cat unprot  <name>");
    inform(p,"/cat suspend [<name>]");
    inform(p,"/cat enable  [<name>]");
    inform(p,"/cat goto    [<name>]");
    inform(p,"/cat time    [<name>] <time>");
    inform(p,"/cat end     [<name>]");
    inform(p,"/cat reset   [<name>]");
    inform(p,"/cat resetall");
    inform(p,"/cat recall");
    inform(p,"/cat ?");
    inform(p,"/cat style [<style_name>]");
    inform(p,"/cat list");
    inform(p,"/cat gold");
    inform(p,"/cat plan    <name> <#levels> [<radius>]");
    inform(p,"/cat build   <name>");
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
          
        } else if(t == 't') {    // A time
          if(!arg.matches("^(\\d+[smhd])+$")&& !arg.matches("^(\\d+[smhd])+-(\\d+[smhd])+$")) {
            throw new Exception("Expecting arg#"+(c+1)+" to be days/hours/mins/secs (e.g 10m5s) (found '"+arg+"')");
          }
        } else if(t=='n') {     // A number
          try {
            Integer.parseInt(arg);
          } catch (NumberFormatException e) {
            throw new Exception("Expecting arg#"+(c+1)+" to be a number (found '"+arg+"')");
          }
        }
      }
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
    if(p==null) {
      inform(p,"You can't plan from the console");
      return;
    }
    Location loc = p.getLocation();
    Block blk = loc.getBlock();
    Dungeon dung = new Dungeon(this,dname,p.getWorld());
    dung.prospect(p,blk.getX(),blk.getY(),blk.getZ(),getCardinalDirection(p),depth);
    dung.show();
    dungeons.add(dname,dung);
    inform(p,dung.summary());
    if(dung.isOk()) {
      Dungeon overlap = dungeons.getOverlap(dung);
      if(overlap != null) {
        inform(p,"'"+dname+"' overlaps dungeon '"+overlap.getName()+"' (replan or remove it)");
      } else {
        inform(p,"'"+dname+"' is good and ready to be built");
        //dung.saveMap(mapdir + File.separator + dname + ".map");
      }
    } else
      inform(p,"'"+dname+"' is incomplete (usually too small for final room/stair)");
  }
  
  public void scatterDungeon(Player p, String dname,int depth, int radius, int distance) {
    if(p==null) {
      inform(p,"Can't scatter from console at the moment (will need to supply world name)");
      return;
    }
    Block from = p.getLocation().getBlock();
    scatterDungeon(from,p,dname,depth,radius,distance);
  }
  
  private void scatterDungeon(Block from,Player p, String dname,int depth, int radius, int distance) {
    World world = from.getWorld();
    
    int x,z;
    Block safe_blk = null;
    Dungeon dung = new Dungeon(this,dname,world);
    int att1=0;  // Outer loop attempts
    do {
      int att2 = 0;  // Inner loop attempts
      do {  // Attempts to find a natural surface block to start from
        x = from.getX()+cnf.nextInt(distance*2)-distance;
        z = from.getZ()+cnf.nextInt(distance*2)-distance;
        Location loc = world.getBlockAt(x,from.getY(),z).getLocation();
        safe_blk = world.getHighestBlockAt(loc).getLocation().getBlock();
        safe_blk = safe_blk.getRelative(BlockFace.DOWN);
        if(!cnf.isNatural(safe_blk))
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
        if(dung.isOk()) {
          dung.show();
          dungeons.add(dname,dung);
          buildDungeon(p,dname);
          inform(p,dung.summary());
          inform(p,"'"+dname+"' has been built");
        }
      }
      att1++;

    } while(!dung.isOk() && att1 < 30);  // Try another location if it's no good
    
    
    if(!dung.isOk()) {
      inform(p,"Failed to find a good location for dungeon '"+dname+"'");
    }
  }
  
  public void buildDungeon(Player p,String dname) {
    if(dungeons.exists(dname)) {
      Dungeon dung = dungeons.get(dname);
      if(dung.isBuilt()) {
        inform(p,"Dungeon "+dname+" has already been built");
        return;
      }
      if(!dung.isNatural()) {
        inform(p,"Loaction of '"+dname+"' is no longer solid-natural (replan)");
        return;
      }
      Dungeon overlap = dungeons.getOverlap(dung);
      if(overlap!=null) {
        inform(p,"'"+dname+"' overlaps dungeon '"+overlap.getName()+"'(replan or remove it)");
        return;
      }
      inform(p,"Building "+dname);
      dung.setupFlagsLocations();
      dung.saveDB();
      dung.render(handler);
      dung.saveMap(mapdir + File.separator + dname + ".map");
      handler.add(p);
    } else {
      inform(p,"Dungeon "+dname+" doesn't exist");
    }
  }

  public void suspendDungeon(Player p,String dname) {
    dungeons.suspend(dname);
  }
  
  public void gotoDungeon(Player p,String dname) {
    Dungeon dung = dungeons.get(dname);
    if(!dung.teleportToTop(p))
      inform(p,"Can't teleport to start of this dungeon");

  }
  
  public void gotoDungeonEnd(Player p,String dname) {
    Dungeon dung = dungeons.get(dname);
    if(!dung.teleportToBot(p))
      inform(p,"Can't teleport to end of a dungeon without any levels");
  }  
  
  public void setResetMinMax(Player p,String dname,String t) {
    Dungeon dung = dungeons.get(dname);
    if(dung!=null) {
      dung.setResetMinMax(t);
    }
  } 
  
  public void enableDungeon(Player p,String dname) {
    dungeons.enable(dname);
  }  
  
  public void deleteDungeon(Player p,String dname) {
    Dungeon dung = dungeons.get(dname);
    dung.delete(this,handler);
    handler.add(p);
    dungeons.remove(dname);
  }
  
  public void resetDungeon(Player p,String dname) {
    Dungeon dung = dungeons.get(dname);
    dung.reset(this);
  }
  
  public void unprotDungeon(Player p,String dname) {
    dungeons.remove(dname);
  }
  
  public void inform(Player p,Exception e) {
    String msg = e.getMessage();
    if(msg==null)
      inform(p,"Exception trapped "+e);
    else
      inform(p,msg);    
  }
  
  public void inform(Player p,String msg) {
    if(msg==null) {
      msg = "";
    }
    if(p==null)
      System.out.println("[" + info.getName() + "] "+msg);
    else
      p.sendMessage(msg);
  }
  
  public void inform(Player p,List<String> msgs) {
    for(String msg: msgs) {
      inform(p,msg);
    }
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

  public String giveCash(CatConfig cnf, Entity ent, double gold) {
    if(cnf == null || cnf.GoldOff())
      return null;
    String res = null;
    if (ent instanceof Player) {
      Player player = (Player) ent;
      if (economy != null) {
        double bal = economy.getBalance(player.getName());
        res = economy.format(bal);
      }
    }
    return res;
  }

  public Boolean takeCash(Entity ent, int gold, String reason) {
    Boolean res = false;
    if (ent instanceof Player) {
      Player player = (Player) ent;
      if (economy != null) {
        if(economy.has(player.getName(), gold)) {
          EconomyResponse resp = economy.withdrawPlayer(player.getName(), gold);
          double bal = resp.balance;
          player.sendMessage("It costs you "+gold+" "+reason+" ("+economy.format(bal)+")");
          res = true;
        } else {
          double bal = economy.getBalance(player.getName());
          player.sendMessage("Not enough money "+reason+" ("+economy.format(bal)+")");
        }
      }
    }
    return res;
  }   
  
}
