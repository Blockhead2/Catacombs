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
import java.util.ArrayList;

// For efficency avoid using Vector2D internal to this class
public class Room {
  private int size_x,size_y;
  private int origin_x,origin_y;
  private int wayin_x,wayin_y;
  private int gen = 0;
  private int extension_attempts = 0;
  private Direction room_dir;
  private Grid grid;
  private Boolean isRoom;
  
  private Boolean special = false;
  private PrePlanned room_map = null;

  //private final static List<PrePlanned> room_list = new ArrayList<PrePlanned>();
  private static List<PrePlanned> room_list = null;
  private Config cnf=null;
  
  public static void setup_rooms() {
    if(room_list == null) {
      room_list = new ArrayList<PrePlanned>();
      room_list.add(new PrePlanned("irreg7x8",PrePlanned.Type.ROOM, new String[] {
        " ###    ",
        " #.#    ",
        "##..####",
        "#......#",
        "#...####",
        "###.#   ",
        "  ###   " 
      }));
      room_list.add(new PrePlanned("circle7x7",PrePlanned.Type.ROOM, new String[] {
        "  ###  ",
        " #...# ",
        "#.....#",
        "#.....#",
        "#.....#",
        " #...# ",
        "  ###  "
      }));
      room_list.add(new PrePlanned("circle9x9",PrePlanned.Type.ROOM, new String[] {
        "  #####  ",
        " #c....# ",
        "#.......#",
        "#...L...#",
        "#..LLL..#",
        "#...L...#",
        "#.......#",
        " #....c# ",
        "  #####  "
      }));            
      room_list.add(new PrePlanned("column13x13",PrePlanned.Type.ROOM, new String[] {
        " ##          ",
        "#c$########  ",
        " #.........# ",
        "#.....M.....#",
        "#..#..#..#..#",
        "#...........#",
        "#...........#",
        "Xt.#..#..#.tX",
        "#...........#",
        "#...........#",
        "#..#..#..#..#",
        "#.....M.....#",
        " #.........# ",
        "  #########  "
      }));
    }
  }

  @Override
  public String toString() {
    return "ROOM:("+origin_x+","+origin_y+") size("+size_x+","+size_y+") gen="+gen+" att="+extension_attempts;
  }

  public Room(Config cnf, Grid grid) {
    this.grid = grid;
    this.cnf = cnf;
    setup_rooms();
    sizeRandom();
    placeRandom();
  }

  private void sizeRandom() {
    special = false;
    if(cnf.CorridorChance()) { //Corridor
      isRoom = false;
      int width = cnf.CorridorType();
      if(cnf.Chance(50)) {
        size_x = cnf.CorridorSize()+2;
        size_y = width+2;
      } else {
        size_x = width+2;
        size_y = cnf.CorridorSize()+2;
      }
    } else {               // Room
      isRoom = true;
      if(cnf.SpecialChance()) {
        special = true;
        room_map = chooseMap();
        // ToDo: select a random rotation for this instance of the room
        size_x = room_map.sx();
        size_y = room_map.sy();
      } else {
        size_x = cnf.RoomSize()+2;
        size_y = cnf.RoomSize()+2;
      }
    }
  }
  
  private void sizeRoomRandom() {
    isRoom = true;
    size_x = cnf.RoomSize()+2;
    size_y = cnf.RoomSize()+2;
  }

  private void placeRandom() {
    if(grid.size.x-size_x+1<1 || grid.size.y-size_y+1<1) {
      origin_x = 0;
      origin_y = 0;
    } else {
      origin_x  = cnf.nextInt(grid.size.x-size_x+1);
      origin_y  = cnf.nextInt(grid.size.y-size_y+1);
    }
  }

  private PrePlanned chooseMap() {
    // ToDo: Should rotate rooms here too to a random direction

    if(!room_list.isEmpty())
      return room_list.get(cnf.nextInt(room_list.size()));
    return null;
  }
  
  
  public Boolean isRoom() {
    return isRoom && floorArea()>=8;
  }

  public int floorArea() {
    return (size_x-2)*(size_y-2);
  }

  public int gen() {
    return gen;
  }

  public Vector2D wayin() {
    return new Vector2D(wayin_x,wayin_y);
  }

  public Direction room_dir() {
    return room_dir;
  }

  public Boolean bored() {
    return extension_attempts>=400;
  }

  public void clearBored() {
    extension_attempts=0;
  }

  public Boolean startRoom(int x,int y){
    return startRoom(x,y,Direction.ANY);
  }

