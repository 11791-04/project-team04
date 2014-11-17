package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.lucene.analysis.en.EnglishMinimalStemmer;
import org.tartarus.snowball.ext.PorterStemmer;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetBeginAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetEndAnnotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

/**
 * A class with a private constructor and only static public functions that stores the different
 * components
 * 
 * @author josephcc
 * 
 */
public class TextUtils {
  private TextUtils() {
  }

  /**
   * Reference to the Properties parameter for StanfordCoreNlp
   */
  private static Properties props;

  /**
   * Reference to the StanfordCoreNLP pipeline
   */
  private static StanfordCoreNLP pipeline;

  /**
   * Reference to the EnglishMinimalStemmer from lucene
   */
  private static EnglishMinimalStemmer stemmer;
  
  /**
   * static initialization blocks to load all the models from external libraries
   */
  static {
    props = new Properties();
    props.put("annotators", "tokenize, ssplit");
    pipeline = new StanfordCoreNLP(props);
    stemmer = new EnglishMinimalStemmer();
  }

  public static List<String> spaceTokenizer(String doc) {
    List<String> res = new ArrayList<String>();

    for (String s : doc.split("\\s+")) {
      res.add(s);
    }
    return res;
  }

  public static String minimalStem(String word) {
    char[] _word = word.toCharArray();
    int _length = stemmer.stem(_word, word.length());
    return new String(_word, 0, _length);
  }

  public static String porterStem(String word) {

    PorterStemmer obj = new PorterStemmer();
    obj.setCurrent(word);
    obj.stem();
    String out = obj.getCurrent();
    return out;
  }

  public static List<String> stanfordTokenizer(String doc) {
    List<String> res = new ArrayList<String>();

    edu.stanford.nlp.pipeline.Annotation document = new edu.stanford.nlp.pipeline.Annotation(doc);
    TextUtils.pipeline.annotate(document);

    for (CoreLabel token : document.get(TokensAnnotation.class)) {
      String word = token.get(TextAnnotation.class).toLowerCase();
      res.add(word);
    }
    return res;
  }
  
  public static List<String> stanfordSentenceTokenizer(String doc) {
    List<String> res = new ArrayList<String>();

    edu.stanford.nlp.pipeline.Annotation document = new edu.stanford.nlp.pipeline.Annotation(doc);
    TextUtils.pipeline.annotate(document);

    System.out.println(document);
    for (CoreMap sent : document.get(SentencesAnnotation.class)) {
      String words = sent.get(TextAnnotation.class);
      System.out.println(sent);
      int begin = sent.get(CharacterOffsetBeginAnnotation.class);
      int end = sent.get(CharacterOffsetEndAnnotation.class);
      System.out.println("SENT: [" + begin + "-" + end + "] " + words);
      res.add(words);
    }
    return res;
  }

  /**
   * Mutable version of the Integer class for fast counting
   * @author josephcc
   *
   */
  public static class MutableInteger {

    /**
     * the actual value
     */
    private int val;

    /**
     * initializer
     * @param val
     */
    public MutableInteger(int val) {
      this.val = val;
    }

    /**
     * Retrieve the value
     * @return the integer
     */
    public int get() {
      return val;
    }

    /**
     * Set the value
     * @param val
     */
    public void set(int val) {
      this.val = val;
    }

    /**
     * pretty print
     */
    public String toString() {
      return Integer.toString(val);
    }
  }
}
