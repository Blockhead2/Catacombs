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

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CatPlayer {
  Player player;
  Location respawn = null;
  // Hit timer
  // Doing splash threat?
  // threat weapon? how much threat
  // List of monsters on threat table for
  
  public CatPlayer(Player player) {
    this.player = player;
  }
  
  public Player getPlayer() {
    return player;
  }

  public Location getRespawn() {
    return respawn;
  }

  public void setRespawn(Location respawn) {
    this.respawn = respawn;
  }
  
}
