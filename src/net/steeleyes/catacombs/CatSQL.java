
package net.steeleyes.catacombs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.bukkit.World;


public class CatSQL {
  
  Connection conn = null;
  String path;
  
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
      Statement stat = conn.createStatement();
      int res = stat.executeUpdate(cmd);
      //if(res != 0)
      //  System.err.println("[Catacombs] SQL error code "+res+" command="+cmd);
      stat.close();
    } catch (Exception e) {
      System.err.println("[Catacombs] Sqlite error: "+e.getMessage());
    }
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
  
  public void suspendDungeon(String dname) {
    command("UPDATE levels SET enable=0 WHERE dname='"+dname+"';");
  }
  public void enableDungeon(String dname) {
    command("UPDATE levels SET enable=1 WHERE dname='"+dname+"';");
  }  
  public void removeDungeon(String dname) {
    command("DELETE FROM levels WHERE dname='"+dname+"';"); 
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
  public void createLegacyTables() {
    if(!tableExists("levels")) {
      System.out.println("[Catacombs] Creating Legacy SQL table 'levels'");
      command("CREATE TABLE `levels` (" +
        "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
        "dname TEXT," +
        "wname TEXT," +
        "pname TEXT," +
        "hut INTEGER," +
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
        "ez INTEGER," +
        "enable INTEGER," +
        "num INTEGER," +
        "dx INTEGER," +
        "dy INTEGER," +
        "map TEXT" +
      ");");      
    }
  }
  
  public void dropTables() {
    // Need to figure out the locking
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
        "minor TEXT," +
        "enable INTEGER" +
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
        "value STRING" +
      ");");      
    } 
    if(!tableExists("locations")) {
      System.out.println("[Catacombs] Creating SQL table 'locations'");
      command("CREATE TABLE `location` (" +
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
        int    enable = rs.getInt("enable");
        System.out.println("[Catacombs]   dungeon '"+dname+"'");
        
        String version = "javax";
        String major = null;
        String minor = "MOSSY_COBBLESTONE";
        int roof = 0;
        int room = 0;
        int floor = 3;
        
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
          
          if(ey==0) version = "mysql";
          World world = plugin.getServer().getWorld(wname);
          if(world == null) {
            System.err.println("[Catacombs] Can't find a world called '"+wname+"'");
          } else {
            CatCuboid.Type t = (hut==1)?CatCuboid.Type.HUT:CatCuboid.Type.LEVEL;
            CatCuboid cube = new CatCuboid(world,xl,yl,zl,xh,yh,zh,t);
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
          }

          //System.out.println("[Catacombs]   "+did+" hut="+hut+" ("+xl+","+yl+","+zl+") ("+xh+","+yh+","+zh+") ("+sx+","+sy+","+sz+") ("+ex+","+ey+","+ez+")");
          String cmd = "INSERT INTO levels2 "+
            "(did,type,roof,room,floor,xl,yl,zl,xh,yh,zh,sx,sy,sz,ex,ey,ez) VALUES"+
            "("+did+",'"+((hut==1)?"HUT":"LEVEL")+"',"+roof+","+room+","+floor+
              ","+xl+","+yl+","+zl+
              ","+xh+","+yh+","+zh+
              ","+sx+","+sy+","+sz+
              ","+ex+","+ey+","+ez+
            ");";
          //System.out.println(cmd);
          command(cmd);


        }
        if(major==null || major.equals("AIR"))
          major = "COBBLESTONE";
        if(roof == 0)
          roof = 1;
        if(room == 0)
          room = 3;
        rs2.close();
        //System.out.println("[Catacombs] convert "+version+" "+major+" "+minor+" "+roof+" "+room+" "+floor);
        String cmd = "INSERT INTO dungeons "+
          "(did,version,dname,wname,pname,enable,major,minor) VALUES"+
          "("+did+",'"+version+"','"+dname+"','"+wname+"','"+pname+"',"+enable+",'"+major+"','"+minor+"');";
        //System.out.println(cmd);
        command(cmd);
        did++;
      }
 
      rs.close();
    } catch(Exception e) {
      System.err.println("[Catacombs] sqlite error "+e.getMessage());
    }    
  } 
}  

        //Statement stat = conn.createStatement();
    
    //ResultSet rs = stat.executeQuery("select * from levels;");
    //while (rs.next()) {
    //  System.out.println("id(" + rs.getString("id")+" dname("+rs.getString("dname")+")");
   // }
   // rs.close();
    
    
//    stat.executeUpdate("drop table if exists people;");
//    stat.executeUpdate("create table people (name, occupation);");
//    
//    PreparedStatement prep = conn.prepareStatement(
//      "insert into people values (?, ?);");
//
//    prep.setString(1, "Gandhi");
//    prep.setString(2, "politics");
//    prep.addBatch();
//    prep.setString(1, "Turing");
//    prep.setString(2, "computers");
//    prep.addBatch();
//    prep.setString(1, "Wittgenstein");
//    prep.setString(2, "smartypants");
//    prep.addBatch();
//
//    conn.setAutoCommit(false);
//    prep.executeBatch();
//    conn.setAutoCommit(true);
//
//    ResultSet rs = stat.executeQuery("select * from people;");
//    while (rs.next()) {
//      System.out.println("name = " + rs.getString("name"));
//      System.out.println("job = " + rs.getString("occupation"));
//    }
//    rs.close();
    //conn.close();
  
