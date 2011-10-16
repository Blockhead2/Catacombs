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

import java.util.ArrayList;
import org.bukkit.block.Block;

public class WorldProtect {
  public final ArrayList<CatCuboid> protect;

  public WorldProtect() {
    protect  = new ArrayList<CatCuboid>();
  }

  public void add(CatCuboid cube) {
    protect.add(cube);
  }

  public void remove(CatCuboid cube) {
    protect.remove(cube);
  }

  // isProtected/isSuspended could use getCube instead
  public Boolean isProtected(int x, int y, int z) {
    for(CatCuboid c : protect) {
      if(c.isIn(x, y, z))
        return true;
    }
    return false;
  }
  
  public Boolean isProtected(Block blk) {
    for(CatCuboid c : protect) {
      if(c.isIn(blk.getX(),blk.getY(),blk.getZ()))
        return true;
    }
    return false;
  }  
  
  public Boolean isSuspended(int x, int y, int z) {
    for(CatCuboid c : protect) {
      if(c.isInSuspended(x, y, z))
        return true;
    }
    return false;
  }
  public CatCuboid getCube(int x, int y, int z) {
    for(CatCuboid c : protect) {
      if(c.isInRaw(x, y, z))
        return c;
    }
    return null;
  }
  public CatCuboid getCube(Block blk) {
    for(CatCuboid c : protect) {
      if(c.isInRaw(blk.getX(),blk.getY(),blk.getZ()))
        return c;
    }
    return null;
  } 
}