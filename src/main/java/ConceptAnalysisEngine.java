import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import util.TypeFactory;
import edu.cmu.lti.oaqa.type.kb.Concept;


public class ConceptAnalysisEngine extends JCasAnnotator_ImplBase {

  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);
  }
  
  @Override
  public void process(JCas jcas) throws AnalysisEngineProcessException {
    System.out.print(jcas.getAnnotationIndex());
    FSIterator<Annotation> iter = jcas.getAnnotationIndex().iterator();
    if (iter.isValid()) {
      iter.moveToNext();
      Concept c = TypeFactory.createConcept(jcas, "www.something.com");
      createConceptAnnotation(jcas, c);
    }
  }

  private void createConceptAnnotation(JCas jcas, Concept c) {
    // TODO Auto-generated method stub
    
  }

}
