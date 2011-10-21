/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.steeleyes.maps;

import java.util.Random;
import org.bukkit.util.config.Configuration;
import java.util.List;

/**
 *
 * @author John Keay
 */
public class LevelBase {
  protected Configuration config;
  protected String style;

  protected final Random rnd = new Random();

  public LevelBase(Configuration config,String style) {
    this.config = config;
    this.style  = style;
  }

  protected Boolean getBoolean(String path, Boolean def) {
    if(config == null)
      return def;
    return config.getBoolean(path, def);
  }

  protected String getString(String path, String def) {
    if(config == null)
      return def;
    return config.getString(path, def);
  }

  protected int getInt(String path, int def) {
    if(config == null)
      return def;    
    return config.getInt(path, def);
  }
  
  protected List<String> getStringList(String path, List<String> def) {
    if(config == null)
      return def;    
    return config.getStringList(path,def);
  }
  
  public Boolean Chance(int i) {
    return rnd.nextInt(100)+1 <= i;
  }

  public int nextInt(int i) {
    return rnd.nextInt(i);
  }
  
  public void save() {
    if(config != null) {
      if(!config.save()) {
        System.err.println("[Catacombs] Problem saving config file");
      } else {
        //System.out.println("[Catacombs] Save config successful");
      }
    }
  }
}
