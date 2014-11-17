package stuff;

import java.util.Map;

/*
 * A type of Similarity object which takes a Question and Answer object and returns
 * the cosine similarity between them
 */
public class CosineSimilarityStrategy implements Similarity {

  @Override
  public Double computeSimilarity(Question query, Answer ans) {
    return computeCosineSimilarity(query.getDocTokenFrequencies(), ans.getDocTokenFrequencies());
  }

  /**
   * @return cosine_similarity
   */
  private Double computeCosineSimilarity(Map<String, Integer> queryVector,
          Map<String, Integer> docVector) {
    Double cosine_similarity = 0.0;

    Iterable<Integer> A = queryVector.values();
    Iterable<Integer> B = docVector.values();
    Double normA = calcEuclideanNorm(A);
    Double normB = calcEuclideanNorm(B);
    Double normAB = normA * normB;

    // Scalar product of A / B
    for (String s : queryVector.keySet()) {
      if (docVector.containsKey(s)) {
        Integer a = queryVector.get(s);
        Integer b = docVector.get(s);
        Double axb = (double) (a * b);
        cosine_similarity += axb;
      }
    }
    // cosine_sim / normAB
    cosine_similarity = cosine_similarity / normAB;
    return cosine_similarity;
  }

  /**
   * Caclulate Euclidean norm of a vector E = SUM(v^2) for v in V
   * 
   * @param V
   * @return Double, euclidean norm
   */
  private Double calcEuclideanNorm(Iterable<Integer> V) {
    Double eucNorm = 0.0;
    for (Integer v : V) {
      v = v * v; // v squared
      eucNorm += v;
    }
    return Math.sqrt(eucNorm);
  }

}
