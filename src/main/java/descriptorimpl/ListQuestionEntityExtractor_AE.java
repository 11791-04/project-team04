package descriptorimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import document.stemmer.KrovetzStemmer;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.retrieval.Document;

public class ListQuestionEntityExtractor_AE extends JCasAnnotator_ImplBase {
  /**
   * The stemmer necessary for query and document processing
   */
  KrovetzStemmer stemmer;


  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    System.out.println("ListQuestionEntityExtractor_AE - initialize()");
    stemmer = new KrovetzStemmer();
  }


  /**
   * @param aJcas
   *         Assumed to contain questions provided by QuestionReader
   *          
   * Identify pivot terms in the query, using unigram heuristics;
   *         
   * Locate pivot terms in abstract, extract text in a window of fixed size.;
   *         
   * Run BioNER on the extracted text.;
   *         
   * Limit to unigram entities for the moment.;
   *         
   * Wipeout recognized entities with unigram heuristics.
   * 
   * IMPORTANT:
   * 
   * Assuming only one question in CAS
   * 
   * Assuming zero or multiple ranked documents
   * 
   * Assuming abstract is always available
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    System.out.println(">>> ListQuestionEntityExtractor_AE.process()");

    // We are only interested in List type here
    String questionType = null;

    // Need the raw text of the question
    String questionText = null;
    List<Pair<String, String>> docID_AbsText_List = new ArrayList<Pair<String, String>>();

    // Reads in EXACTLY ONE question from index
    FSIterator<Annotation> iter = aJCas.getAnnotationIndex().iterator();
    if (iter.isValid()) {
      iter.moveToNext();
      Question question = (Question) iter.get();
      questionText = question.getText().replace('?', ' ');
      questionText = QueryExpander.expandQuery(questionText, stemmer);
      questionType = question.getQuestionType();
      if(!questionType.equals("LIST")) {
        return;
      }
    }

    System.out.println(questionType+": " + questionText);

    // In the following we will collect document IDs and their abstracts
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

    // This stores the abstract in String[]
    Map<String, String[]> absMap = new HashMap<String, String[]>();

    // Convert the abstract texts in to formatted String[]'s
    for(Pair<String, String> p: docID_AbsText_List) {
      String docID = p.getKey();
      String absText = p.getValue();
      String[] absTermArr = TextProcessingTools.getFormattedTermArray(absText, null);
      absMap.put(docID, absTermArr);

    }
    
    // This implments the core algorithm
    ListNER_Analyser anl = new ListNER_Analyser(questionText, absMap);

    // Ranking the answers
    int rank = 1;
    List<Entry<String, Double>>  result = anl.getNEList(100);
    for(Entry<String, Double> e: result) {
      //System.out.printf("ADD **** %s\t%f\n", e.getKey(), e.getValue());
      TypeFactory.createAnswer(aJCas, e.getKey(), new ArrayList<String>(), rank++).addToIndexes(aJCas);
    }

  }

  @Override
  public void collectionProcessComplete() throws AnalysisEngineProcessException {
    System.out.println("ListQuestionEntityExtractor_AE - collectionProcessComplete()");
  }


}
