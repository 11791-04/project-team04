package util.text.counting;

public class FrequencyCounterFactory {
  public static final String cleanStem = "cleanStem";

  public static final String stemStopWord = "stemStopWord";

  public static final String stopWord = "stopWord";

  public static final String freq = "freq";

  public static final String lemma = "lemma";

  public static final String stem = "stem";

  private static String current = cleanStem;

  public static FrequencyCounter getNewFrequencyCounter(String type) {
    if (type != null)
      current = type;
    if (current.equals(cleanStem))
      return new CleanStemCounter();
    else if (current.equals(stemStopWord))
      return new CleanStemStopWordCounter();
    else if (current.equals(stopWord))
      return new StopWordCounter();
    else if (current.equals(freq))
      return new FrequencyCounter();
    else if (current.equals(lemma))
      return new LemmatizeCounter();
    else if (current.equals(stem))
      return new StemCounter();
    else
      return new FrequencyCounter();
  }
}
