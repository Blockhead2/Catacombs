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

import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;

import com.sk89q.catacombs.wepif.PermissionsResolverManager;

public class CatPermissions {
  //public Object  permissionHandler;
  //public Boolean enabled = false;
  
  private PermissionsResolverManager perm;

  public CatPermissions(Plugin plugin) {
    System.out.println("[Catacombs] Using WEPIF for find permissions plugin");
    PermissionsResolverManager.initialize(plugin);
    perm = PermissionsResolverManager.getInstance();
    if(perm == null) {
      System.out.println("[Catacombs] No permissions plugin found, will use player's Op flag");
    }
  }
  
//  public CatPermissions(Server server) {
//    System.out.println("[Catacombs] Looking for Permissions plugin"); 
//    Plugin test = server.getPluginManager().getPlugin("Permissions");
//    if(test != null) {
//      permissionHandler = (PermissionHandler) ((Permissions) test).getHandler();
//      enabled = true;
//      System.out.println("[Catacombs] Found and will use plugin "+test.getDescription().getFullName());
//    } else {
//      System.out.println("[Catacombs] Permission system not detected, defaulting to OP");
//    }
//  }
  
  private Boolean permission(Player player, String permission) {
    if(perm != null && player != null) {
      //return ((PermissionHandler) permissionHandler).has(player, permission);
      return perm.hasPermission(player, permission);
    }
    if(player != null)
      return player.isOp();
    
    return true;
  }

  public boolean admin(Player player){
    return permission(player, "catacombs.admin");
  }

  public Boolean hasPermission(Player player,String perm) {
    return permission(player, "catacombs.admin") ||
           permission(player, perm);
  }
}
