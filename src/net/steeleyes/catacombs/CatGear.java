package net.steeleyes.catacombs;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class CatGear {  
  private Player player;
  
  private List<ItemStack> gear = new ArrayList<ItemStack>(36);
  private ItemStack feet=null;
  private ItemStack chest=null;
  private ItemStack legs=null;
  private ItemStack head=null;
  
  private Location grave=null;
    
  public CatGear(Player player) {
    this.player = player;
    PlayerInventory inv = player.getInventory();
    grave = player.getLocation();
    
    if (inv.getBoots() != null && inv.getBoots().getType() != Material.AIR)
      feet = inv.getBoots().clone();
    if (inv.getChestplate() != null && inv.getChestplate().getType() != Material.AIR)
      chest = inv.getChestplate().clone();
    if (inv.getLeggings() != null && inv.getLeggings().getType() != Material.AIR)
      legs = inv.getLeggings().clone();
    if (inv.getHelmet() != null && inv.getHelmet().getType() != Material.AIR)
      head = inv.getHelmet().clone();

    for(int i=0;i<=35;i++) {
      ItemStack it = null;
      if(inv.getItem(i)!=null) {
        it = inv.getItem(i).clone();        
      }
      gear.add(it);
    }    
  }
  
  public void dropGear() {
    World world = grave.getWorld();
    if(head!=null)
      world.dropItemNaturally(grave, head);
    if(chest!=null)
      world.dropItemNaturally(grave, chest);
    if(legs!=null)
      world.dropItemNaturally(grave, legs);
    if(feet!=null)
      world.dropItemNaturally(grave, feet);
    
    for(int i=0;i<=35;i++) {
      ItemStack stk = gear.get(i);
      if (stk != null && stk.getType() != Material.AIR) {
        world.dropItemNaturally(grave, stk);
      }
    }     
    
  }

  public void restoreGear() {
    PlayerInventory inv = player.getInventory();
    if (head!=null && (inv.getHelmet() == null || inv.getHelmet().getType() == Material.AIR))
      inv.setHelmet(head);
    if (chest!=null && (inv.getChestplate() == null || inv.getChestplate().getType() == Material.AIR))
      inv.setChestplate(chest);
    if (legs!=null && (inv.getLeggings() == null || inv.getLeggings().getType() == Material.AIR))
      inv.setLeggings(legs);      
    if (feet!=null && (inv.getBoots() == null || inv.getBoots().getType() == Material.AIR))
      inv.setBoots(feet);      

    for(int i=0;i<=35;i++) {
      ItemStack stk = gear.get(i);
      if (stk != null && stk.getType() != Material.AIR) {
        inv.setItem(i, gear.get(i));
      }
    }    
  }
  
}
