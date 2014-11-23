package util;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;

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

  private final String findings = "findings/";

  private final String entities = "entities/";

  private final String documents = "documents/";

  public CachedGoPubMedServiceProxy() {
    super();
    try {
      this.cachedFindings = getCachedFindings();
      this.cachedEntities = getCachedEntities();
      this.cachedDocuments = getCachedDocuments();
    } catch (Exception e) {
      System.out.println("Exception: " + e.getMessage());
    }
  }

  private BetterMap<String, Document> getCachedDocuments() throws Exception {
    BetterMap<String, Document> map = new BetterMap<String, Document>();
    BetterMap<String, Object> jsonMap = getFilesInDirectory(documents);
    for (String query : jsonMap.keySet()) {
      ArrayList<Object> jarr = jsonMap.get(query);
      for (Object json : jarr) {
        Document d = (Document) json;
        map.addItem(query, d);
      }
    }
    return map;
  }

  private BetterMap<String, Entity> getCachedEntities() throws Exception {
    BetterMap<String, Entity> map = new BetterMap<String, Entity>();
    BetterMap<String, Object> jsonMap = getFilesInDirectory(entities);
    for (String query : jsonMap.keySet()) {
      ArrayList<Object> jarr = jsonMap.get(query);
      for (Object json : jarr) {
        Entity e = (Entity) json;
        map.addItem(query, e);
      }
    }
    return map;
  }

  private BetterMap<String, Finding> getCachedFindings() throws Exception {
    BetterMap<String, Finding> map = new BetterMap<String, Finding>();
    BetterMap<String, Object> jsonMap = getFilesInDirectory(findings);
    for (String query : jsonMap.keySet()) {
      ArrayList<Object> jarr = jsonMap.get(query);
      for (Object json : jarr) {
        Finding f = (Finding) json;
        map.addItem(query, f);
      }
    }
    return map;
  }

  private BetterMap<String, Object> getFilesInDirectory(String subdir) throws Exception {
    BetterMap<String, Object> json = new BetterMap<String, Object>();
    Files.walk(Paths.get(cachePath + subdir)).forEach(filePath -> {
      if (Files.isRegularFile(filePath)) {
        Scanner scn;
        try {
          scn = new Scanner(filePath.toFile());
          String query = scn.nextLine();
          while (scn.hasNextLine()) {
            Object o = JsonReader.jsonToJava(scn.nextLine());
            json.addItem(query, o);
          }
        } catch (Exception e) {
          System.out.println("Exception: " + e.getMessage());
        }
      }
    });
    return json;
  }

  private String cleanName(String s) {
    Matcher m = punc.matcher(s);
    s = m.replaceAll("");
    m = wsp.matcher(s);
    s = m.replaceAll("_");
    return s;
  }

  private PrintStream getWriter(String name, String subdir) {
    String hash = cleanName(name).toLowerCase();
    try {
      PrintStream ps = new PrintStream(cachePath + subdir + hash + ".txt");
      return ps;
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
    return null;
  }

  private void writeResultToFile(PrintStream ps, Object o) {
    try {
      String json = JsonWriter.objectToJson(o);
      ps.println(json);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public List<Finding> getFindingsFromQuery(String query) {
    if (!cachedFindings.containsKey(query)) {
      PrintStream ps = getWriter(query, findings);
      ps.println(query);
      List<Finding> findings = super.getFindingsFromQuery(query);
      for (Finding f : findings) {
        cachedFindings.addItem(query, f);
        writeResultToFile(ps, f);
      }
      ps.close();
    }
    return cachedFindings.get(query);
  }

  @Override
  public List<Entity> getEntitiesFromQuery(String query) {
    if (!cachedEntities.containsKey(query)) {
      PrintStream ps = getWriter(query, entities);
      ps.println(query);
      List<Entity> entities = super.getEntitiesFromQuery(query);
      for (Entity e : entities) {
        cachedEntities.addItem(query, e);
        writeResultToFile(ps, e);
      }
      ps.close();
    }
    return cachedEntities.get(query);
  }

  @Override
  public List<Document> getPubMedDocumentsFromQuery(String query) {
    if (!cachedDocuments.containsKey(query)) {
      PrintStream ps = getWriter(query, documents);
      ps.println(query);
      List<Document> documents = super.getPubMedDocumentsFromQuery(query);
      for (Document d : documents) {
        cachedDocuments.addItem(query, d);
        writeResultToFile(ps, d);
      }
      ps.close();
    }
    return cachedDocuments.get(query);
  }

}
