package descriptorimpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

public class MetricExtension extends Metric {

  public MetricExtension(String _name) {
    super(_name);
  }
  
  public double getPrecision() {
    Set<String> relevant = new HashSet<String>();
    Set<String> retrieved = new HashSet<String>();
    for(Set<String> r : qrelSet_List) {
      for(String r1 : r) {
        relevant.add(r1);
      }
    }
    for(List<String> r : list_rankList) {
      for(String r1 : r) {
        retrieved.add(r1);
      }
    }
    Double top = (double) Sets.intersection(relevant, retrieved).size();
    Double bottom = (double) retrieved.size();
    Double precision = new Double(top/bottom);
    return precision;
  }
  
  public double getRecall() {
    return 0;
    
  }
  
  public double getFScore() {
    return 0;
    
  }

}
