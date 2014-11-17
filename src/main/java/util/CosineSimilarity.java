package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CosineSimilarity {
  
  /**
   * Returns the best document among the given documents based on Cosine similarity values.
   * @param queryVector
   * @param docVectors
   * @return
   */
  public static String computeCosineSimilarity(String queryVector, ArrayList<String> docVectors) {
    double max = 0.0f;
    String bestDocument = "";
    HashMap<String, Integer> queryMap = getWordMap(queryVector);
    for(String vector : docVectors) {
      double cosValue = computeCosineSimilarityValue(queryMap, getWordMap(vector));
      if(cosValue > max) {
        max = cosValue;
        bestDocument = vector;
      }
    }
    return bestDocument;
  }

  /**
   * Computes cosine similarity for two documents
   * @param queryVector
   * @param docVectors
   * @return
   */
  public static double computeCosineSimilarityForDocuments(ArrayList<String> queryVector, ArrayList<String> docVector) {
    return computeCosineSimilarityValue(getWordMap(queryVector), getWordMap(docVector));
  }
  
  /**
   * Creates a vector based on word count
   * @param vector
   * @return
   */
  private static HashMap<String, Integer> getWordMap(String vector) {
    HashMap<String, Integer> map = new HashMap<String, Integer>();
    String[] words = vector.split("\\s");
    for(String s: words) {
      if(map.containsKey(s))
        map.put(s, map.get(s) + 1);
      else
        map.put(s, 1);
    }
    return map;
  }
  
  /**
   * Creates a vector based on word count
   * @param vector
   * @return
   */
  private static HashMap<String, Integer> getWordMap(ArrayList<String> vector) {
    HashMap<String, Integer> map = new HashMap<String, Integer>();
    for(String s: vector) {
      if(map.containsKey(s))
        map.put(s, map.get(s) + 1);
      else
        map.put(s, 1);
    }
    return map;
  }  
  
  
  /**
   * Computes cosine similarity for two vectors
   * @return cosine_similarity
   */
  private static double computeCosineSimilarityValue(Map<String, Integer> queryVector,
          Map<String, Integer> docVector) {
    double cosine_similarity = 0.0;
    double dot_product = 0.0;
    double queryVectorVal = 0.0;
    double docVectorVal = 0.0;
    for (String key : queryVector.keySet()) {
      queryVectorVal += (queryVector.get(key) * queryVector.get(key));
      if (docVector.containsKey(key))
        dot_product += queryVector.get(key) * docVector.get(key);
    }
    for (String key : docVector.keySet()) {
      docVectorVal += (docVector.get(key) * docVector.get(key));
    }
    cosine_similarity = dot_product / (Math.sqrt(queryVectorVal) * Math.sqrt(docVectorVal));
    return cosine_similarity;
  }
  
  
}
