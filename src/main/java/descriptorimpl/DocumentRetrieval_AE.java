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

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import util.QueryExpander;
import util.TypeFactory;
import util.datastructure.Pair;
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

public class DocumentRetrieval_AE extends JCasAnnotator_ImplBase {

  WebAPIServiceProxy service;

  KrovetzStemmer stemmer;

  private PrintWriter outQuestions;

  boolean baseline = false;

  Set<String> conceptSet;

  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    System.out.println("DocumentRetrieval_AE - initialize()");
    service = WebAPIServiceProxyFactory.getInstance();

    // service = new WebAPIServiceProxy();
    stemmer = new KrovetzStemmer();

    try {
      outQuestions = new PrintWriter(new FileOutputStream(new File("questions.txt"), false));
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    conceptSet = new HashSet<String>();

    try {
      Scanner in = new Scanner(new File("concepts.evil"));

      while (in.hasNextLine()) {
        String l = in.nextLine().trim();
        if (l.equals("")) {
          continue;
        }
        conceptSet.add(l);
        System.out.println("===" + l);
      }

      in.close();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  public String qeWithConcept(String raw) {
    for (String c : conceptSet) {
      if (raw.contains(c)) {
        raw = raw.replace(c, c + " [mesh] ");
      }
    }
    return raw;
  }

  /**
   * @param aJcas
   *          Assumed to contain questions provided by QuestionReader
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    // System.out.println(aJCas.getDocumentText());
    CollectionStatistics cStat = new CollectionStatistics();

    FSIterator<Annotation> iter = aJCas.getAnnotationIndex().iterator();
    if (iter.isValid()) {
      iter.moveToNext();
      Question question = (Question) iter.get();
      QueryInfo query = new QueryInfo(question.getText(), stemmer);
      String questionText = question.getText().replace('?', ' ');
      //questionText = QueryExpander.expandQuery(questionText, stemmer);
      questionText = qeWithConcept(questionText);
      System.out.println("###: " + questionText);
      outQuestions.println(questionText);

      List<PubMedSearchServiceResponse.Document> list = service
              .getPubMedDocumentsFromQuery(questionText);

      for (PubMedSearchServiceResponse.Document d : list) {

        String title = d.getTitle();
        String abstractText = d.getDocumentAbstract();
        String year = d.getYear();
        String pmid = d.getPmid();
        System.out.println("PMID2:" + d.getPmid());

        Map<String, String> fieldTextMap = new HashMap<String, String>();
        fieldTextMap.put("title", title);
        fieldTextMap.put("abstract", abstractText);

        if (title != null && abstractText != null && pmid != null && fieldTextMap != null && year != null) {
          DocInfo docInfo = new DocInfo("http://www.ncbi.nlm.nih.gov/pubmed/" + pmid, pmid,
                  fieldTextMap, year, stemmer);
          cStat.addDoc(docInfo); // Update collection statistics
        }
      }
      cStat.finalize();
      // At this point, we have finished collecting all candidate documents
      // and constructed the collection statistics

      List<Pair<DocInfo, Double>> docScoreList = new ArrayList<Pair<DocInfo, Double>>();
      int defaultRank = 1;
      for (DocInfo doc : cStat.docList) {

        double score = Ranker.scoreDoc(Ranker.RANKER_INDRI, cStat, doc, query);
        if (baseline) {
          score = defaultRank++;
        }
        docScoreList.add(new Pair<DocInfo, Double>(doc, score));
      }

      Collections.sort(docScoreList, new DocScoreComparator());

      int rank = 1;
      for (Pair<DocInfo, Double> p : docScoreList) {
        // System.out.println(p.getValue());
        Document d = TypeFactory.createDocument(aJCas,
                "http://www.ncbi.nlm.nih.gov/pubmed/" + p.getKey().pmid,
                p.getKey().fieldTextMap.get("abstract"), rank, query.text,
                p.getKey().fieldTextMap.get("title"), p.getKey().pmid);
        d.addToIndexes();
        rank++;
      }
      // Document docRetr = new Document(aJCas);
      // docRetr.setUri("KKK");
      // docRetr.setRank(-1);
      // docRetr.addToIndexes();
      //
      // aJCas.addFsToIndexes(docRetr);
    }
  }

  @Override
  public void collectionProcessComplete() throws AnalysisEngineProcessException {
    System.out.println("DocumentRetrieval_AE - collectionProcessComplete()");
    outQuestions.close();
  }

  public static class DocScoreComparator implements Comparator<Pair<DocInfo, Double>> {

    @Override
    public int compare(Pair<DocInfo, Double> a, Pair<DocInfo, Double> b) {
      return -a.getValue().compareTo(b.getValue());
    }

  }

}
