package util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import snippetextraction.SentenceInfo;

public class EvalSnippets {

  public static double getF1(List<SentenceInfo> goldSet, List<SentenceInfo> answerSet) {
    
    Map<String, Integer> secMaxIndexMap = new HashMap<String, Integer>();
    
    String currSection = null;
    int maxIndexInSec = Integer.MIN_VALUE;
    
    
    // Populating secMaxIndexMap, which stores the maximum endindex of the snippets in either gold or answer
    for(SentenceInfo gold: goldSet) {
      if(!gold.sectionIndex.equals(currSection)) {
        
        if(currSection != null) {
          secMaxIndexMap.put(currSection, maxIndexInSec);
        }
        currSection = gold.sectionIndex;
      }
      
      if(gold.endIndex > maxIndexInSec) {
        maxIndexInSec = gold.endIndex;
      }

      
      for(SentenceInfo ans: answerSet) {
        if(ans.sectionIndex != gold.sectionIndex) {continue;}
        
        if(ans.endIndex > maxIndexInSec) {
          maxIndexInSec = ans.endIndex;
        }

      }
    }
    
    int docwiseSnipCoverage = 0;
    int docwiseAnsLength = 0;
    int docwiseGoldLength = 0;
    
    // Now we can calculate the coverage
    for(Entry<String, Integer> e: secMaxIndexMap.entrySet()) {
      int[] stat = new int[e.getValue()];
      // The stat is a calculation buffer that allows us to compute the coverage
      for(int i=0; i<stat.length; i++) {stat[i] = Integer.MIN_VALUE;}
      
      for(SentenceInfo gold: goldSet) {
        if(!gold.sectionIndex.equals(e.getValue())) {continue;}
        for(int i=gold.startIndex; i<gold.endIndex; i++) {stat[i] = -1;}
        docwiseGoldLength += gold.endIndex-gold.startIndex;
      }
      
      for(SentenceInfo ans: answerSet) {
        if(!ans.sectionIndex.equals(e.getValue())) {continue;}
        for(int i=ans.startIndex; i<ans.endIndex; i++) {stat[i] += 2;}
        docwiseAnsLength += ans.endIndex-ans.startIndex;
      }
      
      
      for(int i=0; i<stat.length; i++) {
        
        if(stat[i] == 1) {
          docwiseSnipCoverage += stat[i];
        }
      }
      
    }
    
    double precision = docwiseSnipCoverage/(double)docwiseAnsLength;
    double recall = docwiseSnipCoverage/(double)docwiseGoldLength;
    double F1 = 2*precision*recall / (precision+recall);
    
    return F1;
  }
  
  
  
}
