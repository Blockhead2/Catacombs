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

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

public class CatBlockListener extends BlockListener {
  private static Catacombs plugin;

  public CatBlockListener(Catacombs instance) {
    plugin = instance;
  }

  @Override
  public void onBlockPlace(BlockPlaceEvent event){
    if(event.isCancelled())
      return;

    Player player = event.getPlayer();
    Block block = event.getBlock();
    Material mat = block.getType();
    if(plugin.debug)
      player.sendMessage("PLACE : " + mat+ " ("+block.getX()+","+block.getY()+","+block.getZ()+")");

    if(mat == Material.TORCH)
      return;

    if(plugin.prot.isProtected(block.getWorld().getName(),block.getX(),block.getY(),block.getZ()))
      event.setCancelled(true);
  }

  @Override
  public void onBlockBreak(BlockBreakEvent event){
   // if(event.isCancelled())
   //   return;

    Player player = event.getPlayer();
    Block block = event.getBlock();
    Material mat = block.getType();
    if(plugin.debug)
      player.sendMessage("BREAK : " + mat+ " ("+block.getX()+","+block.getY()+","+block.getZ()+")");

    if(mat == Material.TORCH ||
       mat == Material.RED_MUSHROOM ||
       mat == Material.BROWN_MUSHROOM ||
       mat == Material.WEB)
      return;

    if(mat == Material.MOB_SPAWNER) {
      System.out.println("[Catacombs] break spawner");
      if(plugin.cnf.ProtectSpawners() &&
         plugin.prot.isProtected(block.getWorld().getName(),block.getX(),block.getY(),block.getZ())) {
        System.out.println("[Catacombs] cancel event");
        event.setCancelled(true);
        return;
      }
      //System.out.println("[Catacombs] set AIR");
      //block.setType(Material.AIR);
      return;        
    }
    
    if(plugin.prot.isProtected(block.getWorld().getName(),block.getX(),block.getY(),block.getZ()))
      event.setCancelled(true);
  }

  @Override
  public void onBlockDamage(BlockDamageEvent event){
    if(event.isCancelled())
      return;

    Player player = event.getPlayer();
    Block block = event.getBlock();
    
    //Material major = Material.COBBLESTONE;
    //Material minor = Material.MOSSY_COBBLESTONE;
    CatCuboid c = plugin.prot.getCube(block);
    if(c==null) { //outside the dungeon
      if(plugin.cnf.SecretDoorOnlyInDungeon())
        return;    
    } else {
      //major = c.getMajor();
      //minor = c.getMinor();
    }
    
    //if(plugin.cnf.SecretDoorOnlyInDungeon() &&
    //   !plugin.prot.isProtected(block.getWorld().getName(),block.getX(),block.getY(),block.getZ())) {
    //  return;
   // }

    Material mat = block.getType();
    if(plugin.debug) {
      player.sendMessage("DAMAGE : " + mat+"  ID:"+block.getData() +" ("+block.getX()+","+block.getY()+","+block.getZ()+")");
    }
    if(true) {
      Block piston = null;
      for(int i=1;i<=3;i++) {
        piston = block.getRelative(BlockFace.DOWN,i);
        if(piston.getType() == Material.PISTON_STICKY_BASE)
          break;
        piston = null;
      }
      if(piston == null || (piston.getData() & 7) != 1) // Piston needs to point up
        return;

      Block power = piston.getRelative(BlockFace.DOWN,1);

      if(power.getType() == Material.REDSTONE_TORCH_ON) {
        Block upper_door = piston.getRelative(BlockFace.UP,3);
        Material m = upper_door.getType();
        power.setType(m);
        upper_door.setType(Material.AIR);
      }  else {
        Block upper_door = piston.getRelative(BlockFace.UP,3);
        Material m = power.getType();
        power.setType(Material.REDSTONE_TORCH_ON);
        upper_door.setType(m);
      }
    }
  }

}
