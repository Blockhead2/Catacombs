package Test;

import net.steeleyes.catacombs.*;
import java.util.Random;
/**
 *
 * @author User
 */
public class ProtTest {
  public final static int num_cubes = 1000;
  public final static int num_tests = 1;

  public static Random rnd = new Random();
  //public static MultiWorldProtect prot = new MultiWorldProtect(null);

  public static void main(String[] args) {
    setup();
    test();
  }
  public static void setup() {
    for(int i=0;i<num_cubes;i++) {
      int x = rnd.nextInt(1000)-500;
      int y = rnd.nextInt(1000)-500;
 //     prot.add("world",new Cuboid(x,10,y,x+40,19,y+40));
 //     prot.add("world",new Cuboid(x,20,y,x+40,29,y+40));
  //    prot.add("world",new Cuboid(x,30,y,x+40,39,y+40));
  //    prot.add("world",new Cuboid(x,40,y,x+40,49,y+40));
  //    prot.add("world",new Cuboid(x,50,y,x+40,59,y+40));
    }
  }

  public static void test() {
    int protect = 0;
    for(int i=0;i<num_tests;i++) {
      int x = rnd.nextInt(1000)-500;
      int y = rnd.nextInt(128);
      int z = rnd.nextInt(1000)-500;
    //  if(prot.isProtected("world", x, y, z))
    //    protect++;
    }
    System.out.println("Protected ="+protect);
  }
}
