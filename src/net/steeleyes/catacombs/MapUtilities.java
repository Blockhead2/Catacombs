package net.steeleyes.catacombs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MapUtilities {

  public static <K, V extends Comparable<V>> List<Entry<K, V>> sortByValue(Map<K, V> map) {
    List<Entry<K, V>> entries = new ArrayList<Entry<K, V>>(map.entrySet());
    Collections.sort(entries, new ByValue<K, V>());
    return entries;
  }

  private static class ByValue<K, V extends Comparable<V>> implements Comparator<Entry<K, V>> {
    public int compare(Entry<K, V> o1, Entry<K, V> o2) {
      return o1.getValue().compareTo(o2.getValue());
    }
  }
  
  /*
  public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
    List<T> list = new ArrayList<T>(c);
    java.util.Collections.sort(list);
    return list;
  }

  public static <K, V extends Comparable<V>> Map<K, V> sortByValues(final Map<K, V> map) {
    Comparator<K> valueComparator = new Comparator<K>() {

      public int compare(K k1, K k2) {
        int compare = map.get(k2).compareTo(map.get(k1));
        if (compare == 0) {
          return 1;
        } else {
          return compare;
        }
      }
    };
    Map<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
    sortedByValues.putAll(map);
    return sortedByValues;
  }
  */
}
