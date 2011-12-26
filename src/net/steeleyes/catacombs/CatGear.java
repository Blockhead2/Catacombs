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
  private static Boolean debug = false;
  
  private Player player;
  
  private List<ItemStack> gear = new ArrayList<ItemStack>(36);
  private ItemStack feet=null;
  private ItemStack chest=null;
  private ItemStack legs=null;
  private ItemStack head=null;
  
  private Location grave=null;
    
  //public CatGear(Player player,List<ItemStack> drops) {
  public CatGear(Player player) {
    this.player = player;
    PlayerInventory inv = player.getInventory();
    grave = player.getLocation();
    
    if (inv.getBoots().getType() != Material.AIR) {
      feet = inv.getBoots().clone();
      if(debug) System.out.println("[Catacombs]  feet " + feet);
    }

    if (inv.getChestplate().getType() != Material.AIR) {
      chest = inv.getChestplate().clone();
      if(debug) System.out.println("[Catacombs]  chest " + chest);
    }

    if (inv.getLeggings().getType() != Material.AIR) {
      legs = inv.getLeggings().clone();
      if(debug) System.out.println("[Catacombs]  legs " + legs);
    }

    if (inv.getHelmet().getType() != Material.AIR) {
      head = inv.getHelmet().clone();
      if(debug) System.out.println("[Catacombs]  helm " + head);
    }

    for(int i=0;i<=35;i++) {
      ItemStack it = inv.getItem(i).clone();
      gear.add(it);
      if(it.getType() != Material.AIR) {
        if(debug) System.out.println("[Catacombs]  slot "+i+" : "+it+" : "+System.identityHashCode(it));
        //ItemStack dummy = new ItemStack(Material.TORCH,1);
        //inv.setItem(i,dummy);
      }
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
    if(debug) System.out.println("[Catacombs]  inside CatGear.restoreGear");

    PlayerInventory inv = player.getInventory();
    if (head!=null && inv.getHelmet().getType() == Material.AIR) {
      inv.setHelmet(head);
      if(debug) System.out.println("restore " + head);
    }
    if (chest!=null && inv.getChestplate().getType() == Material.AIR) {
      inv.setChestplate(chest);
      if(debug) System.out.println("restore " + chest);
      
    }
    if (legs!=null && inv.getLeggings().getType() == Material.AIR) {
      inv.setLeggings(legs);
      if(debug) System.out.println("restore " + legs);
      
    }
    if (feet!=null && inv.getBoots().getType() == Material.AIR) {
      inv.setBoots(feet);
      if(debug) System.out.println("restore " + feet);
      
    }
    for(int i=0;i<=35;i++) {
      ItemStack stk = gear.get(i);
      if (stk != null && stk.getType() != Material.AIR) {
        inv.setItem(i, gear.get(i));
        if(debug) System.out.println("restore " + i + " : " + stk + " "+ System.identityHashCode(stk));
      }
    }    
  }
  
}
