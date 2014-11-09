package docretrieval;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Ranker {

  public static final int RANKER_OKAPI = 0;
  public static final int RANKER_INDRI = 1;
  public static final int RANKER_DIRICHLET = 2;
  public static final int RANKER_NQG = 3;

  public static double k1 = -0.5;
  public static double b = 0.8;
  public static double k3 = 0;

  public static double lambda = 0.5;  //JM, Indri, MAG
  public static double mu=10; // Dirichlet, NQG
  public static double delta = 0.1; // NQG


  public static double scoreDoc(int rankerType, CollectionStatistics stat, DocInfo doc, QueryInfo query) {

    double score = 0;

    for(Entry<String, Map<String, Integer>> e: doc.fieldTFMap.entrySet()) {
      String field = e.getKey();
      Map<String, Integer> termFreqVec = e.getValue();
      int length = doc.fieldLenMap.get(field);

      double fScore;
      switch (rankerType) {
        case RANKER_OKAPI:  fScore = scoreOkapi(stat, length, termFreqVec, query);
        break;
        case RANKER_DIRICHLET:  fScore = scoreDirichlet(stat, length, termFreqVec, query);
        break;
        case RANKER_INDRI:  fScore = scoreIndri(stat, length, termFreqVec, query);
        break;
        case RANKER_NQG:  fScore = scoreNQG(stat, length, termFreqVec, query);
        break;
        default: fScore = scoreIndri(stat, length, termFreqVec, query);
      }
      score += fScore;
    }

    return score;
  }


  public static double scoreOkapi(CollectionStatistics stat, int length, Map<String, Integer> termFreqVec, QueryInfo query) {
    double doclen = length;
    double score = 0;
    int numTotalDoc = stat.numDocs;
    int collectionSize = stat.size;

    double avgDoclen = collectionSize/(double)numTotalDoc;
    for(String term: query.termFreqVec.keySet()) {
      int tf_d = termFreqVec.containsKey(term) ? termFreqVec.get(term) : 0;
      int tf_q = query.termFreqVec.get(term);
      double df_t = stat.invList.get(term).size()+1; // Avoid dividing by zero?

      double RSJweight = Math.log((numTotalDoc-df_t+0.5)/(df_t+0.5));


      double TFweight = tf_d / ( tf_d + k1*((1-b) + b*(doclen/avgDoclen) ) );
      double USERweight = ((k3+1)*tf_q)/(double)(k3+tf_q);

      score += RSJweight*TFweight*USERweight;
    }
    return score;
  }



  public static double scoreDirichlet(CollectionStatistics stat, int length, Map<String, Integer> termFeqVec, QueryInfo query) {


    double docScore = 0;
    double doclen = length;
    for(String term: query.termFreqVec.keySet()) {

      double tf_d = termFeqVec.containsKey(term) ? termFeqVec.get(term) : 0;
      double tf_C = stat.collectionTermFreqMap.get(term);

      double termScpre = ( doclen/(doclen+mu) )*(tf_d/doclen)+(mu/(mu+doclen))*(tf_C/(double)stat.size);
      docScore += query.termFreqVec.get(term)*Math.log(termScpre);
    }

    return docScore;
  }

  public static double scoreIndri(CollectionStatistics stat, int length, Map<String, Integer> termFeqVec, QueryInfo query) {

    double doclen = length;
    double docScore = 1d;

    for(String term: query.termFreqVec.keySet()) {
      double termScore = 0d;

      int tf_d = termFeqVec.containsKey(term) ? termFeqVec.get(term) : 0;
      long C_Size = stat.size;
      int tf_C = stat.collectionTermFreqMap.get(term);

      double dirScore = ( doclen/(doclen+mu) )*(tf_d/doclen)+(mu/(mu+doclen))*(tf_C/(double)stat.size);

      termScore += lambda*(dirScore)+(1-lambda)*(tf_C/(double)C_Size);

      docScore *= Math.pow(termScore, query.termFreqVec.get(term));
    }

    return docScore;
  }

  public static double scoreNQG(CollectionStatistics stat, int length, Map<String, Integer> termFeqVec, QueryInfo query) {



    double doclen = length;

    double score = 0;

    for(String term: query.termFreqVec.keySet()) {

      double tf_d = termFeqVec.containsKey(term) ? termFeqVec.get(term) : 0;
      if(tf_d==0){continue;}  // As in formula 18 of XQL paper, it only sum over terms which are in both the query and the document

      double tf_c = stat.collectionTermFreqMap.get(term);

      //double P__q_CwoD = tf_c/(Statistics.collectionSize-docTermFreq);
      double P__q_C = tf_c/(double)(stat.size);

      double partA = Math.log(1+ tf_d/(mu * P__q_C));
      double partB = Math.log(1+ delta/(mu * P__q_C));

      score += query.termFreqVec.get(term) * (partA + partB);
    }
    double partC = query.length * Math.log(mu / (doclen + mu));
    score += partC;

    return score;
  }

}
