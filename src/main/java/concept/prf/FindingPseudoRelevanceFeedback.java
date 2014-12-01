package concept.prf;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import document.stemmer.KrovetzStemmer;
import util.QueryExpander;
import util.text.counter.FrequencyCounter;
import util.text.counter.FrequencyCounterFactory;
import util.webservice.WebAPIServiceProxy;
import util.webservice.WebAPIServiceProxyFactory;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse.Finding;

public class FindingPseudoRelevanceFeedback {

  private WebAPIServiceProxy service;

  private int numRecursion = 2;

  public FindingPseudoRelevanceFeedback() {
    this.service = WebAPIServiceProxyFactory.getInstance();
  }

  public List<Finding> getPRF(List<Finding> initialResults) {
    return getPRFHelper(initialResults, numRecursion);
  }

  private List<Finding> getPRFHelper(List<Finding> initialResults, int recursion) {
    if (recursion != 0) {
      --recursion;
      Collections.sort(initialResults, new Comparator<Finding>() {
        public int compare(Finding o1, Finding o2) {
          if (o1.getScore() < o2.getScore())
            return 1;
          else if (o1.getScore() > o2.getScore())
            return -1;
          else
            return 0;
        }
      });
      List<Finding> nBest = initialResults.subList(0, 10);
      StringBuilder document = new StringBuilder();
      for (Finding f : nBest) {
        document.append(f.getConcept().getLabel() + " ");
      }
      FrequencyCounter fc = FrequencyCounterFactory
              .getNewFrequencyCounter(FrequencyCounterFactory.stemStopWord);
      fc.tokenizeAndPutAll(document.toString().trim(), " ");
      String query = String.join(" ", fc.keySet()).trim();
      query = QueryExpander.expandQuery(query, new KrovetzStemmer());
      List<Finding> newFindings = service.getFindingsFromQuery(query);
      if(newFindings.isEmpty()) {
        return initialResults;
      } else if (newFindings.size() <= initialResults.size()) {
        return initialResults;
      } else {
        return getPRFHelper(newFindings, recursion);
      }
    } else {
      return initialResults;
    }
  }
}
