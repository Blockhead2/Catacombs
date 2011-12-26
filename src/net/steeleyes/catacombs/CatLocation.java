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

public class CatLocation {
  
  public enum Type {
    END_CHEST,
    DUNGEON_DOOR
  }
  
  //store world too?
  //store back link to dungeon?
  
  private Vector location;
  private Type type;
  
  public CatLocation(Type type, Vector location) {
    this.type = type;
    this.location = location;
  }
  
  public CatLocation(Type type, int x, int y, int z) {
    this.type = type;
    this.location = new Vector(x,y,z);
  }  
}
