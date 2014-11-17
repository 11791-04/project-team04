package scoring;

public interface Similarity {
  
  public Double computeSimilarity(Question query, CandidateAnswer ans);

}
