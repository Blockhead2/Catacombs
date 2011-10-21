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

import org.bukkit.World;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.ContainerBlock;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Chest;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Creature;
import org.bukkit.Location;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
/**
 *
 * @author John Keay
 */
public class CatCuboid extends Cuboid {
  private Type type = Type.LEVEL;
  private Boolean enable = true;
  private World world = null;
  public enum Type { LEVEL, HUT }

  public CatCuboid(World world,int xl,int yl,int zl,int xh,int yh, int zh, Type type) {
    super(xl,yl,zl,xh,yh,zh);
    this.type = type;
    this.world = world;
  }

  public CatCuboid(World world,int xl,int yl,int zl,int xh,int yh, int zh) {
    super(xl,yl,zl,xh,yh,zh);
    this.world = world;
  }
  
  @Override
  public String toString() {
    return super.toString()+" enable="+enable+" world="+world+" type="+type;
  }

  public void setType(Type t) {
    type = t;
  }

  public Type getType() {
    return type;
  }
  
  //  Add 3 horrible routine to guess key facts that I didn't save in the .db
  //    TODO: need to figure out how to expand the table without loosing backwards compatibility,
  public int guessRoofSize() {
    int depth = 256;
    for(int x=xl;x<=xh;x++) {
      for(int z=zl;z<=zh;z++) {
        for(int y=yh,i=0;y>=yl;y--,i++) {
          Block blk = world.getBlockAt(x,y,z);
          Material mat = blk.getType();
          if(mat == Material.AIR) { 
            if(i==0) break;          // Stop if top is AIR
            if(i<depth) depth = i;
          }
        }
      }
    }    
    return depth;
  } 
  
  public int guessRoomSize() {
    int max_depth = 0;
    for(int x=xl;x<=xh;x++) {
      for(int z=zl;z<=zh;z++) {
        int depth=0;
        for(int y=yh,i=0;y>=yl;y--,i++) {
          Block blk = world.getBlockAt(x,y,z);
          Material mat = blk.getType();
          if(mat == Material.AIR) { 
            if(i==0)
              break;          // Stop if top is AIR
            depth++;
          } else {
            if(depth>0) {
              if(depth>max_depth)
                max_depth = depth;
              break;          // Stop on first solid after air.
            }
          }
        }
      }
    }    
    return max_depth;
  } 
  
  static int debug = 0;

  // TODO: Badly need to make the major/minor block names persist!!
  //   Hunt for a frequent soild block just below the roof level that's
  //   next to air/web/torch. Call that the major material
  public CatMat guessMajorMat(int roofDepth) {
    List<BlockFace> dirs = new ArrayList<BlockFace>(Arrays.asList(
      BlockFace.NORTH,BlockFace.EAST,
      BlockFace.SOUTH,BlockFace.WEST
    ));

    final CatMat last = new CatMat(Material.AIR);
    final CatMat best_mat = new CatMat(Material.AIR);
    final CatMat mat = new CatMat(Material.AIR);
    int best=0;
    int cnt=0;
    if(debug==1) System.out.println("[Catacombs] roofDepth="+roofDepth);

    for(int x=xl;x<=xh;x++) {
      for(int z=zl;z<=zh;z++) {
        int y=yh-roofDepth;
        Block blk = world.getBlockAt(x,y,z);
        mat.getBlock(blk);
        if(debug==1) System.out.println("[Catacombs] Block mat="+mat+" blk="+blk.getType());

        if(!mat.is(Material.AIR) && !mat.is(Material.TORCH)) {
        if(debug==1) System.out.println("[Catacombs]   soid block Block mat="+mat+" blk="+blk.getType());
          Boolean near_air = false;
          for(BlockFace dir : dirs) {
            Material near = blk.getRelative(dir).getType();
            if(near==Material.AIR || near==Material.TORCH || near==Material.WEB) {
              near_air=true;
              break;
            }
          }
          if(near_air) {
           if(debug==1) System.out.println("[Catacombs]     near air Block mat="+mat+" blk="+blk.getType());
           if(!mat.equals(last)) {
              if(cnt>best) {
                best_mat.get(last);
                best = cnt;
              }
              last.get(mat);
              cnt=1;
            } else {
              cnt++;
            }  
          }
        }
      }
    }
    if(cnt>best) {
      best_mat.get(last);
      best = cnt;
    }
    if(debug==1) System.out.println("[Catacombs] final guess mat = "+best_mat+" best="+best);
    //debug++;
    return best_mat;
  }
  
  public Boolean getEnable() {
    return enable;
  }

  public void setEnable(Boolean enable) {
    this.enable = enable;
  }
  
  public void suspend() {
    enable = false;
  }
  
  public void enable() {
    enable = true;
  }
  
  public Boolean isEnabled() {
    return enable;
  }
  
  public Boolean isLevel() {
    return type == Type.LEVEL;
  }
  
