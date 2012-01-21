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
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;

public class CatMob {
  private LivingEntity ent;
  private String name;
  private HateTable hate;
  private int max_hps;
  private int hps;
  //private int damage;
  private int exp;
  private int cash;
  private int level;
  private long gotHit = 0;
  private long didHit = 0;
  private CatCreature creature;
  
  private CatConfig cnf;
  
  public CatMob(CatConfig cnf,CatCreature c, World world, Location loc) {
    this.cnf = cnf;
    creature = c;
    ent = creature.spawn(world, loc);
    common_init();
  }
  
  public CatMob(CatConfig cnf,CreatureType c,LivingEntity e) {
    this.cnf = cnf;
    creature = CatCreature.getType(c);
    if(creature==null) {
      System.err.println("[Catacombs] Creature name problem "+c);
    } else {
      ent = creature.spawn(e);
    }
    common_init();
  }
  
  @Override
  public String toString() {
    return name+"-"+level;
  }
  
  private void common_init() {
    name = creature.toString();
    name = name.substring(0,1).toUpperCase()+name.substring(1).toLowerCase();

    ent.setHealth(ent.getMaxHealth()); // Make sure the entity has plenty of hits for us to work with
    int num = CatUtils.countPlayerNear(ent, cnf.GroupRadius(), cnf.GroupDepth());
    level = (num<1)?1:num;
    cash = 4+level*2;
    exp = 4+level*2;
    //damage = 3;
    max_hps = (int)(creature.getHits()+cnf.GroupHpFactor()*creature.getHits()*(level-1));
    hps = max_hps;
    hate = new HateTable(cnf,ent,this.toString());
  }
  
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
  
  public CatCreature getCreature() {
    return creature;
  }
  
  public void death(EntityDeathEvent evt) {
    if(ent != null) {
      if(creature != CatCreature.SILVERFISH) {
        // "Share" cash and exp (simply give the cash and exp to all
        //   attackers to encourage team work)
        for(Entity attacker : hate.attackers()) {
          giveExp(attacker, exp);
          String bal = CatUtils.giveCash(cnf,attacker, cash);
          if(bal != null) {
            ((Player)attacker).sendMessage(cash+" coins ("+bal+") "+this);
          }
        }
      }
      if(evt != null)
        evt.setDroppedExp(0);
      
      // ToDo: end dungeon if it's the final boss
      
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
