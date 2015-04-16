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

public class CatFlag {

  public enum Type {
    BOSS_KILLED(Boolean.class),
    IS_ENABLED(Boolean.class),
    RESET_TIME(Long.class),
    RESET_MIN(Long.class),
    RESET_MAX(Long.class),
    ROOF(String.class),
    FLOOR(String.class),
    OWNER(String.class);
    
    private Class c;

    private Type(Class c) {
      this.c = c;
    }
   
    public Boolean wrongClass(Class cls) {
      if(!this.c.equals(cls)) {
        System.err.println("[Catacombs] Incorrect type for '"+this+"' expecting "+c+" found call for "+cls);
        return true;
      }
      return false;
    }
    
    public Boolean isClass(Class cls) {
      if(this.c.equals(cls)) {
        return true;
      }
      return false;
    } 
    
    public Class flagClass() {
      return c;
    }
  }
  
  private int fid=-1;  
  private Type type;
  private Object val;
  private Catacombs plugin=null;
  
  public CatFlag(Catacombs plugin,ResultSet rs) throws Exception{
    this(rs.getString("type"),rs.getString("val"));
    fid = rs.getInt("fid");
    this.plugin = plugin;
  }

  public CatFlag(String stype, String v) {
    type = CatUtils.getEnumFromString(Type.class, stype);
    if(type.isClass(Boolean.class)) {
      val = (v.equalsIgnoreCase("false")||v.equals("0"))?false:true;
    } else if(type.isClass(Double.class)) {
      val = Double.parseDouble(v);
    } else if(type.isClass(Long.class)) {
      val = Long.parseLong(v);
    } else if(type.isClass(Integer.class)) {
      val = Integer.parseInt(v);
    } else {
      val = v;
    }
  }
    
  public CatFlag(Type type, String val) {
    this.type = type;
    if(type.wrongClass(String.class)) {
      this.val = null;
    } else {
      this.val = val;
    }
  }
  
  public CatFlag(Type type, Boolean val) {
    this.type = type;
    if(type.wrongClass(Boolean.class)) {
      this.val = null;
    } else {
      this.val = val;
    } 
  }
  
  public CatFlag(Type type, Integer val) {
    this.type = type;
    if(type.wrongClass(Integer.class)) {
      this.val = null;
    } else {
      this.val = val;
    } 
  }
  
  public CatFlag(Type type, Long val) {
    this.type = type;
    if(type.wrongClass(Long.class)) {
      this.val = null;
    } else {
      this.val = val;
    } 
  }
  
  public CatFlag(Type type, Double val) {
    this.type = type;
    if(type.wrongClass(Double.class)) {
      this.val = null;
    } else {
      this.val = val;
    } 
  }  
  
  @Override
  public String toString() {
    return type+" "+fid+" "+val;
  }
  
  public void saveDB(CatSQL sql, int did) {
    if(fid<=0) {
      sql.command("INSERT INTO flags "+
        "(did,type,val) VALUES ("+did+",'"+type+"','"+val+"');");
      fid = sql.getLastId();
    } else {
      sql.command("UPDATE flags SET val='"+val+"' WHERE fid='"+fid+"';");
    }
  }
  
  public Boolean matches(Type t) {
    return type == t;
  }
  
  public String getRaw() {
    return val.toString();
  }
  
  public Double getDouble() {
    if(type.wrongClass(Double.class))
      return null;
    return (Double)val;
  }
    
  public Long getLong() {
    if(type.wrongClass(Long.class))
      return null;
    return (Long)val;
  }
  
  public Integer getInteger() {
    if(type.wrongClass(Integer.class))
      return null;
    return (Integer)val;
  }
  
  public String getString() {
    if(type.wrongClass(String.class))
      return null;
    return (String)val;
  }
  
  public Boolean getBoolean() {
    if(type.wrongClass(Boolean.class))
      return null;
    return (Boolean)val;
  }
  
  public void setBoolean(Boolean val) {
    if(!type.wrongClass(Boolean.class))
      this.val = val;
  }
  
  public void setString(String val) {
    if(!type.wrongClass(String.class))
      this.val = val;
  }
  
  public void setLong(Long val) {
    if(!type.wrongClass(Long.class))
      this.val = val;
  }

  public void setDouble(Double val) {
    if(!type.wrongClass(Double.class))
      this.val = val;
  }

  public void setInteger(Integer val) {
    if(!type.wrongClass(Integer.class))
      this.val = val;
  }
}
