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

import java.util.Random;

public enum Direction {
  EAST,SOUTH,WEST,NORTH,NORTH_EAST,NORTH_WEST,SOUTH_EAST,SOUTH_WEST,ANY;
  
  static public Direction any(Random rnd) {
    Direction dir=WEST;
    switch (rnd.nextInt(4)) {
      case 0: dir=EAST; break;
      case 1: dir=SOUTH; break;
      case 2: dir=WEST; break;
      case 3: dir=NORTH; break;
    }
    return dir;
  }
  public Direction turn90() {
    switch (this) {
      case NORTH: return Direction.EAST;
      case SOUTH: return Direction.WEST;
      case EAST: return Direction.SOUTH;
      case WEST: return Direction.NORTH;
      case ANY: return Direction.ANY;
    }
    return ANY;
  }
  public Direction turn180() {
    switch (this) {
      case NORTH: return Direction.SOUTH;
      case SOUTH: return Direction.NORTH;
      case EAST: return Direction.WEST;
      case WEST: return Direction.EAST;
      case ANY: return Direction.ANY;
    }
    return ANY;
  }
  public Direction turn270() {
    switch (this) {
      case NORTH: return Direction.WEST;
      case SOUTH: return Direction.EAST;
      case EAST: return Direction.NORTH;
      case WEST: return Direction.SOUTH;
      case ANY: return Direction.ANY;
    }
    return ANY;
  }

  public Boolean horizontal() {
    return this == EAST || this == WEST;
  }
  public Boolean vertical() {
    return this == NORTH || this == SOUTH;
  }
/*
  public Vector2D forwards(Vector2D old) {
    Vector2D v = new Vector2D(old);
    if(this == Direction.ANY) {
      System.err.println("INTERNAL ERROR: stepping forwards on a Driection.ANY not implemented");
    }
    switch (this) {
      case NORTH: v.y++; break;
      case EAST:  v.x++; break;
      case SOUTH: v.y--; break;
      case WEST:  v.x--; break;
    }
    return v;
  }
  public Vector2D backwards(Vector2D old) {
    Vector2D v = new Vector2D(old);
    if(this == Direction.ANY) {
      System.err.println("INTERNAL ERROR: stepping backwards on a Driection.ANY not implemented");
    }
    switch (this) {
      case NORTH: v.y--; break;
      case EAST:  v.x--; break;
      case SOUTH: v.y++; break;
      case WEST:  v.x++; break;
    }
    return v;
  }
  public Vector2D backwards(int x, int y) {
    if(this == Direction.ANY) {
      System.err.println("INTERNAL ERROR: stepping backwards on a Driection.ANY not implemented");
    }
    switch (this) {
      case NORTH: y--; break;
      case EAST:  x--; break;
      case SOUTH: y++; break;
      case WEST:  x++; break;
    }
    return new Vector2D(x,y);
  }
  public Vector2D left(Vector2D old) {
    Vector2D v = new Vector2D(old);
    if(this == Direction.ANY) {
      System.err.println("INTERNAL ERROR: stepping left on a Driection.ANY not implemented");
    }
    switch (this) {
      case NORTH: v.x--; break;
      case EAST:  v.y++; break;
      case SOUTH: v.x++; break;
      case WEST:  v.y--; break;
    }
    return v;
  }
  public Vector2D right(Vector2D old) {
    Vector2D v = new Vector2D(old);
    if(this == Direction.ANY) {
      System.err.println("INTERNAL ERROR: stepping right on a Driection.ANY not implemented");
    }
    switch (this) {
      case NORTH: v.x++; break;
      case EAST:  v.y--; break;
      case SOUTH: v.x--; break;
      case WEST:  v.y++; break;
    }
    return v;
  }
*/
  public int dx(int steps) {
    if(this == EAST) {
      return steps;
    }
    if(this == WEST) {
      return -steps;
    }
    return 0;
  }
  public int dy(int steps) {
    if(this == NORTH) {
      return steps;
    }
    if(this == SOUTH) {
      return -steps;
    }
    return 0;
  }  
  public int forwards_x(int n) {
    if(this == EAST) {
      return n+1;
    }
    if(this == WEST) {
      return n-1;
    }
    return n;
  }
  public int forwards_y(int n) {
    if(this == NORTH) {
      return n+1;
    }
    if(this == SOUTH) {
      return n-1;
    }
    return n;
  }

  public int backwards_x(int n) {
    if(this == EAST) {
      return n-1;
    }
    if(this == WEST) {
      return n+1;
    }
    return n;
  }
  public int backwards_y(int n) {
    if(this == NORTH) {
      return n-1;
    }
    if(this == SOUTH) {
      return n+1;
    }
    return n;
  }

  public int left_x(int n) {
    if(this == NORTH) {
      return n-1;
    }
    if(this == SOUTH) {
      return n+1;
    }
    return n;
  }
  public int left_y(int n) {
    if(this == EAST) {
      return n+1;
    }
    if(this == WEST) {
      return n-1;
    }
    return n;
  }
  
  public int right_x(int n) {
    if(this == NORTH) {
      return n+1;
    }
    if(this == SOUTH) {
      return n-1;
    }
    return n;
  }
  public int right_y(int n) {
    if(this == EAST) {
      return n-1;
    }
    if(this == WEST) {
      return n+1;
    }
    return n;
  }
}
