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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.file.FileConfiguration;

public class MobTypes {
  
  private Map<String,MobType> mobs = new HashMap<String,MobType>();
  private Map<String,CatAbility> abilities = new HashMap<String,CatAbility>();
  FileConfiguration fcnf;
  
  public MobTypes(FileConfiguration config) {
    fcnf = config;
    File mobTypeFile = new File("plugins" + File.separator + "Catacombs" + File.separator + "monsters.yml");
    try {
      if(mobTypeFile.exists()) {
        fcnf.load(mobTypeFile);
        System.out.println("[Catacombs]");
        for(String ability: getKeys(fcnf,"ability")) {
          String path = "ability."+ability;
          String effect = getSString(path+".effect");
          String target = getSString(path+".target");
          int after     = getSInt(path+".after");
          CatAbility ab = new CatAbility(ability,effect,target,after);
          abilities.put(ability,ab);
          System.out.println("[Catacombs]   ability="+ability);
          System.out.println("[Catacombs]     effect="+effect);
          System.out.println("[Catacombs]     target="+target);
          System.out.println("[Catacombs]     after="+after);
        }
        for(String mob: getKeys(fcnf,"monster")) {
          String path = "monster."+mob;
          String shape = getSString(path+".shape");
          int hps = getSInt(path+".hps");
          List<CatAbility> list = new LinkedList<CatAbility>();

          //System.out.println("[Catacombs]   mob="+mob);
          //System.out.println("[Catacombs]     shape="+shape);
          //System.out.println("[Catacombs]     hps="+hps);
          for(String abil: getSStringList(path+".abilities")) {
            if(abilities.containsKey(abil)) {
              //System.out.println("[Catacombs]     ability="+abil);
              list.add(abilities.get(abil));
            } else {
              System.out.println("[Catacombs] Abilitiy '"+abil+"' required by '"+mob+"' is not defined");              
            }
          }
          MobType mt = new MobType(mob,shape,hps,list);
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

  private static List<String> getKeys(FileConfiguration config, String path) {
    if(config.contains(path)) {
      List<String> list = new ArrayList<String>();
      for(String s:config.getConfigurationSection(path).getKeys(false))
        list.add(s);
      return list;
    }
    return null;
  }
  
  protected Object getSP(String path) {
    Object property = null;
    if (property == null) {
      property = fcnf.get(path);
    }
    if(property == null) {
      System.err.println("[Catacombs] No configuration attribute called "+path+" or <style>."+path);
    }
    return property;    
  }
  
  @SuppressWarnings("unchecked")
  private Boolean getSBoolean(String path) {
    return (Boolean) getSP(path);
  }
  @SuppressWarnings("unchecked")
  private Integer getSInt(String path) {
    return (Integer) getSP(path);
  }
  @SuppressWarnings("unchecked")
  private Double getSDouble(String path) {
    return (Double) getSP(path);
  }
  @SuppressWarnings("unchecked")
  private String getSString(String path) {
    return (String) getSP(path);
  }
  @SuppressWarnings("unchecked")
  private List<Boolean> getSBooleanList(String path) {
    return (List<Boolean>) getSP(path);
  }
  @SuppressWarnings("unchecked")
  private List<Integer> getSIntList(String path) {
    return (List<Integer>) getSP(path);
  }
  @SuppressWarnings("unchecked")
  private List<Double> getSDoubleList(String path) {
    return (List<Double>) getSP(path);
  }
  @SuppressWarnings("unchecked")
  private List<String> getSStringList(String path) {
    return (List<String>) getSP(path);
  } 
}
