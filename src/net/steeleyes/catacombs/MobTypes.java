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

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.file.FileConfiguration;

public class MobTypes {
  
  private Map<String,MobType> mobs = new HashMap<String,MobType>();
  private Map<String,CatAbility> abilities = new HashMap<String,CatAbility>();
  private Map<String,CatLootList> loot = new HashMap<String,CatLootList>();
  FileConfiguration fcnf;
  
  public MobTypes(FileConfiguration config) {
    fcnf = config;
    File mobTypeFile = new File("plugins" + File.separator + "Catacombs" + File.separator + "monsters.yml");
    try {
      if(mobTypeFile.exists()) {
        fcnf.load(mobTypeFile);
        System.out.println("[Catacombs]");
        for(String ability: CatUtils.getKeys(fcnf,"ability")) {
          System.out.println("[Catacombs]   ability="+ability);
          CatAbility ab = new CatAbility(ability,fcnf,"ability."+ability);
          abilities.put(ability,ab);
        }
        for(String group: CatUtils.getKeys(fcnf,"loot")) {
          System.out.println("[Catacombs]   loot=loot."+group);
          CatLootList ll = new CatLootList(group,fcnf,"loot."+group);
          loot.put(group,ll);
        }
        for(String mob: CatUtils.getKeys(fcnf,"monster")) {
          String path = "monster."+mob;
          String shape = CatUtils.getSString(fcnf,path+".shape");
          int hps = CatUtils.getSInt(fcnf,path+".hps");
          String gold = CatUtils.getSString(fcnf,path+".gold");
          List<CatAbility> ability_list = new LinkedList<CatAbility>();
          for(String abil: CatUtils.getSStringList(fcnf,path+".abilities")) {
            if(abilities.containsKey(abil)) {
              ability_list.add(abilities.get(abil));
            } else {
              System.out.println("[Catacombs] Abilitiy '"+abil+"' required by '"+mob+"' is not defined");              
            }
          }
          List<CatLootList> loot_list = new LinkedList<CatLootList>();
          for(String l: CatUtils.getSStringList(fcnf,path+".loot")) {
            if(loot.containsKey(l)) {
              loot_list.add(loot.get(l));
            } else {
              System.out.println("[Catacombs] Loot '"+l+"' required by '"+mob+"' is not defined");              
            }
          }
          MobType mt = new MobType(mob,shape,hps,gold,ability_list,loot_list);
          mobs.put(mob,mt);
          System.out.println("[Catacombs] mob="+mt);
        }

      } else {
        System.out.println("[Catacombs] monsters file doesn't exist");
      }
    } catch (Exception e) {
      System.err.println("[Catacombs] "+e.getMessage());
    }
  }
}
