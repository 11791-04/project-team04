package scoring;

import java.util.HashMap;

import docretrieval.CollectionStatistics;
import snippetextraction.SentenceInfo;

/**
 * Converts between a SentenceScorer and a Question type 
 * 
 * @author nwolfe
 * @author pyadapad
 */
public class QuestionAdapter extends Question {

  public QuestionAdapter(SentenceInfo sentence, CollectionStatistics cs) {
    super(null, sentence.getContent(), new HashMap<String,Integer>(cs.collectionTermFreqMap));
  }

}
