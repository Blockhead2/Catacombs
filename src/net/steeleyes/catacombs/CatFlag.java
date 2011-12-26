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

public class CatFlag {

  public enum Type {
    BOSS_KILLED(Boolean.class),
    OWNER(String.class);
    
    private Class c;
    
    private Type(Class c) {
      this.c = c;
    }
   
    public Boolean wrongClass(Class cls) {
      if(!this.c.equals(cls)) {
        System.out.println("[Catacombs] Incorrect type for '"+this+"' expecting "+c+" found call for "+cls);
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
  
  private Type type;
  private Object val;

  public CatFlag(String stype, String v) {
    type = CatUtils.getEnumFromString(Type.class, stype);
    if(type.isClass(Boolean.class)) {
      val = (v.equalsIgnoreCase("true"))?true:false;
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
  
  public Boolean matches(Type t) {
    return type == t;
  }
  
  public String getRaw() {
    return val.toString();
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
}
