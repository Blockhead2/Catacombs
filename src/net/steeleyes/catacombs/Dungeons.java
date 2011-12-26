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

import java.util.HashMap;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map.Entry;

import java.sql.ResultSet;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Dungeons {
  private final HashMap<String,Dungeon> dungeons = new HashMap<String,Dungeon>();

  public Dungeons(Catacombs plugin,CatSQL sql) {
    if(sql!=null) {
      ResultSet rs = sql.query("SELECT dname,wname,pname,hut,xl,yl,zl,xh,yh,zh,sx,sy,sz,ex,ey,ez,enable,num FROM levels");
      try {
        while(rs.next()) {
          String dname = rs.getString("dname");
          String wname = rs.getString("wname");
          String pname = rs.getString("pname");
          World world = plugin.getServer().getWorld(wname);

          Dungeon dung;
          if(!dungeons.containsKey(dname)) { // New dungeon
            CatMat maj = new CatMat(Material.COBBLESTONE);
            CatMat min = new CatMat(Material.MOSSY_COBBLESTONE);
            dung = new Dungeon(dname,plugin.cnf,world,maj,min);
            dung.setBuilder(pname);
            dung.setBuilt(true);
            dungeons.put(dname,dung);
          } else {                          // Existing dungeon
            dung = dungeons.get(dname);
          }
          CatLevel clevel = new CatLevel(plugin,rs,world);
          dung.add(clevel);
        }
        for (Entry<String,Dungeon> entry : dungeons.entrySet()) {
          Dungeon d = entry.getValue();
          d.registerCubes(plugin.prot);
          d.guessMajor();
        }
      } catch(Exception e) {
        System.err.println("[Catacombs] ERROR: "+e.getMessage());
      }
    }
  }
  
  public Boolean exists(String dname) {
    return dungeons.containsKey(dname);
  }
  
  public Boolean isPlanned(String dname) {
    if(dungeons.containsKey(dname)) {
      Dungeon dung = dungeons.get(dname);
      return dung.isOk() && !dung.isBuilt();      
    }
    return false;
  }
  
  public Boolean isBuilt(String dname) {
    if(dungeons.containsKey(dname)) {
      Dungeon dung = dungeons.get(dname);
      return dung.isBuilt();      
    }
    return false;
  }
  
  public Boolean isOk(String dname) {
    if(dungeons.containsKey(dname)) {
      Dungeon dung = dungeons.get(dname);
      return dung.isOk();      
    }
    return false;
  } 
  
  public Dungeon which(Block blk) {
    for(Entry<String,Dungeon> e: dungeons.entrySet()) {
      Dungeon dung = e.getValue();
      if(dung.isInRaw(blk)) {
        return dung;
      }
    }
    return null;
  }
  
  public void debugMajor() {
    for(Entry<String,Dungeon> e: dungeons.entrySet()) {
      Dungeon dung = e.getValue();
      dung.debugMajor();
    }
  }

  public void add(String dname, Dungeon d) {
    if(dungeons.containsKey(dname)) {
      Dungeon dung = dungeons.get(dname);
      if(dung.isBuilt()) {
        System.out.println("[catacombs] Already a built dungeon '"+dname+"'");
      } else {
        //System.out.println("[catacombs] Re-planning dungeon '"+dname+"'");
        dungeons.put(dname,d);
      }
    } else {
      //System.out.println("[catacombs] Planning dungeon '"+dname+"'");
      dungeons.put(dname,d);
    }
  }
  
  public Dungeon getOverlap(Dungeon dung) {
    for(String name : dungeons.keySet()) {
      Dungeon x = dungeons.get(name);
      if(!dung.equals(x)) {
        if(dung.overlaps(x)) {
          return x;
        }
      }
    }   
    return null;
  }
  
  public void unregisterCubes(String name, MultiWorldProtect prot) {
    if(dungeons.containsKey(name)) {
      Dungeon dung = dungeons.get(name);
      dung.unregisterCubes(prot);
    }
  }
  public void registerCubes(String name, MultiWorldProtect prot) {
    if(dungeons.containsKey(name)) {
      Dungeon dung = dungeons.get(name);
      dung.registerCubes(prot);
    }
  }

  public void suspend(Catacombs plugin,String name,CatSQL sql) {
    if(dungeons.containsKey(name)) {
      Dungeon dung = dungeons.get(name);
      dung.suspend(plugin,sql);
    }
  }  
  public void enable(String name,CatSQL sql) {
    if(dungeons.containsKey(name)) {
      Dungeon dung = dungeons.get(name);
      dung.enable(sql);
    }
  }   
  
  public void remove(String name, MultiWorldProtect prot,CatSQL sql) {
    if(dungeons.containsKey(name)) {
      Dungeon dung = dungeons.get(name);
      dung.unregisterCubes(prot);
      dung.remove(sql);
      dungeons.remove(name);
    }
  }
  
  public void remove(String name) {
    if(dungeons.containsKey(name)) {
      dungeons.remove(name);
    }
  }   
  
  public void clearMonsters(Catacombs plugin) {
    for(Entry<String,Dungeon> e: dungeons.entrySet()) {
      e.getValue().clearMonsters(plugin);
    }
  }  
  
  public List<String> getNames() {
    List<String> list = new ArrayList<String>(dungeons.keySet());
    Collections.sort(list);
    return list;
  }
  
  public Dungeon get(String dname) {
    if(dungeons.containsKey(dname)) {
      return dungeons.get(dname);
    }
    return null;
  }
  
  public List<String> getinfo(String dname) {
    if(dungeons.containsKey(dname)) {
      return dungeons.get(dname).getinfo();
    }
    return null;
  }
  public List<String> dump(String dname) {
    if(dungeons.containsKey(dname)) {
      return dungeons.get(dname).dump();
    }
    return null;
  } 
  public List<String> map(String dname) {
    if(dungeons.containsKey(dname)) {
      return dungeons.get(dname).map();
    }
    return null;
  } 
}
