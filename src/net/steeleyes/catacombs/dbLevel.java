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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity()
@Table(name="levels")
public class dbLevel {
  @Id
  private int id;
  private String dname;
  private String wname;
  private String pname;
 
  private Boolean hut;
  private int xl; 
  private int yl; 
  private int zl; 
  private int xh; 
  private int yh; 
  private int zh;   
  
  private int sx;
  private int sy;
  private int sz;
  
  private int ex;
  private int ey;
  private int ez;  
  private Boolean enable;
  
  private int num;
  
  private int dx;
  private int dy;
  @Lob
  private String map;
  transient private String major="cobblestone";
  
  transient private Boolean legacy = false;

  public String getMap() {
    return map;
  }

  public void setMap(String map) {
    this.map = map;
  }

  public String getMajor() {
    return major;
  }

  public void setMajor(String major) {
    this.major = major;
  }

  public int getDx() {
    return dx;
  }

  public void setDx(int dx) {
    this.dx = dx;
  }

  public int getDy() {
    return dy;
  }

  public void setDy(int dy) {
    this.dy = dy;
  }

  public Boolean getEnable() {
    return enable;
  }

  public void setEnable(Boolean enable) {
    this.enable = enable;
  }
 

  public Boolean getLegacy() {
    return legacy;
  }

  public void setLegacy(Boolean legacy) {
    this.legacy = legacy;
  }
  
  @Override
  public String toString() {
    return "dbCube id("+id+") dungeon("+dname+") world("+wname+") owner("+pname+") enable("+enable+") num("+num+") "+
            "hut("+hut+") ("+xl+","+yl+","+zl+") ("+xh+","+yh+","+zh+") " +
            "("+sx+","+sy+","+sz+") ("+ex+","+ey+","+ez+")";
  }
  
  public void setLegacy(String dname,String wname,int xl, int yl, int zl, int xh, int yh, int zh, Boolean hut) {
    this.dname = dname;
    this.wname = wname;
    this.pname = "???";
    this.hut   = hut;
    this.xl    = xl;
    this.yl    = yl;
    this.zl    = zl;
    this.xh    = xh;
    this.yh    = yh;
    this.zh    = zh;
    this.sx    = (xh+xl)>>1;
    this.sy    = yh;
    this.sz    = (zh+zl)>>1;
    this.ex    = 0;
    this.ey    = 0;
    this.ez    = 0;
    this.dx    = 0;
    this.dy    = 0;
    this.map   = "";
    this.enable = true;
    legacy = true;
  }
  
  public void setDname(String dname) {
    this.dname = dname;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setWname(String wname) {
    this.wname = wname;
  }

  public void setXh(int xh) {
    this.xh = xh;
  }

  public void setXl(int xl) {
    this.xl = xl;
  }

  public void setYh(int yh) {
    this.yh = yh;
  }

  public void setYl(int yl) {
    this.yl = yl;
  }

  public void setZh(int zh) {
    this.zh = zh;
  }

  public void setZl(int zl) {
    this.zl = zl;
  }

  public String getDname() {
    return dname;
  }

  public int getId() {
    return id;
  }

  public String getWname() {
    return wname;
  }

  public int getXh() {
    return xh;
  }

  public int getXl() {
    return xl;
  }

  public int getYh() {
    return yh;
  }

  public int getYl() {
    return yl;
  }

  public int getZh() {
    return zh;
  }

  public int getZl() {
    return zl;
  }
  
  public Boolean getHut() {
    return hut;
  }

  public void setHut(Boolean hut) {
    this.hut = hut;
  }

  public String getPname() {
    return pname;
  }

  public void setPname(String pname) {
    this.pname = pname;
  }
  
  public int getEx() {
    return ex;
  }

  public void setEx(int ex) {
    this.ex = ex;
  }

  public int getEy() {
    return ey;
  }

  public void setEy(int ey) {
    this.ey = ey;
  }

  public int getEz() {
    return ez;
  }

  public void setEz(int ez) {
    this.ez = ez;
  }

  public int getSx() {
    return sx;
  }

  public void setSx(int sx) {
    this.sx = sx;
  }

  public int getSy() {
    return sy;
  }

  public void setSy(int sy) {
    this.sy = sy;
  }

  public int getSz() {
    return sz;
  }

  public void setSz(int sz) {
    this.sz = sz;
  }
  
  public int getNum() {
    return num;
  }

  public void setNum(int num) {
    this.num = num;
  }
}
