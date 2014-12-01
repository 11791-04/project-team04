package descriptorimpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;

import listquestion.ListNER_Analyser;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import util.QueryExpander;
import util.TypeConstants;
import util.TypeFactory;
import util.datastructure.Pair;
import util.text.TextProcessingTools;
import util.webservice.WebAPIServiceProxy;
import util.webservice.WebAPIServiceProxyFactory;
import document.DocInfo;
import document.QueryInfo;
import document.scoring.CollectionStatistics;
import document.scoring.Ranker;
import document.stemmer.KrovetzStemmer;
import edu.cmu.lti.oaqa.bio.bioasq.services.PubMedSearchServiceResponse;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.retrieval.Document;

public class ListQuestionEntityExtractor_AE extends JCasAnnotator_ImplBase {

  KrovetzStemmer stemmer;

  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    System.out.println("DocumentRetrieval_AE - initialize()");
    stemmer = new KrovetzStemmer();


  }


  /**
   * @param aJcas
   *          Assumed to contain questions provided by QuestionReader
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
     System.out.println(">>> ListQuestionEntityExtractor_AE.process()");
    /* IMPORTANT:
     * Assuming only one question in CAS
     * Assuming zero or multiple ranked documents
     * Assuming abstract is always available
     */
    
    String questionType = null;
    CollectionStatistics cStat = new CollectionStatistics();
    QueryInfo queryInfo = null;
    String questionText = null;
    List<Pair<String, String>> docID_AbsText_List = new ArrayList<Pair<String, String>>();
    
    // Reads in question from index
    FSIterator<Annotation> iter = aJCas.getAnnotationIndex().iterator();
    if (iter.isValid()) {
      iter.moveToNext();
      Question question = (Question) iter.get();
      queryInfo = new QueryInfo(question.getText(), stemmer);
      questionText = question.getText().replace('?', ' ');
      questionText = QueryExpander.expandQuery(questionText, stemmer);
      questionType = question.getQuestionType();
      if(!questionType.equals("LIST")) {
        return;
      }
    }
    
    System.out.println(questionType+": " + questionText);
    
    try {
      FSIterator<?> it;
      it = aJCas.getFSIndexRepository().getAllIndexedFS(
              aJCas.getRequiredType("edu.cmu.lti.oaqa.type.retrieval.Document"));
      while (it.hasNext()) {
        Document doc = (Document) it.next();
        if (doc.getSearchId() != null
                && doc.getSearchId().equals(TypeConstants.SEARCH_ID_GOLD_STANDARD)) {
          // Nothing to do with gold standard documents. We can not peak
        } else {
          // Try Extracting NEs from our ranked documents
          docID_AbsText_List.add(new Pair<String, String>(doc.getDocId(), doc.getText()));
          //System.out.println(doc.getText());
        }
      }
    } catch (CASException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    Map<String, String[]> absMap = new HashMap<String, String[]>();
    
    
    for(Pair<String, String> p: docID_AbsText_List) {
      String docID = p.getKey();
      String absText = p.getValue();
      String[] absTermArr = TextProcessingTools.getFormattedTermArray(absText, null);
      absMap.put(docID, absTermArr);
      
    }
    ListNER_Analyser anl = new ListNER_Analyser(questionText, absMap);
    
    
    int rank = 1;
    List<Entry<String, Double>>  result = anl.getNEList(20);
    for(Entry<String, Double> e: result) {
      System.out.printf("**** %s\t%f\n", e.getKey(), e.getValue());
      TypeFactory.createAnswer(aJCas, e.getKey(), new ArrayList<String>(), rank++);
    }
    
  }

  @Override
  public void collectionProcessComplete() throws AnalysisEngineProcessException {
    System.out.println("DocumentRetrieval_AE - collectionProcessComplete()");
  }

  public static class DocScoreComparator implements Comparator<Pair<DocInfo, Double>> {

    @Override
    public int compare(Pair<DocInfo, Double> a, Pair<DocInfo, Double> b) {
      return -a.getValue().compareTo(b.getValue());
    }

  }

}
