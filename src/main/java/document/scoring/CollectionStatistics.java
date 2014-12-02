package document.scoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import document.DocInfo;

/**
 * 
 * @author Di
 * This class is vital for document scoring.
 */
public class CollectionStatistics {
  /**
   * It sores the collection-wise term frequency.
   * This collapses field-level statistics
   */
  public Map<String, Integer> collectionTermFreqMap;
  
  /**
   * This stores the internal ID's of existing terms in the collection.
   * Useful when generating term vectors
   */
  public Map<String, Integer> termID_Map;
  
  /**
   * Maintain a roster of documents in the collection
   */
  public ArrayList<DocInfo> docList;
  
  /**
   * Tracks the number of documents in the collection
   */
  public int numDocs;
  
  /**
   * Collection size = number of tokens
   */
  public int size;
  
  /**
   * Collection vocabulary size = number of token-types
   */
  public int V;
  
  /**
   * Inverted list that gets all documents that contain a particular term
   */
  public Map<String, Set<DocInfo>> invList;
  
  /**
   * Trivial instantiation, but needs to add documents to populate
   */
  public CollectionStatistics() {
    super();
    collectionTermFreqMap = new HashMap<String, Integer>();
    termID_Map  = new HashMap<String, Integer>();
    docList = new ArrayList<DocInfo>();
    size = 0;
    numDocs = 0;
    V = 0;
  }
  
  /**
   * 
   * @param d new document to be added
   * 
   * Every time a document is added, the collection statistics updates itself
   */
  public void addDoc(DocInfo d) {
    docList.add(d);
    numDocs = docList.size();
    size += d.length;
    for(Map<String, Integer> fieldTFVec: d.fieldTFMap.values()) {
      update_collectionTermFreqMap(fieldTFVec);
    }
    
    V = collectionTermFreqMap.size();
  }
  
  public void update_collectionTermFreqMap(Map<String, Integer> docTermFreqVec) {
    for(Entry<String, Integer> e: docTermFreqVec.entrySet()) {
      
      
      if(!collectionTermFreqMap.containsKey(e.getKey())) {
        collectionTermFreqMap.put(e.getKey(), 0);
      }
      collectionTermFreqMap.put(e.getKey(), collectionTermFreqMap.get(e.getKey())+e.getValue());
      
    }
  }
  
  /**
   * This is called when there are no more documents to be added.
   * This can actually be called up every update, but not necessarily.
   */
  public void finalize() {
    
    // Set up termID_Map
    int idx = 0;
    for(String term: collectionTermFreqMap.keySet()) {
      termID_Map.put(term, idx);
      idx++;
    }
    
    invList = new HashMap<String, Set<DocInfo>>();
    
    // We initiliaze here because there are some OOV terms from queries
    // For those cases, invlist should simply return an empty list
    for(String term: collectionTermFreqMap.keySet()) {
      if(!invList.containsKey(term)) {
        invList.put(term, new HashSet<DocInfo>());
      }
    }
    
    for(DocInfo d: docList) {
      for(Map<String, Integer> fieldTfVec: d.fieldTFMap.values()) {
        for(String term: fieldTfVec.keySet()) {
          invList.get(term).add(d);
        }
      }
      
    }
    
    /*System.out.println(V);
    System.out.println(size);
    System.out.println(numDocs);*/
    
  }
  
 /**
  * 
  * @param termFreqVec
  * @return a vector representation of the term frequency vector, the index corresponds to termID's
  */
 public double[] getVec(Map<String, Integer> termFreqVec) {

   double[] ret = new double[collectionTermFreqMap.size()];
   for(int i=0; i<ret.length; i++) {
     ret[i] = 0d;
   }

   for(Entry<String, Integer> e: termFreqVec.entrySet()) {
     ret[termID_Map.get(e.getKey())] = e.getValue();;
   }
   return ret;
 }
  
  
}
