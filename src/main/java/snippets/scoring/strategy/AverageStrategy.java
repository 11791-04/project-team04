package snippets.scoring.strategy;

import snippet.scoring.adapter.CandidateAnswer;
import snippet.scoring.factory.Question;
import snippet.scoring.factory.Similarity;

/*
 * Takes the average of all other similarity metrics
 */
public class AverageStrategy implements Similarity {

  private DiceSimilarityStrategy dss;

  private JaccardSimilarityStrategy jss;

  private CosineSimilarityStrategy cos;

  public AverageStrategy() {
    dss = new DiceSimilarityStrategy();
    jss = new JaccardSimilarityStrategy();
    cos = new CosineSimilarityStrategy();
  }

  @Override
  public Double computeSimilarity(Question query, CandidateAnswer ans) {
    Double dice = dss.computeSimilarity(query, ans);
    Double jacc = jss.computeSimilarity(query, ans);
    Double cosine = cos.computeSimilarity(query, ans);
    return (dice + jacc + cosine) / 3;
  }

}