  public Boolean isHut() {
    return type == Type.HUT;
  }
  
  public Boolean overlaps(CatCuboid that) {
    return world.getName().equals(that.world.getName()) &&
      intersects(that);
  }
  
  public Boolean isProtected(Block blk) {
    return enable && world.getName().equals(blk.getWorld().getName()) &&
      super.isIn(blk.getX(),blk.getY(),blk.getZ());
  }
  
  public Boolean isInRaw(Block blk) {
    return world.getName().equals(blk.getWorld().getName()) &&
      super.isIn(blk.getX(),blk.getY(),blk.getZ());
  } 
  
  public Boolean isSuspended(Block blk) {
    return !enable &&  world.getName().equals(blk.getWorld().getName()) &&
      super.isIn(blk.getX(),blk.getY(),blk.getZ());
  }  
  
  public void unrender(BlockChangeHandler handler,Boolean emptyChest,int num_air) {
    if(type==Type.LEVEL)
      setCube(handler,Material.STONE,true);
    else {
      CatCuboid floor = new CatCuboid(world,xl,yl,zl,xh,yh-num_air,zh);
      // Go one higher to cover anything (torches typically) on the hut roof.
      CatCuboid upper = new CatCuboid(world,xl,yh-num_air+1,zl,xh,yh+1,zh);
      upper.setCube(handler,Material.AIR,emptyChest);
      floor.setCube(handler,Material.STONE,true);
    }
  }

  public void clearBlock(Material mat) {
    for(int x=xl;x<=xh;x++) {
      for(int z=zl;z<=zh;z++) {
        for(int y=yl;y<=yh;y++) {
          Block blk = world.getBlockAt(x,y,z);
          if(blk.getType() == mat) {
            if(mat == Material.CHEST) {
              Object o = blk.getState();
              if(o != null && o instanceof ContainerBlock) {
                ContainerBlock cont = (ContainerBlock) o;
                cont.getInventory().clear();
              }
            }
            blk.setType(Material.AIR);
          }
        }
      }
    }
  }
  
  public void addGlow(CatMat major,int roofDepth) {
    for(int x=xl;x<=xh;x++) {
      for(int z=zl;z<=zh;z++) {
        int y = yh-roofDepth+1;
        Block blk = world.getBlockAt(x,y,z);
        Block other = blk.getRelative(BlockFace.DOWN,1);
        if(major.equals(blk) && other.getType() == Material.AIR) {
          blk.setType(Material.GLOWSTONE);
        }
      }
    }
  }
  
  public void removeGlow(CatMat major,int roofDepth) {
    for(int x=xl;x<=xh;x++) {
      for(int z=zl;z<=zh;z++) {
        int y = yh-roofDepth+1;
        Block blk = world.getBlockAt(x,y,z);
        Block other = blk.getRelative(BlockFace.DOWN,1);
        if(blk.getType() == Material.GLOWSTONE && other.getType() == Material.AIR) {
          major.setBlock(blk);
        }
      }
    }
  }  
  
  public void closeDoors() {
    for(int x=xl;x<=xh;x++) {
      for(int z=zl;z<=zh;z++) {
        for(int y=yl;y<=yh;y++) {
          Block blk = world.getBlockAt(x,y,z);
          if(blk.getType() == Material.WOODEN_DOOR || blk.getType() == Material.IRON_DOOR) {
            byte data = blk.getData();
            int d = data & 7;
            if(d == 4 || d == 2)
              data = (byte)(data ^ 4);
            else if (d == 3 || d == 7) {
              Block other = blk.getRelative(BlockFace.EAST,1); //NORTH in my coords
              if(other.getType() == Material.WOODEN_DOOR) {
                data = (byte) (data | 7);
              } else {
                data = (byte) (data & ~4);
              }
            }
            blk.setData(data);

          } else if(blk.getType() == Material.TRAP_DOOR) {
            blk.setData((byte)(blk.getData() & ~4));
          } else if(blk.getType() == Material.PISTON_STICKY_BASE) {
            Block power = blk.getRelative(BlockFace.DOWN,1);
            if(power.getType() != Material.REDSTONE_TORCH_ON) {
              Block upper_door = blk.getRelative(BlockFace.UP,3);
              Material m = power.getType();
              power.setType(Material.REDSTONE_TORCH_ON);
              upper_door.setType(m);
            }
          }
        }
      }
    }
  }

