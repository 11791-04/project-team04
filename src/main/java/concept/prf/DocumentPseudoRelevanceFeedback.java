package concept.prf;

import java.util.List;

import util.text.counter.FrequencyCounter;
import util.text.counter.FrequencyCounterFactory;
import util.webservice.WebAPIServiceProxy;
import util.webservice.WebAPIServiceProxyFactory;
import edu.cmu.lti.oaqa.bio.bioasq.services.PubMedSearchServiceResponse.Document;

public class DocumentPseudoRelevanceFeedback {
  private WebAPIServiceProxy service;

  private int numRecursion = 3;

  public DocumentPseudoRelevanceFeedback() {
    this.service = WebAPIServiceProxyFactory.getInstance();
  }

  public List<Document> getPRF(List<Document> initialResults) {
    return getPRFHelper(initialResults, numRecursion);
  }

  private List<Document> getPRFHelper(List<Document> initialResults, int recursion) {
    if (recursion != 0) {
      --recursion;
      List<Document> nBest = initialResults.subList(0, 10);
      StringBuilder document = new StringBuilder();
      for (Document d : nBest) {
        document.append(d.getDocumentAbstract() + " ");
      }
      FrequencyCounter fc = FrequencyCounterFactory
              .getNewFrequencyCounter(FrequencyCounterFactory.stemStopWord);
      fc.tokenizeAndPutAll(document.toString().trim(), " ");
      String query = String.join(" ", fc.keySet()).trim();
      List<Document> newFindings = service.getPubMedDocumentsFromQuery(query);
      return getPRFHelper(newFindings, recursion);
    } else {
      return initialResults;
    }
  }
}
