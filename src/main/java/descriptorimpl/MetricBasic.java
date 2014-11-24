package descriptorimpl;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public abstract class MetricBasic<T> {

  
  public List<Set<T>> qrelSet_List; // Gold Standard

  public List<List<T>> list_rankList; // Answer

  public String name;
  
  

  public MetricBasic(String _name) {
    super();
    name = _name;
    qrelSet_List = new ArrayList<Set<T>>();
    list_rankList = new ArrayList<List<T>>();
  }

  public void registerAnswerAndGoldStandard(List<T> answer, List<T> gold) {
    Set<T> qrelSet_i = new HashSet<T>();
    for(T docID: gold) {
      qrelSet_i.add(docID);
    }
    qrelSet_List.add(qrelSet_i);
    list_rankList.add(answer);

  }

  public String getName() {
    return name;
  }

  public double getCurrentMAP() {
    double sumAP = 0d;
    for (int i = 0; i < list_rankList.size(); i++) {
      sumAP += getAPforQuery(qrelSet_List.get(i), list_rankList.get(i));
      
    }
    return sumAP / list_rankList.size();
  }

  double getCurrentGMAP(double epsilon) {
    if (Double.compare(epsilon, 0d) == 0) {
      epsilon = 0.01;
    }
    double product = 1d;
    for (int i = 0; i < list_rankList.size(); i++) {
      product *= (getAPforQuery(qrelSet_List.get(i), list_rankList.get(i)) + epsilon);
    }
    return Math.pow(product, 1.0 / list_rankList.size());
  }
  
  public double getCurrentF1() {
    double sumF1 = 0d;
    for (int i = 0; i < list_rankList.size(); i++) {
      sumF1 += getF1forQuery(qrelSet_List.get(i), list_rankList.get(i));
    }
    return sumF1 / list_rankList.size();
  }
  
  
  public void dumpData() {
    System.out.println("GOLD STANDARD DATA");
    System.out.println(qrelSet_List); // Gold Standard
    System.out.println("ANSWER DATA");
    System.out.println(list_rankList); // Answer
    
  }

  public abstract double getAPforQuery(Set<T> qrelSet, List<T> rankList);
  
  public abstract double getF1forQuery(Set<T> goldSet, List<T> answerSet);
  
}
