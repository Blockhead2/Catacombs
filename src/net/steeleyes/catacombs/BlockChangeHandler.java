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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class BlockChangeHandler implements Runnable {  
  private final int MAX_CHANGE = 10000;
  private int changed = 0;
  
  private List<BlockChange> high = new ArrayList<BlockChange>();
  private List<BlockChange> low  = new ArrayList<BlockChange>();
  private List<Player>      who  = new ArrayList<Player>();

  @Override
  public void run() {
    int cnt=0;
    while(!high.isEmpty() && cnt < MAX_CHANGE) {
      BlockChange x = high.remove(0);
      x.blk.setType(x.mat);
      cnt++;
    }
    while(!low.isEmpty() && cnt < MAX_CHANGE) {
      BlockChange x = low.remove(0);
      x.blk.setType(x.mat);
      cnt++;
    } 
    if(cnt>0) {
      changed += cnt;
    }
    if(cnt == 0 && changed > 0) {
      System.out.println("[Catacombs] Block Handler #changes="+changed);
      for(Player p : who) {
        p.sendMessage("Delete is complete");
      }
      who.clear();
      changed = 0;
    }
  }
  
  public void addLow(Block blk,Material mat) {
    low.add(new BlockChange(blk,mat));
  }
  public void addHigh(Block blk,Material mat) {
    high.add(new BlockChange(blk,mat));
  }
  public void add(Player player) {
    who.add(player);
  } 
}
