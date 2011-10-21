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

package net.steeleyes.maps;

import java.util.List;
import java.util.Map;
import java.util.EnumMap;
import java.util.ArrayList;
import java.util.Random;

public class PrePlanned {   
  private Grid grid;
  private String name;
  public enum Type { HUT, ROOM }
  private Type type;
  private Map<Direction,List<Integer>> access = new EnumMap<Direction,List<Integer>>(Direction.class); 

  public PrePlanned(String name, Type type, String[] map) {
    this.name = name;
    this.grid = new Grid(map);
    this.type = type;
    //grid.show();
    initialize_access(Direction.NORTH);
    initialize_access(Direction.EAST);
    initialize_access(Direction.SOUTH);
    initialize_access(Direction.WEST);
  }
  
  private void initialize_access(Direction dir) {
    List<Integer> tmp = new ArrayList<Integer>();
    access.put(dir,tmp);
    int x = (dir.vertical())?1:((dir==Direction.WEST)?0:grid.getSize().x-1);
    int y = (dir.vertical())?((dir==Direction.SOUTH)?0:grid.getSize().y-1):1;
    int sz = (dir.vertical())?grid.getSize().x:grid.getSize().y;
    //System.out.println("[catacombs] Init "+name+" "+dir);
    for(int o=1;o<sz-1;o++) {
      if(grid.get(x,y)==Square.WALL &&
         grid.isWall(dir.left_x(x),dir.left_y(y)) &&
         grid.isWall(dir.right_x(x),dir.right_y(y)) &&
         // It's only a valid access point if the first square in the room is also floor
         grid.isFloor(dir.backwards_x(x),dir.backwards_y(y))
              ) {
        tmp.add(o);
        //System.out.println("[catacombs]   ("+x+","+y+") YES "+o);
      } else {
        //System.out.println("[catacombs]   ("+x+","+y+") NO  "+o);
      }
      if(dir == Direction.NORTH || dir == Direction.WEST) {
        x = dir.right_x(x);
        y = dir.right_y(y);
      } else {
        x = dir.left_x(x);
        y = dir.left_y(y);        
      }
    }
  }
  
  public void show() {
    grid.show();
  }
  
  // ToDo: The routines below here need to understand the room rotation too.
  
  // Get a legal random room access location on the given side
  //  return -1 if there isn't one
  public int getAccess(Random rnd,Direction side) {
    List<Integer> tmp = access.get(side);
    if(!tmp.isEmpty()) {
      return tmp.get(rnd.nextInt(tmp.size()));
    }
    return -1;
  }
  
  public int sx() {
    return grid.getSize().x;
  }
  public int sy() {
    return grid.getSize().y;
  }
  
  public Square get(int x,int y) {
    return grid.get(x,y);
  }
}
