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

import java.sql.ResultSet;

public class CatLocation {
  
  public enum Type {
    END_CHEST,
    DUNGEON_DOOR
  }
  
  //store world too?
  //store back link to dungeon?
  
  private Vector loc;
  private Type type;
  private int xid=-1;
  
  public CatLocation(Type type, Vector loc) {
    this.type = type;
    this.loc = loc;
  }
  
  public CatLocation(Type type, int x, int y, int z) {
    this.type = type;
    this.loc = new Vector(x,y,z);
  }
    
  public CatLocation(String stype, int x, int y, int z) {
    type = CatUtils.getEnumFromString(Type.class, stype);;
    this.loc = new Vector(x,y,z);
  } 
  
  public CatLocation(Catacombs plugin,ResultSet rs) throws Exception{
    this(rs.getString("type"),rs.getInt("x"),rs.getInt("y"),rs.getInt("z"));
    xid = rs.getInt("xid");
  }
  
  @Override
  public String toString() {
    return type+" "+xid+" "+loc;
  }  
  
  public void saveDB(CatSQL sql, int did) {
    if(xid<=0) {
      sql.command("INSERT INTO locations "+
        "(did,type,x,y,z) VALUES ("+did+",'"+type+"',"+loc.x+","+loc.y+","+loc.z+");");
      xid = sql.getLastId();
    } else {
      System.err.println("[Catacombs] INTERNAL ERROR: CatLocation .db updates not implemented yet");
    }    
  }
  public Boolean matches(Type t) {
    return type == t;
  }
}

