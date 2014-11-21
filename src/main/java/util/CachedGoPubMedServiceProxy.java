package util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.lti.oaqa.bio.bioasq.services.LinkedLifeDataServiceResponse.Entity;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse.Finding;
import edu.cmu.lti.oaqa.bio.bioasq.services.PubMedSearchServiceResponse.Document;

public class CachedGoPubMedServiceProxy extends GoPubMedServiceProxy {

  private BetterMap<String, Finding> cachedFindings;
  private BetterMap<String, Entity> cachedEntities;
  private BetterMap<String, Document> cachedDocuments;
  private final String cachePath = "src/main/resources/cache/";
  private final Pattern punc = Pattern.compile("\\p{Punct}+");
  private final Pattern wsp = Pattern.compile("\\p{Space}+");
  
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
  
  private String cleanName(String s) {
    Matcher m = punc.matcher(s);
    s = m.replaceAll("");
    m = wsp.matcher(s);
    s = m.replaceAll("_");
    return s;
  }
  
  private ObjectOutputStream getWriter(String name) {
    String hash = cleanName(name);
    try {
      FileOutputStream fs = new FileOutputStream(new File(cachePath + hash + ".ser"));
      ObjectOutputStream oos = new ObjectOutputStream(fs);
      return oos;
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
    return null;
  }
  
  @Override
  public List<Finding> getFindingsFromQuery(String query) {
    if(!cachedFindings.containsKey(query)) {
      ObjectOutputStream os = getWriter(query);
      List<Finding> findings = super.getFindingsFromQuery(query);
      try {
        os.writeBytes(query);
      } catch (IOException e) {
        System.out.println("IOException: " + e.getMessage());
      }
      for(Finding f : findings) {
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
