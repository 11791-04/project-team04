package descriptorimpl;
import java.util.List;
import java.util.Set;


public class MetricDTC extends MetricBasic<String> {

  
  
  public MetricDTC(String _name) {
    super(_name);
    // TODO Auto-generated constructor stub
  }

  
  @Override
  public double getAPforQuery(Set<String> qrelSet, List<String> rankList) {

    double precAccum = 0d;
    int relCount = 0;
    for (int i = 0; i < rankList.size(); i++) {
      if (qrelSet.contains(rankList.get(i))) {
        relCount++;
        double precAtI = relCount / (double) (i + 1);
        precAccum += precAtI;
        
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
  
}
