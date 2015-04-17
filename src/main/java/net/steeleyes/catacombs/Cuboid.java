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

public class Cuboid {
  public int xl,xh;
  public int yl,yh;
  public int zl,zh;

  public Cuboid(int xl,int yl,int zl,int xh,int yh, int zh) {
    this.xl = Math.min(xl,xh);
    this.yl = Math.min(yl,yh);
    this.zl = Math.min(zl,zh);
    this.xh = Math.max(xl,xh);
    this.yh = Math.max(yl,yh);
    this.zh = Math.max(zl,zh);
  }

  public Boolean isIn(int x,int y,int z) {
    if(x<xl) return false;
    if(x>xh) return false;
    if(z<zl) return false;
    if(z>zh) return false;
    if(y<yl) return false;  // Check vertical last
    if(y>yh) return false;
    return true;
  }

  public Boolean intersects(Cuboid that) {
    if(that.xl>this.xh) return false;
    if(that.xh<this.xl) return false;
    if(that.zl>this.zh) return false;
    if(that.zh<this.zl) return false;
    if(that.yl>this.yh) return false; // Check vertical last
    if(that.yh<this.yl) return false;
    return true;
  }
  
  // Merge the cuboids to create a minimum cuboid that contains both.
  public void union(Cuboid that) {
    this.xl = Math.min(xl,that.xl);
    this.yl = Math.min(yl,that.yl);
    this.zl = Math.min(zl,that.zl);
    this.xh = Math.max(xh,that.xh);
    this.yh = Math.max(yh,that.yh);
    this.zh = Math.max(zh,that.zh);    
  }

  @Override
  public String toString() {
    return "Cuboid ("+xl+","+yl+","+zl+") ("+xh+","+yh+","+zh+")";
  }

  public Boolean isequal(Cuboid that) {
    if(this.xl == that.xl &&
       this.xh == that.xh &&
       this.yl == that.yl &&
       this.yh == that.yh &&
       this.zl == that.zl &&
       this.zh == that.zh)
      return true;
    return false;
  }

  public int floorArea() { return (xh-xl+1)*(zh-zl+1); }
  public int dx()        { return (xh-xl+1); }
  public int dy()        { return (yh-yl+1); }
  public int dz()        { return (zh-zl+1); }

}