  public Boolean startRoom(int x, int y,Direction orig_dir){
    int cnt = 0;
    Boolean ok=false;
    Direction dir;
    sizeRoomRandom();
    do {
      if(orig_dir == Direction.ANY)
        dir = placeFrom(x,y);
      else
        dir = placeFrom(x,y,orig_dir);

      ok = dir != null && grid.fits(origin_x,origin_y,size_x,size_y);
      //System.out.println(" Fits:"+ok+" orig_dir:"+orig_dir+" dir:"+dir+" from:"+v+" attempts"+extension_attempts);
      if(!ok)
        sizeRoomRandom();
      cnt++;
    } while (cnt<5000 && !ok);

    if(!ok) {
      // Stuggled to find start room so check more methodically.
      System.out.println("[Catacombs] Struggled to find start room (check more methodically)");
    }

    if(ok) {
      grid.renderEmpty(origin_x,origin_y,size_x,size_y);
      grid.set(x,y,Square.UP);
      grid.set(dir.forwards_x(x),dir.forwards_y(y),Square.FIXEDFLOORUP);
      grid.upWall(dir.backwards_x(x),dir.backwards_y(y));
      grid.upWall(dir.left_x(x),dir.left_y(y));
      grid.upWall(dir.right_x(x),dir.right_y(y));
      gen = 1;
      dressRoom();
    }
    return ok;
  }

  public Boolean nextRoom(Room from) {
    int cnt = 0;
    Boolean ok = false;
    Direction dir;
    do {
      dir = placeFrom(from);
      ok = (dir != null) && grid.fits(origin_x,origin_y,size_x,size_y) &&
           grid.get(wayin_x,wayin_y) == Square.WALL &&
           grid.isFloor(dir.backwards_x(wayin_x),dir.backwards_y(wayin_y));
      //System.out.println(" Fits:"+ok+" orig_dir:"+from.room_dir+" dir:"+dir+" attempts:"+from.extension_attempts);
      if(!ok)
        sizeRandom();
      from.extension_attempts++;
      cnt++;
    } while (cnt<500 && !ok);

    if(ok) {
      if(special) {
        grid.renderSpecial(origin_x,origin_y,room_map);
      } else {
        grid.renderEmpty(origin_x,origin_y,size_x,size_y);
      }
      Square door = cnf.DoorType();
      if(door != Square.HIDDEN && cnf.DoubleDoorChance()) {
        int left_x  = dir.left_x(wayin_x);
        int left_y  = dir.left_y(wayin_y);
        int right_x = dir.right_x(wayin_x);
        int right_y = dir.right_y(wayin_y);
        if(cnf.Chance(50)) {
          if(grid.okForDoor(right_x,right_y, dir)) {
            grid.fixDoor(right_x,right_y, dir, door);
          } else if (grid.okForDoor(left_x,left_y, dir)) {
            grid.fixDoor(left_x,left_y, dir, door);
          }
        } else {
          if(grid.okForDoor(left_x,left_y, dir)) {
            grid.fixDoor(left_x,left_y, dir, door);
          } else if (grid.okForDoor(right_x,right_y, dir)) {
            grid.fixDoor(right_x,right_y, dir, door);
          }
        }
      }
      grid.fixDoor(wayin_x,wayin_y, dir, door);
      
      gen = from.gen+1;
      dressRoom();
    }
    return ok;
  }

  public Boolean endRoom(Room from) {
    int cnt = 0;
    Boolean ok = false;
    Direction dir;
    do {
      size_x = size_y = 3;
      dir = placeFrom(from);
      size_x = (dir.horizontal())?2:3;
      size_y = (dir.vertical())?2:3;
      ok = (dir != null) && grid.fits(origin_x,origin_y,size_x,size_y) &&
           grid.get(wayin_x,wayin_y) == Square.WALL &&
           grid.get(dir.backwards_x(wayin_x),dir.backwards_y(wayin_y)).isFloor();
      from.extension_attempts++;
      cnt++;
    } while (cnt<2000 && !ok);

    if(ok) {
      extension_attempts=100000;
      grid.set(wayin_x,wayin_y,Square.DOWN);
      grid.set(dir.backwards_x(wayin_x),dir.backwards_y(wayin_y),Square.FIXEDFLOORDOWN);
      grid.downWall(dir.forwards_x(wayin_x),dir.forwards_y(wayin_y));
      grid.downWall(dir.left_x(wayin_x),dir.left_y(wayin_y));
      grid.downWall(dir.right_x(wayin_x),dir.right_y(wayin_y));
      gen = from.gen+1;
      from.chestDoubleRandom();
      if(cnf.Chance(40))
        from.objectRandom(Square.CAKE);
    }
    return ok;
  }

  private void dressRoom() {
    if(cnf.ChestChance())
      chestRandom();

    if(cnf.SpawnerChance())
      objectRandom(Square.SPAWNER);

    if(cnf.ShroomChance())
      for(int i=0;i<=cnf.nextInt(3);i++)
        objectRandom(Square.SHROOM);

    if(cnf.BenchChance())
      objectRandom(Square.WORKBENCH);

    if(cnf.OvenChance())
      objectRandom(Square.FURNACE);

    if(!special && cnf.PoolChance())
      poolRandom();

    if(!special && cnf.SandChance())
      allFloor(Square.SOULSAND,2);
  }

