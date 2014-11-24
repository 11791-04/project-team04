package webservice;

public class WebAPIServiceProxyFactory {
  
  private static WebAPIServiceProxy proxy = null;
  public static boolean DISABLE_CACHE = false;
  
  public static WebAPIServiceProxy getInstance() {
    return getGoPubMedServiceProxy();
  }
  
  private static synchronized WebAPIServiceProxy getGoPubMedServiceProxy() {
    if(WebAPIServiceProxyFactory.proxy == null) 
        if(DISABLE_CACHE) 
          proxy = new WebAPIServiceProxy();
        else
          proxy = new CachedWebAPIServiceProxy();
    return WebAPIServiceProxyFactory.proxy;
  }

}
