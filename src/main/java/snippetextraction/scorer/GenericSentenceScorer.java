package snippetextraction.scorer;

import docretrieval.CollectionStatistics;
import snippetextraction.SentenceInfo;

public abstract class GenericSentenceScorer {

  SentenceInfo query;
  SentenceInfo sentence;
  CollectionStatistics cStat;
  
  public GenericSentenceScorer(SentenceInfo query, SentenceInfo sentence, 
          CollectionStatistics cStat) {
    super();
    this.query = query;
    this.sentence = sentence;
    this.cStat = cStat;
  }
  
  /**
   * Must override
   * @return confidence
   */
  public double score() {
    return Double.NaN;
  }
  
}
