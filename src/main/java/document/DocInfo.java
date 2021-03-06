package document;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import util.text.TextProcessingTools;
import document.stemmer.KrovetzStemmer;

/**
 * 
 * @author Di
 * This class store the document information from its UIMA type.
 */
public class DocInfo {

  
  public String pmid;
  public String uri;

  public Map<String, String> fieldTextMap;
  
  public String year;

  /**
   * Overall length. Collapse field-level lengths
   */
  public Integer length; 
  
  /**
   * Map<field name, <term string, tf>>
   */
  public Map<String, Map<String, Integer>> fieldTFMap;
  public Map<String, Integer> fieldLenMap;

  
  /**
   * 
   * @param uri from PubMed
   * @param pmid from PubMed
   * @param fieldTextMap The actual content of the document in raw texts
   * @param year from PubMed
   * @param stemmer user specified
   * 
   * Populates all fields.
   */
  public DocInfo(String uri, String pmid, Map<String, String> fieldTextMap, String year, KrovetzStemmer stemmer) {
    super();
    this.pmid = pmid;
    this.uri = uri;
    this.fieldTextMap = fieldTextMap;
    this.year = year;

    fieldTFMap = new HashMap<String, Map<String, Integer>>();
    fieldLenMap = new HashMap<String, Integer>();
    
    for(Entry<String, String> e: fieldTextMap.entrySet()) {
      String fieldName = e.getKey();
      String fieldRawText = e.getValue();
      
      String rawFiledText= String.copyValueOf(fieldRawText.toCharArray());
      String[] fieldTermArray = TextProcessingTools.getFormattedTermArray(rawFiledText, stemmer);
      fieldLenMap.put(fieldName, fieldTermArray.length);
      fieldTFMap.put(fieldName, TextProcessingTools.getTFMap(fieldTermArray));
    }
    
    length = 0;
    for(Integer l: fieldLenMap.values()) {
      length += l;
    }
  }
  

}
