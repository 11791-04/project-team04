import java.io.IOException;
import java.util.ArrayList;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import util.TypeConstants;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.retrieval.ConceptSearchResult;
import edu.cmu.lti.oaqa.type.retrieval.Document;
import edu.cmu.lti.oaqa.type.retrieval.Passage;
import edu.cmu.lti.oaqa.type.retrieval.TripleSearchResult;
import edu.stanford.nlp.io.EncodingPrintWriter.out;

import edu.cmu.lti.oaqa.type.kb.Triple;
import edu.cmu.lti.oaqa.type.retrieval.TripleSearchResult;
import edu.stanford.nlp.io.EncodingPrintWriter.out;

public class BasicConsumer extends CasConsumer_ImplBase {

  Metric documentMetric;

  Metric conceptMetric;

  Metric tripleMetric;

  public void initialize() throws ResourceInitializationException {
    documentMetric = new Metric("document");
    conceptMetric = new Metric("concept");
    tripleMetric = new Metric("triple");
  }

  private String triple2String(TripleSearchResult tsr) {
    Triple triple = tsr.getTriple();
    return "<s>" + triple.getSubject() + "</s><o>" + triple.getObject() + "</o><p>"
            + triple.getPredicate() + "</p>";
  }

  @Override
  public void processCas(CAS aCas) throws ResourceProcessException {
    JCas aJCas;
    try {
      aJCas = aCas.getJCas();
    } catch (CASException e) {
      throw new ResourceProcessException(e);
    }

    System.out.println(aJCas);

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

//    documents.sort((p, o) -> p.getRank().compareTo(o.getRank()));
    
    documentMetric.registerAnswerAndGoldStandard(documents, gsDocuments);
    conceptMetric.registerAnswerAndGoldStandard(concepts, gsConcepts);
    tripleMetric.registerAnswerAndGoldStandard(triples, gsTriples);

  }

  @Override
  public void collectionProcessComplete(ProcessTrace arg0) throws ResourceProcessException,
          IOException {

    System.out.println("RESULTS doc");
    System.out.println(documentMetric.getCurrentMAP());
    System.out.println("RESULTS concept");
    System.out.println(conceptMetric.getCurrentMAP());
    System.out.println("RESULTS triples");
    System.out.println(tripleMetric.getCurrentMAP());
    
    System.out.println("RESULTS doc");
    System.out.println(documentMetric.getCurrentGMAP(0.01));
    System.out.println("RESULTS concept");
    System.out.println(conceptMetric.getCurrentGMAP(0.01));
    System.out.println("RESULTS triples");
    System.out.println(tripleMetric.getCurrentGMAP(0.01));
  }

}
