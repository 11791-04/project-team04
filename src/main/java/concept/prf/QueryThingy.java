package concept.prf;

import util.text.counting.FrequencyCounter;
import util.text.counting.FrequencyCounterFactory;

public class QueryThingy {
  public String playWithQuery(String query) {
    FrequencyCounter fc = FrequencyCounterFactory
            .getNewFrequencyCounter(FrequencyCounterFactory.cleanStem);
    fc.tokenizeAndPutAll(query, " ");
    return String.join(" ", fc.keySet());
  }
}
