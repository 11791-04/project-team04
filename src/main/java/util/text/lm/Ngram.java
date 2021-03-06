package util.text.lm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.aliasi.dict.DictionaryEntry;
import com.aliasi.dict.ExactDictionaryChunker;
import com.aliasi.dict.MapDictionary;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;

public class Ngram {

  /**
   * The cutoff for query expansion
   */
  public static final double QECutoff = 1E-4;
  
  /**
   * The cutoff for List Entity
   */
  public static final double ListNECutoff = 1E-6;
  
  /**
   *  The unigram model will be read from a file
   */
  public static Map<String, Double> unigramModel = new HashMap<String, Double>();
  static {

    InputStream is = ExactDictionaryChunker.class.getResourceAsStream("/models/google_1gram");
    String ln;

    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
      while ((ln = br.readLine()) != null) {
        //System.out.println(ln);
        String[] lineSplit = ln.split("\t");
        unigramModel.put(lineSplit[0], Double.parseDouble(lineSplit[1]));
      }
      br.close();
      is.close();
    } catch (Exception e1) {
      e1.printStackTrace();
      System.err.println("Failed to generate unigram");
    }

  }
  
  /**
   * 
   * @param term
   * @return the uniram probability of the term
   */
  public static double getUnigram(String term) {
    if(unigramModel.containsKey(term)) {
      return unigramModel.get(term);
    }else {
      return 0d;
    }
  }
}
