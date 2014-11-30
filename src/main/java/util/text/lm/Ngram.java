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
  
  public static double getUnigram(String term) {
    if(unigramModel.containsKey(term)) {
      return unigramModel.get(term);
    }else {
      return 0d;
    }
  }
}
