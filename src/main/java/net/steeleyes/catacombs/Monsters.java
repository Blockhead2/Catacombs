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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

public class Monsters {
  private static final long HEAL_PLAYER = 2500;
  private static final long SWING_PLAYER = 1900;
  private static final long SWING_MONSTER = 1900*2;
  
  private Catacombs plugin;
  
  private Map<LivingEntity,CatMob> monsters = new HashMap<LivingEntity,CatMob>();
  private Map<LivingEntity,Long> last_strike = new HashMap<LivingEntity,Long>();
  
  public Monsters(Catacombs plugin) {
    this.plugin = plugin;
  }
  
  public Boolean isManaged(LivingEntity ent) {
    return monsters.containsKey(ent);
  }
  
  public void add(CatMob mob) {
    monsters.put(mob.getEntity(),mob); 
  }
  
  public void remove(LivingEntity ent) {
    monsters.remove(ent);
  }  
  public CatMob get(LivingEntity ent) {
    return monsters.get(ent);
  }
  
  public int size() {
    return monsters.size();
  }
  
  // ToDo: remove player threat more efficiently than this
  public void removeThreat(Player player) {
    for(Entry<LivingEntity,CatMob> e:monsters.entrySet()) {
      e.getValue().removeThreat(player);
    } 
  }
  
  public class WildResp {
    public Boolean wild;
    public long delta;
    public WildResp(Boolean wild, long delta) {
      this.wild = wild;
      this.delta = delta;
    }
  }
  
  public WildResp isWild(LivingEntity damager, long swing,CatMob mob) {
    int pct = (mob==null)?0:mob.getHealth();
    Boolean wild = false;
    long delta = 0;
    long now = Calendar.getInstance().getTimeInMillis();
    long new_time = now;
    if(last_strike.containsKey(damager)) {
      long then = last_strike.get(damager);
      delta = now-then;
      if(delta < swing) {
        if(damager instanceof Player) {
          //((Player)damager).sendMessage(String.format(ChatColor.DARK_GRAY+"(%.3f <  %.3f) WILD %3d%%"+ChatColor.WHITE, (float)delta/1000.0, (float)swing/1000.0,pct));
          new_time = now-(swing/2);
        } else {
          new_time = then;
        }
        wild = true;
      } else {
        //if(damager instanceof Player) {
        //  ((Player)damager).sendMessage(String.format("(%.3f >= %.3f)     %3d%%", (float)delta/1000.0, (float)swing/1000.0,pct));
        //}        
      }
    }
    last_strike.put(damager,new_time);
    return new WildResp(wild,delta);
  }
  
  public void playerHeals(CatConfig cnf,Player healer, Player healee) {
    if(!healee.isDead()){
      WildResp resp = isWild(healer,HEAL_PLAYER,null);
      if(!resp.wild) {
        int health = healee.getHealth();
        if(health<20) {
          CatUtils.improveDurability(healer,2);
          healee.setHealth(Math.min(20, health+2));
          //healer.sendMessage("You heal "+healee.getName()+" "+(healee.getHealth()*5)+"%");
          healee.sendMessage(healer.getName()+" heals you");
          splashThreat(healee,healer,0,10,5,2);
          ChatColor col = (resp.delta-HEAL_PLAYER > 500)?ChatColor.YELLOW:ChatColor.GREEN;
          healer.sendMessage(String.format(col+"%.3f"+ChatColor.WHITE+" %.1f %s(%d%%)",
            (float)resp.delta/1000.0, (float)HEAL_PLAYER/1000.0,healee.getName(),(healee.getHealth()*5)));
        } else {
          CatUtils.improveDurability(healer,3);
          healer.sendMessage(healee.getName()+" is on full health");
        }
      } else {
        CatUtils.improveDurability(healer,2);
        healer.sendMessage(String.format(ChatColor.RED+"%.3f"+ChatColor.DARK_GRAY+" %.1f WILD %s(%d%%)"+ChatColor.WHITE,
          (float)resp.delta/1000.0, (float)HEAL_PLAYER/1000.0,healee.getName(),(healee.getHealth()*5)));
      }
    }
  }
  
