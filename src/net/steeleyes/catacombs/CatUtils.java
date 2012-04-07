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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;


public class CatUtils {
  
  private static Random rnd = new Random();
  
  public static Boolean Chance(int i) {
    return rnd.nextInt(100)+1 <= i;
  }
  public static int Between(int lo,int hi) {
    assert(lo<=hi);
    return rnd.nextInt(hi-lo+1)+lo;
  }
  
  // Is a Block soild? (this is a partial list)
  public static Boolean isSolid(Block blk) {
    Material mat = blk.getType();
    return mat != Material.WOODEN_DOOR &&
            mat != Material.IRON_DOOR &&
            mat != Material.AIR &&
            mat != Material.TORCH &&
            mat != Material.WEB &&
            mat != Material.RED_MUSHROOM &&
            mat != Material.BROWN_MUSHROOM; 
  }
  
  public static List<String> getKeys(FileConfiguration config, String path) {
    if(config.contains(path)) {
      List<String> list = new ArrayList<String>();
      for(String s:config.getConfigurationSection(path).getKeys(false)) {
        list.add(s);
      }
      return list;
    }
    return null;
  }  
  
  public static Object getSP(FileConfiguration fcnf,String path) {
    Object property = null;
    if (property == null) {
      property = fcnf.get(path);
    }
    if(property == null) {
      System.err.println("[Catacombs] No configuration attribute called "+path+" or <style>."+path);
    }
    return property;    
  }
  
  @SuppressWarnings("unchecked")
  public static Boolean getSBoolean(FileConfiguration fcnf,String path) {
    return (Boolean) getSP(fcnf,path);
  }
  @SuppressWarnings("unchecked")
  public static Integer getSInt(FileConfiguration fcnf,String path) {
    return (Integer) getSP(fcnf,path);
  }
  @SuppressWarnings("unchecked")
  public static Double getSDouble(FileConfiguration fcnf,String path) {
    return (Double) getSP(fcnf,path);
  }
  @SuppressWarnings("unchecked")
  public static String getSString(FileConfiguration fcnf,String path) {
    return (String) getSP(fcnf,path);
  }
  @SuppressWarnings("unchecked")
  public static List<Boolean> getSBooleanList(FileConfiguration fcnf,String path) {
    return (List<Boolean>) getSP(fcnf,path);
  }
  @SuppressWarnings("unchecked")
  public static List<Integer> getSIntList(FileConfiguration fcnf,String path) {
    return (List<Integer>) getSP(fcnf,path);
  }
  @SuppressWarnings("unchecked")
  public static List<Double> getSDoubleList(FileConfiguration fcnf,String path) {
    return (List<Double>) getSP(fcnf,path);
  }
  @SuppressWarnings("unchecked")
  public static List<String> getSStringList(FileConfiguration fcnf,String path) {
    return (List<String>) getSP(fcnf,path);
  } 
  
  // Just a simple on surface check for the moment
  // ToDo: count under trees and shallow overhangs as surface too
  public static Boolean onSurface(Block blk) {
    Location loc = blk.getLocation();
    Block spawn = loc.getBlock();
    Block surface = blk.getWorld().getHighestBlockAt(loc);
    return spawn.getY() == surface.getY();
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
  
  public static <T> void pickOne(List<T> list) {
    if(!list.isEmpty()) {
      int num = rnd.nextInt(list.size());
      T o = list.get(num);
      list.clear();
      list.add(o);
    }
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
    
  public static long parseTime(String s) {
    Pattern p = Pattern.compile("(\\d+)([smhd])");
    Matcher m = p.matcher(s);
    long num = 0;
    while(m.find()) {
      int i = Integer.parseInt(m.group(1));
      String unit = m.group(2);
      //    System.out.println("[Catacombs]   parse "+unit+" "+i);

      if(unit.equals("m"))
        i=i*60;
      if(unit.equals("h"))
        i=i*60*60;
      if(unit.equals("d"))
        i=i*60*60*24;
      num+=i;
    }
    //System.out.println("[Catacombs] Parse "+s+" = "+num+"sec(s)");

    return num*1000;
  }
  
  public static String formatTime(Long num) {
    String str = "";
    num = num / 1000;
    if(num>=60*60*24) {
      int d = (int)(num/(60*60*24));
      str += d+"d";
      num = num - (d*60*60*24);
    }
    if(num>=60*60) {
      int d = (int)(num/(60*60));
      str += d+"h";
      num = num - (d*60*60);
    }
    if(num>=60) {
      int d = (int)(num/(60));
      str += d+"m";
      num = num - (d*60);
    }
    if(num>0) {
      str += num+"s";
    }
    return str;
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

