package webservice;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.uima.UIMA_IllegalStateException;

import edu.cmu.lti.oaqa.bio.bioasq.services.GoPubMedService;

public class GoPubMedServiceFactory {
  
  private static GoPubMedService service = null;
  
  public static GoPubMedService getInstance() {
    return getGoPubMedService();
  }
  
  private static synchronized GoPubMedService getGoPubMedService() {
    if(GoPubMedServiceFactory.service == null) {
      try {
        service = new GoPubMedService("project.properties");
      } catch (ConfigurationException e) {
        e.printStackTrace();
        throw new UIMA_IllegalStateException();
      }
    }
    return GoPubMedServiceFactory.service;
  }
}