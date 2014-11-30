package snippets.scoring.strategy;

import java.util.Map;
import java.util.Set;

import snippet.scoring.adapter.CandidateAnswer;
import snippet.scoring.factory.Question;
import snippet.scoring.factory.Similarity;
import edu.stanford.nlp.util.Sets;

/**
 * Similarity implementation based on the Jaccard coefficient
 * 
 * @author nwolfe
 *
 */
public class JaccardSimilarityStrategy implements Similarity {

  @Override
  public Double computeSimilarity(Question query, CandidateAnswer ans) {
    return jaccardCoefficient(query.getDocTokenFrequencies(), ans.getDocTokenFrequencies());
  }

  /**
   * Compute the Jaccard coefficient between a Question and CandidateAnswer
   * 
   * Source:
   * http://web.mit.edu/course/6/6.863/OldFiles/tools/tregex/src/edu/stanford/nlp/stats/Counters
   * .java
   * 
   * @param qv
   * @param dv
   * @return
   */
  private Double jaccardCoefficient(Map<String, Integer> qv, Map<String, Integer> dv) {
    Double count1, count2, minCount = 0.0, maxCount = 0.0;
    Set<String> set = Sets.union(qv.keySet(), dv.keySet());
    for (String key : set) {
      count1 = qv.containsKey(key) ? qv.get(key) : 1.0;
      count2 = dv.containsKey(key) ? dv.get(key) : 1.0;
      minCount += (count1 < count2 ? count1 : count2);
      maxCount += (count1 > count2 ? count1 : count2);
    }
    return minCount / maxCount;
  }

}
