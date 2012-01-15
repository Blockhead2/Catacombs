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
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class CatPlayerListener  extends PlayerListener{
  private static Catacombs plugin;

  public CatPlayerListener(Catacombs instance) {
    plugin = instance;
  }

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
      Dungeon dung = plugin.dungeons.which(blk);
      plugin.Commands(null,new String[] {"reset",dung.getName()} );
    }
    
    if(false && /*plugin.cnf.BossEnabled() && */ blk.getType()==Material.CHEST &&
       plugin.prot.isInRaw(blk) && blk.getRelative(BlockFace.DOWN).getType()==Material.GRASS) {
            
      Dungeon dung = plugin.dungeons.which(blk);
      if(dung.isSuspended() || dung.bossKilled())
        return;
        
      if(!dung.triggerEncounter(plugin,blk)) {
        plugin.inform(event.getPlayer(),"There is a battle already in progress in this dungeon");
      }
      event.setCancelled(true);
    } 
  }  
  
  @Override
  public void onPlayerFish(PlayerFishEvent evt) {
    //System.out.println("[Catacombs] fish state="+evt.getState()+" caught="+evt.getCaught()+" dur="+evt.getPlayer().getItemInHand().getDurability());
    if(evt.getState() == PlayerFishEvent.State.CAUGHT_ENTITY) {
      if(evt.getCaught() instanceof Player) {
        Player healer = evt.getPlayer();
        Player healee = (Player) evt.getCaught();
        plugin.monsters.playerHeals(plugin.cnf,healer,healee);
      //} else {  // Fish monsters to test healing code    
      //  Player healer = evt.getPlayer();
      //  plugin.monsters.playerHeals(healer,healer);
      }
    }
  }
  
  @Override
  public void onPlayerRespawn(PlayerRespawnEvent evt) {
    Player player = evt.getPlayer();
    if(plugin.players.hasGear(player)) {
      if(plugin.cnf.DeathKeepGear() && CatUtils.takeCash(player, plugin.cnf.DeathGearCost(),"to restore your equipment")) {
        plugin.players.restoreGear(player);
      } else {
        plugin.players.dropGear(player);
      }
    }
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
  
  @Override
  public void onPlayerJoin(PlayerJoinEvent evt) {
    
  }
  
  @Override
  public void onItemHeldChange(PlayerItemHeldEvent evt) {
    
  }
  
  @Override
  public void onPlayerPortal(PlayerPortalEvent evt) {
    plugin.monsters.removeThreat(evt.getPlayer());
  }
  
  @Override
  public void onPlayerChangedWorld(PlayerChangedWorldEvent evt) {
    plugin.monsters.removeThreat(evt.getPlayer());
  }
  
  @Override
  public void onPlayerBedEnter(PlayerBedEnterEvent evt) {
    plugin.monsters.removeThreat(evt.getPlayer());
  }
  
  //@Override
  //public void onPlayerBedLeave(PlayerBedLeaveEvent evt) {
  // 
  //}  
  
  @Override
  public void onPlayerKick(PlayerKickEvent evt) {
    plugin.monsters.removeThreat(evt.getPlayer());
  }
  
  @Override
  public void onPlayerQuit(PlayerQuitEvent evt) {
    plugin.monsters.removeThreat(evt.getPlayer());
  }
  
  @Override
  public void onPlayerTeleport(PlayerTeleportEvent evt) {
    Block blk = evt.getTo().getBlock();
    //Boolean inDungeon =   plugin.prot.isInRaw(blk);
    //System.out.println("[Catacombs] Player teleport inDungeon="+inDungeon+" "+evt.getCause());
    plugin.monsters.removeThreat(evt.getPlayer());
  }

}
