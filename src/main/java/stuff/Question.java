package stuff;

import java.util.HashMap;
import java.util.Map;

public class Question {

  private final Integer queryId;

  private final String docText;

  private final HashMap<String, Integer> docTokenFrequencies;

  public Question(Integer queryId, String docText, HashMap<String, Integer> docTokenFreqs) {
    super();
    this.queryId = queryId;
    this.docText = docText;
    this.docTokenFrequencies = docTokenFreqs;
  }

  public Integer getQueryId() {
    return queryId;
  }

  public String getDocText() {
    return docText;
  }

  public Map<String, Integer> getDocTokenFrequencies() {
    return docTokenFrequencies;
  }

}
