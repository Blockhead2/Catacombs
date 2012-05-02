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
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class MobTypes {
  
  private Map<String,MobType> mobs = new HashMap<String,MobType>();
  private Map<String,CatAbility> abilities = new HashMap<String,CatAbility>();
  private Map<String,CatLootList> loot = new HashMap<String,CatLootList>();
  
  private FileConfiguration fcnf = null;
  private File mobTypeFile = null;
  
  public MobTypes() {
    mobTypeFile = new File("plugins" + File.separator + "Catacombs" + File.separator + "monsters.yml");
    fcnf = YamlConfiguration.loadConfiguration(mobTypeFile);

    try {
      if (mobTypeFile.exists()) {
        fcnf.load(mobTypeFile);
      } else {
        System.out.println("[Catacombs] monsters file doesn't exist");
        if(!fcnf.contains("monster.Zombie")) {
          fcnf.set("monster.Zombie.shape","zombie");
          fcnf.set("monster.Zombie.hps",22);
          fcnf.set("monster.Zombie.gold","1-6");
          fcnf.set("monster.Zombie.abilities",new LinkedList<String>(Arrays.asList("DizzyingAura")));
        }
        if(!fcnf.contains("monster.FireLich")) {
          fcnf.set("monster.FireLich.shape","skeleton");
          fcnf.set("monster.FireLich.hps",80);
          fcnf.set("monster.FireLich.gold","1-10");
          fcnf.set("monster.FireLich.abilities",new LinkedList<String>(Arrays.asList("FireBurn","DizzyingAura")));
          fcnf.set("monster.FireLich.loot",new LinkedList<String>(Arrays.asList("BoneyStuff","BossLoot1")));
        }
        if(!fcnf.contains("ability.DizzyingAura")) {
          fcnf.set("ability.DizzyingAura.effect","SPIN");
          fcnf.set("ability.DizzyingAura.target","ALL_NEAR_ENT");
          fcnf.set("ability.DizzyingAura.after",5);
        }
        if(!fcnf.contains("ability.FireBurn")) {
          fcnf.set("ability.FireBurn.effect","FIRE_DAMAGE");
          fcnf.set("ability.FireBurn.target","ONE_NEAR_ENT");
          fcnf.set("ability.FireBurn.after",5);
        }
        if(!fcnf.contains("loot")) {
          fcnf.set("loot.BoneyStuff",new LinkedList<String>(Arrays.asList("bone:100:1-5")));
          fcnf.set("loot.BossLoot1",new LinkedList<String>(Arrays.asList("diamond:20:1","golden_apple:10:1")));
        }
        fcnf.save(mobTypeFile);
      }
      if (fcnf.contains("ability")) {
        for (String ability : CatUtils.getKeys(fcnf, "ability")) {
          System.out.println("[Catacombs]   ability=" + ability);
          CatAbility ab = new CatAbility(fcnf, ability, "ability." + ability);
          abilities.put(ability, ab);
        }
      }
      if (fcnf.contains("loot")) {
        for (String group : CatUtils.getKeys(fcnf, "loot")) {
          System.out.println("[Catacombs]   loot=loot." + group);
          CatLootList ll = new CatLootList(fcnf, group, "loot." + group);
          loot.put(group, ll);
        }
      }
      if (fcnf.contains("monster")) {
        for (String mob : CatUtils.getKeys(fcnf, "monster")) {
          String path = "monster." + mob;
          String shape = fcnf.getString(path + ".shape");
          int hps = fcnf.getInt(path + ".hps");
          String gold = fcnf.getString(path + ".gold");
          List<CatAbility> ability_list = new LinkedList<CatAbility>();
          for (String abil : fcnf.getStringList(path + ".abilities")) {
            if (abilities.containsKey(abil)) {
              ability_list.add(abilities.get(abil));
            } else {
              System.out.println("[Catacombs] Abilitiy '" + abil + "' required by '" + mob + "' is not defined");
            }
          }

          List<CatLootList> loot_list = new LinkedList<CatLootList>();
          for (String l : fcnf.getStringList(path + ".loot")) {
            if (loot.containsKey(l)) {
              loot_list.add(loot.get(l));
            } else {
              System.out.println("[Catacombs] Loot '" + l + "' required by '" + mob + "' is not defined");
            }
          }
          MobType mt = new MobType(mob, shape, hps, gold, ability_list, loot_list);
          mobs.put(mob, mt);
          System.out.println("[Catacombs] mob=" + mt);
        }
      }
    } catch (Exception e) {
      System.err.println("[Catacombs] "+e.getMessage());
    }
  }
  
  public MobType get(String mob) {
    if(mobs.containsKey(mob)) {
      return mobs.get(mob);
    }
    System.out.println("[Catacombs] No monster called '"+mob+"' is defined in 'monsters.yml'");
    return null;
  }
}
