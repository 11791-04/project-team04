package util;

public class GoPubMedServiceProxyFactory {
  
  private static GoPubMedServiceProxy proxy = null;
  
  public static GoPubMedServiceProxy getInstance() {
    return getGoPubMedServiceProxy();
  }
  
  private static synchronized GoPubMedServiceProxy getGoPubMedServiceProxy() {
    if(GoPubMedServiceProxyFactory.proxy == null) 
        proxy = new CachedGoPubMedServiceProxy();
    return GoPubMedServiceProxyFactory.proxy;
  }

}