  public void monsterHits(CatConfig cnf, EntityDamageEvent evt) {
    LivingEntity damagee = (LivingEntity) evt.getEntity();
    LivingEntity damager = CatUtils.getDamager(evt);
    
    if(damager == null)
      return;
    
    assert(damagee instanceof Player);
    DamageCause cause = evt.getCause();
    if(cause == DamageCause.PROJECTILE)
      return;
   
    CatMob mob = monsters.get(damager);
    if(mob != null)
      mob.canHit();
    
    WildResp resp = isWild(damager,SWING_MONSTER,null);
    if(resp.wild) {
      evt.setCancelled(true);
      return;
    }
    
    if(false) {
      final int num = CatUtils.armourPoints(((Player)damagee));
      final Player pp = (Player) damagee;
      final int dmg = 10; //evt.getDamage();
      evt.setDamage(dmg);
      pp.setHealth(pp.getMaxHealth());
      final int before = pp.getHealth();
      Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin,new Runnable() {
        public void run() {
          int actual_dmg = before-pp.getHealth();
          System.out.println("[Catacombs] delayed ac="+num+" dmg="+dmg+" actual="+actual_dmg);
        }
      }, 1);
    }
  }
  
  public void playerHits(CatConfig cnf,EntityDamageEvent evt) {
    LivingEntity damagee = (LivingEntity) evt.getEntity();
    CatMob mob = monsters.get(damagee);
    LivingEntity damager = (LivingEntity) CatUtils.getDamager(evt);
    int dmg = evt.getDamage();
        
    evt.setDamage(1);
    if(damager instanceof Player) {
      Player player = (Player) damager;
      WildResp resp = isWild(damager,SWING_PLAYER,mob);
      int adjust = CatUtils.armourEffect(player);
      if(resp.wild) {
        if(!CatUtils.hasBow(player)) {
          String adjustString = (adjust==0)?"":"armour("+ChatColor.RED+adjust+ChatColor.DARK_GRAY+") ";
          player.sendMessage(String.format(ChatColor.RED+"%.3f"+ChatColor.DARK_GRAY+" %.1f WILD %s%s(%d%%) -> %s"+ChatColor.WHITE,
            (float)resp.delta/1000.0, (float)SWING_PLAYER/1000.0,adjustString,mob,mob.getHealth(),mob.getTargetName()));
          evt.setCancelled(true);
          return;
        }
      }
      ItemStack stk = player.getItemInHand();
      Material mat = stk.getType();
      if(mat == Material.SPIDER_EYE || mat == Material.BONE) {
        //System.out.println("[Catacombs] Taunt "+mat+" "+dmg);
        if(mob.taunt(player)) {
          CatUtils.useOne(player);
          player.sendMessage("You taunt "+mob+" with a "+mat);
        }
      } else if (mat == Material.ROTTEN_FLESH) {
        //System.out.println("[Catacombs] Feign death "+mat+" "+dmg);
        if(mob.feignDeath(player)) {
          CatUtils.useOne(player);
          player.sendMessage("You feed "+mob+" "+mat);
        }
      }
      int threat = CatUtils.getThreatFixDurability(player,dmg);
      dmg += adjust;
      if(dmg<1) dmg = 1;
      if(mob.hit(damager, dmg, threat)) {
        evt.setDamage(100);
      }
      String adjustString = (adjust==0)?"":"armour("+ChatColor.RED+adjust+ChatColor.WHITE+") ";
      ChatColor col = (resp.delta-SWING_PLAYER > 500)?ChatColor.YELLOW:ChatColor.GREEN;
      player.sendMessage(String.format(col+"%.3f"+ChatColor.WHITE+" %.1f %s%s(%d%%) -> %s",
        (float)resp.delta/1000.0, (float)SWING_PLAYER/1000.0,adjustString,mob,mob.getHealth(),mob.getTargetName()));        

      // Splash threat when using axes
      if(CatUtils.hasAxe(player)) {
        splashThreat(damagee,damager,4,4,3,2);
      }
    } else {
      if(mob.hit(dmg))
        evt.setDamage(100);
    }
  }
  
  public void splashThreat(LivingEntity around, LivingEntity attacker, int dmg, int threat, int h, int v) {
    for(Entity e : around.getNearbyEntities(h,v,h)) {
      if(e instanceof LivingEntity) {
        CatMob other = monsters.get((LivingEntity)e);
        if(other!=null) {
          if(other.hit(attacker,dmg,threat)) {
            dmg = 100;
          } else {
            dmg = 1;
          }
          LivingEntity ent = (LivingEntity)e;
          //System.out.println("[Catacombs] Splash "+threat+" "+other);
          ent.setHealth(ent.getMaxHealth());
          ent.damage(dmg); // Make the mob go red
        }
      }
    }    
  }

}
