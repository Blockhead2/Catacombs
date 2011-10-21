package Test;

//import org.bukkit.plugin.Plugin;

/**
 *
 * @author John Keay
 */
public interface CashIF {
  //static CashIF create(Plugin p);
  String getBalance(String name);
  void   addMajor(String name, int amount);
  void   addMinor(String name, int amount);
}
