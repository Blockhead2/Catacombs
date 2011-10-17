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

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Chest;

import net.steeleyes.maps.*;

public class CatLevel {
  private int roomDepth;
  private int roofDepth;
  private int floorDepth;
  private int levelDepth;

  private CatConfig cnf = null;

  private int coal = 0;
  private int iron = 0;
  private int redstone = 0;
  private int lapis = 0;
  private int gold = 0;
  private int diamond = 0;

  private World world;
  private Level level=null;
  public  CatCuboid cube=null;
  public  Vector top=null;
  public  Vector bot=null;
  public  Boolean build_ok  = false;
  public  Boolean can_go_lower = false;


  public CatLevel(CatConfig cnf, Location pt) {
    this(cnf,pt.getWorld(),pt.getBlockX(),pt.getBlockY()-1,pt.getBlockZ(),Direction.ANY);
  }

  public CatLevel(CatConfig cnf, World world, Vector v,Direction dir) {
    this(cnf,world,v.x,v.y,v.z,dir);
  }

  public CatLevel(CatConfig cnf, World world, Vector v) {
    this(cnf,world,v.x,v.y,v.z,Direction.ANY);
  }

  public CatLevel(CatConfig cnf, World world, int x,int y,int z) {
    this(cnf,world,x,y,z,Direction.ANY);
  }

  public CatLevel(CatConfig cnf, World world, int x,int y,int z,Direction dir) {
    build_ok = false;
    can_go_lower = false;
    this.cnf = cnf;
    roomDepth = cnf.roomDepth();
    roofDepth = cnf.roofDepth();
    floorDepth = cnf.floorDepth();
    levelDepth = floorDepth+roomDepth+roofDepth;
    this.world  = world;

    if(y+1-levelDepth<=4) {
      System.out.println("[Catacombs] Stopping CatLevel due to bedrock");
      return;
    }
    top = new Vector(x,y,z);
    if(spaceForStairs()) {
      cube = getNaturalCuboid(cnf,world,top.x,top.y+1-levelDepth,top.z);

      System.out.println("Natural="+cube);

      if(cube.dx() < 8 || cube.dz() < 8){
        System.out.println("[Catacombs] Stopping CatLevel < 8x8");
        return;
      }
      // 3D-2D
      int sx = top.x-cube.xl;
      int sy = cube.dz()-1-(top.z-cube.zl);
      System.out.println("[Catacombs] start x="+sx+" y="+sy);

      level = new Level(cnf,cube.dx(),cube.dz(),sx,sy,dir);

      // Check number of rooms to make sure things are ok
      if(level.num_rooms()<1) {
        System.out.println("[Catacombs] Stopping CatLevel 0 rooms");
        return;
      }

      build_ok = true;
      if(level.end_dir() != null) {
        // 3D-2D
        int xx = top.x-level.start().x+level.end().x;
        int yy = top.y-levelDepth;
        int zz = top.z+level.start().y-level.end().y;
        bot = new Vector(xx,yy,zz);
        can_go_lower = true;
      }
    } else {
      System.out.println("[Catacombs] Stopping CatLevel no room for stairs");
    }
  }

  public String getMap() {
    return level.getMap();
  }
  
  public String summary() {
    return "Area ("+cube.dx()+" x "+cube.dz()+" dy"+cube.dy()+") Rooms="+level.num_rooms();
  }

  // ToDo: Need to change map over to the PrePlanned class (and in Level too etc)
  public CatLevel(CatConfig cnf, World world, int x, int y, int z, String[] map,Direction dir) {
    build_ok = true;
    can_go_lower = true;
    this.cnf = cnf;
    roomDepth = cnf.roomDepth();
    roofDepth = cnf.roofDepth();
    floorDepth = cnf.floorDepth()+ cnf.extraDepth();
    levelDepth = floorDepth+roomDepth+roofDepth;
    this.world  = world;

    top = new Vector(x,y+roomDepth+roofDepth-1,z);
    bot = new Vector(x,y-floorDepth-1,z);
    level = new Level(cnf,map,dir);

    int xl = x-level.start().x;
    int yl = y-floorDepth;
    int zl = z+level.start().y-level.grid().size.y+1;
    int xh = x-level.start().x+level.grid().size.x-1;
    int yh = y+roomDepth+roofDepth-1;
    int zh = z+level.start().y;    
    cube = new CatCuboid(world,xl,yl,zl,xh,yh,zh,CatCuboid.Type.HUT);
  }
  
