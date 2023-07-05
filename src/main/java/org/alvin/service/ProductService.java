package org.alvin.service;

import org.alvin.pojo.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductService {
//    @Cacheable
    List<Product> select();
//    @Cacheable
    int insert(Product product);
//    @Cacheable
    int update(Product product);
//    @Cacheable
    int deleteById(int id);
    Product getById(int id);
}
