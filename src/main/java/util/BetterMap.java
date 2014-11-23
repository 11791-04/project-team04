package util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is a template map which is useful for situations in which
 * you have a label pointing to an ArrayList which may grow in size over time
 * and you don't want to worry about overwriting the keys in the map. 
 * 
 * @author nwolfe
 *
 * @param <K> the Object type of the Label
 * @param <E> the parameter of the ArrayList pointed to by the Label
 */
public class BetterMap<K, E> extends HashMap<K, ArrayList<E>> {
  private static final long serialVersionUID = 3663198534069038400L;

  public void addItem(K k, E q) {
    if (super.containsKey(k)) {
      super.get(k).add(q);
    } else {
      ArrayList<E> arr = new ArrayList<E>();
      if(q != null) 
        arr.add(q);
      super.put(k, arr);
    }
  }
}
