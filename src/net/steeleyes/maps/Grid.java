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

public class Grid {
  public final Vector2D size = new Vector2D();
  private Square[][] area;
  public int used=0;
  
  public Grid(int sx,int sy) {
    size.set(sx, sy);
    area = new Square[size.x][size.y];
    for(int x=0;x<size.x;x++) {
      for(int y=0;y<size.y;y++) {
        area[x][y] = Square.UNDEF;
      }
    }
  }

  public Grid(String[] strings) {
    this(strings[0].length(),strings.length);
    for(int y=0;y<size.y;y++) {
      for(int x=0;x<size.x;x++) {
        char c = strings[size.y-y-1].charAt(x);
        Square s = Square.get(c);
        area[x][y] = s;
      }
    }
  }

  public Grid(Grid g,Direction dir) {  // Clone and rotate
    this((dir.vertical())?g.size.x:g.size.y,(dir.vertical())?g.size.y:g.size.x);

    for(int x=0;x<size.x;x++) {
      for(int y=0;y<size.y;y++) {
        switch (dir) {
          case SOUTH: area[x][y] = g.get(x,y);             break;
          case EAST:  area[x][y] = g.get(y,size.x-1-x);    break;
          case NORTH: area[x][y] = g.get(size.x-1-x,size.y-1-y); break;
          case WEST:  area[x][y] = g.get(size.y-1-y,x);    break;
        }
      }
    }
  }
  public Square get(int x,int y) {
    if(x<0 || y<0 || x>= size.x || y>= size.y) return Square.FIXEDWALL;
    return area[x][y];
  }

  public void set(int x,int y, Square b) {
    if(x>=0 && y>=0 || x< size.x || y< size.y)
      area[x][y] = b;
  }

  public Vector2D findFirst(Square s) {
    for(int x=0;x<size.x;x++) {
      for(int y=0;y<size.y;y++) {
        if(area[x][y] == s) {
          return new Vector2D(x,y);
        }
      }
    }
    return null;
  }
  
  public String getMap() {
    String str = "";
    for(int x=0;x<size.x;x++) {
      for(int y=0;y<size.y;y++) {
        str += area[x][y].symb();
      }
    }
    return str;
  }
  
  public float utilized() {
    return ((float)used*100)/((float)size.x*size.y);
  }

  public Boolean okForChest(int x,int y) {
    if(area[x][y] != Square.FLOOR)  //Must be plain floor (not fixed foor etc)
      return false;
    if(this.isChest(x+1,y) || this.isChest(x-1,y) || this.isChest(x,y+1) || this.isChest(x,y-1))
      return false;
    return true;
  }

  public Boolean okForDoor(int x,int y,Direction dir) {
    if(area[x][y] == Square.WALL &&
        get(dir.left_x(x),dir.left_y(y)) == Square.WALL &&
        get(dir.right_x(x),dir.right_y(y)) == Square.WALL &&
        get(dir.backwards_x(x),dir.backwards_y(y)) == Square.FLOOR &&
        get(dir.forwards_x(x),dir.forwards_y(y)) == Square.FLOOR)
      return true;
    return false;
  }

  public void fixDoor(int x,int y,Direction dir,Square door) {
    set(x,y,door);
    fixWall(dir.left_x(x),dir.left_y(y));
    fixWall(dir.right_x(x),dir.right_y(y));
    fixFloor(dir.backwards_x(x),dir.backwards_y(y));
    fixFloor(dir.forwards_x(x),dir.forwards_y(y));
  }

  public void fixBigChest(int x,int y) {
    area[x][y] = Square.BIGCHEST;
  }

  public void fixWall(int x,int y) {
    if(area[x][y] == Square.WALL || area[x][y] == Square.UNDEF)
      area[x][y] = Square.FIXEDWALL;
  }
  public void fixFloor(int x,int y) {
    if(area[x][y] == Square.FLOOR || area[x][y] == Square.UNDEF)
      area[x][y] = Square.FIXEDFLOOR;
  }

  public void downWall(int x,int y) {
    if(area[x][y] == Square.UPWALL) {
      area[x][y] = Square.BOTHWALL;
    } else if (area[x][y] == Square.WALL ||
               area[x][y] == Square.FIXEDWALL) {
      area[x][y] = Square.DOWNWALL;
    } else if (area[x][y] == Square.UNDEF) {
      area[x][y] = Square.DOWNWALL;
      used++;
    }
  }

  public void upWall(int x,int y) {
    if(area[x][y] == Square.DOWNWALL) {
      area[x][y] = Square.BOTHWALL;
    } else if (area[x][y] == Square.WALL ||
               area[x][y] == Square.FIXEDWALL) {
      area[x][y] = Square.UPWALL;
    } else if (area[x][y] == Square.UNDEF) {
      area[x][y] = Square.UPWALL;
      used++;
    }
  }

  public Direction getBackWallDir(int x,int y) {
    if(isWall(x+1,y) && !isWall(x-1,y))  //EAST
      return Direction.EAST;
    if(isWall(x-1,y) && !isWall(x+1,y))  //WEST
      return Direction.WEST;
    if(isWall(x,y+1) && !isWall(x,y-1))  //SOUTH
      return Direction.NORTH;
    if(isWall(x,y-1) && !isWall(x,y+1))  //NORTH
      return Direction.SOUTH;
    return Direction.ANY;
  }

