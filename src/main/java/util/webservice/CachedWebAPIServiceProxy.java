package util.webservice;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONObject;

import util.datastructure.BetterMap;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;

import edu.cmu.lti.oaqa.bio.bioasq.services.LinkedLifeDataServiceResponse;
import edu.cmu.lti.oaqa.bio.bioasq.services.LinkedLifeDataServiceResponse.Entity;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse.Finding;
import edu.cmu.lti.oaqa.bio.bioasq.services.PubMedSearchServiceResponse.Document;
import edu.cmu.lti.oaqa.type.kb.Triple;

/**
 * 
 * @author nwolfe and pyadapad
 * Class which caches results from PubMed API
 *
 */
public class CachedWebAPIServiceProxy extends WebAPIServiceProxy {

  public boolean CLEAR_CACHE = false;

  public boolean APPLY_YEAR_CHANGE_HACK = true;

  private BetterMap<String, Finding> cachedFindings;

  private BetterMap<String, Entity> cachedEntities;

  private BetterMap<String, Document> cachedDocuments;

  private BetterMap<String, JSONObject> cachedMetal;

  private String cachePath = "src/main/resources/cache/";

  private final String findings = "findings/";

  private final String entities = "entities/";

  private final String documents = "documents/";

  private final String snippets = "snippets/";

  public CachedWebAPIServiceProxy() {
    super();
    if (CLEAR_CACHE) {
      clearCache();
      System.exit(0);
    }
    try {
      this.cachedFindings = getCachedFindings();
      this.cachedEntities = getCachedEntities();
      this.cachedDocuments = getCachedDocuments();
      this.cachedMetal = getCachedMetal();
    } catch (Exception e) {
      System.out.println("Exception: " + e.getMessage());
    }
  }

  public void clearCache() {
    String[] subdirs = { findings, entities, documents, snippets};
    for (String subdir : subdirs) {
      try {
        Files.walk(Paths.get(cachePath + subdir)).forEach(filePath -> {
          try {
            if (!Files.isDirectory(filePath)) {
              Files.deleteIfExists(filePath);
            }
          } catch (Exception e) {
            // e.printStackTrace();
                System.out.println("Exception: " + e.getMessage());
              }
            });
      } catch (IOException e) {
        // e.printStackTrace();
        System.out.println("IOException: " + e.getMessage());
      }
    }
  }

  private class FileReadHelper<T> {
    public BetterMap<String, T> getStuff(String subdir) throws Exception {
      BetterMap<String, T> map = new BetterMap<String, T>();
      BetterMap<String, Object> jsonMap = getFilesInDirectory(subdir);
      for (String query : jsonMap.keySet()) {
        ArrayList<Object> jarr = jsonMap.get(query);
        if (jarr.isEmpty()) {
          map.addItem(query, null);
        } else {
          for (Object json : jarr) {
            @SuppressWarnings("unchecked")
            T j = (T) json;
            map.addItem(query, j);
          }
        }
      }
      return map;
    }
  }

  private BetterMap<String, JSONObject> getCachedMetal() throws Exception {
    FileReadHelper<JSONObject> frh = new FileReadHelper<JSONObject>();
    return frh.getStuff(snippets);
  }

  private BetterMap<String, Document> getCachedDocuments() throws Exception {
    FileReadHelper<Document> frh = new FileReadHelper<Document>();
    return frh.getStuff(documents);
  }

  private BetterMap<String, Entity> getCachedEntities() throws Exception {
    FileReadHelper<Entity> frh = new FileReadHelper<Entity>();
    return frh.getStuff(entities);
  }

