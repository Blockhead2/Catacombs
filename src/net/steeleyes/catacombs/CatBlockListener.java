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
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.CreatureType;

public class CatBlockListener extends BlockListener {
  private static Catacombs plugin;

  public CatBlockListener(Catacombs instance) {
    plugin = instance;
  }

  @Override
  public void onBlockPlace(BlockPlaceEvent event){
    Block block = event.getBlockPlaced();
    Material mat = block.getType();
    if(plugin.debug) {
      System.out.println("[Catacombs] Block place="+mat+"("+mat.getId()+") already_cancelled="+event.isCancelled());
    }
    if(event.isCancelled())
      return;

    //if(plugin.debug) {
    //  Player player = event.getPlayer();
    //  player.sendMessage("PLACE : " + mat+ " ("+block.getX()+","+block.getY()+","+block.getZ()+")");
    //}

    if(mat == Material.TORCH)
      return;

    if(plugin.dungeons.isProtected(block))
      event.setCancelled(true);
  }

  @Override
  public void onBlockBreak(BlockBreakEvent event){
   // if(event.isCancelled())
   //   return;

    Block block = event.getBlock();
    Material mat = block.getType();
    if(plugin.debug) {
      Player player = event.getPlayer();
      player.sendMessage("BREAK : " + mat+ " ("+block.getX()+","+block.getY()+","+block.getZ()+")");
    }

    if(plugin.cnf.isBreakable(block))
      return;

    Boolean is_prot = plugin.dungeons.isProtected(block);
    
    if(mat == Material.MOB_SPAWNER) {
      if(plugin.cnf.ProtectSpawners() && is_prot) {
        Player player = event.getPlayer();
        player.sendMessage("Put torches around it to stop spawns");
        event.setCancelled(true);
        return;
      }     
      // Just do the natural for spawner blocks on the server
      return;        
    }
    
    if(is_prot)
      event.setCancelled(true);
  }

  @Override
  public void onBlockIgnite(BlockIgniteEvent event){
    if(event.isCancelled())
      return;

    if(plugin.dungeons.isInRaw(event.getBlock()))
      event.setCancelled(true);
  }
  
  @Override
  public void onBlockBurn(BlockBurnEvent event){
    if(event.isCancelled())
      return;

    if(plugin.dungeons.isInRaw(event.getBlock()))
      event.setCancelled(true);
  }  
  
  @Override
  public void onBlockDamage(BlockDamageEvent event){
    
    // Wordguard cancels the damage events so allow
    //   secret doors and debug to work regardless.
    //if(event.isCancelled())
    //  return;

    Block block = event.getBlock();
    
    if(!plugin.dungeons.isInRaw(block) && plugin.cnf.SecretDoorOnlyInDungeon())
      return;    

    Material mat = block.getType();
    if(plugin.debug) {
      Player player = event.getPlayer();
      player.sendMessage("DAMAGE : " + mat+"  ID:"+block.getData() +" ("+block.getX()+","+block.getY()+","+block.getZ()+")");
      if(block.getType() == Material.MOB_SPAWNER) {
        CreatureSpawner spawner = (CreatureSpawner) block.getState();
        System.out.println("[Catacombs] Spawner "+spawner.getCreatureType()+" delay="+spawner.getDelay()+" light="+spawner.getLightLevel());
        spawner.setCreatureType(CreatureType.ZOMBIE);
      }
    }

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
      byte code = upper_door.getData();
      power.setTypeIdAndData(m.getId(),code,false);
      upper_door.setType(Material.AIR);
    }  else {
      Block upper_door = piston.getRelative(BlockFace.UP,3);
      Material m = power.getType();
      byte code = power.getData();
      power.setType(Material.REDSTONE_TORCH_ON);
      upper_door.setTypeIdAndData(m.getId(),code,false);
    }
  }

}
