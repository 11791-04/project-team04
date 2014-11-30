package util.webservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONObject;

import edu.cmu.lti.oaqa.bio.bioasq.services.GoPubMedService;
import edu.cmu.lti.oaqa.bio.bioasq.services.LinkedLifeDataServiceResponse;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse;
import edu.cmu.lti.oaqa.bio.bioasq.services.PubMedSearchServiceResponse;

/**
 * 
 * @author nwolfe Proxy for the GoPubMed service... handles calls to all the necessary APIs and just
 *         returns the results to you.
 */
public class WebAPIServiceProxy {

  private GoPubMedService service;

  private MetalWebService metal;

  private final Pattern punc = Pattern.compile("\\p{Punct}+");

  private final Pattern wsp = Pattern.compile("\\p{Space}+");

  private boolean FULL_TEXT_ONLY = false;

  public WebAPIServiceProxy() {
    this.service = GoPubMedServiceFactory.getInstance();
    this.metal = new MetalWebService();
  }

  protected String cleanString(String s, String replaceSpacesWith) {
    Matcher m = punc.matcher(s);
    s = m.replaceAll("");
    m = wsp.matcher(s);
    s = m.replaceAll(replaceSpacesWith);
    return s.trim();
  }

  /**
   * @param query
   * @return a list of Finding objects based on a query string
   */
  public List<OntologyServiceResponse.Finding> getFindingsFromQuery(String query) {
    List<OntologyServiceResponse.Finding> findings = new ArrayList<OntologyServiceResponse.Finding>();
    try {
      OntologyServiceResponse.Result diseaseOntologyResult = service
              .findDiseaseOntologyEntitiesPaged(query,0);
      System.out.println("Disease ontology: " + diseaseOntologyResult.getFindings().size());
      findings.addAll(diseaseOntologyResult.getFindings());

      OntologyServiceResponse.Result geneOntologyResult = service.findGeneOntologyEntitiesPaged(
              query, 0, 10);
      System.out.println("Gene ontology: " + geneOntologyResult.getFindings().size());
      findings.addAll(geneOntologyResult.getFindings());

      OntologyServiceResponse.Result jochemResult = service.findJochemEntitiesPaged(query, 0);
      System.out.println("Jochem: " + jochemResult.getFindings().size());
      findings.addAll(jochemResult.getFindings());

      OntologyServiceResponse.Result meshResult = service.findMeshEntitiesPaged(query, 0);
      System.out.println("MeSH: " + meshResult.getFindings().size());
      findings.addAll(meshResult.getFindings());

      OntologyServiceResponse.Result uniprotResult = service.findUniprotEntitiesPaged(query, 0);
      System.out.println("UniProt: " + uniprotResult.getFindings().size());
      findings.addAll(uniprotResult.getFindings());

    } catch (ClientProtocolException e) {
      e.printStackTrace();
      System.out.println("ClientProtocolException occurred! " + e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Exception occurred! " + e.getMessage());
    }
    return findings;
  }

  /**
   * 
   * @param query
   * @return a list of Entities from query string
   */
  public List<LinkedLifeDataServiceResponse.Entity> getEntitiesFromQuery(String query) {
    List<LinkedLifeDataServiceResponse.Entity> entities = new ArrayList<LinkedLifeDataServiceResponse.Entity>();
    LinkedLifeDataServiceResponse.Result linkedLifeDataResult;
    try {
      linkedLifeDataResult = service.findLinkedLifeDataEntitiesPaged(query, 0);
      System.out.println("LinkedLifeData: " + linkedLifeDataResult.getEntities().size());
      entities.addAll(linkedLifeDataResult.getEntities());
    } catch (ClientProtocolException e) {
      e.printStackTrace();
      System.out.println("ClientProtocolException occurred! " + e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Exception occurred! " + e.getMessage());
    }
    return entities;
  }

  /**
   * 
   * @param query
   * @return a list of documents from query string
   */
  public List<PubMedSearchServiceResponse.Document> getPubMedDocumentsFromQuery(String query) {
    List<PubMedSearchServiceResponse.Document> docs = new ArrayList<PubMedSearchServiceResponse.Document>();
    PubMedSearchServiceResponse.Result pubmedResult = null;
    try {
      pubmedResult = service.findPubMedCitations(query, 0);
      System.out.println("Documents: " + pubmedResult.getSize());
      docs = pubmedResult.getDocuments();
      if (FULL_TEXT_ONLY) {
        for (PubMedSearchServiceResponse.Document doc : docs) {
          if (!doc.isFulltextAvailable()) {
            docs.remove(doc);
          }
        }
      }
      return docs;
    } catch (ClientProtocolException e) {
      e.printStackTrace();
      System.out.println("ClientProtocolException occurred! " + e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Exception occurred! " + e.getMessage());
    }
    return docs;
  }

  public JSONObject getDocFullTextJSon(String pmid) {
    return metal.getDocFullTextJSon(pmid);
  }
  
  /**
   * Fetches the triples for a given question text
   * 
   * @param text
   * @return
   * @throws ClientProtocolException
   * @throws IOException
   */
  public ArrayList<HashMap<String, String>> fetchTriples(String text)
          throws ClientProtocolException, IOException {
    ArrayList<HashMap<String, String>> triples = new ArrayList<HashMap<String, String>>();
    List<LinkedLifeDataServiceResponse.Entity> searchResult = getEntitiesFromQuery(text);
    for (LinkedLifeDataServiceResponse.Entity entity : searchResult) {
      Double score = entity.getScore();
      LinkedLifeDataServiceResponse.Relation relation = entity.getRelations().get(0);
      HashMap<String, String> t = new HashMap<String, String>();
      t.put("PRED", relation.getPred());
      t.put("SUB", relation.getSubj());
      t.put("OBJ", relation.getObj());
      t.put("SCORE", score.toString());
      triples.add(t);
    }
    return triples;
  }

}
