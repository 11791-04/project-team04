package descriptorimpl;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import metric.MetricDTC;
import metric.MetricSnippet;
import metric.MetricTriples;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import snippet.SentenceInfo;
import util.TypeConstants;
import document.DocInfo;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.kb.Triple;
import edu.cmu.lti.oaqa.type.retrieval.ConceptSearchResult;
import edu.cmu.lti.oaqa.type.retrieval.Document;
import edu.cmu.lti.oaqa.type.retrieval.Passage;
import edu.cmu.lti.oaqa.type.retrieval.TripleSearchResult;

public class BasicConsumer extends CasConsumer_ImplBase {
  
  MetricDTC documentMetric;

  MetricDTC conceptMetric;

  MetricTriples tripleMetric;

  MetricSnippet snippetMetric;
  
  public void initialize() throws ResourceInitializationException {
    documentMetric = new MetricDTC("document");
    conceptMetric = new MetricDTC("concept");
    tripleMetric = new MetricTriples("triple");
    snippetMetric = new MetricSnippet("snippet");
  }

  private String triple2String(TripleSearchResult tsr) {
    Triple triple = tsr.getTriple();
    return triple.getSubject() + ":delim:" + triple.getObject() + ":delim:"
            + triple.getPredicate();
  }
  
  private SentenceInfo passage2sentence(Passage passage) {
    return new SentenceInfo(passage.getText(), passage.getBeginSection(), passage.getOffsetInBeginSection(), passage.getOffsetInEndSection(), 
            new DocInfo(passage.getUri(), passage.getDocId(), new HashMap<String, String>(), null, null), passage.getScore());
  }

  @Override
  public void processCas(CAS aCas) throws ResourceProcessException {
    JCas aJCas;
    try {
      aJCas = aCas.getJCas();
    } catch (CASException e) {
      throw new ResourceProcessException(e);
    }

    //System.out.println(aJCas);

    FSIterator<?> qit = aJCas.getAnnotationIndex(Question.type).iterator();
    Question question = null;
    if (qit.hasNext()) {
      question = (Question) qit.next();
    }

    ArrayList<String> documents = new ArrayList<String>();
    ArrayList<String> gsDocuments = new ArrayList<String>();

    ArrayList<String> concepts = new ArrayList<String>();
    ArrayList<String> gsConcepts = new ArrayList<String>();

    ArrayList<String> triples = new ArrayList<String>();
    ArrayList<String> gsTriples = new ArrayList<String>();

    ArrayList<SentenceInfo> snippets = new ArrayList<SentenceInfo>();
    ArrayList<SentenceInfo> gsSnippets = new ArrayList<SentenceInfo>();

    try {
      FSIterator<?> it;
      it = aJCas.getFSIndexRepository().getAllIndexedFS(
              aJCas.getRequiredType("edu.cmu.lti.oaqa.type.retrieval.Document"));
      while (it.hasNext()) {
        Document doc = (Document) it.next();
        if (doc.getSearchId() != null
                && doc.getSearchId().equals(TypeConstants.SEARCH_ID_GOLD_STANDARD)) {
          gsDocuments.add(doc.getUri());
        } else {
          documents.add(doc.getUri());
        }
      }
    } catch (CASException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }


    try {
      FSIterator<?> it;
      it = aJCas.getFSIndexRepository().getAllIndexedFS(
              aJCas.getRequiredType("edu.cmu.lti.oaqa.type.retrieval.ConceptSearchResult"));

      while (it.hasNext()) {
        ConceptSearchResult concept = (ConceptSearchResult) it.next();
        if (concept.getSearchId() != null
                && concept.getSearchId().equals(TypeConstants.SEARCH_ID_GOLD_STANDARD)) {
          gsConcepts.add(concept.getUri());
        } else {
          concepts.add(concept.getUri());
        }
      }
    } catch (CASException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    try {
      FSIterator<?> it;
      it = aJCas.getFSIndexRepository().getAllIndexedFS(
              aJCas.getRequiredType("edu.cmu.lti.oaqa.type.retrieval.TripleSearchResult"));

      while (it.hasNext()) {
        TripleSearchResult triple = (TripleSearchResult) it.next();
        String tripleString = triple2String(triple);
        if (triple.getSearchId() != null
                && triple.getSearchId().equals(TypeConstants.SEARCH_ID_GOLD_STANDARD)) {
          gsTriples.add(tripleString);
        } else {
          triples.add(tripleString);
        }
      }
    } catch (CASException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    

    try {
      FSIterator<?> it;
      it = aJCas.getFSIndexRepository().getAllIndexedFS(
              aJCas.getRequiredType("edu.cmu.lti.oaqa.type.retrieval.Passage"));

      while (it.hasNext()) {
        Passage passage = (Passage) it.next();
        SentenceInfo snippet = passage2sentence(passage);
        if (passage.getSearchId() != null
                && passage.getSearchId().equals(TypeConstants.SEARCH_ID_GOLD_STANDARD)) {
          gsSnippets.add(snippet);
        } else {
          snippets.add(snippet);
        }
      }
    } catch (CASException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
//    System.out.println("gs snippet:\n" + gsSnippets);
//    System.out.println("snippet:\n" + snippets);

//    documents.sort((p, o) -> p.getRank().compareTo(o.getRank()));
    
    documentMetric.registerAnswerAndGoldStandard(documents, gsDocuments);
    conceptMetric.registerAnswerAndGoldStandard(concepts, gsConcepts);
    tripleMetric.registerAnswerAndGoldStandard(triples, gsTriples);
    snippetMetric.registerAnswerAndGoldStandard(snippets, gsSnippets);
    
// TODO
//    snippetMetric.registerAnswerAndGoldStandard(snippets, gsSnippets);

  }

  @Override
  public void collectionProcessComplete(ProcessTrace arg0) throws ResourceProcessException,
          IOException {

    System.out.println("RESULTS doc");
    System.out.println(documentMetric.getCurrentMAP());
    System.out.println("RESULTS concept");
    System.out.println(conceptMetric.getCurrentMAP());
    System.out.println("RESULTS triples");
    System.out.println(tripleMetric.getMAPForTriples());
 
    System.out.println("RESULTS doc");
    System.out.println(documentMetric.getCurrentGMAP(0.01));
    System.out.println("RESULTS concept");
    System.out.println(conceptMetric.getCurrentGMAP(0.01));
    System.out.println("RESULTS triples");
    System.out.println(tripleMetric.getCurrentGMAPForTriples(0.01));
    
    
    System.out.println(snippetMetric.list_rankList.size());
    System.out.println("RESULT SNIPPETS:");
    System.out.println(snippetMetric.getCurrentF1());
  }

}