  public CatLevel(Catacombs plugin, dbLevel lvl, World world) {
    build_ok = true;
    can_go_lower = true;
    this.cnf = plugin.cnf;
    this.world  = world;

    top = new Vector(lvl.getSx(),lvl.getSy(),lvl.getSz());
    bot = new Vector(lvl.getEx(),lvl.getEy(),lvl.getEz());
    level = new Level(cnf);

    cube = new CatCuboid(world,lvl.getXl(),lvl.getYl(),lvl.getZl(),
            lvl.getXh(),lvl.getYh(),lvl.getZh(),
            (lvl.getHut())?CatCuboid.Type.HUT:CatCuboid.Type.LEVEL);
    if(lvl.getEnable())
      cube.enable();
    else
      cube.suspend();
    roofDepth = cube.guessRoofSize();
    roomDepth = cube.guessRoomSize();
    floorDepth = 0;
    levelDepth = 0;
  }

  public int getRoofDepth() {
    return roofDepth;
  }

  public int getRoomDepth() {
    return roomDepth;
  }
  
  public Direction end_dir() {
    if(level==null)
      return Direction.ANY;
    return level.end_dir();
  }

  public Direction start_dir() {
    if(level==null)
      return Direction.ANY;
    return level.start_dir();
  }

  public Boolean isOk() {
    return level.isOk();
  }

  public void stealDirection(CatLevel from) {
    if(level.end_dir() == null || level.end_dir() == Direction.ANY) {
      level.end_dir(from.start_dir().turn180());
    }
  }

  public void setEndSquare(Square s) {
    if(level.end().x >=0 && level.end().y >= 0) {
      level.grid().set(level.end().x,level.end().y,s);
    }
    can_go_lower = false;
  }

  public final void show() {
    level.show();
    System.out.println(cube);
    System.out.println("TOP :"+top+" BOT:"+bot+" start_dir:"+level.start_dir()+" end_dir:"+level.end_dir()+ " lower:"+can_go_lower);
  }

  private Boolean spaceForStairs() {
    // Check stright down, ignore the top block
    Boolean stairs_ok = areBlocksNatural(top.x,top.y-1,top.z,0,-1,0,levelDepth-1);
    stairs_ok &= areBlocksNatural(top.x+1,top.y-1,top.z,0,-1,0,levelDepth-1);
    stairs_ok &= areBlocksNatural(top.x-1,top.y-1,top.z,0,-1,0,levelDepth-1);
    stairs_ok &= areBlocksNatural(top.x,top.y-1,top.z+1,0,-1,0,levelDepth-1);
    stairs_ok &= areBlocksNatural(top.x,top.y-1,top.z-1,0,-1,0,levelDepth-1);
    return stairs_ok;
  }

  private void renderTileSection(int xx,int y1,int y2, int zz, Material mat) {
    for(int yy=y1;yy<=y2 && mat!=null;yy++) {
      Material use = (mat==cnf.majorMat())?cnf.CobbleType():mat;

      Block b = world.getBlockAt(xx,yy,zz);
      if(mat==Material.AIR) {
        use = cnf.AirType();
      }
      if(b.getType()==Material.IRON_ORE)
        iron++;
      if(b.getType()==Material.COAL_ORE)
        coal++;
      if(b.getType()==Material.DIAMOND_ORE)
        diamond++;
      if(b.getType()==Material.LAPIS_ORE)
        lapis++;
      if(b.getType()==Material.REDSTONE_ORE)
        redstone++;
      if(b.getType()==Material.GOLD_ORE)
        gold++;
      b.setType(use);
    }
  }

