package org.alvin.api;

import org.alvin.pojo.Product;
import org.alvin.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductApi {
    @Autowired
    ProductService productService;

    @GetMapping("/list")
    public List<Product> list(){
        return productService.select();
    }

    @GetMapping("/add")
    public String add(){
        Product product = new Product();
        product.setName("product 1");
        product.setPrice(100);
        product.setNum(100);
        productService.insert(product);
        return "ok";
    }

    @GetMapping("/update")
    public String update(){
        Product product = new Product();
        product.setId(1);
        product.setName("product update");
        product.setPrice(100);
        product.setNum(100);
        productService.update(product);
        return "ok";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id){
        productService.deleteById(id);
        return "ok";
    }

    @GetMapping("/get/{id}")
    public Product get(@PathVariable("id") int id){
        return productService.getById(id);
    }


}
