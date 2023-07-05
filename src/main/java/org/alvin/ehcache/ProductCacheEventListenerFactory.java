package org.alvin.ehcache;

import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.CacheEventListenerFactory;
import org.alvin.ehcache.listener.ProductCacheEventListener;

import java.util.Properties;

public class ProductCacheEventListenerFactory extends CacheEventListenerFactory {

    @Override
    public CacheEventListener createCacheEventListener(Properties properties) {
        return ProductCacheEventListener.INSTANCE;
    }
}
