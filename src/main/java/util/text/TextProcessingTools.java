package util.text;

import java.util.HashMap;
import java.util.Map;

import document.stemmer.KrovetzStemmer;


/**
 * 
 * @author Di Xu
 *
 */

public class TextProcessingTools {

  
  public static String[] getFormattedTermArray(String rawText, KrovetzStemmer stemmer) {
    rawText = rawText.replaceAll("[.,;!-]", " ");
    rawText = rawText.toLowerCase();
    
    String[] textSplit = rawText.split("\\s+");
    
    for(int i=0; i<textSplit.length; i++) {
      textSplit[i] = stemmer.stem(textSplit[i]);
    }
    
    return textSplit;
  }
  
  public static Map<String, Integer> getTFMap(String[] termArray){
    Map<String, Integer> tfMap = new HashMap<String, Integer>();
    for(String t: termArray) {
      if(!tfMap.containsKey(t)) {tfMap.put(t, 0);}
      tfMap.put(t, tfMap.get(t)+1);
    }
    return tfMap;
  }
  
}
