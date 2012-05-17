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
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Players {
  public final Map<Player,CatPlayer> players = new HashMap<Player,CatPlayer>();
  
  public final Map<Player,CatGear> gear = new HashMap<Player,CatGear>();
  
  public Boolean isManaged(Player p) {
    return players.containsKey(p);
  }
  
  public Boolean hasGear(Player player) {
    return gear.containsKey(player);
  }
  
  public void saveGear(Player player) {
    CatGear stuff = new CatGear(player);
    gear.put(player, stuff);
  }
  
  public void dropGear(Player player) {
    if(gear.containsKey(player)) {
      CatGear stuff = gear.get(player);
      stuff.dropGear();
    }

  }
  
  public void restoreGear(Player player) {
    if(gear.containsKey(player)) {
      CatGear stuff = gear.get(player);
      stuff.restoreGear();
      gear.remove(player);
    }
  }
  
  public void setRespawn(Player player,Location loc) {
    if(players.containsKey(player)) {
      CatPlayer cp = players.get(player);
      cp.setRespawn(loc);
    } else {
      CatPlayer cp = new CatPlayer(player);
      players.put(player,cp);
      cp.setRespawn(loc);
    }
  }
  
  public Location getRespawn(Player player) {
    if(players.containsKey(player)) {
      return players.get(player).getRespawn();
    }
    return null;
  }
  
  public void add(CatPlayer cp) {
    players.put(cp.getPlayer(),cp); 
  }
  
  public void remove(Player player) {
    players.remove(player);
  }  
  
  public CatPlayer get(Player player) {
    return players.get(player);
  }
  
  public int size() {
    return players.size();
  }
}
