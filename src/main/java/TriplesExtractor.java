import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.http.client.ClientProtocolException;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;

import util.TypeConstants;
import util.TypeFactory;
import edu.cmu.lti.oaqa.bio.bioasq.services.GoPubMedService;
import edu.cmu.lti.oaqa.bio.bioasq.services.LinkedLifeDataServiceResponse;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.kb.Triple;

public class TriplesExtractor extends JCasAnnotator_ImplBase {

  private static GoPubMedService service = null;

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    try {
      service = new GoPubMedService("project.properties");
    } catch (ConfigurationException e) {
      System.out.println("ConfigurationException occurred: " + e.getMessage());
    }
    processInstance(aJCas);
  }

  private void processInstance(JCas aJCas) throws AnalysisEngineProcessException {
    System.out.println("In Triples extractor!");
    FSIterator<?> qit = aJCas.getAnnotationIndex(Question.type).iterator();

    Question question = null;
    while (qit.hasNext()) {
      question = (Question) qit.next();
      System.out.println(question.getText());
      System.out.println(question.getId());
      System.out.println(question.getQuestionType());
      try {
        ArrayList<HashMap<String, String>> triplesList = fetchTriples(question.getText());
        int rank = 1;
        for (HashMap<String, String> t : triplesList) {
          /*System.out.println("Predicate: " + t.get("PRED"));
          System.out.println("Subject: " + t.get("SUB"));
          System.out.println("Object: " + t.get("OBJ"));
          System.out.println("Score:" + t.get("SCORE"));*/
          Triple triple = TypeFactory
                  .createTriple(aJCas, t.get("SUB"), t.get("PRED"), t.get("OBJ"));
          TypeFactory.createTripleSearchResult(aJCas, triple, TypeConstants.URI_UNKNOWN,
                  Double.parseDouble(t.get("SCORE")), TypeConstants.TEXT_UNKNOWN,
                  rank++, question.getText(), TypeConstants.SEARCH_ID_UNKNOWN,
                  new ArrayList<>());
          triple.addToIndexes();
        }
      } catch (IOException e) {
        System.out.println("IOException occurred: " + e.getMessage());
      } catch (Exception e) {
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
    LinkedLifeDataServiceResponse.Result linkedLifeDataResult = service
            .findLinkedLifeDataEntitiesPaged(text, 0);
    // System.out.println("LinkedLifeData: " + linkedLifeDataResult.getEntities().size());
    for (LinkedLifeDataServiceResponse.Entity entity : linkedLifeDataResult.getEntities()) {
      // LinkedLifeDataServiceResponse.Entity entity = linkedLifeDataResult.getEntities().get(0);
      // System.out.println(" 6> " + entity.getEntity());
      Double score = entity.getScore();
      // System.out.println("Score: " + entity.getScore());
      // for (LinkedLifeDataServiceResponse.Relation relation : entity.getRelations()) {
      LinkedLifeDataServiceResponse.Relation relation = entity.getRelations().get(0);
      HashMap<String, String> t = new HashMap<String, String>();
      t.put("PRED", relation.getPred());
      t.put("SUB", relation.getSubj());
      t.put("OBJ", relation.getObj());
      t.put("SCORE", score.toString());
      triples.add(t);
      // }
    }
    return triples;
  }

}