  private void renderTile( int xx,int y, int zz,
                          Material floor0,Material floor1,
                          Material room0,Material room1,
                          Material roof0,Material roof1) {
    int floor_l = y+1-levelDepth;
    int floor_h = floor_l+floorDepth-1;
    int room_l  = floor_h+1;
    int room_h  = room_l+roomDepth-1;
    int roof_l  = room_h+1;
    int roof_h  = roof_l+roofDepth-1;
    renderTileSection(xx,floor_l,floor_h-1,zz,floor0);
    renderTileSection(xx,floor_h,floor_h,zz,floor1);
    renderTileSection(xx,room_l,room_l+1,zz,room0);
    renderTileSection(xx,room_l+2,room_h,zz,room1);
    renderTileSection(xx,roof_l,roof_l,zz,roof0);
    renderTileSection(xx,roof_l+1,roof_h,zz,roof1);
    renderTileSection(xx,room_l+2,room_h,zz,room1);
  }

  public void addLeveltoWorld () {
    if(level == null || world == null)
        return;
    Grid g = level.grid();
    int floor_l = top.y+1-levelDepth;
    int floor_h = floor_l+floorDepth-1;
    int room_l  = floor_h+1;
    int room_h  = room_l+roomDepth-1;
    int roof_l  = room_h+1;
    int roof_h  = roof_l+roofDepth-1;

    Material major = cnf.majorMat();
    Material minor = cnf.minorMat();
    Material cob = major;
    Material air = Material.AIR;
    
    Boolean SquareHuts = false;
    
    // Extra cobblestone (major) when outside
    Material ecob = (SquareHuts && cube.isHut())?cob:null;

    // First pass - Place all the Blocks
    for(int x=0;x<g.size.x;x++) {
      for(int y=0;y<g.size.y;y++) {
        // 3D-2D
        Square s = g.get(x,y);
        int xx = top.x+x-level.start().x;
        int zz = top.z-y+level.start().y;
        //int zz = (g.size.y-1-y)+top.z-level.start().y;
        //int zz = y+top.z-level.start_y;

        switch(s) {
          case UPWALL:      renderTile(xx,top.y,zz,null,null,cob ,cob ,cob ,cob ); break;
          case DOWNWALL:    renderTile(xx,top.y,zz,cob ,cob ,cob ,cob ,ecob,null); break;
          case BOTHWALL:    renderTile(xx,top.y,zz,cob ,cob ,cob ,cob ,cob ,cob ); break;
          case WALL:
          case WINDOW:
          case FIXEDWALL:   renderTile(xx,top.y,zz,null,null,cob ,cob ,ecob,null); break;
          case WATER:
          case LAVA:        Material liq = (s==Square.LAVA)?Material.STATIONARY_LAVA:Material.STATIONARY_WATER;
                            renderTile(xx,top.y,zz,cob ,liq ,air ,air ,cob,null);  break;
          case FLOOR:
          case FIXEDFLOOR:  renderTile(xx,top.y,zz,null,cob ,air ,air ,cob,null);  break;
          case FIXEDFLOORUP:   renderTile(xx,top.y,zz,null,cob ,air ,air ,cob,cob );  break;
          case FIXEDFLOORDOWN: renderTile(xx,top.y,zz,cob ,cob ,air ,air ,cob,null);  break;
          case DOOR:
          case WEB:
          case ARCH:
          case HIDDEN:      renderTile(xx,top.y,zz,null,cob ,air ,cob ,ecob,null); break;
          case WORKBENCH:
          case SHROOM:
          case FURNACE:
          case CAKE:
          case TORCH:
          case ANVIL:
          case SOULSAND:
          case BED_F:
          case BED_H:
          case BIGCHEST:
          case MIDCHEST:
          case EMPTYCHEST:
          case CHEST:       renderTile(xx,top.y,zz,null,cob ,air ,air ,cob ,null); break;
          case SPAWNER:     renderTile(xx,top.y,zz,null,cob ,air ,air ,cob ,null); break;
          case O_FLOOR:     
          case O_TORCH:     renderTile(xx,top.y,zz,null,cob ,air ,air ,null,null); break;
          case UP:          renderTile(xx,top.y,zz,null,cob ,air ,air ,air ,air ); break;
          case DOWN:        if(can_go_lower)
                              renderTile(xx,top.y,zz,air ,air ,air ,air ,cob ,null);
                            else
                              renderTile(xx,top.y,zz,null,cob ,air ,air ,cob ,null);
                            break;
          default:
        }
        if(s==Square.HIDDEN) {
          int small = (cnf.Chance(50))?1:0;
          world.getBlockAt(xx,room_l+(1-small),zz).setType(major);
          world.getBlockAt(xx,room_l+small,zz).setType(minor);
          world.getBlockAt(xx,floor_h-2,zz).setType(Material.REDSTONE_TORCH_ON);  // Any wall?
          world.getBlockAt(xx,floor_h-1,zz).setTypeIdAndData(Material.PISTON_STICKY_BASE.getId(),(byte)9,false);
        }
        if(s==Square.CHEST    || s==Square.MIDCHEST ||
           s==Square.BIGCHEST || s==Square.EMPTYCHEST) {
          world.getBlockAt(xx,room_l,zz).setType(Material.CHEST);
          Chest chest = (Chest) world.getBlockAt(xx,room_l,zz).getState();
          if(s==Square.BIGCHEST) {
            CatLoot.bigChest(cnf,chest.getInventory());
            world.getBlockAt(xx,floor_h,zz).setType(Material.GRASS);
          }  else if (s == Square.MIDCHEST) {
            CatLoot.midChest(cnf,chest.getInventory());
            if(coal>0)     chest.getInventory().addItem(new ItemStack(Material.COAL,coal));
            if(iron>0)     chest.getInventory().addItem(new ItemStack(Material.IRON_ORE,iron));
            if(lapis>0)    chest.getInventory().addItem(new ItemStack(Material.INK_SACK,lapis*6,(short)0,(byte)4));
            if(redstone>0) chest.getInventory().addItem(new ItemStack(Material.REDSTONE,redstone*4));
            if(diamond>0)  chest.getInventory().addItem(new ItemStack(Material.DIAMOND,diamond));
            if(gold>0)     chest.getInventory().addItem(new ItemStack(Material.GOLD_ORE,gold));
          }  else if (s == Square.CHEST) {
            CatLoot.smallChest(cnf,chest.getInventory());
          }
        }

        if(s==Square.DOWN && !can_go_lower) {
          world.getBlockAt(xx,room_l,zz).setType(Material.CHEST);
          Chest chest = (Chest) world.getBlockAt(xx,room_l,zz).getState();
          CatLoot.bigChest(cnf,chest.getInventory());
        }
        if(s==Square.WINDOW) {
          world.getBlockAt(xx,room_l+1,zz).setType(Material.GLASS);
        }
        if(s==Square.CAKE) {
          world.getBlockAt(xx,room_l,zz).setType(Material.WOOD);
          world.getBlockAt(xx,room_l+1,zz).setType(Material.CAKE_BLOCK);
        }
        if(s==Square.SOULSAND) {
          world.getBlockAt(xx,floor_h,zz).setType(Material.SOUL_SAND);
        }
        if(s==Square.WEB) {
          world.getBlockAt(xx,room_l,zz).setType(Material.WEB);
          world.getBlockAt(xx,room_l+1,zz).setType(Material.WEB);
        }
        if(s==Square.WORKBENCH) {
          world.getBlockAt(xx,room_l,zz).setType(Material.WORKBENCH);
        }
        if(s==Square.ANVIL) {
          world.getBlockAt(xx,room_l,zz).setType(Material.IRON_BLOCK);
        }
      }
    }

    // Second pass - Place all the items (ladders, doors etc)
    for(int x=0;x<g.size.x;x++) {
      for(int y=0;y<g.size.y;y++) {
        // 3D-2D
        Square s = g.get(x,y);
        int xx = top.x+x-level.start().x;
        int zz = top.z-y+level.start().y;
        if(s==Square.DOOR) {
          byte code = g.getDoorCode(x,y);
          world.getBlockAt(xx,room_l,zz).setTypeIdAndData(Material.WOODEN_DOOR.getId(),(byte)code,false);
          world.getBlockAt(xx,room_l+1,zz).setTypeIdAndData(Material.WOODEN_DOOR.getId(),(byte)(code+8),false);
        }
        if(s==Square.UP) {
          byte code = getLadderCode(x,y);
          for(int yy=room_l;yy<=room_h;yy++) {
            world.getBlockAt(xx,yy,zz).setTypeIdAndData(Material.LADDER.getId(),code,false);
          }
          for(int yy=roof_l;yy<=roof_h;yy++) {
            world.getBlockAt(xx,yy,zz).setTypeIdAndData(Material.LADDER.getId(),code,false);
          }
        }
        if(s==Square.FURNACE) {
          byte code = getFurnaceCode(x,y);
          world.getBlockAt(xx,room_l,zz).setTypeIdAndData(Material.FURNACE.getId(),code,false);
        }
        if(s==Square.SHROOM) {
          world.getBlockAt(xx,room_l,zz).setType(cnf.ShroomType());
        }
        if(s==Square.O_TORCH) {
          world.getBlockAt(xx,room_l,zz).setTypeIdAndData(Material.TORCH.getId(),(byte)5,false);
        }
        if(s==Square.TORCH) {
          world.getBlockAt(xx,room_l+2,zz).setType(Material.TORCH);
          if(cube.isHut())
            world.getBlockAt(xx,roof_l+1,zz).setType(Material.TORCH);
        }
        if(s==Square.BED_H) {
          byte code = (byte) (getBedCode(x,y) | 8);
          world.getBlockAt(xx,room_l,zz).setTypeIdAndData(Material.BED_BLOCK.getId(),(byte)code,false);
        }
        if(s==Square.BED_F) {
          byte code = getBedCode(x,y);
          world.getBlockAt(xx,room_l,zz).setTypeIdAndData(Material.BED_BLOCK.getId(),(byte)code,false);
        }
        if(s==Square.SPAWNER) {
          world.getBlockAt(xx,room_l,zz).setType(Material.MOB_SPAWNER);
          CreatureSpawner spawner = (CreatureSpawner) world.getBlockAt(xx,room_l,zz).getState();
          String type = cnf.SpawnerType();
          spawner.setCreatureTypeId(type);
          if(type.equals("Wolf")) {
             world.getBlockAt(xx,floor_h,zz).setType(Material.GRASS);
             if(world.getBlockAt(xx+1,floor_h,zz).getType() == major)
               world.getBlockAt(xx+1,floor_h,zz).setType(Material.GRASS);
             if(world.getBlockAt(xx-1,floor_h,zz).getType() == major)
               world.getBlockAt(xx-1,floor_h,zz).setType(Material.GRASS);
             if(world.getBlockAt(xx,floor_h,zz+1).getType() == major)
               world.getBlockAt(xx,floor_h,zz+1).setType(Material.GRASS);
             if(world.getBlockAt(xx,floor_h,zz-1).getType() == major)
               world.getBlockAt(xx,floor_h,zz-1).setType(Material.GRASS);
          }
        }
        if(s==Square.DOWN && can_go_lower) {
          byte code = getLadderCode(x,y);
          if(level.end_dir() != null) {
            code = getLadderCode(level.end_dir());
          }
          for(int yy=floor_l;yy<=floor_h;yy++) {
            world.getBlockAt(xx,yy,zz).setTypeIdAndData(Material.LADDER.getId(),code,false);
          }
          code = getTrapDoorCode(x,y);
          world.getBlockAt(xx,room_l,zz).setTypeIdAndData(Material.TRAP_DOOR.getId(),(byte)code,false);
        }
      }
    }
  }

