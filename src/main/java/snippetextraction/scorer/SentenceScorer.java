package snippetextraction.scorer;

import snippet.SentenceInfo;
import snippet.scoring.adapter.CandidateAnswer;
import snippet.scoring.adapter.CandidateAnswerAdapter;
import snippet.scoring.factory.Question;
import snippet.scoring.factory.QuestionAdapter;
import snippet.scoring.factory.Similarity;
import snippet.scoring.factory.SimilarityFactory;
import document.scoring.CollectionStatistics;

/**
 * Sublcass of {@link GenericSentenceScorer} which "similarity" score for the query
 * and candidate answer
 * 
 * @author nwolfe 
 * @author pyadapad
 *
 */
public class SentenceScorer extends GenericSentenceScorer {

  private CandidateAnswer ca;
  private Question q;
  private String scoreType = "cosine";
  
  public SentenceScorer(SentenceInfo query, SentenceInfo sentence, CollectionStatistics cStat) {
    super(query, sentence, cStat);
    q = new QuestionAdapter(sentence);
    ca = new CandidateAnswerAdapter(sentence);
  }

  @Override
  public double score() {
    Similarity sim = SimilarityFactory.getNewSimilarity(scoreType);
    return sim.computeSimilarity(q, ca);
  }

}
