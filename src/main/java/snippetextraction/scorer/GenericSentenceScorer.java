package snippetextraction.scorer;

import document.scoring.CollectionStatistics;
import snippet.SentenceInfo;

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
  public abstract double score(); 
}
