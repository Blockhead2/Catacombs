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
import java.util.List;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class CatLootList {
  private final List<String> list = new ArrayList<String>();
  private String name;

  public CatLootList() {
    name = "???";
  }
  
  public CatLootList(FileConfiguration fcnf, String name, String path) {
    this.name = name;
    List<String> l = CatUtils.getSStringList(fcnf,path);
    if(l!=null) {
      for(String str: l)
        list.add(str);
    }
  }
  
  @Override
  public String toString() {
    return name+" "+list;
  }  
  
  public void add(String str) {
    list.add(str);
  }
  
  public void clear() {
    list.clear();
  }
  
  public void generateItems(List<ItemStack> inv) {
    for(String loot : list) {
      String tmp[] = loot.split(":");
      String matName = tmp[0];
      short code=0;
      if(matName.contains("/")) {
        String mat[] = matName.split("/");
        matName = mat[0];
        code = Short.parseShort(mat[1]);
      }
      Material m = Material.matchMaterial(matName);
      if(m!=null) {
        if(CatUtils.Chance(Integer.parseInt(tmp[1]))) {
          String vals[] = tmp[2].split("-");
          int num = 1;
          int lo = Integer.parseInt(vals[0]);
          if(vals.length == 1) {
            num = lo;
          } else {
            int hi = Integer.parseInt(vals[1]);
            num = CatUtils.Between(lo,hi);
          }
          ItemStack stk;
          if(m == Material.POTION) {
            stk = new ItemStack(m,num,(short)code);
          } else {
            stk = (code>0)?new ItemStack(m,num,(short)0,(byte)code):new ItemStack(m,num);
          }
          inv.add(stk);
        }
      }
    }   
  } 
  

  
}
