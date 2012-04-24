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

import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;


public class BlockChange {
  private Block blk;
  private Material mat;
  private byte code=-1;
  private List<ItemStack> items = null;
  private String spawner = null;
  private String[] line = null;
  
  public BlockChange(Block blk, Material mat) {
    this.blk = blk;
    this.mat = mat;
  }
  
  public BlockChange(Block blk, Material mat,List<ItemStack> items) {
    this.blk = blk;
    this.mat = mat;
    this.items = items;
  } 
  
  public BlockChange(Block blk, Material mat, byte code) {
    this.blk = blk;
    this.mat = mat;
    this.code = code;
  }

  public Block getBlk() {
    return blk;
  }

  public byte getCode() {
    return code;
  }

  public List<ItemStack> getItems() {
    return items;
  }

  public Material getMat() {
    return mat;
  }

  public String getSpawner() {
    return spawner;
  }

  public void setSpawner(String spawner) {
    this.spawner = spawner;
  }

  public void setItems(List<ItemStack> items) {
    this.items = items;
  }
  
  public Boolean hasLines() {
    return line!=null;
  }
  
  public void setLine(int i, String str) {
    if(line==null) {
      line = new String[4];
    }
    if(i>=0 && i<=3) {
      line[i] = str;
    }
  }
  
  public String getLine(int i) {
    if(line!=null && i>=0 && i<=3) {
      return line[i];
    }
    return null;
  } 
  
}
