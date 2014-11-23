package descriptorimpl;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Metric {

  public List<Set<String>> qrelSet_List; // Gold Standard

  public List<List<String>> list_rankList; // Answer

  private String name;

  public Metric(String _name) {
    super();
    name = _name;
    qrelSet_List = new ArrayList<Set<String>>();
    list_rankList = new ArrayList<List<String>>();
  }

  public void registerAnswerAndGoldStandard(ArrayList<String> answer, ArrayList<String> gold) {
	  Set<String> qrelSet_i = new HashSet<String>();
	  for(String docID: gold) {
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
  
  public void dumpData() {
    System.out.println("GOLD STANDARD DATA");
    System.out.println(qrelSet_List); // Gold Standard
    System.out.println("ANSWER DATA");
    System.out.println(list_rankList); // Answer
    
  }

  private double getAPforQuery(Set<String> qrelSet, List<String> rankList) {

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

}
