import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;


import util.TypeConstants;
import edu.cmu.lti.oaqa.type.answer.Answer;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.retrieval.ConceptSearchResult;
import edu.cmu.lti.oaqa.type.retrieval.Document;
import edu.cmu.lti.oaqa.type.retrieval.Passage;
import edu.cmu.lti.oaqa.type.retrieval.TripleSearchResult;
import edu.cmu.lti.oaqa.type.kb.Triple;

public class ExactMatchConsumer extends CasConsumer_ImplBase {


  public void initialize() throws ResourceInitializationException {
  }

  @Override
  public void processCas(CAS aCas) throws ResourceProcessException {
    JCas aJCas;
    try {
      aJCas = aCas.getJCas();
    } catch (CASException e) {
      throw new ResourceProcessException(e);
    }

    FSIterator<?> qit = aJCas.getAnnotationIndex(Question.type).iterator();
    Question question = null;
    if (qit.hasNext()) {
      question = (Question) qit.next();
      
    }
    String qType = question.getQuestionType();
    if (qType.equals("OPINION")) {
      return;
    }
    System.out.println("Question Type: " + qType);    

    try {
      FSIterator<?> it;
      it = aJCas.getFSIndexRepository().getAllIndexedFS(
              aJCas.getRequiredType("edu.cmu.lti.oaqa.type.answer.Answer"));
      while (it.hasNext()) {
        Answer answer = (Answer) it.next();
        if (answer.getRank() == TypeConstants.RANK_UNKNOWN) {
          System.out.println("gANSWER: " + answer.getText());
        } else {
          System.out.println("ANSWER: " + answer.getText());
        }
      }
    } catch (CASException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  @Override
  public void collectionProcessComplete(ProcessTrace arg0) throws ResourceProcessException,
          IOException {

  }

}
