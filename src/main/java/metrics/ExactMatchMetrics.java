package metrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.uima.resource.ResourceInitializationException;

public class ExactMatchMetrics {

  private int numberOfQuestions = 0;

  private int factoidStrictCorrect = 0;

  private int factoidLenientCorrect = 0;

  private ArrayList<Double> listPrecision;

  private ArrayList<Double> listRecall;

  private ArrayList<Double> listF1;
  
  private ArrayList<Double> listSoftPrecision;

  private ArrayList<Double> listSoftRecall;

  private ArrayList<Double> listSoftF1;

  private ArrayList<Double> factoidMRR;

  public ExactMatchMetrics() {
    listPrecision = new ArrayList<Double>();
    listRecall = new ArrayList<Double>();
    listF1 = new ArrayList<Double>();
    listSoftPrecision = new ArrayList<Double>();
    listSoftRecall = new ArrayList<Double>();
    listSoftF1 = new ArrayList<Double>();
    factoidMRR = new ArrayList<Double>();
  }

  public void register(ArrayList<String> answers, ArrayList<String> goldStandards) {
    int overlap = intersection(goldStandards, answers);
    double softOverlap = softIntersection(goldStandards, answers);

    numberOfQuestions += 1;
    if (answers.isEmpty()) {
      factoidMRR.add(0.0);
    } else if (!goldStandards.isEmpty()) {
      if (goldStandards.get(0).equals(answers.get(0))) {
        factoidStrictCorrect += 1;
      }
      if (overlap != 0) {
        factoidLenientCorrect += 1;
        factoidMRR.add(1.0 / (answers.indexOf(goldStandards.get(0)) + 1));
      } else {
        factoidMRR.add(0.0);
      }
    }

    if (overlap == 0) {
      listPrecision.add(0.0);
      listRecall.add(0.0);
      listF1.add(0.0);
    } else {
      listPrecision.add(((double) overlap) / answers.size());
      listRecall.add(((double) overlap) / goldStandards.size());
      Double P = listPrecision.get(listPrecision.size() - 1);
      Double R = listRecall.get(listRecall.size() - 1);
      listF1.add(2 * P * R / (P + R));
    }
    
    if (softOverlap == 0.0) {
      listSoftPrecision.add(0.0);
      listSoftRecall.add(0.0);
      listSoftF1.add(0.0);
    } else {      
      listSoftPrecision.add(((double) softOverlap) / answers.size());
      listSoftRecall.add(((double) softOverlap) / goldStandards.size());
      Double softP = listSoftPrecision.get(listSoftPrecision.size() - 1);
      Double softR = listSoftRecall.get(listSoftRecall.size() - 1);
      listSoftF1.add(2 * softP * softR / (softP + softR));
    }
  }

  public float strictAccuracy() {
    return ((float) factoidStrictCorrect) / numberOfQuestions;
  }

  public float lenientAccuracy() {
    return ((float) factoidLenientCorrect) / numberOfQuestions;
  }

  public float mrr() {
    return (float) averageList(factoidMRR);
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
  

  public float softP() {
    return (float) averageList(listSoftPrecision);
  }

  public float softR() {
    return (float) averageList(listSoftRecall);
  }

  public float softF1() {
    return (float) averageList(listSoftF1);
  }

  public int total() {
    return numberOfQuestions;
  }

  private double averageList(List<Double> list) {
    return list.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
  }

  private <T> int intersection(List<T> list1, List<T> list2) {
    List<T> list = new ArrayList<T>();

    for (T t : list1) {
      if (list2.contains(t)) {
        list.add(t);
      }
    }

    return list.size();
  }
  
  private double softIntersection(List<String> list1, List<String> list2) {
    double total = 0.0;
    for(String s1 : list1) {
      double maxScore = 0.0;
      List<String> tokens1 = Arrays.asList(s1.toLowerCase().split("\\s+"));
      for(String s2 : list2) {
        List<String> tokens2 = Arrays.asList(s2.toLowerCase().split("\\s+"));
        int overlap = intersection(tokens1, tokens2);
        maxScore = Math.max(maxScore, ((double)2*overlap) / (tokens1.size()+tokens2.size()));
      }
      total += maxScore;
    }
    return total;
  }

}
