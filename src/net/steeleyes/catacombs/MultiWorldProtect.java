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
import org.bukkit.block.Block;

public class MultiWorldProtect {
  public HashMap<String,WorldProtect> protect;

  public MultiWorldProtect() {
    protect  = new HashMap<String,WorldProtect>();
  }
  
  public void add(String world,CatCuboid cube) {
    if(!protect.containsKey(world)) {
      protect.put(world,new WorldProtect());
    }
    WorldProtect p = protect.get(world);
    p.add(cube);
  }

  public WorldProtect get(String world) {
    return protect.get(world);
  }

  public void remove(String world,CatCuboid cube) {
    if(protect.containsKey(world)) {
      WorldProtect p = protect.get(world);
      p.remove(cube);
    }
  }
  
  public Boolean isProtected(Block blk) {
    String world = blk.getWorld().getName();
    if(protect.containsKey(world)) {
      WorldProtect p = protect.get(world);
      return p.isProtected(blk);
    }
    return false;
  }  
  
  public Boolean isSuspended(Block blk) {
    String world = blk.getWorld().getName();
    if(protect.containsKey(world)) {
      WorldProtect p = protect.get(world);
      return p.isSuspended(blk);
    }
    return false;
  }
  
  public Boolean isInRaw(Block blk) {
    String world = blk.getWorld().getName();
    if(protect.containsKey(world)) {
      WorldProtect p = protect.get(world);
      return p.isInRaw(blk);
    }
    return false;
  }
  
  public CatCuboid getCube(Block blk) {
    String world = blk.getWorld().getName();
    if(protect.containsKey(world)) {
      WorldProtect p = protect.get(world);
      return p.getCube(blk);
    }
    return null;
  }
}
