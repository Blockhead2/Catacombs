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
import java.util.ArrayList;

import org.bukkit.block.Block;
import org.bukkit.World;

class DefaultProtection implements CatProtection {
  private HashMap<String,ArrayList<CatCuboid>> protect;

  DefaultProtection() {
    protect = new HashMap<String,ArrayList<CatCuboid>>();
  }

  public Boolean isProtected(Block blk) {
    return isProtected(blk.getWorld(),blk.getX(),blk.getY(),blk.getZ());
  }
  
  public Boolean isProtected(World world, int x,int y, int z) {
    String name = world.getName();
    if(protect.containsKey(name)) {
      ArrayList<CatCuboid> list = protect.get(name);
      for(CatCuboid c : list) {
        if(c.isIn(x, y, z))
          return true;
      }
    }
    return false;
  }

  public void addCube(World world, CatCuboid cube) {
    String name = world.getName();
    if(protect.containsKey(name)) {
      ArrayList<CatCuboid> list = protect.get(name);
      list.add(cube);
    } else {
      ArrayList<CatCuboid> list = new ArrayList<CatCuboid>();
      list.add(cube);
      protect.put(name,list);
    }
  }

  public void removeCube(World world, CatCuboid cube) {

  }

}
