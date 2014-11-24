package snippet.scoring.factory;

import snippet.scoring.adapter.CandidateAnswer;

public interface Similarity {
  
  public Double computeSimilarity(Question query, CandidateAnswer ans);

}
