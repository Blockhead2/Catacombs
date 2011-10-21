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

import org.bukkit.Material;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.block.Block;
import org.bukkit.World;

public class CatPlayerListener  extends PlayerListener{
  private static Catacombs plugin;

  public CatPlayerListener(Catacombs instance) {
    plugin = instance;
  }
/*
  @Override
  public void onPlayerLogin(PlayerLoginEvent event){
    Player player = event.getPlayer();

    //plugin.sql.playerlogin(player.getName());
    if(plugin.debug) {
      if(plugin.permissions.admin(player)) {
        System.out.println("[" + plugin.info.getName() + "] Player '"+player+"' logged in (is admin)");
      } else {
        System.out.println("[" + plugin.info.getName() + "] Player '"+player+"' logged in (is not admin)");
      }
    }
  }
 */
  @Override
  public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
    if (event.isCancelled())
      return;
    Player player = event.getPlayer();
    Block blk = player.getLocation().getBlock();
    World w = blk.getWorld();
    if(plugin.prot.isProtected(blk)) {
      for(String cmd:plugin.cnf.BannedCommands()) {
        if (event.getMessage().startsWith(cmd)) {
          player.sendMessage("'"+cmd+"' is blocked in dungeons");
          event.setCancelled(true);
          return;
        }
      }
    }
  }
  
  @Override
  public void onPlayerInteract(PlayerInteractEvent event) {
    if (event.isCancelled())
      return;
    
    Block blk = event.getClickedBlock();
    if(blk.getType()==Material.STONE_BUTTON && plugin.prot.isInRaw(blk)) {
      plugin.Commands(event.getPlayer(),new String[] {"reset"} );
    }
    
    // Will be hand for boss mob spawn!
    //if(blk.getType()==Material.CHEST && plugin.prot.isInRaw(blk)) {
    //  System.out.println("[Catacombs] Chest open");
    //  event.setCancelled(true);
    //  plugin.test_encounter = new CatEncounter(blk);
    //}
  }  
  
  @Override
  public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
    if (event.isCancelled())
      return;
    
    Block block = event.getBlockClicked();
    CatCuboid c = plugin.prot.getCube(block);
    if(c != null) {
      event.setCancelled(true);
    }
  }

  @Override
  public void onPlayerBucketFill(PlayerBucketFillEvent event) {
    if (event.isCancelled())
      return;
    
    Block block = event.getBlockClicked();
    CatCuboid c = plugin.prot.getCube(block);
    if(c != null) {
      event.setCancelled(true);
    }
  }
}
