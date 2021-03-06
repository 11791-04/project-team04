package snippet.scoring.factory;

import snippets.scoring.strategy.AverageStrategy;
import snippets.scoring.strategy.CosineSimilarityStrategy;
import snippets.scoring.strategy.DiceJaccardStrategy;
import snippets.scoring.strategy.DiceSimilarityStrategy;
import snippets.scoring.strategy.JaccardSimilarityStrategy;
import snippets.scoring.strategy.VoteStrategy;
import snippets.scoring.strategy.WeightedSumStrategy;

public class SimilarityFactory {
  
  public final static String weighted = "weighted";
  
  public final static String avg = "average";
  
  public final static String vote = "vote";

  public final static String dj = "dice-jaccard";

  public final static String dice = "dice";

  public final static String jaccard = "jaccard";

  public final static String cos = "cosine";

  private static String current = weighted;

  public static Similarity getNewSimilarity(String type) {
    if(type != null)
      current = type;
    if (current.equals(dj))
      return new DiceJaccardStrategy();
    else if (current.equals(dice))
      return new DiceSimilarityStrategy();
    else if (current.equals(jaccard))
      return new JaccardSimilarityStrategy();
    else if (current.equals(cos))
      return new CosineSimilarityStrategy();
    else if (current.equals(vote))
      return new VoteStrategy();
    else if (current.equals(avg))
      return new AverageStrategy();
    else if (current.equals(weighted))
      return new WeightedSumStrategy();
    else
      return new CosineSimilarityStrategy();
  }
}
