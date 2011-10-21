package Test;

import java.util.HashMap;
//import org.bukkit.plugin.Plugin;

/**
 *
 * @author John Keay
 */
public class CashDefault implements CashIF {
  private final HashMap<String,Balance> accounts = new HashMap<String,Balance>();

  private class Balance {
    public int major = 0;
    public int minor = 0;
  }
  
  public static CashIF create() {
    CashDefault o = new CashDefault();
    return (CashIF) o;
  }
  
  public String getBalance(String name) {
    if(accounts.containsKey(name)) {
      Balance bal = accounts.get(name);
      if(bal.minor>0)
        return bal.major+" gold, "+bal.minor+" silver";
      return bal.major+" gold";
    }
    return "0 gold";
  }
  
  public void addMajor(String name, int amount) {
    if(accounts.containsKey(name)) {
      Balance bal = accounts.get(name);
      bal.major += amount;
    } else {
      Balance n = new Balance();
      n.major = amount;
      accounts.put(name, n);
    }
  }
  
  public void addMinor(String name, int amount) {
    if(accounts.containsKey(name)) {
      Balance bal = accounts.get(name);
      bal.major += amount;
    } else {
      Balance n = new Balance();
      n.major = amount;
      accounts.put(name, n);
    }    
  } 
  
}
