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

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.Location;

import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import org.bukkit.entity.Monster;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class CatEntityListener implements Listener {
  private static Catacombs plugin;

  public CatEntityListener(Catacombs instance) {
    plugin = instance;
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onEntityDeath(EntityDeathEvent evt) {
    LivingEntity damagee = (LivingEntity) evt.getEntity();
    Block blk = damagee.getLocation().getBlock();
    Boolean inDungeon =   plugin.dungeons.isInRaw(blk);
    
    if(evt instanceof PlayerDeathEvent) {
      PlayerDeathEvent pevt = (PlayerDeathEvent) evt;
      Player player = (Player) damagee;
      if(inDungeon) {
        if(plugin.cnf.DeathExpKept()>0) // Don't drop any exp if some will be retained.
          pevt.setDroppedExp(0);
        int expLevel = player.getLevel();
        pevt.setNewExp((int)(7.0*expLevel*plugin.cnf.DeathExpKept()));
        if(plugin.cnf.DeathKeepGear()) {
          plugin.players.saveGear(player);
          evt.getDrops().clear(); // We'll handle the items, don't drop them
        }
      }
      if(plugin.cnf.AdvancedCombat())
        plugin.monsters.removeThreat(player);
      return;
    }
    
    //Is the monster managed?
    if(plugin.cnf.AdvancedCombat()) {
      if(plugin.monsters.isManaged(damagee)) {
        CatMob mob = plugin.monsters.get(damagee);
//        if(mob.getCreature() == CatCreature.SILVERFISH && CatUtils.Chance(30)) {
//          for(int i=0;i<2;i++) {
//            CatMob mob2 = new CatMob(plugin.cnf,CatCreature.SILVERFISH,blk.getWorld(),blk.getLocation());
//            plugin.monsters.add(mob2);
//          }
//        }
        //System.out.println("[Catacombs] Entity death (adv) "+evt.getEntity() +" "+mob+" "+mob.getHealth());
        plugin.monsters.remove(damagee);
        mob.death(evt); 
      }
    } else {
      if(inDungeon && !plugin.cnf.GoldOff()) {
        EntityDamageEvent ev = damagee.getLastDamageCause();
        Entity damager = CatUtils.getDamager(ev);
        if(damager instanceof Player) {
          double gold = plugin.cnf.Gold();
          String bal = plugin.giveCash(plugin.cnf,damager,gold);
          if(bal!=null && gold > 0)
            ((Player)damager).sendMessage(gold+" coins ("+bal+")");
        } 
      }
    }   
  }  
  
  @EventHandler(priority = EventPriority.LOW)
  public void onEntityDamage(EntityDamageEvent evt) {
    if(evt.isCancelled())
      return;

    Block blk = evt.getEntity().getLocation().getBlock();
    Boolean inDungeon = plugin.dungeons.isInRaw(blk);
    
    if(!inDungeon) {
      return;
    }
    
    if(!(evt.getEntity() instanceof LivingEntity))
      return;
    
    LivingEntity damagee = (LivingEntity) evt.getEntity();
    LivingEntity damager = (LivingEntity) CatUtils.getDamager(evt);

    if(plugin.cnf.NoPvPInDungeon() && damagee instanceof Player && damager instanceof Player) {
      evt.setCancelled(true);
      return;      
    }
    
    //Is the target a managed monster?
    if(plugin.monsters.isManaged(damagee)) {
      // Projectiles cause 2 events at the moment.
      if(evt.getCause() == DamageCause.PROJECTILE && evt.getDamage() == 0)
        return;
      plugin.monsters.playerHits(plugin.cnf,evt);
    } else {  
      if(damagee instanceof Player) {
        //if(plugin.debug)
        //  evt.setCancelled(true);
        //else
        plugin.monsters.monsterHits(plugin.cnf,evt);
      }
    }
  }
  
  @EventHandler(priority = EventPriority.LOW)
  public void onEntityTarget(EntityTargetEvent evt) {
    if(evt.isCancelled())
      return;
    if(!plugin.cnf.AdvancedCombat()) return;
    LivingEntity damagee = (LivingEntity) evt.getEntity();
    if(plugin.monsters.isManaged(damagee)) {
      CatMob mob = plugin.monsters.get(damagee);
      mob.target(evt);
      
      // If target is dead then remove from hate list
      // Check player going out of range works
      
      //TargetReason reason = evt.getReason();
      //Entity target = evt.getTarget();
      //System.out.println("[Catacombs] (cancel) Retarget "+target+" "+reason);
      //System.out.println("[Catacombs] Cancel re-target "+damagee);
      //evt.setCancelled(true);
    }

  }

  
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
    Dungeon dung = plugin.dungeons.getDungeon(blk);

    if(dung == null) { // Not in dungeon
      if(plugin.cnf.MobsSpawnOnlyInDungeons() && isMonster)
        evt.setCancelled(true);
      return;
    }
    
    if(!dung.isEnabled()) {  // Cancel spawns in suspended dungeons
      evt.setCancelled(true);
      return;
    }   

    // In enabled dungeon
    // Prevent creatures spawning from spawners in good light (WOLVES, PIGMEN, BLAZE mostly)
    if(reason == SpawnReason.SPAWNER &&
       blk.getLightLevel()>10) {
      evt.setCancelled(true);
      return;         
    }
    
    if(!(evt.getEntity() instanceof LivingEntity))
      return;
       
    LivingEntity ent = (LivingEntity) evt.getEntity();
    
//    // No hook yet to prevent the smooth_stone dungeons getting trashed by this yet
//    if(evt.getCreatureType() == CreatureType.SILVERFISH) {
//      System.out.println("[Catacombs] Silverfish spawn "+evt.getSpawnReason());
//
//      if(evt.getSpawnReason() == SpawnReason.CUSTOM && !plugin.monsters.isManaged(ent)) {
//        System.out.println("[Catacombs] Cancel Silverfish spawn "+evt.getSpawnReason());
//        evt.setCancelled(true);
//        return;
//      }
//    }
    
    CatConfig cnf = plugin.cnf;
    if(cnf.AdvancedCombat()) {
      if(reason == SpawnReason.CUSTOM || plugin.monsters.isManaged(ent)) { // The mob is already on the list
        return;
      }
      
      // Cancel the dungeon spawn if nobody is close
      int num_players = CatUtils.countPlayerNear(ent,cnf.SpawnRadius(),cnf.SpawnDepth());
      if(num_players==0) {
        evt.setCancelled(true);
        return;
      }
      
      Boolean isSilverFish = (evt.getEntityType() == EntityType.SILVERFISH);
      
      int num_mobs = CatUtils.countCreatureNear(ent, cnf.MonsterRadius(), 2);
      //System.out.println("[Catacombs] spawn players="+num_players+" mobs="+num_mobs+" size="+plugin.monsters.size());
      if(!isSilverFish && num_mobs >= cnf.MonsterMax()*num_players) {
        evt.setCancelled(true);
        return;
      }      

      //Location loc = evt.getLocation();
      CatMob mob = new CatMob(plugin,evt.getEntityType(),ent);
      plugin.monsters.add(mob);

      //CatMob mob2 = new CatMob(CatCreature.CHICKEN,loc.getWorld(),loc);
      //plugin.monsters.add(mob2);
      //System.out.println("[Catacombs] "+plugin.monsters.size()+" "+ent);
    } else {
      EntityType t = evt.getEntityType();
      if(t == EntityType.WOLF)
        ((Wolf)ent).setAngry(true);
        
      if(t == EntityType.PIG_ZOMBIE)
        ((PigZombie)ent).setAngry(true);
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onEntityExplode(EntityExplodeEvent eEvent){
    if(eEvent.isCancelled())
      return;
   
    Location loc = eEvent.getLocation();
    Block blk = loc.getBlock();
    List<Block> list = eEvent.blockList();

    if(plugin.dungeons.isProtected(blk) || any_protected(list)) {
      list.clear();
    }
  }
  
  private Boolean any_protected(List<Block> list) {
    for(Block b : list) {
      if(plugin.dungeons.isProtected(b)) {
        return true;
      }
    }
    return false;
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onEntityChangeBlock(EntityChangeBlockEvent eEvent) {
    if(eEvent.isCancelled())
      return;

    Block blk = eEvent.getBlock();
    if(eEvent.getEntity() instanceof Enderman && plugin.dungeons.isInRaw(blk)) {
      //System.out.println("[Catacombs] Enderman EntityChangeBlockEvent cancelled");
      eEvent.setCancelled(true);
    }
  }

}
