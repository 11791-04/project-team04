package descriptorimpl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import util.FindingPseudoRelevanceFeedback;
import util.PseudoRelevanceFeedbackFactory;
import util.WebAPIServiceProxy;
import util.WebAPIServiceProxyFactory;
import util.TypeFactory;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse.Finding;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.kb.Concept;
import edu.cmu.lti.oaqa.type.retrieval.ConceptSearchResult;

public class ConceptAnalysisEngine extends JCasAnnotator_ImplBase {

  private WebAPIServiceProxy service;
  private FindingPseudoRelevanceFeedback prf;

  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);
    this.service = WebAPIServiceProxyFactory.getInstance();
    this.prf = PseudoRelevanceFeedbackFactory.getFindingPseudoRelevanceFeedback();
  }

  @Override
  public void process(JCas jcas) throws AnalysisEngineProcessException {
    System.out.println("Processing Concept Search Results...");
    FSIterator<?> iter = jcas.getAnnotationIndex(Question.type).iterator();
    if (iter.hasNext()) {
      Question q = (Question) iter.get();
      String query = q.getText();
      /* TODO */
      //query = new QueryThingy().playWithQuery(query);
      List<Finding> findings = service.getFindingsFromQuery(query);
      //findings = prf.getPRF(findings);
      Collections.sort(findings, new Comparator<Finding>() {
        public int compare(Finding o1, Finding o2) {
          if (o1.getScore() < o2.getScore())
            return 1;
          else if (o1.getScore() > o2.getScore())
            return -1;
          else
            return 0;
        }
      });
      Integer rank = 0;
      for (Finding f : findings) {
        createConceptSearchResultFromFinding(jcas, f, query, ++rank);
      }
    }
  }

  private void createConceptSearchResultFromFinding(JCas jcas, Finding f, String queryString,
          Integer rank) {
    OntologyServiceResponse.Concept c = f.getConcept();
    Concept concept = TypeFactory.createConcept(jcas, c.getLabel(), c.getUri());
    ConceptSearchResult csr = TypeFactory.createConceptSearchResult(jcas, concept, c.getUri(),
            f.getScore(), c.getLabel(), queryString);
    csr.setRank(rank);
    // System.out.println("Concept: " + concept.getName() + " URI: " +
    // concept.getUris().getNthElement(0));
    csr.addToIndexes(jcas);
  }
}
