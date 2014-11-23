package util;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import edu.cmu.lti.oaqa.bio.bioasq.services.*;

/**
 * 
 * @author nwolfe Proxy for the GoPubMed service... handles calls to all the necessary APIs and just
 *         returns the results to you.
 */
public class GoPubMedServiceProxy {

  private GoPubMedService service;

  private boolean FULL_TEXT_ONLY = false;

  public GoPubMedServiceProxy() {
    this.service = GoPubMedServiceFactory.getInstance();
  }

  /**
   * @param query
   * @return a list of Finding objects based on a query string
   */
  public List<OntologyServiceResponse.Finding> getFindingsFromQuery(String query) {
    List<OntologyServiceResponse.Finding> findings = new LinkedList<OntologyServiceResponse.Finding>();
    try {
      OntologyServiceResponse.Result diseaseOntologyResult = service
              .findDiseaseOntologyEntitiesPaged(query, 0);
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
      System.out.println("ClientProtocolException occurred! " + e.getMessage());
    } catch (Exception e) {
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
    List<LinkedLifeDataServiceResponse.Entity> entities = new LinkedList<LinkedLifeDataServiceResponse.Entity>();
    LinkedLifeDataServiceResponse.Result linkedLifeDataResult;
    try {
      linkedLifeDataResult = service.findLinkedLifeDataEntitiesPaged(query, 0);
      System.out.println("LinkedLifeData: " + linkedLifeDataResult.getEntities().size());
      entities.addAll(linkedLifeDataResult.getEntities());
    } catch (ClientProtocolException e) {
      System.out.println("ClientProtocolException occurred! " + e.getMessage());
    } catch (Exception e) {
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
    List<PubMedSearchServiceResponse.Document> docs = null;
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
    } catch (ClientProtocolException e) {
      System.out.println("ClientProtocolException occurred! " + e.getMessage());
    } catch (Exception e) {
      System.out.println("Exception occurred! " + e.getMessage());
    }
    return docs;
  }

}
