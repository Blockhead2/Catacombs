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
  private Direction special_dir;
  private PrePlanned room_map = null;

  private static List<PrePlanned> room_list = null;
  private static List<PrePlanned> down_list = null;
  private Config cnf=null;

  public static void setup_downs() {
    // Stairs down should be against a northery wall so end_dir works ok on the level below
    if(down_list == null) {
      down_list = new ArrayList<PrePlanned>();
      down_list.add(new PrePlanned("original",PrePlanned.Type.DOWN, new String[] {
        " D ",
        "DVD"
      }));
//      down_list.add(new PrePlanned("down7x9",PrePlanned.Type.DOWN, new String[] {
//        "    D    ",
//        "#XXDVDXX#",
//        "X...:...X",
//        "X.......X",
//        "X.......X",
//        "X.......X",
//        "#########"
//      }));
    }
  }      
  public static void setup_rooms() {
    if(room_list == null) {
      room_list = new ArrayList<PrePlanned>();
//      room_list.add(new PrePlanned("test5x7",PrePlanned.Type.ROOM, new String[] {
//        "#X#X#",
//        "X...X",
//        "X.o.X",
//        "#...X",
//        "X...X",
//        "X...#",
//        "##XX#" 
//      }));
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
      room_list.add(new PrePlanned("doortest9x9",PrePlanned.Type.ROOM, new String[] {
        "  #####  ",
        " #.#...# ",
        "#.......#",
        "##......#",
        "#......##",
        "#.......#",
        "#.......#",
        " #.#.#.# ",
        "  #####  "
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
      room_list.add(new PrePlanned("chanting9x9",PrePlanned.Type.ROOM, new String[] {
        "#########",
        "#t.....t#",
        "#.KKKKK.#",
        "#.K,,,K.#",
        "#.K,e,K.#",
        "#.K,,,K.#",
        "#.KK.KK.#",
        "#t.....t#",
        "#########"
      }));
      room_list.add(new PrePlanned("lavaCross9x9",PrePlanned.Type.ROOM, new String[] {
        "  X###X  ",
        " #LL.LL# ",
        "XLLL.LLLX",
        "#LLL.LLL#",
        "#.......#",
        "#LLL.LLL#",
        "XLLL.LLLX",
        " #LL.LL# ",
        "  X###X  "
      }));
      room_list.add(new PrePlanned("cells9x9",PrePlanned.Type.ROOM, new String[] {
        "  X###X  ",
        " X.I.I.X ",
        "X..I.I..X",
        "#III.III#",
        "#.......#",
        "#III.III#",
        "X..I.I..X",
        " X.I.I.X ",
        "  X###X  "
      }));
      room_list.add(new PrePlanned("web9x9",PrePlanned.Type.ROOM, new String[] {
        "  #####  ",
        " #Mw.wM# ",
        "#wwwwwww#",
        "#.wwwww.#",
        "#.wwwww.#",
        "#.wwwww.#",
        "#wwwwwww#",
        " #Mw.wM# ",
        "  #####  "
      }));
      room_list.add(new PrePlanned("water9x9",PrePlanned.Type.ROOM, new String[] {
        "  #####  ",
        " #MW.WM# ",
        "#WWWWWWW#",
        "#.WWWWW.#",
        "#.WWWWW.#",
        "#.WWWWW.#",
        "#WWWWWWW#",
        " #MW.WM# ",
        "  #####  "
      }));
      room_list.add(new PrePlanned("bakery9x9",PrePlanned.Type.ROOM, new String[] {
        "#########",
        "#===.===#",
        "#=.....=#",
        "#=.....=#",
        "#...f...#",
        "#=.....=#",
        "#=.....=#",
        "#===.===#",
        "#########"
      }));
      room_list.add(new PrePlanned("lavaJump9x9",PrePlanned.Type.ROOM, new String[] {
        "  X###X  ",
        " #LL.LL# ",
        "XLLL.LLLX",
        "#LLL.LLL#",
        "#...L...#",
        "#LLL.LLL#",
        "XLLL.LLLX",
        " #LL.LL# ",
        "  X###X  "
      }));
      room_list.add(new PrePlanned("range13x9",PrePlanned.Type.ROOM, new String[] {
        "#############",
        "#...........#",
        "#...........#",
        "#.IIIIIIIIII#",
        "XxIx.xIx.xIcX",
        "X1I2I1I1I1I.X",
        "X2I2I2I2I2I.X",
        "X2.2I2.2I2..X",
        "X>X>X>X>X>XXX"
      }));      
      room_list.add(new PrePlanned("Forge13x13",PrePlanned.Type.ROOM, new String[] {
        "#############",
        "#f#f#...#f#f#",
        "#...........#",
        "#...........#",
        "#....W.L....#",
        "#.....a.....#",
        "#...........#",
        "#.....a.....#",
        "#....L.W....#",
        "#...........#",
        "#...........#",
        "#f#f#...#f#f#",
        "#############"
      }));
      room_list.add(new PrePlanned("shroomFarm13x9",PrePlanned.Type.ROOM, new String[] {
        "#############",
        "#mmmmm.mmmmm#",
        "#mmmmmmmmmmm#",
        "#mmmmmmmmmmm#",
        "#.mmmmmmmmm.#",
        "#mmmmmmG#+#G#",
        "#mmmmmmG...a#",
        "#mmmmm.GfeTo#",
        "#############"
      }));
      room_list.add(new PrePlanned("arches13x13",PrePlanned.Type.ROOM, new String[] {
        "#############",
        "#..A.....A..#",
        "#..A.....A..#",
        "#AA#AA#AA#AA#",
        "#..A.....A..#",
        "#..A..M..A..#",
        "#AA#..c..#AA#",
        "#..A..M..A..#",
        "#..A.....A..#",
        "#AA#AA#AA#AA#",
        "#..A.....A..#",
        "#..A.....A..#",
        "#############"
      }));      
      room_list.add(new PrePlanned("maze13x13",PrePlanned.Type.ROOM, new String[] {
        "#############",
        "#M#.......#.#",
        "#.#.#.###.#.#",
        "#.#.#.#.#.#.#",
        "#.#.#.#.#.#.#",
        "#...#.#.#...#",
        "#####.#.#####",
        "#.....#.....#",
        "X.#########.#",
        "X.....#.....#",
        "X.###.#####.#",
        "X.cC#.......#",
        "XXXX#########"
      }));
      room_list.add(new PrePlanned("barracks20x9",PrePlanned.Type.ROOM, new String[] {
        "#############       ",
        "#Zz........t#XXXXXXX",
        "#...a..oo..Z#.KKKKKX",
        "#Zz.T......z#tK,,,KX",
        "#...f..oo...$..,e,KX",
        "#Zz.o......z#tK,,,KX",
        "#...o..oo..Z#.KKKKKX",
        "#Zz........t#XXXXXXX",
        "#############       "
      }));
      room_list.add(new PrePlanned("kitchen",PrePlanned.Type.ROOM, new String[] {
        "   #####   ",
        "  ##...##  ",
        "###.....###",
        "#c+.....+c#",
        "###..T..###",
        "#.........#",
        "##f..T..f##",
        " #f.....f# ",
        " #f.....f# ",
        " #..#L#..# ",
        "  ## # ##  "
      }));
      room_list.add(new PrePlanned("column13x15",PrePlanned.Type.ROOM, new String[] {
        " XX #####    ",
        "Xc$X.....XX  ",
        " X.........X ",
        "#.....M.....#",
        "#..#..#..#..#",
        "#...........#",
        "#...........#",
        "Xt.#..#..#.tX",
        "#...........#",
        "#...........#",
        "#..#..#..#..#",
        "#.....M.....#",
        " X.........X ",
        "  XX.....XX  ",
        "    #####    " 
      }));
      room_list.add(new PrePlanned("treasure13x15",PrePlanned.Type.ROOM, new String[] {
        "    XXXXX    ",
        "  XXCctcCXX  ",
        " X..##$##..X ",
        "X...........X",
        "X.M...M...M.X",
        "X...........X",
        "#IIII...IIII#",
        "#...........#",
        "#...........#",
        "#...........#",
        "#IIII...IIII#",
        "#...........#",
        " X.M.....M.X ",
        "  XX.....XX  ",
        "    #####    " 
      }));
      room_list.add(new PrePlanned("lavaSpiral13x15",PrePlanned.Type.ROOM, new String[] {
        "    #####    ",
        "  XX.....XX  ",
        " X.........X ",
        "#..LLLLLLL..#",
        "#........L..#",
        "#..LLLLL.L..#",
        "#..L...L.L..#",
        "#..L.#w#.L..#",
        "#..L.IcI.L..#",
        "#..L.#I#.L..#",
        "#..L.....L..#",
        "#..LLLLLLL..#",
        " X.........X ",
        "  XX.....XX  ",
        "    #####    " 
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
    setup_downs();
    sizeRandom(room_list);
    placeRandom();
  }

  private void sizeRandom(List<PrePlanned> list) {
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
      sizeRoomRandom(list);
    }
  }
  
  private void sizeRoomRandom(List<PrePlanned> list) {
    isRoom = true;
    if(cnf.SpecialChance()) {
      sizeRoomSpecial(list);
    } else {
      size_x = cnf.RoomSize()+2;
      size_y = cnf.RoomSize()+2;
    }
  }
  
  private void sizeRoomSpecial(List<PrePlanned> list) {
    isRoom = true;
    special = true;
    room_map = chooseMap(list);
    special_dir = Direction.any(cnf.rnd);
    size_x = room_map.sx(special_dir);
    size_y = room_map.sy(special_dir);
  }
  
  private void placeRandom() {
    if(grid.getSize().x-size_x+1<1 || grid.getSize().y-size_y+1<1) {
      origin_x = 0;
      origin_y = 0;
    } else {
      origin_x  = cnf.nextInt(grid.getSize().x-size_x+1);
      origin_y  = cnf.nextInt(grid.getSize().y-size_y+1);
    }
  }

  private PrePlanned chooseMap(List<PrePlanned> list) {
    if(!list.isEmpty())
      return list.get(cnf.nextInt(list.size()));
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
    sizeRoomRandom(room_list);
    do {
      if(orig_dir == Direction.ANY)
        dir = placeFrom(x,y);
      else
        dir = placeFrom(x,y,orig_dir);

      ok = dir != null && grid.fits(origin_x,origin_y,size_x,size_y);
      //System.out.println(" Fits:"+ok+" orig_dir:"+orig_dir+" dir:"+dir+" from:"+v+" attempts"+extension_attempts);
      if(!ok)
        sizeRoomRandom(room_list);
      cnt++;
    } while (cnt<5000 && !ok);

    if(!ok) {
      // Stuggled to find start room so check more methodically.
      System.out.println("[Catacombs] Mapping code struggled to find valid location for starting room");
    }

    if(ok) {
      if(special) {
        grid.renderSpecial(origin_x,origin_y,room_map,special_dir);
      } else {
        grid.renderEmpty(origin_x,origin_y,size_x,size_y);
      }
      grid.set(x,y,Square.UP);
      grid.set(dir.forwards_x(x),dir.forwards_y(y),Square.FIXEDFLOORUP);
      placeSign(dir,x,y);
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
    Direction dir=null;
    do {
      for(int s=0;s<10;s++) { // Have a few attempts to place each room
        dir = placeFrom(from);
        ok = (dir != null) && grid.fits(origin_x,origin_y,size_x,size_y) &&
             grid.get(wayin_x,wayin_y) == Square.WALL &&
             grid.isFloor(dir.backwards_x(wayin_x),dir.backwards_y(wayin_y));
        //System.out.println(" Fits:"+ok+" orig_dir:"+from.room_dir+" dir:"+dir+" attempts:"+from.extension_attempts);
        if(ok) {
          break;
        }
      }
      if(!ok)
        sizeRandom(room_list);
      from.extension_attempts++;
      cnt++;
    } while (cnt<500 && !ok); // Try different sizes and shapes

    if(ok) {
      if(special) {
        grid.renderSpecial(origin_x,origin_y,room_map,special_dir);
      } else {
        grid.renderEmpty(origin_x,origin_y,size_x,size_y);
      }
      build_door(dir);  
      gen = from.gen+1;
      dressRoom();
    }
    return ok;
  }
  
  public Boolean endRoom(Room from) {
    int cnt = 0;
    Boolean ok = false;
    Direction dir=null;
    do {
      sizeRoomSpecial(down_list);
      for(int s=0;s<30;s++) { // Have a few attempts to place each room
        dir = placeFrom(from);
        ok = (dir != null) && grid.fits(origin_x,origin_y,size_x,size_y) &&
             grid.get(wayin_x,wayin_y) == Square.WALL &&
             grid.isFloor(dir.backwards_x(wayin_x),dir.backwards_y(wayin_y));
        //System.out.println(" Fits:"+ok+" orig_dir:"+from.room_dir+" dir:"+dir+" attempts:"+from.extension_attempts);
        if(ok) {
          break;
        }
      }
      if(!ok)
        sizeRoomSpecial(down_list);
      from.extension_attempts++;
      cnt++;      
    } while (cnt<2000 && !ok); // Try different sizes and shapes

    if(ok) {
      extension_attempts=100000;
      if(special) {
        grid.renderSpecial(origin_x,origin_y,room_map,special_dir);
      } else {
        grid.renderEmpty(origin_x,origin_y,size_x,size_y);
      }      
      gen = from.gen+1;
      if(grid.get(wayin_x,wayin_y) == Square.DOWN) {
        grid.set(dir.backwards_x(wayin_x),dir.backwards_y(wayin_y),Square.FIXEDFLOORDOWN);
        placeSign(dir.turn180(),wayin_x,wayin_y);
        from.chestDoubleRandom();
        if(cnf.Chance(40))
          from.objectRandom(Square.CAKE);
      } else {
        build_door(dir);
        // Find steps down as new location of end
  //wayin_x =0;
  //wayin_y =0;
        chestDoubleRandom();
        if(cnf.Chance(40))
          objectRandom(Square.CAKE);        
      }
    }
    return ok;
  }
  
  public void build_door(Direction dir) {
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
  }  

  private void dressRoom() {
    if(cnf.EnchantChance())
      enchantRandom();
 
    if(cnf.TrapChance())
      trapRandom();
    
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
    
    if(cnf.AnvilChance())
      objectRandom(Square.ANVIL);
    
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
  
  private Boolean trapRandom() {
    wallLoc loc;
    if(true) {
      loc = wallLocation(this);
      if(loc != null) {
        int dx = loc.dir.turn180().dx(1);
        int dy = loc.dir.turn180().dy(1);
        if(grid.get(loc.x,loc.y) == Square.WALL &&
           grid.get(loc.x+dx,loc.y+dy) == Square.FLOOR &&  
           grid.get(loc.x+dx*2,loc.y+dy*2) == Square.FLOOR &&    
           grid.get(loc.x+dx*3,loc.y+dy*3) == Square.FLOOR ) {
          grid.set(loc.x,loc.y,Square.ARROW);
          grid.set(loc.x+dx,loc.y+dy,Square.RED2);
          grid.set(loc.x+dx*2,loc.y+dy*2,Square.RED1);
          grid.set(loc.x+dx*3,loc.y+dy*3,Square.PRESSURE);
          return true;
        }        
      }
    }    
    return false;
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
  
  private Boolean enchantRandom() {
    int inset = 3;

    int px = size_x-inset*2;
    int py = size_y-inset*2;

    if(px >0 && py >0 && (size_x>3 && size_y>3)) {
      int px1,py1;
      for(int at=0;at<100;at++) {
        px1 = origin_x+cnf.nextInt(px)+inset;
        py1 = origin_y+cnf.nextInt(py)+inset;
        if(grid.get(px1,py1) == Square.FLOOR) {
          grid.set(px1,py1,Square.ENCHANT);
          for(int i=-2;i<2;i++) {
            if(cnf.Chance(50) && grid.get(px1-2,py1+i) == Square.FLOOR)
              grid.set(px1-2,py1+i,Square.BOOKCASE);
            if(cnf.Chance(50) && grid.get(px1+2,py1-i) == Square.FLOOR)
              grid.set(px1+2,py1-i,Square.BOOKCASE);          
            if(cnf.Chance(50) && grid.get(px1-i,py1-2) == Square.FLOOR)
              grid.set(px1-i,py1-2,Square.BOOKCASE);  
            if(cnf.Chance(50) && grid.get(px1+i,py1+2) == Square.FLOOR)
              grid.set(px1+i,py1+2,Square.BOOKCASE);  
          }
          for(int i=-1;i<1;i++) {
            if(grid.get(px1-1,py1+i) == Square.FLOOR)
              grid.set(px1-1,py1+i,Square.FIXEDFLOOR);
            if(grid.get(px1+1,py1-i) == Square.FLOOR)
              grid.set(px1+1,py1-i,Square.FIXEDFLOOR);          
            if(grid.get(px1-i,py1-1) == Square.FLOOR)
              grid.set(px1-i,py1-1,Square.FIXEDFLOOR);  
            if(grid.get(px1+i,py1+1) == Square.FLOOR)
              grid.set(px1+i,py1+1,Square.FIXEDFLOOR);  
          }
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
          grid.set(px2,py2,(cnf.MedHalfEmpty())?Square.EMPTYCHEST:Square.CHEST);
          return true;
        }
      }
    }
    return false;
  }
  
  private void placeSign(Direction dir,int x, int y) {
    x = dir.forwards_x(x);
    y = dir.forwards_y(y);
    if(grid.get(dir.right_x(x),dir.right_y(y))==Square.FLOOR) {
      grid.set(dir.right_x(x),dir.right_y(y),Square.SIGNPOST);
    } else if(grid.get(dir.left_x(x),dir.left_y(y))==Square.FLOOR) {
      grid.set(dir.left_x(x),dir.left_y(y),Square.SIGNPOST);      
    }
  }
  
  private class wallLoc {
    public int x;
    public int y;
    public Direction dir;
    public wallLoc(int x,int y,Direction dir) {
      this.x=x;
      this.y=y;
      this.dir=dir;
    }
  }
  
  private wallLoc wallLocation(Room from) {
    Direction dir = Direction.any(cnf.rnd);

    if(dir.horizontal()) {
      int offset = (from.special)?from.room_map.getAccess(cnf.rnd,dir,from.special_dir):cnf.nextInt(from.size_y-2)+1;
      if(offset<0) return null;
      wayin_x = (dir==Direction.EAST)?from.origin_x+from.size_x-1:from.origin_x;
      wayin_y = from.origin_y+offset;
    } else {
      int offset = (from.special)?from.room_map.getAccess(cnf.rnd,dir,from.special_dir):cnf.nextInt(from.size_x-2)+1;
      if(offset<0) return null;
      wayin_x = from.origin_x+offset;
      wayin_y = (dir==Direction.NORTH)?from.origin_y+from.size_y-1:from.origin_y;
    }
    return new wallLoc(wayin_x,wayin_y,dir);
  }
  
  private Direction placeFrom(Room from) {
    wallLoc w = wallLocation(from);
    if(w==null)
      return null;
    return this.placeFrom(w.x,w.y,w.dir);
  }

  private Direction placeFrom(int x, int y) {
    return this.placeFrom(x,y,Direction.ANY);
  }

  private Direction placeFrom(int x,int y,Direction dir) {
    wayin_x = x;
    wayin_y = y;
    if(dir==Direction.ANY)
      dir = Direction.any(cnf.rnd);

    //if(size_x<3 || size_y<3) {
    //  System.err.println("[Catacombs] Room too small "+size_x+" x "+size_y);
    //}
    //  sizeRandom();

    room_dir = dir;
    if(dir.horizontal()) {
      int offset = (special)?room_map.getAccess(cnf.rnd,dir.turn180(),special_dir):cnf.nextInt(size_y-2)+1;
      if(offset<0) return null;
      origin_x = (dir == Direction.EAST)?x:x-size_x+1;
      origin_y = y-offset;
    } else {
      int offset = (special)?room_map.getAccess(cnf.rnd,dir.turn180(),special_dir):cnf.nextInt(size_x-2)+1;
      if(offset<0) return null;
      origin_y = (dir == Direction.NORTH)?y:y-size_y+1;
      origin_x = x-offset;
    }
    return dir;
  }

}
