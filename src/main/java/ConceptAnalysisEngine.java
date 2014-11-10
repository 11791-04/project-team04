import java.util.LinkedList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import util.GoPubMedServiceFactory;
import util.GoPubMedServiceProxy;
import util.TypeFactory;
import edu.cmu.lti.oaqa.bio.bioasq.services.GoPubMedService;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse.Finding;
import edu.cmu.lti.oaqa.type.kb.Concept;
import edu.cmu.lti.oaqa.type.kb.ConceptMention;
import edu.cmu.lti.oaqa.type.retrieval.ConceptSearchResult;


public class ConceptAnalysisEngine extends JCasAnnotator_ImplBase {

  private GoPubMedServiceProxy service;
  private List<ConceptSearchResult> conceptSearchResults;
  
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);
    this.service = new GoPubMedServiceProxy();
    this.conceptSearchResults = new LinkedList<ConceptSearchResult>();
  }
  
  @Override
  public void process(JCas jcas) throws AnalysisEngineProcessException {
    String query = jcas.getSofaDataString();
    List<Finding> findings = service.getFindingsFromQuery(query);
    for(Finding f : findings) {
      createConceptSearchResultFromFinding(jcas, f);
    }
  }

  private void createConceptSearchResultFromFinding(JCas jcas, Finding f) {
    OntologyServiceResponse.Concept c = f.getConcept();
    Concept concept = TypeFactory.createConcept(jcas, c.getLabel(), c.getUri());
    ConceptSearchResult csr = TypeFactory.createConceptSearchResult(jcas, concept, c.getUri());
    System.out.println("Concept: " + concept.getName() + " URI: " + concept.getUris().getNthElement(0));
    conceptSearchResults.add(csr);
    csr.addToIndexes(jcas);
  }

}
