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

public enum CatAbility {
  ARROW,
  FIREBALL,
  FIRE_TARGET, // instant on target
  FIRE_NEAR,   // instant on melee
  FIRE_FAR,    // instant on ranged
  FIRE_WAVE,   // delayed around mob
  FIRE_MINE,   // delayed around random player old location
  FIRE_BOMB,   // delayed around random player
  // Delayed if not around mob
  // Delayed if not around player old location
  // Delayed if not around player
  LIGHTNING_TARGET,
  LIGHTNING_NEAR,
  LIGHTNING_FAR,
  LIGHTNING_WAVE,
  LIGHTNING_BOMB,
  ROOT_TARGET,
  ROOT_NEAR,
  ROOT_FAR,
  ROOT_WAVE,
  ROOT_BOMB,
  THROW_TARGET,
  THROW_NEAR,
  THROW_FAR,
  THROW_WAVE,
  THROW_BOMB,
  SUMMON_TARGET,
  SUMMON_NEAR,
  SUMMON_FAR,
  SUMMON_WAVE,
  SUMMON_BOMB,
  SPIN_TARGET,
  SPIN_NEAR,
  SPIN_FAR,
  SPIN_WAVE,
  SPIN_BOMB,
  SHUFFLE,
  WARP_TARGET,
  WARP_NEAR,
  WARP_FAR,
  WARP_WAVE,
  WARP_BOMB,
  WARP_MINE,
  NO_RANGED,
  NO_MELEE;
  
}
