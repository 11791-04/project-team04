package snippet.scoring.adapter;

import java.util.HashMap;

import snippet.SentenceInfo;
import util.text.counting.FrequencyCounter;
import util.text.counting.FrequencyCounterFactory;

/**
 * Converts between a {@link SentenceInfo} and a {@link CandidateAnswer} type 
 * 
 * @author nwolfe
 * @author pyadapad
 */
public class CandidateAnswerAdapter extends CandidateAnswer {

  private static String  counterType = "freq";
  
  public CandidateAnswerAdapter(SentenceInfo sentence) {
    super(null, null, sentence.getContent(), getTermFrequencies(sentence));          
  }
  
  private static HashMap<String,Integer> getTermFrequencies(SentenceInfo s) {
    FrequencyCounter fc = FrequencyCounterFactory.getNewFrequencyCounter(counterType);
    fc.tokenizeAndPutAll(s.getContent(), "\\s");
    return (HashMap<String, Integer>) fc;
  }
}