  public void refillChests(CatConfig config) {
    for(int x=xl;x<=xh;x++) {
      for(int z=zl;z<=zh;z++) {
        for(int y=yl;y<=yh;y++) {
          Block blk = world.getBlockAt(x,y,z);
          if(blk.getType() == Material.CHEST) {
            if(type == Type.LEVEL) {  // Leave chests in the hut
              Chest chest = (Chest) blk.getState();
              chest.getInventory().clear(); // Clear and refill chests inside
              if(isBigChest(blk)) {
                CatLoot.bigChest(config,chest.getInventory());
              }  else if (isMidChest(blk)) {
                CatLoot.midChest(config,chest.getInventory());
              }  else {
                CatLoot.smallChest(config,chest.getInventory());
              }
            }
          } else if(blk.getType() == Material.DISPENSER) {
            Dispenser cont = (Dispenser) blk.getState();
            cont.getInventory().clear(); // Clear and refill chests inside
            CatLoot.fillChest(config,cont.getInventory(),config.TrapList());
          }
        }
      }
    }
  }

  public void clearMonsters() {
    for(Entity ent : world.getEntities()) {
      if(ent instanceof Creature) {
        Location loc = ent.getLocation();
        Block blk = loc.getBlock();
        if(isIn(blk.getX(),blk.getY(),blk.getZ())) {
          ent.remove();
        }
      }
    }
  }
/*
  public void clearPlayers() {
    for(Entity ent : world.getEntities()) {
      if(ent instanceof Player) {
        Player player = (Player) ent;
        Location loc = ent.getLocation();
        Block blk = loc.getBlock();
        if(isIn(blk.getX(),blk.getY(),blk.getZ())) {
          Location safe_place = world.getHighestBlockAt(loc).getLocation();
          System.out.println("[Catacombs] "+player.getName()+" is in dungeon, moving to 'safety'");
          player.teleport(safe_place);
        }
      }
    }
  }
*/
  public Boolean isBigChest(Block blk) {
    int cnt = 0;
    Material major = Material.COBBLESTONE;
    Material minor = Material.MOSSY_COBBLESTONE;

    Material below = blk.getRelative(BlockFace.DOWN,1).getType();
    
    Block b;
    b = blk.getRelative(BlockFace.EAST,1);
    if(b.getType() == major || b.getType() == minor) {
      cnt++;
    }
    b = blk.getRelative(BlockFace.WEST,1);
    if(b.getType() == major || b.getType() == minor) {
      cnt++;
    }
    b = blk.getRelative(BlockFace.NORTH,1);
    if(b.getType() == major || b.getType() == minor) {
      cnt++;
    }
    b = blk.getRelative(BlockFace.SOUTH,1);
    if(b.getType() == major || b.getType() == minor) {
      cnt++;
    }
    return cnt==3 || below == Material.GRASS;
  }

  public Boolean isMidChest(Block blk) {
    int cnt = 0;

    Block b;
    b = blk.getRelative(BlockFace.EAST,1);
    if(b.getType() == Material.CHEST) {
      cnt++;
    }
    b = blk.getRelative(BlockFace.NORTH,1);
    if(b.getType() == Material.CHEST) {
      cnt++;
    }
    return cnt==1;
  }

  public void setCube(BlockChangeHandler handler, Material mat,Boolean emptyChest) {
    for(int x=xl;x<=xh;x++) {
      for(int z=zl;z<=zh;z++) {
        for(int y=yl;y<=yh;y++) {
          Block blk = world.getBlockAt(x,y,z);
          if(emptyChest) {
            Object o = blk.getState();
            if(o != null && o instanceof ContainerBlock) {
              ContainerBlock cont = (ContainerBlock) o;
              cont.getInventory().clear();
            }
          }
          Material before = blk.getType();
          if(before == Material.LADDER ||
             before == Material.TORCH ||
             before == Material.TRAP_DOOR ||
             before == Material.WATER ||
             before == Material.LAVA ||
             before == Material.BED_BLOCK ||   // Not sure how best to delete beds
             before == Material.RED_MUSHROOM ||
             before == Material.BROWN_MUSHROOM ||
             before == Material.REDSTONE_TORCH_ON ||
             before == Material.WOODEN_DOOR
             ) {
            handler.addHigh(blk,mat);
          } else {
            if(before == Material.CHEST) {
              // A bukkit bug crashes the client when chests are deleted.
              //blk.setType(Material.DIRT);
              //handler.addHigh(blk,mat);
            } else {
              handler.addLow(blk,mat);
            }
          }
        }
      }
    }
  }

  public Boolean isNatural(CatConfig cnf) {
    for(int x=xl;x<=xh;x++) {
      for(int y=yl;y<=yh;y++) {
        for(int z=zl;z<=zh;z++) {
          Block blk = world.getBlockAt(x,y,z);
          if(!cnf.isNatural(blk)) {
            return false;
          }
        }
      }
    }
    return true;
  }

  public Boolean isCubeAir() {
    for(int x=xl;x<=xh;x++) {
      for(int y=yl;y<=yh;y++) {
        for(int z=zl;z<=zh;z++) {
          Block blk = world.getBlockAt(x,y,z);
          if(blk.getType() != Material.AIR) {
            return false;
          }
        }
      }
    }
    return true;
  }


}
