package docretrieval;

import java.util.Map;

import docretrieval.stemmer.KrovetzStemmer;

public class QueryInfo {

  public Map<String, Integer> termFreqVec;
  
  public int length;
  public String text;
  
  public QueryInfo(String queryText, KrovetzStemmer stemmer) {
    
    this.text = queryText;
    
    String rawQuery= String.copyValueOf(queryText.toCharArray());
    String[] titleTermArray = TextProcessingTools.getFormattedTermArray(rawQuery, stemmer);
    this.length = titleTermArray.length;
    this.termFreqVec = TextProcessingTools.getTFMap(titleTermArray);
  }
  
}
