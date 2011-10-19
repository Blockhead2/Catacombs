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

import org.bukkit.World;
import org.bukkit.Material;
import org.bukkit.block.ContainerBlock;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.block.CreatureSpawner;


public class BlockChangeHandler implements Runnable {  
  private final int MAX_CHANGE = 10000;
  private int changed = 0;
    
  private final List<BlockChange> delay = new ArrayList<BlockChange>();
  private final List<BlockChange> high  = new ArrayList<BlockChange>();
  private final List<BlockChange> low   = new ArrayList<BlockChange>();
  private final List<Player>      who   = new ArrayList<Player>();

  private void setBlock(BlockChange x) {
    if(x.code>=0)
      x.blk.setTypeIdAndData(x.mat.getId(),x.code,false);
    else
      x.blk.setType(x.mat);
    if(x.items != null) {
      if(x.blk.getState() instanceof ContainerBlock) {
        ContainerBlock cont = (ContainerBlock) x.blk.getState();
        Inventory inv = cont.getInventory();
        for(ItemStack s: x.items) {
          inv.addItem(s);
        }
        if(x.mat == Material.DISPENSER) {
          delay.add(new BlockChange(x.blk,null,x.code));
        }
      }  
    }
    if(x.getSpawner()!=null) {
      CreatureSpawner spawner = (CreatureSpawner) x.blk.getState();
      spawner.setCreatureTypeId(x.getSpawner());
    }
  }
  
  @Override
  public void run() {
    
    while(!delay.isEmpty()) {
      BlockChange x = delay.remove(0);
      x.blk.setData(x.code);   
    }
    
    int cnt=0;
    while(!high.isEmpty() && cnt < MAX_CHANGE) {
      BlockChange x = high.remove(0);
      setBlock(x);
      cnt++;
    }
    while(!low.isEmpty() && cnt < MAX_CHANGE) {
      BlockChange x = low.remove(0);
      setBlock(x);
      cnt++;
    } 
    if(cnt>0) {
      changed += cnt;
    }
    if(cnt == 0 && changed > 0) {
      System.out.println("[Catacombs] Block Handler #changes="+changed);
      for(Player p : who) {
        p.sendMessage("Catacomb changes complete");
      }
      who.clear();
      changed = 0;
    }
  }
  
  
  
  // TODO: Tidy this mess up
  public void addLow(BlockChange b) {
    low.add(b);
  }
  public void addHigh(BlockChange b) {
    high.add(b);
  }
  public void addLow(Block blk,Material mat) {
    low.add(new BlockChange(blk,mat));
  }
  public void addHigh(Block blk,Material mat) {
    high.add(new BlockChange(blk,mat));
  }
  public void addLow(Block blk,Material mat,byte code,List<ItemStack> items) {
    BlockChange ch = new BlockChange(blk,mat,code);
    ch.setItems(items);
    low.add(ch);
  }
  public void addHigh(Block blk,Material mat,byte code,List<ItemStack> items) {
    BlockChange ch = new BlockChange(blk,mat,code);
    ch.setItems(items);
    high.add(ch);
  }
  public void addLow(Block blk,Material mat,List<ItemStack> items) {
    low.add(new BlockChange(blk,mat,items));
  }
  public void addHigh(Block blk,Material mat,List<ItemStack> items) {
    high.add(new BlockChange(blk,mat,items));
  }
  public void addLow(Block blk,Material mat, byte code) {
    low.add(new BlockChange(blk,mat,code));
  }
  public void addHigh(Block blk,Material mat, byte code) {
    high.add(new BlockChange(blk,mat,code));
  }
  public void addLow(World world,int x,int y,int z,Material mat) {
    Block blk = world.getBlockAt(x,y,z);
    low.add(new BlockChange(blk,mat));
  }
  public void addHigh(World world,int x,int y,int z,Material mat) {
    Block blk = world.getBlockAt(x,y,z);
    high.add(new BlockChange(blk,mat));
  }
  public void addLow(World world,int x,int y,int z,Material mat,List<ItemStack> items) {
    Block blk = world.getBlockAt(x,y,z);
    low.add(new BlockChange(blk,mat,items));
  }
  public void addLow(World world,int x,int y,int z,Material mat,byte code,List<ItemStack> items) {
    Block blk = world.getBlockAt(x,y,z);
    BlockChange ch = new BlockChange(blk,mat,code);
    ch.setItems(items);
    low.add(ch);
  }
  public void addHigh(World world,int x,int y,int z,Material mat,byte code,List<ItemStack> items) {
    Block blk = world.getBlockAt(x,y,z);
    BlockChange ch = new BlockChange(blk,mat,code);
    ch.setItems(items);
    high.add(ch);
  }
  public void addHigh(World world,int x,int y,int z,Material mat,List<ItemStack> items) {
    Block blk = world.getBlockAt(x,y,z);
    high.add(new BlockChange(blk,mat,items));
  }
  public void addLow(World world,int x,int y,int z,Material mat,byte code) {
    Block blk = world.getBlockAt(x,y,z);
    low.add(new BlockChange(blk,mat,code));
  }
  public void addHigh(World world,int x,int y,int z,Material mat,byte code) {
    Block blk = world.getBlockAt(x,y,z);
    high.add(new BlockChange(blk,mat,code));
  }
  public void add(Player player) {
    who.add(player);
  } 
}
