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
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import com.nijikokun.catacombsregister.payment.Methods;
import org.bukkit.entity.Monster;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

public class CatListener implements Listener {
  private Catacombs plugin;
  private Methods Methods = null;

  public CatListener(final Catacombs plugin) {
    this.plugin = plugin;
  }  
  
  /////////////////////////////////////////////////////////////////////////////
  //
  //    BLOCK Events
  //
  /////////////////////////////////////////////////////////////////////////////
    
  @EventHandler(priority = EventPriority.LOW)
  public void onBlockDamage(BlockDamageEvent event) {
    Block blk = event.getBlock();
    if(!plugin.cnf.SecretDoorOnlyInDungeon() && !plugin.dungeons.isInRaw(blk)) {
      // Toggle secret doors outside dungeons if the option is false
      CatUtils.toggleSecretDoor(blk);
    }
  }
  
  /////////////////////////////////////////////////////////////////////////////
  //
  //    PLAYER Events
  //
  /////////////////////////////////////////////////////////////////////////////
    
  @EventHandler(priority = EventPriority.LOW)
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
  
  /////////////////////////////////////////////////////////////////////////////
  //
  //    ENTITY Events
  //
  /////////////////////////////////////////////////////////////////////////////
    
  @EventHandler(priority = EventPriority.LOW)
  public void onCreatureSpawn(CreatureSpawnEvent evt) {
    if(evt.isCancelled())
      return;
    
    SpawnReason reason = evt.getSpawnReason();
    Block blk = evt.getLocation().getBlock();
    Boolean isMonster = evt.getEntity() instanceof Monster;
      
    if(plugin.cnf.MobsSpawnOnlyUnderground() &&
       isMonster &&
       reason == SpawnReason.NATURAL &&
       CatUtils.onSurface(blk)) {
      evt.setCancelled(true);
      return;
    }    
    
    if(plugin.dungeons.getDungeon(blk) == null) { // Not in dungeon
      if(plugin.cnf.MobsSpawnOnlyInDungeons() && isMonster)
        evt.setCancelled(true);
      return;
    }
  }
  
  /////////////////////////////////////////////////////////////////////////////
  //
  //    SERVER Events
  //
  /////////////////////////////////////////////////////////////////////////////
    
  @EventHandler(priority = EventPriority.LOW)
  public void onPluginDisable(PluginDisableEvent event) {
    if (this.Methods != null && this.Methods.hasMethod()) {
      Boolean check = this.Methods.checkDisabled(event.getPlugin());

      if(check) {
        Methods.reset();
        System.out.println("[" + plugin.info.getName() + "] Payment method was disabled. No longer accepting payments.");
      }
    }
  }
 
  @EventHandler(priority = EventPriority.LOW)
  public void onPluginEnable(PluginEnableEvent event) {
    if (!this.Methods.hasMethod()) {
      if (!hasPreferred()) {
        System.out.println("[" + plugin.info.getName() + "] Preferred payment method [" + getPreferred() + "] not found, using first found.");
        Methods.setVersion(plugin.info.getVersion());
        Methods.setMethod(plugin.getServer().getPluginManager());
      } else {
        System.out.println("[" + plugin.info.getName() + "] attempting to get preferred payment method [" + Methods.getPreferred() + "].");      
        Methods.setVersion(plugin.info.getVersion());
        Methods.setMethod(plugin.getServer().getPluginManager());
      }
      if (Methods.getMethod() != null) {
        System.out.println("[" + plugin.info.getName() + "] Payment method found (" + Methods.getMethod().getName() + " version: " + Methods.getMethod().getVersion() + ")");
      } else {
        System.out.println("[" + plugin.info.getName() + "] No payment method found");
      }
    }

    //System.out.print("[" + plugin.info.getName() + "] version " + plugin.info.getVersion()+ " is enabled");
  }

  private String getPreferred() {
    return plugin.cnf.Economy();
  }

  private boolean hasPreferred() {
    return Methods.setPreferred(getPreferred());
  }
     
}