  private CatCuboid getNaturalCuboid(CatConfig cnf,World world,int ox,int oy, int oz) {
    int lx = ox;
    int lz = oz;
    int hx = ox;
    int hz = oz;
    CatCuboid space = null;

    if(world == null)
      return new CatCuboid(world,top.x-10,top.y,top.z-10,top.x+10,top.y-levelDepth+1,top.z+10);

    int iteration = 1;

    int changed;
    do {
      Boolean natural;
      int px,pz;
      changed = 0;

      // Try expand in low X direction
      for(px=lx-1,pz=lz,natural=true;pz<=hz && natural;pz++)
        natural = natural && areBlocksNatural(px,oy,pz,0,1,0,levelDepth);
      if(natural && ox-lx<cnf.RadiusMax()) { changed++; lx--; }

      // Try expand in High X direction
      for(px=hx+1,pz=lz,natural=true;pz<=hz && natural;pz++)
        natural = natural && areBlocksNatural(px,oy,pz,0,1,0,levelDepth);
      if(natural && hx-ox<cnf.RadiusMax()) { changed++; hx++; }

      // Try expand in low Z direction
      for(px=lx,pz=lz-1,natural=true;px<=hx && natural;px++)
        natural = natural && areBlocksNatural(px,oy,pz,0,1,0,levelDepth);
      if(natural && oz-lz<cnf.RadiusMax()) { changed++; lz--; }

      // Try expand in High Z direction
      for(px=lx,pz=hz+1,natural=true;px<=hx && natural;px++)
        natural = natural && areBlocksNatural(px,oy,pz,0,1,0,levelDepth);
      if(natural && hz-oz<cnf.RadiusMax()) { changed++; hz++; }

      iteration++;
    } while (changed>0 && iteration < 256);

    space = new CatCuboid(world,lx,oy,lz,hx,oy+levelDepth-1,hz);
    return space;
  }

