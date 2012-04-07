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
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;

public class CatMob {
  private Catacombs plugin;
  //private CatConfig cnf;
  
  private LivingEntity ent;
  private String name;
  private HateTable hate;
  private int max_hps;
  private int hps;
  private int exp;
  private int cash;
  private int level;
  private long gotHit = 0;
  private long didHit = 0;
  private int extra_damage=0;
  private int extra_armour=0;
  
  private MobType type=null;
  private CatAbility casting = null;
  private Object focus;
  
  private int tickId=0;
  
  private EntityType tmp_type;
  
  //private List<Object> targets; 
  //private List<CatAbility> running = new LinkedList<CatAbility>();
  
  
  public CatMob(Catacombs plugin,MobType type, World world, Location loc) {
    this.plugin = plugin;
    //this.cnf = plugin.cnf;
    this.type = type;
    ent = type.spawn(world,loc);
    name = type.getName();

    common_init();
  }
  
  public CatMob(Catacombs plugin,EntityType c,LivingEntity e) {
    this.plugin = plugin;
    name = c.toString();
    //this.cnf = plugin.cnf;
    
    // Need to figure out a default MobType from the creaturetype
//    type=plugin.mobtypes.get(c.toString());    
//    if(type==null) {
//      System.err.println("[Catacombs] Can't find a monster called '"+c+"'");
//    } else {
//      ent = type.spawn(e);
//    }
    
    common_init();
  }
  
  @Override
  public String toString() {
    if(type != null)
      return type.getName()+"@"+type.getHps();
    return name+"-"+level;
  }
  
  private void common_init() {
    name = name.substring(0,1).toUpperCase()+name.substring(1).toLowerCase();

    ent.setHealth(ent.getMaxHealth()); // Make sure the entity has plenty of hits for us to work with
    int num = CatUtils.countPlayerNear(ent, plugin.cnf.GroupRadius(), plugin.cnf.GroupDepth());
    level = (num<1)?1:num;
    cash = 4+level*2;
    exp = 4+level*2;
    //damage = 3;
    max_hps = (int)(type.getHps()+plugin.cnf.GroupHpFactor()*type.getHps()*(level-1));
    hps = max_hps;
    hate = new HateTable(plugin.cnf,ent,this.toString());
    
//    tickId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,
//            new Runnable() {
//
//              public void run() {
//                System.out.println("[Catacombs] tick "+this);
//              }
//            }, type.getSpeed()*2, type.getSpeed());

//    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
//            new Runnable() {
//
//              public void run() {
//                System.out.println("[Catacombs] End tick "+this);
//                plugin.getServer().getScheduler().cancelTask(tickId);
//                tickId = 0;
//              }
//            }, 120);
  }

  public Object getFocus() {
    return focus;
  }

  public void setFocus(Object focus) {
    this.focus = focus;
  }
//
//  public List<Object> getTargets() {
//    return targets;
//  }
//
//  public void setTargets(List<Object> targets) {
//    this.targets = targets;
//  }
  
  public Boolean hit(LivingEntity attacker,int dmg,int threat) {
    if(attacker instanceof Player) {
      hate.addThreat(attacker, threat);
      ((Creature)ent).setTarget((LivingEntity)hate.target());
    }        
    return hit(dmg);
  }
  
  public void canHit() {
    didHit = Calendar.getInstance().getTimeInMillis();
  }
  
  public Boolean hit(int dmg) {
    gotHit = Calendar.getInstance().getTimeInMillis();
    hps = hps - dmg;
    if(hps<=0) {
      ent.setHealth(1); 
    } else {
      ent.setHealth(ent.getMaxHealth());
    }
    return hps<=0;
  }
  
  public Boolean taunt(Player player) {
    Boolean res = hate.taunt(player);
    return res;
  }
  public Boolean feignDeath(Player player) {
    Boolean res = hate.zeroThreat(player);
    return res;
  }  
  public void heal(int dmg) {
    hps += dmg;
    if(hps>max_hps)
      hps = max_hps;
  }
  
  public void heal() {
    hps = max_hps;
  }
  
  public int getHealth() {
    return 100*hps/max_hps;
  }  
  
  public Boolean isDead() {
    return hps<=0;
  }
  
  public LivingEntity getEntity() {
    return ent;
  }
  
  public int getLevel() {
    return level;
  }
  
  public void death(EntityDeathEvent evt) {
    if(ent != null) {
      if(!plugin.cnf.GoldOff() && type.getShape() != MobShape.SILVERFISH) {
        // "Share" cash and exp (simply give the cash and exp to all
        //   attackers to encourage team work)
        for(Entity attacker : hate.attackers()) {
          giveExp(attacker, exp);
          String bal = plugin.giveCash(plugin.cnf,attacker, cash);
          if(bal != null) {
            ((Player)attacker).sendMessage(cash+" coins ("+bal+") "+this);
          }
        }
      }
      if(evt != null)
        evt.setDroppedExp(0);
      
      // ToDo: end dungeon if it's the final boss
      if(tickId>0) {
        plugin.getServer().getScheduler().cancelTask(tickId);
        tickId = 0;
      }
      hate=null;
      ent=null;
    }
  }
   
  public static void giveExp(Entity ent, int amt) {
    if (ent instanceof Player) {
      Player player = (Player) ent;
      player.giveExp(amt);
    }
  }   
  
  public String getTargetName() {
    LivingEntity t = hate.target();
    if(t != null && t instanceof Player)
      return ((Player)t).getName();
    return "";
  }
  
  public Player getTarget() {
    LivingEntity t = hate.target();
    if(t instanceof Player)
      return (Player)t;
    return null;
  }  
  public void target(EntityTargetEvent evt) {      
    // somebody on hate then target them. 
    if(hate.size()>0) {
      evt.setTarget(hate.target());
    } else {
      if(evt.getTarget() instanceof LivingEntity) {
        LivingEntity next = (LivingEntity) evt.getTarget();
        if(next != null) {
          if(!next.isDead()) {
            hit(next,0,1);
          }
        }
      }
    }
  }
  
    // Player does a drop aggro operation
  public void zeroThreat(LivingEntity ent) {
    hate.zeroThreat(ent);
  }

  // Player dies/leaves/Teleports/kicked or out of range
  public void removeThreat(LivingEntity ent) {
    hate.removeThreat(ent);
  }
  
}
