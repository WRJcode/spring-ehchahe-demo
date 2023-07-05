package org.alvin.config;

import net.sf.ehcache.CacheManager;
import org.springframework.context.annotation.Bean;


//@Configuration
public class EhcacheConfig {
    @Bean
    public CacheManager cacheManager() {
        CacheManager cacheManager = new CacheManager();
        cacheManager.addCache("product");
        return cacheManager;
    }
}
