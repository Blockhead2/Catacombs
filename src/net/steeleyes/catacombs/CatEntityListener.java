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
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EndermanPickupEvent;
import org.bukkit.event.entity.EndermanPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.entity.CreatureType;
import org.bukkit.Material;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.PigZombie;

import com.nijikokun.catacombsregister.payment.Method;
import com.nijikokun.catacombsregister.payment.Methods;

/**
 *
 * @author John Keay
 */
public class CatEntityListener extends EntityListener {
  private static Catacombs plugin;

  public CatEntityListener(Catacombs instance) {
    plugin = instance;
  }

  @Override
  public void onEntityDeath(EntityDeathEvent dEvent){
    Entity eTarget = dEvent.getEntity();
    if(eTarget instanceof LivingEntity) {
      LivingEntity leTarget = (LivingEntity) eTarget;
      Block mob_blk = leTarget.getLocation().getBlock();
        if(plugin.prot.isProtected(mob_blk)) {
        EntityDamageEvent edEvent = leTarget.getLastDamageCause();
        if (edEvent instanceof EntityDamageByEntityEvent) {
          EntityDamageByEntityEvent edbeEvent = (EntityDamageByEntityEvent)edEvent;
          Entity attacker = edbeEvent.getDamager();
          if (attacker instanceof Player) {
             Player player = (Player) attacker;
             int gold = plugin.cnf.Gold();
             Method meth = Methods.getMethod();
             if(meth != null) {
               meth.getAccount(player.getName()).add(gold);
               double bal = meth.getAccount(player.getName()).balance();
    //         plugin.cash.addMajor(player.getName(),gold);
               player.sendMessage("You loot "+gold+" coins, "+meth.format(bal));
             }
             //System.out.println("gold="+gold);

             //  KILL SHOT : "+eTarget+" DMG:"+edEvent.getDamage()+"  HPs:"+leTarget.getHealth());

             // PigZombies drop too much meat for balance
             List<ItemStack> list = dEvent.getDrops();
             for(ItemStack i: list) {
               if(i.getType() == Material.GRILLED_PORK &&
                  plugin.cnf.Chance(50)) {  // Turn half the meat to torches
                 i.setType(Material.TORCH);
               }
             }
             //list.add(new ItemStack(Material.TORCH,1));
          }
        }
      }
    }
  }
/*
  @Override
  public void onEntityDamage(EntityDamageEvent event){
    Entity target = event.getEntity();
    if (event instanceof EntityDamageByEntityEvent) {
      EntityDamageByEntityEvent edbeEvent = (EntityDamageByEntityEvent)event;
      Entity attacker = edbeEvent.getDamager();
      if (attacker instanceof Player) {
        Player player = (Player) attacker;
        if(target instanceof LivingEntity) {
          LivingEntity l = (LivingEntity) target;
          if(plugin.debug)
            player.sendMessage("ATTACK : "+target+" DMG:"+event.getDamage()+"  HPs:"+l.getHealth());
        }
      }
    }
  }
*/
  @Override
  public void onCreatureSpawn(CreatureSpawnEvent sEvent){
    if(sEvent.isCancelled())
      return;
    
    if(true) {
      Location loc = sEvent.getLocation();
      Block blk = loc.getBlock();
      if(plugin.prot.isSuspended(blk)) {
        sEvent.setCancelled(true);
        return;
      }
    }
    
    CreatureSpawnEvent.SpawnReason r = sEvent.getSpawnReason();
    if(r==CreatureSpawnEvent.SpawnReason.SPAWNER) {
      CreatureType type = sEvent.getCreatureType();
      if(type == CreatureType.PIG_ZOMBIE) {
        Location loc = sEvent.getLocation();
        Block blk = loc.getBlock();
        if(plugin.prot.isProtected(blk)) {
          PigZombie z = (PigZombie) sEvent.getEntity();
          if(blk.getLightLevel()>10) {  // Light stops these zombies
            if(plugin.debug)
              System.out.println("[" + plugin.info.getName() + "] PigZombie spawn is cancelled (good light)");
            sEvent.setCancelled(true);
          } else {
            z.setAngry(true);
            if(plugin.debug)
              System.out.println("[" + plugin.info.getName() + "] PigZombie has spawned (making it angry)");
          }
        }
      } else if(type == CreatureType.WOLF) {
        Location loc = sEvent.getLocation();
        Block blk = loc.getBlock();
        if(plugin.prot.isProtected(blk)) {
          if(blk.getLightLevel()>10) {  // Light stops these Wolves
            if(plugin.debug)
              System.out.println("[" + plugin.info.getName() + "] Wolf spawn is cancelled (good light)");
            sEvent.setCancelled(true);
          } else {
            Wolf w = (Wolf) sEvent.getEntity();
            w.setAngry(true);
            if(plugin.debug)
              System.out.println("[" + plugin.info.getName() + "] Wolf has spawned (making it angry)");
          }
        }
      }
    }
  }

  @Override
  public void onEntityExplode(EntityExplodeEvent eEvent){
    if(eEvent.isCancelled())
      return;
   
    Location loc = eEvent.getLocation();
    Block blk = loc.getBlock();
    List<Block> list = eEvent.blockList();

    if(plugin.prot.isProtected(blk) ||
       any_protected(list)) {
      /*
      List<Block> delete = new ArrayList<Block>();
      for(Block b : list) {
        if(plugin.prot.isProtected(b.getWorld().getName(),b.getX(),b.getY(),b.getZ())) {
          delete.add(b);
        }
      }
      for(Block b : delete) {
        list.remove(b);
      }
      */
      list.clear();

      /*  // Work around for 
      if(list.size()>0) {
        w.createExplosion(loc,0); // Create a weak explosion to trigger the sound and animation
        eEvent.setCancelled(true);
        if(plugin.debug)
          System.out.println("[" + plugin.info.getName() + "] Main creeper explosion (cancelled)");
      } else {
        if(plugin.debug)
          System.out.println("[" + plugin.info.getName() + "] Dummy creeper explosion (not cancelled)");
      }
       */
    }
  }
  
  private Boolean any_protected(List<Block> list) {
    for(Block b : list) {
      if(plugin.prot.isProtected(b)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void onEndermanPickup(EndermanPickupEvent eEvent) {
    if(eEvent.isCancelled())
      return;

    Block blk = eEvent.getBlock();
    if(plugin.prot.isProtected(blk))
      eEvent.setCancelled(true);
  }
  
  @Override
  public void onEndermanPlace(EndermanPlaceEvent eEvent) {
    if(eEvent.isCancelled())
      return;

    Block blk = eEvent.getLocation().getBlock();
    if(plugin.prot.isProtected(blk))
      eEvent.setCancelled(true);
  }
}