  private Boolean areBlocksNatural(int cx, int cy, int cz,int dx,int dy,int dz, int cnt) {
    // For testing
    if(world==null)
      return true;

    for(int i=1;i<=cnt;i++) {
      Block blk = world.getBlockAt(cx,cy,cz);
      if(!CatCuboid.isBlockNatural(blk)) {
        return false;
      }
      cx += dx;
      cy += dy;
      cz += dz;
    }
    return true;
  }

  public byte getTrapDoorCode(int x, int y) {
    Direction dir = level.grid().getBackWallDir(x, y);
    switch(dir) {
      case NORTH: return 2;
      case EAST:  return 1;
      case SOUTH: return 3;
      case WEST:  return 0;
    }
    return 0;
  }
  
  public byte getLadderCode(int x, int y) {
    if(cube.isHut())
      return 0;
    return getLadderCode(level.grid().getBackWallDir(x, y));
  }

  public byte getLadderCode(Direction dir) {
    switch(dir) {
      case NORTH: return 3;
      case EAST:  return 4;
      case SOUTH: return 2;
      case WEST:  return 5;
    }
    return 0;
  }

  public byte getFurnaceCode(int x, int y) {
    Direction dir = level.grid().getFloorDir(x, y);
    switch(dir) {
      case NORTH: return 4;
      case EAST:  return 5;
      case SOUTH: return 3;
      case WEST:  return 2;
    }
    return 4;
  }
  public byte getBedCode(int x, int y) {
    Direction dir = level.grid().getBedDir(x, y);
    switch(dir) {
      case NORTH: return 2;
      case EAST:  return 3;
      case SOUTH: return 0;
      case WEST:  return 1;
    }
    return 0;
  }
}
