package org.alvin.mapper;

import org.alvin.pojo.Product;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductMapper {
    List<Product> select();
    int insert(Product product);
    int update(Product product);
    int deleteById(int id);
    Product getById(int id);
}
