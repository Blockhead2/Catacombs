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
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Chest;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Creature;
import org.bukkit.Location;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.server.v1_4_5.EnumSkyBlock;
import org.bukkit.craftbukkit.v1_4_5.CraftWorld;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.material.Bed;
import org.bukkit.material.MaterialData;


public class CatCuboid extends Cuboid {
  private Type type = Type.LEVEL;
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
    return super.toString()+" world="+world.getName()+" type="+type;
  }
  

  public List<String> dump(Vector top) {
    List<String> info = map();
    info.add(" ");
    for(int y=yl;y<=yh;y++) {
      for(int x=xl;x<=xh;x++) {
        for(int z=zl;z<=zh;z++) {
          Block blk = world.getBlockAt(x,y,z);
          info.add((x-top.x)+","+(y-top.y)+","+(z-top.z)+","+blk.getTypeId()+","+blk.getData());
        }
      }
    }
    return info;
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
    if(depth==256)
      depth = 1;
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
    if(max_depth<3)
      max_depth = 3;
    return max_depth;
  } 
  
  public Vector guessEndLocation() {
    // Look for a ladder down first
    for(int x=xl;x<=xh;x++) {
      for(int z=zl;z<=zh;z++) {
        int y = yl;
        Block blk = world.getBlockAt(x,y,z);
        if(blk.getType()==Material.LADDER) {
          return new Vector(x,y-1,z);
        }
      }
    }
    // If no ladder down then look for the end chest
    for(int x=xl;x<=xh;x++) {
      for(int z=zl;z<=zh;z++) {
        for(int y=yl;y<=yh;y++) {
          Block blk = world.getBlockAt(x,y,z);
          if(isBigChest(blk)) {
            return new Vector(x,yl-1,z);
          }
        }
      }
    }
    return null;
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
  
  public Boolean isLevel() {
    return type == Type.LEVEL;
  }
  
  public Boolean isHut() {
    return type == Type.HUT;
  }
  
  public Boolean overlaps(CatCuboid that) {
    // higher levels check the worlds match so no need to check again
    assert(world.equals(that.world));
    return intersects(that);
  }

  public Boolean isInRaw(Block blk) {
    // higher levels check the worlds match so no need to check again
    assert(world.equals(blk.getWorld()));
    return super.isIn(blk.getX(),blk.getY(),blk.getZ());
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

  @Deprecated
  public void clearBlock(Material mat) {
    for(int x=xl;x<=xh;x++) {
      for(int z=zl;z<=zh;z++) {
        for(int y=yl;y<=yh;y++) {
          Block blk = world.getBlockAt(x,y,z);
          if(blk.getType() == mat) {
            if(mat == Material.CHEST) {
              Object o = blk.getState();
              if(o != null && o instanceof InventoryHolder) {
                InventoryHolder cont = (InventoryHolder) o;
                cont.getInventory().clear();
              }
            }
            blk.setType(Material.AIR);
          }
        }
      }
    }
  }
  
  public void removeTorch() {
    for(int x=xl;x<=xh;x++) {
      for(int z=zl;z<=zh;z++) {
        for(int y=yl;y<=yh;y++) {
          Block blk = world.getBlockAt(x,y,z);
          if(blk.getType() == Material.TORCH) {
            blk.setTypeId(Material.AIR.getId());
          }
        }
      }
    }
    forceLightLevel(0);
  }
  
  public void forceLightLevel(int level) {
    net.minecraft.server.v1_4_5.World w = ((CraftWorld) world).getHandle();
    for(int x=xl;x<=xh;x++) {
      for(int z=zl;z<=zh;z++) {
        for(int y=yl;y<=yh;y++) {
          w.b(EnumSkyBlock.BLOCK, x, y, z, level);
        }
      }
    }
  }
  
  public void restoreCake() {
    for(int x=xl;x<=xh;x++) {
      for(int z=zl;z<=zh;z++) {
        for(int y=yl;y<=yh;y++) {
          Block blk = world.getBlockAt(x,y,z);
          Block above = blk.getRelative(BlockFace.UP);
          if(blk.getType() == Material.FENCE) {
            if(above.getType() == Material.AIR) {
              above.setType(Material.CAKE_BLOCK);
            } else if(above.getType() == Material.CAKE_BLOCK) {
              if(above.getData() != (byte)0) {
                above.setData((byte)0);
              }
            }
          }
        }
      }
    }
  }
  
  public void addGlow(CatMat mat,int roofDepth) {
    for(int x=xl;x<=xh;x++) {
      for(int z=zl;z<=zh;z++) {
        int y = yh-roofDepth+1;
        Block blk = world.getBlockAt(x,y,z);
        Block other = blk.getRelative(BlockFace.DOWN,1);
        if(mat.equals(blk) && other.getType() == Material.AIR) {
          blk.setType(Material.GLOWSTONE);
        }
      }
    }
  }
  
  public void removeGlow(CatMat mat,int roofDepth) {
    for(int x=xl;x<=xh;x++) {
      for(int z=zl;z<=zh;z++) {
        int y = yh-roofDepth+1;
        Block blk = world.getBlockAt(x,y,z);
        Block other = blk.getRelative(BlockFace.DOWN,1);
        if(blk.getType() == Material.GLOWSTONE && other.getType() == Material.AIR) {
          mat.setBlock(blk);
        }
      }
    }
  }  
  
  public void buildWindows(CatMat mat,int height) {
    if(type == Type.HUT)
      return;
    
    int x,z;
    int y = yl+height;

    x = xl;
    for(z=zl+1;z<=zh-1;z++) {
      Block blk = world.getBlockAt(x,y,z);
      if(world.getBlockAt(x-1,y,z).getType() == Material.AIR &&
         world.getBlockAt(x+1,y,z).getType() == Material.AIR  ) {
        mat.setBlock(blk);
      }
    }
    x = xh;
    for(z=zl+1;z<=zh-1;z++) {
      Block blk = world.getBlockAt(x,y,z);
      if(world.getBlockAt(x-1,y,z).getType() == Material.AIR &&
         world.getBlockAt(x+1,y,z).getType() == Material.AIR  ) {
        mat.setBlock(blk);
      }
    }
    z = zl;
    for(x=xl+1;x<=xh-1;x++) {
      Block blk = world.getBlockAt(x,y,z);
      if(world.getBlockAt(x,y,z-1).getType() == Material.AIR &&
         world.getBlockAt(x,y,z+1).getType() == Material.AIR  ) {
        mat.setBlock(blk);
      }
    }
    z = zh;
    for(x=xl+1;x<=xh-1;x++) {
      Block blk = world.getBlockAt(x,y,z);
      if(world.getBlockAt(x,y,z-1).getType() == Material.AIR &&
         world.getBlockAt(x,y,z+1).getType() == Material.AIR  ) {
        mat.setBlock(blk);
      }
    }
  }   
  
  public void closeDoors(Catacombs plugin) {
    //Boolean secretOn = !plugin.cnf.SecretDoorOff();
    for(int x=xl;x<=xh;x++) {
      for(int z=zl;z<=zh;z++) {
        for(int y=yl;y<=yh;y++) {
          Block blk = world.getBlockAt(x,y,z);
          if(blk.getType() == Material.WOODEN_DOOR || blk.getType() == Material.IRON_DOOR_BLOCK) {
            byte data = blk.getData();
            if(data >= 4 && data < 8) {
              data = (byte)(data ^ 4);
              blk.setData(data);
            }
          } else if(blk.getType() == Material.TRAP_DOOR) {
            blk.setData((byte)(blk.getData() & ~4));
          } else if(blk.getType() == Material.LEVER) {
            blk.setData((byte)(blk.getData() & ~8));
          } else if(blk.getType() == Material.PISTON_STICKY_BASE) {
            Block power = blk.getRelative(BlockFace.DOWN,1);
            if(power.getType() != Material.REDSTONE_TORCH_ON) {
              Block upper_door = blk.getRelative(BlockFace.UP,3);
              Material m = power.getType();
              byte code = power.getData();
              power.setType(Material.REDSTONE_TORCH_ON);
              upper_door.setTypeIdAndData(m.getId(),code,false);
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

  public void clearMonsters(Catacombs plugin) {
    for(Entity ent : world.getEntities()) {
      if(ent instanceof Creature) {
        Location loc = ent.getLocation();
        Block blk = loc.getBlock();
        if(isIn(blk.getX(),blk.getY(),blk.getZ())) {
          //if(plugin.monsters != null)
          //  plugin.monsters.remove((LivingEntity)ent);
          ent.remove();
        }
      }
    }
  }
  
  // Issues with the secret door piston creation in earlier versions of Catacombs
  //   left some secret doors broken (only opening by 1 block rather than 2).
  //   This code finds any problem locations by inspection of nearby blocks
  //   and fixes them.
  public int fixSecretDoors() {
    int cnt = 0;
    for(int x=xl;x<=xh;x++) {
      for(int z=zl;z<=zh;z++) {
        for(int y=yl;y<=yh;y++) {
          Block blk = world.getBlockAt(x,y,z);
          Material mat = blk.getType();
          Block above = blk.getRelative(BlockFace.UP);
          Block above2 = above.getRelative(BlockFace.UP);
          Block below = blk.getRelative(BlockFace.DOWN);
          if(blk.getType() == Material.PISTON_STICKY_BASE &&
             below.getType() == Material.REDSTONE_TORCH_ON &&
             above.getType() != Material.PISTON_EXTENSION) {
            //System.out.println("[Catacombs] Fixing closed secret door "+blk+" "+mat+" "+above.getType()+" "+below.getType());
            above.setTypeIdAndData(Material.PISTON_EXTENSION.getId(),(byte)9,false);
            blk.setTypeIdAndData(Material.PISTON_STICKY_BASE.getId(),(byte)9,false);
            cnt++;
          } else if (blk.getType() == Material.PISTON_STICKY_BASE &&
             below.getType() != Material.REDSTONE_TORCH_ON && above2.getType() != Material.AIR) {
             //System.out.println("[Catacombs] fixing open secret door "+blk+" "+mat+" "+above.getType()+" "+below.getType());
             above.setTypeIdAndData(above2.getType().getId(),above2.getData(),false);
             above2.setType(Material.AIR);
             blk.setTypeIdAndData(Material.PISTON_STICKY_BASE.getId(),(byte)1,false);
             cnt++;
          }
        }
      }
    }
    return cnt;
  }
  
  // Minecraft v1.2.3 Changed the way door hinges are positioned
  //   This code will fix up the old dungeons by inspection of nearby
  //   blocks. It will also close all the doors.
  public int fixDoors() {
    int cnt = 0;
    for(int x=xl;x<=xh;x++) {
      for(int z=zl;z<=zh;z++) {
        for(int y=yl;y<=yh;y++) {
          Block blk = world.getBlockAt(x,y,z);
          Material mat = blk.getType();
          Block above = blk.getRelative(BlockFace.UP);
          if((mat == Material.WOODEN_DOOR && above.getType() == Material.WOODEN_DOOR) ||
             (mat == Material.IRON_DOOR_BLOCK && above.getType() == Material.IRON_DOOR_BLOCK) ) {
            byte a = above.getData();
            byte b = blk.getData();
            byte new_a = getDoorUpperCode(blk);
            byte new_b = getDoorLowerCode(blk);
            if(a!=new_a || b!=new_b) {
              blk.setData(new_b); 
              above.setData(new_a); 
              cnt++;
            }
          }
        }
      }
    }
    return cnt;
  }
  
  public int changeDoorsToIron() {
    int cnt = 0;
    for(int x=xl;x<=xh;x++) {
      for(int z=zl;z<=zh;z++) {
        for(int y=yl;y<=yh;y++) {
          Block blk = world.getBlockAt(x,y,z);
          Material mat = blk.getType();
          Block above = blk.getRelative(BlockFace.UP);
          if(mat == Material.WOODEN_DOOR && above.getType() == Material.WOODEN_DOOR) {
            byte blk_b = blk.getData();
            byte above_b = above.getData();
            blk.setTypeIdAndData(Material.IRON_DOOR_BLOCK.getId(), blk_b, false);
            above.setTypeIdAndData(Material.IRON_DOOR_BLOCK.getId(), above_b, false);
            cnt++;
          }
        }
      }
    }
    return cnt;
  }
  
  private byte getDoorLowerCode(Block blk) {
    Block e = blk.getRelative(BlockFace.EAST);
    Block w = blk.getRelative(BlockFace.WEST);
    if(CatUtils.isSolid(e) && CatUtils.isSolid(w))
      return 0;

    Block n = blk.getRelative(BlockFace.NORTH);
    Block s = blk.getRelative(BlockFace.SOUTH);
    if(CatUtils.isSolid(n) && CatUtils.isSolid(s))
      return 1;
    
    if(CatUtils.isSolid(e)) return 0;
    if(CatUtils.isSolid(w)) return 0;
    if(CatUtils.isSolid(s)) return 1;
    if(CatUtils.isSolid(n)) return 1;
    return 0;
  }
  
  private byte getDoorUpperCode(Block blk) {
    Block e = blk.getRelative(BlockFace.EAST);
    Block w = blk.getRelative(BlockFace.WEST);
    if(CatUtils.isSolid(e) && CatUtils.isSolid(w))
      return 8;

    Block n = blk.getRelative(BlockFace.NORTH);
    Block s = blk.getRelative(BlockFace.SOUTH);
    if(CatUtils.isSolid(n) && CatUtils.isSolid(s))
      return 8;
    
    if(CatUtils.isSolid(e)) return 8;
    if(CatUtils.isSolid(w)) return 9;
    if(CatUtils.isSolid(s)) return 8;
    if(CatUtils.isSolid(n)) return 9;
    return 0;
  }  
  
  public Boolean isBigChest(Block blk) {
    if(blk.getType() != Material.CHEST)
      return false;
    
    int cnt = 0;
    Material major = Material.COBBLESTONE;
    Material minor = Material.MOSSY_COBBLESTONE;

    Material below = blk.getRelative(BlockFace.DOWN,1).getType();
    
    for(BlockFace dir : Arrays.asList(
      BlockFace.NORTH,BlockFace.EAST,
      BlockFace.SOUTH,BlockFace.WEST)) {
      Block b = blk.getRelative(dir,1);
      if(b.getType() == major || b.getType() == minor) {
        cnt++;
      }
    }

    return below == Material.GRASS || cnt == 3;
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
          Object o = blk.getState();
          if(emptyChest) {
            if(o != null && o instanceof InventoryHolder) {
              InventoryHolder cont = (InventoryHolder) o;
              cont.getInventory().clear();
            }
          }
          Material before = blk.getType();
          if(before == Material.LADDER ||
             before == Material.TORCH ||
             before == Material.TRAP_DOOR ||
             before == Material.WATER ||
             before == Material.LAVA ||
             before == Material.RED_MUSHROOM ||
             before == Material.BROWN_MUSHROOM ||
             before == Material.REDSTONE_TORCH_ON ||
             before == Material.IRON_DOOR_BLOCK ||
             before == Material.WALL_SIGN ||
             before == Material.SIGN_POST ||
             before == Material.VINE ||
             before == Material.WOODEN_DOOR
             ) {
            handler.addHigh(blk,mat);
          } else if(before == Material.BED_BLOCK) {
            MaterialData md = blk.getState().getData();
            if(md instanceof Bed) {
              Bed bed = (Bed) md;
              if(bed.isHeadOfBed()) {
                Block foot = blk.getRelative(bed.getFacing().getOppositeFace());
                foot.setType(Material.AIR);
                blk.setType(Material.AIR);
                handler.addLow(blk,mat);
                handler.addLow(blk,mat);
              }
            }
          } else if(o != null && o instanceof InventoryHolder) {
            blk.setType(Material.AIR);
            blk.breakNaturally();
            handler.addLow(blk,mat);
          } else {
            handler.addLow(blk,mat);
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

  // Try and figure out an approximate map from the world blocks
  public List<String> map() {
    List<String> info = new ArrayList<String>();
    char [][] grid = new char[xh-xl+1][zh-zl+1];
    for(int x=xl;x<=xh;x++) {
      for(int z=zl;z<=zh;z++) {
        grid[x-xl][z-zl] = ' ';
      }
    }
    for(int y=yl;y<=yh;y++) {
      for(int x=xl;x<=xh;x++) {
        for(int z=zl;z<=zh;z++) {
          Block blk = world.getBlockAt(x,y,z);
          int id = blk.getTypeId();
          char was = grid[x-xl][z-zl];
          if (id == 29) grid[x-xl][z-zl]='$';
          if ((was == ' ' || was==',') && (id==4 || id==48 || id==98 || id==24)) grid[x-xl][z-zl]='#';
          if ((was == '#' || was==' ') && (id==0)) grid[x-xl][z-zl]=',';
          if ((was == ',') && (id==0)) grid[x-xl][z-zl]='.';
          if (id==20 || id==102) grid[x-xl][z-zl]='G';
          if (id==9 || id ==8) grid[x-xl][z-zl]='W';
          if (id==11 || id == 10) grid[x-xl][z-zl]='L';
          if (id==26) grid[x-xl][z-zl]='z';
          if (id==30) grid[x-xl][z-zl]='w';
          if (id==42) grid[x-xl][z-zl]='A';
          if (id==50) grid[x-xl][z-zl]='t';
          if (id==52) grid[x-xl][z-zl]='M';
          if (id==54) grid[x-xl][z-zl]='c';
          if (id==58) grid[x-xl][z-zl]='T';
          if (id==61) grid[x-xl][z-zl]='f';
          if (id==64) grid[x-xl][z-zl]='+';
          if (id==65) grid[x-xl][z-zl]='^';
          if (id==70) grid[x-xl][z-zl]='x';
          if (id==88) grid[x-xl][z-zl]='s';
          if (id==92) grid[x-xl][z-zl]='=';
          if (id==96) grid[x-xl][z-zl]='v';
        }
      }
    }
    for(int z=zl;z<=zh;z++) {
      String s = "# ";
      for(int x=xl;x<=xh;x++) {
        char ch = grid[x-xl][z-zl];
        s += (ch==',')?'#':ch;
      }
      info.add(s);
    }
    info.add(" ");
    return info;
  }
}
