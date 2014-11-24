package metric;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import snippet.SentenceInfo;


public class MetricSnippet extends MetricBasic<SentenceInfo> {

  public MetricSnippet(String _name) {
    super(_name);
    // TODO Auto-generated constructor stub
  }

  @Override
  public double getAPforQuery(Set<SentenceInfo> qrelSet, List<SentenceInfo> rankList) {
    // TODO Auto-generated method stub
    return 0;
  }


  private Map<String, Map<String, SentenceCoverage>> 
  update_doc_Sec_MaxIndex_Map(Set<SentenceInfo> goldSet, List<SentenceInfo> answerSet) {

    //System.out.println("update_doc_Sec_MaxIndex_Map");

    Map<String, Map<String, SentenceCoverage>> doc_Sec_MaxIndex_Map
    = new HashMap<String, Map<String, SentenceCoverage>>();

    for(SentenceInfo gold: goldSet) {
      String docUri = gold.hostDoc.uri;
      String secID = gold.sectionIndex;

      //System.out.println(docUri+": sectionIndex: "+gold.sectionIndex);
      if(!doc_Sec_MaxIndex_Map.containsKey(docUri)) {
        doc_Sec_MaxIndex_Map.put(docUri, new HashMap<String, SentenceCoverage>());
      }
      if(!doc_Sec_MaxIndex_Map.get(docUri).containsKey(secID)) {
        doc_Sec_MaxIndex_Map.get(docUri).put(secID, new SentenceCoverage(docUri, secID));
      }
      doc_Sec_MaxIndex_Map.get(docUri).get(secID).goldSents.add(gold);
    }

    for(SentenceInfo ans: answerSet) {
      String docUri = ans.hostDoc.uri;
      String secID = ans.sectionIndex;

      //System.out.println(docUri+": sectionIndex: "+ans.sectionIndex);
      if(!doc_Sec_MaxIndex_Map.containsKey(docUri)) {
        doc_Sec_MaxIndex_Map.put(docUri, new HashMap<String, SentenceCoverage>());
      }
      if(!doc_Sec_MaxIndex_Map.get(docUri).containsKey(secID)) {
        doc_Sec_MaxIndex_Map.get(docUri).put(secID, new SentenceCoverage(docUri, secID));
      }
      doc_Sec_MaxIndex_Map.get(docUri).get(secID).ansSents.add(ans);
    }

    return doc_Sec_MaxIndex_Map;
  }




  @SuppressWarnings("unchecked")
  @Override
  public double getF1forQuery(Set<SentenceInfo> goldSet, List<SentenceInfo> answerSet) {
    //System.out.println(goldSet.size()+" vs "+answerSet.size());

    // <uri, <section name, max index>>
    Map<String, Map<String, Integer>> doc_Sec_MaxIndex_Map 
    = new HashMap<String, Map<String, Integer>>();

    Map<String, Map<String, SentenceCoverage>> base =
            update_doc_Sec_MaxIndex_Map(goldSet, answerSet);



    int allDocDeno = 0;
    double allDocF1_sum = 0d;
    for(Map<String, SentenceCoverage> secStat: base.values()) {
      int indocDeno = 0;
      double inDocSecwiseF1_sum = 0;
      for(SentenceCoverage sc: secStat.values()) {
        if(sc.goldSents.size()>0 && sc.ansSents.size()>0) {
          inDocSecwiseF1_sum += sc.computeF1();
          indocDeno++;
        }


      }
      if(indocDeno != 0) {
        allDocF1_sum += inDocSecwiseF1_sum/(double)indocDeno;
        allDocDeno++;
      }
    }
    double overallF1 = allDocF1_sum/(double)allDocDeno;
    if(Double.compare(overallF1, Double.NaN)==0) {overallF1 = 0d;}
    //System.out.println("doc_Sec_MaxIndex_Map.size() = "+doc_Sec_MaxIndex_Map.size());

    //System.out.println("overallF1 = "+overallF1);
    return overallF1;
  }


  private class SentenceCoverage{
    public String URI;
    public String sectionID;
    public List<SentenceInfo> goldSents = new ArrayList<SentenceInfo>();
    public List<SentenceInfo> ansSents = new ArrayList<SentenceInfo>();



    public SentenceCoverage(String uRI, String sectionID) {
      super();
      this.URI = uRI;
      this.sectionID = sectionID;
    }

    public double computeF1() {

      int upper = Integer.MIN_VALUE;
      int lower = Integer.MAX_VALUE;
      for(SentenceInfo s: goldSents) {
        if(s.getStartIndex() < lower) {lower = s.getStartIndex();}
        if(s.getEndIndex() > upper) {upper = s.getEndIndex();}
      }
      for(SentenceInfo s: ansSents) {
        if(s.getStartIndex() < lower) {lower = s.getStartIndex();}
        if(s.getEndIndex() > upper) {upper = s.getEndIndex();}
      }


      int[] stat = new int[upper-lower];
      for(int i=0; i<stat.length; i++) {stat[i] = Integer.MIN_VALUE;}



      int secAnsSize = 0;
      for(SentenceInfo s: ansSents) {
        for(int i=s.getStartIndex()-lower; i<s.getEndIndex()-lower; i++) {
          stat[i] = -1;
        }
        secAnsSize += s.getEndIndex()-s.getStartIndex();
      }

      int secGoldSize = 0;
      for(SentenceInfo s: goldSents) {
        for(int i=s.getStartIndex()-lower; i<s.getEndIndex()-lower; i++) {
          stat[i] += 1;
        }

        secGoldSize += s.getEndIndex()-s.getStartIndex();
      }

      int secCorrSize = 0;
      for(int a: stat) {
        if(a == 0) {secCorrSize++;}
      }



      double precision = secCorrSize/(double)secAnsSize;
      if(Double.compare(precision, Double.NaN)==0) { precision = 0d;}
      double recall = secCorrSize/(double)secGoldSize;
      if(Double.compare(recall, Double.NaN)==0) { recall = 0d;}

      double F1 = 2*precision*recall / (precision+recall);
      if(Double.compare(F1, Double.NaN)==0) { F1 = 0d;}
      //System.out.printf("URI=%s, Sec:%s, secCorrSize=%d, secAnsSize=%d, secGoldSize=%d, precision=%f, recall %f, F1=%f\n",
     //        URI, sectionID, secCorrSize, secAnsSize, secGoldSize, precision, recall, F1);



      return F1;
    }

  }

}
