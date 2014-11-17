package scoring;

/*
 * Returns a weighted sum of all strategies
 */
public class WeightedSumStrategy implements Similarity {

  private DiceSimilarityStrategy dss;

  private JaccardSimilarityStrategy jss;

  private CosineSimilarityStrategy cos;
  
  /**
   * Here's the black magic!
   */
  private Double diceCoeff = 0.1;

  private Double jaccCoeff = 0.3;

  private Double cosCoeff = 0.5;

  public WeightedSumStrategy() {
    dss = new DiceSimilarityStrategy();
    jss = new JaccardSimilarityStrategy();
    cos = new CosineSimilarityStrategy();
  }

  @Override
  public Double computeSimilarity(Question query, CandidateAnswer ans) {
    Double dice = dss.computeSimilarity(query, ans);
    Double jacc = jss.computeSimilarity(query, ans);
    Double cosine = cos.computeSimilarity(query, ans);
    return (dice * diceCoeff) + (jacc * jaccCoeff) + (cosine * cosCoeff);
  }

}
