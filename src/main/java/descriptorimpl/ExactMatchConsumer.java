package descriptorimpl;

import java.io.IOException;
import java.util.ArrayList;


import metrics.ExactMatchMetrics;

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

public class ExactMatchConsumer extends CasConsumer_ImplBase {

  private ExactMatchMetrics yesNoMetric;
  private ExactMatchMetrics listMetric;
  private ExactMatchMetrics factoidMetric;

  public void initialize() throws ResourceInitializationException {
    yesNoMetric = new ExactMatchMetrics();
    listMetric = new ExactMatchMetrics();
    factoidMetric = new ExactMatchMetrics();
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

    ArrayList<String> goldStandards = new ArrayList<String>();
    ArrayList<String> answers = new ArrayList<String>();
    try {
      FSIterator<?> it;
      it = aJCas.getFSIndexRepository().getAllIndexedFS(
              aJCas.getRequiredType("edu.cmu.lti.oaqa.type.answer.Answer"));
      while (it.hasNext()) {
        Answer answer = (Answer) it.next();
        if (answer.getRank() == TypeConstants.RANK_UNKNOWN) {
          System.out.println("gANSWER: " + answer.getText());
          goldStandards.add(answer.getText());
        } else {
          System.out.println("ANSWER: " + answer.getRank() + " " + answer.getText());
          answers.add(answer.getText());
        }
      }
    } catch (CASException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    if (goldStandards.isEmpty()) {
      System.out.println("No Gold Standard Answers Found, skipping");
      return;
    }
    switch (qType) {
      case "FACTOID":
        factoidMetric.register(answers, goldStandards);
        break;
      case "LIST":
        listMetric.register(answers, goldStandards);
        break;
      case "YES_NO":
        yesNoMetric.register(answers, goldStandards);
        break;
      default:
        System.out.println("UNKNOWN Question Type: " + qType);
        return;
    }
  }

  @Override
  public void collectionProcessComplete(ProcessTrace arg0) throws ResourceProcessException,
          IOException {
    System.out.println("================================================================================");
    System.out.println("# of FACTOID QUESTIONS: " + factoidMetric.total());
    System.out.println("FACTOID Strict Accuracy:  " + factoidMetric.strictAccuracy());
    System.out.println("FACTOID Lenient Accuracy: " + factoidMetric.lenientAccuracy());
    System.out.println("FACTOID MRR: " + factoidMetric.mrr());
    System.out.println("");
    
    System.out.println("# of YESNO QUESTIONS: " + yesNoMetric.total());
    System.out.println("YESNO Accuracy: " + yesNoMetric.strictAccuracy());
    System.out.println("");
    
    System.out.println("# of LIST QUESTIONS: " + listMetric.total());
    System.out.println("LIST Mean P:  " + listMetric.p());
    System.out.println("LIST Mean R:  " + listMetric.r());
    System.out.println("LIST Mean F1: " + listMetric.f1());
    System.out.println("");
    System.out.println("LIST Mean Soft P:  " + listMetric.softP());
    System.out.println("LIST Mean Soft R:  " + listMetric.softR());
    System.out.println("LIST Mean Soft F1: " + listMetric.softF1());
    System.out.println("================================================================================");

  }

}
