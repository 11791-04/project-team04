package util.text.counting;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Extension of the FrequencyCounter class which does preprocessing on the data by removing
 * all whitespace characters and punctuation marks using a Regex expression
 * before adding it to the map using the Stanford Lemmatizer stemWord() function
 * which stems the word and returns it in lowercase form. 
 */
public class CleanStemCounter extends FrequencyCounter {

  private static final long serialVersionUID = -522079579866695088L;

  private final Pattern punc = Pattern.compile("\\p{Punct}+");

  private final Pattern wsp = Pattern.compile("\\p{Space}+");

  @Override
  public void tokenizeAndPutAll(String doc, String delimiter) {
    if (delimiter.equals(" ") || delimiter == null)
      delimiter = "\\s+";
    List<String> items = new LinkedList<String>();
    // Strip punctuation
    Matcher m = punc.matcher(doc);
    doc = m.replaceAll(" ");
    m = wsp.matcher(doc);
    doc = m.replaceAll(" ");
    for (String str : doc.split(delimiter)) {
      // Lemmatize the words here
      str = StanfordLemmatizer.stemWord(str);
      items.add(str);
    }
    this.putAll(items);
  }

}
