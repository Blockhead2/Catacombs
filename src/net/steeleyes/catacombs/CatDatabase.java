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

import java.sql.*;
import java.util.ArrayList;
//import org.bukkit.Material;

public class CatDatabase {
  private final String driver = "com.mysql.jdbc.Driver";

  private String url;
  private String prefix;
  private String dbName;
  private String userName;
  private String password;

  private String ct;
  private String dt;
  private String ut;

  private CatConfig config;
  private Boolean on;
  private Connection conn=null;

  public CatDatabase(CatConfig cfg) {
    config = cfg;
    
    on           = config.MySQLEnabled();
    prefix       = config.MySQLPrefix();
    dbName       = config.MySQLdbName();
    userName     = config.MySQLuserName();
    password     = config.MySQLuserPass();
    url          = "jdbc:mysql://"+config.MySQLAddr()+":"+config.MySQLPort()+"/";

    // Shortcut for the tables.
    ct           = prefix+"cubes";
    dt           = prefix+"dungeons";
    ut           = prefix+"users";

    //if(!on) {
    //  System.out.println("[Catacombs] WARNING: MYSQL is disabled (not everything will work as intended)");
    //  return;
    //}

    //System.out.println("[Catacombs] Connecting to MYSQL");

    try {
      Class.forName(driver).newInstance();
      try {
        conn = DriverManager.getConnection(url+dbName,userName,password);
      } catch (SQLException e) {
        System.err.println("[Catacombs] MYSQL Connect: " + e.getMessage());
        conn=null;
        return;
      }
      System.out.println("[Catacombs] Connected sucessfully to MYSQL");
    } catch (Exception e) {
      System.err.println("[Catacombs] Problem connecting to MYSQL database "+url);
      conn=null;
      return;
    }
    initialize();
  }

  public void checkConnection() {
    if(!on) return;

    try {
      if(conn!=null && !conn.isValid(5)) {
        reconnect();
      }
    } catch (SQLException e) {
      System.out.println("[Catacombs] checkConnection:"+e.getMessage());
    }
  }

  public void reconnect() {
    if(!on) return;

    try {
      conn = DriverManager.getConnection(url+dbName,userName,password);
      System.out.println("[Catacombs] reconnected to MYSQL database");
    } catch (SQLException e) {
      System.err.println("[Catacombs] reconnect: " + e.getMessage());
      conn=null;
    }
  }

  public ArrayList<CatCuboid> getDungeonCubes(String dname) {
    String str = "SELECT "+
        ct+".xl,"+ct+".yl,"+ct+".zl, "+
        ct+".xh,"+ct+".yh,"+ct+".zh,"+ct+".hut " +
        "FROM "+dt+","+ct+" "+
        "WHERE ("+dt+".dname='"+dname+"') AND" +
        " ("+dt+".did="+ct+".did)";
    return getCubes(str);
  }

  public ArrayList<CatCuboid> getDungeonCubes(String dname,String wname) {
    String str = "SELECT "+
        ct+".xl,"+ct+".yl,"+ct+".zl, "+
        ct+".xh,"+ct+".yh,"+ct+".zh,"+ct+".hut " +
        "FROM "+dt+","+ct+" "+
        "WHERE ("+dt+".dname='"+dname+"') AND ("+dt+".world='"+wname+"') AND" +
        " ("+dt+".did="+ct+".did)";
    return getCubes(str);
  }

  public ArrayList<CatCuboid> getWorldCubes(String wname) {
    String str = "SELECT "+
        ct+".xl,"+ct+".yl,"+ct+".zl, "+
        ct+".xh,"+ct+".yh,"+ct+".zh,"+ct+".hut " +
        "FROM "+dt+","+ct+" "+
        "WHERE ("+dt+".world='"+wname+"') AND" +
        " ("+dt+".did="+ct+".did)";
    return getCubes(str);
  }

