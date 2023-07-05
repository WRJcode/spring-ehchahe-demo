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

#### 12. Ehcache是什么？

Ehcache是一个纯Java的进程内缓存框架，具有快速、精干等特点，是Hibernate中默认的CacheProvider，采用Apache许可证2.0发布。

#### 13. Ehcache的特点

- 快速：Ehcache是一个轻量级的缓存，它的设计目标是为了提高应用程序的性能和减少数据库负载，它的运行速度非常快，因为它是一个内存缓存，数据保存在JVM的内存中，所以它的读写速度非常快。
- 精干：Ehcache是一个精干的缓存，它的核心包只有几百K，而且它的API非常简单，学习起来非常容易。
- 高效：Ehcache是一个高效的缓存，它的运行效率非常高，它的运行依赖于slf4j和jgroups这两个包，这两个包都是非常高效的包，所以Ehcache也是一个高效的缓存。
- 线程安全：Ehcache是一个线程安全的缓存，它的线程安全是指它的多个实例可以在同一个虚拟机中运行，而且不会相互影响，它的线程安全是通过synchronized关键字来实现的。
- 分布式缓存：Ehcache是一个分布式缓存，它可以把缓存数据分布在多个服务器上，这样就可以避免单点故障，提高系统的可用性。
- 可扩展性：Ehcache是一个可扩展的缓存，它可以通过CacheManager来管理多个Cache，每个Cache都可以设置不同的缓存策略，这样就可以根据不同的需求来设置不同的缓存策略，提高系统的灵活性。
- 缓存策略：Ehcache提供了多种缓存策略，包括FIFO、LFU、LRU、CLOCK等，可以根据不同的需求来设置不同的缓存策略，提高系统的灵活性。
- 持久化：Ehcache提供了持久化功能，可以把缓存数据保存到磁盘中，这样即使系统重启，缓存数据也不会丢失，提高系统的可靠性。
- 监控：Ehcache提供了多种监控功能，可以通过JMX、RMI、CacheManagerEventListener等方式来监控缓存的状态，提高系统的可靠性。
- 缓存事件：Ehcache提供了多种缓存事件，可以通过CacheEventListener、CacheManagerEventListener等方式来监听缓存事件，提高系统的灵活性。
- 缓存加载：Ehcache提供了缓存加载功能，可以通过CacheLoader来加载缓存数据，提高系统的灵活性。
- 缓存扩展：Ehcache提供了缓存扩展功能，可以通过CacheExtension来扩展缓存，提高系统的灵活性。
- 缓存过滤：Ehcache提供了缓存过滤功能，可以通过CacheFilter来过滤缓存数据，提高系统的灵活性。
- 缓存搜索：Ehcache提供了缓存搜索功能，可以通过CacheSearch来搜索缓存数据，提高系统的灵活性。
- 缓存分区：Ehcache提供了缓存分区功能，可以通过CacheDecoratorFactory来分区缓存，提高系统的灵活性。
- 缓存复制：Ehcache提供了缓存复制功能，可以通过CacheReplicator来复制缓存，提高系统的灵活性。
- 缓存压缩：Ehcache提供了缓存压缩功能，可以通过CacheWriter来压缩缓存，提高系统的灵活性。
- 缓存事务：Ehcache提供了缓存事务功能，可以通过CacheTransactionManager来管理缓存事务，提高系统的灵活性。
- 缓存序列化：Ehcache提供了缓存序列化功能，可以通过CacheSerializer来序列化缓存数据，提高系统的灵活性。
- 缓存分析：Ehcache提供了缓存分析功能，可以通过CacheAnalyzer来分析缓存数据，提高系统的灵活性。
- 缓存监控：Ehcache提供了缓存监控功能，可以通过CacheMonitor来监控缓存数据，提高系统的灵活性。
- 缓存统计：Ehcache提供了缓存统计功能，可以通过CacheStatistics来统计缓存数据，提高系统的灵活性。
- 缓存管理：Ehcache提供了缓存管理功能，可以通过CacheManager来管理缓存，提高系统的灵活性。
- 缓存配置：Ehcache提供了缓存配置功能，可以通过CacheConfiguration来配置缓存，提高系统的灵活性。
- 缓存监控：Ehcache提供了缓存监控功能，可以通过CacheManager来监控缓存，提高系统的灵活性。

#### 14. Ehcache的架构

Ehcache的架构如图所示：

![Ehcache的架构](
https://raw.githubusercontent.com/leelance/spring-boot-all/master/doc/images/ehcache-architecture.png)

Ehcache的架构主要由以下几个部分组成：

- Cache：缓存，用于存储缓存数据。
- CacheManager：缓存管理器，用于管理缓存。
- CacheConfiguration：缓存配置，用于配置缓存。
- CacheEventListener：缓存事件监听器，用于监听缓存事件。
- CacheManagerEventListener：缓存管理器事件监听器，用于监听缓存管理器事件。
- CacheLoader：缓存加载器，用于加载缓存数据。
- CacheExtension：缓存扩展器，用于扩展缓存。
- CacheFilter：缓存过滤器，用于过滤缓存数据。
- CacheSearch：缓存搜索器，用于搜索缓存数据。
- CacheDecoratorFactory：缓存装饰器工厂，用于装饰缓存。
- CacheReplicator：缓存复制器，用于复制缓存。
- CacheWriter：缓存写入器，用于写入缓存。
- CacheTransactionManager：缓存事务管理器，用于管理缓存事务。
- CacheSerializer：缓存序列化器，用于序列化缓存数据。
- CacheAnalyzer：缓存分析器，用于分析缓存数据。
- CacheMonitor：缓存监控器，用于监控缓存数据。
- CacheStatistics：缓存统计器，用于统计缓存数据。
- CacheManager：缓存管理器，用于管理缓存。
- CacheConfiguration：缓存配置，用于配置缓存。
- CacheManagerEventListener：缓存管理器事件监听器，用于监听缓存管理器事件。
- CacheManagerPeerProvider：缓存管理器对等提供器，用于提供缓存管理器对等。
- CacheManagerPeerListener：缓存管理器对等监听器，用于监听缓存管理器对等。
- CacheManagerPeerProviderFactory：缓存管理器对等提供器工厂，用于创建缓存管理器对等提供器。
- CacheManagerPeerListenerFactory：缓存管理器对等监听器工厂，用于创建缓存管理器对等监听器。
- CacheManagerEventListenerFactory：缓存管理器事件监听器工厂，用于创建缓存管理器事件监听器。
- CacheLoaderFactory：缓存加载器工厂，用于创建缓存加载器。
- CacheExtensionFactory：缓存扩展器工厂，用于创建缓存扩展器。
- CacheFilterFactory：缓存过滤器工厂，用于创建缓存过滤器。
- CacheSearchFactory：缓存搜索器工厂，用于创建缓存搜索器。
- CacheDecoratorFactory：缓存装饰器工厂，用于创建缓存装饰器。
- CacheReplicatorFactory：缓存复制器工厂，用于创建缓存复制器。
- CacheWriterFactory：缓存写入器工厂，用于创建缓存写入器。
- CacheTransactionManagerLookup：缓存事务管理器查找器，用于查找缓存事务管理器。
- CacheSerializerFactory：缓存序列化器工厂，用于创建缓存序列化器。








