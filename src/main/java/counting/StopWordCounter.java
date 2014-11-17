package counting;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UIMARuntimeException;

/*
 * Extension of the FrequencyCounter class which does preprocessing on the data by removing 
 * all stop words, lowercasing, and then before adding it to the map
 */
public class StopWordCounter extends FrequencyCounter {

  private static final long serialVersionUID = -7268589467673447334L;

  private final Pattern punc = Pattern.compile("\\p{Punct}+");

  private final Pattern wsp = Pattern.compile("\\p{Space}+");

  private final HashMap<String, String> stopwords;

  public StopWordCounter() {
    super();
    stopwords = new HashMap<String, String>();
    try {
      Scanner scn = new Scanner(new File("src/main/resources/stopwords.txt"));
      while (scn.hasNextLine()) {
        String stopword = scn.nextLine();
        stopwords.put(stopword, stopword);
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      throw new UIMARuntimeException();
    }
  }

  @Override
  public void tokenizeAndPutAll(String doc, String delimiter) {
    if (delimiter.equals(" ") || delimiter == null)
      delimiter = "\\s+";
    List<String> items = new LinkedList<String>();
    // Strip punctuation
    doc = doc.toLowerCase();
    Matcher m = punc.matcher(doc);
    doc = m.replaceAll(" ");
    m = wsp.matcher(doc);
    doc = m.replaceAll(" ");
    for (String str : doc.split(delimiter)) {
      if (!stopwords.containsKey(str)) {
        items.add(str);
      }
    }
    this.putAll(items);
  }

}
