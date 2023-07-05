### 这是一个SpringBoot集成Ehcache的Demo

#### 1. pom.xml中引入依赖
```xml
<!-- ehcache -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
<!-- ehcache -->
<dependency>
    <groupId>net.sf.ehcache</groupId>
    <artifactId>ehcache</artifactId>
</dependency>
```

#### 2. application.yml中配置ehcache
```yaml
spring:
  cache:
    ehcache:
      config: classpath:ehcache.xml
```

#### 3. Ehcache常用配置

| 属性 | 说明 |
| --- | --- |
| maxElementsInMemory | 内存中最大缓存对象数 |
| eternal | 缓存是否永久有效，一但设置了，timeout将不起作用 |
| timeToIdleSeconds | 缓存数据的钝化时间 |
| timeToLiveSeconds | 缓存数据的生存时间 |
| overflowToDisk | 内存不足时，是否启用磁盘缓存 |
| diskPersistent | 是否缓存虚拟机重启期间数据 |
| diskExpiryThreadIntervalSeconds | 磁盘失效线程运行时间间隔 |


#### 4. 编写缓存监听器
```java
public class ProductCacheEventListenerFactory extends CacheEventListenerFactory {
    @Override
    public CacheEventListener createCacheEventListener(Properties properties) {
        return new ProductCacheEventListener();
    }
}
```

```java
public class ProductCacheEventListener implements CacheEventListener {
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
        System.out.println("notifyElementExpired");
    }

    @Override
    public void notifyElementEvicted(Ehcache ehcache, Element element) {
        System.out.println("notifyElementEvicted");
    }

    @Override
    public void notifyRemoveAll(Ehcache ehcache) {
        System.out.println("notifyRemoveAll");
    }

    @Override
    public void dispose() {
        System.out.println("dispose");
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
```

#### 5. 编写缓存配置类
```java
@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        return new EhCacheCacheManager(ehCacheCacheManager().getObject());
    }

    @Bean
    public EhCacheManagerFactoryBean ehCacheCacheManager() {
        EhCacheManagerFactoryBean cacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        cacheManagerFactoryBean.setConfigLocation(new ClassPathResource("ehcache.xml"));
        cacheManagerFactoryBean.setShared(true);
        return cacheManagerFactoryBean;
    }
}
```

#### 6. 编写缓存接口
```java
public interface ProductCache {
    Product get(Long id);

    void put(Product product);

    void remove(Long id);
}
```

#### 7. 编写缓存实现类
```java
@Component
public class ProductCacheImpl implements ProductCache {
    @Resource
    private CacheManager cacheManager;

    @Override
    public Product get(Long id) {
        Cache cache = cacheManager.getCache("product");
        Element element = cache.get(id);
        return element == null ? null : (Product) element.getObjectValue();
    }

    @Override
    public void put(Product product) {
        Cache cache = cacheManager.getCache("product");
        cache.put(new Element(product.getId(), product));
    }

    @Override
    public void remove(Long id) {
        Cache cache = cacheManager.getCache("product");
        cache.remove(id);
    }
}
```

#### 8. 编写测试类
```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductCacheTest {
    @Resource
    private ProductCache productCache;

    @Test
    public void test() {
        Product product = new Product();
        product.setId(1L);
        product.setName("测试商品");
        productCache.put(product);
        Product product1 = productCache.get(1L);
        System.out.println(product1);
        productCache.remove(1L);
        Product product2 = productCache.get(1L);
        System.out.println(product2);
    }
}
```

#### 9. 编写测试结果
```text
notifyElementPut
Product(id=1, name=测试商品)
notifyElementRemoved
null
```

#### 10. Ehcache配置详解

```xml
<ehcache>
    <!--磁盘存储位置-->
    <diskStore path="java.io.tmpdir/product"/>
    <!--默认缓存配置-->
    <defaultCache
            maxElementsInMemory="10000" <!--内存中最大缓存对象数-->
            eternal="false" <!--缓存是否永久有效，一但设置了，timeout将不起作用-->
            timeToIdleSeconds="120" <!--缓存数据的钝化时间-->
            timeToLiveSeconds="120" <!--缓存数据的生存时间-->
            overflowToDisk="false" <!--内存不足时，是否启用磁盘缓存-->
            diskPersistent="false" <!--是否缓存虚拟机重启期间数据-->
            diskExpiryThreadIntervalSeconds="120" <!--磁盘失效线程运行时间间隔-->
    />
    <!--product 缓存-->
    <cache
            name="product"
            maxEntriesLocalHeap="200" <!--缓存最大对象数-->
            timeToLiveSeconds="600"> <!--缓存数据的生存时间-->
        <cacheEventListenerFactory   class="org.alvin.ehcache.ProductCacheEventListenerFactory"/>
    </cache>
</ehcache>
```

#### 11. Ehcache缓存策略

| 策略 | 说明 |
| --- | --- |
| FIFO | 先进先出 |
| LFU | 最少使用 |
| LRU | 最近最少使用 |
| CLOCK | 时钟算法 |




