package Test;

import org.bukkit.plugin.Plugin;
import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.api.UserDoesNotExistException;
import com.earth2me.essentials.api.NoLoanPermittedException;

/**
 *
 * @author John Keay
 */
public class CashEssentialsEco implements CashIF {
  private Essentials plug = null;

  public static CashIF create(Plugin p) {
    CashEssentialsEco o = new CashEssentialsEco();
    o.plug = (Essentials) p;
    return (CashIF) o;
  }
    
  public String getBalance(String name) {
    if(plug != null) {
      double num = 0.0;
      try {
        num = Economy.getMoney(name);
      } catch(UserDoesNotExistException ex) {
        System.out.println("[Catacombs] User does not exist in Essentials Economy: " + ex.getMessage());  
      }
      return Economy.format(num);
    }
    return "0";
  }
  
  public void addMajor(String name, int amount) {
    if(plug!=null) {
      try {
        Economy.add(name, amount);
      } catch (UserDoesNotExistException e) {
        System.out.println("[Catacombs] User does not exist in Essentials Economy: "+e.getMessage());
      } catch (NoLoanPermittedException e) {
        System.out.println("[Catacombs] No loan permitted in Essentials Economy: "+e.getMessage());
      }
    } 
  }
  
  public void addMinor(String name, int amount) {
  
  }   
}
