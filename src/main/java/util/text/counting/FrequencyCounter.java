package util.text.counting;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * This is a decorator of the typical HashMap to specifically handle cases where we are trying to
 * count the keys we insert. Modeled after the type of FrequencyCounter class we have in python. This
 * is useful for bag-of-words representations of text documents
 * 
 * @author nwolfe
 *  
 */
public class FrequencyCounter extends HashMap<String, Integer> {
  private static final long serialVersionUID = -4771350887733387006L;
  
  /*
   * Wraps the typical put method and does nothing with the integer.
   * We handle the counts internally. 
   * 
   * (non-Javadoc)
   * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
   */
  @Override
  public Integer put(String key, Integer value) {
    if (this.containsKey(key)) {
      Integer update = this.get(key) + 1;
      return super.put(key, update);
    } else {
      return super.put(key, new Integer(1));
    }
  }
  
  /*
   * Add a collection of Strings to this map
   */
  public void putAll(Iterable<String> document) {
    for(String word : document)
      this.put(word, 0);
  }
  
  /**
   * clear the contents of the map
   */
  public void empty() {
    super.clear();
  }
  
  /**
   * Splits a document based on a String delimiter and adds them to the map
   * 
   * @param doc
   * @param delimiter
   */
  public void tokenizeAndPutAll(String doc, String delimiter) {
    if(delimiter.equals(" ") || delimiter == null) 
      delimiter = "\\s+";
    List<String> items = new LinkedList<String>();
    for (String s : doc.split(delimiter))
      items.add(s);
    this.putAll(items);
  }
}