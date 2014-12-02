package document;

import java.util.Map;

import util.text.TextProcessingTools;
import document.stemmer.KrovetzStemmer;

/**
 * 
 * @author Di
 * 
 * Stores basic information about the query
 */
public class QueryInfo {

  public Map<String, Integer> termFreqVec;
  
  public int length;
  public String text;

  /**
   * 
   * @param queryText raw text
   * @param stemmer specified by the user, can be null
   */
  public QueryInfo(String queryText, KrovetzStemmer stemmer) {
    
    this.text = queryText;
    
    String rawQuery= String.copyValueOf(queryText.toCharArray());
    String[] titleTermArray = TextProcessingTools.getFormattedTermArray(rawQuery, stemmer);
    this.length = titleTermArray.length;
    this.termFreqVec = TextProcessingTools.getTFMap(titleTermArray);
  }
  
}
