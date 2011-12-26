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

import org.bukkit.block.Block;
import org.bukkit.Material;

public class CatMat {
  private Material mat;
  private byte code = 0;
  private Boolean has_code = false;
  
  // TODO: Add creator function from Strings like this 'smooth_brick:2'
  
  public CatMat(Material mat) {
    this.mat = mat;
  }
  
  public CatMat(Block blk) {
    this.mat = blk.getType();
    if(blk.getData()>0) {
      this.code = blk.getData();
      has_code = true;
    }
  }
  
  public CatMat(Material mat, byte code) {
    this.mat = mat;
    this.code = code;
    has_code = true;
  } 

  static public CatMat parseMaterial(String orig) {
    CatMat m = null;
    String name = orig;
    byte code = -1;
    if(name.contains(":")) {
      String tmp[] = name.split(":");
      name = tmp[0];
      try {
        code = Byte.parseByte(tmp[1]);
      } catch(Exception e) {
        System.err.println("[Catacombs] Unknown material '"+orig+"' invalid data byte, expecting a number");
        return null;
      }
    }
    Material mat = Material.matchMaterial(name);
    if(mat == null || !mat.isBlock()) {
      System.err.println("[Catacombs] Invalid block material '"+orig+"'");
      return null;
    }
    if(code>=0)
      return new CatMat(mat,code);
    return new CatMat(mat);
  }   
  //public CatMat dup() {
  //  if(has_code)
  //    return new CatMat(mat,code);
  //  return new CatMat(mat);
  //}
  
  @Override
  public String toString() {
    if(has_code)
      return mat.toString()+":"+code;
    return mat.toString();
  }
  
  public Boolean equals(CatMat that) {
    if(that == null) return false;
    if(this.has_code || that.has_code) {
      return this.mat == that.mat && this.code == that.code;
    }
    return this.mat == that.mat;
  }
  
  public Boolean equals(Block blk) {
    if(this.has_code || blk.getData()> 0) {
      return this.mat == blk.getType() && this.code == blk.getData();
    }
    return this.mat == blk.getType();
  }
  
  public Boolean is(Material that) {
    if(this.has_code) {
      return false;
    }
    return this.mat == that;
  } 
  public void setBlock(Block blk) {
    if(this.has_code)
      blk.setTypeIdAndData(mat.getId(), this.code, false);
    else
      blk.setType(mat);
  }

  public void getBlock(Block blk) {
    Material m = blk.getType();
    byte c = blk.getData();
    if(c> 0) {
      code = c;
      has_code = true;
    } else {
      code = -1;
      has_code = false;
    }
    mat = m;
  }
  
  public void get(CatMat that) {
    has_code=that.has_code;
    mat=that.mat;
    code=that.code;
  }
  
  public byte getCode() {
    return code;
  }

  public Boolean getHas_code() {
    return has_code;
  }

  public Material getMat() {
    return mat;
  }
  
  
}
