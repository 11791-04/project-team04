package listquestion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import util.text.lm.Ngram;
import util.text.ner.BioNER;

public class ListNER_Analyser {

  /**
   * The smoothing constant when scoring Bio Entities
   */
  public static final double smoothing = 0.001;
  
  /**
   * A set telling us which terms are in the query that are supposed to be ignored
   */
  Set<String> queryTermSet;
  
  /**
   * A String[] representation of the query
   */
  String[] query;
  /**
   * It maps a document ID to its abstract text in String[]
   */
  Map<String, String[]> absMap; //<docID, Abstract in String[]>
  
  /**
   * It counts the term frequency of a term across all documents
   */
  Map<String, Integer> termDFMap; //<term, df>
  
  /**
   * Maps a term to its locations (a list) in a document.
   */
  Map<String, Map<String, List<Integer>>> termDocLocMap;  //<docID, <term, [list of locations]>>
  
  
  /**
   * It will process the query, assuming it is just a bag of pivot terms
   * @param queryStr
   * @param absMap
   */
  public ListNER_Analyser(String queryStr, Map<String, String[]> absMap) {
    super();
    this.query = queryStr.trim().split("[ ]+");
    queryTermSet = new HashSet<String>(Arrays.asList(this.query));
    
    
    this.absMap = absMap;
    termDFMap = new HashMap<String, Integer>();
    termDocLocMap = new HashMap<String, Map<String, List<Integer>>>();
    
    for(Entry<String, String[]> e: absMap.entrySet()) {
      String docID = e.getKey();
      if(!termDocLocMap.containsKey(docID)) {termDocLocMap.put(docID, new HashMap<String, List<Integer>>());}
      
      String[] termArray = e.getValue();
      Set<String> termSet = new HashSet<String>();
      for(int i=0; i<termArray.length; i++) {
        termSet.add(termArray[i]);
        if(!termDocLocMap.containsKey(termArray[i])) {termDocLocMap.get(docID).put(termArray[i], new ArrayList<Integer>());}
        termDocLocMap.get(docID).get(termArray[i]).add(i);
      }
      for(String t: termSet) {
        if(!termDFMap.containsKey(t)) {termDFMap.put(t, 0);}
        termDFMap.put(t, termDFMap.get(t)+1);
      }
    }
    
  }
  
  /**
   * This returns a sorted list of Bio tags
   * @param W the window size
   * @return
   */
  public List<Entry<String, Double>> getNEList(int W){
    
    Map<String, Double> scoreBoard = new HashMap<String, Double>();
    
    double score = 1d;
    for(String docID: absMap.keySet()) {
      Map<String, Integer> docNEFreqMap= getNearbyNEs(docID, W);
      // Also a chance to combine bigrams back from unigrams
      
      for(String term: docNEFreqMap.keySet()) {
        double docwiseScore = docNEFreqMap.get(term);
        if(docNEFreqMap.get(term)==0) {docwiseScore = smoothing;}
        if(!scoreBoard.containsKey(term)) {scoreBoard.put(term, 1d);}
        scoreBoard.put(term, scoreBoard.get(term)*docwiseScore);
      }
    }
    List<Entry<String, Double>> sorted = scoreBoard.entrySet().stream().sorted(
            (e1, e2) -> -Double.compare(e1.getValue(), e2.getValue())
            ).collect(Collectors.toList());
    
    
    return sorted;
  }
  
  /**
   * Get the nearby entities in a particular document according to the query
   * @param docID
   * @param W window size
   * @return
   */
  public Map<String, Integer> getNearbyNEs(String docID, int W) {
    // The freq in the window
    Map<String, Integer> nerFreqMap = new HashMap<String, Integer>();
    
    for(String term: query) {
      Set<String> ners = getNearbyNEs(docID, term, W);
      ners.remove(term);
      
      for(String ne: ners) {
        if(!nerFreqMap.containsKey(ne)) {nerFreqMap.put(ne, 0);}
        nerFreqMap.put(ne, nerFreqMap.get(ne)+1);
      }
      
    }
    
    Set<String> toRemove = new HashSet<String>();
    for(String term: nerFreqMap.keySet()) {
      if(Ngram.getUnigram(term.toLowerCase()) > Ngram.ListNECutoff
              || queryTermSet.contains(term)) {
        toRemove.add(term);
      }
    }
    for(String term: toRemove) {nerFreqMap.remove(term);}
    
    return nerFreqMap;
  }
  
  /**
   * Get the nearby entities in a particular document for a particular term 
   * according to the query
   * @param docID
   * @param term
   * @param W window size
   * @return
   */
  public Set<String> getNearbyNEs(String docID, String term, int W) {
   //System.out.printf(">> getNearbyNEs: %s, %s\n", docID, term);

    if(!termDocLocMap.get(docID).containsKey(term)){return new HashSet<String>();}
    
    String[] abstractArr = absMap.get(docID);
    
    Set<String> ners = new HashSet<String>();
    for(int pivot: termDocLocMap.get(docID).get(term)) {
      StringBuilder sb = new StringBuilder();
      for(int i=Math.max(0, pivot-W/2); i<Math.min(pivot+W/2, abstractArr.length-1); i++) {
        sb.append(abstractArr[i]+" ");
      }
      String windowStr = sb.toString().trim();
      ners.addAll(BioNER.getUnigramBioTags(windowStr));
    }

    return ners;
  }
  
  
  
  
  
}