  private void poolRandom() {
    Square pool = cnf.PoolType();
    int inset = 3;

    int px = size_x-inset*2;
    int py = size_y-inset*2;

    if(px >0 && py >0 && (size_x>3 && size_y>3)) {
      int px1,px2,py1,py2;
      if(cnf.FullPoolChance()) { // Full pool
        px1 = origin_x+inset;
        py1 = origin_y+inset;
        px2 = origin_x+px-1+inset;
        py2 = origin_y+py-1+inset;
      } else {
        px1 = origin_x+cnf.nextInt(px)+inset;
        py1 = origin_y+cnf.nextInt(py)+inset;
        px2 = origin_x+cnf.nextInt(px)+inset;
        py2 = origin_y+cnf.nextInt(py)+inset;
      }
      for(px=Math.min(px1,px2);px<=Math.max(px1,px2);px++) {
        for(py=Math.min(py1,py2);py<=Math.max(py1,py2);py++) {
          if(grid.get(px,py) == Square.FLOOR) {
            grid.set(px,py,pool);
          }
        }
      }
    }
  }

  private Boolean objectRandom(Square blk) {
    int inset = 1;

    int px = size_x-inset*2;
    int py = size_y-inset*2;

    if(px >0 && py >0 && (size_x>3 && size_y>3)) {
      int px1,py1;
      for(int at=0;at<100;at++) {
        px1 = origin_x+cnf.nextInt(px)+inset;
        py1 = origin_y+cnf.nextInt(py)+inset;
        if(grid.get(px1,py1) == Square.FLOOR) {
          grid.set(px1,py1,blk);
          return true;
        }
      }
    }
    return false;
  }
  private void allFloor(Square blk,int inset) {
    int px = size_x-inset*2;
    int py = size_y-inset*2;

    for(int x=0;x<px;x++) {
      for(int y=0;y<py;y++) {
        int px1 = origin_x+x+inset;
        int py1 = origin_y+y+inset;
        if(grid.get(px1,py1) == Square.FLOOR) {
          grid.set(px1,py1,blk);
        }
      }
    }
  }

  private Boolean chestRandom() {
    int inset = 1;

    int px = size_x-inset*2;
    int py = size_y-inset*2;

    if(px >0 && py >0 && (size_x>3 && size_y>3)) {
      int px1,py1;
      for(int at=0;at<100;at++) {
        px1 = origin_x+cnf.nextInt(px)+inset;
        py1 = origin_y+cnf.nextInt(py)+inset;
        if(grid.okForChest(px1,py1)) {
          grid.set(px1,py1,Square.CHEST);
          return true;
        }
      }
    }
    return false;
  }

  private Boolean chestDoubleRandom() {
    int inset = 1;

    int px = size_x-inset*2;
    int py = size_y-inset*2;

    if(px >0 && py >0 && (size_x>3 && size_y>3)) {
      int px1,py1;
      for(int at=0;at<100;at++) {
        px1 = origin_x+cnf.nextInt(px)+inset;
        py1 = origin_y+cnf.nextInt(py)+inset;
        int px2 = px1;
        int py2 = py1;
        switch (cnf.nextInt(4)) {
          case 0: px2+=1; break;
          case 1: px2-=1; break;
          case 2: py2+=1; break;
          case 3: py2-=1; break;
        }
        if(grid.okForChest(px1, py1) && grid.okForChest(px2, py2)) {
          grid.set(px1,py1,Square.MIDCHEST);
          grid.set(px2,py2,Square.CHEST);
          return true;
        }
      }
    }
    return false;
  }

  private Direction placeFrom(Room from) {
    Direction dir = Direction.any(cnf.rnd);

    if(dir.horizontal()) {
      int offset = (from.special)?from.room_map.getAccess(cnf.rnd,dir):cnf.nextInt(from.size_y-2)+1;
      if(offset<0) return null;
      wayin_x = (dir==Direction.EAST)?from.origin_x+from.size_x-1:from.origin_x;
      wayin_y = from.origin_y+offset;
    } else {
      int offset = (from.special)?from.room_map.getAccess(cnf.rnd,dir):cnf.nextInt(from.size_x-2)+1;
      if(offset<0) return null;
      wayin_x = from.origin_x+offset;
      wayin_y = (dir==Direction.NORTH)?from.origin_y+from.size_y-1:from.origin_y;
    }
    return this.placeFrom(wayin_x,wayin_y,dir);
  }

  private Direction placeFrom(int x, int y) {
    return this.placeFrom(x,y,Direction.ANY);
  }

  private Direction placeFrom(int x,int y,Direction dir) {
    wayin_x = x;
    wayin_y = y;
    if(dir==Direction.ANY)
      dir = Direction.any(cnf.rnd);

    if(size_x<3 || size_y<3)
      sizeRandom();

    room_dir = dir;
    if(dir.horizontal()) {
      int offset = (special)?room_map.getAccess(cnf.rnd,dir.turn180()):cnf.nextInt(size_y-2)+1;
      if(offset<0) return null;
      origin_x = (dir == Direction.EAST)?x:x-size_x+1;
      // Need to check special room too 
      origin_y = y-offset;
    } else {
      int offset = (special)?room_map.getAccess(cnf.rnd,dir.turn180()):cnf.nextInt(size_x-2)+1;
      if(offset<0) return null;
      origin_y = (dir == Direction.NORTH)?y:y-size_y+1;
      origin_x = x-offset;
    }
    //System.out.println(" Dir:"+dir+" from:"+v+" origin"+origin+" size:"+size);

    return dir;
  }

}
