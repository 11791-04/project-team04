package concept.prf;

import util.text.counter.FrequencyCounter;
import util.text.counter.FrequencyCounterFactory;

public class QueryThingy {
  public String playWithQuery(String query) {
    FrequencyCounter fc = FrequencyCounterFactory
            .getNewFrequencyCounter(FrequencyCounterFactory.cleanStem);
    fc.tokenizeAndPutAll(query, " ");
    return String.join(" ", fc.keySet());
  }
}