  public Direction getFloorDir(int x, int y) {
    if(isFloor(x+1,y))
      return Direction.EAST;
    if(isFloor(x-1,y))
      return Direction.WEST;
    if(isFloor(x,y+1))
      return Direction.NORTH;
    return Direction.SOUTH;
  }

  public byte getDoorCode(int x, int y) {
    if(get(x+1,y).isWall() && get(x-1,y).isWall())
      return 3;
    if(get(x,y+1).isWall() && get(x,y-1).isWall())
      return 0;
    if(get(x+1,y).isWall()) return 6;
    if(get(x-1,y).isWall()) return 3;
    if(get(x,y+1).isWall()) return 0;
    if(get(x,y-1).isWall()) return 7;
    return 0;
  }
  public Direction getBedDir(int x, int y) {
    if(area[x][y]==Square.BED_F) {   
      if(get(x+1,y) == Square.BED_H) return Direction.EAST;
      if(get(x-1,y) == Square.BED_H) return Direction.WEST;
      if(get(x,y+1) == Square.BED_H) return Direction.NORTH;
      return Direction.SOUTH;
    }
    if(get(x+1,y) == Square.BED_F) return Direction.WEST;
    if(get(x-1,y) == Square.BED_F) return Direction.EAST;
    if(get(x,y+1) == Square.BED_F) return Direction.SOUTH;
    return Direction.NORTH;
  } 
  /*
  public byte getBedCode(int x, int y) {
    if(area[x][y]==Square.BED_F) {   
      if(get(x+1,y) == Square.BED_H) return 2;
      if(get(x-1,y) == Square.BED_H) return 0;
      if(get(x,y+1) == Square.BED_H) return 1;
      return 3;
    }
    if(get(x+1,y) == Square.BED_F) return 0;
    if(get(x-1,y) == Square.BED_F) return 2;
    if(get(x,y+1) == Square.BED_F) return 3;
    return 1;
  }
  */
  public Boolean isChest(int x,int y) {
    if(x<0 || y<0 || x>= size.x || y>= size.y) return false;
    return area[x][y].isChest();
  }

  public Boolean isWall(int x,int y) {
    if(x<0 || y<0 || x>= size.x || y>= size.y) return false;
    return area[x][y].isWall();
  }

  public Boolean isFloor(int x,int y) {
    if(x<0 || y<0 || x>= size.x || y>= size.y) return false;
    return area[x][y].isFloor();
  }

  public Boolean isBoundaryUndef(int x,int y) {
    if(x<0 || y<0 || x>= size.x || y>= size.y) return false;
    return area[x][y].isBoundaryUndef();
  }

  public Boolean isUndef(int x,int y) {
    if(x<0 || y<0 || x>= size.x || y>= size.y) return false;
    return area[x][y].isUndef();
  }

  public Boolean fits(int origin_x,int origin_y,int size_x, int size_y) {
    if(origin_x+size_x-1 >= size.x ||
       origin_x < 0 || origin_y+size_y-1>=size.y || origin_y < 0)
      return false;

    for(int x=origin_x;x<origin_x+size_x;x++) {
      for(int y=origin_y;y<origin_y+size_y;y++) {
        if(x==origin_x || y == origin_y ||
           x == origin_x+size_x-1 || y == origin_y+size_y-1) {
          if(!area[x][y].isBoundaryUndef())
            return false;
        } else {
          if(area[x][y]!=Square.UNDEF)
            return false;
        }
      }
    }
    return true;
  }

  public void renderEmpty(int origin_x,int origin_y,int size_x, int size_y) {
    if(origin_x+size_x-1 >= size.x || origin_x < 0 ||
       origin_y+size_y-1>=size.y || origin_y < 0)
      return;

    for(int x=origin_x;x<origin_x+size_x;x++) {
      for(int y=origin_y;y<origin_y+size_y;y++) {
        if(x==origin_x || y == origin_y ||
           x == origin_x+size_x-1 || y == origin_y+size_y-1) {
          if(isUndef(x,y)) {
            set(x,y,Square.WALL);
            used++;
          }
        } else {
          if(isUndef(x,y)) {
            set(x,y,Square.FLOOR);
            used++;
          }
        }
      }
    }
  }
  public void renderSpecial(int origin_x,int origin_y,PrePlanned map) {
    if(origin_x+map.sx()-1 >= size.x || origin_x < 0 ||
       origin_y+map.sy()-1>=size.y   || origin_y < 0)
      return;

    for(int x=origin_x,mx=0;x<origin_x+map.sx();x++,mx++) {
      for(int y=origin_y,my=0;y<origin_y+map.sy();y++,my++) {
        Square s = map.get(mx,my);
        if(isUndef(x,y) && s != Square.UNDEF) {
          set(x,y,s);
          used++;
        }
      }
    }
  }
  
  public void show() {
    //System.out.println(this);

    String line = "";
    for(int x=0;x<size.x+2;x++) {
      line += Square.NONE.symb();
    }
    System.out.println(line);
    for(int y=size.y-1;y>=0;y--) {
      line = "";
      line += Square.NONE.symb();
      for(int x=0;x<size.x;x++) {
        Square b = area[x][y];
        line += b.symb();
      }
      line += Square.NONE.symb();
      System.out.println(line);
    }
    line = "";
    for(int x=0;x<size.x+2;x++) {
      line += Square.NONE.symb();
    }
    System.out.println(line);
  }

  @Override
  public String toString() {
    return "Size"+size;
  }
}
