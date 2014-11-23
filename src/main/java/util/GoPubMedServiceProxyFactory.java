package util;

public class GoPubMedServiceProxyFactory {
  
  private static GoPubMedServiceProxy proxy = null;
  private static final boolean DISABLE_CACHE = false;
  
  public static GoPubMedServiceProxy getInstance() {
    return getGoPubMedServiceProxy();
  }
  
  private static synchronized GoPubMedServiceProxy getGoPubMedServiceProxy() {
    if(GoPubMedServiceProxyFactory.proxy == null) 
        if(DISABLE_CACHE) 
          proxy = new GoPubMedServiceProxy();
        else
          proxy = new CachedGoPubMedServiceProxy();
    return GoPubMedServiceProxyFactory.proxy;
  }

}
