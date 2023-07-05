package org.alvin.ehcache.listener;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

public class ProductCacheEventListener implements CacheEventListener {

    public static ProductCacheEventListener INSTANCE = new ProductCacheEventListener();


    @Override
    public void notifyElementRemoved(Ehcache ehcache, Element element) throws CacheException {
        System.out.println("notifyElementRemoved");
    }

    @Override
    public void notifyElementPut(Ehcache ehcache, Element element) throws CacheException {
        System.out.println("notifyElementPut");
    }

    @Override
    public void notifyElementUpdated(Ehcache ehcache, Element element) throws CacheException {
        System.out.println("notifyElementUpdated");
    }

    @Override
    public void notifyElementExpired(Ehcache ehcache, Element element) {

    }

    @Override
    public void notifyElementEvicted(Ehcache ehcache, Element element) {
        System.out.println("notifyElementEvicted");
    }

    @Override
    public void notifyRemoveAll(Ehcache ehcache) {

    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return null;
    }

    @Override
    public void dispose() {
        System.out.println("dispose");
    }
}
