package descriptorimpl;

import counting.FrequencyCounter;
import counting.FrequencyCounterFactory;

public class QueryThingy {
  public String playWithQuery(String query) {
    FrequencyCounter fc = FrequencyCounterFactory
            .getNewFrequencyCounter(FrequencyCounterFactory.cleanStem);
    fc.tokenizeAndPutAll(query, " ");
    return String.join(" ", fc.keySet());
  }
}
