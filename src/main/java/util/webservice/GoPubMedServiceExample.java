package util.webservice;

import java.io.IOException;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.http.client.ClientProtocolException;

import edu.cmu.lti.oaqa.bio.bioasq.services.GoPubMedService;
import edu.cmu.lti.oaqa.bio.bioasq.services.LinkedLifeDataServiceResponse;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse;
import edu.cmu.lti.oaqa.bio.bioasq.services.PubMedSearchServiceResponse;

public class GoPubMedServiceExample {

  public static void main(String[] args) throws ClientProtocolException, IOException,
          ConfigurationException {
     String text = "In which isochores are Alu elements enriched?";
//    String text = "Are there any DNMT3 proteins present in plants?";
    GoPubMedService service = new GoPubMedService("project.properties");
    OntologyServiceResponse.Result diseaseOntologyResult = service
            .findDiseaseOntologyEntitiesPaged(text, 0);
    /*System.out.println("Disease ontology: " + diseaseOntologyResult.getFindings().size());
    for (OntologyServiceResponse.Finding finding : diseaseOntologyResult.getFindings()) {
      System.out.println(" 1> " + finding.getConcept().getLabel() + " "
              + finding.getConcept().getUri() + " " + finding.getScore());
    }
    OntologyServiceResponse.Result geneOntologyResult = service.findGeneOntologyEntitiesPaged(text,
            0, 10);
    System.out.println("Gene ontology: " + geneOntologyResult.getFindings().size());
    for (OntologyServiceResponse.Finding finding : geneOntologyResult.getFindings()) {
      System.out.println(" 2> " + finding.getConcept().getLabel() + " "
              + finding.getConcept().getUri() + " " + finding.getScore());
    }
    OntologyServiceResponse.Result jochemResult = service.findJochemEntitiesPaged(text, 0);
    System.out.println("Jochem: " + jochemResult.getFindings().size());
    for (OntologyServiceResponse.Finding finding : jochemResult.getFindings()) {
      System.out.println(" 3> " + finding.getConcept().getLabel() + " "
              + finding.getConcept().getUri() + " " + finding.getScore());
    }
    OntologyServiceResponse.Result meshResult = service.findMeshEntitiesPaged(text, 0);
    System.out.println("MeSH: " + meshResult.getFindings().size());
    for (OntologyServiceResponse.Finding finding : meshResult.getFindings()) {
      System.out.println(" 4> " + finding.getConcept().getLabel() + " "
              + finding.getConcept().getUri() + " " + finding.getScore());
    }
    OntologyServiceResponse.Result uniprotResult = service.findUniprotEntitiesPaged(text, 0);
    System.out.println("UniProt: " + uniprotResult.getFindings().size());
    for (OntologyServiceResponse.Finding finding : uniprotResult.getFindings()) {
      System.out.println(" 5> " + finding.getConcept().getLabel() + " "
              + finding.getConcept().getUri() + " " + finding.getScore());
    }
    LinkedLifeDataServiceResponse.Result linkedLifeDataResult = service
            .findLinkedLifeDataEntitiesPaged(text, 0);
    System.out.println("LinkedLifeData: " + linkedLifeDataResult.getEntities().size());
    for (LinkedLifeDataServiceResponse.Entity entity : linkedLifeDataResult.getEntities()) {
      System.out.println(" 6> " + entity.getEntity());
      for (LinkedLifeDataServiceResponse.Relation relation : entity.getRelations()) {
        System.out.println("   - labels: " + relation.getLabels());
        System.out.println("   - pred: " + relation.getPred());
        System.out.println("   - sub: " + relation.getSubj());
        System.out.println("   - obj: " + relation.getObj());
      }
    }*/
    text = text.replace('?', ' ');
    System.out.println(text);

    PubMedSearchServiceResponse.Result pubmedResult = service.findPubMedCitations(text, 0);
    List<PubMedSearchServiceResponse.Document> list =  pubmedResult.getDocuments();
    
    for(PubMedSearchServiceResponse.Document d: list) {
      System.out.println(d.getDocumentAbstract());
      System.out.println(d.getPmid());
    }
    
    System.out.println(pubmedResult.getSize());
  }
}
