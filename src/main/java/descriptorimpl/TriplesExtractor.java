package descriptorimpl;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;

import util.TypeConstants;
import util.TypeFactory;
import webservice.CachedWebAPIServiceProxy;
import webservice.WebAPIServiceProxy;
import webservice.WebAPIServiceProxyFactory;
import edu.cmu.lti.oaqa.bio.bioasq.services.LinkedLifeDataServiceResponse;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.kb.Triple;
import edu.cmu.lti.oaqa.type.retrieval.TripleSearchResult;

/**
 * 
 * Analysis engine for extracting triples.
 * @author pyadapad
 *
 */
public class TriplesExtractor extends JCasAnnotator_ImplBase {

  private static WebAPIServiceProxy service = null;

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    service = WebAPIServiceProxyFactory.getInstance();
    processInstance(aJCas);
  }

  private void processInstance(JCas aJCas) throws AnalysisEngineProcessException {
    System.out.println("In Triples extractor!");
    FSIterator<?> qit = aJCas.getAnnotationIndex(Question.type).iterator();

    Question question = null;
    while (qit.hasNext()) {
      question = (Question) qit.next();
      System.out.println(question.getId());
      System.out.println(question.getQuestionType());
      try {
        ArrayList<HashMap<String, String>> triplesList = fetchTriples(question.getText());
        int rank = 1;
        for (HashMap<String, String> t : triplesList) {
          Triple triple = TypeFactory
                  .createTriple(aJCas, t.get("SUB"), t.get("PRED"), t.get("OBJ"));
          TripleSearchResult tsr = TypeFactory.createTripleSearchResult(aJCas, triple,
                  TypeConstants.URI_UNKNOWN, Double.parseDouble(t.get("SCORE")),
                  TypeConstants.TEXT_UNKNOWN, rank++, question.getText(),
                  TypeConstants.SEARCH_ID_UNKNOWN, new ArrayList<>());
          tsr.addToIndexes();
        }
      } catch (IOException e) {
        e.printStackTrace();
        System.out.println("IOException occurred: " + e.getMessage());
      } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Exception occurred: " + e.getMessage());
      }
    }

  }

  /**
   * Fetches the triples for a given question text
   * 
   * @param text
   * @return
   * @throws ClientProtocolException
   * @throws IOException
   */
  public static ArrayList<HashMap<String, String>> fetchTriples(String text)
          throws ClientProtocolException, IOException {
    ArrayList<HashMap<String, String>> triples = new ArrayList<HashMap<String, String>>();
    List<LinkedLifeDataServiceResponse.Entity> searchResult = service.getEntitiesFromQuery(text);
    for (LinkedLifeDataServiceResponse.Entity entity : searchResult) {
      Double score = entity.getScore();
      for (LinkedLifeDataServiceResponse.Relation relation : entity.getRelations()) {
      HashMap<String, String> t = new HashMap<String, String>();
      t.put("PRED", relation.getPred());
      t.put("SUB", relation.getSubj());
      t.put("OBJ", relation.getObj());
      t.put("SCORE", score.toString());
      triples.add(t);
      }
    }
    return triples;
  }

}
