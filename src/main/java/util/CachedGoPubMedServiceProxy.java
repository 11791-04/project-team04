package util;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.File;
import java.util.List;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;

import edu.cmu.lti.oaqa.bio.bioasq.services.LinkedLifeDataServiceResponse.Entity;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse.Finding;
import edu.cmu.lti.oaqa.bio.bioasq.services.PubMedSearchServiceResponse.Document;

public class CachedGoPubMedServiceProxy extends GoPubMedServiceProxy {

  private BetterMap<String, Finding> cachedFindings;
  private BetterMap<String, Entity> cachedEntities;
  private BetterMap<String, Document> cachedDocuments;
  
  public CachedGoPubMedServiceProxy() {
    super();
    this.cachedFindings = getCachedFindings();
    this.cachedEntities = getCachedEntities();
    this.cachedDocuments = getCachedDocuments();
  }
  
  private BetterMap<String, Document> getCachedDocuments() {
    return new BetterMap<String, Document>();
  }

  private BetterMap<String, Entity> getCachedEntities() {
    return new BetterMap<String, Entity>();
  }

  private BetterMap<String, Finding> getCachedFindings() {
    return new BetterMap<String, Finding>();
  }
  
  private PrintStream getWriter(String name) {
    String hash = name.replaceAll("\\s", "_");
    try {
      PrintStream ps = new PrintStream(hash);
      return ps;
    } catch (FileNotFoundException e) {
      System.out.println("FileNotFoundException: " + e.getMessage());
    }
    return null;
  }

  @Override
  public List<Finding> getFindingsFromQuery(String query) {
    if(!cachedFindings.containsKey(query)) {
      //PrintStream ps = getWriter(query);
      //ps.println(query);
      for(Finding f : super.getFindingsFromQuery(query)) {
        cachedFindings.addItem(query, f);
      }
    } 
    return cachedFindings.get(query);
  }

  @Override
  public List<Entity> getEntitiesFromQuery(String query) {
    if(!cachedEntities.containsKey(query)) {
      for(Entity f : super.getEntitiesFromQuery(query)) {
        cachedEntities.addItem(query, f);
      }
    } 
    return cachedEntities.get(query);
  }

  @Override
  public List<Document> getPubMedDocumentsFromQuery(String query) {
    if(!cachedDocuments.containsKey(query)) {
      for(Document f : super.getPubMedDocumentsFromQuery(query)) {
        cachedDocuments.addItem(query, f);
      }
    } 
    return cachedDocuments.get(query);
  }

}
