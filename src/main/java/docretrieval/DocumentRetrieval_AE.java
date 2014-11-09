package docretrieval;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.http.client.ClientProtocolException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import docretrieval.stemmer.KrovetzStemmer;
import edu.cmu.lti.oaqa.bio.bioasq.services.GoPubMedService;
import edu.cmu.lti.oaqa.bio.bioasq.services.PubMedSearchServiceResponse;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.retrieval.Document;


public class DocumentRetrieval_AE extends JCasAnnotator_ImplBase {
  GoPubMedService service;
  KrovetzStemmer stemmer;
  
  
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    System.out.println("DocumentRetrieval_AE - initialize()");
    try {
      service = new GoPubMedService("project.properties");
    } catch (ConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    stemmer = new KrovetzStemmer();
  
    
  }

  
  /**
   * @param aJcas Assumed to contain questions provided by QuestionReader
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    System.out.println(aJCas.getDocumentText());
    CollectionStatistics cStat = new CollectionStatistics();
    
    FSIterator<Annotation> iter = aJCas.getAnnotationIndex().iterator();
    if (iter.isValid()) {
      iter.moveToNext();
      Question question = (Question) iter.get();

      System.out.println("Question: "+question.getText());

      String questionText = question.getText().replace('?', ' ');

      PubMedSearchServiceResponse.Result pubmedResult = null;

      try {
        pubmedResult = service.findPubMedCitations(questionText, 0);
      } catch (ClientProtocolException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
      List<PubMedSearchServiceResponse.Document> list =  pubmedResult.getDocuments();

      for(PubMedSearchServiceResponse.Document d: list) {
        System.out.println("PMID: "+d.getPmid());
        
        String title = d.getTitle();
        String abstractText = d.getDocumentAbstract();
        String year = d.getYear();
        String pmid = d.getPmid();
        
        Map<String, String> fieldTextMap = new HashMap<String, String>();
        fieldTextMap.put("title", title);
        fieldTextMap.put("abstract", abstractText);
        
        DocInfo docInfo = new DocInfo(pmid, fieldTextMap, year, stemmer);
        cStat.addDoc(docInfo);  // Update collection statistics
      }
      cStat.finalize();
      // At this point, we have finished collecting all candidate documents 
      // and constructed the collection statistics
      

      System.out.println(pubmedResult.getSize());
      
      Document docRetr = new Document(aJCas);
      docRetr.setUri("KKK");
      docRetr.setRank(-1);
      docRetr.addToIndexes();
      
      aJCas.addFsToIndexes(docRetr);
    }
  }

  @Override
  public void collectionProcessComplete() throws AnalysisEngineProcessException {
    System.out.println("DocumentRetrieval_AE - collectionProcessComplete()");
  
    
    
    
  }

}