  /*
  public WorldProtect getWorldCubes(String world) {
    String str = "SELECT "+
        ct+".xl,"+ct+".yl,"+ct+".zl, "+
        ct+".xh,"+ct+".yh,"+ct+".zh,"+ct+".hut " +
        "FROM "+dt+","+ct+" "+
        "WHERE ("+dt+".world='"+world+"') AND" +
        " ("+dt+".did="+ct+".did)";
    WorldProtect prot = new WorldProtect();
    for (Cuboid c: getCubes(str)) {
      prot.add(c);
    }
    return prot;
  }*/

  private ArrayList<CatCuboid> getCubes(String str) {
    ArrayList<CatCuboid> list = new ArrayList<CatCuboid>();
    if(!on) return list;
    checkConnection();
    if(conn!=null) {
      try {
        Statement s = conn.createStatement();
        s.executeQuery(str);
        ResultSet rs = s.getResultSet();
        while(rs.next()) {
          int xl  = rs.getInt(ct+".xl");
          int xh  = rs.getInt(ct+".xh");
          int yl  = rs.getInt(ct+".yl");
          int yh  = rs.getInt(ct+".yh");
          int zl  = rs.getInt(ct+".zl");
          int zh  = rs.getInt(ct+".zh");
          int hut = rs.getInt(ct+".hut");
          list.add(new CatCuboid(null,xl,yl,zl,xh,yh,zh,
                  (hut==1)?CatCuboid.Type.HUT:CatCuboid.Type.LEVEL));
        }
      } catch (SQLException e) {
        System.err.println("[Catacombs] getCubes:"+e.getMessage());
      }
    }
    return list;
  }

  public ArrayList<String> getWorlds() {
    return getNames("SELECT DISTINCT world FROM "+dt);
  }
  public ArrayList<String> getUsers() {
    return getNames("SELECT uname FROM "+ut);
  }
  public ArrayList<String> getDungeons() {
    return getNames("SELECT dname FROM "+dt);
  }
  public ArrayList<String> getDungeons(String world) {
    return getNames("SELECT dname FROM "+dt+" WHERE world='"+world+"'");
  }

  private ArrayList<String> getNames(String str) {
    ArrayList<String> list = new ArrayList<String>();
    if(!on) return list;
    checkConnection();
    if(conn!=null) {
      try {
        Statement s = conn.createStatement();
        s.executeQuery(str);
        ResultSet rs = s.getResultSet();
        while(rs.next()) {
          list.add(rs.getString(1));
        }
      } catch (SQLException e) {
        System.err.println("[Catacombs] getNames:"+e.getMessage());
      }
    }
    return list;
  }

  public int getDungeonId(String name) {
    return getId("SELECT did FROM "+dt+" WHERE dname='"+name+"'");
  }

  public int getUserId(String name) {
    return getId("SELECT uid FROM "+ut+" WHERE uname='"+name+"'");
  }

  public int getGold(String name) {
    return getId("SELECT gold FROM "+ut+" WHERE uname='"+name+"'");
  }

  private int getId(String str) {
    int id = 0;
    if(!on) return id;
    checkConnection();
    if(conn!=null) {
      try {
        Statement s = conn.createStatement();
        s.executeQuery(str);
        ResultSet rs = s.getResultSet();
        if(rs.next()) {
          id = rs.getInt(1);
        }
      } catch (SQLException e) {
        System.err.println("[Catacombs] getId:"+e.getMessage());
      }
    }
    return id;
  }

  public boolean write(String sql) {
    if(!on) return false;
    checkConnection();
    if(conn!=null) {
      try {
        PreparedStatement stmt = null;
        stmt = this.conn.prepareStatement(sql);
        stmt.executeUpdate();
        return true;
      } catch(SQLException e) {
        System.err.println("[Catacombs] write: " + e.getMessage());
      }
    }
    return false;
  }

