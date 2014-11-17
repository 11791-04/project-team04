package scoring;

import java.util.HashMap;

import snippetextraction.SentenceInfo;
import docretrieval.CollectionStatistics;

/**
 * Converts between a {@link SentenceInfo} and a {@link CandidateAnswer} type 
 * 
 * @author nwolfe
 * @author pyadapad
 */
public class CandidateAnswerAdapter extends CandidateAnswer {

  public CandidateAnswerAdapter(SentenceInfo sentence, CollectionStatistics cs) {
    super(null, null, sentence.getContent(), new HashMap<String,Integer>(cs.collectionTermFreqMap));
  }

}
