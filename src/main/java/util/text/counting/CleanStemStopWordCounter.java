package util.text.counting;

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
 * all whitespace characters and punctuation marks using a Regex expression, then 
 * removing all stop words and then before adding it to the map using the Stanford 
 * Lemmatizer stemWord() function which stems the word and returns it in lowercase form. 
 */
public class CleanStemStopWordCounter extends FrequencyCounter {

  private static final long serialVersionUID = 341926969087233677L;

  private final Pattern punc = Pattern.compile("\\p{Punct}+");

  private final Pattern wsp = Pattern.compile("\\p{Space}+");

  private final HashMap<String, String> stopwords;
  
  private final Integer minLength = 2;

  public CleanStemStopWordCounter() {
    super();
    stopwords = new HashMap<String, String>();
    try {
      Scanner scn = new Scanner(new File("src/main/resources/stoppers"));
      while (scn.hasNextLine()) {
        String stopword = scn.nextLine();
        stopwords.put(stopword, stopword);
      }
      scn.close();
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
      // Lemmatize the words here
      if (!stopwords.containsKey(str)) {
        str = StanfordLemmatizer.stemWord(str);
        if(str.length() > minLength) 
          items.add(str);
      }
    }
    this.putAll(items);
  }

}
