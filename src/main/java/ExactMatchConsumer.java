import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

  private int yesNoTotal = 0;

  private int yesNoCorrect = 0;

  private ArrayList<Double> listPrecision;

  private ArrayList<Double> listRecall;

  private ArrayList<Double> listF1;

  public void initialize() throws ResourceInitializationException {
    listPrecision = new ArrayList<Double>();
    listRecall = new ArrayList<Double>();
    listF1 = new ArrayList<Double>();
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
        
        break;
      case "LIST":
        List<String> TP = intersection(goldStandards, answers);
        if (TP.size() == 0) {
          listPrecision.add(0.0);
          listRecall.add(0.0);
        } else {
          listPrecision.add(((double)TP.size()) / answers.size());
          listRecall.add(((double)TP.size()) / goldStandards.size());
        }
        if (TP.size() == 0) {
          listF1.add(0.0);
        } else {
          Double P = listPrecision.get(listPrecision.size() - 1);
          Double R = listRecall.get(listRecall.size() - 1);
          listF1.add(2*P*R / (P+R));
        }
        break;
      case "YES_NO":
        yesNoTotal += 1;
        if (answers.isEmpty()) {
          System.out.println("YESNO Answer does not exist in the INDEX!!!");
        } else if (!goldStandards.isEmpty()) {
          if (goldStandards.get(0).equals(answers.get(0))) {
            yesNoCorrect += 1;
          }
        }
        break;
      default:
        System.out.println("UNKNOWN Question Type: " + qType);
        return;
    }
  }

  public <T> List<T> intersection(List<T> list1, List<T> list2) {
    List<T> list = new ArrayList<T>();

    for (T t : list1) {
      if (list2.contains(t)) {
        list.add(t);
      }
    }

    return list;
  }
  
  public double averageList(List<Double> list) {
    return list.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
  }

  @Override
  public void collectionProcessComplete(ProcessTrace arg0) throws ResourceProcessException,
          IOException {
    System.out.println("================================================================================");
    System.out.println("YESNO Accuracy: " + (((float) yesNoCorrect) / yesNoTotal) + " (total: " + yesNoTotal + ")");
    System.out.println("LIST Mean P:  " + averageList(listPrecision) + " (total: " + listPrecision.size() + ")");
    System.out.println("LIST Mean R:  " + averageList(listRecall));
    System.out.println("LIST Mean F1: " + averageList(listF1));
    System.out.println("================================================================================");

  }

}
