import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;

import docretrieval.CollectionStatistics;
import docretrieval.DocInfo;
import docretrieval.stemmer.KrovetzStemmer;
import edu.cmu.lti.oaqa.type.retrieval.Document;

import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.json.JSONArray;
import org.json.JSONObject;

import snippetextraction.SentenceInfo;
import util.MetalWebService;
import util.TextUtils;
import util.TypeConstants;

public class SnippetAnalysisEngine extends JCasAnnotator_ImplBase {

  KrovetzStemmer stemmer;
  
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    // TODO Auto-generated method stub
    super.initialize(aContext);
    stemmer = new KrovetzStemmer();
  }

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    CollectionStatistics cStat = new CollectionStatistics();
    ArrayList<SentenceInfo> allSentences = new ArrayList<SentenceInfo>();
    try {
      FSIterator<?> it;
      it = aJCas.getFSIndexRepository().getAllIndexedFS(
              aJCas.getRequiredType("edu.cmu.lti.oaqa.type.retrieval.Document"));
      while (it.hasNext()) {
        Document doc = (Document) it.next();
        if (doc.getSearchId() != null
                && doc.getSearchId().equals(TypeConstants.SEARCH_ID_GOLD_STANDARD)) {
          continue;
        }
        try {
	        String pmid = doc.getDocId();
	        JSONObject docFull = MetalWebService.getDocFullTextJSon(pmid);
			    JSONArray sectionArr = docFull.getJSONArray("sections");
			    System.out.println(sectionArr.length());
			    
	        Map<String, String> fieldTextMap = new HashMap<String, String>();
	        fieldTextMap.put("title", doc.getTitle());
			    for(int i=0; i<sectionArr.length(); i++) {
		        fieldTextMap.put("section:" + i, (String) sectionArr.get(i));
			    }
	        DocInfo docInfo = new DocInfo(pmid, fieldTextMap, null, stemmer);
	        cStat.addDoc(docInfo);
			    
			    for(int i=0; i<sectionArr.length(); i++) {
			      String section = (String) sectionArr.get(i);
			      List<SentenceInfo> sentences = TextUtils.stanfordSentenceTokenizer(section);
			      for(SentenceInfo sentence : sentences) {
			        sentence.hostDoc = docInfo;
			        sentence.sectionIndex = i;
			        allSentences.add(sentence);
			        System.out.println(sentence);
			      }
		      }
        } catch (Exception e) {
		      // TODO Auto-generated catch block
        }
      }
      cStat.finalize();
    } catch (CASException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  @Override
  public void collectionProcessComplete() throws AnalysisEngineProcessException {
    // TODO Auto-generated method stub
    super.collectionProcessComplete();
  }

}
