package stuff;

public class DiceJaccardStrategy implements Similarity {

  private DiceSimilarityStrategy dss;

  private JaccardSimilarityStrategy jss;

/*
 * Computes the harmonic mean of the Dice and Jaccard coefficients 
 */
  public DiceJaccardStrategy() {
    dss = new DiceSimilarityStrategy();
    jss = new JaccardSimilarityStrategy();
  }

  @Override
  public Double computeSimilarity(Question query, Answer ans) {
    Double dice = dss.computeSimilarity(query, ans);
    Double jaccard = jss.computeSimilarity(query, ans);
    Double[] data = { dice, jaccard };
    return harmonicMean(data);
  }

  private Double harmonicMean(Double[] data) {
    Double sum = 0.0;
    for (int i = 0; i < data.length; i++) {
      sum += 1.0 / data[i];
    }
    return data.length / sum;
  }

}
