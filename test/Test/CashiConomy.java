package Test;

import com.iConomy.iConomy;
import com.iConomy.system.Account;
import com.iConomy.system.Holdings;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author John Keay
 */
public class CashiConomy implements CashIF {
  public iConomy iConomy = null;

  public static CashIF create(Plugin p) {
    CashiConomy o = new CashiConomy();
    o.iConomy = (iConomy) p;
    return (CashIF) o;
  }
    
  public String getBalance(String name) {
    if(iConomy != null) {
      return iConomy.format(name);
    }
    return "0";
  }
  
  public void addMajor(String name, int amount) {
    if(iConomy!=null) {
      Account acc = iConomy.getAccount(name);
      if(acc != null) {
        Holdings balance = acc.getHoldings();
        balance.add(amount);
      }
    } 
  }
  
  public void addMinor(String name, int amount) {
  
  } 
  
}
