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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class CatSQL {
  
  private Connection conn = null;
  private String path;
  public Boolean debug = false;
  
  public CatSQL(String p) {
    path = p;
    try {
      Class.forName("org.sqlite.JDBC");
      conn = DriverManager.getConnection("jdbc:sqlite:"+path);
      conn.setAutoCommit(true);
    } catch (Exception e) {
      System.err.println("[Catacombs] Sqlite error: "+e.getMessage());
    }
  }
    
  public Boolean tableExists(String table) {
    Boolean exists = false;
    try {
      Statement stat = conn.createStatement();
      ResultSet rs = stat.executeQuery("SELECT name FROM sqlite_master WHERE name='"+table+"';");
      exists = rs.next();
      stat.close();
    } catch (Exception e) {
      System.err.println("[Catacombs] Sqlite error: "+e.getMessage());
    }
    return exists;
  }
  
  public void command(String cmd) {
    try {
      if(debug) {
        System.out.println("[Catacombs] "+cmd);
      } else {
        Statement stat = conn.createStatement();
        int res = stat.executeUpdate(cmd);
        stat.close();
      }
    } catch (Exception e) {
      System.err.println("[Catacombs] Sqlite error: "+e.getMessage());
    }
  }
  
  public int getLastId() {
    int id = -1;
    try {
      ResultSet rs = query("select last_insert_rowid();");
      if(rs.next()) {
        id = rs.getInt(1);
      }
    } catch(Exception e) {
      System.err.println("[Catacombs] Sqlite error: "+e.getMessage());
    }
    return id;    
  }
  
  public ResultSet query(String cmd) {
    ResultSet rs = null;
    try {
      Statement stat = conn.createStatement();
      rs = stat.executeQuery(cmd);
      if(rs==null)
        System.err.println("[Catacombs] Sqlite error: command returned null "+cmd);
      // Must not close until finished with rs
    } catch (Exception e) {
      System.err.println("[Catacombs] Sqlite error: "+e.getMessage());
    }
    return rs;
  }
   
  public void removeDungeon(int did) {
    command("DELETE FROM levels2 WHERE did='"+did+"';"); 
    command("DELETE FROM flags WHERE did='"+did+"';"); 
    command("DELETE FROM locations WHERE did='"+did+"';"); 
    command("DELETE FROM dungeons WHERE did='"+did+"';"); 
  }
  
  public int getDid(String dname) {
    int did = -1;
    try {
      ResultSet rs = query("SELECT did FROM dungeons WHERE dname='"+dname+"';");
      if(rs.next()) {
        did = rs.getInt("id");
      } else {
        System.err.println("[Catacombs] Dungeon '"+dname+"' doesn't exist in sql database");
      }
      rs.close();
    } catch (Exception e) {
      System.err.println("[Catacombs] Sqlite error: "+e.getMessage());
    }
    return did;
  }
//  public void createLegacyTables() {
//    if(!tableExists("levels")) {
//      System.out.println("[Catacombs] Creating Legacy SQL table 'levels'");
//      command("CREATE TABLE `levels` (" +
//        "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
//        "dname TEXT," +
//        "wname TEXT," +
//        "pname TEXT," +
//        "hut INTEGER," +
//        "xl INTEGER," +
//        "yl INTEGER," +
//        "zl INTEGER," +
//        "xh INTEGER," +
//        "yh INTEGER," +
//        "zh INTEGER," +
//        "sx INTEGER," +
//        "sy INTEGER," +
//        "sz INTEGER," +
//        "ex INTEGER," +
//        "ey INTEGER," +
//        "ez INTEGER," +
//        "enable INTEGER," +
//        "num INTEGER," +
//        "dx INTEGER," +
//        "dy INTEGER," +
//        "map TEXT" +
//      ");");      
//    }
//  }
  
  public void dropTables() {
    // ToDo: Need to figure out the locking
    System.out.println("[Catacombs] Dropping SQL tables");
    command("DROP TABLE IF EXISTS `dungeons`;");
    command("DROP TABLE IF EXISTS `levels2`;");
  }
  
  public void createTables() {
    if(!tableExists("dungeons")) {
      System.out.println("[Catacombs] Creating SQL table 'dungeons'");
      command("CREATE TABLE `dungeons` (" +
        "did INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
        "version TEXT," +
        "dname TEXT," +
        "wname TEXT," +
        "pname TEXT," +
        "major TEXT," +
        "minor TEXT" +
      ");");      
    }
    if(!tableExists("levels2")) {
      System.out.println("[Catacombs] Creating SQL table 'levels2'");
      command("CREATE TABLE `levels2` (" +
        "lid INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
        "did INTEGER," +
        "type TEXT," +
        "room INTEGER," +
        "roof INTEGER," +
        "floor INTEGER," +
        "xl INTEGER," +
        "yl INTEGER," +
        "zl INTEGER," +
        "xh INTEGER," +
        "yh INTEGER," +
        "zh INTEGER," +
        "sx INTEGER," +
        "sy INTEGER," +
        "sz INTEGER," +
        "ex INTEGER," +
        "ey INTEGER," +
        "ez INTEGER" +
      ");");      
    }
    if(!tableExists("flags")) {
      System.out.println("[Catacombs] Creating SQL table 'flags'");
      command("CREATE TABLE `flags` (" +
        "fid INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
        "did INTEGER," +
        "type STRING," +
        "val STRING" +
      ");");      
    } 
    if(!tableExists("locations")) {
      System.out.println("[Catacombs] Creating SQL table 'locations'");
      command("CREATE TABLE `locations` (" +
        "xid INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
        "did INTEGER," +
        "type STRING," +
        "x INTEGER," +
        "y INTEGER," +
        "z INTEGER" +
      ");");      
    }     
  }
  
  public void Convert2(Catacombs plugin) {
    try {
      ResultSet rs = query("SELECT dname,wname,pname,enable FROM levels GROUP BY dname;");
      int did = 1;
      while(rs.next()) {
        String dname = rs.getString("dname");
        String wname = rs.getString("wname");
        String pname = rs.getString("pname");
        Boolean    enable = rs.getInt("enable")!=0;
        System.out.println("[Catacombs]   dungeon '"+dname+"'");
        
        String version = "javax";
        String major = null;
        String minor = "MOSSY_COBBLESTONE";
        int roof = 0;
        int room = 0;
        Block end = null;
        World world = plugin.getServer().getWorld(wname);
        if(world == null) {
          System.err.println("[Catacombs] Can't find a world called '"+wname+"' dungeon '"+dname+"' can't be converted and will be dropped");
        } else {
          ResultSet rs2 = query("SELECT xl,yl,zl,xh,yh,zh,sx,sy,sz,ex,ey,ez,hut FROM levels WHERE dname='"+dname+"' ORDER BY yh DESC;");
          while(rs2.next()) {
            int xl = rs2.getInt("xl");
            int yl = rs2.getInt("yl");
            int zl = rs2.getInt("zl");
            int xh = rs2.getInt("xh");
            int yh = rs2.getInt("yh");
            int zh = rs2.getInt("zh");
            int sx = rs2.getInt("sx");
            int sy = rs2.getInt("sy");
            int sz = rs2.getInt("sz");
            int ex = rs2.getInt("ex");
            int ey = rs2.getInt("ey");
            int ez = rs2.getInt("ez");
            int hut = rs2.getInt("hut");

            if(ex==0 && ey==0 && ez==0) version = "mysql";

            CatCuboid.Type t = (hut==1)?CatCuboid.Type.HUT:CatCuboid.Type.LEVEL;
            CatCuboid cube = new CatCuboid(world,xl,yl,zl,xh,yh,zh,t);
            if(ex==0 && ey==0 && ez==0) {
              Vector v = cube.guessEndLocation();
              if(v!=null) {
                ex = v.x;
                ey = v.y;
                ez = v.z;
                System.out.println("[Catacombs]     end location for legacy level = "+v);
              } else {
                System.err.println("[Catacombs] Can't find end location for level in '"+dname+"'");
              }
            }
            
            int floor = guessFloorDepth(world.getBlockAt(ex,ey+4,ez));
            
            end = world.getBlockAt(ex,ey+floor+1,ez);  // Save the last level end chest
            
            if(roof == 0) {
              roof = cube.guessRoofSize();
            }
            if(room == 0) {
              room = cube.guessRoomSize();
            }
            if(major == null && roof > 0) {
              CatMat m = cube.guessMajorMat(roof);
              major = m.toString();
              if(major.equals("AIR")) // Try again on the next level down
                major = null;
            }
            
            String cmd = "INSERT INTO levels2 "+
              "(did,type,roof,room,floor,xl,yl,zl,xh,yh,zh,sx,sy,sz,ex,ey,ez) VALUES"+
              "("+did+",'"+((hut==1)?"HUT":"LEVEL")+"',"+roof+","+room+","+floor+
                ","+xl+","+yl+","+zl+
                ","+xh+","+yh+","+zh+
                ","+sx+","+sy+","+sz+
                ","+ex+","+ey+","+ez+
              ");";
            command(cmd);
          }
          rs2.close();
          
          if(major==null || major.equals("AIR"))
            major = "COBBLESTONE";

          assert(end!=null); // Problems: no levels or no end Block

          command("INSERT INTO dungeons "+
            "(did,version,dname,wname,pname,major,minor) VALUES"+
            "("+did+",'"+version+"','"+dname+"','"+wname+"','"+pname+"','"+major+"','"+minor+"');");       
          command("INSERT INTO flags (did,type,val) VALUES ("+did+",'IS_ENABLED','"+enable+"');");        
          command("INSERT INTO flags (did,type,val) VALUES ("+did+",'BOSS_KILLED','0');");
          command("INSERT INTO flags (did,type,val) VALUES ("+did+",'RESET_TIME','0');");
          command("INSERT INTO flags (did,type,val) VALUES ("+did+",'RESET_MAX','0');");
          command("INSERT INTO flags (did,type,val) VALUES ("+did+",'RESET_MIN','0');");
          command("INSERT INTO flags (did,type,val) VALUES ("+did+",'ROOF','"+major+"');");
          command("INSERT INTO flags (did,type,val) VALUES ("+did+",'FLOOR','"+major+"');");
          command("INSERT INTO locations (did,type,x,y,z) VALUES ("+did+",'END_CHEST',"+
                  end.getX()+","+end.getY()+","+end.getZ()+");");        

          did++;
        }
      }
      rs.close();
    } catch(Exception e) {
      System.err.println("[Catacombs] sqlite error "+e.getMessage());
    }    
  } 
  
  private int guessFloorDepth(Block blk) {
    int floor = 3;
    while(true) {
      if(blk.getType() == Material.CHEST || blk.getType() == Material.TRAP_DOOR) {
        return floor;
      }
      floor++;
      blk = blk.getRelative(BlockFace.UP);
      if(blk==null || floor > 20) {
        return 3;
      }
    }
  }
}  

  
