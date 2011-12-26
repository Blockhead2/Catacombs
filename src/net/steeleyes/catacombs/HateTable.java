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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class HateTable {
  private final static double RANGE = 16.0;
  private final static double RANGE_SQR = RANGE * RANGE;
  private final static double PULL_THREAT = 1.10;
  private final static Boolean debug = false;
  
  private CatConfig cnf;
  
  private LivingEntity self;  // Needed for distance checks
  private LivingEntity aggro = null;
  private final Map<LivingEntity, Integer> threats = new HashMap<LivingEntity, Integer>();
  private String name;

  public HateTable(CatConfig cnf,LivingEntity self,String name) {
    this.cnf = cnf;
    this.self = self;
    this.name = name;
  }
  
  @Override
  public String toString() {
    String str = "HateTable:";
    for(Entry<LivingEntity,Integer> e : CatUtils.sortByValue(threats)) {
      int threat = e.getValue();
      LivingEntity ent = e.getKey();
      String name = (ent instanceof Player)?((Player)ent).getName():ent.toString();
      str += name+"("+threat+") ";
    }
    return str;
  }

  // Player hits this target and causes a given threat
  public void addThreat(LivingEntity ent, int val) {
    int threat = (threats.containsKey(ent)) ? threats.get(ent) + val : val;
    threats.put(ent, threat);
    updateThreat();
  }

  // Player does a drop aggro operation
  public Boolean zeroThreat(LivingEntity ent) {
    if (threats.containsKey(ent)) {
      threats.put(ent, 0);
      if (aggro != null && ent.equals(aggro)) {
        updateTarget();
      }
      return true;
    }
    return false;
  }

  // Player dies/leaves/Teleports/kicked or out of range
  public Boolean removeThreat(LivingEntity ent) {
    if (threats.containsKey(ent)) {
      threats.remove(ent);
      if (aggro != null && ent.equals(aggro)) {
        updateTarget();
      }
      return true;
    }
    return false;
  }

  // match threat of target and pull aggro
  public Boolean taunt(LivingEntity ent) {
    if(ent == null || aggro == null || aggro.equals(ent))
      return false;
    
    Integer curr = threats.get(aggro);
    if(curr==null) {
      curr = -1;  
    }
    threats.put(ent, curr);
    aggro = ent;
    ((Creature)self).setTarget(aggro);
    return curr>=0;
  }
  
  public void updateTarget() {
    updateThreat();
    if(self instanceof Creature && aggro != null)
      ((Creature)self).setTarget(aggro);
  }

  public LivingEntity target() {
    return aggro;
  }
  
  public int size() {
    return threats.size();
  }

  public Boolean isHated(LivingEntity ent) {
    return threats.containsKey(ent);
  }

  public Set<LivingEntity> attackers() {
    return threats.keySet();
  }

  private Entry<LivingEntity, Integer> highest() {
    Entry<LivingEntity, Integer> hi = null;
    if (!threats.isEmpty()) {
      Location sloc = self.getLocation();
      int max = -1;

      Iterator<Map.Entry<LivingEntity, Integer>> it = threats.entrySet().iterator();
      while (it.hasNext()) {
        Entry<LivingEntity, Integer> e = it.next();

        Entity ent = e.getKey();
        Location eloc = ent.getLocation();
        if (sloc.distanceSquared(eloc) > RANGE_SQR) {
          if(ent instanceof Player) {
            ((Player)ent).sendMessage("You dropped out of range "+name);
          }
          if (debug) {
            System.out.println("[Aggro] out of range " + ent);
          }
          it.remove(); // attacker is out of range (no exp/loot for them)
        } else {
          int threat = e.getValue();
          if (threat > max) {
            max = threat;
            hi = e;
          }
        }
      }
    }
    return hi;
  }

  private void updateThreat() {
    Entry<LivingEntity, Integer> hi = highest();
    if (hi == null) {
      // Target dropped and nobody to switch to
      aggro = null;
    } else {
      LivingEntity max_who = hi.getKey();
      if(max_who == null) {
        System.err.println("[Catacombs] max_who is null "+this);
      }
      if (aggro == null) { // No target
        aggro = max_who;
        //if(aggro instanceof Player) {
        //  ((Player)aggro).sendMessage(name+" heads towards you");
        //}
        if (debug) {
          System.out.println("[Aggro] new target " + aggro);
        }
        return;
      }

      if (aggro.equals(max_who)) { // already targetted
        return;
      }
      if(aggro == null) {
        System.err.println("[Catacombs] aggro is null "+this);
      }
      int max = hi.getValue();
      int curr = (threats.containsKey(aggro))?threats.get(aggro):-1;
      if (curr == -1 || (double)max > ((double)curr) * PULL_THREAT) {  // Pull aggro
        aggro = max_who;
        if(aggro instanceof Player) {
          ((Player)aggro).sendMessage(name+" switches to you");
        }
        if (debug) {
          System.out.println("[Aggro] pulled aggro " + aggro);
        }
      }
    }
  }
  

}

