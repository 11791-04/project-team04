package document.scoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import document.DocInfo;

public class CollectionStatistics {
  
  public Map<String, Integer> collectionTermFreqMap;  // This collapses field-level statistics
  public Map<String, Integer> termID_Map;
  public ArrayList<DocInfo> docList;
  public int numDocs;
  public int size;
  public int V;
  
  public Map<String, Set<DocInfo>> invList;
  
  public CollectionStatistics() {
    super();
    collectionTermFreqMap = new HashMap<String, Integer>();
    termID_Map  = new HashMap<String, Integer>();
    docList = new ArrayList<DocInfo>();
    size = 0;
    numDocs = 0;
    V = 0;
  }
  
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
