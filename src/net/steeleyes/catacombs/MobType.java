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

import java.util.List;

public class MobType {
  private int hps;
  private String name;
  private String shape;
  private List<CatAbility> abilities;
  
  public MobType(String name,String shape,int hps,List<CatAbility> abilities) {
    this.name = name;
    this.shape = shape;
    this.hps = hps;
    this.abilities = abilities;
  }
  
  @Override
  public String toString() {
    return name+" "+hps+" "+abilities;
  }   
}
