package org.alvin.service.impl;

import org.alvin.mapper.ProductMapper;
import org.alvin.pojo.Product;
import org.alvin.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "product")
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<Product> select() {
        return productMapper.select();
    }

    @CachePut(value = "product", key = "#product.id")
    @Override
    public int insert(Product product) {
        return productMapper.insert(product);
    }

    @CachePut(value = "product", key = "#product.id")
    @Override
    public int update(Product product) {
        return productMapper.update(product);
    }

    @CacheEvict(value = "product", key = "#id")
    @Override
    public int deleteById(int id) {
        return productMapper.deleteById(id);
    }

    @Cacheable(value = "product", key = "#id")
    @Override
    public Product getById(int id) {
        return productMapper.getById(id);
    }
}
