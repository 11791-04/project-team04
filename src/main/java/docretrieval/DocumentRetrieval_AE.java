package docretrieval;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

import util.TypeFactory;
import docretrieval.stemmer.KrovetzStemmer;
import edu.cmu.lti.oaqa.bio.bioasq.services.GoPubMedService;
import edu.cmu.lti.oaqa.bio.bioasq.services.PubMedSearchServiceResponse;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.retrieval.Document;


public class DocumentRetrieval_AE extends JCasAnnotator_ImplBase {
  GoPubMedService service;
  KrovetzStemmer stemmer;
  boolean baseline = true;

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
    //    System.out.println(aJCas.getDocumentText());
    CollectionStatistics cStat = new CollectionStatistics();

    FSIterator<Annotation> iter = aJCas.getAnnotationIndex().iterator();
    if (iter.isValid()) {
      iter.moveToNext();
      Question question = (Question) iter.get();

      QueryInfo query = new QueryInfo(question.getText(), stemmer);

      //      System.out.println("Question: "+question.getText());

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


      List<Pair<DocInfo, Double>> docScoreList = new ArrayList<Pair<DocInfo, Double>>();
      int defaultRank = 1;
      for(DocInfo doc: cStat.docList) {

        double score = Ranker.scoreDoc(Ranker.RANKER_OKAPI, cStat, doc, query);
        if(baseline) {
          score = defaultRank++;
        }
        docScoreList.add(new Pair<DocInfo, Double>(doc, score));
      }

      Collections.sort(docScoreList, new DocScoreComparator());

      int rank = 1;
      for(Pair<DocInfo, Double> p: docScoreList) {
        //        System.out.println(p.getValue());
        Document d = TypeFactory.createDocument(aJCas, 
                "http://www.ncbi.nlm.nih.gov/pubmed/"+p.getKey().pmid,
                p.getKey().fieldTextMap.get("abstract"),
                rank, query.text, p.getKey().fieldTextMap.get("title"), p.getKey().pmid);
        d.addToIndexes();
        rank++;
      }
      //      Document docRetr = new Document(aJCas);
      //      docRetr.setUri("KKK");
      //      docRetr.setRank(-1);
      //      docRetr.addToIndexes();
      //      
      //      aJCas.addFsToIndexes(docRetr);
    }
  }

  @Override
  public void collectionProcessComplete() throws AnalysisEngineProcessException {
    System.out.println("DocumentRetrieval_AE - collectionProcessComplete()");

  }

  public static class DocScoreComparator implements Comparator<Pair<DocInfo, Double>>{

    @Override
    public int compare(Pair<DocInfo, Double> a, Pair<DocInfo, Double> b) {
      return -a.getValue().compareTo(b.getValue());
    }

  }

}
