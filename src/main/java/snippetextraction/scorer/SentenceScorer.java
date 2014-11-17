package snippetextraction.scorer;

import scoring.CandidateAnswer;
import scoring.CandidateAnswerAdapter;
import scoring.Question;
import scoring.QuestionAdapter;
import scoring.Similarity;
import scoring.SimilarityFactory;
import snippetextraction.SentenceInfo;
import docretrieval.CollectionStatistics;

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
