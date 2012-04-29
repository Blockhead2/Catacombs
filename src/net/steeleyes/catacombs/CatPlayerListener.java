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




public class CatPlayerListener {

  
//  @EventHandler(priority = EventPriority.LOW)
//  public void onPlayerInteract(PlayerInteractEvent event) {
//    if (event.isCancelled())
//      return;
//    

////    if(plugin.debug && blk.getType()==Material.WEB && plugin.dungeons.isInRaw(blk)) {
////            
////      Dungeon dung = plugin.dungeons.which(blk);
////      //if(dung.isSuspended() || dung.bossKilled())
////      //  return;
////      CatMob mob = new CatMob(plugin,plugin.mobtypes.get("Zombie"),blk.getWorld(),blk.getLocation());
////      plugin.monsters.add(mob);
////        
//////      if(!dung.triggerEncounter(plugin,blk)) {
//////        plugin.inform(event.getPlayer(),"There is a battle already in progress in this dungeon");
//////      }
////      event.setCancelled(true);
////    } 
//  }  
  
//  @EventHandler(priority = EventPriority.LOW)
//  public void onPlayerFish(PlayerFishEvent evt) {
//    if(!plugin.cnf.AdvancedCombat()) return;
//    //System.out.println("[Catacombs] fish state="+evt.getState()+" caught="+evt.getCaught()+" dur="+evt.getPlayer().getItemInHand().getDurability());
//    if(evt.getState() == PlayerFishEvent.State.CAUGHT_ENTITY) {
//      if(evt.getCaught() instanceof Player) {
//        Player healer = evt.getPlayer();
//        Player healee = (Player) evt.getCaught();
//        plugin.monsters.playerHeals(plugin.cnf,healer,healee);
//      //} else {  // Fish monsters to test healing code    
//      //  Player healer = evt.getPlayer();
//      //  plugin.monsters.playerHeals(healer,healer);
//      }
//    }
//  }
  
  
  

  
//  @EventHandler(priority = EventPriority.LOW)
//  public void onPlayerPortal(PlayerPortalEvent evt) {
//    if(!plugin.cnf.AdvancedCombat()) return;
//    plugin.monsters.removeThreat(evt.getPlayer());
//  }
//  
//  @EventHandler(priority = EventPriority.LOW)
//  public void onPlayerChangedWorld(PlayerChangedWorldEvent evt) {
//    if(!plugin.cnf.AdvancedCombat()) return;
//    plugin.monsters.removeThreat(evt.getPlayer());
//  }
//  
//  @EventHandler(priority = EventPriority.LOW)
//  public void onPlayerBedEnter(PlayerBedEnterEvent evt) {
//    if(!plugin.cnf.AdvancedCombat()) return;
//    plugin.monsters.removeThreat(evt.getPlayer());
//  }
//  
//  
//  @EventHandler(priority = EventPriority.LOW)
//  public void onPlayerKick(PlayerKickEvent evt) {
//    if(!plugin.cnf.AdvancedCombat()) return;
//    plugin.monsters.removeThreat(evt.getPlayer());
//  }
//  
//  @EventHandler(priority = EventPriority.LOW)
//  public void onPlayerQuit(PlayerQuitEvent evt) {
//    if(!plugin.cnf.AdvancedCombat()) return;
//    plugin.monsters.removeThreat(evt.getPlayer());
//  }
//  
//  @EventHandler(priority = EventPriority.LOW)
//  public void onPlayerTeleport(PlayerTeleportEvent evt) {
//    if(!plugin.cnf.AdvancedCombat()) return;
//    Block blk = evt.getTo().getBlock();
//    //Boolean inDungeon =   plugin.prot.isInRaw(blk);
//    //System.out.println("[Catacombs] Player teleport inDungeon="+inDungeon+" "+evt.getCause());
//    plugin.monsters.removeThreat(evt.getPlayer());
//  }

}
