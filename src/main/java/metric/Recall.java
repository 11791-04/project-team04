package metric;

import java.util.Set;

import com.google.common.collect.Sets;

public class Recall<T> {
  public double recallCalc(Set<T> relevant, Set<T> retrieved) {
    Double top = (double) Sets.intersection(relevant, retrieved).size();
    Double bottom = (double) relevant.size();
    Double precision = new Double(top / bottom);
    return precision;
  }
}
