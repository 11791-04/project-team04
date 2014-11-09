package docretrieval;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import docretrieval.stemmer.KrovetzStemmer;


public class DocInfo {

  
  public String pmid;

  public Map<String, String> fieldTextMap;
  
  public String year;

  public Integer length; // Overall length. Collapse field-level lengths
  
  /**
   * Map<field name, <term string, tf>>
   */
  public Map<String, Map<String, Integer>> fieldTFMap;
  public Map<String, Integer> fieldLenMap;

  
  public DocInfo(String pmid, Map<String, String> fieldTextMap, String year, KrovetzStemmer stemmer) {
    super();
    this.pmid = pmid;
    this.fieldTextMap = fieldTextMap;
    this.year = year;

    fieldTFMap = new HashMap<String, Map<String, Integer>>();
    fieldLenMap = new HashMap<String, Integer>();
    
    for(Entry<String, String> e: fieldTextMap.entrySet()) {
      String fieldName = e.getKey();
      String fieldRawText = e.getValue();
      
      String rawFiledText= String.copyValueOf(fieldRawText.toCharArray());
      String[] titleTermArray = TextProcessingTools.getFormattedTermArray(rawFiledText, stemmer);
      fieldLenMap.put(fieldName, titleTermArray.length);
      fieldTFMap.put(fieldName, TextProcessingTools.getTFMap(titleTermArray));
    }
    
    length = 0;
    for(Integer l: fieldLenMap.values()) {
      length += l;
    }
  }
  

}
