package stuff;

/**
 * Computes all other metrics and takes a vote
 * 
 * @author nwolfe
 */
public class VoteStrategy implements Similarity {

  private DiceSimilarityStrategy dss;

  private JaccardSimilarityStrategy jss;

  private CosineSimilarityStrategy cos;

  public VoteStrategy() {
    dss = new DiceSimilarityStrategy();
    jss = new JaccardSimilarityStrategy();
    cos = new CosineSimilarityStrategy();
  }

  @Override
  public Double computeSimilarity(Question query, Answer ans) {
    Double dice = dss.computeSimilarity(query, ans);
    Double jacc = jss.computeSimilarity(query, ans);
    Double cosine = cos.computeSimilarity(query, ans);
    return Math.max(cosine, Math.max(dice, jacc));
  }
}