  private void initialize () {
    write("CREATE TABLE IF NOT EXISTS `"+ut+"` ("+
      "uid int(16) NOT NULL PRIMARY KEY AUTO_INCREMENT,"+
      "uname varchar(40) UNIQUE NOT NULL,"+
      "gold int(32) unsigned NOT NULL DEFAULT '0',"+
      "stamp_create timestamp DEFAULT '0000-00-00 00:00:00',"+
      "stamp_update timestamp DEFAULT now() on update now()"+
      ") ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;");

    write("CREATE TABLE IF NOT EXISTS `"+dt+"` ("+
      "did int(16) NOT NULL PRIMARY KEY AUTO_INCREMENT,"+
      "dname varchar(40) NOT NULL,"+
      "world varchar(50) NOT NULL DEFAULT ''," +
      "stamp_create timestamp DEFAULT '0000-00-00 00:00:00',"+
      "stamp_update timestamp DEFAULT now() on update now()"+
      ") ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;");

    write("CREATE TABLE IF NOT EXISTS `"+ct+"` ("+
      "did int(16) NOT NULL,"+
      "hut int(16) NOT NULL DEFAULT '0',"+
      "xl int(64) NOT NULL DEFAULT '0',"+
      "yl int(64) NOT NULL DEFAULT '0',"+
      "zl int(64) NOT NULL DEFAULT '0',"+
      "xh int(64) NOT NULL DEFAULT '0',"+
      "yh int(64) NOT NULL DEFAULT '0',"+
      "zh int(64) NOT NULL DEFAULT '0',"+
      "stamp_create timestamp DEFAULT '0000-00-00 00:00:00',"+
      "stamp_update timestamp DEFAULT now() on update now()"+
      ") ENGINE=MyISAM DEFAULT CHARSET=latin1;");
  }

  public void playerlogin(String name) {
    if(getUserId(name)==0) {
      write("INSERT INTO "+ut+" (stamp_create,uname) VALUES (null,'"+name+"')");
      System.out.println("[Catacombs] Added new user "+name);
    }
  }

  public void addCube(CatCuboid c,int did) {
    int hut = (c.isHut())?1:0;
    write("INSERT INTO `"+ct+"` (stamp_create,did,xl,yl,zl,xh,yh,zh,hut) "+
          "VALUES (null,'"+did+"','"+c.xl+"','"+c.yl+"','"+c.zl+
          "','"+c.xh+"','"+c.yh+"','"+c.zh+"','"+hut+"')");
    //System.out.println("[Catacombs] Added new cube "+c);
  }

  public int addDungeon(String name,String world) {
    int did = 0;
    if(getDungeonId(name)==0) {  // Check if it exists
      write("INSERT INTO `"+dt+"` " +
           "(stamp_create,dname,world) VALUES (null,'"+name+"','"+world+"')");
      did = getDungeonId(name);
      System.out.println("[Catacombs] Added new dungeon "+name+" ("+did+")");
    } else {
      System.out.println("[Catacombs] Dungeon "+name+" already exists");
    }
    return did;
  }

  public void addGold(String user,int num) {
    write("UPDATE `"+ut+"` SET gold = gold + "+num+" WHERE uname='"+user+"'");
  }

  public MultiWorldProtect delDungeon(String dname) {
    write("DELETE d,c FROM "+dt+" as d " +
          "LEFT JOIN "+ct+" AS c ON c.did = d.did WHERE d.dname='"+dname+"'");

    // Just reload the entire database for the moment
    
    return getAllCubes();
  }

  public void delUser(String uname) {
    String str = "DELETE FROM "+ut+" WHERE uname='"+uname+"'";
  }

  public MultiWorldProtect getAllCubes() {
    // Could do this in one pass (if I get dname and cubes at same time)
    MultiWorldProtect prot = new MultiWorldProtect();
    for (String world : getWorlds()) {
      ArrayList<CatCuboid> list = getWorldCubes(world);
      for (CatCuboid c: list) {
        prot.add(world,c);
      }
    }
    return prot;
  }

}
