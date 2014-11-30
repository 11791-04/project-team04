package metrics;

import java.util.Set;

import com.google.common.collect.Sets;

public class Precision<T> {
  public double precisionCalc(Set<T> relevant, Set<T> retrieved) {
    Double top = (double) Sets.intersection(relevant, retrieved).size();
    Double bottom = (double) retrieved.size();
    Double precision = new Double(top/bottom);
    return precision;
  }
}