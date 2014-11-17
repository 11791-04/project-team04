package scoring;

import java.util.HashMap;
import java.util.Map;

public class CandidateAnswer implements Comparable<CandidateAnswer> {

  private final Integer queryId;

  private final Integer relevance;

  private final String docText;

  private final HashMap<String, Integer> docTokenFrequencies;
  
  private Integer rank = 0;
  
  private Double similarity = 0.0;

  public CandidateAnswer(Integer queryId, Integer relevance, String docText,
          HashMap<String, Integer> docTokenFreqs) {
    super();
    this.queryId = queryId;
    this.relevance = relevance;
    this.docText = docText;
    this.docTokenFrequencies = docTokenFreqs;
  }

  public Integer getQueryId() {
    return queryId;
  }

  public Integer getRelevance() {
    return relevance;
  }

  public String getDocText() {
    return docText;
  }

  public Map<String, Integer> getDocTokenFrequencies() {
    return docTokenFrequencies;
  }
  
  public void setSimilarity(Double similarity) {
    this.similarity = similarity;
  }

  public Double getSimilarity() {
    return similarity;
  }

  public Integer getRank() {
    return rank;
  }

  public void setRank(Integer rank) {
    this.rank = rank;
  }
  
  public String getReport() {
    String report = String.format("similarity=%.4f\trank=%d\tqid=%d\trel=%d\t%s", 
            getSimilarity(), 
            getRank(), 
            getQueryId(), 
            getRelevance(),
            getDocText());
    return report;
  }

  @Override
  public int compareTo(CandidateAnswer o) {
    if (this.similarity > o.getSimilarity()) {
      return -1;
    } else if (this.similarity < o.getSimilarity()) {
      return 1;
    } else if (this.relevance >= o.relevance) {
      return 1;
    } else {
      return -1;
    }
  }

}
