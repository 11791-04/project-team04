package descriptorimpl;

import java.util.List;
import java.util.Set;

public class MetricExtension extends Metric {

  public MetricExtension(String _name) {
    super(_name);
  }

  public void getPrecision() {
    int i = 0;
    Double AP = 0.0;
    for (Set<String> relevant : qrelSet_List) {
      List<String> retrieved = list_rankList.get(i++);
      Double p = getAPforQuery(relevant,retrieved);
      System.out.println("Q" + i + " Precision: " + p);
      AP += p;
    }
    System.out.println("Mean Average Precision: " + (AP / i));
  }

  public double getRecall() {
    return 0;

  }

  public double getFScore() {
    return 0;

  }

}
