package metrics;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.uima.resource.ResourceInitializationException;

public class ExactMatchMetrics {
  private int yesNoTotal = 0;

  private int yesNoCorrect = 0;

  private int factoidTotal = 0;

  private int factoidStrictCorrect = 0;

  private int factoidLenientCorrect = 0;

  private ArrayList<Double> listPrecision;

  private ArrayList<Double> listRecall;

  private ArrayList<Double> listF1;

  private ArrayList<Double> factoidMRR;

  public ExactMatchMetrics() {
    listPrecision = new ArrayList<Double>();
    listRecall = new ArrayList<Double>();
    listF1 = new ArrayList<Double>();
    factoidMRR = new ArrayList<Double>();
  }

  public void register(ArrayList<String> answers, ArrayList<String> goldStandards) {

    factoidTotal += 1;
    if (answers.isEmpty()) {
      System.out.println("FACTOID Answer does not exist in the INDEX!!!");
      factoidMRR.add(0.0);
    } else if (!goldStandards.isEmpty()) {
      if (goldStandards.get(0).equals(answers.get(0))) {
        factoidStrictCorrect += 1;
      }
      List<String> TP = intersection(goldStandards, answers);
      if (!TP.isEmpty()) {
        factoidLenientCorrect += 1;
        factoidMRR.add(1.0 / (answers.indexOf(goldStandards.get(0)) + 1));
      } else {
        factoidMRR.add(0.0);
      }
    }

    List<String> TP = intersection(goldStandards, answers);
    if (TP.size() == 0) {
      listPrecision.add(0.0);
      listRecall.add(0.0);
    } else {
      listPrecision.add(((double) TP.size()) / answers.size());
      listRecall.add(((double) TP.size()) / goldStandards.size());
    }
    if (TP.size() == 0) {
      listF1.add(0.0);
    } else {
      Double P = listPrecision.get(listPrecision.size() - 1);
      Double R = listRecall.get(listRecall.size() - 1);
      listF1.add(2 * P * R / (P + R));
    }

    yesNoTotal += 1;
    if (answers.isEmpty()) {
      System.out.println("YESNO Answer does not exist in the INDEX!!!");
    } else if (!goldStandards.isEmpty()) {
      if (goldStandards.get(0).equals(answers.get(0))) {
        yesNoCorrect += 1;
      }
    }
  }

  public float strictAccuracy() {
    return ((float) factoidStrictCorrect) / factoidTotal;
  }

  public float lenientAccuracy() {
    return ((float) factoidLenientCorrect) / factoidTotal;
  }

  public float mrr() {
    return (float) averageList(factoidMRR);
  }

  public float accuracy() {
    return ((float) yesNoCorrect) / yesNoTotal;
  }

  public float p() {
    return (float) averageList(listPrecision);
  }

  public float r() {
    return (float) averageList(listRecall);
  }

  public float f1() {
    return (float) averageList(listF1);
  }

  public int total() {
    return factoidTotal;
  }

  private double averageList(List<Double> list) {
    return list.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
  }

  private <T> List<T> intersection(List<T> list1, List<T> list2) {
    List<T> list = new ArrayList<T>();

    for (T t : list1) {
      if (list2.contains(t)) {
        list.add(t);
      }
    }

    return list;
  }

}
