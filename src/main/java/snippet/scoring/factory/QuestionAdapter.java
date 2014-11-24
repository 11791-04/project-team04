package snippet.scoring.factory;

import java.util.HashMap;

import snippet.SentenceInfo;
import util.text.counting.FrequencyCounter;
import util.text.counting.FrequencyCounterFactory;

/**
 * Converts between a SentenceScorer and a Question type 
 * 
 * @author nwolfe
 * @author pyadapad
 */
public class QuestionAdapter extends Question {
  
  private static String  counterType = "freq";

  public QuestionAdapter(SentenceInfo sentence) {
    super(null, sentence.getContent(), getTermFrequencies(sentence));          
  }
  
  private static HashMap<String,Integer> getTermFrequencies(SentenceInfo s) {
    FrequencyCounter fc = FrequencyCounterFactory.getNewFrequencyCounter(counterType);
    fc.tokenizeAndPutAll(s.getContent(), "\\s");
    return (HashMap<String, Integer>) fc;
  }

}
