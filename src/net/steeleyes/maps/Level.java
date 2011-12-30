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

import java.util.ArrayList;
import java.util.List;

public class Level {
  private Grid grid;
  private final Vector2D start = new Vector2D();
  private final Vector2D end   = new Vector2D();
  private Direction start_dir=null;
  private Direction end_dir=null;
  private int end_gen=0;
  private Config cnf=null;
  private ArrayList<Room> rooms = new ArrayList<Room>();
  private int num_rooms = 0;
  
  public Level(Config cnf) {
    this.cnf = cnf;
    grid = new Grid(1,1);    
    num_rooms = 1;
    this.start.x = 0;
    this.start.y = 0;
    end.set(start);
    start_dir = Direction.ANY;
  }
  
  public Level(Config cnf,int sx,int sy,Direction dir) {
    this(cnf,sx,sy,cnf.nextInt(sx-2)+1,cnf.nextInt(sy-2)+1,dir);
  }
  public Level(Config cnf,int sx,int sy) {
    this(cnf,sx,sy,cnf.nextInt(sx-2)+1,cnf.nextInt(sy-2)+1);
  }
  public Level(Config cnf, int sx,int sy,int start_x, int start_y) {
    this(cnf,sx,sy,cnf.nextInt(sx-2)+1,cnf.nextInt(sy-2)+1,Direction.ANY);
  }
  public Level(Config cnf,PrePlanned map,Direction dir) {
    this.cnf = cnf;
    Grid tmp = new Grid(map,Direction.NORTH);
    grid = new Grid(tmp,dir);
    Vector2D stair = grid.findFirst(Square.DOWN);
    if(stair ==  null) {
      System.err.println("[Catacombs] The map for the level (shown below) doesn't contain a stair down");
      map.show();
      stair = new Vector2D(0,0);
    }
    num_rooms = 1;
    this.start.x = stair.x;
    this.start.y = stair.y;
    end.set(start);
    start_dir = dir;
  }
  
  public Level(Config cnf, int sx,int sy,int start_x, int start_y,Direction dir) {
    this.cnf = cnf;
    grid = new Grid(sx,sy);
    start.set(start_x,start_y);
    Room r = new Room(cnf,grid);
    if(r.startRoom(start_x,start_y,dir)) {
      //System.out.println("Utilized "+grid.utilized()+" used "+grid.used);

      start_dir = r.room_dir();
      rooms.add(r);
      Room n = new Room(cnf,grid);
      Room from = r;
      int max_gen=0;
      // Place rooms until we have to back track too much
      for(int t=0;t<1000 && from != null;t++) {
        if(from.bored()) {
          from = getRoomNearEnd();
        }
        if(from != null && n.nextRoom(from)) {
          rooms.add(n);
          if(n.gen()>max_gen)
            max_gen = n.gen();

          // area 10x10 40% 2.5%bad, 45% 3% bad, 50% 3.5%bad, 70% 10%bad
          // area 20x20 70% clean
          // area 25x25 80% clean 85%
          // area 30x30 70% clean 80%clean 90%clean
//          if(grid.utilized()>85.0 || ((float)n.gen() < (float)max_gen*0.75 && (max_gen-n.gen()>4))) {
          if(grid.utilized()>85.0) {
          //if(grid.utilized()>55.0) {
            from = null;
          } else {
            from = n;
          }
          n = new Room(cnf,grid);
        }
      }

      // Find a location for the end room
      clearRoomAttempts();
      from = getRoomGen();
      for(int t=0;t<1000 && from != null;t++) {
        if(from.bored()) {
          from = getRoomGen();
        }
        if(from != null && n.endRoom(from)) {
          end.set(n.wayin());
          end_dir = n.room_dir();
          end_gen = n.gen();
          from=null;
          n = new Room(cnf,grid);
        }
      }
      
      if(true && from == null) {
        // Fill the rest of the map
        from = getRoomNearEnd();
        for(int t=0;t<1000 && from != null;t++) {
          if(from.bored()) {
            from = getRoomNearEnd();
          }
          if(from != null && n.nextRoom(from)) {
            rooms.add(n);
            from = n;
            n = new Room(cnf,grid);
          }
        }        
      }
      if(end_dir == null) {
        System.out.println("[Catacombs] Stopping level no endroom");
      }
    } else {
      System.out.println("[Catacombs] Stopping level no startroom");
    }
    num_rooms = rooms.size();
    rooms = null;  // Tidy up to save space
  }

  public String getMapString() {
    return grid.getMapString();
  }
  
  public List<String> getMap() {
    return grid.getMap();
  }
  
  public Boolean isOk() {
    return start_dir != null && end_dir != null && num_rooms>0;
  }

  public Direction end_dir() {
    return end_dir;
  }
  public void end_dir(Direction dir) {
    end_dir = dir;
  }

  public Vector2D start() {
    return start;
  }
  public Vector2D end() {
    return end;
  }
  public Direction start_dir() {
    return start_dir;
  }
  public void start_dir(Direction dir) {
    start_dir = dir;
  }
  public int num_rooms() {
    return num_rooms;
  }
  public Grid grid() {
    return grid;
  }
  public void stealDirection(Level from) {
    if(end_dir == null || end_dir == Direction.ANY) {
      end_dir = from.start_dir();
    }
  }

  public void show() {
    grid.show();
    System.out.println(isOk()+" Start"+start+":"+start_dir+
      " End"+end+":"+end_dir+
      " Rooms="+num_rooms+" Grid:"+grid);
  }

  private void clearRoomAttempts () {
    if(rooms != null) {
      for(Room x : rooms) {
        x.clearBored();
      }
    }
  }

  private Room getRoomNearEnd() {
    Room r = null;
    if(rooms != null) {
      for(Room x : rooms) {
        if(!x.bored()) {
          r=x;
        }
      }
    }
    return r;
  }
  
  private Room getRoomGen() {
    Room r = null;
    int max = 0;
    if(rooms != null) {
      for(Room x : rooms) {
        if(!x.bored()) {
          if(x.gen() >= max && x.isRoom()) {
            r = x;
            max = x.gen();
          }
        }
      }
    }
    return r;
  }

}
