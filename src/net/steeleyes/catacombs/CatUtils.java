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

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.nijikokun.catacombsregister.payment.Method;
import com.nijikokun.catacombsregister.payment.Method.MethodAccount;
import com.nijikokun.catacombsregister.payment.Methods;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class CatUtils {
  
  private static Random rnd = new Random();
  
  public static Boolean Chance(int i) {
    return rnd.nextInt(100)+1 <= i;
  }
  
  public static String giveCash(CatConfig cnf,Entity ent, int gold) {
    if(cnf == null || cnf.GoldOff())
      return null;
    String res = null;
    if (ent instanceof Player) {
      Player player = (Player) ent;
      Method meth = Methods.getMethod();
      if (meth != null) {
        meth.getAccount(player.getName()).add(gold);
        double bal = meth.getAccount(player.getName()).balance();
        res = meth.format(bal);
      }
    }
    return res;
  }
  
  public static Boolean takeCash(Entity ent, int gold, String reason) {
    Boolean res = false;
    if (ent instanceof Player) {
      Player player = (Player) ent;
      Method meth = Methods.getMethod();
      if (meth != null) {
        MethodAccount acc = meth.getAccount(player.getName());
        if(acc.hasEnough(gold)) {
          acc.subtract(gold);
          double bal = acc.balance();
          player.sendMessage("It costs you "+gold+" "+reason+" ("+meth.format(bal)+")");
          res = true;
        } else {
          double bal = acc.balance();
          player.sendMessage("Not enough money "+reason+" ("+meth.format(bal)+")");
        }
      }
    }
    return res;
  }   
  
  // Just a simple on surface check for the moment
  // ToDo: count under trees and shallow overhangs as surface too
  public static Boolean onSurface(Block blk) {
    Location loc = blk.getLocation();
    Block surface = blk.getWorld().getHighestBlockAt(loc);
    return loc.equals(surface);
  }
  
  public static Boolean nobodyNear(Entity ent,double h, double v) {
    if(ent!=null) {
      for(Entity e:ent.getNearbyEntities(h,v,h)) {
        if(e instanceof Player)
          return false;
      }
    }
    return true;
  }
  
  public static List<Player> getPlayerNear(Entity ent, double r) {
    List<Player> list = new LinkedList<Player>();
    if(ent!=null) {
      for(Entity e:ent.getNearbyEntities(r,r,r)) {
        if(e instanceof Player)
          list.add((Player)e);
      }
    }
    return list;
  }
  
  public static List<Player> getPlayerFar(Entity ent, double r1,double r2) {
    assert(r2>r1);
    double r1_sqr = r1*r1;
    List<Player> list = new LinkedList<Player>();
    Location loc = ent.getLocation();
    if(ent!=null) {
      for(Entity e:ent.getNearbyEntities(r2,r2,r2)) {
        if(e instanceof Player) {
          if(loc.distanceSquared(ent.getLocation()) > r1_sqr) {
            list.add((Player)e);
          }
        }
      }
    }
    return list;
  }  
  
  public static int countPlayerNear(Entity ent,double h, double v) {
    int cnt = 0;
    if(ent!=null) {
      for(Entity e:ent.getNearbyEntities(h,v,h)) {
        if(e instanceof Player)
          cnt++;
      }
    }
    return cnt;
  }
  
  public static int countCreatureNear(Entity ent,double h, double v) {
    int cnt = 0;
    if(ent!=null) {
      for(Entity e:ent.getNearbyEntities(h,v,h)) {
        if(e instanceof Creature)
          cnt++;
      }
    }
    return cnt;
  }  
  
  public static LivingEntity getDamager(EntityDamageEvent evt) {
    Entity damager = null;
    
    if(evt instanceof EntityDamageByEntityEvent) {
      EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) evt;
      damager = e.getDamager();
      if (damager instanceof Projectile) {
        damager = ((Projectile) damager).getShooter();
      }
    }
    if(damager instanceof LivingEntity) {
      return (LivingEntity)damager;
    }
    return null;
  }

  public static Boolean hasAxe(Player player) {
    ItemStack stk = player.getItemInHand();
    if(stk==null)
      return false;
    
    Material mat = stk.getType();
    //System.out.println("[Catacombs] "+mat+" "+stk.getDurability());
    
    Boolean axe =  (mat==Material.WOOD_AXE ||
            mat==Material.GOLD_AXE ||
            mat==Material.STONE_AXE ||
            mat==Material.IRON_AXE ||
            mat==Material.DIAMOND_AXE);
    return axe;
  }
  public static Boolean hasBow(Player player) {
    ItemStack stk = player.getItemInHand();
    if(stk==null)
      return false;
    
    Material mat = stk.getType();
    
    return mat==Material.BOW;
  }  
  
  @SuppressWarnings( "deprecation" )
  public static void improveDurability(Player player, int amt) {
    ItemStack stk = player.getItemInHand();
    if(stk != null) {
      int curr = stk.getDurability();
      curr -= amt;
      if(curr<0)
        curr = 0;
      stk.setDurability((short)curr);
      //System.out.println("[Catacombs] durability "+stk.getType()+" new="+curr+" boost="+amt);
      player.updateInventory();
    }
  }
  
  public static int getThreatFixDurability(Player player, int dmg) {
    ItemStack stk = player.getItemInHand();
    int threat = dmg;
    if(stk==null)
      return threat;
    
    Material mat = stk.getType();
    
    //short durability = stk.getDurability();
    int chance = 0;
    Boolean hasAxe = (mat==Material.WOOD_AXE ||  mat==Material.STONE_AXE || mat==Material.GOLD_AXE);
    
    if(mat == Material.WOOD_AXE ||
       mat==Material.WOOD_SWORD) {
      threat += 4;
      chance = 50;
    } else if(mat == Material.GOLD_AXE ||
              mat==Material.GOLD_SWORD) {
      threat += 5;
      chance = 80;
    } else if(mat == Material.STONE_AXE ||
              mat==Material.STONE_SWORD) {
      threat += 2;
      chance = 0;
    }
    int boost = (Chance(chance))?1:0;
    if(hasAxe) boost += 1;
    improveDurability(player,boost);
    //durability -= boost;
    //if(durability<0)
    //  durability = 0;
    //stk.setDurability((short)durability);
    //player.updateInventory();
    //System.out.println("[Catacombs] durability "+mat+" new="+durability+" boost="+boost);
    return threat;
  }
  
  public static void useOne(Player player) {
    ItemStack stk = player.getItemInHand();
    int num = stk.getAmount();
    if (num > 1) {
      stk.setAmount(num - 1);
    } else {
      //ItemStack air = new ItemStack(Material.AIR);
      player.setItemInHand(null);
    }  
  }
  
  public static Boolean tankWeapon(Player player) {
    ItemStack stk = player.getItemInHand();
    if(stk==null)
      return false;
    
    Material mat = stk.getType();
    return mat==Material.WOOD_AXE ||
           mat==Material.STONE_AXE ||
           mat==Material.GOLD_AXE ||
           mat==Material.WOOD_SWORD ||
           mat==Material.STONE_SWORD ||
           mat==Material.GOLD_SWORD;
  }
  
  public static int armourEffect(Player player) {
    if(tankWeapon(player))
      return 0;
       
    for(ItemStack stk : player.getInventory().getArmorContents()) {
      Material mat = stk.getType();
      if(mat==Material.DIAMOND_BOOTS ||
         mat==Material.DIAMOND_LEGGINGS ||
         mat==Material.DIAMOND_CHESTPLATE ||
         mat==Material.DIAMOND_HELMET)
        return -3;
      if(mat==Material.IRON_BOOTS ||
         mat==Material.IRON_LEGGINGS ||
         mat==Material.IRON_CHESTPLATE ||
         mat==Material.IRON_HELMET)
        return -2;
    }
    return 0;
  }
  
  public static int armourPoints(Player player) {
    int total = 0;
    
    for(ItemStack stk : player.getInventory().getArmorContents()) {
      Material mat = stk.getType();
      if(mat==Material.DIAMOND_BOOTS) total+=3;
      else if(mat==Material.DIAMOND_LEGGINGS) total+=6;
      else if(mat==Material.DIAMOND_CHESTPLATE) total+=8;
      else if(mat==Material.DIAMOND_HELMET) total+=3;
      else if(mat==Material.IRON_BOOTS) total+=2;
      else if(mat==Material.IRON_LEGGINGS) total+=5;
      else if(mat==Material.IRON_CHESTPLATE) total+=6;
      else if(mat==Material.IRON_HELMET) total+=2;
      else if(mat==Material.GOLD_BOOTS) total+=1;
      else if(mat==Material.GOLD_LEGGINGS) total+=3;
      else if(mat==Material.GOLD_CHESTPLATE) total+=5;
      else if(mat==Material.GOLD_HELMET) total+=2;
      else if(mat==Material.LEATHER_BOOTS) total+=1;
      else if(mat==Material.LEATHER_LEGGINGS) total+=2;
      else if(mat==Material.LEATHER_CHESTPLATE) total+=3;
      else if(mat==Material.LEATHER_HELMET) total+=1;
    }
    return total;
  }
  
  public static <T extends Enum<T>> T getEnumFromString(Class<T> c, String string) {
    if (c != null && string != null) {
      try {
        return Enum.valueOf(c, string.trim().toUpperCase());
      } catch (IllegalArgumentException ex) {
      }
    }
    return null;
  }
    
  public static <K, V extends Comparable<V>> List<Entry<K, V>> sortByValue(Map<K, V> map) {
    List<Entry<K, V>> entries = new ArrayList<Entry<K, V>>(map.entrySet());
    Collections.sort(entries, new ByValue<K, V>());
    return entries;
  }

  private static class ByValue<K, V extends Comparable<V>> implements Comparator<Entry<K, V>> {
    public int compare(Entry<K, V> o1, Entry<K, V> o2) {
      return o2.getValue().compareTo(o1.getValue());
    }
  }
}

