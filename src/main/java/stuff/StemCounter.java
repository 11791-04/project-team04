package stuff;

import java.util.LinkedList;
import java.util.List;

/*
 * Extension of the FrequencyCounter class which does preprocessing on the data
 * before adding it to the map using the Stanford Lemmatizer stemWord() function
 * which stems the word and returns it in lowercase form. 
 */
public class StemCounter extends FrequencyCounter {
  
  private static final long serialVersionUID = -522079579866695088L;

  @Override
  public void tokenizeAndPutAll(String doc, String delimiter) {
    if (delimiter.equals(" ") || delimiter == null)
      delimiter = "\\s+";
    List<String> items = new LinkedList<String>();
    for (String str : doc.split(delimiter)) {
      // Lemmatize the words here
      str = StanfordLemmatizer.stemWord(str);
      items.add(str);
    }
    this.putAll(items);
  }

}
