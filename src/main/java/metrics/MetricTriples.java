package metrics;

import java.util.List;
import java.util.Set;

/**
 * Class to calculate metric for Triples.
 * @author pyadapad
 *
 */
public class MetricTriples extends MetricBasic<String> {

  public MetricTriples(String _name) {
    super(_name);
    // TODO Auto-generated constructor stub
  }

  @Override
  public double getAPforQuery(Set<String> qrelSet, List<String> rankList) {
    double precAccum = 0d;
    double relCount = 0;
    for (int i = 0; i < rankList.size(); i++) {
      String[] sParts = rankList.get(i).split(":delim:");
      int matchCount = 0;
      for(String g : qrelSet) {
        String[] gParts = g.split(":delim:");
        for(int j = 0; j < 3; j++) {
          if(gParts[j].contains(sParts[j])) {
            matchCount++;
          }
        }
      }
      if(matchCount != 0) {
        relCount = relCount + (double) matchCount / 3.0f;
        precAccum += (double)relCount / (double) (i + 1);
      }
    }
    if(relCount==0) {return 0d;}
    double AP = precAccum / relCount;

    return AP;
  }

  @Override
  public double getF1forQuery(Set<String> goldSet, List<String> answerSet) {
    // TODO Auto-generated method stub
    return 0;
  }

  public double getCurrentGMAPForTriples(double epsilon) {
    if (Double.compare(epsilon, 0d) == 0) {
      epsilon = 0.01;
    }
    double product = 1d;
    for (int i = 0; i < list_rankList.size(); i++) {
      product *= (getAPforQuery(qrelSet_List.get(i), list_rankList.get(i)) + epsilon);
    }
    return Math.pow(product, 1.0 / list_rankList.size());
  }
  
  public double getMAPForTriples() {
    double sumAP = 0d;
    for (int i = 0; i < list_rankList.size(); i++) {
      sumAP += getAPforQuery(qrelSet_List.get(i), list_rankList.get(i));
    }
    return sumAP / list_rankList.size();
  }
  
}
