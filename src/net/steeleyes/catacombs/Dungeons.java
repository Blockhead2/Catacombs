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
import java.util.Set;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map.Entry;


import com.avaje.ebean.EbeanServer;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Dungeons {
  public final HashMap<String,Dungeon> dungeons = new HashMap<String,Dungeon>();

  public Dungeons(Catacombs plugin,EbeanServer db) {
    if(db!=null) {
      List<dbLevel> list=null;
      try {
        list = db.find(dbLevel.class).where().findList();
      } catch(Exception e) {
        System.err.println("[Catacombs] Problem loading catacombs database");
      }
      if (list != null && !list.isEmpty()) {
        for(dbLevel info: list) {
          String dname = info.getDname();
          String wname = info.getWname();
          String pname = info.getPname();
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
          CatLevel clevel = new CatLevel(plugin,info,world);
          dung.levels.add(clevel);      
        }
        for (Entry<String,Dungeon> entry : dungeons.entrySet()) {
          Dungeon d = entry.getValue();
          d.registerCubes(plugin.prot);
          d.guessMajor();
        }
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
        System.out.println("[catacombs] Re-planning dungeon '"+dname+"'");
        dungeons.put(dname,d);
      }
    } else {
      System.out.println("[catacombs] Planning dungeon '"+dname+"'");
      dungeons.put(dname,d);
    }
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
  
  public Set<CatCuboid> getCubes(String name) {
    if(dungeons.containsKey(name)) {
      Dungeon dung = dungeons.get(name);
      return dung.getCubes();
    }
    return null;
  }
  
  public void suspend(String name,EbeanServer db) {
    if(dungeons.containsKey(name)) {
      Dungeon dung = dungeons.get(name);
      dung.suspend(db);
    }
  }  
  public void enable(String name,EbeanServer db) {
    if(dungeons.containsKey(name)) {
      Dungeon dung = dungeons.get(name);
      dung.enable(db);
    }
  }  
        
  public void remove(String name, MultiWorldProtect prot,EbeanServer db) {
    if(dungeons.containsKey(name)) {
      Dungeon dung = dungeons.get(name);
      dung.unregisterCubes(prot);
      dung.remove(db);
      dungeons.remove(name);
    }
  }  
  
  public void remove(String name) {
    if(dungeons.containsKey(name)) {
      dungeons.remove(name);
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

}
