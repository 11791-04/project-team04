import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Metric {

  public List<Set<String>> qrelSet_List;

  
  public Metric(List<List<String>> list_qrelList) {
    super();
    qrelSet_List = new ArrayList<Set<String>>();
    for(int i=0; i<list_qrelList.size(); i++) {
      Set<String> qrelSet_i = new HashSet<String>();
      for(String docID: list_qrelList.get(i)) {
        qrelSet_i.add(docID);
      }
      qrelSet_List.add(qrelSet_i);
    }
    
  }

  public double getMAP(List<List<String>> list_rankList) {
    double sumAP = 0d;
    for(int i=0; i<list_rankList.size(); i++) {
      sumAP += getAPforQuery(qrelSet_List.get(i), list_rankList.get(i));
    }
    return sumAP/list_rankList.size();
  }
  
  double getGMAP(List<List<String>> list_rankList, double epsilon) {
    if(Double.compare(epsilon, 0d)==0) {epsilon = 0.01;}
    double product = 1d;
    for(int i=0; i<list_rankList.size(); i++) {
      product *= (getAPforQuery(qrelSet_List.get(i), list_rankList.get(i))+epsilon);
    }
    return Math.pow(product, 1.0/list_rankList.size());
  }

  
  
  
  
  public double getAPforQuery(Set<String> qrelSet, List<String> rankList) {
    
    double precAccum = 0d;
    int relCount = 0;
    for(int i=0; i<rankList.size(); i++) {
      if(qrelSet.contains(rankList.get(i))) {
        relCount++;
        double precAtI = relCount/(double)(i+1);
        precAccum += precAtI;
      }
    }
    double AP = precAccum/relCount;
    
    return AP;
  }

}
