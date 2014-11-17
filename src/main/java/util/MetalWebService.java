package util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author saxman
 */
public class MetalWebService {
  public static final String LOG_TAG = "ExampleApp";

  public static final String METAL_API_BASE = "http://metal.lti.cs.cmu.edu:30002/pmc/";




  public static URL buildUrl(String searchType, String keyword, Double lat, Double lng, Integer radius, String reference) {
    StringBuilder sb = new StringBuilder(METAL_API_BASE);
    sb.append(searchType);
    
    System.out.printf("URL: %s\n", sb.toString());

    URL url;
    try {
      url = new URL(sb.toString());
    } catch (MalformedURLException e) {
      e.printStackTrace();
      return null;
    }
    return url;
  }


  public static JSONObject get_JSON_Obj_by_URL(URL url) {
    HttpURLConnection conn = null;
    StringBuilder jsonResults = new StringBuilder();
    try {
      conn = (HttpURLConnection) url.openConnection();
      InputStreamReader in = new InputStreamReader(conn.getInputStream());

      int read;
      char[] buff = new char[10240];
      while ((read = in.read(buff)) != -1) {
        jsonResults.append(buff, 0, read);
      }
    } catch (MalformedURLException e) {
      System.err.println("Error processing Places API URL");
      return null;
    } catch (IOException e) {
      System.err.println("Error connecting to Places API");
      System.err.println("Retry in 3 seconds.");
      try {
        Thread.sleep(3000);
      } catch (InterruptedException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      
      return get_JSON_Obj_by_URL(url);
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }

    System.out.println(jsonResults.toString());

    // Create a JSON object hierarchy from the results
    JSONObject jsonObj = null;
    try {
      jsonObj = new JSONObject(jsonResults.toString());
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return jsonObj;
  }




}