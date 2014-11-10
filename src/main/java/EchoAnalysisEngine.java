import java.util.ArrayList;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;

import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.retrieval.ConceptSearchResult;
import edu.cmu.lti.oaqa.type.retrieval.Document;
import edu.cmu.lti.oaqa.type.retrieval.Passage;
import edu.cmu.lti.oaqa.type.retrieval.TripleSearchResult;



public class EchoAnalysisEngine extends JCasAnnotator_ImplBase {

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    System.out.println(aJCas);
    
    FSIterator<?> qit = aJCas.getAnnotationIndex(Question.type).iterator();
    Question question = null;
    if(qit.hasNext()) {
      question = (Question) qit.next();
      System.out.println(question.getText());
      System.out.println(question.getId());
      System.out.println(question.getQuestionType());
    }
//    
//    try {
//      FSIterator<?> it;
//      it = aJCas.getFSIndexRepository().getAllIndexedFS(aJCas.getRequiredType("edu.cmu.lti.oaqa.type.retrieval.Document"));
//      while (it.hasNext()) {
//        Document doc = (Document) it.next();
//        System.out.println(doc);
//      }
//    } catch (CASException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    }   
    
//    try {
//      FSIterator<?> it;
//      it = aJCas.getFSIndexRepository().getAllIndexedFS(aJCas.getRequiredType("edu.cmu.lti.oaqa.type.retrieval.ConceptSearchResult"));
//
//      while (it.hasNext()) {
//        ConceptSearchResult concept = (ConceptSearchResult) it.next();
//        System.out.println(concept);
//      }
//    } catch (CASException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    }
    
    try {
      FSIterator<?> it;
      it = aJCas.getFSIndexRepository().getAllIndexedFS(aJCas.getRequiredType("edu.cmu.lti.oaqa.type.retrieval.TripleSearchResult"));

      while (it.hasNext()) {
        TripleSearchResult triple = (TripleSearchResult) it.next();
        System.out.println(triple);
      }
    } catch (CASException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
//    try {
//      FSIterator<?> it;
//      it = aJCas.getFSIndexRepository().getAllIndexedFS(aJCas.getRequiredType("edu.cmu.lti.oaqa.type.retrieval.Passage"));
//      while (it.hasNext()) {
//        Passage snippet = (Passage) it.next();
//        System.out.println(snippet);
//      }
//    } catch (CASException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    }

    System.out.println("---------");

  }

}
