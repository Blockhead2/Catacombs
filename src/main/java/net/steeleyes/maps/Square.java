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

import java.util.Map;
import java.util.HashMap;
import java.util.EnumSet;

public enum Square {
  UNDEF(' '),
  WALL('#'),
  FIXEDWALL('X'),
  UPWALL('U'),
  DOWNWALL('D'),
  BOTHWALL('B'),
  PRESSURE('x'),
  RED1('1'),
  RED2('2'),
  ARROW('>'),
  FLOOR('.'),
  FIXEDFLOOR(','),
  FIXEDFLOORUP(';'),
  FIXEDFLOORDOWN(':'),
  O_FLOOR('`'),
  WINDOW('G'),
  BARS('I'),
  HIGH_BARS('b'),
  DOOR('+'),
  UP('^'),
  DOWN('V'),
  ARCH('A'),
  HIDDEN('$'),
  WATER('W'),
  LAVA('L'),
  ANVIL('a'),
  FURNACE('f'),
  BOOKCASE('k'),
  BOOKCASE2('K'),
  SIGNPOST('p'),
  ENCHANT('e'),
  TORCH('t'),
  O_TORCH('~'),
  WEB('w'),
  SHROOM('m'),
  CAKE('='),
  SOULSAND('s'),
  EMPTYCHEST('o'),
  CHEST('c'),
  MIDCHEST('C'),
  BIGCHEST('*'),
  WORKBENCH('T'),
  SPAWNER('M'),
  BED_H('Z'),
  BED_F('z'),
  NONE('!');

  private final char ch;

  private static final Map<Character,Square> lookup
          = new HashMap<Character,Square>();

  static {
    for(Square s : EnumSet.allOf(Square.class))
      lookup.put(s.ch,s);
  }

  Square(char ch) {
    this.ch = ch;
  }

  public char symb() {
    return ch;
  }

  public static Square get(char c) {
    Square s = lookup.get(c);
    if(s==null) {
      System.err.println("ERROR: Attempt to get Symbol for unknown char "+c);
      s = Square.UNDEF;
    }
    return s;
  }

  public Boolean isFloor() {
    return this == FLOOR ||
      this == FIXEDFLOOR ||
      this == FIXEDFLOORUP ||
      this == FIXEDFLOORDOWN;
  }

  public Boolean isWall() {
    return this == Square.WALL ||
      this == Square.FIXEDWALL ||
      this == Square.DOWNWALL ||
      this == Square.BOTHWALL ||
      this == Square.UPWALL;
  }

  public Boolean isChest() {
    return this == Square.CHEST ||
      this == Square.MIDCHEST ||
      this == Square.BIGCHEST ||
      this == Square.EMPTYCHEST;
  }

  public Boolean isDoor() {
    return this == Square.DOOR ||
      this == Square.ARCH ||
      this == Square.HIDDEN ||
      this == Square.WEB;
  }

  public Boolean isStair() {
    return this == Square.UP ||
      this == Square.DOWN;
  }

  public Boolean isUndef() {
    return this == Square.UNDEF;
  }

  public Boolean isBoundaryUndef() {
    return this == Square.WALL ||
      this == Square.UNDEF ||
      this == Square.FIXEDWALL ||
      this == Square.ARCH ||
      this == Square.DOWNWALL ||
      this == Square.BOTHWALL ||
      this == Square.UPWALL ||
      this == Square.UP ||
      this == Square.DOWN ||
      this == Square.DOOR ||
      this == Square.HIDDEN ||
      this == Square.WEB;

//    return this.isUndef() || this.isWall() || this.isStair() || this.isDoor();
  }
}