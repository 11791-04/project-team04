package metric;

import java.util.Set;

public class F1<T> {
  public double f1Calc(Set<T> relevant, Set<T> retrieved) {
    Double precision = new Precision<T>().precisionCalc(relevant, retrieved);
    Double recall = new Recall<T>().recallCalc(relevant, retrieved);
    Double F1 = (2 * (precision * recall)) / (precision + recall);
    return F1;
  }
}