  private BetterMap<String, Finding> getCachedFindings() throws Exception {
    FileReadHelper<Finding> frh = new FileReadHelper<Finding>();
    return frh.getStuff(findings);
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
            String line = scn.nextLine();
            line = new StupidHack().doHackyBullshit(line);
            Object o = JsonReader.jsonToJava(line);
            json.addItem(query, o);
          }
        } catch (Exception e) {
          System.out.println("Exception: " + e.getMessage());
        }
      }
    });
    return json;
  }
  
  private class StupidHack {
    public String doHackyBullshit(String line) {
      if (APPLY_YEAR_CHANGE_HACK) {
        String hack = "\"year\":\"2014\"";
        String replaceHack = "\"year\":\"2012\"";
        line = doReplaceBullshit(line, hack, replaceHack);
        hack = "mesh/2014";
        replaceHack = "mesh/2012";
        return doReplaceBullshit(line, hack, replaceHack);
      } else
      return line;
    }
    private String doReplaceBullshit(String line, String hack, String replacement) {
      if(line.contains(hack)) {
        return line.replaceAll(hack, replacement);
      } else {
        return line;
      }
    }
  }
  
  private PrintStream getWriter(String name, String subdir) {
    //String hash = cleanString(name, "_").toLowerCase();
    String hash = UUID.randomUUID().toString();
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
      ps.print("\n" + json);
    } catch (IOException e) {
      // e.printStackTrace();
    }
  }

  /**
   * Checks the cache for findings. If cached results do not
   * exist, then call the API.
   */
  @Override
  public List<Finding> getFindingsFromQuery(String query) {
    if (!cachedFindings.containsKey(query)) {
      PrintStream ps = getWriter(query, findings);
      ps.print(query);
      List<Finding> findings = super.getFindingsFromQuery(query);
      if (findings.isEmpty()) {
        cachedFindings.addItem(query, null);
        writeResultToFile(ps, null);
      } else {
        for (Finding f : findings) {
          cachedFindings.addItem(query, f);
          writeResultToFile(ps, f);
        }
      }
      ps.close();
      return findings;
    } else
      return cachedFindings.get(query);
  }

  /**
   * Check cache for previously fetched entities.
   * If not found, use the API in parent class 
   */
  @Override
  public List<Entity> getEntitiesFromQuery(String query) {
    if (!cachedEntities.containsKey(query)) {
      PrintStream ps = getWriter(query, entities);
      ps.print(query);
      List<Entity> entities = super.getEntitiesFromQuery(query);
      if (entities.isEmpty()) {
        cachedFindings.addItem(query, null);
        writeResultToFile(ps, null);
      } else {
        for (Entity e : entities) {
          cachedEntities.addItem(query, e);
          writeResultToFile(ps, e);
        }
      }
      ps.close();
      return entities;
    } else
      return cachedEntities.get(query);
  }

  /**
   * Fetches all the documents that are cached. If not cached,
   * the PubMed API is used to fetch documents.
   */
  @Override
  public List<Document> getPubMedDocumentsFromQuery(String query) {
    if (!cachedDocuments.containsKey(query)) {
      PrintStream ps = getWriter(query, documents);
      ps.print(query);
      List<Document> documents = super.getPubMedDocumentsFromQuery(query);
      if (documents.isEmpty()) {
        cachedFindings.addItem(query, null);
        writeResultToFile(ps, null);
      } else {
        for (Document d : documents) {
          cachedDocuments.addItem(query, d);
          writeResultToFile(ps, d);
        }
      }
      ps.close();
      return documents;
    } else
      return cachedDocuments.get(query);
  }

  /**
   * Method to get FullText in json. Check for
   * cached results before calling API.
   */
  @Override
  public JSONObject getDocFullTextJSon(String pmid) {
    if (!cachedMetal.containsKey(pmid)) {
      PrintStream ps = getWriter(pmid, snippets);
      ps.print(pmid);
      JSONObject json = super.getDocFullTextJSon(pmid);
      cachedMetal.addItem(pmid, json);
      writeResultToFile(ps, json);
      ps.close();
      return json;
    } else
      // only ever one result...
      return cachedMetal.get(pmid).get(0);
  }

}
