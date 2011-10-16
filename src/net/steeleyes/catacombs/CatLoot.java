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
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Inventory;

public class CatLoot {

  static private void fillChest(CatConfig cnf, Inventory inv, List<String> list) {
    for(String loot : list) {
      String tmp[] = loot.split(":");
      Material m = Material.matchMaterial(tmp[0]);
      if(m!=null) {
        //System.out.print("[Catacombs] "+m+" ("+tmp[1]+"%) ("+tmp[2]+")"); 
        if(cnf.Chance(Integer.parseInt(tmp[1]))) {
          String vals[] = tmp[2].split("-");
          if(vals.length == 1) {
            inv.addItem(new ItemStack(m,Integer.parseInt(vals[0])));
          } else {
            inv.addItem(new ItemStack(m,cnf.nextInt(Integer.parseInt(vals[1])-Integer.parseInt(vals[0])+1)+Integer.parseInt(vals[0])));
          }
        }
      }
    }    
  }
  
  static public void smallChest(CatConfig cnf,Inventory inv) {   
    if(cnf.LeatherEquipChance())
      inv.addItem(leather_equipment(cnf));
    
    fillChest(cnf,inv,cnf.LootSmallList());
  }

  static public void midChest(CatConfig cnf,Inventory inv) {
    if(cnf.MedEquipChance()) {
      if(cnf.Chance(90)) {
        inv.addItem(new ItemStack(Material.IRON_INGOT,cnf.nextInt(10)+1));
        inv.addItem(iron_equipment(cnf));
      } else {
        inv.addItem(new ItemStack(Material.GOLD_INGOT,cnf.nextInt(10)+1));
        inv.addItem(gold_equipment(cnf));
        inv.addItem(new ItemStack(Material.GOLDEN_APPLE,1));
      }
    }
    fillChest(cnf,inv,cnf.LootMediumList());

    smallChest(cnf,inv);
  }

  static public void bigChest(CatConfig cnf,Inventory inv) {

    if(cnf.BigEquipChance())
      inv.addItem(diamond_equipment(cnf));

    fillChest(cnf,inv,cnf.LootBigList());
    smallChest(cnf,inv);
  }
  
  static private ItemStack leather_equipment(CatConfig cnf) {
    ItemStack is = null;
    switch (cnf.nextInt(6)+1) {
      case 1: 
      case 2:  is = new ItemStack(Material.LEATHER_HELMET,1); break;
      case 3:  is = new ItemStack(Material.LEATHER_CHESTPLATE,1); break;
      case 4:  is = new ItemStack(Material.LEATHER_LEGGINGS,1); break;
      case 5: 
      case 6:  is = new ItemStack(Material.LEATHER_BOOTS,1); break;
    }   
    return is;
  }
  static private ItemStack iron_equipment(CatConfig cnf) {
    ItemStack is = null;
    switch (cnf.nextInt(12)+1) {
      case 1:
      case 2:  is = new ItemStack(Material.IRON_HELMET,1); break;
      case 3:  is = new ItemStack(Material.IRON_CHESTPLATE,1); break;
      case 4:  is = new ItemStack(Material.IRON_LEGGINGS,1); break;
      case 5:
      case 6:  is = new ItemStack(Material.IRON_BOOTS,1); break;
      case 7:  is = new ItemStack(Material.IRON_PICKAXE,1); break;
      case 8:
      case 9:  is = new ItemStack(Material.IRON_SPADE,1); break;
      case 10: is = new ItemStack(Material.IRON_AXE,1); break;
      case 11: is = new ItemStack(Material.IRON_SWORD,1); break;
      case 12: is = new ItemStack(Material.IRON_HOE,1); break;
    }
    return is;
  }

  static private ItemStack gold_equipment(CatConfig cnf) {
    ItemStack is = null;
    switch (cnf.nextInt(12)+1) {
      case 1:
      case 2:  is = new ItemStack(Material.GOLD_HELMET,1); break;
      case 3:  is = new ItemStack(Material.GOLD_CHESTPLATE,1); break;
      case 4:  is = new ItemStack(Material.GOLD_LEGGINGS,1); break;
      case 5:
      case 6:  is = new ItemStack(Material.GOLD_BOOTS,1); break;
      case 7:  is = new ItemStack(Material.GOLD_PICKAXE,1); break;
      case 8:
      case 9:  is = new ItemStack(Material.GOLD_SPADE,1); break;
      case 10: is = new ItemStack(Material.GOLD_AXE,1); break;
      case 11: is = new ItemStack(Material.GOLD_SWORD,1); break;
      case 12: is = new ItemStack(Material.GOLD_HOE,1); break;
    }
    return is;
  }

  static private ItemStack diamond_equipment(CatConfig cnf) {
    ItemStack is = null;
    switch (cnf.nextInt(12)+1) {
      case 1:
      case 2:  is = new ItemStack(Material.DIAMOND_HELMET,1); break;
      case 3:  is = new ItemStack(Material.DIAMOND_CHESTPLATE,1); break;
      case 4:  is = new ItemStack(Material.DIAMOND_LEGGINGS,1); break;
      case 5:
      case 6:  is = new ItemStack(Material.DIAMOND_BOOTS,1); break;
      case 7:  is = new ItemStack(Material.DIAMOND_PICKAXE,1); break;
      case 8:
      case 9:  is = new ItemStack(Material.DIAMOND_SPADE,1); break;
      case 10: is = new ItemStack(Material.DIAMOND_AXE,1); break;
      case 11: is = new ItemStack(Material.DIAMOND_SWORD,1); break;
      case 12: is = new ItemStack(Material.DIAMOND_HOE,1); break;
    }
    return is;
  }

}
